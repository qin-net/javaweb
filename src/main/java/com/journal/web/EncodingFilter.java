package com.journal.web;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * 字符编码过滤器
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.web
 *
 * 功能详述：
 *   对所有请求（/*）统一设置请求和响应的字符编码为 UTF-8，
 *   解决中文乱码问题。该过滤器在过滤器链中最先执行，
 *   确保后续 Servlet 获取的参数和输出的内容均以 UTF-8 编码处理。
 *
 * 依赖：
 *   javax.servlet.Filter
 */
public class EncodingFilter implements Filter {

    /** 统一字符编码 */
    private static final String ENCODING = "UTF-8";

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
     * 核心过滤方法，设置请求和响应的字符编码为 UTF-8 后继续执行过滤器链。
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
        request.setCharacterEncoding(ENCODING);
        response.setCharacterEncoding(ENCODING);
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
