package com.journal.web;

import com.journal.exception.BusinessException;
import com.journal.service.DashboardService;
import com.journal.util.JsonUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 仪表盘数据 Servlet
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.web
 *
 * 功能详述：
 *   对接 /api/dashboard/* 路径，提供系统首页数据看板所需的统计信息接口。
 *   目前支持 GET /api/dashboard/stats，返回投稿总数、各状态稿件数量、
 *   各期刊稿件分布、月度投稿趋势及最近投稿稿件等聚合统计数据。
 *
 * 依赖：
 *   com.journal.service.DashboardService
 *   com.journal.util.JsonUtil
 */
public class DashboardServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    /** 仪表盘统计业务服务 */
    private final DashboardService dashboardService = new DashboardService();

    /**
     * 处理 GET 请求，根据路径分发到对应的统计接口。
     * GET /api/dashboard/stats → 返回仪表盘统计数据。
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
            String pathInfo = req.getPathInfo();
            if ("/stats".equals(pathInfo)) {
                Map<String, Object> stats = dashboardService.getStats();
                writeJson(resp, JsonUtil.writeSuccess(stats));
            } else {
                writeJson(resp, JsonUtil.writeError(404, "未找到对应的资源"));
            }
        } catch (BusinessException e) {
            writeJson(resp, JsonUtil.writeError(400, e.getMessage()));
        } catch (Exception e) {
            writeJson(resp, JsonUtil.writeError(500, "服务器内部错误：" + e.getMessage()));
        }
    }
}
