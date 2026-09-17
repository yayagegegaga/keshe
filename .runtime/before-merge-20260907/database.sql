/*M!999999\- enable the sandbox mode */ 
-- MariaDB dump 10.20-12.3.3-MariaDB, for Win64 (AMD64)
--
-- Host: localhost    Database: stray_animal
-- ------------------------------------------------------
-- Server version	12.3.3-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*M!100616 SET @OLD_NOTE_VERBOSITY=@@NOTE_VERBOSITY, NOTE_VERBOSITY=0 */;

--
-- Table structure for table `adoption_application`
--

DROP TABLE IF EXISTS `adoption_application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `adoption_application` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `animal_id` bigint(20) NOT NULL COMMENT '动物ID',
  `applicant_id` bigint(20) NOT NULL COMMENT '申请人ID',
  `applicant_name` varchar(50) NOT NULL COMMENT '申请人姓名',
  `phone` varchar(20) NOT NULL COMMENT '联系电话',
  `address` varchar(255) DEFAULT NULL COMMENT '居住地址',
  `reason` text NOT NULL COMMENT '申请理由',
  `experience` text DEFAULT NULL COMMENT '养宠经验',
  `status` varchar(30) NOT NULL DEFAULT 'PENDING' COMMENT '申请状态',
  `review_remark` varchar(255) DEFAULT NULL COMMENT '审核备注',
  `create_time` datetime NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_adoption_application_animal_id` (`animal_id`),
  KEY `idx_adoption_application_applicant_id` (`applicant_id`),
  KEY `idx_adoption_application_status` (`status`),
  KEY `idx_adoption_application_create_time` (`create_time`),
  KEY `idx_adoption_application_applicant_animal` (`applicant_id`,`animal_id`),
  KEY `idx_adoption_application_animal_status` (`animal_id`,`status`),
  CONSTRAINT `fk_adoption_application_animal` FOREIGN KEY (`animal_id`) REFERENCES `animal` (`id`),
  CONSTRAINT `fk_adoption_application_applicant` FOREIGN KEY (`applicant_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='领养申请表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `adoption_application`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `adoption_application` WRITE;
/*!40000 ALTER TABLE `adoption_application` DISABLE KEYS */;
INSERT INTO `adoption_application` VALUES
(1,1,3,'张三','13800000003','东城区和平路 1 号','希望给小橘一个稳定家庭。','有照顾猫咪经验。','PENDING',NULL,'2026-09-04 23:59:10','2026-09-04 23:59:10'),
(2,7,3,'张三','13800000003','东城区和平路 1 号','奶糖性格很适合家里环境。','曾领养过流浪猫。','FIRST_APPROVED','初审资料基本符合','2026-09-04 23:59:10','2026-09-04 23:59:10'),
(3,8,3,'张三','13800000003','东城区和平路 1 号','希望领养来福陪伴家人。','有养犬经验。','INTERVIEWING','已安排面谈','2026-09-04 23:59:10','2026-09-04 23:59:10'),
(4,9,3,'张三','13800000003','东城区和平路 1 号','糯米比较亲人，愿意试养。','家中环境适合猫咪。','TRIAL','进入试养阶段','2026-09-04 23:59:10','2026-09-04 23:59:10'),
(5,5,3,'张三','13800000003','东城区和平路 1 号','愿意长期照顾小白。','了解老年猫照护。','SUCCESS','领养成功','2026-09-04 23:59:10','2026-09-04 23:59:10');
/*!40000 ALTER TABLE `adoption_application` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `adoption_review_log`
--

DROP TABLE IF EXISTS `adoption_review_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `adoption_review_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `application_id` bigint(20) NOT NULL COMMENT '申请ID',
  `reviewer_id` bigint(20) DEFAULT NULL COMMENT '操作人ID',
  `old_status` varchar(30) DEFAULT NULL COMMENT '原状态',
  `new_status` varchar(30) NOT NULL COMMENT '新状态',
  `operation_type` varchar(30) NOT NULL COMMENT '操作类型',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_adoption_review_log_application_id` (`application_id`),
  KEY `idx_adoption_review_log_reviewer_id` (`reviewer_id`),
  KEY `idx_adoption_review_log_create_time` (`create_time`),
  CONSTRAINT `fk_adoption_review_log_application` FOREIGN KEY (`application_id`) REFERENCES `adoption_application` (`id`),
  CONSTRAINT `fk_adoption_review_log_reviewer` FOREIGN KEY (`reviewer_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='领养审核日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `adoption_review_log`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `adoption_review_log` WRITE;
/*!40000 ALTER TABLE `adoption_review_log` DISABLE KEYS */;
INSERT INTO `adoption_review_log` VALUES
(1,1,3,NULL,'PENDING','SUBMIT','提交领养申请','2026-09-04 23:59:10'),
(2,2,3,NULL,'PENDING','SUBMIT','提交领养申请','2026-09-04 23:59:10'),
(3,2,1,'PENDING','FIRST_APPROVED','FIRST_APPROVE','初审资料基本符合','2026-09-04 23:59:10'),
(4,3,3,NULL,'PENDING','SUBMIT','提交领养申请','2026-09-04 23:59:10'),
(5,3,1,'PENDING','FIRST_APPROVED','FIRST_APPROVE','初审通过','2026-09-04 23:59:10'),
(6,3,1,'FIRST_APPROVED','INTERVIEWING','INTERVIEW','已安排面谈','2026-09-04 23:59:10'),
(7,4,3,NULL,'PENDING','SUBMIT','提交领养申请','2026-09-04 23:59:10'),
(8,4,1,'PENDING','FIRST_APPROVED','FIRST_APPROVE','初审通过','2026-09-04 23:59:10'),
(9,4,1,'FIRST_APPROVED','INTERVIEWING','INTERVIEW','面谈通过','2026-09-04 23:59:10'),
(10,4,1,'INTERVIEWING','TRIAL','TRIAL','进入试养阶段','2026-09-04 23:59:10'),
(11,5,3,NULL,'PENDING','SUBMIT','提交领养申请','2026-09-04 23:59:10'),
(12,5,1,'PENDING','FIRST_APPROVED','FIRST_APPROVE','初审通过','2026-09-04 23:59:10'),
(13,5,1,'FIRST_APPROVED','INTERVIEWING','INTERVIEW','面谈通过','2026-09-04 23:59:10'),
(14,5,1,'INTERVIEWING','TRIAL','TRIAL','进入试养','2026-09-04 23:59:10'),
(15,5,1,'TRIAL','SUCCESS','SUCCESS','领养成功','2026-09-04 23:59:10');
/*!40000 ALTER TABLE `adoption_review_log` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `animal`
--

DROP TABLE IF EXISTS `animal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `animal` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `animal_no` varchar(50) NOT NULL COMMENT '动物编号',
  `name` varchar(50) DEFAULT NULL COMMENT '动物名称',
  `type` varchar(30) NOT NULL COMMENT '动物类型',
  `gender` varchar(20) DEFAULT NULL COMMENT '性别',
  `age_stage` varchar(30) DEFAULT NULL COMMENT '年龄阶段',
  `color` varchar(50) DEFAULT NULL COMMENT '毛色',
  `health_status` varchar(50) DEFAULT NULL COMMENT '健康状态',
  `sterilization_status` varchar(50) DEFAULT NULL COMMENT '绝育状态',
  `vaccine_status` varchar(50) DEFAULT NULL COMMENT '疫苗状态',
  `area` varchar(100) DEFAULT NULL COMMENT '所在区域',
  `status` varchar(30) NOT NULL DEFAULT 'WAIT_RESCUE' COMMENT '动物状态',
  `description` text DEFAULT NULL COMMENT '描述',
  `cover_image` varchar(255) DEFAULT NULL COMMENT '封面图片',
  `create_time` datetime NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_animal_no` (`animal_no`),
  KEY `idx_animal_status` (`status`),
  KEY `idx_animal_type` (`type`),
  KEY `idx_animal_area` (`area`),
  KEY `idx_animal_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='动物档案表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `animal`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `animal` WRITE;
/*!40000 ALTER TABLE `animal` DISABLE KEYS */;
INSERT INTO `animal` VALUES
(1,'A-CAT-0001','小橘','CAT','MALE','YOUNG','橘色','健康','UNKNOWN','已接种基础疫苗','东城区','ADOPTABLE','亲人活泼，适合家庭领养。','/mock/animals/xiaoju-cover.png','2026-09-04 23:59:10','2026-09-05 15:55:01'),
(2,'A-CAT-0002','小黑','CAT','FEMALE','ADULT','黑色','观察中','UNKNOWN','UNKNOWN','西城区','OBSERVING','刚救助入站，正在观察饮食和精神状态。','/mock/animals/xiaohei-cover.png','2026-09-04 23:59:10','2026-09-05 15:55:01'),
(3,'A-CAT-0003','花花','CAT','UNKNOWN','BABY','三花','治疗中','UNKNOWN','未接种','朝阳区','TREATING','有轻微皮肤问题，正在治疗。','/mock/animals/huahua-cover.png','2026-09-04 23:59:10','2026-09-05 15:55:01'),
(4,'A-DOG-0001','旺财','DOG','MALE','ADULT','黄色','待检查','UNKNOWN','UNKNOWN','海淀区','WAIT_RESCUE','社区居民上报，仍在现场等待救助。','/mock/animals/wangcai-cover.png','2026-09-04 23:59:10','2026-09-05 16:03:40'),
(5,'A-CAT-0004','小白','CAT','FEMALE','OLD','白色','稳定','已绝育','已接种','丰台区','ADOPTED','已完成领养，后续将进入回访流程。','/mock/animals/xiaobai-cover.png','2026-09-04 23:59:10','2026-09-05 15:55:01'),
(6,'A-DOG-0002','豆包','DOG','FEMALE','YOUNG','棕白','健康','UNKNOWN','已接种基础疫苗','通州区','ADOPTABLE','性格温顺，适合提交领养申请测试。','/mock/animals/doubao-cover.png','2026-09-04 23:59:10','2026-09-05 16:03:40'),
(7,'A-CAT-0005','奶糖','CAT','FEMALE','YOUNG','奶牛色','健康','已绝育','已接种','东城区','APPLYING','已有领养申请初审通过。','/mock/animals/naitang-cover.png','2026-09-04 23:59:10','2026-09-05 15:55:01'),
(8,'A-DOG-0003','来福','DOG','MALE','ADULT','黑棕','健康','UNKNOWN','已接种','朝阳区','APPLYING','领养申请已进入面谈。','/mock/animals/laifu-cover.png','2026-09-04 23:59:10','2026-09-05 16:03:40'),
(9,'A-CAT-0006','糯米','CAT','MALE','ADULT','白灰','稳定','已绝育','已接种','海淀区','TRIAL','当前处于试养阶段。','/mock/animals/nuomi-cover.png','2026-09-04 23:59:10','2026-09-05 15:55:01');
/*!40000 ALTER TABLE `animal` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `animal_health_record`
--

DROP TABLE IF EXISTS `animal_health_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `animal_health_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `animal_id` bigint(20) NOT NULL COMMENT '动物ID',
  `health_status` varchar(50) NOT NULL COMMENT '健康状态',
  `hospital` varchar(100) DEFAULT NULL COMMENT '医院',
  `treatment_content` text DEFAULT NULL COMMENT '治疗内容',
  `record_time` datetime NOT NULL COMMENT '记录时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_health_record_animal_id` (`animal_id`),
  KEY `idx_health_record_record_time` (`record_time`),
  CONSTRAINT `fk_health_record_animal` FOREIGN KEY (`animal_id`) REFERENCES `animal` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='动物健康记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `animal_health_record`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `animal_health_record` WRITE;
/*!40000 ALTER TABLE `animal_health_record` DISABLE KEYS */;
INSERT INTO `animal_health_record` VALUES
(1,1,'健康','社区合作宠物医院','完成基础体检，精神和食欲正常。','2026-06-01 10:00:00','可进入领养展示','2026-09-04 23:59:10'),
(2,2,'观察中','救助站观察室','入站观察，暂未发现明显外伤。','2026-06-05 14:30:00','继续观察三天','2026-09-04 23:59:10'),
(3,3,'治疗中','安心宠物医院','皮肤清洁和外用药处理。','2026-06-08 09:20:00','一周后复查','2026-09-04 23:59:10'),
(4,4,'待检查','现场待救助','暂未接触，等待志愿者协助。','2026-06-10 16:00:00','注意安全','2026-09-04 23:59:10'),
(5,5,'稳定','社区合作宠物医院','老年猫基础体检，状态稳定。','2026-05-28 11:10:00','已领养','2026-09-04 23:59:10');
/*!40000 ALTER TABLE `animal_health_record` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `animal_image`
--

DROP TABLE IF EXISTS `animal_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `animal_image` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `animal_id` bigint(20) NOT NULL COMMENT '动物ID',
  `image_url` varchar(255) NOT NULL COMMENT '图片地址',
  `image_type` varchar(30) NOT NULL COMMENT '图片类型：COVER、DETAIL、RESCUE、HEALTH',
  `create_time` datetime NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_animal_image_animal_id` (`animal_id`),
  CONSTRAINT `fk_animal_image_animal` FOREIGN KEY (`animal_id`) REFERENCES `animal` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='动物图片表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `animal_image`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `animal_image` WRITE;
/*!40000 ALTER TABLE `animal_image` DISABLE KEYS */;
INSERT INTO `animal_image` VALUES
(1,1,'/mock/animals/xiaoju-cover.png','COVER','2026-09-04 23:59:10'),
(2,1,'/mock/animals/xiaoju-cover.png','DETAIL','2026-09-04 23:59:10'),
(3,2,'/mock/animals/xiaohei-cover.png','COVER','2026-09-04 23:59:10'),
(4,3,'/mock/animals/huahua-cover.png','HEALTH','2026-09-04 23:59:10'),
(5,4,'/mock/animals/wangcai-cover.png','RESCUE','2026-09-04 23:59:10'),
(6,5,'/mock/animals/xiaobai-cover.png','COVER','2026-09-04 23:59:10'),
(7,7,'/mock/animals/naitang-cover.png','COVER','2026-09-05 15:55:01'),
(8,9,'/mock/animals/nuomi-cover.png','COVER','2026-09-05 15:55:01'),
(9,6,'/mock/animals/doubao-cover.png','COVER','2026-09-05 16:03:40'),
(10,8,'/mock/animals/laifu-cover.png','COVER','2026-09-05 16:03:40');
/*!40000 ALTER TABLE `animal_image` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `file_record`
--

DROP TABLE IF EXISTS `file_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `file_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `original_name` varchar(255) NOT NULL COMMENT '原始文件名',
  `stored_name` varchar(255) NOT NULL COMMENT '存储文件名',
  `file_url` varchar(255) NOT NULL COMMENT '文件访问URL',
  `file_path` varchar(500) NOT NULL COMMENT '文件物理路径',
  `file_type` varchar(50) NOT NULL COMMENT '文件业务类型',
  `biz_id` bigint(20) DEFAULT NULL COMMENT '关联业务ID',
  `content_type` varchar(100) DEFAULT NULL COMMENT 'MIME类型',
  `file_size` bigint(20) NOT NULL COMMENT '文件大小',
  `uploader_id` bigint(20) NOT NULL COMMENT '上传人ID',
  `create_time` datetime NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_file_record_uploader_id` (`uploader_id`),
  KEY `idx_file_record_file_type` (`file_type`),
  KEY `idx_file_record_biz_id` (`biz_id`),
  KEY `idx_file_record_create_time` (`create_time`),
  CONSTRAINT `fk_file_record_uploader` FOREIGN KEY (`uploader_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `file_record`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `file_record` WRITE;
/*!40000 ALTER TABLE `file_record` DISABLE KEYS */;
INSERT INTO `file_record` VALUES
(1,'animal-demo.jpg','animal-demo.jpg','/uploads/mock/animal-demo.jpg','uploads/mock/animal-demo.jpg','ANIMAL_IMAGE',1,'image/jpeg',102400,1,'2026-09-04 23:59:10'),
(2,'task-demo.png','task-demo.png','/uploads/mock/task-demo.png','uploads/mock/task-demo.png','TASK_IMAGE',4,'image/png',204800,2,'2026-09-04 23:59:10'),
(3,'follow-up-demo.jpg','follow-up-demo.jpg','/uploads/mock/follow-up-demo.jpg','uploads/mock/follow-up-demo.jpg','FOLLOW_UP_IMAGE',1,'image/jpeg',153600,1,'2026-09-04 23:59:10');
/*!40000 ALTER TABLE `file_record` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `follow_up_record`
--

DROP TABLE IF EXISTS `follow_up_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `follow_up_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `application_id` bigint(20) NOT NULL COMMENT '领养申请ID',
  `animal_id` bigint(20) NOT NULL COMMENT '动物ID',
  `adopter_id` bigint(20) NOT NULL COMMENT '领养人ID',
  `follow_time` datetime NOT NULL COMMENT '回访时间',
  `content` text NOT NULL COMMENT '回访内容',
  `animal_condition` varchar(255) DEFAULT NULL COMMENT '动物当前情况',
  `image_url` varchar(255) DEFAULT NULL COMMENT '回访图片URL',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_follow_up_application_id` (`application_id`),
  KEY `idx_follow_up_animal_id` (`animal_id`),
  KEY `idx_follow_up_adopter_id` (`adopter_id`),
  KEY `idx_follow_up_follow_time` (`follow_time`),
  KEY `idx_follow_up_create_time` (`create_time`),
  CONSTRAINT `fk_follow_up_adopter` FOREIGN KEY (`adopter_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `fk_follow_up_animal` FOREIGN KEY (`animal_id`) REFERENCES `animal` (`id`),
  CONSTRAINT `fk_follow_up_application` FOREIGN KEY (`application_id`) REFERENCES `adoption_application` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='回访记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `follow_up_record`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `follow_up_record` WRITE;
/*!40000 ALTER TABLE `follow_up_record` DISABLE KEYS */;
INSERT INTO `follow_up_record` VALUES
(1,5,5,3,'2026-06-20 10:00:00','第一次电话回访，领养人反馈小白适应良好。','饮食正常，精神稳定','/mock/follow-up/xiaobai-1.jpg','继续观察','2026-09-04 23:59:10','2026-09-04 23:59:10'),
(2,5,5,3,'2026-06-24 15:30:00','第二次图片回访，确认居家环境安全。','活动正常，已熟悉新环境','/mock/follow-up/xiaobai-2.jpg','状态良好','2026-09-04 23:59:10','2026-09-04 23:59:10'),
(3,5,5,3,'2026-06-28 19:20:00','第三次回访，记录疫苗和饮食安排。','食欲稳定，体重无异常','/mock/follow-up/xiaobai-3.jpg','建议按期复查','2026-09-04 23:59:10','2026-09-04 23:59:10');
/*!40000 ALTER TABLE `follow_up_record` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '接收用户ID',
  `title` varchar(100) NOT NULL COMMENT '通知标题',
  `content` text NOT NULL COMMENT '通知内容',
  `type` varchar(30) NOT NULL COMMENT '通知类型',
  `is_read` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否已读：0未读，1已读',
  `create_time` datetime NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
  PRIMARY KEY (`id`),
  KEY `idx_notification_user_id` (`user_id`),
  KEY `idx_notification_is_read` (`is_read`),
  KEY `idx_notification_type` (`type`),
  KEY `idx_notification_create_time` (`create_time`),
  CONSTRAINT `fk_notification_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统通知表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
INSERT INTO `notification` VALUES
(1,3,'领养申请初审通过','你的领养申请已初审通过，请等待后续面谈安排。','ADOPTION',0,'2026-09-04 23:59:10',NULL),
(2,3,'领养申请成功','恭喜你，领养申请已成功。','ADOPTION',0,'2026-09-04 23:59:10',NULL),
(3,2,'志愿者任务审核通过','你的志愿者任务已审核通过：小白领养回访。','TASK',1,'2026-09-04 23:59:10','2026-06-20 09:00:00'),
(4,2,'新的救助工单','你有一个新的救助工单待处理。','RESCUE_ORDER',0,'2026-09-04 23:59:10',NULL),
(5,3,'系统通知','欢迎使用流浪动物救助与领养协同管理平台。','SYSTEM',1,'2026-09-04 23:59:10','2026-06-18 12:00:00');
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `rescue_clue`
--

DROP TABLE IF EXISTS `rescue_clue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `rescue_clue` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `submitter_id` bigint(20) NOT NULL COMMENT '提交人ID',
  `animal_type` varchar(30) NOT NULL COMMENT '动物类型：CAT、DOG、OTHER',
  `location` varchar(255) NOT NULL COMMENT '发现地点',
  `description` text NOT NULL COMMENT '线索描述',
  `emergency_level` varchar(30) NOT NULL COMMENT '紧急程度：LOW、MEDIUM、HIGH',
  `image_url` varchar(255) DEFAULT NULL COMMENT '现场图片',
  `contact` varchar(100) DEFAULT NULL COMMENT '联系方式',
  `status` varchar(30) NOT NULL DEFAULT 'PENDING' COMMENT '线索状态',
  `reviewer_id` bigint(20) DEFAULT NULL COMMENT '审核人ID',
  `review_remark` varchar(255) DEFAULT NULL COMMENT '审核备注',
  `create_time` datetime NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_rescue_clue_submitter_id` (`submitter_id`),
  KEY `idx_rescue_clue_status` (`status`),
  KEY `idx_rescue_clue_animal_type` (`animal_type`),
  KEY `idx_rescue_clue_emergency_level` (`emergency_level`),
  KEY `idx_rescue_clue_create_time` (`create_time`),
  KEY `fk_rescue_clue_reviewer` (`reviewer_id`),
  CONSTRAINT `fk_rescue_clue_reviewer` FOREIGN KEY (`reviewer_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `fk_rescue_clue_submitter` FOREIGN KEY (`submitter_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='救助线索表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rescue_clue`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `rescue_clue` WRITE;
/*!40000 ALTER TABLE `rescue_clue` DISABLE KEYS */;
INSERT INTO `rescue_clue` VALUES
(1,3,'CAT','东城区和平路小区门口','发现一只橘猫疑似受伤，行动缓慢。','HIGH','/mock/rescue/clue-1.jpg','13800000003','PENDING',NULL,NULL,'2026-09-04 23:59:10','2026-09-04 23:59:10'),
(2,3,'DOG','海淀区学院路公交站','流浪犬长时间徘徊，已转救助工单。','MEDIUM','/mock/rescue/clue-2.jpg','13800000003','CONVERTED',1,'线索有效，已创建工单','2026-09-04 23:59:10','2026-09-04 23:59:10'),
(3,3,'OTHER','朝阳区公园北门','疑似误报，现场未发现动物。','LOW',NULL,'13800000003','INVALID',1,'现场核实无效','2026-09-04 23:59:10','2026-09-04 23:59:10');
/*!40000 ALTER TABLE `rescue_clue` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `rescue_order`
--

DROP TABLE IF EXISTS `rescue_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `rescue_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `clue_id` bigint(20) DEFAULT NULL COMMENT '来源线索ID',
  `title` varchar(100) NOT NULL COMMENT '工单标题',
  `description` text NOT NULL COMMENT '工单描述',
  `location` varchar(255) NOT NULL COMMENT '地点',
  `emergency_level` varchar(30) NOT NULL COMMENT '紧急程度：LOW、MEDIUM、HIGH',
  `status` varchar(30) NOT NULL DEFAULT 'WAIT_ASSIGN' COMMENT '工单状态',
  `creator_id` bigint(20) NOT NULL COMMENT '创建人ID',
  `handler_id` bigint(20) DEFAULT NULL COMMENT '处理人ID',
  `process_result` text DEFAULT NULL COMMENT '处理结果',
  `process_image` varchar(255) DEFAULT NULL COMMENT '处理结果图片',
  `create_time` datetime NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新时间',
  `close_time` datetime DEFAULT NULL COMMENT '关闭时间',
  PRIMARY KEY (`id`),
  KEY `idx_rescue_order_clue_id` (`clue_id`),
  KEY `idx_rescue_order_status` (`status`),
  KEY `idx_rescue_order_creator_id` (`creator_id`),
  KEY `idx_rescue_order_handler_id` (`handler_id`),
  KEY `idx_rescue_order_emergency_level` (`emergency_level`),
  KEY `idx_rescue_order_create_time` (`create_time`),
  CONSTRAINT `fk_rescue_order_clue` FOREIGN KEY (`clue_id`) REFERENCES `rescue_clue` (`id`),
  CONSTRAINT `fk_rescue_order_creator` FOREIGN KEY (`creator_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `fk_rescue_order_handler` FOREIGN KEY (`handler_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='救助工单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rescue_order`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `rescue_order` WRITE;
/*!40000 ALTER TABLE `rescue_order` DISABLE KEYS */;
INSERT INTO `rescue_order` VALUES
(1,2,'公交站流浪犬救助','根据线索前往公交站处理流浪犬。','海淀区学院路公交站','MEDIUM','WAIT_ASSIGN',1,NULL,NULL,NULL,'2026-09-04 23:59:10','2026-09-04 23:59:10',NULL),
(2,NULL,'社区投喂点临时救助','社区发现猫咪疑似生病，需要志愿者协助。','西城区社区投喂点','HIGH','ASSIGNED',1,2,NULL,NULL,'2026-09-04 23:59:10','2026-09-04 23:59:10',NULL),
(3,NULL,'桥下幼猫救助','桥下发现幼猫，志愿者已到场处理中。','朝阳区河边桥下','HIGH','PROCESSING',1,2,NULL,NULL,'2026-09-04 23:59:10','2026-09-04 23:59:10',NULL),
(4,NULL,'小区门口猫咪救助关闭样例','已完成现场处理并关闭。','东城区和平路小区门口','LOW','CLOSED',1,2,'已完成基础检查并送至救助站。','/mock/rescue/order-4-result.jpg','2026-09-04 23:59:10','2026-09-04 23:59:10','2026-06-15 18:30:00');
/*!40000 ALTER TABLE `rescue_order` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `rescue_order_log`
--

DROP TABLE IF EXISTS `rescue_order_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `rescue_order_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint(20) NOT NULL COMMENT '工单ID',
  `operator_id` bigint(20) DEFAULT NULL COMMENT '操作人ID',
  `old_status` varchar(30) DEFAULT NULL COMMENT '原状态',
  `new_status` varchar(30) NOT NULL COMMENT '新状态',
  `operation_type` varchar(30) NOT NULL COMMENT '操作类型',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_rescue_order_log_order_id` (`order_id`),
  KEY `idx_rescue_order_log_operator_id` (`operator_id`),
  KEY `idx_rescue_order_log_create_time` (`create_time`),
  CONSTRAINT `fk_rescue_order_log_operator` FOREIGN KEY (`operator_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `fk_rescue_order_log_order` FOREIGN KEY (`order_id`) REFERENCES `rescue_order` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工单流转日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rescue_order_log`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `rescue_order_log` WRITE;
/*!40000 ALTER TABLE `rescue_order_log` DISABLE KEYS */;
INSERT INTO `rescue_order_log` VALUES
(1,1,1,NULL,'WAIT_ASSIGN','CREATE','由线索创建工单','2026-09-04 23:59:10'),
(2,2,1,NULL,'WAIT_ASSIGN','CREATE','管理员直接创建工单','2026-09-04 23:59:10'),
(3,2,1,'WAIT_ASSIGN','ASSIGNED','ASSIGN','分配给志愿者','2026-09-04 23:59:10'),
(4,3,1,NULL,'WAIT_ASSIGN','CREATE','管理员直接创建工单','2026-09-04 23:59:10'),
(5,3,1,'WAIT_ASSIGN','ASSIGNED','ASSIGN','分配给志愿者','2026-09-04 23:59:10'),
(6,3,2,'ASSIGNED','PROCESSING','START','志愿者开始处理','2026-09-04 23:59:10'),
(7,4,1,NULL,'WAIT_ASSIGN','CREATE','管理员直接创建工单','2026-09-04 23:59:10'),
(8,4,1,'WAIT_ASSIGN','ASSIGNED','ASSIGN','分配给志愿者','2026-09-04 23:59:10'),
(9,4,2,'ASSIGNED','PROCESSING','START','志愿者开始处理','2026-09-04 23:59:10'),
(10,4,2,'PROCESSING','WAIT_CONFIRM','FINISH','提交处理结果','2026-09-04 23:59:10'),
(11,4,1,'WAIT_CONFIRM','CLOSED','CLOSE','管理员确认关闭','2026-09-04 23:59:10');
/*!40000 ALTER TABLE `rescue_order_log` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_code` varchar(50) NOT NULL COMMENT '角色编码',
  `role_name` varchar(50) NOT NULL COMMENT '角色名称',
  `description` varchar(255) DEFAULT NULL COMMENT '角色说明',
  `create_time` datetime NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_role_code` (`role_code`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES
(1,'SUPER_ADMIN','系统管理员','拥有系统最高管理权限','2026-09-04 23:59:10','2026-09-04 23:59:10'),
(2,'ADMIN','救助站管理员','负责线索审核、工单分配、动物和领养管理','2026-09-04 23:59:10','2026-09-04 23:59:10'),
(3,'USER','普通用户','提交救助线索和领养申请','2026-09-04 23:59:10','2026-09-04 23:59:10'),
(4,'VOLUNTEER','志愿者','处理救助工单和志愿者任务','2026-09-04 23:59:10','2026-09-04 23:59:10');
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像地址',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '状态：0禁用，1启用',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_username` (`username`),
  KEY `idx_sys_user_phone` (`phone`),
  KEY `idx_sys_user_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES
(1,'admin','$2a$10$5M1HJMdyJw4EgEthKEm7/O2fsAsjqL0UNWlsOJlFMF8RB5tDCFxwq','超级管理员','13800000000','admin@example.com',NULL,1,'2026-09-07 16:39:53','2026-09-04 23:59:10','2026-09-07 16:39:53'),
(2,'volunteer','$2a$10$908hel0J5WCpg7D6b8VS0.cUKk4j8RFKYoXRgfynAtLKwbUuQ6vsK','志愿者测试账号','13800000002','volunteer@example.com',NULL,1,'2026-09-05 16:19:28','2026-09-04 23:59:10','2026-09-05 16:19:29'),
(3,'user','$2a$10$rhZIFvXpYU7K0VQPy.AkXuOXZMFqx4GvSUtyk/8oqjhmPFGp/p502','普通用户测试账号','13800000003','user@example.com',NULL,1,'2026-09-05 16:31:08','2026-09-04 23:59:10','2026-09-05 16:31:08');
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_role` (`user_id`,`role_id`),
  KEY `idx_sys_user_role_role_id` (`role_id`),
  CONSTRAINT `fk_sys_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`),
  CONSTRAINT `fk_sys_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
INSERT INTO `sys_user_role` VALUES
(1,1,1),
(2,2,4),
(3,3,3);
/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `task_record`
--

DROP TABLE IF EXISTS `task_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `task_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_id` bigint(20) NOT NULL COMMENT '任务ID',
  `volunteer_id` bigint(20) NOT NULL COMMENT '志愿者ID',
  `content` text NOT NULL COMMENT '完成内容',
  `image_url` varchar(255) DEFAULT NULL COMMENT '完成图片URL',
  `create_time` datetime NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_task_record_task_id` (`task_id`),
  KEY `idx_task_record_volunteer_id` (`volunteer_id`),
  KEY `idx_task_record_create_time` (`create_time`),
  CONSTRAINT `fk_task_record_task` FOREIGN KEY (`task_id`) REFERENCES `volunteer_task` (`id`),
  CONSTRAINT `fk_task_record_volunteer` FOREIGN KEY (`volunteer_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务完成记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `task_record`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `task_record` WRITE;
/*!40000 ALTER TABLE `task_record` DISABLE KEYS */;
INSERT INTO `task_record` VALUES
(1,3,2,'已完成送医复查，医生建议继续外用药一周。','/mock/tasks/record-3.jpg','2026-09-04 23:59:10'),
(2,4,2,'领养家庭反馈小白适应良好，饮食和精神状态稳定。','/mock/tasks/record-4.jpg','2026-09-04 23:59:10');
/*!40000 ALTER TABLE `task_record` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `volunteer_task`
--

DROP TABLE IF EXISTS `volunteer_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `volunteer_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(100) NOT NULL COMMENT '任务标题',
  `task_type` varchar(30) NOT NULL COMMENT '任务类型',
  `description` text DEFAULT NULL COMMENT '任务描述',
  `location` varchar(255) NOT NULL COMMENT '任务地点',
  `status` varchar(30) NOT NULL DEFAULT 'WAIT_CLAIM' COMMENT '任务状态',
  `publisher_id` bigint(20) NOT NULL COMMENT '发布人ID',
  `volunteer_id` bigint(20) DEFAULT NULL COMMENT '领取志愿者ID',
  `start_time` datetime DEFAULT NULL COMMENT '任务开始时间',
  `finish_time` datetime DEFAULT NULL COMMENT '任务完成时间',
  `review_remark` varchar(255) DEFAULT NULL COMMENT '审核或取消备注',
  `version` int(11) NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  `create_time` datetime NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_volunteer_task_status` (`status`),
  KEY `idx_volunteer_task_type` (`task_type`),
  KEY `idx_volunteer_task_publisher_id` (`publisher_id`),
  KEY `idx_volunteer_task_volunteer_id` (`volunteer_id`),
  KEY `idx_volunteer_task_create_time` (`create_time`),
  CONSTRAINT `fk_volunteer_task_publisher` FOREIGN KEY (`publisher_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `fk_volunteer_task_volunteer` FOREIGN KEY (`volunteer_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='志愿者任务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `volunteer_task`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `volunteer_task` WRITE;
/*!40000 ALTER TABLE `volunteer_task` DISABLE KEYS */;
INSERT INTO `volunteer_task` VALUES
(1,'东城区投喂点补粮','FEEDING','为固定投喂点补充猫粮和清水。','东城区和平路投喂点','WAIT_CLAIM',1,NULL,NULL,NULL,NULL,0,'2026-09-04 23:59:10','2026-09-04 23:59:10'),
(2,'西城区猫屋清理','CLEANING','清理社区猫屋并更换垫布。','西城区社区猫屋','CLAIMED',1,2,'2026-06-12 09:00:00',NULL,NULL,1,'2026-09-04 23:59:10','2026-09-04 23:59:10'),
(3,'花花复查送医','MEDICAL','带花花到合作医院复查皮肤情况。','安心宠物医院','FINISHED',1,2,'2026-06-13 10:00:00','2026-06-13 12:00:00',NULL,2,'2026-09-04 23:59:10','2026-09-04 23:59:10'),
(4,'小白领养回访','FOLLOW_UP','电话回访小白领养家庭，记录适应情况。','线上电话回访','REVIEWED',1,2,'2026-06-14 15:00:00','2026-06-14 15:30:00','记录完整，审核通过',3,'2026-09-04 23:59:10','2026-09-04 23:59:10'),
(5,'临时救助协助任务','TEMP_RESCUE','原计划协助临时救助，因线索取消同步取消任务。','朝阳区公园北门','CANCELED',1,NULL,NULL,NULL,'线索取消，任务取消',1,'2026-09-04 23:59:10','2026-09-04 23:59:10');
/*!40000 ALTER TABLE `volunteer_task` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Dumping routines for database 'stray_animal'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*M!100616 SET NOTE_VERBOSITY=@OLD_NOTE_VERBOSITY */;

-- Dump completed on 2026-09-07 16:50:31
