package com.journal.model;

/**
 * 系统用户实体类
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-15
 * 所属包：com.journal.model
 *
 * 功能详述：
 *   对应系统用户表 sys_user，封装用户的登录账号、密码（SHA-256哈希）、
 *   真实姓名、角色ID、关联业务表ID（ref_id）、邮箱、状态及创建时间等。
 *   其中 roleCode、roleName、dataScope 为非持久化字段，
 *   由查询时 JOIN sys_role 表动态填充，用于权限控制判断。
 *   ref_id 在 admin 时为 NULL，reviewer 时为 reviewer 表的 id，
 *   author 时为 author 表的 id。
 */
public class SysUser {

    /** 用户ID */
    private int id;

    /** 登录用户名 */
    private String username;

    /** 登录密码（SHA-256哈希值） */
    private String password;

    /** 真实姓名 */
    private String realName;

    /** 角色ID */
    private int roleId;

    /** 关联业务表ID（admin时为NULL，reviewer时为reviewer表id，author时为author表id） */
    private Integer refId;

    /** 邮箱 */
    private String email;

    /** 账号状态（1=启用，0=禁用） */
    private int status;

    /** 创建时间（yyyy-MM-dd HH:mm:ss） */
    private String createTime;

    /** 角色编码（非持久化字段，JOIN sys_role 获取） */
    private String roleCode;

    /** 角色名称（非持久化字段，JOIN sys_role 获取） */
    private String roleName;

    /** 数据范围（非持久化字段，JOIN sys_role 获取；1=全部数据，2=仅本人数据） */
    private int dataScope;

    /**
     * 无参构造器
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     */
    public SysUser() {
    }

    /**
     * 获取用户ID
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @return 用户ID
     */
    public int getId() {
        return id;
    }

    /**
     * 设置用户ID
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param id 用户ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 获取登录用户名
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @return 登录用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置登录用户名
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param username 登录用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取登录密码（SHA-256哈希值）
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @return 登录密码哈希值
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置登录密码
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param password 登录密码哈希值
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取真实姓名
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @return 真实姓名
     */
    public String getRealName() {
        return realName;
    }

    /**
     * 设置真实姓名
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param realName 真实姓名
     */
    public void setRealName(String realName) {
        this.realName = realName;
    }

    /**
     * 获取角色ID
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @return 角色ID
     */
    public int getRoleId() {
        return roleId;
    }

    /**
     * 设置角色ID
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param roleId 角色ID
     */
    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    /**
     * 获取关联业务表ID
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @return 关联业务表ID，admin时为null
     */
    public Integer getRefId() {
        return refId;
    }

    /**
     * 设置关联业务表ID
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param refId 关联业务表ID
     */
    public void setRefId(Integer refId) {
        this.refId = refId;
    }

    /**
     * 获取邮箱
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @return 邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置邮箱
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param email 邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 获取账号状态
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @return 账号状态（1=启用，0=禁用）
     */
    public int getStatus() {
        return status;
    }

    /**
     * 设置账号状态
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param status 账号状态
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * 获取创建时间
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @return 创建时间字符串
     */
    public String getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param createTime 创建时间字符串
     */
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取角色编码（非持久化字段）
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @return 角色编码
     */
    public String getRoleCode() {
        return roleCode;
    }

    /**
     * 设置角色编码（非持久化字段）
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param roleCode 角色编码
     */
    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    /**
     * 获取角色名称（非持久化字段）
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @return 角色名称
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * 设置角色名称（非持久化字段）
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param roleName 角色名称
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    /**
     * 获取数据范围（非持久化字段）
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @return 数据范围（1=全部数据，2=仅本人数据）
     */
    public int getDataScope() {
        return dataScope;
    }

    /**
     * 设置数据范围（非持久化字段）
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param dataScope 数据范围
     */
    public void setDataScope(int dataScope) {
        this.dataScope = dataScope;
    }

    /**
     * 返回该对象的字符串表示，便于调试和日志输出
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @return 包含所有字段信息的字符串
     */
    @Override
    public String toString() {
        return "SysUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", realName='" + realName + '\'' +
                ", roleId=" + roleId +
                ", refId=" + refId +
                ", email='" + email + '\'' +
                ", status=" + status +
                ", createTime='" + createTime + '\'' +
                ", roleCode='" + roleCode + '\'' +
                ", roleName='" + roleName + '\'' +
                ", dataScope=" + dataScope +
                '}';
    }
}
