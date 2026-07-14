package com.journal.util;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * JSON 工具类，基于 Gson 封装，提供对象与 JSON 字符串之间的序列化与反序列化操作。
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.util
 *
 * 功能详述：
 *   提供将 Java 对象序列化为 JSON 字符串的 toJson 方法，
 *   以及将 JSON 字符串反序列化为指定类型对象的 fromJson 方法。
 *   同时提供 writeSuccess 和 writeError 方法，用于快速构建
 *   统一格式的响应 JSON（包含 code、message、data 三个字段）。
 *   Gson 实例以 static final 形式持有，全局共享。
 *
 * 依赖：
 *   Gson（com.google.gson.Gson）
 */
public class JsonUtil {

    /** Gson 实例，全局共享，线程安全（Gson 本身是线程安全的） */
    private static final Gson GSON = new Gson();

    /**
     * 将 Java 对象序列化为 JSON 字符串。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param obj 要序列化的对象
     * @return JSON 字符串；若入参为 null 则返回 "null"
     */
    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    /**
     * 将 JSON 字符串反序列化为指定类型的 Java 对象。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param <T>       目标类型
     * @param json      JSON 字符串
     * @param classOfT  目标类型的 Class 对象
     * @return 反序列化得到的对象；若 json 为 null 则返回 null
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        if (json == null) {
            return null;
        }
        return GSON.fromJson(json, classOfT);
    }

    /**
     * 构建成功响应的 JSON 字符串，格式为：{"code":200,"message":"success","data":...}
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param data 响应数据，可为任意类型
     * @return 包含 code、message、data 的 JSON 字符串
     */
    public static String writeSuccess(Object data) {
        Map<String, Object> result = new HashMap<>(3);
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", data);
        return GSON.toJson(result);
    }

    /**
     * 构建错误响应的 JSON 字符串，格式为：{"code":xxx,"message":"...","data":null}
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param code    错误码
     * @param message 错误信息
     * @return 包含 code、message、data(null) 的 JSON 字符串
     */
    public static String writeError(int code, String message) {
        Map<String, Object> result = new HashMap<>(3);
        result.put("code", code);
        result.put("message", message);
        result.put("data", null);
        return GSON.toJson(result);
    }
}
