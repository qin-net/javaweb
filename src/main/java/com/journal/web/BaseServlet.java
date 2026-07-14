package com.journal.web;

import com.google.gson.Gson;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet 抽象基类，提供所有业务 Servlet 共用的工具方法。
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.web
 *
 * 功能详述：
 *   继承 HttpServlet，封装 JSON 响应输出、请求体读取、路径 ID 提取、
 *   整数参数安全获取等通用方法。所有业务 Servlet（PaperServlet、
 *   ReviewServlet 等）均继承此基类，复用上述能力，避免重复代码。
 *   内部持有静态 Gson 实例，供子类进行 JSON 序列化与反序列化。
 *
 * 依赖：
 *   javax.servlet.http.HttpServlet
 *   com.google.gson.Gson
 */
public abstract class BaseServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /** Gson 实例，全局共享，供子类使用 */
    protected static final Gson GSON = new Gson();

    /**
     * 向响应中写入 JSON 字符串。
     * 设置 Content-Type 为 application/json;charset=UTF-8，写出 JSON 内容并刷新输出流。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param resp HttpServletResponse 对象
     * @param json 要写出的 JSON 字符串
     * @throws IOException 如果写出过程中发生 I/O 异常
     */
    protected void writeJson(HttpServletResponse resp, String json) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        out.write(json);
        out.flush();
    }

    /**
     * 读取 HTTP 请求体的字符串内容。
     * 通过 BufferedReader 逐行读取请求体并拼接为完整字符串。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param req HttpServletRequest 对象
     * @return 请求体的完整字符串内容
     * @throws IOException 如果读取过程中发生 I/O 异常
     */
    protected String readBody(HttpServletRequest req) throws IOException {
        BufferedReader reader = req.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    /**
     * 从 URI 路径末尾提取 ID。
     * 例如路径 /api/papers/123 返回 "123"，路径 /api/papers/123/status 返回 "123"。
     * 如果路径为空、根路径或无 ID 段，则返回 null。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param req HttpServletRequest 对象
     * @return 路径中的 ID 字符串，无 ID 时返回 null
     */
    protected String getPathId(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.isEmpty()) {
            return null;
        }
        // 去掉开头的斜杠
        String path = pathInfo.substring(1);
        // 取第一个路径段作为 ID
        int slashIndex = path.indexOf('/');
        if (slashIndex > 0) {
            return path.substring(0, slashIndex);
        }
        return path;
    }

    /**
     * 从 URI 路径中提取子资源名称。
     * 例如 /api/papers/123/reviews 返回 "reviews"，/api/papers/123 返回 null。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param req HttpServletRequest 对象
     * @return 子资源名称字符串，无子资源时返回 null
     */
    protected String getSubResource(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.isEmpty()) {
            return null;
        }
        String path = pathInfo.substring(1);
        int slashIndex = path.indexOf('/');
        if (slashIndex > 0 && slashIndex < path.length() - 1) {
            return path.substring(slashIndex + 1);
        }
        return null;
    }

    /**
     * 安全获取整数类型的请求参数。
     * 如果参数不存在或无法解析为整数，则返回默认值。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param req          HttpServletRequest 对象
     * @param name         参数名
     * @param defaultValue 默认值
     * @return 参数的整数值，或默认值
     */
    protected int getIntParam(HttpServletRequest req, String name, int defaultValue) {
        String value = req.getParameter(name);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 安全获取字符串类型的请求参数。
     * 如果参数为 null 或空白字符串，则返回 null。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param req  HttpServletRequest 对象
     * @param name 参数名
     * @return 参数值（去除首尾空白），为空时返回 null
     */
    protected String getParam(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    /**
     * 从 JsonObject 中安全获取字符串字段。
     * 如果字段不存在或为 JsonNull，则返回 null。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param json JsonObject 对象
     * @param key  字段名
     * @return 字段的字符串值，不存在或为 null 时返回 null
     */
    protected String getJsonString(com.google.gson.JsonObject json, String key) {
        if (json.has(key) && !json.get(key).isJsonNull()) {
            return json.get(key).getAsString();
        }
        return null;
    }

    /**
     * 从 JsonObject 中安全获取整数字段。
     * 如果字段不存在或为 JsonNull，则返回默认值。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param json         JsonObject 对象
     * @param key          字段名
     * @param defaultValue 默认值
     * @return 字段的整数值，不存在或为 null 时返回默认值
     */
    protected int getJsonInt(com.google.gson.JsonObject json, String key, int defaultValue) {
        if (json.has(key) && !json.get(key).isJsonNull()) {
            com.google.gson.JsonElement elem = json.get(key);
            if (elem.isJsonPrimitive()) {
                com.google.gson.JsonPrimitive prim = elem.getAsJsonPrimitive();
                if (prim.isNumber()) {
                    return prim.getAsInt();
                } else if (prim.isString()) {
                    try {
                        return Integer.parseInt(prim.getAsString().trim());
                    } catch (NumberFormatException e) {
                        return defaultValue;
                    }
                }
            }
        }
        return defaultValue;
    }
}
