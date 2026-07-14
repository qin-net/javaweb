# 大黑山大学学报期刊投稿管理系统

## 项目简介

本系统是大黑山大学学报编辑部使用的期刊投稿管理平台，面向作者投稿、审稿人评审、编辑部管理三大角色，覆盖稿件从投稿、指派审稿人、审稿、录用到发表的完整生命周期。系统支持工学版、理学版、文科版、生物医学版四个学报版本的稿件管理。

前端基于 Vite + React + TypeScript 构建，后端采用 Java Servlet + JDBC + MySQL，通过 RESTful API 进行前后端交互。

---

## 技术栈

| 类别 | 技术 |
|------|------|
| 语言 | Java 8 |
| Web 容器 | Apache Tomcat 9.x |
| 数据库 | MySQL 8.0 |
| JDBC 驱动 | mysql-connector-java 8.0.33 |
| JSON 处理 | Gson 2.10.1 |
| Servlet 规范 | Servlet 4.0 (javax.servlet-api 4.0.1) |
| 构建工具 | Apache Maven 3.6+ |
| 打包方式 | WAR |

---

## 项目架构

项目严格遵循五层架构设计，各层职责分明、依赖方向单一（上层依赖下层，禁止反向依赖）：

```
com.journal
├── model/            第一层 — 实体模型层（纯 POJO，零依赖）
├── interfacemodel/   第二层 — 业务接口层（定义契约，只引用 model）
├── dao/              第三层 — 数据访问层（JDBC 操作，引用 model + util）
├── impl/             第四层 — 接口实现层（实现 interface，委托 dao）
├── service/          第五层 — 业务服务层（编排业务流程，调用 impl）
├── web/              控制层 — Servlet + Filter（接收 HTTP 请求，调用 service）
├── util/             工具层 — DBUtil / DateUtil / JsonUtil
└── exception/        异常层 — BusinessException
```

---

## 数据库表结构

系统共 5 张表：

| 表名 | 说明 |
|------|------|
| `author` | 作者信息（姓名、邮箱、所属机构、院系、电话） |
| `reviewer` | 审稿人信息（姓名、邮箱、机构、院系、专业领域、电话） |
| `manuscript` | 稿件信息（标题、摘要、关键词、正文、期刊版本、状态、各阶段日期、指派审稿人） |
| `reference_lit` | 参考文献（关联稿件，含标题、作者、期刊、年份、卷期页码） |
| `review_record` | 审稿记录（关联稿件和审稿人，含审稿决定和审稿意见） |

---

## API 接口一览

所有接口以 `/api` 为前缀，响应格式统一为 `{"code": 200, "message": "success", "data": ...}`。

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/api/dashboard/stats` | 获取仪表盘统计数据 |
| GET | `/api/papers` | 获取稿件列表（支持分页、筛选） |
| GET | `/api/papers/{id}` | 获取稿件详情（含参考文献） |
| POST | `/api/papers` | 投稿新稿件 |
| PUT | `/api/papers/{id}` | 更新稿件内容 |
| PUT | `/api/papers/{id}/status` | 更新稿件状态 |
| GET | `/api/reviews` | 获取审稿记录（支持按稿件/审稿人筛选） |
| POST | `/api/reviews` | 提交审稿意见 |
| GET | `/api/authors` | 获取作者列表（支持关键词搜索） |
| GET | `/api/authors/{id}` | 获取作者详情 |
| GET | `/api/reviewers` | 获取审稿人列表（支持关键词搜索） |
| GET | `/api/reviewers/{id}` | 获取审稿人详情 |
| GET | `/api/assignments` | 获取审稿指派列表 |
| POST | `/api/assignments` | 创建审稿指派 |

---

## 环境准备

在启动项目之前，请确保以下环境已安装就绪：

### 1. JDK 8

```bash
java -version
# 输出应包含 1.8.x
```

如未安装，前往 [Oracle JDK 8](https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html) 或使用 OpenJDK 安装。

### 2. Apache Maven 3.6+

```bash
mvn -version
# 输出应包含 Apache Maven 3.6.x 或更高
```

### 3. MySQL 8.0

```bash
mysql --version
# 输出应包含 8.0.x
```

确保 MySQL 服务已启动，并且可以使用 root 账户登录。

### 4. Apache Tomcat 9.x

**下载与安装**

前往 [Tomcat 9 官方下载页](https://tomcat.apache.org/download-90.cgi)，在 Binary Distributions → Core 下选择对应系统的压缩包：macOS / Linux 选 `tar.gz`，Windows 选 `zip`。下载后解压到你喜欢的位置即可，无需安装，以下将解压路径记为 `CATALINA_HOME`。

```bash
# macOS / Linux 示例
cd ~/devtools
tar -xzf apache-tomcat-9.0.93.tar.gz
export CATALINA_HOME=~/devtools/apache-tomcat-9.0.93
```

```powershell
# Windows 示例（PowerShell）
# 解压 zip 到 D:\devtools\apache-tomcat-9.0.93
$env:CATALINA_HOME = "D:\devtools\apache-tomcat-9.0.93"
```

**配置 UTF-8 编码**

打开 `$CATALINA_HOME/conf/server.xml`，找到 HTTP Connector（默认端口 8080），添加 `URIEncoding="UTF-8"` 属性，确保 URL 中的中文参数不会乱码：

```xml
<Connector port="8080" protocol="HTTP/1.1"
           connectionTimeout="20000"
           redirectPort="8443"
           URIEncoding="UTF-8" />
```

**验证 Tomcat 可正常启动**

```bash
# macOS / Linux
$CATALINA_HOME/bin/startup.sh

# Windows
%CATALINA_HOME%\bin\startup.bat
```

浏览器访问 `http://localhost:8080`，看到 Tomcat 欢迎页说明安装成功。验证完毕后先关掉：

```bash
$CATALINA_HOME/bin/shutdown.sh       # macOS / Linux
%CATALINA_HOME%\bin\shutdown.bat     # Windows
```

---

## 快速启动

### 第一步：创建数据库并建表

```bash
# 登录 MySQL
mysql -u root -p

# 执行建表脚本（在 MySQL 命令行中）
source /你的项目路径/src/main/resources/schema.sql
```

或者一条命令搞定：

```bash
mysql -u root -p < src/main/resources/schema.sql
```

### 第二步：导入测试数据

```bash
mysql -u root -p < src/main/resources/init-data.sql
```

导入后数据库中将包含 15 位作者、10 位审稿人、40 篇稿件（含参考文献）、30 条审稿记录。

### 第三步：修改数据库连接配置（如有需要）

如果你的 MySQL 用户名或密码不是 `root/root`，需要修改配置文件：

```
src/main/java/com/journal/util/DBUtil.java
```

找到以下常量并修改为你的实际值：

```java
private static final String URL =
        "jdbc:mysql://localhost:3306/journal_submission_system"
                + "?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai";
private static final String USERNAME = "root";
private static final String PASSWORD = "root";
```

如果 MySQL 运行在非默认端口，同步修改 URL 中的 `3306`。

### 第四步：Maven 编译打包

```bash
cd /你的项目路径

# 编译（验证代码无误）
mvn compile

# 打包为 WAR 文件
mvn package -DskipTests
```

打包成功后会在 `target/` 目录下生成 `journal-system.war`。

### 第五步：部署到 Tomcat

提供三种部署方式，根据你的使用场景任选其一。

**方式一：命令行部署 WAR 包（推荐用于服务器 / 演示）**

```bash
# 1. 将打包好的 WAR 复制到 Tomcat 的 webapps 目录
cp target/journal-system.war $CATALINA_HOME/webapps/

# 2. 启动 Tomcat
$CATALINA_HOME/bin/startup.sh      # macOS / Linux
%CATALINA_HOME%\bin\startup.bat    :: Windows
```

Tomcat 启动后会自动将 `journal-system.war` 解压为 `$CATALINA_HOME/webapps/journal-system/` 目录，无需手动解压。后端访问地址为 `http://localhost:8080/journal-system/api/...`。

如果需要停止 Tomcat：

```bash
$CATALINA_HOME/bin/shutdown.sh     # macOS / Linux
%CATALINA_HOME%\bin\shutdown.bat   :: Windows
```

如果需要查看实时日志排查问题：

```bash
tail -f $CATALINA_HOME/logs/catalina.out          # macOS / Linux
type %CATALINA_HOME%\logs\catalina.out             :: Windows
```

**方式二：IntelliJ IDEA 中配置 Tomcat（推荐用于开发调试）**

第 1 步 — 打开运行配置：菜单栏点击 Run → Edit Configurations，弹出配置窗口后点击左上角的 `+` 号，在列表中找到 Tomcat Server → Local，点击添加。

第 2 步 — 配置 Server 选项卡：在 Application server 一栏点击 Configure，选择你的 Tomcat 安装目录（即 `CATALINA_HOME` 路径），IDEA 会自动识别版本。下方 Open browser 可以勾选，URL 填写 `http://localhost:8080/journal-system/`，这样启动后会自动打开浏览器。

第 3 步 — 配置 Deployment 选项卡：切换到 Deployment 选项卡，点击右侧的 `+` 号 → Artifact，在弹出列表中选择 `journal-submission-system:war exploded`（exploded 模式支持热部署，修改代码后无需重新打包）。下方的 Application context 确认为 `/journal-system`。

第 4 步 — 启动：点击 Apply 保存配置，然后点击绿色三角形按钮（Run）或小虫子按钮（Debug）启动。IDEA 控制台会输出 Tomcat 日志，看到 `Deployment of web application directory [...journal-system] has finished` 即部署成功。

第 5 步 — 热更新（可选）：在 Server 选项卡的 On 'Update' action 和 On frame deactivation 中，可以选择 Update classes and resources，这样修改 Java 代码或静态资源后，切换窗口即可自动热部署，无需手动重启。

**方式三：Eclipse 中配置 Tomcat**

第 1 步 — 添加 Server Runtime：菜单栏 Window → Preferences → Server → Runtime Environments → Add，选择 Apache Tomcat v9.0，指定 Tomcat 安装目录，点击 Finish。

第 2 步 — 创建 Server 实例：切换到 Servers 视图（Window → Show View → Servers），在空白处右键 → New → Server，选择刚才配置的 Tomcat v9.0，点击 Next。

第 3 步 — 部署项目：在 Add and Remove 界面，将左侧的 `journal-submission-system` 添加到右侧 Configured 列表中，点击 Finish。

第 4 步 — 启动：在 Servers 视图中右键 Tomcat → Start（或 Debug），启动完成后访问 `http://localhost:8080/journal-system/api/dashboard/stats` 验证。

### 第六步：验证启动

后端启动后，在浏览器或命令行中访问：

```bash
curl http://localhost:8080/journal-system/api/dashboard/stats
```

如果返回类似以下 JSON，说明后端已正常运行：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalSubmissions": 40,
    "underReview": 10,
    "accepted": 5,
    "published": 10,
    "byJournal": [...],
    "monthlyTrend": [...],
    "recentPapers": [...]
  }
}
```

### 第七步：启动前端（可选）

```bash
cd frontend
npm install
npm run dev
```

前端默认运行在 `http://localhost:5173`，API 请求会代理到后端的 `http://localhost:8080/journal-system/api`。

如果前端代理配置与后端部署路径不一致，需要修改 `frontend/vite.config.ts` 中的 proxy target。

---

## 项目文件清单

```
javahomework/
├── pom.xml                                 Maven 项目配置
├── README.md                               本文件
├── 编码规范要求文档.md                        编码规范说明
│
├── frontend/                               前端项目（Vite + React + TypeScript）
│   ├── package.json
│   ├── vite.config.ts
│   └── src/
│       ├── pages/                          页面组件
│       ├── components/                     公共组件
│       ├── services/api.ts                 API 调用封装
│       ├── types/index.ts                  TypeScript 类型定义
│       └── mock/                           模拟数据
│
└── src/main/
    ├── java/com/journal/
    │   ├── model/                          实体模型（7 个类）
    │   │   ├── ManuscriptStatus.java       稿件状态常量
    │   │   ├── Journal.java                期刊版本常量
    │   │   ├── Author.java                 作者实体
    │   │   ├── Reviewer.java               审稿人实体
    │   │   ├── Reference.java              参考文献实体
    │   │   ├── Manuscript.java             稿件实体
    │   │   └── ReviewRecord.java           审稿记录实体
    │   │
    │   ├── interfacemodel/                 业务接口（6 个接口）
    │   │   ├── ManuscriptInterface.java    稿件数据操作接口
    │   │   ├── SubmissionInterface.java    投稿操作接口
    │   │   ├── ReviewInterface.java        审稿操作接口
    │   │   ├── AuthorInterface.java        作者数据操作接口
    │   │   ├── ReviewerInterface.java      审稿人数据操作接口
    │   │   └── QueryInterface.java         统计查询接口
    │   │
    │   ├── dao/                            数据访问层（7 个类）
    │   │   ├── ManuscriptDAO.java          稿件 DAO（动态条件查询）
    │   │   ├── SubmissionDAO.java          投稿 DAO（事务管理）
    │   │   ├── ReviewDAO.java              审稿记录 DAO
    │   │   ├── AuthorDAO.java              作者 DAO
    │   │   ├── ReviewerDAO.java            审稿人 DAO
    │   │   ├── QueryDAO.java               统计查询 DAO
    │   │   └── ReferenceDAO.java           参考文献 DAO
    │   │
    │   ├── impl/                           接口实现层（6 个类）
    │   │   ├── ManuscriptImpl.java
    │   │   ├── SubmissionImpl.java          含参数校验
    │   │   ├── ReviewImpl.java
    │   │   ├── AuthorImpl.java
    │   │   ├── ReviewerImpl.java
    │   │   └── QueryImpl.java
    │   │
    │   ├── service/                        业务服务层（5 个类）
    │   │   ├── ManuscriptService.java      稿件全流程管理
    │   │   ├── ReviewService.java          审稿流程（含状态自动流转）
    │   │   ├── AuthorService.java          作者及投稿关联
    │   │   ├── ReviewerService.java        审稿人及审稿关联
    │   │   └── DashboardService.java       仪表盘数据聚合
    │   │
    │   ├── web/                            Servlet 控制层（9 个类）
    │   │   ├── CorsFilter.java             跨域过滤器
    │   │   ├── EncodingFilter.java         编码过滤器
    │   │   ├── BaseServlet.java            Servlet 抽象基类
    │   │   ├── DashboardServlet.java       仪表盘接口
    │   │   ├── PaperServlet.java           稿件接口（CRUD + 分页）
    │   │   ├── ReviewServlet.java          审稿接口
    │   │   ├── AuthorServlet.java          作者接口
    │   │   ├── ReviewerServlet.java        审稿人接口
    │   │   └── AssignmentServlet.java      指派接口
    │   │
    │   ├── util/                           工具类（3 个类）
    │   │   ├── DBUtil.java                 数据库连接管理
    │   │   ├── DateUtil.java               日期处理
    │   │   └── JsonUtil.java               JSON 序列化/响应封装
    │   │
    │   └── exception/                      异常类（1 个类）
    │       └── BusinessException.java      业务异常
    │
    ├── resources/
    │   ├── schema.sql                      数据库建表脚本
    │   └── init-data.sql                   测试数据初始化脚本
    │
    └── webapp/WEB-INF/
        └── web.xml                         Servlet 与 Filter 映射配置
```

后端共计 **44 个 Java 源文件**，覆盖 8 个包。

---

## 常见问题

**Q: 启动后访问接口返回 404？**

检查 WAR 包是否部署成功。在浏览器中访问 `http://localhost:8080/journal-system/`，如果 Tomcat 页面正常显示说明容器没问题，再检查 `/api` 路径是否正确。

**Q: 访问接口返回数据库连接错误？**

确认 MySQL 服务已启动，并检查 `DBUtil.java` 中的连接参数（地址、端口、用户名、密码）是否与你的环境一致。

**Q: 中文乱码？**

系统已通过 `EncodingFilter` 统一设置 UTF-8 编码，同时数据库建表脚本指定了 `utf8mb4` 字符集。如仍有乱码，检查 MySQL 的 `character_set_server` 配置和 Tomcat 的 `server.xml` 中 Connector 的 `URIEncoding="UTF-8"` 设置。

**Q: 前端请求后端出现跨域错误？**

系统已通过 `CorsFilter` 处理跨域。如果你修改了后端部署路径，确保前端 `vite.config.ts` 中的 proxy target 与之一致。
