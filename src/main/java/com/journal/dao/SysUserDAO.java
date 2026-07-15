package com.journal.dao;

import com.journal.exception.BusinessException;
import com.journal.model.SysUser;
import com.journal.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 系统用户数据访问对象，负责对 sys_user 表进行查询操作。
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-15
 * 所属包：com.journal.dao
 *
 * 功能详述：
 *   提供按用户名查询和按ID查询系统用户的数据访问方法。
 *   查询时通过 LEFT JOIN sys_role 表关联获取角色编码（role_code）、
 *   角色名称（role_name）和数据范围（data_scope），填充到 SysUser
 *   的非持久化字段中，用于后续的权限控制判断。
 *   所有 SQL 均使用 PreparedStatement 防止 SQL 注入，
 *   资源关闭统一在 finally 块中调用 DBUtil.close 完成。
 *
 * 依赖：
 *   com.journal.util.DBUtil
 *   com.journal.model.SysUser
 */
public class SysUserDAO {

    /**
     * 根据用户名查询系统用户，并 JOIN sys_role 获取角色信息。
     * 查询结果包含 role_code、role_name、data_scope 等非持久化字段。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param username 用户名
     * @return 系统用户对象（含角色信息），若未找到返回 null
     */
    public SysUser findByUsername(String username) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        SysUser user = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT u.id, u.username, u.password, u.real_name, u.role_id, " +
                    "u.ref_id, u.email, u.status, u.create_time, " +
                    "r.role_code, r.role_name, r.data_scope " +
                    "FROM sys_user u " +
                    "LEFT JOIN sys_role r ON u.role_id = r.id " +
                    "WHERE u.username = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                user = mapRow(rs);
            }
        } catch (SQLException e) {
            throw new BusinessException("根据用户名查询系统用户失败：username=" + username, e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return user;
    }

    /**
     * 根据用户ID查询系统用户，并 JOIN sys_role 获取角色信息。
     * 查询结果包含 role_code、role_name、data_scope 等非持久化字段。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param id 用户ID
     * @return 系统用户对象（含角色信息），若未找到返回 null
     */
    public SysUser findById(int id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        SysUser user = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT u.id, u.username, u.password, u.real_name, u.role_id, " +
                    "u.ref_id, u.email, u.status, u.create_time, " +
                    "r.role_code, r.role_name, r.data_scope " +
                    "FROM sys_user u " +
                    "LEFT JOIN sys_role r ON u.role_id = r.id " +
                    "WHERE u.id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                user = mapRow(rs);
            }
        } catch (SQLException e) {
            throw new BusinessException("根据ID查询系统用户失败：id=" + id, e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return user;
    }

    /**
     * 将 ResultSet 的当前行映射为 SysUser 对象。
     * 同时从 JOIN 的 sys_role 表中读取 role_code、role_name、data_scope
     * 填充到非持久化字段中。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param rs ResultSet 结果集
     * @return 映射后的 SysUser 对象
     * @throws SQLException 如果读取结果集时发生错误
     */
    private SysUser mapRow(ResultSet rs) throws SQLException {
        SysUser user = new SysUser();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRealName(rs.getString("real_name"));
        user.setRoleId(rs.getInt("role_id"));
        int refId = rs.getInt("ref_id");
        user.setRefId(rs.wasNull() ? null : refId);
        user.setEmail(rs.getString("email"));
        user.setStatus(rs.getInt("status"));
        user.setCreateTime(rs.getString("create_time"));
        user.setRoleCode(rs.getString("role_code"));
        user.setRoleName(rs.getString("role_name"));
        user.setDataScope(rs.getInt("data_scope"));
        return user;
    }
}
