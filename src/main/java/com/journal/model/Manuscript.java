package com.journal.model;

import java.util.List;

/**
 * 稿件实体类
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.model
 *
 * 功能详述：
 *   对应稿件信息表，封装稿件的标题、摘要、关键词、正文内容、
 *   所属期刊、作者信息、状态及各流程节点日期、指派审稿人信息等。
 *   其中参考文献列表为非数据库字段，由关联查询填充；
 *   所有日期字段均使用 String 类型（格式 yyyy-MM-dd），便于 JSON 序列化。
 */
public class Manuscript {

    /** 稿件ID */
    private int id;

    /** 标题 */
    private String title;

    /** 摘要 */
    private String abstractText;

    /** 关键词列表 */
    private List<String> keywords;

    /** 正文内容 */
    private String content;

    /** 参考文献列表（非数据库字段，关联查询填充） */
    private List<Reference> references;

    /** 所属期刊名称 */
    private String journalName;

    /** 作者ID */
    private int authorId;

    /** 作者姓名 */
    private String authorName;

    /** 稿件状态 */
    private String status;

    /** 投稿日期（yyyy-MM-dd） */
    private String submissionDate;

    /** 审稿日期（yyyy-MM-dd） */
    private String reviewDate;

    /** 录用日期（yyyy-MM-dd） */
    private String acceptanceDate;

    /** 发表日期（yyyy-MM-dd） */
    private String publicationDate;

    /** 指派审稿人ID（可空） */
    private Integer assignedReviewerId;

    /** 指派审稿人姓名 */
    private String assignedReviewerName;

    /**
     * 无参构造器
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public Manuscript() {
    }

    /**
     * 全参构造器
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param id                   稿件ID
     * @param title                标题
     * @param abstractText         摘要
     * @param keywords             关键词列表
     * @param content              正文内容
     * @param references           参考文献列表
     * @param journalName          所属期刊名称
     * @param authorId             作者ID
     * @param authorName           作者姓名
     * @param status               稿件状态
     * @param submissionDate       投稿日期
     * @param reviewDate           审稿日期
     * @param acceptanceDate       录用日期
     * @param publicationDate      发表日期
     * @param assignedReviewerId   指派审稿人ID
     * @param assignedReviewerName 指派审稿人姓名
     */
    public Manuscript(int id, String title, String abstractText, List<String> keywords, String content,
                       List<Reference> references, String journalName, int authorId, String authorName,
                       String status, String submissionDate, String reviewDate, String acceptanceDate,
                       String publicationDate, Integer assignedReviewerId, String assignedReviewerName) {
        this.id = id;
        this.title = title;
        this.abstractText = abstractText;
        this.keywords = keywords;
        this.content = content;
        this.references = references;
        this.journalName = journalName;
        this.authorId = authorId;
        this.authorName = authorName;
        this.status = status;
        this.submissionDate = submissionDate;
        this.reviewDate = reviewDate;
        this.acceptanceDate = acceptanceDate;
        this.publicationDate = publicationDate;
        this.assignedReviewerId = assignedReviewerId;
        this.assignedReviewerName = assignedReviewerName;
    }

    /**
     * 获取稿件ID
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public int getId() {
        return id;
    }

    /**
     * 设置稿件ID
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 获取标题
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置标题
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取摘要
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public String getAbstractText() {
        return abstractText;
    }

    /**
     * 设置摘要
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    /**
     * 获取关键词列表
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public List<String> getKeywords() {
        return keywords;
    }

    /**
     * 设置关键词列表
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    /**
     * 获取正文内容
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置正文内容
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取参考文献列表
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public List<Reference> getReferences() {
        return references;
    }

    /**
     * 设置参考文献列表
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setReferences(List<Reference> references) {
        this.references = references;
    }

    /**
     * 获取所属期刊名称
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public String getJournalName() {
        return journalName;
    }

    /**
     * 设置所属期刊名称
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setJournalName(String journalName) {
        this.journalName = journalName;
    }

    /**
     * 获取作者ID
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public int getAuthorId() {
        return authorId;
    }

    /**
     * 设置作者ID
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    /**
     * 获取作者姓名
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public String getAuthorName() {
        return authorName;
    }

    /**
     * 设置作者姓名
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    /**
     * 获取稿件状态
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置稿件状态
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取投稿日期
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public String getSubmissionDate() {
        return submissionDate;
    }

    /**
     * 设置投稿日期
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setSubmissionDate(String submissionDate) {
        this.submissionDate = submissionDate;
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
     * 获取录用日期
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public String getAcceptanceDate() {
        return acceptanceDate;
    }

    /**
     * 设置录用日期
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setAcceptanceDate(String acceptanceDate) {
        this.acceptanceDate = acceptanceDate;
    }

    /**
     * 获取发表日期
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public String getPublicationDate() {
        return publicationDate;
    }

    /**
     * 设置发表日期
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    /**
     * 获取指派审稿人ID
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public Integer getAssignedReviewerId() {
        return assignedReviewerId;
    }

    /**
     * 设置指派审稿人ID
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setAssignedReviewerId(Integer assignedReviewerId) {
        this.assignedReviewerId = assignedReviewerId;
    }

    /**
     * 获取指派审稿人姓名
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public String getAssignedReviewerName() {
        return assignedReviewerName;
    }

    /**
     * 设置指派审稿人姓名
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setAssignedReviewerName(String assignedReviewerName) {
        this.assignedReviewerName = assignedReviewerName;
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
        return "Manuscript{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", abstractText='" + abstractText + '\'' +
                ", keywords=" + keywords +
                ", content='" + content + '\'' +
                ", references=" + references +
                ", journalName='" + journalName + '\'' +
                ", authorId=" + authorId +
                ", authorName='" + authorName + '\'' +
                ", status='" + status + '\'' +
                ", submissionDate='" + submissionDate + '\'' +
                ", reviewDate='" + reviewDate + '\'' +
                ", acceptanceDate='" + acceptanceDate + '\'' +
                ", publicationDate='" + publicationDate + '\'' +
                ", assignedReviewerId=" + assignedReviewerId +
                ", assignedReviewerName='" + assignedReviewerName + '\'' +
                '}';
    }
}
