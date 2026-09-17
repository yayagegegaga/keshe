-- 一次性清理旧的志愿者任务数据，不修改表结构和业务功能。
-- 执行后可重新导入新的志愿者任务数据。
SET NAMES utf8mb4;

START TRANSACTION;

-- 先清理依赖任务主表的完成记录，再清理任务专属附件和通知。
DELETE FROM task_record;
DELETE FROM file_record WHERE file_type = 'TASK_IMAGE';
DELETE FROM notification WHERE type = 'TASK';
DELETE FROM volunteer_task;

COMMIT;
