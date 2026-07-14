package com.journal.impl;

import com.journal.interfacemodel.QueryInterface;
import com.journal.dao.QueryDAO;
import com.journal.model.Manuscript;
import com.journal.exception.BusinessException;
import java.util.List;
import java.util.Map;

/**
 * 统计查询实现类
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.impl
 *
 * 功能详述：
 *   实现系统统计查询业务逻辑，包括按期刊统计稿件数、
 *   月度投稿趋势统计以及获取最近投稿的稿件。
 *   所有方法委托给 QueryDAO 完成实际数据访问。
 *
 * 实现接口：
 *   com.journal.interfacemodel.QueryInterface
 *
 * 依赖：
 *   com.journal.dao.QueryDAO
 */
public class QueryImpl implements QueryInterface {

    /** 统计查询数据访问对象 */
    private final QueryDAO queryDAO = new QueryDAO();

    /**
     * 按期刊统计稿件数
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param journalName 期刊名称
     * @return 该期刊的稿件数量
     */
    @Override
    public int countByJournal(String journalName) {
        return queryDAO.countByJournal(journalName);
    }

    /**
     * 月度投稿趋势统计
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @return 月度投稿趋势统计结果列表，每条记录包含月份和对应投稿数量
     */
    @Override
    public List<Map<String, Object>> countMonthlyTrend() {
        return queryDAO.countMonthlyTrend();
    }

    /**
     * 获取最近投稿的稿件
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param limit 返回的稿件数量上限
     * @return 最近投稿的稿件列表
     */
    @Override
    public List<Manuscript> findRecentPapers(int limit) {
        return queryDAO.findRecentPapers(limit);
    }
}
