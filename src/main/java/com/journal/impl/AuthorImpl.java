package com.journal.impl;

import com.journal.interfacemodel.AuthorInterface;
import com.journal.dao.AuthorDAO;
import com.journal.model.Author;
import com.journal.exception.BusinessException;
import java.util.List;

/**
 * 作者数据操作实现类
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.impl
 *
 * 功能详述：
 *   实现作者数据的查询业务逻辑，支持按ID查询、全量查询
 *   以及按关键词搜索（匹配姓名、邮箱、机构）。
 *   所有方法委托给 AuthorDAO 完成实际数据访问。
 *
 * 实现接口：
 *   com.journal.interfacemodel.AuthorInterface
 *
 * 依赖：
 *   com.journal.dao.AuthorDAO
 */
public class AuthorImpl implements AuthorInterface {

    /** 作者数据访问对象 */
    private final AuthorDAO authorDAO = new AuthorDAO();

    /**
     * 根据ID查询作者
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param id 作者ID
     * @return 对应的作者对象，未找到时返回null
     */
    @Override
    public Author findById(int id) {
        return authorDAO.findById(id);
    }

    /**
     * 查询所有作者
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @return 全部作者列表
     */
    @Override
    public List<Author> findAll() {
        return authorDAO.findAll();
    }

    /**
     * 按关键词搜索作者（匹配姓名/邮箱/机构）
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param keyword 搜索关键词
     * @return 符合条件的作者列表
     */
    @Override
    public List<Author> findByKeyword(String keyword) {
        return authorDAO.findByKeyword(keyword);
    }
}
