package com.journal.interfacemodel;

import com.journal.model.Reviewer;
import java.util.List;

/**
 * 审稿人数据操作接口
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.interfacemodel
 *
 * 功能详述：
 *   定义审稿人数据的查询业务契约，支持按ID查询、全量查询
 *   以及按关键词搜索（匹配姓名、邮箱、机构、专业领域）。
 *
 * 实现类：
 *   impl/ReviewerImpl.java
 */
public interface ReviewerInterface {

    /**
     * 根据ID查询审稿人
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param id 审稿人ID
     * @return 对应的审稿人对象，未找到时返回null
     */
    Reviewer findById(int id);

    /**
     * 查询所有审稿人
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @return 全部审稿人列表
     */
    List<Reviewer> findAll();

    /**
     * 按关键词搜索审稿人（匹配姓名/邮箱/机构/专业领域）
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param keyword 搜索关键词
     * @return 符合条件的审稿人列表
     */
    List<Reviewer> findByKeyword(String keyword);
}
