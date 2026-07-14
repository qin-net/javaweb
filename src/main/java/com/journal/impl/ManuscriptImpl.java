package com.journal.impl;

import com.journal.interfacemodel.ManuscriptInterface;
import com.journal.dao.ManuscriptDAO;
import com.journal.model.Manuscript;
import com.journal.exception.BusinessException;
import java.util.List;

/**
 * 稿件数据操作实现类
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.impl
 *
 * 功能详述：
 *   实现稿件数据的增删改查业务逻辑，包括按ID查询、全量查询、
 *   条件查询、插入、更新、状态更新、审稿人指派以及稿件统计等操作。
 *   所有方法委托给 ManuscriptDAO 完成实际数据访问，
 *   Impl 层仅负责委托调用，不包含 SQL 操作。
 *
 * 实现接口：
 *   com.journal.interfacemodel.ManuscriptInterface
 *
 * 依赖：
 *   com.journal.dao.ManuscriptDAO
 */
public class ManuscriptImpl implements ManuscriptInterface {

    /** 稿件数据访问对象 */
    private final ManuscriptDAO manuscriptDAO = new ManuscriptDAO();

    /**
     * 根据ID查询稿件
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param id 稿件ID
     * @return 对应的稿件对象，未找到时返回null
     */
    @Override
    public Manuscript findById(int id) {
        return manuscriptDAO.findById(id);
    }

    /**
     * 查询所有稿件
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @return 全部稿件列表
     */
    @Override
    public List<Manuscript> findAll() {
        return manuscriptDAO.findAll();
    }

    /**
     * 条件查询稿件
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param keyword 关键词（匹配标题/摘要）
     * @param journalName 期刊名称
     * @param status 稿件状态
     * @param authorId 作者ID
     * @return 符合条件的稿件列表
     */
    @Override
    public List<Manuscript> findByCondition(String keyword, String journalName, String status, String authorId) {
        return manuscriptDAO.findByCondition(keyword, journalName, status, authorId);
    }

    /**
     * 插入稿件，返回自增ID
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param manuscript 稿件对象
     * @return 插入后生成的自增ID
     */
    @Override
    public int insert(Manuscript manuscript) {
        return manuscriptDAO.insert(manuscript);
    }

    /**
     * 更新稿件
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param manuscript 稿件对象
     * @return 更新成功返回true，否则返回false
     */
    @Override
    public boolean update(Manuscript manuscript) {
        try {
            manuscriptDAO.update(manuscript);
            return true;
        } catch (BusinessException e) {
            return false;
        }
    }

    /**
     * 更新稿件状态
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param id 稿件ID
     * @param status 新状态
     * @return 更新成功返回true，否则返回false
     */
    @Override
    public boolean updateStatus(int id, String status) {
        try {
            manuscriptDAO.updateStatus(id, status);
            return true;
        } catch (BusinessException e) {
            return false;
        }
    }

    /**
     * 指派审稿人
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param manuscriptId 稿件ID
     * @param reviewerId 审稿人ID
     * @param reviewerName 审稿人姓名
     * @return 指派成功返回true，否则返回false
     */
    @Override
    public boolean assignReviewer(int manuscriptId, int reviewerId, String reviewerName) {
        try {
            manuscriptDAO.assignReviewer(manuscriptId, reviewerId, reviewerName);
            return true;
        } catch (BusinessException e) {
            return false;
        }
    }

    /**
     * 按状态统计稿件数量
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param status 稿件状态
     * @return 符合该状态的稿件数量
     */
    @Override
    public int countByStatus(String status) {
        return manuscriptDAO.countByStatus(status);
    }

    /**
     * 统计稿件总数
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @return 稿件总数
     */
    @Override
    public int countAll() {
        return manuscriptDAO.countAll();
    }

    /**
     * 查询某作者的稿件
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param authorId 作者ID
     * @return 该作者的所有稿件列表
     */
    @Override
    public List<Manuscript> findByAuthorId(int authorId) {
        return manuscriptDAO.findByAuthorId(authorId);
    }

    /**
     * 查询所有已指派审稿人的稿件
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @return 已指派审稿人的稿件列表
     */
    @Override
    public List<Manuscript> findAssigned() {
        return manuscriptDAO.findAssigned();
    }
}
