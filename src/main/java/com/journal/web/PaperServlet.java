package com.journal.web;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.journal.dao.ManuscriptDAO;
import com.journal.exception.BusinessException;
import com.journal.model.Manuscript;
import com.journal.model.Reference;
import com.journal.model.ReviewRecord;
import com.journal.model.SysUser;
import com.journal.service.ManuscriptService;
import com.journal.service.ReviewService;
import com.journal.util.JsonUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 论文稿件 Servlet
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.web
 *
 * 功能详述：
 *   对接 /api/papers/* 路径，提供稿件的增删改查 RESTful 接口。
 *   支持论文列表分页查询（含关键词、期刊名、状态、作者ID筛选）、
 *   论文详情查询、新论文投稿（含参考文献）、论文信息更新、
 *   论文状态变更等操作。请求体和响应体均使用 JSON 格式。
 *
 * 依赖：
 *   com.journal.service.ManuscriptService
 *   com.journal.util.JsonUtil
 *   com.google.gson.Gson
 */
public class PaperServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    /** 稿件业务服务 */
    private final ManuscriptService manuscriptService = new ManuscriptService();

    /** 稿件数据访问对象（用于数据权限过滤查询） */
    private final ManuscriptDAO manuscriptDAO = new ManuscriptDAO();

    /** 审稿业务服务（用于查询稿件关联的审稿记录） */
    private final ReviewService reviewService = new ReviewService();

    /**
     * 处理 GET 请求。
     * GET /api/papers                → 论文列表分页查询（支持 keyword, journalName, status, authorId, page, pageSize 参数）
     * GET /api/papers/{id}           → 获取论文详情
     * GET /api/papers/{id}/reviews   → 获取论文关联的审稿记录
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param req  HttpServletRequest 对象
     * @param resp HttpServletResponse 对象
     * @throws IOException 如果 I/O 操作发生异常
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String pathId = getPathId(req);
            String subResource = getSubResource(req);
            if (pathId == null) {
                handleList(req, resp);
            } else if ("reviews".equals(subResource)) {
                handlePaperReviews(pathId, resp);
            } else {
                handleDetail(pathId, resp);
            }
        } catch (BusinessException e) {
            writeJson(resp, JsonUtil.writeError(400, e.getMessage()));
        } catch (Exception e) {
            writeJson(resp, JsonUtil.writeError(500, "服务器内部错误：" + e.getMessage()));
        }
    }

    /**
     * 处理获取论文关联的审稿记录。
     * GET /api/papers/{id}/reviews → 返回该论文的所有审稿记录列表。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param pathId 路径中的论文 ID 字符串
     * @param resp   HttpServletResponse 对象
     * @throws IOException 如果 I/O 操作发生异常
     */
    private void handlePaperReviews(String pathId, HttpServletResponse resp) throws IOException {
        int paperId = Integer.parseInt(pathId);
        List<ReviewRecord> reviews = reviewService.getByPaperId(paperId);
        writeJson(resp, JsonUtil.writeSuccess(reviews));
    }

    /**
     * 处理论文列表分页查询。
     * 调用 ManuscriptService.search() 获取全部匹配结果后手动分页。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param req  HttpServletRequest 对象
     * @param resp HttpServletResponse 对象
     * @throws IOException 如果 I/O 操作发生异常
     */
    private void handleList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 从 Session 获取当前登录用户，进行数据权限过滤
        HttpSession session = req.getSession(false);
        SysUser currentUser = null;
        if (session != null) {
            currentUser = (SysUser) session.getAttribute("currentUser");
        }

        int page = getIntParam(req, "page", 1);
        int pageSize = getIntParam(req, "pageSize", 10);

        List<Manuscript> allResults;

        if (currentUser != null && "reviewer".equals(currentUser.getRoleCode())) {
            // 审稿人只看到指派给自己的稿件
            allResults = manuscriptDAO.findByReviewerId(currentUser.getRefId());
        } else if (currentUser != null && "author".equals(currentUser.getRoleCode())) {
            // 作者只看到自己的稿件
            allResults = manuscriptDAO.findByAuthorId(currentUser.getRefId());
        } else {
            // 管理员走原有搜索逻辑
            String keyword = getParam(req, "keyword");
            String journalName = getParam(req, "journalName");
            String status = getParam(req, "status");
            String authorId = getParam(req, "authorId");
            allResults = manuscriptService.search(keyword, journalName, status, authorId);
        }

        if (allResults == null) {
            allResults = new ArrayList<>();
        }

        int total = allResults.size();
        int fromIndex = Math.min((page - 1) * pageSize, total);
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<Manuscript> pageItems = allResults.subList(fromIndex, toIndex);

        Map<String, Object> pageData = new LinkedHashMap<>();
        pageData.put("items", pageItems);
        pageData.put("total", total);
        pageData.put("page", page);
        pageData.put("pageSize", pageSize);

        writeJson(resp, JsonUtil.writeSuccess(pageData));
    }

    /**
     * 处理论文详情查询。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param pathId 路径中的论文 ID 字符串
     * @param resp   HttpServletResponse 对象
     * @throws IOException 如果 I/O 操作发生异常
     */
    private void handleDetail(String pathId, HttpServletResponse resp) throws IOException {
        int id = Integer.parseInt(pathId);
        Manuscript manuscript = manuscriptService.getById(id);
        if (manuscript == null) {
            writeJson(resp, JsonUtil.writeError(404, "论文不存在"));
        } else {
            writeJson(resp, JsonUtil.writeSuccess(manuscript));
        }
    }

    /**
     * 处理 POST 请求，投稿新论文。
     * 读取请求体 JSON，解析 Manuscript 和 References 数据，调用 ManuscriptService.submit()。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param req  HttpServletRequest 对象
     * @param resp HttpServletResponse 对象
     * @throws IOException 如果 I/O 操作发生异常
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String body = readBody(req);
            JsonObject json = GSON.fromJson(body, JsonObject.class);
            if (json == null) {
                writeJson(resp, JsonUtil.writeError(400, "请求体不能为空"));
                return;
            }

            Manuscript manuscript = new Manuscript();
            manuscript.setTitle(getJsonString(json, "title"));
            manuscript.setAbstractText(getJsonString(json, "abstractText"));
            manuscript.setContent(getJsonString(json, "content"));
            manuscript.setJournalName(getJsonString(json, "journalName"));
            manuscript.setAuthorId(getJsonInt(json, "authorId", 0));

            // 设置默认状态为投稿
            manuscript.setStatus("submitted");

            // 解析 keywords 数组
            if (json.has("keywords") && json.get("keywords").isJsonArray()) {
                JsonArray kwArray = json.getAsJsonArray("keywords");
                List<String> keywords = GSON.fromJson(kwArray, new TypeToken<List<String>>(){}.getType());
                manuscript.setKeywords(keywords);
            }

            // 解析 references 数组
            List<Reference> references = parseReferences(json);

            int newId = manuscriptService.submit(manuscript, references);
            manuscript.setId(newId);

            writeJson(resp, JsonUtil.writeSuccess(manuscript));
        } catch (BusinessException e) {
            writeJson(resp, JsonUtil.writeError(400, e.getMessage()));
        } catch (Exception e) {
            writeJson(resp, JsonUtil.writeError(500, "服务器内部错误：" + e.getMessage()));
        }
    }

    /**
     * 处理 PUT 请求。
     * PUT /api/papers/{id}         → 更新论文信息
     * PUT /api/papers/{id}/status  → 更新论文状态
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param req  HttpServletRequest 对象
     * @param resp HttpServletResponse 对象
     * @throws IOException 如果 I/O 操作发生异常
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String subResource = getSubResource(req);
            if ("status".equals(subResource)) {
                handleUpdateStatus(req, resp);
            } else if ("accept".equals(subResource)) {
                handleAccept(req, resp);
            } else {
                handleUpdate(req, resp);
            }
        } catch (BusinessException e) {
            writeJson(resp, JsonUtil.writeError(400, e.getMessage()));
        } catch (Exception e) {
            writeJson(resp, JsonUtil.writeError(500, "服务器内部错误：" + e.getMessage()));
        }
    }

    /**
     * 处理论文收录操作。
     * PUT /api/papers/{id}/accept → 将论文状态变更为 accepted。
     * 前端调用时不携带请求体。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param req  HttpServletRequest 对象
     * @param resp HttpServletResponse 对象
     * @throws IOException 如果 I/O 操作发生异常
     */
    private void handleAccept(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathId = getPathId(req);
        if (pathId == null) {
            writeJson(resp, JsonUtil.writeError(400, "缺少论文 ID"));
            return;
        }
        int id = Integer.parseInt(pathId);
        boolean success = manuscriptService.updateStatus(id, "accepted");
        if (success) {
            Manuscript updated = manuscriptService.getById(id);
            writeJson(resp, JsonUtil.writeSuccess(updated));
        } else {
            writeJson(resp, JsonUtil.writeError(500, "收录操作失败"));
        }
    }

    /**
     * 处理更新论文信息。
     * 读取请求体 JSON，解析 Manuscript 和 References 数据，调用 ManuscriptService.update()。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param req  HttpServletRequest 对象
     * @param resp HttpServletResponse 对象
     * @throws IOException 如果 I/O 操作发生异常
     */
    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathId = getPathId(req);
        if (pathId == null) {
            writeJson(resp, JsonUtil.writeError(400, "缺少论文 ID"));
            return;
        }

        int id = Integer.parseInt(pathId);
        String body = readBody(req);
        JsonObject json = GSON.fromJson(body, JsonObject.class);
        if (json == null) {
            writeJson(resp, JsonUtil.writeError(400, "请求体不能为空"));
            return;
        }

        Manuscript manuscript = new Manuscript();
        manuscript.setId(id);
        manuscript.setTitle(getJsonString(json, "title"));
        manuscript.setAbstractText(getJsonString(json, "abstractText"));
        manuscript.setContent(getJsonString(json, "content"));
        manuscript.setJournalName(getJsonString(json, "journalName"));
        manuscript.setAuthorId(getJsonInt(json, "authorId", 0));

        // 解析 keywords 数组
        if (json.has("keywords") && json.get("keywords").isJsonArray()) {
            JsonArray kwArray = json.getAsJsonArray("keywords");
            List<String> keywords = GSON.fromJson(kwArray, new TypeToken<List<String>>(){}.getType());
            manuscript.setKeywords(keywords);
        }

        // 解析 references 数组
        List<Reference> references = parseReferences(json);

        boolean success = manuscriptService.update(manuscript, references);
        if (success) {
            Manuscript updated = manuscriptService.getById(id);
            writeJson(resp, JsonUtil.writeSuccess(updated));
        } else {
            writeJson(resp, JsonUtil.writeError(500, "更新论文失败"));
        }
    }

    /**
     * 处理更新论文状态。
     * 读取请求体 JSON，提取 status 字段，调用 ManuscriptService.updateStatus()。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param req  HttpServletRequest 对象
     * @param resp HttpServletResponse 对象
     * @throws IOException 如果 I/O 操作发生异常
     */
    private void handleUpdateStatus(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathId = getPathId(req);
        if (pathId == null) {
            writeJson(resp, JsonUtil.writeError(400, "缺少论文 ID"));
            return;
        }

        int id = Integer.parseInt(pathId);
        String body = readBody(req);
        JsonObject json = GSON.fromJson(body, JsonObject.class);
        if (json == null) {
            writeJson(resp, JsonUtil.writeError(400, "请求体不能为空"));
            return;
        }

        String status = getJsonString(json, "status");
        if (status == null || status.isEmpty()) {
            writeJson(resp, JsonUtil.writeError(400, "缺少 status 字段"));
            return;
        }

        boolean success = manuscriptService.updateStatus(id, status);
        if (success) {
            Manuscript updated = manuscriptService.getById(id);
            writeJson(resp, JsonUtil.writeSuccess(updated));
        } else {
            writeJson(resp, JsonUtil.writeError(500, "更新论文状态失败"));
        }
    }

    /**
     * 从 JSON 对象中解析参考文献列表。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param json 请求体 JsonObject
     * @return 参考文献列表，无 references 字段时返回空列表
     */
    private List<Reference> parseReferences(JsonObject json) {
        List<Reference> references = new ArrayList<>();
        if (json.has("references") && json.get("references").isJsonArray()) {
            JsonArray refArray = json.getAsJsonArray("references");
            for (JsonElement elem : refArray) {
                JsonObject refObj = elem.getAsJsonObject();
                Reference ref = new Reference();
                ref.setRefKey(getJsonString(refObj, "refKey"));
                ref.setTitle(getJsonString(refObj, "title"));
                ref.setAuthors(getJsonString(refObj, "authors"));
                ref.setJournal(getJsonString(refObj, "journal"));
                ref.setYear(getJsonInt(refObj, "year", 0));
                ref.setVolume(getJsonString(refObj, "volume"));
                ref.setIssue(getJsonString(refObj, "issue"));
                ref.setPages(getJsonString(refObj, "pages"));
                ref.setDoi(getJsonString(refObj, "doi"));
                references.add(ref);
            }
        }
        return references;
    }
}
