# 分阶段开发计划

## 第一阶段：最小可运行项目骨架

- 后端 Spring Boot 3.x + Maven 基础项目
- MySQL 与 MyBatis-Plus 基础配置
- 统一响应 `Result`
- 分页响应 `PageResult`
- 统一异常处理
- Knife4j / OpenAPI 文档
- 健康检查接口 `/api/health`
- 前端 Vue 3 + Vite 基础骨架

## 第二阶段：认证与权限

- 用户注册登录
- Spring Security
- JWT
- RBAC 角色权限
- 初始化管理员账号接入登录流程

## 第三阶段：核心动物档案

- 动物档案增删改查
- 动物分页查询和条件筛选
- 动物状态管理

## 第四阶段：救助线索与救助工单

- 线索提交和审核
- 工单创建、分配、处理、关闭
- 工单状态流转校验
- 工单流转日志
