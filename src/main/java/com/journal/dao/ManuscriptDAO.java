package com.journal.dao;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.journal.exception.BusinessException;
import com.journal.model.Manuscript;
import com.journal.model.Reference;
import com.journal.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 稿件数据访问对象，负责对 manuscript 表进行增删改查操作。
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.dao
 *
 * 功能详述：
 *   提供稿件的单条查询、列表查询、动态条件查询、插入、更新、
 *   状态更新、审稿人指派、按状态统计、按作者查询等数据访问方法。
 *   关键词字段以 JSON 数组字符串形式存储在数据库中，
 *   读取时使用 Gson 解析为 List<String>，写入时使用 Gson 序列化。
 *   所有 SQL 均使用 PreparedStatement 防止 SQL 注入，
 *   资源关闭统一在 finally 块中调用 DBUtil.close 完成。
 *
 * 依赖：
 *   com.journal.util.DBUtil
 *   com.journal.model.Manuscript
 *   com.journal.model.Reference
 *   com.google.gson.Gson
 */
public class ManuscriptDAO {

    /** Gson 实例，用于 keywords 字段的 JSON 序列化与反序列化 */
    private static final Gson GSON = new Gson();

    /**
     * 根据稿件ID查询单条稿件记录。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param id 稿件ID
     * @return 稿件对象，若未找到返回 null
     */
    public Manuscript findById(int id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Manuscript manuscript = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT id, title, abstract_text, keywords, content, journal_name, " +
                    "author_id, author_name, status, submission_date, review_date, " +
                    "acceptance_date, publication_date, assigned_reviewer_id, assigned_reviewer_name " +
                    "FROM manuscript WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                manuscript = mapRow(rs);
            }
        } catch (SQLException e) {
            throw new BusinessException("根据ID查询稿件失败：id=" + id, e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return manuscript;
    }

    /**
     * 查询全部稿件记录，按投稿日期降序排列。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @return 稿件对象列表
     */
    public List<Manuscript> findAll() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Manuscript> list = new ArrayList<Manuscript>();
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT id, title, abstract_text, keywords, content, journal_name, " +
                    "author_id, author_name, status, submission_date, review_date, " +
                    "acceptance_date, publication_date, assigned_reviewer_id, assigned_reviewer_name " +
                    "FROM manuscript ORDER BY submission_date DESC";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new BusinessException("查询全部稿件失败", e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    /**
     * 根据动态条件查询稿件列表。keyword 模糊匹配 title、author_name、abstract_text；
     * journalName 精确匹配期刊名称；status 精确匹配状态；authorId 精确匹配作者ID。
     * 各参数为 null 或空字符串时不作为查询条件。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param keyword     关键词（模糊匹配标题、作者姓名、摘要）
     * @param journalName 期刊名称（精确匹配）
     * @param status      稿件状态（精确匹配）
     * @param authorId    作者ID（字符串形式，精确匹配）
     * @return 符合条件的稿件对象列表
     */
    public List<Manuscript> findByCondition(String keyword, String journalName, String status, String authorId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Manuscript> list = new ArrayList<Manuscript>();
        try {
            conn = DBUtil.getConnection();
            StringBuilder sql = new StringBuilder("SELECT id, title, abstract_text, keywords, content, " +
                    "journal_name, author_id, author_name, status, submission_date, review_date, " +
                    "acceptance_date, publication_date, assigned_reviewer_id, assigned_reviewer_name " +
                    "FROM manuscript WHERE 1=1");
            List<Object> params = new ArrayList<Object>();
            if (keyword != null && !keyword.trim().isEmpty()) {
                sql.append(" AND (title LIKE ? OR author_name LIKE ? OR abstract_text LIKE ?)");
                String likePattern = "%" + keyword + "%";
                params.add(likePattern);
                params.add(likePattern);
                params.add(likePattern);
            }
            if (journalName != null && !journalName.trim().isEmpty()) {
                sql.append(" AND journal_name = ?");
                params.add(journalName);
            }
            if (status != null && !status.trim().isEmpty()) {
                sql.append(" AND status = ?");
                params.add(status);
            }
            if (authorId != null && !authorId.trim().isEmpty()) {
                sql.append(" AND author_id = ?");
                params.add(Integer.parseInt(authorId));
            }
            sql.append(" ORDER BY submission_date DESC");
            pstmt = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new BusinessException("动态条件查询稿件失败", e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    /**
     * 插入一条稿件记录，并返回数据库生成的自增主键ID。
     * keywords 字段使用 Gson 转为 JSON 字符串存储。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param m 稿件对象
     * @return 新插入记录的自增ID，插入失败返回 -1
     */
    public int insert(Manuscript m) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int generatedId = -1;
        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO manuscript (title, abstract_text, keywords, content, journal_name, " +
                    "author_id, author_name, status, submission_date, review_date, acceptance_date, " +
                    "publication_date, assigned_reviewer_id, assigned_reviewer_name) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
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
                generatedId = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new BusinessException("插入稿件失败：" + m.getTitle(), e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return generatedId;
    }

    /**
     * 更新稿件的全字段信息。根据稿件ID进行更新。
     * keywords 字段使用 Gson 转为 JSON 字符串存储。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param m 稿件对象
     */
    public void update(Manuscript m) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE manuscript SET title = ?, abstract_text = ?, keywords = ?, content = ?, " +
                    "journal_name = ?, author_id = ?, author_name = ?, status = ?, submission_date = ?, " +
                    "review_date = ?, acceptance_date = ?, publication_date = ?, " +
                    "assigned_reviewer_id = ?, assigned_reviewer_name = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
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
        } catch (SQLException e) {
            throw new BusinessException("更新稿件失败：id=" + m.getId(), e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    /**
     * 根据稿件ID更新稿件状态。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param id     稿件ID
     * @param status 新状态值
     */
    public void updateStatus(int id, String status) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE manuscript SET status = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new BusinessException("更新稿件状态失败：id=" + id + ", status=" + status, e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    /**
     * 为稿件指派审稿人，同时将稿件状态更新为"审稿中"。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param manuscriptId 稿件ID
     * @param reviewerId   审稿人ID
     * @param reviewerName 审稿人姓名
     */
    public void assignReviewer(int manuscriptId, int reviewerId, String reviewerName) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE manuscript SET assigned_reviewer_id = ?, assigned_reviewer_name = ?, " +
                    "status = 'reviewing' WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, reviewerId);
            pstmt.setString(2, reviewerName);
            pstmt.setInt(3, manuscriptId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new BusinessException("指派审稿人失败：manuscriptId=" + manuscriptId +
                    ", reviewerId=" + reviewerId, e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    /**
     * 根据状态统计稿件数量。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param status 稿件状态
     * @return 该状态下的稿件数量
     */
    public int countByStatus(String status) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int count = 0;
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM manuscript WHERE status = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new BusinessException("按状态统计稿件数量失败：status=" + status, e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return count;
    }

    /**
     * 统计全部稿件数量。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @return 稿件总数量
     */
    public int countAll() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int count = 0;
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM manuscript";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new BusinessException("统计全部稿件数量失败", e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return count;
    }

    /**
     * 根据作者ID查询该作者的所有稿件。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param authorId 作者ID
     * @return 该作者的稿件列表
     */
    public List<Manuscript> findByAuthorId(int authorId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Manuscript> list = new ArrayList<Manuscript>();
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT id, title, abstract_text, keywords, content, journal_name, " +
                    "author_id, author_name, status, submission_date, review_date, " +
                    "acceptance_date, publication_date, assigned_reviewer_id, assigned_reviewer_name " +
                    "FROM manuscript WHERE author_id = ? ORDER BY submission_date DESC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, authorId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new BusinessException("按作者ID查询稿件失败：authorId=" + authorId, e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    /**
     * 根据审稿人ID查询指派给该审稿人的所有稿件。
     * 查询条件为 assigned_reviewer_id 等于传入的审稿人ID，
     * 结果按投稿日期降序排列。用于审稿人只看到指派给自己的稿件。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-15
     *
     * @param reviewerId 审稿人ID（对应 reviewer 表的 id）
     * @return 指派给该审稿人的稿件列表
     */
    public List<Manuscript> findByReviewerId(int reviewerId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Manuscript> list = new ArrayList<Manuscript>();
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT id, title, abstract_text, keywords, content, journal_name, " +
                    "author_id, author_name, status, submission_date, review_date, " +
                    "acceptance_date, publication_date, assigned_reviewer_id, assigned_reviewer_name " +
                    "FROM manuscript WHERE assigned_reviewer_id = ? ORDER BY submission_date DESC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, reviewerId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new BusinessException("按审稿人ID查询稿件失败：reviewerId=" + reviewerId, e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    /**
     * 查询所有已指派审稿人的稿件。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @return 已指派审稿人的稿件列表
     */
    public List<Manuscript> findAssigned() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Manuscript> list = new ArrayList<Manuscript>();
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT id, title, abstract_text, keywords, content, journal_name, " +
                    "author_id, author_name, status, submission_date, review_date, " +
                    "acceptance_date, publication_date, assigned_reviewer_id, assigned_reviewer_name " +
                    "FROM manuscript WHERE assigned_reviewer_id IS NOT NULL ORDER BY submission_date DESC";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new BusinessException("查询已指派审稿人的稿件失败", e);
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
