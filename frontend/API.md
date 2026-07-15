# 大黑山大学学报 — 期刊投稿管理系统 API 接口文档

> 版本：v2.0 &nbsp;|&nbsp; 更新日期：2026-07-15 &nbsp;|&nbsp; 编写者：张鸿昊

---

## 基础信息

| 项目 | 说明 |
|------|------|
| Base URL | `/api` |
| 请求格式 | `application/json; charset=UTF-8` |
| 响应格式 | `application/json; charset=UTF-8` |
| 认证方式 | HttpSession（Cookie 传递 `JSESSIONID`），前端请求需携带 `withCredentials: true` |
| 跨域策略 | 仅允许 `http://localhost:5173`，`Access-Control-Allow-Credentials: true` |
| 字符编码 | UTF-8 |
| ID 类型约定 | **所有业务实体的 ID 字段均为整数（int）类型**，非字符串 |

---

## 通用响应格式

所有接口统一使用如下 JSON 信封结构，业务数据封装在 `data` 字段中。

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
  "code": 400,
  "message": "具体错误描述",
  "data": null
}
```

### 错误码定义

| 业务码 | 含义 | 典型触发场景 |
|--------|------|-------------|
| 200 | 操作成功 | — |
| 400 | 请求参数错误 | 缺少必填字段、格式错误、用户名/密码错误、非法状态值 |
| 401 | 未认证 | 未登录或会话过期 |
| 404 | 资源不存在 | 查询的稿件/作者/审稿人不存在 |
| 500 | 服务器内部错误 | 数据库异常、未预期的运行时错误 |

---

## 过滤器链

所有 `/api/*` 请求依次经过以下过滤器，在到达业务 Servlet 之前执行预处理：

| 顺序 | 过滤器 | 路径 | 职责 |
|------|--------|------|------|
| 1 | CorsFilter | `/api/*` | 设置 CORS 响应头；OPTIONS 预检请求直接返回 200，不继续传递 |
| 2 | AuthFilter | `/api/*` | Session 鉴权；放行 OPTIONS 和 `/api/auth/login`；其余请求检查 Session 中的 `currentUser`，缺失则返回 401 |
| 3 | EncodingFilter | `/*` | 设置请求/响应字符编码为 UTF-8 |

---

## 一、认证接口 (`/api/auth/*`)

认证系统基于 RBAC 模型，采用 HttpSession 维持登录状态。系统内置三种角色：管理员（admin）、审稿人（reviewer）、作者（author），每种角色通过 `sys_role_menu` 关联表配置可访问的菜单和功能。

### 1.1 用户登录

```
POST /api/auth/login
```

**请求体：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | string | 是 | 登录用户名 |
| password | string | 是 | 登录密码（明文传输，服务端使用 SHA-256 哈希比对） |

**请求示例：**

```json
{
  "username": "admin",
  "password": "123456"
}
```

**成功响应（200）：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "admin",
    "realName": "系统管理员",
    "roleId": 1,
    "roleCode": "admin",
    "roleName": "系统管理员",
    "refId": null,
    "email": "admin@dhs.edu.cn",
    "status": 1,
    "createTime": "2026-07-15 00:00:00",
    "dataScope": 1
  }
}
```

**响应字段说明：**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | int | 系统用户 ID |
| username | string | 用户名 |
| realName | string | 真实姓名 |
| roleId | int | 角色 ID |
| roleCode | string | 角色编码：`admin` / `reviewer` / `author` |
| roleName | string | 角色名称 |
| refId | int \| null | 关联业务 ID。管理员为 null；审稿人对应 reviewer 表 id；作者对应 author 表 id |
| email | string | 邮箱 |
| status | int | 账号状态：1 启用，0 禁用 |
| createTime | string | 创建时间 |
| dataScope | int | 数据权限范围：1 全部数据，2 仅本人数据 |

**错误场景：**

| 场景 | 错误码 | message |
|------|--------|---------|
| 请求体为空或非法 JSON | 400 | 请求体不能为空 |
| username 或 password 为空 | 400 | 用户名和密码不能为空 |
| 用户名不存在 / 密码错误 | 400 | 用户名或密码错误 |

**说明：** 登录成功后，服务端将用户信息存入 HttpSession（attribute 名为 `currentUser`），后续请求通过 Cookie 中的 `JSESSIONID` 维持会话。响应中不包含 `password` 字段（已置 null）。

### 1.2 获取当前登录用户

```
GET /api/auth/me
```

无请求参数。

**成功响应（200）：** `data` 结构同登录接口的响应（不含 password）。

**未登录响应（401）：**

```json
{
  "code": 401,
  "message": "未登录或会话已过期",
  "data": null
}
```

### 1.3 获取当前用户菜单列表

```
GET /api/auth/menus
```

无请求参数。根据当前登录用户的角色，从 `sys_role_menu` + `sys_menu` 关联查询该角色可访问的菜单列表，按 `sort_order` 升序排列。

**成功响应（200）：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "menuName": "工作台",
      "menuCode": "dashboard",
      "path": "/",
      "apiPattern": "/api/dashboard/*",
      "icon": "LayoutDashboard",
      "sortOrder": 1,
      "status": 1
    }
  ]
}
```

**菜单字段说明：**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | int | 菜单 ID |
| menuName | string | 菜单显示名称 |
| menuCode | string | 菜单编码（唯一标识） |
| path | string | 前端路由路径 |
| apiPattern | string | 后端 API 路径模式 |
| icon | string | lucide-react 图标名称 |
| sortOrder | int | 排序号 |
| status | int | 状态：1 启用，0 禁用 |

**各角色默认菜单配置：**

| 角色 | 菜单列表 |
|------|---------|
| admin（管理员） | 工作台、稿件管理、作者管理、审稿人管理、审稿指派 |
| reviewer（审稿人） | 审稿管理 |
| author（作者） | 在线投稿、稿件管理 |

### 1.4 用户登出

```
POST /api/auth/logout
```

无请求体。服务端销毁当前 Session。

**响应（200）：**

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

**说明：** 即使当前未登录，调用此接口也会返回 200。

---

## 二、仪表盘统计 (`/api/dashboard/*`)

### 2.1 获取仪表盘统计数据

```
GET /api/dashboard/stats
```

无请求参数。聚合查询稿件的各维度统计数据。

**成功响应（200）：**

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
      { "month": "2026-01", "submissions": 5, "accepted": 3 }
    ],
    "recentPapers": [
      {
        "id": 40,
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

**响应字段说明：**

| 字段 | 类型 | 说明 |
|------|------|------|
| totalSubmissions | int | 稿件总数 |
| underReview | int | 状态为 `reviewing` 的稿件数 |
| accepted | int | 状态为 `accepted` 的稿件数 |
| published | int | 状态为 `published` 的稿件数 |
| byJournal | array | 按期刊分类统计，每项含 `name`(string) 和 `count`(int) |
| monthlyTrend | array | 按月投稿趋势，每项含 `month`(string, YYYY-MM)、`submissions`(int)、`accepted`(int) |
| recentPapers | array | 最近投稿的 10 篇稿件摘要列表 |

---

## 三、稿件管理 (`/api/papers/*`)

### 3.1 获取稿件列表（分页）

```
GET /api/papers
```

**请求参数（Query String）：**

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| keyword | string | 否 | — | 搜索关键词（匹配标题、作者名、摘要） |
| journalName | string | 否 | — | 期刊名称：`工学版` / `理学版` / `文科版` / `生物医学版` |
| status | string | 否 | — | 稿件状态：`submitted` / `reviewing` / `accepted` / `rejected` / `published` |
| authorId | string | 否 | — | 作者 ID（字符串形式传递） |
| page | int | 否 | 1 | 页码，从 1 开始 |
| pageSize | int | 否 | 10 | 每页数量 |

**数据权限过滤规则（重要）：**

后端根据当前登录用户的角色自动过滤数据，**审稿人和作者角色会忽略所有 Query 筛选参数**：

| 角色 | 过滤行为 |
|------|---------|
| admin | 使用全部 Query 参数进行筛选，返回所有匹配数据 |
| reviewer | 忽略 Query 参数，强制只返回 `assigned_reviewer_id = 当前用户 refId` 的稿件 |
| author | 忽略 Query 参数，强制只返回 `author_id = 当前用户 refId` 的稿件 |

**响应示例（200）：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "items": [
      {
        "id": 1,
        "title": "基于深度学习的遥感图像目标检测算法研究",
        "abstractText": "针对遥感图像中目标检测精度低的问题...",
        "keywords": ["深度学习", "遥感图像", "目标检测", "YOLOv8"],
        "content": "1 引言\n随着遥感技术的飞速发展...",
        "journalName": "工学版",
        "authorId": 1,
        "authorName": "张明辉",
        "status": "published",
        "submissionDate": "2024-03-15",
        "reviewDate": "2024-05-20",
        "acceptanceDate": "2024-06-10",
        "publicationDate": "2024-07-01",
        "assignedReviewerId": 1,
        "assignedReviewerName": "马文军",
        "references": []
      }
    ],
    "total": 40,
    "page": 1,
    "pageSize": 10
  }
}
```

**分页响应字段：**

| 字段 | 类型 | 说明 |
|------|------|------|
| items | Manuscript[] | 当前页的稿件列表 |
| total | int | 符合条件的稿件总数 |
| page | int | 当前页码 |
| pageSize | int | 每页数量 |

**说明：** 分页采用内存分页方式（先查全量，再截取），不使用 SQL LIMIT。列表接口返回的 `references` 字段为空数组，详情接口中才会填充完整的参考文献列表。

### 3.2 获取稿件详情

```
GET /api/papers/{id}
```

**路径参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| id | int | 稿件 ID |

**成功响应（200）：** `data` 为完整的 Manuscript 对象，包含关联查询的 `references` 参考文献列表。

**错误场景：** 稿件不存在 → 404 `"论文不存在"`。

**说明：** 详情接口不做数据权限过滤，任何已登录用户均可通过 ID 查询任意稿件详情。

### 3.3 获取稿件的审稿记录

```
GET /api/papers/{id}/reviews
```

**路径参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| id | int | 稿件 ID |

**成功响应（200）：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "paperId": 1,
      "paperTitle": "基于深度学习的遥感图像目标检测算法研究",
      "reviewerId": 1,
      "reviewerName": "马文军",
      "decision": "approved",
      "comments": "该论文选题新颖，实验设计合理...",
      "reviewDate": "2024-05-20"
    }
  ]
}
```

**说明：** 若该稿件不存在或无审稿记录，返回空数组，不会返回 404。

### 3.4 创建投稿

```
POST /api/papers
```

**请求体：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| title | string | 是 | 论文题目 |
| abstractText | string | 是 | 摘要内容 |
| content | string | 是 | 正文内容 |
| journalName | string | 是 | 期刊名称：`工学版` / `理学版` / `文科版` / `生物医学版` |
| authorId | int | 是 | 作者 ID |
| keywords | string[] | 否 | 关键词数组（建议至少 3 个） |
| references | Reference[] | 否 | 参考文献列表（见下方 Reference 结构） |

**请求示例：**

```json
{
  "title": "论文题目",
  "abstractText": "摘要内容...",
  "content": "正文内容...",
  "journalName": "工学版",
  "authorId": 1,
  "keywords": ["关键词1", "关键词2", "关键词3"],
  "references": [
    {
      "refKey": "ref-1",
      "title": "参考文献标题",
      "authors": "作者1, 作者2",
      "journal": "期刊名",
      "year": 2024,
      "volume": "10",
      "issue": "2",
      "pages": "100-120",
      "doi": "10.xxxx/xxxxx"
    }
  ]
}
```

**成功响应（200）：** `data` 为新创建的 Manuscript 对象（含服务端生成的 `id`）。

**服务端自动行为：** `status` 强制设置为 `submitted`（请求体中的 status 字段会被忽略）；`submissionDate` 自动设置为服务器当天日期；`authorName` 根据 `authorId` 自动查询填充。

### 3.5 更新稿件信息

```
PUT /api/papers/{id}
```

**路径参数：** `id`（int，稿件 ID）

**请求体：** 与创建投稿接口字段相同。

**注意：** 此接口为**全量替换**语义，所有字段都会被请求体中的值覆盖。未传的字段会被置为 null 或默认值 0。若不传 `references` 字段，原有参考文献会被清空。前端调用时务必传递完整字段。

**成功响应（200）：** `data` 为更新后的完整 Manuscript 对象（重新查库获取）。

### 3.6 更新稿件状态

```
PUT /api/papers/{id}/status
```

**路径参数：** `id`（int，稿件 ID）

**请求体：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| status | string | 是 | 目标状态，必须为合法值：`submitted` / `reviewing` / `accepted` / `rejected` / `published` |

**请求示例：**

```json
{
  "status": "published"
}
```

**状态变更自动联动：**

| 目标状态 | 自动行为 |
|---------|---------|
| accepted | 自动设置 `acceptanceDate` 为当天日期 |
| published | 自动设置 `publicationDate` 为当天日期 |
| 其他 | 无额外自动行为 |

**错误场景：** status 为空 → 400 `"缺少 status 字段"`；status 非合法值 → 400 `"非法的稿件状态：xxx"`。

**成功响应（200）：** `data` 为更新后的完整 Manuscript 对象。

### 3.7 标记稿件为已收录

```
PUT /api/papers/{id}/accept
```

**路径参数：** `id`（int，稿件 ID）

无请求体。服务端自动将 `status` 设为 `accepted`，`acceptanceDate` 设为当天日期。

**成功响应（200）：** `data` 为更新后的完整 Manuscript 对象。

---

## 四、审稿管理 (`/api/reviews/*`)

### 4.1 查询审稿记录

```
GET /api/reviews
```

**请求参数（Query String，互斥优先级：paperId > reviewerId > 全部）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| paperId | int | 否 | 按稿件 ID 查询（若提供则忽略 reviewerId） |
| reviewerId | int | 否 | 按审稿人 ID 查询（仅当 paperId 未提供时生效） |

**说明：** 若两个参数均未提供，返回全部审稿记录。此接口**不支持路径参数**形式的单条查询（如 `GET /api/reviews/1` 会返回 404）。

**成功响应（200）：** `data` 为 ReviewRecord 数组。

### 4.2 提交审稿意见

```
POST /api/reviews
```

**请求体：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| paperId | int | 是 | 稿件 ID |
| reviewerId | int | 是 | 审稿人 ID |
| decision | string | 是 | 审稿决定：`approved`（同意录用）/ `rejected`（退回修改） |
| comments | string | 是 | 审稿意见文字内容 |
| paperTitle | string | 否 | 稿件标题（前端传入，便于记录展示） |
| reviewerName | string | 否 | 审稿人姓名（前端传入，便于记录展示） |

**请求示例：**

```json
{
  "paperId": 5,
  "reviewerId": 3,
  "decision": "approved",
  "comments": "论文选题新颖，实验设计合理，建议录用。",
  "paperTitle": "基于深度学习的遥感图像目标检测算法研究",
  "reviewerName": "徐明达"
}
```

**服务端自动行为：**

- `reviewDate` 由服务端自动设置为当天日期，请求体中的 reviewDate 会被忽略。
- 提交审稿后，**自动更新对应稿件的状态和日期**：
  - `decision = "approved"` → 稿件 `status` 变为 `accepted`，`reviewDate` 和 `acceptanceDate` 设为当天
  - `decision = "rejected"` → 稿件 `status` 变为 `rejected`，`reviewDate` 设为当天

**成功响应（200）：** `data` 为新创建的 ReviewRecord 对象（含服务端生成的 `id` 和 `reviewDate`）。

---

## 五、作者管理 (`/api/authors/*`)

此模块仅提供只读查询接口。

### 5.1 获取作者列表

```
GET /api/authors
```

**请求参数（Query String）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | string | 否 | 搜索关键词（匹配姓名、邮箱、机构） |

**成功响应（200）：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "张明辉",
      "email": "zhangmh@dlut.edu.cn",
      "institution": "大连理工大学",
      "department": "机械工程学院",
      "phone": "13804111234",
      "paperIds": [1, 6, 10]
    }
  ]
}
```

### 5.2 获取作者详情

```
GET /api/authors/{id}
```

**路径参数：** `id`（int，作者 ID）

**成功响应（200）：** `data` 为单个 Author 对象。

**错误场景：** 作者不存在 → 404 `"作者不存在"`。

### 5.3 获取作者的稿件列表

```
GET /api/authors/{id}/papers
```

**路径参数：** `id`（int，作者 ID）

**成功响应（200）：** `data` 为该作者的所有 Manuscript 对象数组。若作者不存在或无稿件，返回空数组。

---

## 六、审稿人管理 (`/api/reviewers/*`)

此模块仅提供只读查询接口。

### 6.1 获取审稿人列表

```
GET /api/reviewers
```

**请求参数（Query String）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | string | 否 | 搜索关键词（匹配姓名、邮箱、机构、专业领域） |

**成功响应（200）：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "马文军",
      "email": "mawj@tsinghua.edu.cn",
      "institution": "清华大学",
      "department": "计算机科学与技术系",
      "specialty": "人工智能与机器学习",
      "phone": "13801030001",
      "reviewIds": [1, 2, 3]
    }
  ]
}
```

### 6.2 获取审稿人详情

```
GET /api/reviewers/{id}
```

**路径参数：** `id`（int，审稿人 ID）

**成功响应（200）：** `data` 为单个 Reviewer 对象。

**错误场景：** 审稿人不存在 → 404 `"审稿人不存在"`。

### 6.3 获取审稿人的审稿记录

```
GET /api/reviewers/{id}/reviews
```

**路径参数：** `id`（int，审稿人 ID）

**成功响应（200）：** `data` 为该审稿人的所有 ReviewRecord 对象数组。

---

## 七、审稿指派管理 (`/api/assignments/*`)

### 7.1 获取当前指派列表

```
GET /api/assignments
```

无请求参数。查询所有 `assigned_reviewer_id` 不为空的稿件，构造指派记录列表。

**成功响应（200）：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "asgn-5",
      "paperId": "5",
      "paperTitle": "基于深度学习的遥感图像目标检测算法研究",
      "reviewerId": "3",
      "reviewerName": "徐明达",
      "assignedDate": "2024-03-15"
    }
  ]
}
```

**字段说明（特殊类型）：**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | string | 虚拟指派 ID，格式为 `"asgn-{稿件ID}"`（系统中无独立指派表，此 ID 为动态生成） |
| paperId | string | 稿件 ID（此接口中为字符串类型，与其他接口的 int 类型不同，请注意类型转换） |
| paperTitle | string | 稿件标题 |
| reviewerId | string \| null | 审稿人 ID（字符串类型，未指派时为 null） |
| reviewerName | string | 审稿人姓名 |
| assignedDate | string | 复用稿件的投稿日期（`submissionDate`），非真正的指派日期 |

### 7.2 指派审稿人

```
POST /api/assignments
```

**请求体：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| paperId | int | 是 | 稿件 ID，必须大于 0 |
| reviewerId | int | 是 | 审稿人 ID，必须大于 0 |

**请求示例：**

```json
{
  "paperId": 5,
  "reviewerId": 3
}
```

**服务端自动行为：** 指派成功后，稿件的 `assigned_reviewer_id` 和 `assigned_reviewer_name` 字段会被更新，稿件状态自动变为 `reviewing`。

**成功响应（200）：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "paperId": 5,
    "reviewerId": 3,
    "message": "审稿人指派成功"
  }
}
```

**错误场景：**

| 场景 | 错误码 | message |
|------|--------|---------|
| paperId 缺失或 ≤ 0 | 400 | 缺少有效的 paperId |
| reviewerId 缺失或 ≤ 0 | 400 | 缺少有效的 reviewerId |
| 审稿人不存在 | 400 | 审稿人不存在：reviewerId=xxx |

---

## 八、数据模型参考

### Manuscript（稿件）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | int | 稿件唯一标识（自增） |
| title | string | 论文题目 |
| abstractText | string | 摘要 |
| keywords | string[] | 关键词数组（数据库中以 JSON 数组字符串存储在 VARCHAR 列） |
| content | string | 正文内容 |
| references | Reference[] | 参考文献列表（关联查询填充，仅详情接口返回完整数据） |
| journalName | string | 期刊名称 |
| authorId | int | 作者 ID（外键关联 author 表） |
| authorName | string | 作者姓名 |
| status | string | 稿件状态 |
| submissionDate | string | 投稿日期（YYYY-MM-DD） |
| reviewDate | string \| null | 审稿日期 |
| acceptanceDate | string \| null | 收录日期 |
| publicationDate | string \| null | 发表日期 |
| assignedReviewerId | int \| null | 指派审稿人 ID（外键关联 reviewer 表，未指派时为 null） |
| assignedReviewerName | string \| null | 指派审稿人姓名 |

### JournalName（期刊名称枚举）

| 值 | 说明 |
|------|------|
| `工学版` | 大黑山大学学报（工学版） |
| `理学版` | 大黑山大学学报（理学版） |
| `文科版` | 大黑山大学学报（文科版） |
| `生物医学版` | 大黑山大学学报（生物医学版） |

### PaperStatus（稿件状态枚举）

| 值 | 说明 | 状态流转 |
|------|------|---------|
| `submitted` | 已投稿 | 初始状态 |
| `reviewing` | 审稿中 | 指派审稿人后自动流转 |
| `accepted` | 已收录 | 审稿通过或手动收录后流转 |
| `rejected` | 已退回 | 审稿不通过时流转 |
| `published` | 已发表 | 手动标记发表 |

状态流转路径：`submitted` → `reviewing` → `accepted` / `rejected` → `published`

### Reference（参考文献）

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | int | 否（服务端生成） | 引用唯一标识 |
| manuscriptId | int | 否（服务端填充） | 关联稿件 ID |
| refKey | string | 是 | 引用键（如 `ref-1`） |
| title | string | 是 | 文献标题 |
| authors | string | 是 | 作者列表 |
| journal | string | 是 | 期刊/出版物名称 |
| year | int | 是 | 发表年份 |
| volume | string | 否 | 卷号 |
| issue | string | 否 | 期号 |
| pages | string | 否 | 页码 |
| doi | string | 否 | DOI 标识符 |

### ReviewRecord（审稿记录）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | int | 审稿记录唯一标识（自增） |
| paperId | int | 稿件 ID |
| paperTitle | string | 稿件题目 |
| reviewerId | int | 审稿人 ID |
| reviewerName | string | 审稿人姓名 |
| decision | string | 审稿决定：`approved` / `rejected` |
| comments | string | 审稿意见 |
| reviewDate | string | 审稿日期（YYYY-MM-DD，服务端自动生成） |

### Author（作者）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | int | 作者唯一标识（自增） |
| name | string | 姓名 |
| email | string | 邮箱 |
| institution | string | 所属机构 |
| department | string | 院系/部门 |
| phone | string | 联系电话 |
| paperIds | int[] | 投稿稿件 ID 列表（非持久化字段，业务层查询填充） |

### Reviewer（审稿人）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | int | 审稿人唯一标识（自增） |
| name | string | 姓名 |
| email | string | 邮箱 |
| institution | string | 所属机构 |
| department | string | 院系/部门 |
| specialty | string | 专业领域 |
| phone | string | 联系电话 |
| reviewIds | int[] | 审稿记录 ID 列表（非持久化字段，业务层查询填充） |

### SysUser（系统用户）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | int | 用户 ID |
| username | string | 登录用户名 |
| realName | string | 真实姓名 |
| roleId | int | 角色 ID |
| roleCode | string | 角色编码（JOIN 查询填充）：`admin` / `reviewer` / `author` |
| roleName | string | 角色名称（JOIN 查询填充） |
| refId | int \| null | 关联业务表 ID |
| email | string | 邮箱 |
| status | int | 状态：1 启用，0 禁用 |
| createTime | string | 创建时间 |
| dataScope | int | 数据范围：1 全部，2 仅本人 |

### SysMenu（系统菜单）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | int | 菜单 ID |
| menuName | string | 菜单显示名称 |
| menuCode | string | 菜单唯一编码 |
| path | string | 前端路由路径 |
| apiPattern | string | 后端 API 路径模式 |
| icon | string | 图标名称（对应 lucide-react 组件） |
| sortOrder | int | 排序号 |
| status | int | 状态：1 启用，0 禁用 |

---

## 九、Servlet 路由映射总表

| Servlet | URL Pattern | 支持方法 |
|---------|------------|---------|
| AuthServlet | `/api/auth/*` | GET, POST |
| DashboardServlet | `/api/dashboard/*` | GET |
| PaperServlet | `/api/papers/*` | GET, POST, PUT |
| ReviewServlet | `/api/reviews/*` | GET, POST |
| AuthorServlet | `/api/authors/*` | GET |
| ReviewerServlet | `/api/reviewers/*` | GET |
| AssignmentServlet | `/api/assignments/*` | GET, POST |

不支持的 HTTP 方法会返回 `405 Method Not Allowed`（Servlet 容器默认行为）。

---

## 十、测试账号

系统内置以下测试账号，**默认密码统一为 `123456`**：

| 用户名 | 角色 | 关联业务 | 说明 |
|--------|------|---------|------|
| admin | 系统管理员 | — | 拥有全部管理功能 |
| reviewer1 ~ reviewer10 | 审稿人 | reviewer 表 id 1~10 | 仅能审阅被指派的稿件 |
| author1 ~ author15 | 作者 | author 表 id 1~15 | 仅能投稿和查看自己的稿件 |

---

## 十一、注意事项

1. **日期格式**：所有日期字段统一使用 `YYYY-MM-DD` 格式（如 `2026-07-15`），由服务端 `DateUtil.today()` 生成。
2. **关键词存储**：`keywords` 字段在数据库中以 JSON 数组字符串形式存储在 VARCHAR(500) 列中，API 层面以字符串数组形式交互。
3. **稿件状态流转**：`submitted` → `reviewing`（指派审稿人时）→ `accepted`（审稿通过/手动收录）/ `rejected`（审稿退回）→ `published`（手动标记）。状态变更由指派审稿人、提交审稿意见、标记收录等操作自动触发。
4. **数据权限**：审稿人角色的稿件列表接口自动过滤为仅指派给自己的稿件；作者角色自动过滤为仅自己的稿件；管理员可查看全部数据。此过滤仅在列表接口生效，详情接口不做过滤。
5. **全量替换**：`PUT /api/papers/{id}` 为全量替换语义，前端务必传递所有字段（包括 references），否则未传字段会被清空。
6. **Session 维持**：前端必须在 axios 请求中设置 `withCredentials: true`，否则浏览器不会发送 `JSESSIONID` Cookie，导致每次请求都被视为未登录。
7. **CORS 限制**：后端仅允许 `http://localhost:5173` 来源的跨域请求，部署到其他地址时需修改 `CorsFilter` 中的 `Access-Control-Allow-Origin` 配置。
8. **ID 类型**：所有业务实体 ID 均为 int 类型。例外：`GET /api/assignments` 响应中的 `id`、`paperId`、`reviewerId` 为 string 类型（历史设计原因），前端使用时需注意类型转换。
