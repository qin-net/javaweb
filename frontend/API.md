# 大黑山大学学报 - 期刊投稿管理系统 API 接口文档

## 基础信息

| 项目 | 说明 |
|------|------|
| Base URL | `/api` |
| 请求格式 | `application/json` |
| 响应格式 | `application/json` |
| 认证方式 | 暂未实现（预留 Bearer Token） |
| 字符编码 | UTF-8 |

## 通用响应格式

### 成功响应

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

### 错误响应

```json
{
  "code": 404,
  "message": "not found",
  "data": null
}
```

### HTTP 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 一、仪表盘统计

### 1.1 获取仪表盘统计数据

```
GET /api/dashboard/stats
```

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalSubmissions": 40,
    "underReview": 12,
    "accepted": 8,
    "published": 8,
    "byJournal": [
      { "name": "工学版", "count": 10 },
      { "name": "理学版", "count": 10 },
      { "name": "文科版", "count": 10 },
      { "name": "生物医学版", "count": 10 }
    ],
    "monthlyTrend": [
      { "month": "2026-01", "submissions": 5, "accepted": 3 },
      { "month": "2026-02", "submissions": 4, "accepted": 2 }
    ],
    "recentPapers": [
      {
        "id": "p40",
        "title": "论文题目示例",
        "authorName": "作者名",
        "journalName": "工学版",
        "status": "submitted",
        "submissionDate": "2026-07-10"
      }
    ]
  }
}
```

---

## 二、稿件管理 (Papers)

### 2.1 获取稿件列表

```
GET /api/papers
```

**请求参数（Query String）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | string | 否 | 搜索关键词（匹配标题、作者名、摘要） |
| journalName | string | 否 | 期刊名称：`工学版` / `理学版` / `文科版` / `生物医学版` |
| status | string | 否 | 稿件状态：`submitted` / `reviewing` / `accepted` / `rejected` / `published` |
| authorId | string | 否 | 作者ID，用于筛选某作者的稿件 |
| page | number | 否 | 页码，从1开始，默认1 |
| pageSize | number | 否 | 每页数量，默认10 |

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "items": [
      {
        "id": "p1",
        "title": "基于深度学习的遥感图像目标检测算法研究",
        "abstract": "针对遥感图像中目标检测精度低的问题...",
        "keywords": ["深度学习", "遥感图像", "目标检测", "YOLOv8"],
        "content": "1 引言\n随着遥感技术的飞速发展...",
        "references": [
          {
            "id": "ref-1-1",
            "title": "Deep Learning for Remote Sensing Image Analysis",
            "authors": "Zhang Y, Li X",
            "journal": "IEEE Trans. on Geoscience and Remote Sensing",
            "year": 2023,
            "volume": "61",
            "issue": "3",
            "pages": "1-15",
            "doi": "10.1109/TGRS.2023.1234567"
          }
        ],
        "journalName": "工学版",
        "authorId": "a1",
        "authorName": "张明辉",
        "status": "published",
        "submissionDate": "2024-03-15",
        "reviewDate": "2024-05-20",
        "acceptanceDate": "2024-06-10",
        "publicationDate": "2024-07-01",
        "assignedReviewerId": "r1",
        "assignedReviewerName": "马文军"
      }
    ],
    "total": 40,
    "page": 1,
    "pageSize": 10
  }
}
```

### 2.2 获取稿件详情

```
GET /api/papers/:id
```

**路径参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| id | string | 稿件ID |

**响应：** 返回单个 Paper 对象，同列表项结构。

### 2.3 创建投稿

```
POST /api/papers
```

**请求体：**

```json
{
  "title": "论文题目",
  "journalName": "工学版",
  "abstract": "摘要内容...",
  "keywords": ["关键词1", "关键词2", "关键词3"],
  "content": "正文内容...",
  "references": [
    {
      "title": "参考文献标题",
      "authors": "作者1, 作者2",
      "journal": "期刊名",
      "year": 2024,
      "volume": "10",
      "issue": "2",
      "pages": "100-120",
      "doi": "10.xxxx/xxxxx"
    }
  ],
  "authorId": "a1",
  "authorName": "作者名"
}
```

**必填字段：** `title`, `journalName`, `abstract`, `keywords`(至少3个), `content`, `authorId`, `authorName`

**响应：** 返回新创建的 Paper 对象，其中 `id` 和 `submissionDate` 由服务端自动生成，`status` 默认为 `submitted`。

### 2.4 更新稿件

```
PUT /api/papers/:id
```

**请求体：** 同创建接口，支持部分字段更新。

### 2.5 标记稿件为已收录

```
PUT /api/papers/:id/accept
```

无请求体。服务端自动设置 `status = "accepted"` 和 `acceptanceDate` 为当前日期。

**响应：** 返回更新后的 Paper 对象。

### 2.6 获取稿件的审稿记录

```
GET /api/papers/:id/reviews
```

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "rev1",
      "paperId": "p1",
      "paperTitle": "基于深度学习的遥感图像目标检测算法研究",
      "reviewerId": "r1",
      "reviewerName": "马文军",
      "decision": "approved",
      "comments": "该论文选题新颖，实验设计合理，结果分析充分...",
      "reviewDate": "2024-05-20"
    }
  ]
}
```

---

## 三、审稿管理 (Reviews)

### 3.1 提交审稿意见

```
POST /api/reviews
```

**请求体：**

```json
{
  "paperId": "p1",
  "reviewerId": "r1",
  "reviewerName": "马文军",
  "decision": "approved",
  "comments": "审稿意见文字..."
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| paperId | string | 是 | 稿件ID |
| reviewerId | string | 是 | 审稿人ID |
| reviewerName | string | 是 | 审稿人姓名 |
| decision | string | 是 | 审稿决定：`approved`（同意录用）/ `rejected`（退回修改） |
| comments | string | 是 | 审稿文字意见 |

**说明：** 提交审稿后，对应稿件状态会自动更新：
- `approved` → 稿件状态变为 `accepted`，设置 `acceptanceDate`
- `rejected` → 稿件状态变为 `rejected`

---

## 四、作者管理 (Authors)

### 4.1 获取作者列表

```
GET /api/authors
```

**请求参数（Query String）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | string | 否 | 搜索关键词（匹配姓名、邮箱、机构） |

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "a1",
      "name": "张明辉",
      "email": "zhangmh@dlut.edu.cn",
      "institution": "大连理工大学",
      "department": "机械工程学院",
      "phone": "13804111234",
      "paperIds": ["p1", "p6", "p10"]
    }
  ]
}
```

### 4.2 获取作者详情

```
GET /api/authors/:id
```

### 4.3 获取作者的稿件列表

```
GET /api/authors/:id/papers
```

**响应：** 返回该作者的所有 Paper 对象数组。

---

## 五、审稿人管理 (Reviewers)

### 5.1 获取审稿人列表

```
GET /api/reviewers
```

**请求参数（Query String）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | string | 否 | 搜索关键词（匹配姓名、邮箱、机构、专业领域） |

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "r1",
      "name": "马文军",
      "email": "mawj@tsinghua.edu.cn",
      "institution": "清华大学",
      "department": "计算机科学与技术系",
      "specialty": "人工智能与机器学习",
      "phone": "13801030001",
      "reviewIds": ["rev1", "rev2", "rev3"]
    }
  ]
}
```

### 5.2 获取审稿人详情

```
GET /api/reviewers/:id
```

### 5.3 获取审稿人的审稿记录

```
GET /api/reviewers/:id/reviews
```

**响应：** 返回该审稿人的所有 Review 对象数组。

---

## 六、审稿指派管理 (Assignments)

### 6.1 指派审稿人

```
POST /api/assignments
```

**请求体：**

```json
{
  "paperId": "p5",
  "reviewerId": "r3",
  "reviewerName": "刘志远"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| paperId | string | 是 | 稿件ID |
| reviewerId | string | 是 | 审稿人ID |
| reviewerName | string | 是 | 审稿人姓名 |

**说明：** 指派后稿件状态自动变为 `reviewing`，稿件的 `assignedReviewerId` 和 `assignedReviewerName` 字段会被设置。

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": { "success": true }
}
```

### 6.2 获取当前指派列表

```
GET /api/assignments
```

**响应：** 返回所有已指派审稿人的 Paper 对象数组（即 `assignedReviewerId` 不为空的稿件）。

---

## 七、数据模型参考

### Paper（稿件）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | string | 稿件唯一标识 |
| title | string | 论文题目 |
| abstract | string | 摘要 |
| keywords | string[] | 关键词数组 |
| content | string | 正文内容 |
| references | Reference[] | 参考文献列表 |
| journalName | JournalName | 期刊名称枚举 |
| authorId | string | 作者ID |
| authorName | string | 作者姓名 |
| status | PaperStatus | 稿件状态枚举 |
| submissionDate | string | 投稿日期 (YYYY-MM-DD) |
| reviewDate | string? | 审稿日期 |
| acceptanceDate | string? | 收录日期 |
| publicationDate | string? | 发表日期 |
| assignedReviewerId | string? | 指派审稿人ID |
| assignedReviewerName | string? | 指派审稿人姓名 |

### JournalName（期刊枚举）

| 值 | 说明 |
|------|------|
| `工学版` | 工学版 |
| `理学版` | 理学版 |
| `文科版` | 文科版 |
| `生物医学版` | 生物医学版 |

### PaperStatus（状态枚举）

| 值 | 说明 |
|------|------|
| `submitted` | 已投稿 |
| `reviewing` | 审稿中 |
| `accepted` | 已收录 |
| `rejected` | 已退回 |
| `published` | 已发表 |

### Reference（参考文献）

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | string | 是 | 引用唯一标识 |
| title | string | 是 | 文献标题 |
| authors | string | 是 | 作者 |
| journal | string | 是 | 期刊/出版物名称 |
| year | number | 是 | 发表年份 |
| volume | string | 否 | 卷号 |
| issue | string | 否 | 期号 |
| pages | string | 否 | 页码 |
| doi | string | 否 | DOI标识符 |

### Review（审稿记录）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | string | 审稿记录唯一标识 |
| paperId | string | 稿件ID |
| paperTitle | string | 稿件题目 |
| reviewerId | string | 审稿人ID |
| reviewerName | string | 审稿人姓名 |
| decision | ReviewDecision | 审稿决定枚举 |
| comments | string | 审稿意见 |
| reviewDate | string | 审稿日期 (YYYY-MM-DD) |

### ReviewDecision（审稿决定枚举）

| 值 | 说明 |
|------|------|
| `approved` | 同意录用 |
| `rejected` | 退回修改 |

### Author（作者）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | string | 作者唯一标识 |
| name | string | 姓名 |
| email | string | 邮箱 |
| institution | string | 所属机构 |
| department | string | 院系/部门 |
| phone | string | 联系电话 |
| paperIds | string[] | 投稿稿件ID列表 |

### Reviewer（审稿人）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | string | 审稿人唯一标识 |
| name | string | 姓名 |
| email | string | 邮箱 |
| institution | string | 所属机构 |
| department | string | 院系/部门 |
| specialty | string | 专业领域 |
| phone | string | 联系电话 |
| reviewIds | string[] | 审稿记录ID列表 |

---

## 八、错误码定义

| 错误码 | HTTP状态码 | 说明 |
|--------|------------|------|
| 200 | 200 | 操作成功 |
| 400 | 400 | 请求参数错误（缺少必填字段、格式错误等） |
| 404 | 404 | 请求的资源不存在 |
| 500 | 500 | 服务器内部错误 |

---

## 九、注意事项

1. **日期格式**：所有日期字段统一使用 `YYYY-MM-DD` 格式（如 `2026-07-14`）
2. **关键词**：关键词字段为字符串数组，投稿时至少需要3个关键词
3. **稿件状态流转**：`submitted` → `reviewing` → `accepted`/`rejected` → `published`
4. **审稿后自动更新**：提交审稿意见后，系统会自动更新稿件的状态和日期
5. **指派审稿**：将稿件指派给审稿人后，稿件自动进入 `reviewing` 状态
6. **收录标记**：调用 accept 接口或审稿通过后，稿件标记为 `accepted` 状态
7. **分页**：稿件列表接口支持分页，默认每页10条，返回 `total`、`page`、`pageSize` 字段
