package com.journal.model;

/**
 * 系统角色实体类
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-15
 * 所属包：com.journal.model
 *
 * 功能详述：
 *   对应系统角色表 sys_role，封装角色的名称、编码、数据范围、
 *   描述及状态等信息。data_scope 字段用于权限控制：
 *   1=全部数据（管理员可见所有数据），2=仅本人数据（作者/审稿人
 *   仅可见与自己关联的数据）。角色编码（role_code）如 admin、
 *   reviewer、author 等，用于在业务逻辑中判断用户角色。
 */
public class SysRole {

    /** 角色ID */
    private int id;

    /** 角色名称 */
    private String roleName;

    /** 角色编码（如 admin、reviewer、author） */
    private String roleCode;

    /** 数据范围（1=全部数据，2=仅本人数据） */
    private int dataScope;

    /** 角色描述 */
    private String description;

    /** 角色状态（1=启用，0=禁用） */
    private int status;

    /**
     * 无参构造器
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     */
    public SysRole() {
    }

    /**
     * 获取角色ID
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @return 角色ID
     */
    public int getId() {
        return id;
    }

    /**
     * 设置角色ID
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param id 角色ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 获取角色名称
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
     * 设置角色名称
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
     * 获取角色编码
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
     * 设置角色编码
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
     * 获取数据范围
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
     * 设置数据范围
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
     * 获取角色描述
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @return 角色描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置角色描述
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param description 角色描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取角色状态
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @return 角色状态（1=启用，0=禁用）
     */
    public int getStatus() {
        return status;
    }

    /**
     * 设置角色状态
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param status 角色状态
     */
    public void setStatus(int status) {
        this.status = status;
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
        return "SysRole{" +
                "id=" + id +
                ", roleName='" + roleName + '\'' +
                ", roleCode='" + roleCode + '\'' +
                ", dataScope=" + dataScope +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
