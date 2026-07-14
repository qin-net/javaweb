package com.journal.impl;

import com.journal.interfacemodel.SubmissionInterface;
import com.journal.dao.SubmissionDAO;
import com.journal.model.Manuscript;
import com.journal.model.Reference;
import com.journal.exception.BusinessException;
import java.util.List;

/**
 * 投稿操作实现类
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.impl
 *
 * 功能详述：
 *   实现投稿业务逻辑，包括新建投稿（含参考文献）和更新投稿信息。
 *   submitManuscript 方法在委托 DAO 前对 title、content、authorId 进行非空校验；
 *   updateManuscript 方法在委托 DAO 前对稿件 ID 进行有效性校验。
 *   所有数据访问操作委托给 SubmissionDAO 完成。
 *
 * 实现接口：
 *   com.journal.interfacemodel.SubmissionInterface
 *
 * 依赖：
 *   com.journal.dao.SubmissionDAO
 */
public class SubmissionImpl implements SubmissionInterface {

    /** 投稿数据访问对象 */
    private final SubmissionDAO submissionDAO = new SubmissionDAO();

    /**
     * 投稿（含参考文献），返回稿件ID
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param manuscript 稿件对象
     * @param references 参考文献列表
     * @return 投稿成功后生成的稿件ID
     */
    @Override
    public int submitManuscript(Manuscript manuscript, List<Reference> references) {
        if (manuscript == null) {
            throw new BusinessException("投稿失败：稿件对象不能为空");
        }
        if (manuscript.getTitle() == null || manuscript.getTitle().trim().isEmpty()) {
            throw new BusinessException("投稿失败：稿件标题不能为空");
        }
        if (manuscript.getContent() == null || manuscript.getContent().trim().isEmpty()) {
            throw new BusinessException("投稿失败：稿件正文内容不能为空");
        }
        if (manuscript.getAuthorId() <= 0) {
            throw new BusinessException("投稿失败：作者ID不能为空");
        }
        return submissionDAO.submitManuscript(manuscript, references);
    }

    /**
     * 更新投稿
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param manuscript 稿件对象
     * @param references 参考文献列表
     * @return 更新成功返回true，否则返回false
     */
    @Override
    public boolean updateManuscript(Manuscript manuscript, List<Reference> references) {
        if (manuscript == null) {
            throw new BusinessException("更新投稿失败：稿件对象不能为空");
        }
        if (manuscript.getId() <= 0) {
            throw new BusinessException("更新投稿失败：稿件ID必须大于0");
        }
        try {
            submissionDAO.updateManuscript(manuscript, references);
            return true;
        } catch (BusinessException e) {
            return false;
        }
    }
}
