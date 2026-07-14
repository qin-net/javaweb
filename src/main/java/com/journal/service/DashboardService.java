package com.journal.service;

import com.journal.impl.ManuscriptImpl;
import com.journal.impl.QueryImpl;
import com.journal.model.Journal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 仪表盘统计业务服务类，封装系统首页数据看板的统计查询业务流程。
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.service
 *
 * 功能详述：
 *   协调 ManuscriptImpl 和 QueryImpl，聚合系统全局统计数据，
 *   包括投稿总数、审稿中数量、已录用数量、已发表数量、
 *   各期刊稿件数量分布、月度投稿趋势以及最近投稿的稿件列表，
 *   以 Map 形式返回供前端仪表盘渲染。
 *
 * 依赖：
 *   com.journal.impl.ManuscriptImpl
 *   com.journal.impl.QueryImpl
 */
public class DashboardService {

    /** 稿件数据操作实现 */
    private final ManuscriptImpl manuscriptImpl = new ManuscriptImpl();

    /** 统计查询操作实现 */
    private final QueryImpl queryImpl = new QueryImpl();

    /** 四个期刊分版名称数组 */
    private static final String[] JOURNAL_NAMES = {
            Journal.ENGINEERING,
            Journal.SCIENCE,
            Journal.LIBERAL_ARTS,
            Journal.BIOMEDICAL
    };

    /**
     * 获取仪表盘统计数据，包括投稿总数、各状态数量、
     * 各期刊稿件分布、月度投稿趋势及最近投稿稿件。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @return 包含各项统计数据的Map
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<String, Object>();

        stats.put("totalSubmissions", manuscriptImpl.countAll());
        stats.put("underReview", manuscriptImpl.countByStatus("reviewing"));
        stats.put("accepted", manuscriptImpl.countByStatus("accepted"));
        stats.put("published", manuscriptImpl.countByStatus("published"));

        List<Map<String, Object>> byJournal = new ArrayList<Map<String, Object>>();
        for (String journalName : JOURNAL_NAMES) {
            Map<String, Object> journalStat = new HashMap<String, Object>();
            journalStat.put("name", journalName);
            journalStat.put("count", queryImpl.countByJournal(journalName));
            byJournal.add(journalStat);
        }
        stats.put("byJournal", byJournal);

        stats.put("monthlyTrend", queryImpl.countMonthlyTrend());
        stats.put("recentPapers", queryImpl.findRecentPapers(10));

        return stats;
    }
}
