package com.journal.interfacemodel;

import com.journal.model.Manuscript;
import com.journal.model.Reference;
import java.util.List;

/**
 * 投稿操作接口
 *
 * 编写者：张鸿昊
 * 完成时间：2026-07-14
 * 所属包：com.journal.interfacemodel
 *
 * 功能详述：
 *   定义投稿业务契约，包括新建投稿（含参考文献）和更新投稿信息，
 *   涵盖稿件与参考文献的关联操作。
 *
 * 实现类：
 *   impl/SubmissionImpl.java
 */
public interface SubmissionInterface {

    /**
     * 投稿（含参考文献），返回稿件ID
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param manuscript 稿件对象
     * @param references 参考文献列表
     * @return 投稿成功后生成的稿件ID
     */
    int submitManuscript(Manuscript manuscript, List<Reference> references);

    /**
     * 更新投稿
     *
     * 编写者：张鸿昊
     * 完成时间：2026-07-14
     *
     * @param manuscript 稿件对象
     * @param references 参考文献列表
     * @return 更新成功返回true，否则返回false
     */
    boolean updateManuscript(Manuscript manuscript, List<Reference> references);
}
