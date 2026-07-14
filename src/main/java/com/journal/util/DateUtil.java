package com.journal.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期处理工具类，提供日期格式化、解析以及类型转换功能。
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.util
 *
 * 功能详述：
 *   提供 java.util.Date 与 java.sql.Date 之间的互相转换方法，
 *   支持将日期对象格式化为 yyyy-MM-dd 字符串、将字符串解析为日期对象，
 *   以及获取当前日期的格式化字符串。内部使用 SimpleDateFormat 实现。
 *
 * 依赖：
 *   JDK 标准库（java.util.Date、java.sql.Date、java.text.SimpleDateFormat）
 */
public class DateUtil {

    /** 日期格式：yyyy-MM-dd */
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * 将 java.util.Date 转换为 java.sql.Date。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param date 要转换的 java.util.Date 对象
     * @return 转换后的 java.sql.Date 对象；若入参为 null 则返回 null
     */
    public static java.sql.Date toSqlDate(Date date) {
        if (date == null) {
            return null;
        }
        return new java.sql.Date(date.getTime());
    }

    /**
     * 将 java.sql.Date 转换为 java.util.Date。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param date 要转换的 java.sql.Date 对象
     * @return 转换后的 java.util.Date 对象；若入参为 null 则返回 null
     */
    public static Date toUtilDate(java.sql.Date date) {
        if (date == null) {
            return null;
        }
        return new Date(date.getTime());
    }

    /**
     * 将日期对象格式化为 yyyy-MM-dd 格式的字符串。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param date 要格式化的日期对象
     * @return 格式化后的字符串；若入参为 null 则返回 null
     */
    public static String format(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        return sdf.format(date);
    }

    /**
     * 将 yyyy-MM-dd 格式的字符串解析为 java.util.Date 对象。
     * 解析失败时将 ParseException 包装为 RuntimeException 抛出。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param dateStr 要解析的日期字符串
     * @return 解析得到的 Date 对象；若入参为 null 则返回 null
     * @throws RuntimeException 如果字符串格式不符合 yyyy-MM-dd 导致解析失败
     */
    public static Date parse(String dateStr) {
        if (dateStr == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException("日期解析失败，期望格式为 yyyy-MM-dd：" + dateStr, e);
        }
    }

    /**
     * 获取当前日期的 yyyy-MM-dd 格式字符串。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @return 当前日期的格式化字符串
     */
    public static String today() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        return sdf.format(new Date());
    }
}
