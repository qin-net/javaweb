package com.journal.dao;

import com.journal.exception.BusinessException;
import com.journal.model.Author;
import com.journal.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者数据访问对象，负责对 author 表进行查询操作。
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.dao
 *
 * 功能详述：
 *   提供作者的按ID查询、查询全部以及按关键词模糊查询等数据访问方法。
 *   关键词查询支持对 name、email、institution 三个字段进行 LIKE 模糊匹配。
 *   所有 SQL 均使用 PreparedStatement 防止 SQL 注入，
 *   资源关闭统一在 finally 块中调用 DBUtil.close 完成。
 *
 * 依赖：
 *   com.journal.util.DBUtil
 *   com.journal.model.Author
 */
public class AuthorDAO {

    /**
     * 根据作者ID查询单条作者记录。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param id 作者ID
     * @return 作者对象，若未找到返回 null
     */
    public Author findById(int id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Author author = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT id, name, email, institution, department, phone " +
                    "FROM author WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                author = mapRow(rs);
            }
        } catch (SQLException e) {
            throw new BusinessException("根据ID查询作者失败：id=" + id, e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return author;
    }

    /**
     * 查询全部作者记录。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @return 作者对象列表
     */
    public List<Author> findAll() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Author> list = new ArrayList<Author>();
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT id, name, email, institution, department, phone FROM author";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new BusinessException("查询全部作者失败", e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    /**
     * 根据关键词模糊查询作者记录。匹配字段为 name、email、institution。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param keyword 搜索关键词
     * @return 符合条件的作者对象列表
     */
    public List<Author> findByKeyword(String keyword) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Author> list = new ArrayList<Author>();
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT id, name, email, institution, department, phone " +
                    "FROM author WHERE name LIKE ? OR email LIKE ? OR institution LIKE ?";
            pstmt = conn.prepareStatement(sql);
            String likePattern = "%" + keyword + "%";
            pstmt.setString(1, likePattern);
            pstmt.setString(2, likePattern);
            pstmt.setString(3, likePattern);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new BusinessException("按关键词查询作者失败：keyword=" + keyword, e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    /**
     * 将 ResultSet 的当前行映射为 Author 对象。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param rs ResultSet 结果集
     * @return 映射后的 Author 对象
     * @throws SQLException 如果读取结果集时发生错误
     */
    private Author mapRow(ResultSet rs) throws SQLException {
        Author author = new Author();
        author.setId(rs.getInt("id"));
        author.setName(rs.getString("name"));
        author.setEmail(rs.getString("email"));
        author.setInstitution(rs.getString("institution"));
        author.setDepartment(rs.getString("department"));
        author.setPhone(rs.getString("phone"));
        return author;
    }
}
