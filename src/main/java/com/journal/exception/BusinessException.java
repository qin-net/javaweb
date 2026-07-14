package com.journal.exception;

/**
 * 业务逻辑异常类，用于在业务处理过程中抛出可预期的业务错误。
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.exception
 *
 * 功能详述：
 *   继承自 RuntimeException，属于非受检异常。
 *   在期刊投稿管理系统的业务逻辑层中，当检测到违反业务规则的场景
 *   （如重复投稿、状态非法等）时抛出此异常，由全局异常处理器统一捕获并返回错误响应。
 *
 * 依赖：
 *   JDK 标准库（java.lang.RuntimeException）
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 使用指定的错误信息构造 BusinessException。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param message 错误描述信息
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * 使用指定的错误信息和原因构造 BusinessException。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param message 错误描述信息
     * @param cause   导致此异常的原始异常
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
