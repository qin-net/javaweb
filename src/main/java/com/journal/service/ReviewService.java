package com.journal.service;

import com.journal.impl.ManuscriptImpl;
import com.journal.impl.ReviewImpl;
import com.journal.model.Manuscript;
import com.journal.model.ManuscriptStatus;
import com.journal.model.ReviewRecord;
import com.journal.util.DateUtil;

import java.util.List;

/**
 * 审稿业务服务类，封装审稿记录提交及查询的完整业务流程。
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.service
 *
 * 功能详述：
 *   协调 ReviewImpl 和 ManuscriptImpl，提供审稿记录的提交、
 *   按稿件查询、按审稿人查询及全量查询等业务操作。
 *   在提交审稿记录时，根据审稿决定自动更新稿件状态：
 *   approved 时将稿件状态置为 accepted 并设置审稿日期和录用日期，
 *   rejected 时将稿件状态置为 rejected 并设置审稿日期。
 *
 * 依赖：
 *   com.journal.impl.ReviewImpl
 *   com.journal.impl.ManuscriptImpl
 */
public class ReviewService {

    /** 审稿数据操作实现 */
    private final ReviewImpl reviewImpl = new ReviewImpl();

    /** 稿件数据操作实现 */
    private final ManuscriptImpl manuscriptImpl = new ManuscriptImpl();

    /**
     * 提交审稿记录。插入审稿记录后，根据审稿决定更新稿件状态：
     * approved → 稿件状态变为 accepted，设置审稿日期和录用日期；
     * rejected → 稿件状态变为 rejected，设置审稿日期。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param record 审稿记录对象
     * @return 插入后生成的审稿记录ID
     */
    public int submitReview(ReviewRecord record) {
        int id = reviewImpl.insert(record);
        String decision = record.getDecision();
        int paperId = record.getPaperId();
        String today = DateUtil.today();

        Manuscript manuscript = manuscriptImpl.findById(paperId);
        if (manuscript != null) {
            manuscript.setReviewDate(today);
            if ("approved".equals(decision)) {
                manuscript.setStatus(ManuscriptStatus.ACCEPTED);
                manuscript.setAcceptanceDate(today);
            } else if ("rejected".equals(decision)) {
                manuscript.setStatus(ManuscriptStatus.REJECTED);
            }
            manuscriptImpl.update(manuscript);
        }
        return id;
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
    public List<ReviewRecord> getByPaperId(int paperId) {
        return reviewImpl.findByPaperId(paperId);
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
    public List<ReviewRecord> getByReviewerId(int reviewerId) {
        return reviewImpl.findByReviewerId(reviewerId);
    }

    /**
     * 查询所有审稿记录。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @return 全部审稿记录列表
     */
    public List<ReviewRecord> getAll() {
        return reviewImpl.findAll();
    }
}
