package com.journal.model;

/**
 * 参考文献实体类
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.model
 *
 * 功能详述：
 *   对应参考文献信息表，封装某篇稿件所引用的参考文献详细信息，
 *   包括标题、作者、发表期刊、年份、卷期、页码及 DOI 等。
 */
public class Reference {

    /** 参考文献ID */
    private int id;

    /** 参考文献引用标识（如 [1]） */
    private String refKey;

    /** 所属稿件ID */
    private int manuscriptId;

    /** 文献标题 */
    private String title;

    /** 文献作者 */
    private String authors;

    /** 发表期刊 */
    private String journal;

    /** 发表年份 */
    private int year;

    /** 卷号 */
    private String volume;

    /** 期号 */
    private String issue;

    /** 页码 */
    private String pages;

    /** DOI */
    private String doi;

    /**
     * 无参构造器
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public Reference() {
    }

    /**
     * 全参构造器
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param id           参考文献ID
     * @param refKey       参考文献引用标识
     * @param manuscriptId 所属稿件ID
     * @param title        文献标题
     * @param authors      文献作者
     * @param journal      发表期刊
     * @param year         发表年份
     * @param volume       卷号
     * @param issue        期号
     * @param pages        页码
     * @param doi          DOI
     */
    public Reference(int id, String refKey, int manuscriptId, String title, String authors, String journal, int year, String volume, String issue, String pages, String doi) {
        this.id = id;
        this.refKey = refKey;
        this.manuscriptId = manuscriptId;
        this.title = title;
        this.authors = authors;
        this.journal = journal;
        this.year = year;
        this.volume = volume;
        this.issue = issue;
        this.pages = pages;
        this.doi = doi;
    }

    /**
     * 获取参考文献ID
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public int getId() {
        return id;
    }

    /**
     * 设置参考文献ID
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 获取参考文献引用标识
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public String getRefKey() {
        return refKey;
    }

    /**
     * 设置参考文献引用标识
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setRefKey(String refKey) {
        this.refKey = refKey;
    }

    /**
     * 获取所属稿件ID
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public int getManuscriptId() {
        return manuscriptId;
    }

    /**
     * 设置所属稿件ID
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setManuscriptId(int manuscriptId) {
        this.manuscriptId = manuscriptId;
    }

    /**
     * 获取文献标题
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置文献标题
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取文献作者
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public String getAuthors() {
        return authors;
    }

    /**
     * 设置文献作者
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setAuthors(String authors) {
        this.authors = authors;
    }

    /**
     * 获取发表期刊
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public String getJournal() {
        return journal;
    }

    /**
     * 设置发表期刊
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setJournal(String journal) {
        this.journal = journal;
    }

    /**
     * 获取发表年份
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public int getYear() {
        return year;
    }

    /**
     * 设置发表年份
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * 获取卷号
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public String getVolume() {
        return volume;
    }

    /**
     * 设置卷号
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setVolume(String volume) {
        this.volume = volume;
    }

    /**
     * 获取期号
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public String getIssue() {
        return issue;
    }

    /**
     * 设置期号
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setIssue(String issue) {
        this.issue = issue;
    }

    /**
     * 获取页码
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public String getPages() {
        return pages;
    }

    /**
     * 设置页码
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setPages(String pages) {
        this.pages = pages;
    }

    /**
     * 获取 DOI
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public String getDoi() {
        return doi;
    }

    /**
     * 设置 DOI
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setDoi(String doi) {
        this.doi = doi;
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
        return "Reference{" +
                "id=" + id +
                ", refKey='" + refKey + '\'' +
                ", manuscriptId=" + manuscriptId +
                ", title='" + title + '\'' +
                ", authors='" + authors + '\'' +
                ", journal='" + journal + '\'' +
                ", year=" + year +
                ", volume='" + volume + '\'' +
                ", issue='" + issue + '\'' +
                ", pages='" + pages + '\'' +
                ", doi='" + doi + '\'' +
                '}';
    }
}
