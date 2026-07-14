package com.journal.impl;

import com.journal.interfacemodel.ReviewInterface;
import com.journal.dao.ReviewDAO;
import com.journal.model.ReviewRecord;
import com.journal.exception.BusinessException;
import java.util.List;

/**
 * 审稿数据操作实现类
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.impl
 *
 * 功能详述：
 *   实现审稿记录数据的查询与插入业务逻辑，支持按稿件ID、按审稿人ID
 *   维度检索审稿记录，以及新增审稿记录和查询全部审稿记录。
 *   所有方法委托给 ReviewDAO 完成实际数据访问。
 *
 * 实现接口：
 *   com.journal.interfacemodel.ReviewInterface
 *
 * 依赖：
 *   com.journal.dao.ReviewDAO
 */
public class ReviewImpl implements ReviewInterface {

    /** 审稿记录数据访问对象 */
    private final ReviewDAO reviewDAO = new ReviewDAO();

    /**
     * 根据ID查询审稿记录
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param id 审稿记录ID
     * @return 对应的审稿记录对象，未找到时返回null
     */
    @Override
    public ReviewRecord findById(int id) {
        return reviewDAO.findById(id);
    }

    /**
     * 查询稿件的审稿记录
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param paperId 稿件ID
     * @return 该稿件的所有审稿记录列表
     */
    @Override
    public List<ReviewRecord> findByPaperId(int paperId) {
        return reviewDAO.findByPaperId(paperId);
    }

    /**
     * 查询审稿人的审稿记录
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param reviewerId 审稿人ID
     * @return 该审稿人的所有审稿记录列表
     */
    @Override
    public List<ReviewRecord> findByReviewerId(int reviewerId) {
        return reviewDAO.findByReviewerId(reviewerId);
    }

    /**
     * 插入审稿记录，返回自增ID
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param record 审稿记录对象
     * @return 插入后生成的自增ID
     */
    @Override
    public int insert(ReviewRecord record) {
        return reviewDAO.insert(record);
    }

    /**
     * 查询所有审稿记录
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @return 全部审稿记录列表
     */
    @Override
    public List<ReviewRecord> findAll() {
        return reviewDAO.findAll();
    }
}
