CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像地址',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0禁用，1启用',
    last_login_time DATETIME DEFAULT NULL COMMENT '最后登录时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sys_user_username (username),
    KEY idx_sys_user_phone (phone),
    KEY idx_sys_user_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    role_code VARCHAR(50) NOT NULL COMMENT '角色编码',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    description VARCHAR(255) DEFAULT NULL COMMENT '角色说明',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sys_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统角色表';

CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sys_user_role (user_id, role_id),
    KEY idx_sys_user_role_role_id (role_id),
    CONSTRAINT fk_sys_user_role_user FOREIGN KEY (user_id) REFERENCES sys_user (id),
    CONSTRAINT fk_sys_user_role_role FOREIGN KEY (role_id) REFERENCES sys_role (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

CREATE TABLE IF NOT EXISTS animal (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    animal_no VARCHAR(50) NOT NULL COMMENT '动物编号',
    name VARCHAR(50) DEFAULT NULL COMMENT '动物名称',
    type VARCHAR(30) NOT NULL COMMENT '动物类型',
    gender VARCHAR(20) DEFAULT NULL COMMENT '性别',
    age_stage VARCHAR(30) DEFAULT NULL COMMENT '年龄阶段',
    color VARCHAR(50) DEFAULT NULL COMMENT '毛色',
    health_status VARCHAR(50) DEFAULT NULL COMMENT '健康状态',
    sterilization_status VARCHAR(50) DEFAULT NULL COMMENT '绝育状态',
    vaccine_status VARCHAR(50) DEFAULT NULL COMMENT '疫苗状态',
    area VARCHAR(100) DEFAULT NULL COMMENT '所在区域',
    status VARCHAR(30) NOT NULL DEFAULT 'WAIT_RESCUE' COMMENT '动物状态',
    description TEXT DEFAULT NULL COMMENT '描述',
    cover_image VARCHAR(255) DEFAULT NULL COMMENT '封面图片',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_animal_no (animal_no),
    KEY idx_animal_status (status),
    KEY idx_animal_type (type),
    KEY idx_animal_area (area),
    KEY idx_animal_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='动物档案表';

CREATE TABLE IF NOT EXISTS animal_image (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    animal_id BIGINT NOT NULL COMMENT '动物ID',
    image_url VARCHAR(255) NOT NULL COMMENT '图片地址',
    image_type VARCHAR(30) NOT NULL COMMENT '图片类型：COVER、DETAIL、RESCUE、HEALTH',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_animal_image_animal_id (animal_id),
    CONSTRAINT fk_animal_image_animal FOREIGN KEY (animal_id) REFERENCES animal (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='动物图片表';

CREATE TABLE IF NOT EXISTS animal_health_record (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    animal_id BIGINT NOT NULL COMMENT '动物ID',
    health_status VARCHAR(50) NOT NULL COMMENT '健康状态',
    hospital VARCHAR(100) DEFAULT NULL COMMENT '医院',
    treatment_content TEXT DEFAULT NULL COMMENT '治疗内容',
    record_time DATETIME NOT NULL COMMENT '记录时间',
    remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_health_record_animal_id (animal_id),
    KEY idx_health_record_record_time (record_time),
    CONSTRAINT fk_health_record_animal FOREIGN KEY (animal_id) REFERENCES animal (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='动物健康记录表';

CREATE TABLE IF NOT EXISTS rescue_clue (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    submitter_id BIGINT NOT NULL COMMENT '提交人ID',
    animal_type VARCHAR(30) NOT NULL COMMENT '动物类型：CAT、DOG、OTHER',
    location VARCHAR(255) NOT NULL COMMENT '发现地点',
    description TEXT NOT NULL COMMENT '线索描述',
    emergency_level VARCHAR(30) NOT NULL COMMENT '紧急程度：LOW、MEDIUM、HIGH',
    image_url VARCHAR(255) DEFAULT NULL COMMENT '现场图片',
    contact VARCHAR(100) DEFAULT NULL COMMENT '联系方式',
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING' COMMENT '线索状态',
    reviewer_id BIGINT DEFAULT NULL COMMENT '审核人ID',
    review_remark VARCHAR(255) DEFAULT NULL COMMENT '审核备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_rescue_clue_submitter_id (submitter_id),
    KEY idx_rescue_clue_status (status),
    KEY idx_rescue_clue_animal_type (animal_type),
    KEY idx_rescue_clue_emergency_level (emergency_level),
    KEY idx_rescue_clue_create_time (create_time),
    CONSTRAINT fk_rescue_clue_submitter FOREIGN KEY (submitter_id) REFERENCES sys_user (id),
    CONSTRAINT fk_rescue_clue_reviewer FOREIGN KEY (reviewer_id) REFERENCES sys_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='救助线索表';

CREATE TABLE IF NOT EXISTS rescue_order (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    clue_id BIGINT DEFAULT NULL COMMENT '来源线索ID',
    title VARCHAR(100) NOT NULL COMMENT '工单标题',
    description TEXT NOT NULL COMMENT '工单描述',
    location VARCHAR(255) NOT NULL COMMENT '地点',
    emergency_level VARCHAR(30) NOT NULL COMMENT '紧急程度：LOW、MEDIUM、HIGH',
    status VARCHAR(30) NOT NULL DEFAULT 'WAIT_ASSIGN' COMMENT '工单状态',
    creator_id BIGINT NOT NULL COMMENT '创建人ID',
    handler_id BIGINT DEFAULT NULL COMMENT '处理人ID',
    process_result TEXT DEFAULT NULL COMMENT '处理结果',
    process_image VARCHAR(255) DEFAULT NULL COMMENT '处理结果图片',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    close_time DATETIME DEFAULT NULL COMMENT '关闭时间',
    PRIMARY KEY (id),
    KEY idx_rescue_order_clue_id (clue_id),
    KEY idx_rescue_order_status (status),
    KEY idx_rescue_order_creator_id (creator_id),
    KEY idx_rescue_order_handler_id (handler_id),
    KEY idx_rescue_order_emergency_level (emergency_level),
    KEY idx_rescue_order_create_time (create_time),
    CONSTRAINT fk_rescue_order_clue FOREIGN KEY (clue_id) REFERENCES rescue_clue (id),
    CONSTRAINT fk_rescue_order_creator FOREIGN KEY (creator_id) REFERENCES sys_user (id),
    CONSTRAINT fk_rescue_order_handler FOREIGN KEY (handler_id) REFERENCES sys_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='救助工单表';

CREATE TABLE IF NOT EXISTS rescue_order_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    order_id BIGINT NOT NULL COMMENT '工单ID',
    operator_id BIGINT DEFAULT NULL COMMENT '操作人ID',
    old_status VARCHAR(30) DEFAULT NULL COMMENT '原状态',
    new_status VARCHAR(30) NOT NULL COMMENT '新状态',
    operation_type VARCHAR(30) NOT NULL COMMENT '操作类型',
    remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_rescue_order_log_order_id (order_id),
    KEY idx_rescue_order_log_operator_id (operator_id),
    KEY idx_rescue_order_log_create_time (create_time),
    CONSTRAINT fk_rescue_order_log_order FOREIGN KEY (order_id) REFERENCES rescue_order (id),
    CONSTRAINT fk_rescue_order_log_operator FOREIGN KEY (operator_id) REFERENCES sys_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工单流转日志表';

CREATE TABLE IF NOT EXISTS volunteer_task (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    title VARCHAR(100) NOT NULL COMMENT '任务标题',
    task_type VARCHAR(30) NOT NULL COMMENT '任务类型',
    description TEXT DEFAULT NULL COMMENT '任务描述',
    location VARCHAR(255) NOT NULL COMMENT '任务地点',
    status VARCHAR(30) NOT NULL DEFAULT 'WAIT_CLAIM' COMMENT '任务状态',
    publisher_id BIGINT NOT NULL COMMENT '发布人ID',
    volunteer_id BIGINT DEFAULT NULL COMMENT '领取志愿者ID',
    start_time DATETIME DEFAULT NULL COMMENT '任务开始时间',
    finish_time DATETIME DEFAULT NULL COMMENT '任务完成时间',
    review_remark VARCHAR(255) DEFAULT NULL COMMENT '审核或取消备注',
    version INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_volunteer_task_status (status),
    KEY idx_volunteer_task_type (task_type),
    KEY idx_volunteer_task_publisher_id (publisher_id),
    KEY idx_volunteer_task_volunteer_id (volunteer_id),
    KEY idx_volunteer_task_create_time (create_time),
    CONSTRAINT fk_volunteer_task_publisher FOREIGN KEY (publisher_id) REFERENCES sys_user (id),
    CONSTRAINT fk_volunteer_task_volunteer FOREIGN KEY (volunteer_id) REFERENCES sys_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='志愿者任务表';

CREATE TABLE IF NOT EXISTS task_record (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    task_id BIGINT NOT NULL COMMENT '任务ID',
    volunteer_id BIGINT NOT NULL COMMENT '志愿者ID',
    content TEXT NOT NULL COMMENT '完成内容',
    image_url VARCHAR(255) DEFAULT NULL COMMENT '完成图片URL',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_task_record_task_id (task_id),
    KEY idx_task_record_volunteer_id (volunteer_id),
    KEY idx_task_record_create_time (create_time),
    CONSTRAINT fk_task_record_task FOREIGN KEY (task_id) REFERENCES volunteer_task (id),
    CONSTRAINT fk_task_record_volunteer FOREIGN KEY (volunteer_id) REFERENCES sys_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务完成记录表';

CREATE TABLE IF NOT EXISTS adoption_application (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    animal_id BIGINT NOT NULL COMMENT '动物ID',
    applicant_id BIGINT NOT NULL COMMENT '申请人ID',
    applicant_name VARCHAR(50) NOT NULL COMMENT '申请人姓名',
    phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    address VARCHAR(255) DEFAULT NULL COMMENT '居住地址',
    reason TEXT NOT NULL COMMENT '申请理由',
    experience TEXT DEFAULT NULL COMMENT '养宠经验',
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING' COMMENT '申请状态',
    review_remark VARCHAR(255) DEFAULT NULL COMMENT '审核备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_adoption_application_animal_id (animal_id),
    KEY idx_adoption_application_applicant_id (applicant_id),
    KEY idx_adoption_application_status (status),
    KEY idx_adoption_application_create_time (create_time),
    KEY idx_adoption_application_applicant_animal (applicant_id, animal_id),
    KEY idx_adoption_application_animal_status (animal_id, status),
    CONSTRAINT fk_adoption_application_animal FOREIGN KEY (animal_id) REFERENCES animal (id),
    CONSTRAINT fk_adoption_application_applicant FOREIGN KEY (applicant_id) REFERENCES sys_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='领养申请表';

CREATE TABLE IF NOT EXISTS adoption_review_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    application_id BIGINT NOT NULL COMMENT '申请ID',
    reviewer_id BIGINT DEFAULT NULL COMMENT '操作人ID',
    old_status VARCHAR(30) DEFAULT NULL COMMENT '原状态',
    new_status VARCHAR(30) NOT NULL COMMENT '新状态',
    operation_type VARCHAR(30) NOT NULL COMMENT '操作类型',
    remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_adoption_review_log_application_id (application_id),
    KEY idx_adoption_review_log_reviewer_id (reviewer_id),
    KEY idx_adoption_review_log_create_time (create_time),
    CONSTRAINT fk_adoption_review_log_application FOREIGN KEY (application_id) REFERENCES adoption_application (id),
    CONSTRAINT fk_adoption_review_log_reviewer FOREIGN KEY (reviewer_id) REFERENCES sys_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='领养审核日志表';

CREATE TABLE IF NOT EXISTS follow_up_record (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    application_id BIGINT NOT NULL COMMENT '领养申请ID',
    animal_id BIGINT NOT NULL COMMENT '动物ID',
    adopter_id BIGINT NOT NULL COMMENT '领养人ID',
    follow_time DATETIME NOT NULL COMMENT '回访时间',
    content TEXT NOT NULL COMMENT '回访内容',
    animal_condition VARCHAR(255) DEFAULT NULL COMMENT '动物当前情况',
    image_url VARCHAR(255) DEFAULT NULL COMMENT '回访图片URL',
    remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_follow_up_application_id (application_id),
    KEY idx_follow_up_animal_id (animal_id),
    KEY idx_follow_up_adopter_id (adopter_id),
    KEY idx_follow_up_follow_time (follow_time),
    KEY idx_follow_up_create_time (create_time),
    CONSTRAINT fk_follow_up_application FOREIGN KEY (application_id) REFERENCES adoption_application (id),
    CONSTRAINT fk_follow_up_animal FOREIGN KEY (animal_id) REFERENCES animal (id),
    CONSTRAINT fk_follow_up_adopter FOREIGN KEY (adopter_id) REFERENCES sys_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='回访记录表';

CREATE TABLE IF NOT EXISTS notification (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '接收用户ID',
    title VARCHAR(100) NOT NULL COMMENT '通知标题',
    content TEXT NOT NULL COMMENT '通知内容',
    type VARCHAR(30) NOT NULL COMMENT '通知类型',
    is_read TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读：0未读，1已读',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    read_time DATETIME DEFAULT NULL COMMENT '阅读时间',
    PRIMARY KEY (id),
    KEY idx_notification_user_id (user_id),
    KEY idx_notification_is_read (is_read),
    KEY idx_notification_type (type),
    KEY idx_notification_create_time (create_time),
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES sys_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统通知表';

CREATE TABLE IF NOT EXISTS file_record (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    original_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
    stored_name VARCHAR(255) NOT NULL COMMENT '存储文件名',
    file_url VARCHAR(255) NOT NULL COMMENT '文件访问URL',
    file_path VARCHAR(500) NOT NULL COMMENT '文件物理路径',
    file_type VARCHAR(50) NOT NULL COMMENT '文件业务类型',
    biz_id BIGINT DEFAULT NULL COMMENT '关联业务ID',
    content_type VARCHAR(100) DEFAULT NULL COMMENT 'MIME类型',
    file_size BIGINT NOT NULL COMMENT '文件大小',
    uploader_id BIGINT NOT NULL COMMENT '上传人ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_file_record_uploader_id (uploader_id),
    KEY idx_file_record_file_type (file_type),
    KEY idx_file_record_biz_id (biz_id),
    KEY idx_file_record_create_time (create_time),
    CONSTRAINT fk_file_record_uploader FOREIGN KEY (uploader_id) REFERENCES sys_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件记录表';
