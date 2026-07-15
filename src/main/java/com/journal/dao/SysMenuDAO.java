package com.journal.dao;

import com.journal.exception.BusinessException;
import com.journal.model.SysMenu;
import com.journal.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统菜单数据访问对象，负责对 sys_menu 表进行查询操作。
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-15
 * 所属包：com.journal.dao
 *
 * 功能详述：
 *   提供按角色ID查询菜单列表的数据访问方法。
 *   查询时通过 sys_role_menu 关联表 JOIN sys_menu 表，
 *   获取该角色所拥有的全部菜单，并按 sort_order 排序。
 *   所有 SQL 均使用 PreparedStatement 防止 SQL 注入，
 *   资源关闭统一在 finally 块中调用 DBUtil.close 完成。
 *
 * 依赖：
 *   com.journal.util.DBUtil
 *   com.journal.model.SysMenu
 */
public class SysMenuDAO {

    /**
     * 根据角色ID查询该角色的菜单列表。
     * 通过 sys_role_menu 关联表 JOIN sys_menu 表查询，
     * 结果按 sort_order 升序排列，仅返回状态为启用的菜单。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param roleId 角色ID
     * @return 该角色的菜单列表，按 sort_order 排序
     */
    public List<SysMenu> findMenusByRoleId(int roleId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<SysMenu> list = new ArrayList<SysMenu>();
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT m.id, m.menu_name, m.menu_code, m.path, m.api_pattern, " +
                    "m.icon, m.sort_order, m.status " +
                    "FROM sys_menu m " +
                    "INNER JOIN sys_role_menu rm ON m.id = rm.menu_id " +
                    "WHERE rm.role_id = ? AND m.status = 1 " +
                    "ORDER BY m.sort_order ASC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, roleId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new BusinessException("根据角色ID查询菜单列表失败：roleId=" + roleId, e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    /**
     * 将 ResultSet 的当前行映射为 SysMenu 对象。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param rs ResultSet 结果集
     * @return 映射后的 SysMenu 对象
     * @throws SQLException 如果读取结果集时发生错误
     */
    private SysMenu mapRow(ResultSet rs) throws SQLException {
        SysMenu menu = new SysMenu();
        menu.setId(rs.getInt("id"));
        menu.setMenuName(rs.getString("menu_name"));
        menu.setMenuCode(rs.getString("menu_code"));
        menu.setPath(rs.getString("path"));
        menu.setApiPattern(rs.getString("api_pattern"));
        menu.setIcon(rs.getString("icon"));
        menu.setSortOrder(rs.getInt("sort_order"));
        menu.setStatus(rs.getInt("status"));
        return menu;
    }
}
