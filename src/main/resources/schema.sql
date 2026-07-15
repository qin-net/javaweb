-- ============================================================
-- 大黑山大学学报期刊投稿管理系统 - 数据库建表脚本
-- 数据库名：journal_submission_system
-- 字符集：utf8mb4
-- ============================================================

-- 确保客户端连接使用 UTF-8
SET NAMES utf8mb4;

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
    name        VARCHAR(300) NOT NULL COMMENT '姓名',            -- 加长到300，支持长机构名
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
    title                  VARCHAR(300) NOT NULL COMMENT '论文题目',   -- 加长到300
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
    paper_title   VARCHAR(300) NOT NULL COMMENT '稿件题目',        -- 加长到300
    reviewer_id   INT          NOT NULL COMMENT '审稿人ID',
    reviewer_name VARCHAR(50)  NOT NULL COMMENT '审稿人姓名',
    decision      VARCHAR(20)  NOT NULL COMMENT '审稿决定(approved/rejected)',
    comments      TEXT         NOT NULL COMMENT '审稿意见',
    review_date   DATE         NOT NULL COMMENT '审稿日期',
    CONSTRAINT fk_review_paper FOREIGN KEY (paper_id) REFERENCES manuscript(id),
    CONSTRAINT fk_review_reviewer FOREIGN KEY (reviewer_id) REFERENCES reviewer(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审稿记录表';

-- ============================================================
-- RBAC 权限系统表（共5张）
-- ============================================================

-- -----------------------------------------------------------
-- 6. 角色表 (sys_role)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_role (
    id          INT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
    role_name   VARCHAR(50)  NOT NULL COMMENT '角色名称',
    role_code   VARCHAR(50)  NOT NULL UNIQUE COMMENT '角色编码(admin/reviewer/author)',
    data_scope  TINYINT      NOT NULL DEFAULT 1 COMMENT '数据范围:1全部 2仅本人',
    description VARCHAR(200) NULL COMMENT '角色描述',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态:1启用 0禁用'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- -----------------------------------------------------------
-- 7. 系统用户表 (sys_user)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_user (
    id          INT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username    VARCHAR(50)  NOT NULL UNIQUE COMMENT '登录用户名',
    password    VARCHAR(128) NOT NULL COMMENT '密码(SHA-256哈希)',
    real_name   VARCHAR(50)  NOT NULL COMMENT '真实姓名',
    role_id     INT          NOT NULL COMMENT '角色ID',
    ref_id      INT          NULL COMMENT '关联业务ID(作者ID或审稿人ID)',
    email       VARCHAR(100) NULL COMMENT '邮箱',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态:1启用 0禁用',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES sys_role(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- -----------------------------------------------------------
-- 8. 用户角色关联表 (sys_user_role)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_user_role (
    id      INT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
    user_id INT NOT NULL COMMENT '用户ID',
    role_id INT NOT NULL COMMENT '角色ID',
    UNIQUE KEY uk_user_role (user_id, role_id),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES sys_user(id),
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES sys_role(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- -----------------------------------------------------------
-- 9. 菜单表 (sys_menu)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_menu (
    id          INT AUTO_INCREMENT PRIMARY KEY COMMENT '菜单ID',
    menu_name   VARCHAR(50)  NOT NULL COMMENT '菜单名称',
    menu_code   VARCHAR(50)  NOT NULL UNIQUE COMMENT '菜单编码',
    path        VARCHAR(200) NOT NULL COMMENT '前端路由路径',
    api_pattern VARCHAR(200) NULL COMMENT '后端API路径模式(用于鉴权)',
    icon        VARCHAR(50)  NULL COMMENT '图标名称',
    sort_order  INT          NOT NULL DEFAULT 0 COMMENT '排序号',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态:1启用 0禁用'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- -----------------------------------------------------------
-- 10. 角色菜单关联表 (sys_role_menu)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_role_menu (
    id      INT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
    role_id INT NOT NULL COMMENT '角色ID',
    menu_id INT NOT NULL COMMENT '菜单ID',
    UNIQUE KEY uk_role_menu (role_id, menu_id),
    CONSTRAINT fk_rm_role FOREIGN KEY (role_id) REFERENCES sys_role(id),
    CONSTRAINT fk_rm_menu FOREIGN KEY (menu_id) REFERENCES sys_menu(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';