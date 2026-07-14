package com.journal.model;

import java.util.List;

/**
 * 作者实体类
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.model
 *
 * 功能详述：
 *   对应作者信息表，封装作者的基本信息（姓名、邮箱、所属单位、
 *   部门、联系电话），并附带其投稿稿件ID列表（非数据库字段，
 *   用于 API 返回时聚合展示）。
 */
public class Author {

    /** 作者ID */
    private int id;

    /** 姓名 */
    private String name;

    /** 邮箱 */
    private String email;

    /** 所属单位 */
    private String institution;

    /** 所属部门 */
    private String department;

    /** 联系电话 */
    private String phone;

    /** 投稿稿件ID列表（非数据库字段，用于 API 返回） */
    private List<String> paperIds;

    /**
     * 无参构造器
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public Author() {
    }

    /**
     * 全参构造器
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param id          作者ID
     * @param name        姓名
     * @param email       邮箱
     * @param institution 所属单位
     * @param department  所属部门
     * @param phone       联系电话
     * @param paperIds    投稿稿件ID列表
     */
    public Author(int id, String name, String email, String institution, String department, String phone, List<String> paperIds) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.institution = institution;
        this.department = department;
        this.phone = phone;
        this.paperIds = paperIds;
    }

    /**
     * 获取作者ID
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public int getId() {
        return id;
    }

    /**
     * 设置作者ID
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 获取姓名
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public String getName() {
        return name;
    }

    /**
     * 设置姓名
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取邮箱
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置邮箱
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 获取所属单位
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public String getInstitution() {
        return institution;
    }

    /**
     * 设置所属单位
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setInstitution(String institution) {
        this.institution = institution;
    }

    /**
     * 获取所属部门
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public String getDepartment() {
        return department;
    }

    /**
     * 设置所属部门
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     * 获取联系电话
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 设置联系电话
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 获取投稿稿件ID列表
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public List<String> getPaperIds() {
        return paperIds;
    }

    /**
     * 设置投稿稿件ID列表
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     */
    public void setPaperIds(List<String> paperIds) {
        this.paperIds = paperIds;
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
        return "Author{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", institution='" + institution + '\'' +
                ", department='" + department + '\'' +
                ", phone='" + phone + '\'' +
                ", paperIds=" + paperIds +
                '}';
    }
}
