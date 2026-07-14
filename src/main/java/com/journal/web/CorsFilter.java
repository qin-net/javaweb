package com.journal.web;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 跨域资源共享（CORS）过滤器
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.web
 *
 * 功能详述：
 *   对所有 /api/* 路径的请求统一设置 CORS 响应头，允许任意来源的
 *   跨域访问。支持的 HTTP 方法包括 GET、POST、PUT、DELETE、OPTIONS。
 *   对于 OPTIONS 预检请求，直接返回 200 状态码，不继续执行后续过滤器链，
 *   从而快速响应浏览器的跨域预检探测。
 *
 * 依赖：
 *   javax.servlet.Filter
 */
public class CorsFilter implements Filter {

    /**
     * 过滤器初始化方法，无额外初始化逻辑。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param filterConfig 过滤器配置对象
     * @throws ServletException 如果初始化过程中发生异常
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 无需额外初始化操作
    }

    /**
     * 核心过滤方法，设置 CORS 响应头并处理 OPTIONS 预检请求。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
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
        HttpServletResponse resp = (HttpServletResponse) response;

        // 设置 CORS 响应头
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setHeader("Access-Control-Max-Age", "3600");

        // OPTIONS 预检请求直接返回 200，不继续执行过滤器链
        if ("OPTIONS".equalsIgnoreCase(((HttpServletRequest) request).getMethod())) {
            resp.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * 过滤器销毁方法，无额外清理逻辑。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    @Override
    public void destroy() {
        // 无需额外清理操作
    }
}
