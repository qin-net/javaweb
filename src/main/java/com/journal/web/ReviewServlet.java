package com.journal.web;

import com.google.gson.JsonObject;
import com.journal.exception.BusinessException;
import com.journal.model.ReviewRecord;
import com.journal.service.ReviewService;
import com.journal.util.DateUtil;
import com.journal.util.JsonUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 审稿记录 Servlet
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.web
 *
 * 功能详述：
 *   对接 /api/reviews/* 路径，提供审稿记录的查询和提交接口。
 *   GET  请求支持按稿件 ID、审稿人 ID 筛选或获取全部审稿记录；
 *   POST 请求接收审稿意见 JSON，提交审稿记录并自动更新稿件状态。
 *
 * 依赖：
 *   com.journal.service.ReviewService
 *   com.journal.util.JsonUtil
 *   com.journal.util.DateUtil
 */
public class ReviewServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    /** 审稿业务服务 */
    private final ReviewService reviewService = new ReviewService();

    /**
     * 处理 GET 请求。
     * GET /api/reviews                → 获取全部审稿记录
     * GET /api/reviews?paperId={id}   → 按稿件 ID 获取审稿记录
     * GET /api/reviews?reviewerId={id}→ 按审稿人 ID 获取审稿记录
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
            if (pathId != null) {
                writeJson(resp, JsonUtil.writeError(404, "未找到对应的资源"));
                return;
            }

            String paperIdStr = getParam(req, "paperId");
            String reviewerIdStr = getParam(req, "reviewerId");

            List<ReviewRecord> reviews;
            if (paperIdStr != null) {
                int paperId = Integer.parseInt(paperIdStr);
                reviews = reviewService.getByPaperId(paperId);
            } else if (reviewerIdStr != null) {
                int reviewerId = Integer.parseInt(reviewerIdStr);
                reviews = reviewService.getByReviewerId(reviewerId);
            } else {
                reviews = reviewService.getAll();
            }

            writeJson(resp, JsonUtil.writeSuccess(reviews));
        } catch (BusinessException e) {
            writeJson(resp, JsonUtil.writeError(400, e.getMessage()));
        } catch (Exception e) {
            writeJson(resp, JsonUtil.writeError(500, "服务器内部错误：" + e.getMessage()));
        }
    }

    /**
     * 处理 POST 请求，提交审稿意见。
     * 读取请求体 JSON，解析为 ReviewRecord，设置审稿日期后调用 ReviewService.submitReview()。
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

            ReviewRecord record = new ReviewRecord();
            record.setPaperId(getJsonInt(json, "paperId", 0));
            record.setReviewerId(getJsonInt(json, "reviewerId", 0));
            record.setDecision(getJsonString(json, "decision"));
            record.setComments(getJsonString(json, "comments"));
            record.setPaperTitle(getJsonString(json, "paperTitle"));
            record.setReviewerName(getJsonString(json, "reviewerName"));
            record.setReviewDate(DateUtil.today());

            int newId = reviewService.submitReview(record);
            record.setId(newId);

            writeJson(resp, JsonUtil.writeSuccess(record));
        } catch (BusinessException e) {
            writeJson(resp, JsonUtil.writeError(400, e.getMessage()));
        } catch (Exception e) {
            writeJson(resp, JsonUtil.writeError(500, "服务器内部错误：" + e.getMessage()));
        }
    }
}
