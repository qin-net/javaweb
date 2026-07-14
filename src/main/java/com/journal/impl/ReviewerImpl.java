package com.journal.impl;

import com.journal.interfacemodel.ReviewerInterface;
import com.journal.dao.ReviewerDAO;
import com.journal.model.Reviewer;
import com.journal.exception.BusinessException;
import java.util.List;

/**
 * 审稿人数据操作实现类
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.impl
 *
 * 功能详述：
 *   实现审稿人数据的查询业务逻辑，支持按ID查询、全量查询
 *   以及按关键词搜索（匹配姓名、邮箱、机构、专业领域）。
 *   所有方法委托给 ReviewerDAO 完成实际数据访问。
 *
 * 实现接口：
 *   com.journal.interfacemodel.ReviewerInterface
 *
 * 依赖：
 *   com.journal.dao.ReviewerDAO
 */
public class ReviewerImpl implements ReviewerInterface {

    /** 审稿人数据访问对象 */
    private final ReviewerDAO reviewerDAO = new ReviewerDAO();

    /**
     * 根据ID查询审稿人
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param id 审稿人ID
     * @return 对应的审稿人对象，未找到时返回null
     */
    @Override
    public Reviewer findById(int id) {
        return reviewerDAO.findById(id);
    }

    /**
     * 查询所有审稿人
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @return 全部审稿人列表
     */
    @Override
    public List<Reviewer> findAll() {
        return reviewerDAO.findAll();
    }

    /**
     * 按关键词搜索审稿人（匹配姓名/邮箱/机构/专业领域）
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param keyword 搜索关键词
     * @return 符合条件的审稿人列表
     */
    @Override
    public List<Reviewer> findByKeyword(String keyword) {
        return reviewerDAO.findByKeyword(keyword);
    }
}
