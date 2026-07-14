package com.journal.web;

import com.google.gson.JsonObject;
import com.journal.exception.BusinessException;
import com.journal.impl.ManuscriptImpl;
import com.journal.model.Manuscript;
import com.journal.service.ManuscriptService;
import com.journal.util.JsonUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 审稿指派 Servlet
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.web
 *
 * 功能详述：
 *   对接 /api/assignments/* 路径，提供审稿人指派记录的查询和创建接口。
 *   GET  /api/assignments → 获取所有已指派审稿人的稿件列表，以指派记录格式返回；
 *   POST /api/assignments  → 为指定稿件指派审稿人。
 *   指派记录使用 Map<String, Object> 动态构建，不依赖单独的模型类。
 *
 * 依赖：
 *   com.journal.service.ManuscriptService
 *   com.journal.impl.ManuscriptImpl
 *   com.journal.util.JsonUtil
 */
public class AssignmentServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    /** 稿件业务服务 */
    private final ManuscriptService manuscriptService = new ManuscriptService();

    /** 稿件数据操作实现（用于查询已指派稿件） */
    private final ManuscriptImpl manuscriptImpl = new ManuscriptImpl();

    /**
     * 处理 GET 请求，获取所有已指派审稿人的稿件列表。
     * 调用 ManuscriptImpl.findAssigned() 获取已指派稿件，遍历构建指派记录列表。
     * 每条指派记录包含：id、paperId、paperTitle、reviewerId、reviewerName、assignedDate。
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
            List<Manuscript> assignedManuscripts = manuscriptImpl.findAssigned();
            List<Map<String, Object>> assignments = new ArrayList<>();

            if (assignedManuscripts != null) {
                for (Manuscript m : assignedManuscripts) {
                    Map<String, Object> assignment = new LinkedHashMap<>();
                    assignment.put("id", "asgn-" + m.getId());
                    assignment.put("paperId", String.valueOf(m.getId()));
                    assignment.put("paperTitle", m.getTitle());
                    assignment.put("reviewerId",
                            m.getAssignedReviewerId() != null ? String.valueOf(m.getAssignedReviewerId()) : null);
                    assignment.put("reviewerName", m.getAssignedReviewerName());
                    assignment.put("assignedDate", m.getSubmissionDate());
                    assignments.add(assignment);
                }
            }

            writeJson(resp, JsonUtil.writeSuccess(assignments));
        } catch (BusinessException e) {
            writeJson(resp, JsonUtil.writeError(400, e.getMessage()));
        } catch (Exception e) {
            writeJson(resp, JsonUtil.writeError(500, "服务器内部错误：" + e.getMessage()));
        }
    }

    /**
     * 处理 POST 请求，为指定稿件指派审稿人。
     * 读取请求体 JSON，提取 paperId 和 reviewerId，调用 ManuscriptService.assignReviewer()。
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

            int paperId = getJsonInt(json, "paperId", 0);
            int reviewerId = getJsonInt(json, "reviewerId", 0);

            if (paperId <= 0) {
                writeJson(resp, JsonUtil.writeError(400, "缺少有效的 paperId"));
                return;
            }
            if (reviewerId <= 0) {
                writeJson(resp, JsonUtil.writeError(400, "缺少有效的 reviewerId"));
                return;
            }

            boolean success = manuscriptService.assignReviewer(paperId, reviewerId);
            if (success) {
                Map<String, Object> result = new LinkedHashMap<>();
                result.put("paperId", paperId);
                result.put("reviewerId", reviewerId);
                result.put("message", "审稿人指派成功");
                writeJson(resp, JsonUtil.writeSuccess(result));
            } else {
                writeJson(resp, JsonUtil.writeError(500, "审稿人指派失败"));
            }
        } catch (BusinessException e) {
            writeJson(resp, JsonUtil.writeError(400, e.getMessage()));
        } catch (Exception e) {
            writeJson(resp, JsonUtil.writeError(500, "服务器内部错误：" + e.getMessage()));
        }
    }
}
