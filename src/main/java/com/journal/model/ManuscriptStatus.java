package com.journal.model;

/**
 * 稿件状态常量类
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.model
 *
 * 功能详述：
 *   定义稿件在系统流转过程中的各个状态常量，包括已提交、审稿中、
 *   已录用、已拒绝、已发表五种状态，并提供状态合法性校验方法。
 */
public class ManuscriptStatus {

    /** 已提交 */
    public static final String SUBMITTED = "submitted";

    /** 审稿中 */
    public static final String REVIEWING = "reviewing";

    /** 已录用 */
    public static final String ACCEPTED = "accepted";

    /** 已拒绝 */
    public static final String REJECTED = "rejected";

    /** 已发表 */
    public static final String PUBLISHED = "published";

    /**
     * 私有构造器，禁止实例化常量类
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    private ManuscriptStatus() {
    }

    /**
     * 校验传入的状态值是否为合法的稿件状态
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param status 待校验的状态字符串
     * @return 若状态值合法返回 true，否则返回 false
     */
    public static boolean isValid(String status) {
        if (status == null) {
            return false;
        }
        return SUBMITTED.equals(status)
                || REVIEWING.equals(status)
                || ACCEPTED.equals(status)
                || REJECTED.equals(status)
                || PUBLISHED.equals(status);
    }
}
