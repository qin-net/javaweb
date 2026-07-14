package com.journal.interfacemodel;

import com.journal.model.Author;
import java.util.List;

/**
 * 作者数据操作接口
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.interfacemodel
 *
 * 功能详述：
 *   定义作者数据的查询业务契约，支持按ID查询、全量查询
 *   以及按关键词搜索（匹配姓名、邮箱、机构）。
 *
 * 实现类：
 *   impl/AuthorImpl.java
 */
public interface AuthorInterface {

    /**
     * 根据ID查询作者
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param id 作者ID
     * @return 对应的作者对象，未找到时返回null
     */
    Author findById(int id);

    /**
     * 查询所有作者
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @return 全部作者列表
     */
    List<Author> findAll();

    /**
     * 按关键词搜索作者（匹配姓名/邮箱/机构）
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param keyword 搜索关键词
     * @return 符合条件的作者列表
     */
    List<Author> findByKeyword(String keyword);
}
