package com.journal.dao;

import com.journal.exception.BusinessException;
import com.journal.model.Reference;
import com.journal.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 参考文献数据访问对象，负责对 reference_lit 表进行查询操作。
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.dao
 *
 * 功能详述：
 *   提供按稿件ID查询参考文献列表的数据访问方法，
 *   用于在 Service 层加载稿件的关联参考文献数据。
 *   所有 SQL 均使用 PreparedStatement 防止 SQL 注入，
 *   资源关闭统一在 finally 块中调用 DBUtil.close 完成。
 *
 * 依赖：
 *   com.journal.util.DBUtil
 *   com.journal.model.Reference
 */
public class ReferenceDAO {

    /**
     * 根据稿件ID查询该稿件的所有参考文献记录。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param manuscriptId 稿件ID
     * @return 该稿件关联的参考文献列表，无记录时返回空列表
     */
    public List<Reference> findByManuscriptId(int manuscriptId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Reference> list = new ArrayList<Reference>();
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT id, manuscript_id, ref_key, title, authors, " +
                    "journal, year, volume, issue, pages, doi " +
                    "FROM reference_lit WHERE manuscript_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, manuscriptId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new BusinessException("根据稿件ID查询参考文献失败：manuscriptId=" + manuscriptId, e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    /**
     * 将 ResultSet 的当前行映射为 Reference 对象。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param rs ResultSet 结果集
     * @return 映射后的 Reference 对象
     * @throws SQLException 如果读取结果集时发生错误
     */
    private Reference mapRow(ResultSet rs) throws SQLException {
        Reference ref = new Reference();
        ref.setId(rs.getInt("id"));
        ref.setManuscriptId(rs.getInt("manuscript_id"));
        ref.setRefKey(rs.getString("ref_key"));
        ref.setTitle(rs.getString("title"));
        ref.setAuthors(rs.getString("authors"));
        ref.setJournal(rs.getString("journal"));
        ref.setYear(rs.getInt("year"));
        ref.setVolume(rs.getString("volume"));
        ref.setIssue(rs.getString("issue"));
        ref.setPages(rs.getString("pages"));
        ref.setDoi(rs.getString("doi"));
        return ref;
    }
}
