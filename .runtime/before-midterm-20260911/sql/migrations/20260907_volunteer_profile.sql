-- Run once against an existing database before starting the updated backend.
-- Fresh databases already include these columns in schema.sql.
SET NAMES utf8mb4;
ALTER TABLE sys_user
    ADD COLUMN student_id VARCHAR(50) DEFAULT NULL COMMENT '志愿者学号',
    ADD COLUMN real_name VARCHAR(50) DEFAULT NULL COMMENT '志愿者真实姓名',
    ADD COLUMN major VARCHAR(100) DEFAULT NULL COMMENT '志愿者专业';
