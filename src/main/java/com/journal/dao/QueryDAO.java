package com.journal.dao;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.journal.exception.BusinessException;
import com.journal.model.Manuscript;
import com.journal.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计查询数据访问对象，负责对 manuscript 表进行聚合统计查询。
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.dao
 *
 * 功能详述：
 *   提供按期刊统计稿件数量、按月份统计投稿与录用趋势、
 *   查询最近投稿稿件等统计与查询方法。
 *   月度趋势查询使用 SQL DATE_FORMAT 分组统计，
 *   返回 List<Map<String, Object>> 结构，便于上层灵活消费。
 *   所有 SQL 均使用 PreparedStatement 防止 SQL 注入，
 *   资源关闭统一在 finally 块中调用 DBUtil.close 完成。
 *
 * 依赖：
 *   com.journal.util.DBUtil
 *   com.journal.model.Manuscript
 *   com.google.gson.Gson
 */
public class QueryDAO {

    /** Gson 实例，用于 keywords 字段的 JSON 反序列化 */
    private static final Gson GSON = new Gson();

    /**
     * 根据期刊名称统计稿件数量。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param journalName 期刊名称
     * @return 该期刊下的稿件数量
     */
    public int countByJournal(String journalName) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int count = 0;
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM manuscript WHERE journal_name = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, journalName);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new BusinessException("按期刊统计稿件数量失败：journalName=" + journalName, e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return count;
    }

    /**
     * 统计最近12个月的投稿与录用趋势。
     * 使用 SQL DATE_FORMAT 按月分组，统计每月投稿数量及录用（accepted 或 published）数量。
     * 结果按月份降序排列，最多返回12条记录。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @return 月度趋势列表，每个 Map 包含 "month"(String)、"submissions"(int)、"accepted"(int)
     */
    public List<Map<String, Object>> countMonthlyTrend() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT DATE_FORMAT(submission_date, '%Y-%m') AS month, " +
                    "COUNT(*) AS submissions, " +
                    "SUM(CASE WHEN status='accepted' OR status='published' THEN 1 ELSE 0 END) AS accepted " +
                    "FROM manuscript GROUP BY month ORDER BY month DESC LIMIT 12";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<String, Object>();
                row.put("month", rs.getString("month"));
                row.put("submissions", rs.getInt("submissions"));
                row.put("accepted", rs.getInt("accepted"));
                list.add(row);
            }
        } catch (SQLException e) {
            throw new BusinessException("统计月度投稿趋势失败", e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    /**
     * 查询最近投稿的稿件列表。
     * 按投稿日期降序排列，限制返回条数。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param limit 返回的最大记录数
     * @return 最近的稿件对象列表
     */
    public List<Manuscript> findRecentPapers(int limit) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Manuscript> list = new ArrayList<Manuscript>();
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT id, title, abstract_text, keywords, content, journal_name, " +
                    "author_id, author_name, status, submission_date, review_date, " +
                    "acceptance_date, publication_date, assigned_reviewer_id, assigned_reviewer_name " +
                    "FROM manuscript ORDER BY submission_date DESC LIMIT ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, limit);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new BusinessException("查询最近投稿稿件失败：limit=" + limit, e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    /**
     * 将 ResultSet 的当前行映射为 Manuscript 对象。
     * keywords 字段使用 Gson 从 JSON 字符串解析为 List<String>。
     * 日期字段使用 java.sql.Date 读取后调用 toString() 转换为 yyyy-MM-dd 字符串。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param rs ResultSet 结果集
     * @return 映射后的 Manuscript 对象
     * @throws SQLException 如果读取结果集时发生错误
     */
    private Manuscript mapRow(ResultSet rs) throws SQLException {
        Manuscript m = new Manuscript();
        m.setId(rs.getInt("id"));
        m.setTitle(rs.getString("title"));
        m.setAbstractText(rs.getString("abstract_text"));
        String keywordsJson = rs.getString("keywords");
        if (keywordsJson != null && !keywordsJson.isEmpty()) {
            List<String> keywords = GSON.fromJson(keywordsJson, new TypeToken<List<String>>(){}.getType());
            m.setKeywords(keywords);
        }
        m.setContent(rs.getString("content"));
        m.setJournalName(rs.getString("journal_name"));
        m.setAuthorId(rs.getInt("author_id"));
        m.setAuthorName(rs.getString("author_name"));
        m.setStatus(rs.getString("status"));
        java.sql.Date submissionDate = rs.getDate("submission_date");
        m.setSubmissionDate(submissionDate != null ? submissionDate.toString() : null);
        java.sql.Date reviewDate = rs.getDate("review_date");
        m.setReviewDate(reviewDate != null ? reviewDate.toString() : null);
        java.sql.Date acceptanceDate = rs.getDate("acceptance_date");
        m.setAcceptanceDate(acceptanceDate != null ? acceptanceDate.toString() : null);
        java.sql.Date publicationDate = rs.getDate("publication_date");
        m.setPublicationDate(publicationDate != null ? publicationDate.toString() : null);
        int assignedReviewerId = rs.getInt("assigned_reviewer_id");
        m.setAssignedReviewerId(rs.wasNull() ? null : assignedReviewerId);
        m.setAssignedReviewerName(rs.getString("assigned_reviewer_name"));
        return m;
    }
}
