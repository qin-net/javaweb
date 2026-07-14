package com.journal.dao;

import com.journal.exception.BusinessException;
import com.journal.model.ReviewRecord;
import com.journal.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 审稿记录数据访问对象，负责对 review_record 表进行增删改查操作。
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.dao
 *
 * 功能详述：
 *   提供审稿记录的单条查询、按稿件ID查询、按审稿人ID查询、
 *   查询全部以及插入审稿记录等数据访问方法。
 *   所有 SQL 均使用 PreparedStatement 防止 SQL 注入，
 *   资源关闭统一在 finally 块中调用 DBUtil.close 完成。
 *
 * 依赖：
 *   com.journal.util.DBUtil
 *   com.journal.model.ReviewRecord
 */
public class ReviewDAO {

    /**
     * 根据审稿记录ID查询单条审稿记录。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param id 审稿记录ID
     * @return 审稿记录对象，若未找到返回 null
     */
    public ReviewRecord findById(int id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ReviewRecord record = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT id, paper_id, paper_title, reviewer_id, reviewer_name, " +
                    "decision, comments, review_date FROM review_record WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                record = mapRow(rs);
            }
        } catch (SQLException e) {
            throw new BusinessException("根据ID查询审稿记录失败：id=" + id, e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return record;
    }

    /**
     * 根据稿件ID查询该稿件的所有审稿记录。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param paperId 稿件ID
     * @return 该稿件的审稿记录列表
     */
    public List<ReviewRecord> findByPaperId(int paperId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<ReviewRecord> list = new ArrayList<ReviewRecord>();
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT id, paper_id, paper_title, reviewer_id, reviewer_name, " +
                    "decision, comments, review_date FROM review_record WHERE paper_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, paperId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new BusinessException("按稿件ID查询审稿记录失败：paperId=" + paperId, e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    /**
     * 根据审稿人ID查询该审稿人的所有审稿记录。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param reviewerId 审稿人ID
     * @return 该审稿人的审稿记录列表
     */
    public List<ReviewRecord> findByReviewerId(int reviewerId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<ReviewRecord> list = new ArrayList<ReviewRecord>();
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT id, paper_id, paper_title, reviewer_id, reviewer_name, " +
                    "decision, comments, review_date FROM review_record WHERE reviewer_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, reviewerId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new BusinessException("按审稿人ID查询审稿记录失败：reviewerId=" + reviewerId, e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    /**
     * 插入一条审稿记录，并返回数据库生成的自增主键ID。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param r 审稿记录对象
     * @return 新插入记录的自增ID，插入失败返回 -1
     */
    public int insert(ReviewRecord r) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int generatedId = -1;
        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO review_record (paper_id, paper_title, reviewer_id, reviewer_name, " +
                    "decision, comments, review_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, r.getPaperId());
            pstmt.setString(2, r.getPaperTitle());
            pstmt.setInt(3, r.getReviewerId());
            pstmt.setString(4, r.getReviewerName());
            pstmt.setString(5, r.getDecision());
            pstmt.setString(6, r.getComments());
            pstmt.setDate(7, r.getReviewDate() != null ? java.sql.Date.valueOf(r.getReviewDate()) : null);
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                generatedId = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new BusinessException("插入审稿记录失败：paperId=" + r.getPaperId() +
                    ", reviewerId=" + r.getReviewerId(), e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return generatedId;
    }

    /**
     * 查询全部审稿记录。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @return 审稿记录列表
     */
    public List<ReviewRecord> findAll() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<ReviewRecord> list = new ArrayList<ReviewRecord>();
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT id, paper_id, paper_title, reviewer_id, reviewer_name, " +
                    "decision, comments, review_date FROM review_record";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new BusinessException("查询全部审稿记录失败", e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    /**
     * 将 ResultSet 的当前行映射为 ReviewRecord 对象。
     * review_date 字段使用 java.sql.Date 读取后调用 toString() 转换为 yyyy-MM-dd 字符串。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param rs ResultSet 结果集
     * @return 映射后的 ReviewRecord 对象
     * @throws SQLException 如果读取结果集时发生错误
     */
    private ReviewRecord mapRow(ResultSet rs) throws SQLException {
        ReviewRecord record = new ReviewRecord();
        record.setId(rs.getInt("id"));
        record.setPaperId(rs.getInt("paper_id"));
        record.setPaperTitle(rs.getString("paper_title"));
        record.setReviewerId(rs.getInt("reviewer_id"));
        record.setReviewerName(rs.getString("reviewer_name"));
        record.setDecision(rs.getString("decision"));
        record.setComments(rs.getString("comments"));
        java.sql.Date reviewDate = rs.getDate("review_date");
        record.setReviewDate(reviewDate != null ? reviewDate.toString() : null);
        return record;
    }
}
