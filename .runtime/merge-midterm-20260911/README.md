# 校园流浪动物救助与领养管理系统

这是一个面向校园社区、救助站和公益组织场景的流浪动物救助与领养协同管理平台。项目采用单体后端 + 前后端分离架构，重点覆盖认证授权、RBAC、业务状态流转、事务一致性、审核日志、回访闭环、站内通知、文件上传、Redis 统计缓存和接口文档。

## 技术栈

后端：

- Java 17
- Spring Boot 3.x
- Spring Security
- JWT
- MyBatis-Plus
- MySQL 8
- Redis
- Knife4j / OpenAPI
- Maven

前端：

- Vue 3
- Vite
- Element Plus
- Axios
- Pinia
- Vue Router

## 项目结构

```text
backend   后端 Spring Boot 项目
frontend  前端 Vue 3 项目
sql       数据库建表和初始化数据
docs      项目文档
```

## 已实现模块

- 用户登录注册、JWT 认证、Redis Token 缓存、RBAC 角色权限
- 动物档案管理、动物图片、健康记录
- 救助线索审核、救助工单分配、工单状态流转日志
- 志愿者任务发布、领取、完成、审核和防重复领取
- 领养申请、审核流转、动物状态同步、领养审核日志
- 领养成功后的回访记录
- 站内通知、已读状态、未读数量
- 本地文件上传、文件记录、业务类型和业务 ID 绑定
- 首页统计概览，Redis 缓存 5 分钟，Redis 不可用时降级查库

## 数据库表

核心表：

```text
sys_user                 用户
sys_role                 角色
sys_user_role            用户角色关联
animal                   动物档案
animal_image             动物图片
animal_health_record     健康记录
rescue_clue              救助线索
rescue_order             救助工单
rescue_order_log         工单流转日志
volunteer_task           志愿者任务
task_record              任务完成记录
adoption_application     领养申请
adoption_review_log      领养审核日志
follow_up_record         回访记录
notification             站内通知
file_record              文件记录
```

`file_record` 支持 `biz_id`，用于将文件绑定到具体业务数据，例如动物、任务、回访或工单。

## 核心业务流程

### 救助线索到工单

```text
用户提交救助线索
-> 管理员审核线索
-> 审核通过后创建救助工单
-> 管理员分配给志愿者
-> 志愿者开始处理
-> 志愿者提交处理结果
-> 管理员确认关闭
-> 查看工单流转日志
```

### 志愿者任务

```text
管理员发布任务
-> 志愿者领取任务
-> 后端通过条件更新防重复领取
-> 志愿者提交完成记录
-> 管理员审核任务
```

### 领养申请

```text
用户查看可领养动物
-> 提交领养申请
-> 动物状态 ADOPTABLE -> APPLYING
-> 管理员初审
-> 面谈
-> 试养
-> 动物状态 APPLYING -> TRIAL
-> 领养成功
-> 动物状态 TRIAL -> ADOPTED
-> 写入审核日志
-> 后续回访
```

## 本地启动

1. 创建数据库：

```sql
CREATE DATABASE stray_animal DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 导入 SQL：

```bash
mysql -u root -p stray_animal < sql/schema.sql
mysql -u root -p stray_animal < sql/data.sql
```

3. 修改配置：

编辑 `backend/src/main/resources/application-dev.yml`，按本机环境调整 MySQL 和 Redis。

也可以用环境变量覆盖：

```powershell
$env:MYSQL_URL="jdbc:mysql://localhost:3306/stray_animal?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true"
$env:MYSQL_USERNAME="root"
$env:MYSQL_PASSWORD="你的MySQL密码"
$env:REDIS_HOST="localhost"
$env:REDIS_PORT="6379"
```

4. 启动 Redis：

```text
host: localhost
port: 6379
database: 0
```

5. 启动后端：

```bash
cd backend
mvn spring-boot:run
```

6. 启动前端：

```bash
cd frontend
npm install
npm run dev
```

默认地址：

```text
后端：http://localhost:8080
前端：http://localhost:5173
Knife4j：http://localhost:8080/doc.html
OpenAPI：http://localhost:8080/v3/api-docs
```

## 测试账号

```text
admin / Admin@123 / SUPER_ADMIN
volunteer / Volunteer@123 / VOLUNTEER
user / User@123 / USER
```

## 文件上传

配置：

```yaml
file:
  upload-dir: uploads
  access-prefix: /uploads
  max-size: 10485760
```

规则：

- 上传接口：`POST /api/files/upload`
- 表单字段：`file`、`biz_type`、可选 `biz_id`
- 允许后缀：`jpg`、`jpeg`、`png`、`gif`、`webp`、`pdf`
- 默认最大 10MB
- 文件按 `uploads/yyyy/MM/dd/` 保存
- 前端只接收文件 URL、原文件名、业务类型、业务 ID、大小、上传时间等安全字段，不返回服务器物理路径
- `/uploads/**` 映射到本地上传目录

## 核心接口

认证：

```text
POST /api/auth/register
POST /api/auth/login
POST /api/auth/logout
GET  /api/auth/me
```

动物：

```text
GET    /api/animals
GET    /api/animals/{id}
POST   /api/animals
PUT    /api/animals/{id}
PUT    /api/animals/{id}/status
DELETE /api/animals/{id}
```

救助线索与工单：

```text
POST /api/rescue-clues
GET  /api/rescue-clues
GET  /api/rescue-clues/my
PUT  /api/rescue-clues/{id}/review
PUT  /api/rescue-clues/{id}/cancel

POST /api/rescue-orders
GET  /api/rescue-orders
GET  /api/rescue-orders/{id}
PUT  /api/rescue-orders/{id}/assign
PUT  /api/rescue-orders/{id}/start
PUT  /api/rescue-orders/{id}/finish
PUT  /api/rescue-orders/{id}/close
PUT  /api/rescue-orders/{id}/cancel
GET  /api/rescue-orders/{id}/logs
```

志愿任务：

```text
POST   /api/volunteer-tasks
GET    /api/volunteer-tasks
GET    /api/volunteer-tasks/my
PUT    /api/volunteer-tasks/{id}/claim
PUT    /api/volunteer-tasks/{id}/finish
PUT    /api/volunteer-tasks/{id}/review
PUT    /api/volunteer-tasks/{id}/cancel
DELETE /api/volunteer-tasks/{id}
```

领养申请：

```text
POST /api/adoptions
GET  /api/adoptions
GET  /api/adoptions/my
GET  /api/adoptions/{id}
PUT  /api/adoptions/{id}/first-approve
PUT  /api/adoptions/{id}/interview
PUT  /api/adoptions/{id}/trial
PUT  /api/adoptions/{id}/success
PUT  /api/adoptions/{id}/reject
PUT  /api/adoptions/{id}/trial-failed
PUT  /api/adoptions/{id}/cancel
GET  /api/adoptions/{id}/logs
```

回访、通知、文件、统计：

```text
POST   /api/follow-ups
GET    /api/follow-ups
GET    /api/follow-ups/my
GET    /api/follow-ups/{id}
DELETE /api/follow-ups/{id}

GET /api/notifications/my
PUT /api/notifications/{id}/read
PUT /api/notifications/read-all
GET /api/notifications/unread-count

POST   /api/files/upload
GET    /api/files
GET    /api/files/my
DELETE /api/files/{id}

GET /api/statistics/overview
```

## 权限规则

- 普通用户只能查看和操作自己的领养申请、回访记录、通知和文件记录
- 管理员和系统管理员可以查看和管理全局数据
- 志愿者可以处理分配给自己的工单和任务
- 文件删除允许管理员删除全局文件，普通用户只能删除自己上传的文件
- 未登录返回 401，权限不足返回 403

## 业务流程亮点

- 救助闭环：线索提交、审核、转工单、分配、处理、确认关闭
- 志愿任务闭环：发布、领取、完成、审核，并通过条件更新防止重复领取
- 领养闭环：申请、初审、面谈、试养、成功、回访
- 事务一致性：领养成功会同时更新申请、动物状态、其他申请和审核日志
- Redis 缓存首页统计，用于减少重复聚合查询数据库，不是高并发包装
- 文件上传包含后缀、大小、业务类型校验，并支持业务 ID 绑定
- 前后端分离，接口统一返回 `Result`，分页统一使用 `PageResult`

## Redis 说明

- 登录 Token 缓存：`login:token:{userId}:{token}`
- 首页统计缓存：`statistics:overview:{userId}`
- 统计缓存过期时间：5 分钟
- Redis 不可用时，统计接口会降级查数据库并返回结果

注意：登录 token 写入依赖 Redis。若 Redis 未启动，登录会返回明确错误：`Redis 未启动或连接失败，请启动 Redis 后重试登录`。

## 演示流程

### 流程一：救助线索到救助工单

1. 使用 `user / User@123` 登录。
2. 提交救助线索。
3. 使用 `admin / Admin@123` 登录。
4. 查看待审核线索并审核通过，生成救助工单。
5. 管理员将工单分配给 `volunteer`。
6. 使用 `volunteer / Volunteer@123` 登录。
7. 查看自己的工单，开始处理并提交处理结果。
8. 管理员确认关闭工单。
9. 查看工单日志，确认每一步状态变化都有记录。

### 流程二：志愿者任务

1. 管理员发布志愿者任务。
2. 志愿者查看待领取任务。
3. 志愿者领取任务。
4. 再次领取同一任务，应返回业务异常。
5. 志愿者提交完成记录。
6. 管理员审核通过，任务进入 `REVIEWED`。

### 流程三：领养申请

1. 用户查看 `ADOPTABLE` 动物。
2. 用户提交领养申请。
3. 动物状态变为 `APPLYING`。
4. 管理员依次推进初审、面谈、试养。
5. 动物状态变为 `TRIAL`。
6. 管理员确认领养成功。
7. 动物状态变为 `ADOPTED`。
8. 查看 `adoption_review_log` 审核记录。

### 流程四：回访、通知、文件、统计

1. 管理员为 `SUCCESS` 申请新增回访。
2. 用户查看自己的回访记录。
3. 用户查看站内通知并标记已读。
4. 登录用户上传文件并绑定业务 ID。
5. 普通用户查看自己的文件，管理员查看全局文件。
6. 首页统计面板展示统计数据。
7. Redis 中可看到 `statistics:overview:{userId}` 缓存。

## 常见问题

### 登录返回“认证服务异常”

本地已排查到一次真实原因：`application-dev.yml` 默认配置为 `root/root`，但当前 MySQL 拒绝该账号密码，日志堆栈为：

```text
java.sql.SQLException: Access denied for user 'root'@'localhost' (using password: YES)
```

修复方式：

1. 确认 MySQL 密码。
2. 修改 `application-dev.yml` 或设置 `MYSQL_USERNAME`、`MYSQL_PASSWORD` 环境变量。
3. 确认已创建 `stray_animal` 数据库。
4. 重新导入 `sql/schema.sql` 和 `sql/data.sql`。
5. 启动 Redis。
6. 重启后端。

### Redis 未启动

当前系统登录成功后需要写入 `login:token:*`。如果 Redis 未启动，登录会失败并提示 Redis 连接问题。首页统计接口做了降级，Redis 不可用时仍会查数据库返回。

### schema.sql 已导入过，又新增字段怎么办

当前 SQL 更适合从零初始化。若已有旧库，建议开发环境直接重建数据库后重新导入：

```sql
DROP DATABASE stray_animal;
CREATE DATABASE stray_animal DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

再执行：

```bash
mysql -u root -p stray_animal < sql/schema.sql
mysql -u root -p stray_animal < sql/data.sql
```

## 面试讲解材料

详见 [docs/interview.md](docs/interview.md)。

## 后续优化方向

- 操作日志 AOP 和审计追踪
- 文件存储扩展为 MinIO
- 更细粒度的权限模型和菜单权限
- 前端路由懒加载与构建产物拆包
- 更完善的自动化测试和接口测试集合
