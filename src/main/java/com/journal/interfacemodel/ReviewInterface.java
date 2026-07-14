package com.journal.interfacemodel;

import com.journal.model.ReviewRecord;
import java.util.List;

/**
 * 审稿数据操作接口
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.interfacemodel
 *
 * 功能详述：
 *   定义审稿记录数据的查询与插入业务契约，支持按稿件、按审稿人
 *   维度检索审稿记录，以及新增审稿记录。
 *
 * 实现类：
 *   impl/ReviewImpl.java
 */
public interface ReviewInterface {

    /**
     * 根据ID查询审稿记录
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param id 审稿记录ID
     * @return 对应的审稿记录对象，未找到时返回null
     */
    ReviewRecord findById(int id);

    /**
     * 查询稿件的审稿记录
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param paperId 稿件ID
     * @return 该稿件的所有审稿记录列表
     */
    List<ReviewRecord> findByPaperId(int paperId);

    /**
     * 查询审稿人的审稿记录
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param reviewerId 审稿人ID
     * @return 该审稿人的所有审稿记录列表
     */
    List<ReviewRecord> findByReviewerId(int reviewerId);

    /**
     * 插入审稿记录，返回自增ID
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param record 审稿记录对象
     * @return 插入后生成的自增ID
     */
    int insert(ReviewRecord record);

    /**
     * 查询所有审稿记录
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @return 全部审稿记录列表
     */
    List<ReviewRecord> findAll();
}
