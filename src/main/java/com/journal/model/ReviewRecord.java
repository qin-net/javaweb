package com.journal.model;

/**
 * 审稿记录实体类
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.model
 *
 * 功能详述：
 *   对应审稿记录信息表，封装某位审稿人对某篇稿件的审稿意见，
 *   包括审稿决定、审稿意见内容及审稿日期等信息。
 */
public class ReviewRecord {

    /** 审稿记录ID */
    private int id;

    /** 稿件ID */
    private int paperId;

    /** 稿件标题 */
    private String paperTitle;

    /** 审稿人ID */
    private int reviewerId;

    /** 审稿人姓名 */
    private String reviewerName;

    /** 审稿决定 */
    private String decision;

    /** 审稿意见 */
    private String comments;

    /** 审稿日期（yyyy-MM-dd） */
    private String reviewDate;

    /**
     * 无参构造器
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public ReviewRecord() {
    }

    /**
     * 全参构造器
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param id           审稿记录ID
     * @param paperId      稿件ID
     * @param paperTitle   稿件标题
     * @param reviewerId   审稿人ID
     * @param reviewerName 审稿人姓名
     * @param decision     审稿决定
     * @param comments     审稿意见
     * @param reviewDate   审稿日期
     */
    public ReviewRecord(int id, int paperId, String paperTitle, int reviewerId, String reviewerName, String decision, String comments, String reviewDate) {
        this.id = id;
        this.paperId = paperId;
        this.paperTitle = paperTitle;
        this.reviewerId = reviewerId;
        this.reviewerName = reviewerName;
        this.decision = decision;
        this.comments = comments;
        this.reviewDate = reviewDate;
    }

    /**
     * 获取审稿记录ID
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public int getId() {
        return id;
    }

    /**
     * 设置审稿记录ID
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 获取稿件ID
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public int getPaperId() {
        return paperId;
    }

    /**
     * 设置稿件ID
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setPaperId(int paperId) {
        this.paperId = paperId;
    }

    /**
     * 获取稿件标题
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public String getPaperTitle() {
        return paperTitle;
    }

    /**
     * 设置稿件标题
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setPaperTitle(String paperTitle) {
        this.paperTitle = paperTitle;
    }

    /**
     * 获取审稿人ID
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public int getReviewerId() {
        return reviewerId;
    }

    /**
     * 设置审稿人ID
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setReviewerId(int reviewerId) {
        this.reviewerId = reviewerId;
    }

    /**
     * 获取审稿人姓名
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public String getReviewerName() {
        return reviewerName;
    }

    /**
     * 设置审稿人姓名
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    /**
     * 获取审稿决定
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public String getDecision() {
        return decision;
    }

    /**
     * 设置审稿决定
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setDecision(String decision) {
        this.decision = decision;
    }

    /**
     * 获取审稿意见
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public String getComments() {
        return comments;
    }

    /**
     * 设置审稿意见
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     * 获取审稿日期
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public String getReviewDate() {
        return reviewDate;
    }

    /**
     * 设置审稿日期
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }

    /**
     * 返回该对象的字符串表示，便于调试和日志输出
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @return 包含所有字段信息的字符串
     */
    @Override
    public String toString() {
        return "ReviewRecord{" +
                "id=" + id +
                ", paperId=" + paperId +
                ", paperTitle='" + paperTitle + '\'' +
                ", reviewerId=" + reviewerId +
                ", reviewerName='" + reviewerName + '\'' +
                ", decision='" + decision + '\'' +
                ", comments='" + comments + '\'' +
                ", reviewDate='" + reviewDate + '\'' +
                '}';
    }
}
