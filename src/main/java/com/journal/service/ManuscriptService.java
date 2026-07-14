package com.journal.service;

import com.journal.dao.ReferenceDAO;
import com.journal.exception.BusinessException;
import com.journal.impl.ManuscriptImpl;
import com.journal.impl.ReviewerImpl;
import com.journal.impl.SubmissionImpl;
import com.journal.model.Manuscript;
import com.journal.model.ManuscriptStatus;
import com.journal.model.Reference;
import com.journal.model.Reviewer;
import com.journal.util.DateUtil;

import java.util.List;

/**
 * 稿件业务服务类，封装稿件相关的完整业务流程。
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.service
 *
 * 功能详述：
 *   协调 ManuscriptImpl、SubmissionImpl、ReviewerImpl 和 ReferenceDAO，
 *   提供稿件的查询（含参考文献关联数据加载）、投稿、更新、状态变更、
 *   审稿人指派等业务操作。在状态变更时进行合法性校验并自动填充
 *   录用日期、发表日期等流程节点信息。
 *
 * 依赖：
 *   com.journal.impl.ManuscriptImpl
 *   com.journal.impl.SubmissionImpl
 *   com.journal.impl.ReviewerImpl
 *   com.journal.dao.ReferenceDAO
 */
public class ManuscriptService {

    /** 稿件数据操作实现 */
    private final ManuscriptImpl manuscriptImpl = new ManuscriptImpl();

    /** 投稿操作实现 */
    private final SubmissionImpl submissionImpl = new SubmissionImpl();

    /** 审稿人数据操作实现 */
    private final ReviewerImpl reviewerImpl = new ReviewerImpl();

    /** 参考文献数据访问对象 */
    private final ReferenceDAO referenceDAO = new ReferenceDAO();

    /**
     * 根据稿件ID查询稿件详情，并加载关联的参考文献列表。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param id 稿件ID
     * @return 包含参考文献的稿件对象，未找到时返回null
     */
    public Manuscript getById(int id) {
        Manuscript manuscript = manuscriptImpl.findById(id);
        if (manuscript != null) {
            List<Reference> references = referenceDAO.findByManuscriptId(id);
            manuscript.setReferences(references);
        }
        return manuscript;
    }

    /**
     * 查询所有稿件。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @return 全部稿件列表
     */
    public List<Manuscript> getAll() {
        return manuscriptImpl.findAll();
    }

    /**
     * 根据关键词、期刊名称、状态、作者ID等条件搜索稿件。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param keyword     关键词（匹配标题/摘要/作者姓名）
     * @param journalName 期刊名称
     * @param status      稿件状态
     * @param authorId    作者ID
     * @return 符合条件的稿件列表
     */
    public List<Manuscript> search(String keyword, String journalName, String status, String authorId) {
        return manuscriptImpl.findByCondition(keyword, journalName, status, authorId);
    }

    /**
     * 投稿：提交稿件及其参考文献列表。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param m   稿件对象
     * @param refs 参考文献列表
     * @return 投稿成功后生成的稿件ID
     */
    public int submit(Manuscript m, List<Reference> refs) {
        return submissionImpl.submitManuscript(m, refs);
    }

    /**
     * 更新稿件信息及其参考文献列表。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param m   稿件对象
     * @param refs 参考文献列表
     * @return 更新成功返回true，否则返回false
     */
    public boolean update(Manuscript m, List<Reference> refs) {
        return submissionImpl.updateManuscript(m, refs);
    }

    /**
     * 更新稿件状态。先校验状态合法性，再更新状态。
     * 若状态为 accepted，同时设置录用日期；
     * 若状态为 published，同时设置发表日期。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param id     稿件ID
     * @param status 新状态
     * @return 更新成功返回true，否则返回false
     */
    public boolean updateStatus(int id, String status) {
        if (!ManuscriptStatus.isValid(status)) {
            throw new BusinessException("非法的稿件状态：" + status);
        }
        boolean result = manuscriptImpl.updateStatus(id, status);
        if (result) {
            Manuscript m = manuscriptImpl.findById(id);
            if (m != null) {
                if (ManuscriptStatus.ACCEPTED.equals(status)) {
                    m.setAcceptanceDate(DateUtil.today());
                    manuscriptImpl.update(m);
                } else if (ManuscriptStatus.PUBLISHED.equals(status)) {
                    m.setPublicationDate(DateUtil.today());
                    manuscriptImpl.update(m);
                }
            }
        }
        return result;
    }

    /**
     * 为稿件指派审稿人。先查询审稿人获取姓名，再执行指派操作。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param paperId    稿件ID
     * @param reviewerId 审稿人ID
     * @return 指派成功返回true，否则返回false
     */
    public boolean assignReviewer(int paperId, int reviewerId) {
        Reviewer reviewer = reviewerImpl.findById(reviewerId);
        if (reviewer == null) {
            throw new BusinessException("审稿人不存在：reviewerId=" + reviewerId);
        }
        return manuscriptImpl.assignReviewer(paperId, reviewerId, reviewer.getName());
    }
}
