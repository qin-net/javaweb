package com.journal.model;

/**
 * 系统菜单实体类
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-15
 * 所属包：com.journal.model
 *
 * 功能详述：
 *   对应系统菜单表 sys_menu，封装菜单的名称、编码、前端路由路径、
 *   API匹配模式、图标、排序值及状态等信息。菜单通过 sys_role_menu
 *   关联表与角色建立多对多关系，用于实现 RBAC 权限控制中
 *   的前端菜单动态渲染和后端 API 访问控制。
 */
public class SysMenu {

    /** 菜单ID */
    private int id;

    /** 菜单名称 */
    private String menuName;

    /** 菜单编码 */
    private String menuCode;

    /** 前端路由路径 */
    private String path;

    /** API匹配模式（用于后端接口权限校验） */
    private String apiPattern;

    /** 菜单图标 */
    private String icon;

    /** 排序值（数值越小越靠前） */
    private int sortOrder;

    /** 菜单状态（1=启用，0=禁用） */
    private int status;

    /**
     * 无参构造器
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     */
    public SysMenu() {
    }

    /**
     * 获取菜单ID
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @return 菜单ID
     */
    public int getId() {
        return id;
    }

    /**
     * 设置菜单ID
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param id 菜单ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 获取菜单名称
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @return 菜单名称
     */
    public String getMenuName() {
        return menuName;
    }

    /**
     * 设置菜单名称
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param menuName 菜单名称
     */
    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    /**
     * 获取菜单编码
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @return 菜单编码
     */
    public String getMenuCode() {
        return menuCode;
    }

    /**
     * 设置菜单编码
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param menuCode 菜单编码
     */
    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    /**
     * 获取前端路由路径
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @return 前端路由路径
     */
    public String getPath() {
        return path;
    }

    /**
     * 设置前端路由路径
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param path 前端路由路径
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取API匹配模式
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @return API匹配模式
     */
    public String getApiPattern() {
        return apiPattern;
    }

    /**
     * 设置API匹配模式
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param apiPattern API匹配模式
     */
    public void setApiPattern(String apiPattern) {
        this.apiPattern = apiPattern;
    }

    /**
     * 获取菜单图标
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @return 菜单图标
     */
    public String getIcon() {
        return icon;
    }

    /**
     * 设置菜单图标
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param icon 菜单图标
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * 获取排序值
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @return 排序值
     */
    public int getSortOrder() {
        return sortOrder;
    }

    /**
     * 设置排序值
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param sortOrder 排序值
     */
    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * 获取菜单状态
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @return 菜单状态（1=启用，0=禁用）
     */
    public int getStatus() {
        return status;
    }

    /**
     * 设置菜单状态
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param status 菜单状态
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
        return "SysMenu{" +
                "id=" + id +
                ", menuName='" + menuName + '\'' +
                ", menuCode='" + menuCode + '\'' +
                ", path='" + path + '\'' +
                ", apiPattern='" + apiPattern + '\'' +
                ", icon='" + icon + '\'' +
                ", sortOrder=" + sortOrder +
                ", status=" + status +
                '}';
    }
}
