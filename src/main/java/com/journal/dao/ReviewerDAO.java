package com.journal.dao;

import com.journal.exception.BusinessException;
import com.journal.model.Reviewer;
import com.journal.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 审稿人数据访问对象，负责对 reviewer 表进行查询操作。
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.dao
 *
 * 功能详述：
 *   提供审稿人的按ID查询、查询全部以及按关键词模糊查询等数据访问方法。
 *   关键词查询支持对 name、email、institution、specialty 四个字段进行 LIKE 模糊匹配。
 *   所有 SQL 均使用 PreparedStatement 防止 SQL 注入，
 *   资源关闭统一在 finally 块中调用 DBUtil.close 完成。
 *
 * 依赖：
 *   com.journal.util.DBUtil
 *   com.journal.model.Reviewer
 */
public class ReviewerDAO {

    /**
     * 根据审稿人ID查询单条审稿人记录。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param id 审稿人ID
     * @return 审稿人对象，若未找到返回 null
     */
    public Reviewer findById(int id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Reviewer reviewer = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT id, name, email, institution, department, specialty, phone " +
                    "FROM reviewer WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                reviewer = mapRow(rs);
            }
        } catch (SQLException e) {
            throw new BusinessException("根据ID查询审稿人失败：id=" + id, e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return reviewer;
    }

    /**
     * 查询全部审稿人记录。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @return 审稿人对象列表
     */
    public List<Reviewer> findAll() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Reviewer> list = new ArrayList<Reviewer>();
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT id, name, email, institution, department, specialty, phone FROM reviewer";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new BusinessException("查询全部审稿人失败", e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    /**
     * 根据关键词模糊查询审稿人记录。匹配字段为 name、email、institution、specialty。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param keyword 搜索关键词
     * @return 符合条件的审稿人对象列表
     */
    public List<Reviewer> findByKeyword(String keyword) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Reviewer> list = new ArrayList<Reviewer>();
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT id, name, email, institution, department, specialty, phone " +
                    "FROM reviewer WHERE name LIKE ? OR email LIKE ? OR institution LIKE ? OR specialty LIKE ?";
            pstmt = conn.prepareStatement(sql);
            String likePattern = "%" + keyword + "%";
            pstmt.setString(1, likePattern);
            pstmt.setString(2, likePattern);
            pstmt.setString(3, likePattern);
            pstmt.setString(4, likePattern);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new BusinessException("按关键词查询审稿人失败：keyword=" + keyword, e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    /**
     * 将 ResultSet 的当前行映射为 Reviewer 对象。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param rs ResultSet 结果集
     * @return 映射后的 Reviewer 对象
     * @throws SQLException 如果读取结果集时发生错误
     */
    private Reviewer mapRow(ResultSet rs) throws SQLException {
        Reviewer reviewer = new Reviewer();
        reviewer.setId(rs.getInt("id"));
        reviewer.setName(rs.getString("name"));
        reviewer.setEmail(rs.getString("email"));
        reviewer.setInstitution(rs.getString("institution"));
        reviewer.setDepartment(rs.getString("department"));
        reviewer.setSpecialty(rs.getString("specialty"));
        reviewer.setPhone(rs.getString("phone"));
        return reviewer;
    }
}
