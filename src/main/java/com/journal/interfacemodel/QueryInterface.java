package com.journal.interfacemodel;

import com.journal.model.Manuscript;
import java.util.List;
import java.util.Map;

/**
 * 统计查询接口
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.interfacemodel
 *
 * 功能详述：
 *   定义系统统计查询的业务契约，包括按期刊统计稿件数、
 *   月度投稿趋势统计以及获取最近投稿的稿件。
 *
 * 实现类：
 *   impl/QueryImpl.java
 */
public interface QueryInterface {

    /**
     * 按期刊统计稿件数
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param journalName 期刊名称
     * @return 该期刊的稿件数量
     */
    int countByJournal(String journalName);

    /**
     * 月度投稿趋势统计
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @return 月度投稿趋势统计结果列表，每条记录包含月份和对应投稿数量
     */
    List<Map<String, Object>> countMonthlyTrend();

    /**
     * 获取最近投稿的稿件
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param limit 返回的稿件数量上限
     * @return 最近投稿的稿件列表
     */
    List<Manuscript> findRecentPapers(int limit);
}
