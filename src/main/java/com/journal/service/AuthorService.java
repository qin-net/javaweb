package com.journal.service;

import com.journal.impl.AuthorImpl;
import com.journal.impl.ManuscriptImpl;
import com.journal.model.Author;
import com.journal.model.Manuscript;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者业务服务类，封装作者查询及关联稿件数据填充的完整业务流程。
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.service
 *
 * 功能详述：
 *   协调 AuthorImpl 和 ManuscriptImpl，提供作者的按ID查询、
 *   全量查询及关键词搜索等业务操作。在查询作者信息时，
 *   自动通过 ManuscriptImpl 查询该作者的投稿稿件，
 *   并将稿件ID列表填充到作者的 paperIds 字段中，
 *   便于 API 返回时聚合展示。
 *
 * 依赖：
 *   com.journal.impl.AuthorImpl
 *   com.journal.impl.ManuscriptImpl
 */
public class AuthorService {

    /** 作者数据操作实现 */
    private final AuthorImpl authorImpl = new AuthorImpl();

    /** 稿件数据操作实现 */
    private final ManuscriptImpl manuscriptImpl = new ManuscriptImpl();

    /**
     * 根据作者ID查询作者信息，并填充其投稿稿件ID列表。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param id 作者ID
     * @return 包含稿件ID列表的作者对象，未找到时返回null
     */
    public Author getById(int id) {
        Author author = authorImpl.findById(id);
        if (author != null) {
            fillPaperIds(author, id);
        }
        return author;
    }

    /**
     * 查询所有作者，并为每个作者填充其投稿稿件ID列表。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @return 包含稿件ID列表的全部作者列表
     */
    public List<Author> getAll() {
        List<Author> authors = authorImpl.findAll();
        if (authors != null) {
            for (Author author : authors) {
                fillPaperIds(author, author.getId());
            }
        }
        return authors;
    }

    /**
     * 按关键词搜索作者（匹配姓名/邮箱/机构）。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param keyword 搜索关键词
     * @return 符合条件的作者列表
     */
    public List<Author> search(String keyword) {
        return authorImpl.findByKeyword(keyword);
    }

    /**
     * 为指定作者填充投稿稿件ID列表。
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param author  作者对象
     * @param authorId 作者ID
     */
    private void fillPaperIds(Author author, int authorId) {
        List<Manuscript> papers = manuscriptImpl.findByAuthorId(authorId);
        List<String> paperIds = new ArrayList<String>();
        if (papers != null) {
            for (Manuscript m : papers) {
                paperIds.add(String.valueOf(m.getId()));
            }
        }
        author.setPaperIds(paperIds);
    }
}
