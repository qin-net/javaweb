package com.journal.interfacemodel;

import com.journal.model.Manuscript;
import java.util.List;

/**
 * 稿件数据操作接口
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.interfacemodel
 *
 * 功能详述：
 *   定义稿件数据的增删改查业务契约，包括按ID查询、条件查询、
 *   状态更新、审稿人指派以及稿件统计等操作。
 *
 * 实现类：
 *   impl/ManuscriptImpl.java
 */
public interface ManuscriptInterface {

    /**
     * 根据ID查询稿件
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param id 稿件ID
     * @return 对应的稿件对象，未找到时返回null
     */
    Manuscript findById(int id);

    /**
     * 查询所有稿件
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @return 全部稿件列表
     */
    List<Manuscript> findAll();

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
    List<Manuscript> findByCondition(String keyword, String journalName, String status, String authorId);

    /**
     * 插入稿件，返回自增ID
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param manuscript 稿件对象
     * @return 插入后生成的自增ID
     */
    int insert(Manuscript manuscript);

    /**
     * 更新稿件
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param manuscript 稿件对象
     * @return 更新成功返回true，否则返回false
     */
    boolean update(Manuscript manuscript);

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
    boolean updateStatus(int id, String status);

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
    boolean assignReviewer(int manuscriptId, int reviewerId, String reviewerName);

    /**
     * 按状态统计稿件数量
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param status 稿件状态
     * @return 符合该状态的稿件数量
     */
    int countByStatus(String status);

    /**
     * 统计稿件总数
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @return 稿件总数
     */
    int countAll();

    /**
     * 查询某作者的稿件
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param authorId 作者ID
     * @return 该作者的所有稿件列表
     */
    List<Manuscript> findByAuthorId(int authorId);

    /**
     * 查询所有已指派审稿人的稿件
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @return 已指派审稿人的稿件列表
     */
    List<Manuscript> findAssigned();
}
