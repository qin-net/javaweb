package com.journal.web;

import com.journal.exception.BusinessException;
import com.journal.model.ReviewRecord;
import com.journal.model.Reviewer;
import com.journal.service.ReviewService;
import com.journal.service.ReviewerService;
import com.journal.util.JsonUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 审稿人 Servlet
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.web
 *
 * 功能详述：
 *   对接 /api/reviewers/* 路径，提供审稿人的查询接口。
 *   GET /api/reviewers                 → 获取审稿人列表（支持 keyword 关键词搜索）
 *   GET /api/reviewers/{id}            → 获取审稿人详情
 *   GET /api/reviewers/{id}/reviews    → 获取审稿人的审稿记录
 *
 * 依赖：
 *   com.journal.service.ReviewerService
 *   com.journal.util.JsonUtil
 */
public class ReviewerServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    /** 审稿人业务服务 */
    private final ReviewerService reviewerService = new ReviewerService();

    /** 审稿业务服务（用于查询审稿人的审稿记录） */
    private final ReviewService reviewService = new ReviewService();

    /**
     * 处理 GET 请求。
     * 无路径 ID 且有 keyword 参数时 → 按关键词搜索审稿人；
     * 无路径 ID 且无 keyword 参数时 → 获取全部审稿人列表；
     * 有路径 ID 时 → 获取审稿人详情。
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
                List<Reviewer> reviewers;
                if (keyword != null) {
                    reviewers = reviewerService.search(keyword);
                } else {
                    reviewers = reviewerService.getAll();
                }
                writeJson(resp, JsonUtil.writeSuccess(reviewers));
            } else if ("reviews".equals(subResource)) {
                int reviewerId = Integer.parseInt(pathId);
                List<ReviewRecord> reviews = reviewService.getByReviewerId(reviewerId);
                writeJson(resp, JsonUtil.writeSuccess(reviews));
            } else {
                int id = Integer.parseInt(pathId);
                Reviewer reviewer = reviewerService.getById(id);
                if (reviewer == null) {
                    writeJson(resp, JsonUtil.writeError(404, "审稿人不存在"));
                } else {
                    writeJson(resp, JsonUtil.writeSuccess(reviewer));
                }
            }
        } catch (BusinessException e) {
            writeJson(resp, JsonUtil.writeError(400, e.getMessage()));
        } catch (Exception e) {
            writeJson(resp, JsonUtil.writeError(500, "服务器内部错误：" + e.getMessage()));
        }
    }
}
