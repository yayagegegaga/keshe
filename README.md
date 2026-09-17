# 校园流浪动物救助与领养管理系统

这是一个面向校园社区、救助站和公益组织场景的流浪动物救助与领养协同管理平台。项目采用单体后端 + 前后端分离架构，重点覆盖认证授权、RBAC、业务状态流转、事务一致性、审核日志、站内通知、文件上传、Redis 统计缓存和接口文档。

## 技术栈

**后端：**
- Java 17
- Spring Boot 3.x
- Spring Security
- JWT
- MyBatis-Plus
- MySQL 8
- Redis
- Knife4j / OpenAPI
- Maven

**前端：**
- Vue 3
- Vite
- Element Plus
- Axios
- Pinia
- Vue Router

## 项目结构

    backend   后端 Spring Boot 项目
    frontend  前端 Vue 3 项目
    sql       数据库建表和初始化数据
    docs      项目文档

## 已实现模块

- 用户登录注册、JWT 认证、Redis Token 缓存、RBAC 角色权限（4种角色）
- **志愿者注册**（含学号、真实姓名、专业）
- 动物档案管理、动物图片、健康记录，**状态全部中文显示**
- 救助线索审核、救助工单创建/分配/处理/关闭/删除、工单流转日志
- 志愿者任务发布、领取、完成、审核和防重复领取，**志愿者可查看所有任务**
- 领养申请、五阶段审核流转、动物状态同步、领养审核日志，**状态中文显示**
- 站内通知、已读状态、未读数量
- 本地文件上传、文件记录、业务类型和业务 ID 绑定（已整合到动物档案和线索提交）
- 首页统计概览，**按角色显示不同统计卡片，可点击跳转**，Redis 缓存 5 分钟，Redis 不可用时降级查库

## 数据库表

核心表：

    sys_user                 用户（含学号、真实姓名、专业）
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
    notification             站内通知
    file_record              文件记录

> 注：`follow_up_record` 表结构保留，但对应功能已从演示界面移除。

## 核心业务流程

### 用户注册

    普通用户注册 / 志愿者注册
    -> 志愿者注册需填写学号、真实姓名、专业
    -> 注册后分别绑定 USER / VOLUNTEER 角色

### 救助线索到工单

    用户提交救助线索（含图片上传）
    -> 管理员审核线索
    -> 审核通过后创建救助工单
    -> 管理员可分配给志愿者
    -> 志愿者开始处理
    -> 志愿者提交处理结果
    -> 管理员确认关闭
    -> 查看工单流转日志

### 志愿者任务

    管理员发布任务
    -> 志愿者领取任务
    -> 后端通过条件更新防重复领取
    -> 志愿者提交完成记录
    -> 管理员审核任务

### 领养申请

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

## 本地启动

### 1. 创建数据库

    CREATE DATABASE stray_animal DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

### 2. 导入 SQL

    mysql -u root -p stray_animal < sql/schema.sql
    mysql -u root -p stray_animal < sql/data.sql

### 3. 修改配置

编辑 `backend/src/main/resources/application-dev.yml`，按本机环境调整 MySQL 和 Redis。

### 4. 启动 Redis

    host: localhost
    port: 6379
    database: 0

### 5. 启动后端

    cd backend
    mvn clean package -DskipTests
    java -jar target/stray-animal-backend-0.0.1-SNAPSHOT.jar

或：

    mvn spring-boot:run

### 6. 启动前端

    cd frontend
    npm install
    npm run dev

默认地址：

    后端：http://localhost:8080
    前端：http://localhost:5173
    Knife4j：http://localhost:8080/doc.html

## 测试账号

    admin / Admin@123 / SUPER_ADMIN
    volunteer / Volunteer@123 / VOLUNTEER
    user / User@123 / USER

## 文件上传

配置：

    file:
      upload-dir: uploads
      access-prefix: /uploads
      max-size: 10485760

规则：

- 上传接口：`POST /api/files/upload`
- 表单字段：`file`、`biz_type`、可选 `biz_id`
- 允许后缀：`jpg`、`jpeg`、`png`、`gif`、`webp`、`pdf`
- 默认最大 10MB
- 文件按 `uploads/yyyy/MM/dd/` 保存
- 前端只接收文件 URL、原文件名、业务类型、业务 ID、大小、上传时间等安全字段，不返回服务器物理路径
- `/uploads/**` 映射到本地上传目录

## 核心接口

### 认证

    POST /api/auth/register             普通用户注册
    POST /api/auth/register-volunteer   志愿者注册
    POST /api/auth/login
    POST /api/auth/logout
    GET  /api/auth/me

### 动物

    GET    /api/animals
    GET    /api/animals/{id}
    POST   /api/animals
    PUT    /api/animals/{id}
    PUT    /api/animals/{id}/status
    DELETE /api/animals/{id}

### 救助线索与工单

    POST /api/rescue-clues
    GET  /api/rescue-clues
    GET  /api/rescue-clues/my
    PUT  /api/rescue-clues/{id}/review
    PUT  /api/rescue-clues/{id}/cancel

    POST   /api/rescue-orders
    GET    /api/rescue-orders
    GET    /api/rescue-orders/{id}
    PUT    /api/rescue-orders/{id}/assign
    PUT    /api/rescue-orders/{id}/start
    PUT    /api/rescue-orders/{id}/finish
    PUT    /api/rescue-orders/{id}/close
    PUT    /api/rescue-orders/{id}/cancel
    DELETE /api/rescue-orders/{id}
    GET    /api/rescue-orders/{id}/logs

### 志愿任务

    POST   /api/volunteer-tasks
    GET    /api/volunteer-tasks
    GET    /api/volunteer-tasks/my
    PUT    /api/volunteer-tasks/{id}/claim
    PUT    /api/volunteer-tasks/{id}/finish
    PUT    /api/volunteer-tasks/{id}/review
    PUT    /api/volunteer-tasks/{id}/cancel
    DELETE /api/volunteer-tasks/{id}

### 领养申请

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

### 通知、文件、统计

    GET /api/notifications/my
    PUT /api/notifications/{id}/read
    PUT /api/notifications/read-all
    GET /api/notifications/unread-count

    POST   /api/files/upload
    GET    /api/files
    GET    /api/files/my
    DELETE /api/files/{id}

    GET /api/statistics/overview

## 权限规则

- 普通用户只能查看和操作自己的领养申请、通知和文件记录
- 管理员和系统管理员可以查看和管理全局数据
- 志愿者可以查看所有任务，处理分配给自己的工单和任务
- 文件删除允许管理员删除全局文件，普通用户只能删除自己上传的文件
- 未登录返回 401，权限不足返回 403

## 业务流程亮点

- 救助闭环：线索提交、审核、转工单、分配、处理、确认关闭
- 志愿任务闭环：发布、领取、完成、审核，并通过条件更新防止重复领取
- 领养闭环：申请、初审、面谈、试养、成功
- 事务一致性：领养成功会同时更新申请、动物状态、其他申请和审核日志
- Redis 缓存首页统计，用于减少重复聚合查询数据库
- 文件上传包含后缀、大小、业务类型校验，并支持业务 ID 绑定
- 前后端分离，接口统一返回 `Result`，分页统一使用 `PageResult`
- 工单和志愿者任务**支持任意状态删除**（管理员权限）

## Redis 说明

- 登录 Token 缓存：`login:token:{userId}:{token}`
- 首页统计缓存：`statistics:overview:{userId}`
- 统计缓存过期时间：5 分钟
- Redis 不可用时，统计接口会降级查数据库并返回结果

> 注意：登录 token 写入依赖 Redis。若 Redis 未启动，登录会返回明确错误：`Redis 未启动或连接失败，请启动 Redis 后重试登录`。

## 演示流程

### 流程一：用户注册与登录

1. 普通用户注册（`/register`）
2. 切换"志愿者注册"标签，填写学号、真实姓名、专业
3. 使用注册账号登录
4. 观察不同角色展示的不同菜单和统计卡片

### 流程二：动物档案管理与领养

1. 管理员登录，进入"动物管理"，创建动物档案（点击封面上传图片）
2. 修改动物状态（中文显示）
3. 普通用户登录，点击首页"可领养动物"卡片
4. 进入动物列表，查看详情
5. 点击"申请领养"，填写申请表单
6. 管理员"领养审核"，依次推进初审、面谈、试养、成功

### 流程三：救助线索到工单

1. 使用普通用户登录，提交救助线索（含图片上传）
2. 使用管理员登录，查看待审核线索并审核通过，生成救助工单
3. 管理员将工单分配给志愿者
4. 使用志愿者登录，查看工单，开始处理并提交处理结果
5. 管理员确认关闭工单
6. 查看工单日志

### 流程四：志愿者任务

1. 管理员发布志愿者任务
2. 志愿者查看所有任务
3. 志愿者领取任务
4. 再次领取同一任务，应返回业务异常
5. 志愿者提交完成记录
6. 管理员审核通过

## 常见问题

### 登录返回"认证服务异常"

1. 确认 MySQL 密码
2. 修改 `application-dev.yml` 或设置环境变量
3. 确认已创建 `stray_animal` 数据库
4. 重新导入 `sql/schema.sql` 和 `sql/data.sql`
5. 启动 Redis
6. 重启后端

### Redis 未启动

当前系统登录成功后需要写入 `login:token:*`。如果 Redis 未启动，登录会失败并提示 Redis 连接问题。首页统计接口做了降级，Redis 不可用时仍会查数据库返回。

### schema.sql 已导入过，又新增字段怎么办

若已有旧库，建议开发环境直接重建数据库后重新导入：

    DROP DATABASE stray_animal;
    CREATE DATABASE stray_animal DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

再执行：

    mysql -u root -p stray_animal < sql/schema.sql
    mysql -u root -p stray_animal < sql/data.sql

## 后续优化方向

- 操作日志 AOP 和审计追踪
- 文件存储扩展为 MinIO
- 更细粒度的权限模型和菜单权限
- 前端路由懒加载与构建产物拆包
- 更完善的自动化测试和接口测试集合