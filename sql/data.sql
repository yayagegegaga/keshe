SET NAMES utf8mb4;
INSERT INTO sys_role (id, role_code, role_name, description)
VALUES
    (1, 'SUPER_ADMIN', '系统管理员', '拥有系统最高管理权限'),
    (2, 'ADMIN', '救助站管理员', '负责线索审核、工单分配、动物和领养管理'),
    (3, 'USER', '普通用户', '提交救助线索和领养申请'),
    (4, 'VOLUNTEER', '志愿者', '处理救助工单和志愿者任务')
ON DUPLICATE KEY UPDATE
    role_name = VALUES(role_name),
    description = VALUES(description);

-- password: Admin@123
INSERT INTO sys_user (id, username, password, nickname, phone, email, status)
VALUES
    (1, 'admin', '$2a$10$5M1HJMdyJw4EgEthKEm7/O2fsAsjqL0UNWlsOJlFMF8RB5tDCFxwq', '超级管理员', '13800000000', 'admin@example.com', 1)
ON DUPLICATE KEY UPDATE
    password = VALUES(password),
    nickname = VALUES(nickname),
    phone = VALUES(phone),
    email = VALUES(email),
    status = VALUES(status);

INSERT INTO sys_user_role (user_id, role_id)
VALUES (1, 1)
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

-- password: Volunteer@123
INSERT INTO sys_user (id, username, password, nickname, phone, email, status)
VALUES
    (2, 'volunteer', '$2a$10$908hel0J5WCpg7D6b8VS0.cUKk4j8RFKYoXRgfynAtLKwbUuQ6vsK', '志愿者测试账号', '13800000002', 'volunteer@example.com', 1)
ON DUPLICATE KEY UPDATE
    password = VALUES(password),
    nickname = VALUES(nickname),
    phone = VALUES(phone),
    email = VALUES(email),
    status = VALUES(status);

-- password: User@123
INSERT INTO sys_user (id, username, password, nickname, phone, email, status)
VALUES
    (3, 'user', '$2a$10$rhZIFvXpYU7K0VQPy.AkXuOXZMFqx4GvSUtyk/8oqjhmPFGp/p502', '普通用户测试账号', '13800000003', 'user@example.com', 1)
ON DUPLICATE KEY UPDATE
    password = VALUES(password),
    nickname = VALUES(nickname),
    phone = VALUES(phone),
    email = VALUES(email),
    status = VALUES(status);

INSERT INTO sys_user_role (user_id, role_id)
VALUES
    (2, 4),
    (3, 3)
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

INSERT INTO animal (
    id, animal_no, name, type, gender, age_stage, color, health_status,
    sterilization_status, vaccine_status, area, status, description, cover_image
)
VALUES
    (1, 'A-CAT-0001', '小橘', 'CAT', 'MALE', 'YOUNG', '橘色', '健康', 'UNKNOWN', '已接种基础疫苗', '一食堂后门', 'ADOPTABLE', '常在校园流浪，亲人活泼，适合同学领养。', '/mock/animals/xiaoju-cover.png'),
    (2, 'A-CAT-0002', '小黑', 'CAT', 'FEMALE', 'ADULT', '黑色', '观察中', 'UNKNOWN', 'UNKNOWN', '3号宿舍楼下', 'OBSERVING', '刚由同学上报救助入站，正在观察饮食和精神状态。', '/mock/animals/xiaohei-cover.png'),
    (3, 'A-CAT-0003', '花花', 'CAT', 'UNKNOWN', 'BABY', '三花', '治疗中', 'UNKNOWN', '未接种', '图书馆北侧', 'TREATING', '有轻微皮肤问题，正在校医院治疗。', '/mock/animals/huahua-cover.png'),
    (4, 'A-DOG-0001', '旺财', 'DOG', 'MALE', 'ADULT', '黄色', '待检查', 'UNKNOWN', 'UNKNOWN', '操场看台旁', 'WAIT_RESCUE', '有同学上报，仍在操场附近等待救助。', '/mock/animals/wangcai-cover.png'),
    (5, 'A-CAT-0004', '小白', 'CAT', 'FEMALE', 'OLD', '白色', '稳定', '已绝育', '已接种', '教学楼A栋西侧', 'ADOPTED', '已完成领养，后续将进入回访流程。', '/mock/animals/xiaobai-cover.png'),
    (6, 'A-DOG-0002', '豆包', 'DOG', 'FEMALE', 'YOUNG', '棕白', '健康', 'UNKNOWN', '已接种基础疫苗', '实验楼B座后面', 'ADOPTABLE', '性格温顺，适合提交领养申请测试。', '/mock/animals/doubao-cover.png'),
    (7, 'A-CAT-0005', '奶糖', 'CAT', 'FEMALE', 'YOUNG', '奶牛色', '健康', '已绝育', '已接种', '一食堂后门', 'APPLYING', '已有领养申请初审通过。', '/mock/animals/naitang-cover.png'),
    (8, 'A-DOG-0003', '来福', 'DOG', 'MALE', 'ADULT', '黑棕', '健康', 'UNKNOWN', '已接种', '图书馆北侧', 'APPLYING', '领养申请已进入面谈。', '/mock/animals/laifu-cover.png'),
    (9, 'A-CAT-0006', '糯米', 'CAT', 'MALE', 'ADULT', '白灰', '稳定', '已绝育', '已接种', '操场看台旁', 'TRIAL', '当前处于试养阶段。', '/mock/animals/nuomi-cover.png')
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    type = VALUES(type),
    gender = VALUES(gender),
    age_stage = VALUES(age_stage),
    color = VALUES(color),
    health_status = VALUES(health_status),
    sterilization_status = VALUES(sterilization_status),
    vaccine_status = VALUES(vaccine_status),
    area = VALUES(area),
    status = VALUES(status),
    description = VALUES(description),
    cover_image = VALUES(cover_image);

INSERT INTO animal_image (id, animal_id, image_url, image_type)
VALUES
    (1, 1, '/mock/animals/xiaoju-cover.png', 'COVER'),
    (2, 1, '/mock/animals/xiaoju-cover.png', 'DETAIL'),
    (3, 2, '/mock/animals/xiaohei-cover.png', 'COVER'),
    (4, 3, '/mock/animals/huahua-cover.png', 'HEALTH'),
    (5, 4, '/mock/animals/wangcai-cover.png', 'RESCUE'),
    (6, 5, '/mock/animals/xiaobai-cover.png', 'COVER'),
    (7, 7, '/mock/animals/naitang-cover.png', 'COVER'),
    (8, 9, '/mock/animals/nuomi-cover.png', 'COVER'),
    (9, 6, '/mock/animals/doubao-cover.png', 'COVER'),
    (10, 8, '/mock/animals/laifu-cover.png', 'COVER')
ON DUPLICATE KEY UPDATE
    image_url = VALUES(image_url),
    image_type = VALUES(image_type);

INSERT INTO animal_health_record (id, animal_id, health_status, hospital, treatment_content, record_time, remark)
VALUES
    (1, 1, '健康', '校医院', '完成基础体检，精神和食欲正常。', '2026-06-01 10:00:00', '可进入领养展示'),
    (2, 2, '观察中', '救助站观察室', '入站观察，暂未发现明显外伤。', '2026-06-05 14:30:00', '继续观察三天'),
    (3, 3, '治疗中', '校医院', '皮肤清洁和外用药处理。', '2026-06-08 09:20:00', '一周后复查'),
    (4, 4, '待检查', '现场待救助', '暂未接触，等待志愿者协助。', '2026-06-10 16:00:00', '注意安全'),
    (5, 5, '稳定', '校医院', '老年猫基础体检，状态稳定。', '2026-05-28 11:10:00', '已领养')
ON DUPLICATE KEY UPDATE
    health_status = VALUES(health_status),
    hospital = VALUES(hospital),
    treatment_content = VALUES(treatment_content),
    record_time = VALUES(record_time),
    remark = VALUES(remark);

INSERT INTO rescue_clue (
    id, submitter_id, animal_type, location, description, emergency_level,
    image_url, contact, status, reviewer_id, review_remark
)
VALUES
    (1, 3, 'CAT', '一食堂后门', '发现一只橘猫疑似受伤，行动缓慢。', 'HIGH', '/mock/rescue/clue-1.jpg', '13800000003', 'PENDING', NULL, NULL),
    (2, 3, 'DOG', '图书馆北侧', '流浪犬长时间徘徊，已转救助工单。', 'MEDIUM', '/mock/rescue/clue-2.jpg', '13800000003', 'CONVERTED', 1, '线索有效，已创建工单'),
    (3, 3, 'OTHER', '操场看台旁', '疑似误报，现场未发现动物。', 'LOW', NULL, '13800000003', 'INVALID', 1, '现场核实无效')
ON DUPLICATE KEY UPDATE
    animal_type = VALUES(animal_type),
    location = VALUES(location),
    description = VALUES(description),
    emergency_level = VALUES(emergency_level),
    image_url = VALUES(image_url),
    contact = VALUES(contact),
    status = VALUES(status),
    reviewer_id = VALUES(reviewer_id),
    review_remark = VALUES(review_remark);

INSERT INTO rescue_order (
    id, clue_id, title, description, location, emergency_level, status,
    creator_id, handler_id, process_result, process_image, close_time
)
VALUES
    (1, 2, '图书馆流浪犬救助', '根据线索前往图书馆北侧处理流浪犬。', '图书馆北侧', 'MEDIUM', 'WAIT_ASSIGN', 1, NULL, NULL, NULL, NULL),
    (2, NULL, '宿舍区投喂点临时救助', '宿舍区发现猫咪疑似生病，需要志愿者协助。', '3号宿舍楼下投喂点', 'HIGH', 'ASSIGNED', 1, 2, NULL, NULL, NULL),
    (3, NULL, '篮球场花坛幼猫救助', '篮球场东侧花坛发现幼猫，志愿者已到场处理中。', '篮球场东侧', 'HIGH', 'PROCESSING', 1, 2, NULL, NULL, NULL),
    (4, NULL, '一食堂猫咪救助关闭样例', '已完成现场处理并关闭。', '一食堂后门', 'LOW', 'CLOSED', 1, 2, '已完成基础检查并送至救助站。', '/mock/rescue/order-4-result.jpg', '2026-06-15 18:30:00')
ON DUPLICATE KEY UPDATE
    title = VALUES(title),
    description = VALUES(description),
    location = VALUES(location),
    emergency_level = VALUES(emergency_level),
    status = VALUES(status),
    creator_id = VALUES(creator_id),
    handler_id = VALUES(handler_id),
    process_result = VALUES(process_result),
    process_image = VALUES(process_image),
    close_time = VALUES(close_time);

INSERT INTO rescue_order_log (id, order_id, operator_id, old_status, new_status, operation_type, remark)
VALUES
    (1, 1, 1, NULL, 'WAIT_ASSIGN', 'CREATE', '由线索创建工单'),
    (2, 2, 1, NULL, 'WAIT_ASSIGN', 'CREATE', '管理员直接创建工单'),
    (3, 2, 1, 'WAIT_ASSIGN', 'ASSIGNED', 'ASSIGN', '分配给志愿者'),
    (4, 3, 1, NULL, 'WAIT_ASSIGN', 'CREATE', '管理员直接创建工单'),
    (5, 3, 1, 'WAIT_ASSIGN', 'ASSIGNED', 'ASSIGN', '分配给志愿者'),
    (6, 3, 2, 'ASSIGNED', 'PROCESSING', 'START', '志愿者开始处理'),
    (7, 4, 1, NULL, 'WAIT_ASSIGN', 'CREATE', '管理员直接创建工单'),
    (8, 4, 1, 'WAIT_ASSIGN', 'ASSIGNED', 'ASSIGN', '分配给志愿者'),
    (9, 4, 2, 'ASSIGNED', 'PROCESSING', 'START', '志愿者开始处理'),
    (10, 4, 2, 'PROCESSING', 'WAIT_CONFIRM', 'FINISH', '提交处理结果'),
    (11, 4, 1, 'WAIT_CONFIRM', 'CLOSED', 'CLOSE', '管理员确认关闭')
ON DUPLICATE KEY UPDATE
    operator_id = VALUES(operator_id),
    old_status = VALUES(old_status),
    new_status = VALUES(new_status),
    operation_type = VALUES(operation_type),
    remark = VALUES(remark);

INSERT INTO volunteer_task (
    id, title, task_type, description, location, status, publisher_id,
    volunteer_id, start_time, finish_time, review_remark, version
)
VALUES
    (1, '一食堂投喂点补粮', 'FEEDING', '为固定投喂点补充猫粮和清水。', '一食堂后门投喂点', 'WAIT_CLAIM', 1, NULL, NULL, NULL, NULL, 0),
    (2, '宿舍楼下猫屋清理', 'CLEANING', '清理猫屋并更换垫布。', '3号宿舍楼下猫屋', 'CLAIMED', 1, 2, '2026-06-12 09:00:00', NULL, NULL, 1),
    (3, '花花复查送医', 'MEDICAL', '带花花到合作医院复查皮肤情况。', '安心宠物医院', 'FINISHED', 1, 2, '2026-06-13 10:00:00', '2026-06-13 12:00:00', NULL, 2),
    (4, '小白领养回访', 'FOLLOW_UP', '电话回访小白领养家庭，记录适应情况。', '线上电话回访', 'REVIEWED', 1, 2, '2026-06-14 15:00:00', '2026-06-14 15:30:00', '记录完整，审核通过', 3),
    (5, '操场临时救助协助任务', 'TEMP_RESCUE', '原计划协助临时救助，因线索取消同步取消任务。', '操场看台旁', 'CANCELED', 1, NULL, NULL, NULL, '线索取消，任务取消', 1)
ON DUPLICATE KEY UPDATE
    title = VALUES(title),
    task_type = VALUES(task_type),
    description = VALUES(description),
    location = VALUES(location),
    status = VALUES(status),
    publisher_id = VALUES(publisher_id),
    volunteer_id = VALUES(volunteer_id),
    start_time = VALUES(start_time),
    finish_time = VALUES(finish_time),
    review_remark = VALUES(review_remark),
    version = VALUES(version);

INSERT INTO task_record (id, task_id, volunteer_id, content, image_url)
VALUES
    (1, 3, 2, '已完成送医复查，医生建议继续外用药一周。', '/mock/tasks/record-3.jpg'),
    (2, 4, 2, '领养家庭反馈小白适应良好，饮食和精神状态稳定。', '/mock/tasks/record-4.jpg')
ON DUPLICATE KEY UPDATE
    content = VALUES(content),
    image_url = VALUES(image_url);

INSERT INTO adoption_application (
    id, animal_id, applicant_id, applicant_name, phone, address,
    reason, experience, status, review_remark
)
VALUES
    (1, 1, 3, '张三', '13800000003', '校内 1 号宿舍楼', '希望给小橘一个稳定归宿。', '有照顾猫咪经验。', 'PENDING', NULL),
    (2, 7, 3, '张三', '13800000003', '校内 1 号宿舍楼', '奶糖性格很适合宿舍环境。', '曾领养过流浪猫。', 'FIRST_APPROVED', '初审资料基本符合'),
    (3, 8, 3, '张三', '13800000003', '校内 1 号宿舍楼', '希望领养来福陪伴家人。', '有养犬经验。', 'INTERVIEWING', '已安排面谈'),
    (4, 9, 3, '张三', '13800000003', '校内 1 号宿舍楼', '糯米比较亲人，愿意试养。', '家中环境适合猫咪。', 'TRIAL', '进入试养阶段'),
    (5, 5, 3, '张三', '13800000003', '校内 1 号宿舍楼', '愿意长期照顾小白。', '了解老年猫照护。', 'SUCCESS', '领养成功')
ON DUPLICATE KEY UPDATE
    animal_id = VALUES(animal_id),
    applicant_id = VALUES(applicant_id),
    applicant_name = VALUES(applicant_name),
    phone = VALUES(phone),
    address = VALUES(address),
    reason = VALUES(reason),
    experience = VALUES(experience),
    status = VALUES(status),
    review_remark = VALUES(review_remark);

INSERT INTO adoption_review_log (
    id, application_id, reviewer_id, old_status, new_status, operation_type, remark
)
VALUES
    (1, 1, 3, NULL, 'PENDING', 'SUBMIT', '提交领养申请'),
    (2, 2, 3, NULL, 'PENDING', 'SUBMIT', '提交领养申请'),
    (3, 2, 1, 'PENDING', 'FIRST_APPROVED', 'FIRST_APPROVE', '初审资料基本符合'),
    (4, 3, 3, NULL, 'PENDING', 'SUBMIT', '提交领养申请'),
    (5, 3, 1, 'PENDING', 'FIRST_APPROVED', 'FIRST_APPROVE', '初审通过'),
    (6, 3, 1, 'FIRST_APPROVED', 'INTERVIEWING', 'INTERVIEW', '已安排面谈'),
    (7, 4, 3, NULL, 'PENDING', 'SUBMIT', '提交领养申请'),
    (8, 4, 1, 'PENDING', 'FIRST_APPROVED', 'FIRST_APPROVE', '初审通过'),
    (9, 4, 1, 'FIRST_APPROVED', 'INTERVIEWING', 'INTERVIEW', '面谈通过'),
    (10, 4, 1, 'INTERVIEWING', 'TRIAL', 'TRIAL', '进入试养阶段'),
    (11, 5, 3, NULL, 'PENDING', 'SUBMIT', '提交领养申请'),
    (12, 5, 1, 'PENDING', 'FIRST_APPROVED', 'FIRST_APPROVE', '初审通过'),
    (13, 5, 1, 'FIRST_APPROVED', 'INTERVIEWING', 'INTERVIEW', '面谈通过'),
    (14, 5, 1, 'INTERVIEWING', 'TRIAL', 'TRIAL', '进入试养'),
    (15, 5, 1, 'TRIAL', 'SUCCESS', 'SUCCESS', '领养成功')
ON DUPLICATE KEY UPDATE
    reviewer_id = VALUES(reviewer_id),
    old_status = VALUES(old_status),
    new_status = VALUES(new_status),
    operation_type = VALUES(operation_type),
    remark = VALUES(remark);

INSERT INTO follow_up_record (
    id, application_id, animal_id, adopter_id, follow_time, content,
    animal_condition, image_url, remark
)
VALUES
    (1, 5, 5, 3, '2026-06-20 10:00:00', '第一次电话回访，领养人反馈小白适应良好。', '饮食正常，精神稳定', '/mock/follow-up/xiaobai-1.jpg', '继续观察'),
    (2, 5, 5, 3, '2026-06-24 15:30:00', '第二次图片回访，确认居家环境安全。', '活动正常，已熟悉新环境', '/mock/follow-up/xiaobai-2.jpg', '状态良好'),
    (3, 5, 5, 3, '2026-06-28 19:20:00', '第三次回访，记录疫苗和饮食安排。', '食欲稳定，体重无异常', '/mock/follow-up/xiaobai-3.jpg', '建议按期复查')
ON DUPLICATE KEY UPDATE
    follow_time = VALUES(follow_time),
    content = VALUES(content),
    animal_condition = VALUES(animal_condition),
    image_url = VALUES(image_url),
    remark = VALUES(remark);

INSERT INTO notification (
    id, user_id, title, content, type, is_read, read_time
)
VALUES
    (1, 3, '领养申请初审通过', '你的领养申请已初审通过，请等待后续面谈安排。', 'ADOPTION', 0, NULL),
    (2, 3, '领养申请成功', '恭喜你，领养申请已成功。', 'ADOPTION', 0, NULL),
    (3, 2, '志愿者任务审核通过', '你的志愿者任务已审核通过：小白领养回访。', 'TASK', 1, '2026-06-20 09:00:00'),
    (4, 2, '新的救助工单', '你有一个新的救助工单待处理。', 'RESCUE_ORDER', 0, NULL),
    (5, 3, '系统通知', '欢迎使用流浪动物救助与领养协同管理平台。', 'SYSTEM', 1, '2026-06-18 12:00:00')
ON DUPLICATE KEY UPDATE
    title = VALUES(title),
    content = VALUES(content),
    type = VALUES(type),
    is_read = VALUES(is_read),
    read_time = VALUES(read_time);

INSERT INTO file_record (
    id, original_name, stored_name, file_url, file_path, file_type, biz_id,
    content_type, file_size, uploader_id
)
VALUES
    (1, 'animal-demo.jpg', 'animal-demo.jpg', '/uploads/mock/animal-demo.jpg', 'uploads/mock/animal-demo.jpg', 'ANIMAL_IMAGE', 1, 'image/jpeg', 102400, 1),
    (2, 'task-demo.png', 'task-demo.png', '/uploads/mock/task-demo.png', 'uploads/mock/task-demo.png', 'TASK_IMAGE', 4, 'image/png', 204800, 2),
    (3, 'follow-up-demo.jpg', 'follow-up-demo.jpg', '/uploads/mock/follow-up-demo.jpg', 'uploads/mock/follow-up-demo.jpg', 'FOLLOW_UP_IMAGE', 1, 'image/jpeg', 153600, 1)
ON DUPLICATE KEY UPDATE
    original_name = VALUES(original_name),
    stored_name = VALUES(stored_name),
    file_url = VALUES(file_url),
    file_path = VALUES(file_path),
    file_type = VALUES(file_type),
    biz_id = VALUES(biz_id),
    content_type = VALUES(content_type),
    file_size = VALUES(file_size),
    uploader_id = VALUES(uploader_id);
