package com.journal.dao;

import com.google.gson.Gson;
import com.journal.exception.BusinessException;
import com.journal.model.Manuscript;
import com.journal.model.Reference;
import com.journal.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * 投稿数据访问对象，负责处理稿件与参考文献的级联事务操作。
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.dao
 *
 * 功能详述：
 *   提供投稿（提交稿件及参考文献）和更新稿件（更新稿件信息及参考文献）
 *   两个事务性操作方法。投稿时先 INSERT manuscript 获取自增ID，
 *   再批量 INSERT reference_lit；更新时先 UPDATE manuscript，
 *   再 DELETE 旧参考文献，最后批量 INSERT 新参考文献。
 *   所有操作在同一个事务中完成，保证数据一致性，
 *   异常时回滚事务。
 *
 * 依赖：
 *   com.journal.util.DBUtil
 *   com.journal.model.Manuscript
 *   com.journal.model.Reference
 *   com.google.gson.Gson
 */
public class SubmissionDAO {

    /** Gson 实例，用于 keywords 字段的 JSON 序列化 */
    private static final Gson GSON = new Gson();

    /**
     * 提交稿件（事务操作）：先插入稿件记录获取自增ID，
     * 再使用批量操作插入参考文献列表。
     * 整个操作在同一个事务中完成，任何步骤失败则回滚。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param m   稿件对象
     * @param refs 参考文献列表
     * @return 新插入稿件的自增ID
     */
    public int submitManuscript(Manuscript m, List<Reference> refs) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int manuscriptId = -1;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // Step 1: INSERT manuscript 并获取自增ID
            String insertManuscriptSql = "INSERT INTO manuscript (title, abstract_text, keywords, content, " +
                    "journal_name, author_id, author_name, status, submission_date, review_date, " +
                    "acceptance_date, publication_date, assigned_reviewer_id, assigned_reviewer_name) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(insertManuscriptSql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, m.getTitle());
            pstmt.setString(2, m.getAbstractText());
            pstmt.setString(3, GSON.toJson(m.getKeywords()));
            pstmt.setString(4, m.getContent());
            pstmt.setString(5, m.getJournalName());
            pstmt.setInt(6, m.getAuthorId());
            pstmt.setString(7, m.getAuthorName());
            pstmt.setString(8, m.getStatus());
            pstmt.setDate(9, m.getSubmissionDate() != null ? java.sql.Date.valueOf(m.getSubmissionDate()) : null);
            pstmt.setDate(10, m.getReviewDate() != null ? java.sql.Date.valueOf(m.getReviewDate()) : null);
            pstmt.setDate(11, m.getAcceptanceDate() != null ? java.sql.Date.valueOf(m.getAcceptanceDate()) : null);
            pstmt.setDate(12, m.getPublicationDate() != null ? java.sql.Date.valueOf(m.getPublicationDate()) : null);
            if (m.getAssignedReviewerId() != null) {
                pstmt.setInt(13, m.getAssignedReviewerId());
            } else {
                pstmt.setNull(13, java.sql.Types.INTEGER);
            }
            pstmt.setString(14, m.getAssignedReviewerName());
            pstmt.executeUpdate();

            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                manuscriptId = rs.getInt(1);
            }
            m.setId(manuscriptId);

            // 关闭当前 pstmt 以便复用变量
            pstmt.close();

            // Step 2: 批量 INSERT reference_lit
            if (refs != null && !refs.isEmpty()) {
                String insertRefSql = "INSERT INTO reference_lit (manuscript_id, ref_key, title, authors, " +
                        "journal, year, volume, issue, pages, doi) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                pstmt = conn.prepareStatement(insertRefSql);
                for (Reference ref : refs) {
                    pstmt.setInt(1, manuscriptId);
                    pstmt.setString(2, ref.getRefKey());
                    pstmt.setString(3, ref.getTitle());
                    pstmt.setString(4, ref.getAuthors());
                    pstmt.setString(5, ref.getJournal());
                    pstmt.setInt(6, ref.getYear());
                    pstmt.setString(7, ref.getVolume());
                    pstmt.setString(8, ref.getIssue());
                    pstmt.setString(9, ref.getPages());
                    pstmt.setString(10, ref.getDoi());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    // 忽略回滚异常
                }
            }
            throw new BusinessException("提交稿件事务失败：" + m.getTitle(), e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return manuscriptId;
    }

    /**
     * 更新稿件及参考文献（事务操作）：先更新稿件记录，
     * 再删除旧的参考文献，最后批量插入新的参考文献。
     * 整个操作在同一个事务中完成，任何步骤失败则回滚。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param m   稿件对象
     * @param refs 新的参考文献列表
     */
    public void updateManuscript(Manuscript m, List<Reference> refs) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // Step 1: UPDATE manuscript
            String updateManuscriptSql = "UPDATE manuscript SET title = ?, abstract_text = ?, keywords = ?, " +
                    "content = ?, journal_name = ?, author_id = ?, author_name = ?, status = ?, " +
                    "submission_date = ?, review_date = ?, acceptance_date = ?, publication_date = ?, " +
                    "assigned_reviewer_id = ?, assigned_reviewer_name = ? WHERE id = ?";
            pstmt = conn.prepareStatement(updateManuscriptSql);
            pstmt.setString(1, m.getTitle());
            pstmt.setString(2, m.getAbstractText());
            pstmt.setString(3, GSON.toJson(m.getKeywords()));
            pstmt.setString(4, m.getContent());
            pstmt.setString(5, m.getJournalName());
            pstmt.setInt(6, m.getAuthorId());
            pstmt.setString(7, m.getAuthorName());
            pstmt.setString(8, m.getStatus());
            pstmt.setDate(9, m.getSubmissionDate() != null ? java.sql.Date.valueOf(m.getSubmissionDate()) : null);
            pstmt.setDate(10, m.getReviewDate() != null ? java.sql.Date.valueOf(m.getReviewDate()) : null);
            pstmt.setDate(11, m.getAcceptanceDate() != null ? java.sql.Date.valueOf(m.getAcceptanceDate()) : null);
            pstmt.setDate(12, m.getPublicationDate() != null ? java.sql.Date.valueOf(m.getPublicationDate()) : null);
            if (m.getAssignedReviewerId() != null) {
                pstmt.setInt(13, m.getAssignedReviewerId());
            } else {
                pstmt.setNull(13, java.sql.Types.INTEGER);
            }
            pstmt.setString(14, m.getAssignedReviewerName());
            pstmt.setInt(15, m.getId());
            pstmt.executeUpdate();

            // 关闭当前 pstmt 以便复用变量
            pstmt.close();

            // Step 2: DELETE 旧参考文献
            String deleteRefSql = "DELETE FROM reference_lit WHERE manuscript_id = ?";
            pstmt = conn.prepareStatement(deleteRefSql);
            pstmt.setInt(1, m.getId());
            pstmt.executeUpdate();

            // 关闭当前 pstmt 以便复用变量
            pstmt.close();

            // Step 3: 批量 INSERT 新参考文献
            if (refs != null && !refs.isEmpty()) {
                String insertRefSql = "INSERT INTO reference_lit (manuscript_id, ref_key, title, authors, " +
                        "journal, year, volume, issue, pages, doi) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                pstmt = conn.prepareStatement(insertRefSql);
                for (Reference ref : refs) {
                    pstmt.setInt(1, m.getId());
                    pstmt.setString(2, ref.getRefKey());
                    pstmt.setString(3, ref.getTitle());
                    pstmt.setString(4, ref.getAuthors());
                    pstmt.setString(5, ref.getJournal());
                    pstmt.setInt(6, ref.getYear());
                    pstmt.setString(7, ref.getVolume());
                    pstmt.setString(8, ref.getIssue());
                    pstmt.setString(9, ref.getPages());
                    pstmt.setString(10, ref.getDoi());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    // 忽略回滚异常
                }
            }
            throw new BusinessException("更新稿件事务失败：id=" + m.getId(), e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }
}
