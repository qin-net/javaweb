package com.journal.web;

import com.journal.exception.BusinessException;
import com.journal.model.Author;
import com.journal.model.Manuscript;
import com.journal.service.AuthorService;
import com.journal.service.ManuscriptService;
import com.journal.util.JsonUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 作者 Servlet
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.web
 *
 * 功能详述：
 *   对接 /api/authors/* 路径，提供作者的查询接口。
 *   GET /api/authors               → 获取作者列表（支持 keyword 关键词搜索）
 *   GET /api/authors/{id}          → 获取作者详情
 *   GET /api/authors/{id}/papers   → 获取作者的论文列表
 *
 * 依赖：
 *   com.journal.service.AuthorService
 *   com.journal.util.JsonUtil
 */
public class AuthorServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    /** 作者业务服务 */
    private final AuthorService authorService = new AuthorService();

    /** 稿件业务服务（用于查询作者的论文列表） */
    private final ManuscriptService manuscriptService = new ManuscriptService();

    /**
     * 处理 GET 请求。
     * 无路径 ID 且有 keyword 参数时 → 按关键词搜索作者；
     * 无路径 ID 且无 keyword 参数时 → 获取全部作者列表；
     * 有路径 ID 时 → 获取作者详情。
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
                String keyword = getParam(req, "keyword");
                List<Author> authors;
                if (keyword != null) {
                    authors = authorService.search(keyword);
                } else {
                    authors = authorService.getAll();
                }
                writeJson(resp, JsonUtil.writeSuccess(authors));
            } else if ("papers".equals(subResource)) {
                int authorId = Integer.parseInt(pathId);
                List<Manuscript> papers = manuscriptService.search(null, null, null, String.valueOf(authorId));
                writeJson(resp, JsonUtil.writeSuccess(papers));
            } else {
                int id = Integer.parseInt(pathId);
                Author author = authorService.getById(id);
                if (author == null) {
                    writeJson(resp, JsonUtil.writeError(404, "作者不存在"));
                } else {
                    writeJson(resp, JsonUtil.writeSuccess(author));
                }
            }
        } catch (BusinessException e) {
            writeJson(resp, JsonUtil.writeError(400, e.getMessage()));
        } catch (Exception e) {
            writeJson(resp, JsonUtil.writeError(500, "服务器内部错误：" + e.getMessage()));
        }
    }
}
