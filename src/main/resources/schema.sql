-- ============================================================
-- 大黑山大学学报期刊投稿管理系统 - 数据库建表脚本
-- 数据库名：journal_submission_system
-- 字符集：utf8mb4
-- ============================================================

CREATE DATABASE IF NOT EXISTS journal_submission_system
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

USE journal_submission_system;

-- -----------------------------------------------------------
-- 1. 作者表 (author)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS author (
    id          INT AUTO_INCREMENT PRIMARY KEY COMMENT '作者ID',
    name        VARCHAR(50)  NOT NULL COMMENT '姓名',
    email       VARCHAR(100) NOT NULL COMMENT '邮箱',
    institution VARCHAR(100) NOT NULL COMMENT '所属机构',
    department  VARCHAR(100) NOT NULL COMMENT '院系/部门',
    phone       VARCHAR(20)  NOT NULL COMMENT '联系电话'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作者表';

-- -----------------------------------------------------------
-- 2. 审稿人表 (reviewer)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS reviewer (
    id          INT AUTO_INCREMENT PRIMARY KEY COMMENT '审稿人ID',
    name        VARCHAR(50)  NOT NULL COMMENT '姓名',
    email       VARCHAR(100) NOT NULL COMMENT '邮箱',
    institution VARCHAR(100) NOT NULL COMMENT '所属机构',
    department  VARCHAR(100) NOT NULL COMMENT '院系/部门',
    specialty   VARCHAR(100) NOT NULL COMMENT '专业领域',
    phone       VARCHAR(20)  NOT NULL COMMENT '联系电话'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审稿人表';

-- -----------------------------------------------------------
-- 3. 稿件表 (manuscript)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS manuscript (
    id                     INT AUTO_INCREMENT PRIMARY KEY COMMENT '稿件ID',
    title                  VARCHAR(200) NOT NULL COMMENT '论文题目',
    abstract_text          TEXT         NOT NULL COMMENT '摘要',
    keywords               VARCHAR(500) NOT NULL COMMENT '关键词(JSON数组)',
    content                TEXT         NOT NULL COMMENT '正文内容',
    journal_name           VARCHAR(20)  NOT NULL COMMENT '期刊名称',
    author_id              INT          NOT NULL COMMENT '作者ID',
    author_name            VARCHAR(50)  NOT NULL COMMENT '作者姓名',
    status                 VARCHAR(20)  NOT NULL DEFAULT 'submitted' COMMENT '稿件状态',
    submission_date        DATE         NOT NULL COMMENT '投稿日期',
    review_date            DATE         NULL COMMENT '审稿日期',
    acceptance_date        DATE         NULL COMMENT '收录日期',
    publication_date       DATE         NULL COMMENT '发表日期',
    assigned_reviewer_id   INT          NULL COMMENT '指派审稿人ID',
    assigned_reviewer_name VARCHAR(50)  NULL COMMENT '指派审稿人姓名',
    CONSTRAINT fk_manuscript_author FOREIGN KEY (author_id) REFERENCES author(id),
    CONSTRAINT fk_manuscript_reviewer FOREIGN KEY (assigned_reviewer_id) REFERENCES reviewer(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='稿件表';

-- -----------------------------------------------------------
-- 4. 参考文献表 (reference_lit)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS reference_lit (
    id            INT AUTO_INCREMENT PRIMARY KEY COMMENT '参考文献ID',
    manuscript_id INT          NOT NULL COMMENT '所属稿件ID',
    ref_key       VARCHAR(50)  NOT NULL COMMENT '引用标识(如ref-1-1)',
    title         VARCHAR(300) NOT NULL COMMENT '文献标题',
    authors       VARCHAR(200) NOT NULL COMMENT '作者',
    journal       VARCHAR(200) NOT NULL COMMENT '期刊/出版物名称',
    year          INT          NOT NULL COMMENT '发表年份',
    volume        VARCHAR(20)  NULL COMMENT '卷号',
    issue         VARCHAR(20)  NULL COMMENT '期号',
    pages         VARCHAR(50)  NULL COMMENT '页码',
    doi           VARCHAR(100) NULL COMMENT 'DOI标识符',
    CONSTRAINT fk_reference_manuscript FOREIGN KEY (manuscript_id) REFERENCES manuscript(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='参考文献表';

-- -----------------------------------------------------------
-- 5. 审稿记录表 (review_record)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS review_record (
    id            INT AUTO_INCREMENT PRIMARY KEY COMMENT '审稿记录ID',
    paper_id      INT          NOT NULL COMMENT '稿件ID',
    paper_title   VARCHAR(200) NOT NULL COMMENT '稿件题目',
    reviewer_id   INT          NOT NULL COMMENT '审稿人ID',
    reviewer_name VARCHAR(50)  NOT NULL COMMENT '审稿人姓名',
    decision      VARCHAR(20)  NOT NULL COMMENT '审稿决定(approved/rejected)',
    comments      TEXT         NOT NULL COMMENT '审稿意见',
    review_date   DATE         NOT NULL COMMENT '审稿日期',
    CONSTRAINT fk_review_paper FOREIGN KEY (paper_id) REFERENCES manuscript(id),
    CONSTRAINT fk_review_reviewer FOREIGN KEY (reviewer_id) REFERENCES reviewer(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审稿记录表';
