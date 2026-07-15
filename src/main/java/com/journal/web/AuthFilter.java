package com.journal.web;

import com.journal.model.SysUser;
import com.journal.util.JsonUtil;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 认证过滤器，对 /api/* 请求进行登录状态校验。
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-15
 * 所属包：com.journal.web
 *
 * 功能详述：
 *   实现 javax.servlet.Filter 接口，对 /api/* 路径的请求进行
 *   统一的登录状态校验。放行规则如下：
 *   1. OPTIONS 请求（CORS 预检）直接放行；
 *   2. /api/auth/login 路径放行（登录接口无需认证）；
 *   3. 其他 /api/* 请求，检查 HttpSession 中是否存在 "currentUser"
 *      属性，不存在则返回 401 未授权 JSON 响应。
 *   数据权限过滤不在 Filter 中处理，由各 Servlet 根据 Session 中的
 *   用户信息（roleCode、dataScope、refId）自行过滤。
 *
 * 依赖：
 *   javax.servlet.Filter
 *   com.journal.util.JsonUtil
 *   com.journal.model.SysUser
 */
public class AuthFilter implements Filter {

    /**
     * 过滤器初始化方法，无额外初始化逻辑。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param filterConfig 过滤器配置对象
     * @throws ServletException 如果初始化过程中发生异常
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 无需额外初始化操作
    }

    /**
     * 核心过滤方法，对 /api/* 请求进行登录状态校验。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param request  Servlet 请求对象
     * @param response Servlet 响应对象
     * @param chain    过滤器链
     * @throws IOException      如果 I/O 操作发生异常
     * @throws ServletException 如果 Servlet 处理发生异常
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // 放行 OPTIONS 预检请求
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        // 放行登录接口
        String requestURI = req.getRequestURI();
        if (requestURI.contains("/api/auth/login")) {
            chain.doFilter(request, response);
            return;
        }

        // 检查 Session 中是否有 currentUser
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            resp.setContentType("application/json;charset=UTF-8");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(JsonUtil.writeError(401, "未登录或会话已过期，请重新登录"));
            resp.getWriter().flush();
            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * 过滤器销毁方法，无额外清理逻辑。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     */
    @Override
    public void destroy() {
        // 无需额外清理操作
    }
}
