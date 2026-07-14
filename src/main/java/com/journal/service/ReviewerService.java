package com.journal.service;

import com.journal.impl.ReviewImpl;
import com.journal.impl.ReviewerImpl;
import com.journal.model.ReviewRecord;
import com.journal.model.Reviewer;

import java.util.ArrayList;
import java.util.List;

/**
 * 审稿人业务服务类，封装审稿人查询及关联审稿记录数据填充的完整业务流程。
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.service
 *
 * 功能详述：
 *   协调 ReviewerImpl 和 ReviewImpl，提供审稿人的按ID查询、
 *   全量查询及关键词搜索等业务操作。在查询审稿人信息时，
 *   自动通过 ReviewImpl 查询该审稿人的审稿记录，
 *   并将审稿记录ID列表填充到审稿人的 reviewIds 字段中，
 *   便于 API 返回时聚合展示。
 *
 * 依赖：
 *   com.journal.impl.ReviewerImpl
 *   com.journal.impl.ReviewImpl
 */
public class ReviewerService {

    /** 审稿人数据操作实现 */
    private final ReviewerImpl reviewerImpl = new ReviewerImpl();

    /** 审稿数据操作实现 */
    private final ReviewImpl reviewImpl = new ReviewImpl();

    /**
     * 根据审稿人ID查询审稿人信息，并填充其审稿记录ID列表。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param id 审稿人ID
     * @return 包含审稿记录ID列表的审稿人对象，未找到时返回null
     */
    public Reviewer getById(int id) {
        Reviewer reviewer = reviewerImpl.findById(id);
        if (reviewer != null) {
            fillReviewIds(reviewer, id);
        }
        return reviewer;
    }

    /**
     * 查询所有审稿人，并为每个审稿人填充其审稿记录ID列表。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @return 包含审稿记录ID列表的全部审稿人列表
     */
    public List<Reviewer> getAll() {
        List<Reviewer> reviewers = reviewerImpl.findAll();
        if (reviewers != null) {
            for (Reviewer reviewer : reviewers) {
                fillReviewIds(reviewer, reviewer.getId());
            }
        }
        return reviewers;
    }

    /**
     * 按关键词搜索审稿人（匹配姓名/邮箱/机构/专业领域）。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param keyword 搜索关键词
     * @return 符合条件的审稿人列表
     */
    public List<Reviewer> search(String keyword) {
        return reviewerImpl.findByKeyword(keyword);
    }

    /**
     * 为指定审稿人填充审稿记录ID列表。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param reviewer   审稿人对象
     * @param reviewerId 审稿人ID
     */
    private void fillReviewIds(Reviewer reviewer, int reviewerId) {
        List<ReviewRecord> records = reviewImpl.findByReviewerId(reviewerId);
        List<String> reviewIds = new ArrayList<String>();
        if (records != null) {
            for (ReviewRecord r : records) {
                reviewIds.add(String.valueOf(r.getId()));
            }
        }
        reviewer.setReviewIds(reviewIds);
    }
}
