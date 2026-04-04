-- 树洞社区数据库脚本
-- 创建日期: 2026-04-04

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `treehole` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `treehole`;

-- =====================================================
-- 用户表
-- =====================================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
  `status` INT DEFAULT 0 COMMENT '状态: 0-待审核, 1-正常, 2-封禁',
  `is_deleted` INT DEFAULT 0 COMMENT '删除标记: 0-未删除, 1-已删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =====================================================
-- 帖子表
-- =====================================================
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL COMMENT '发布者用户ID',
  `content` TEXT NOT NULL COMMENT '帖子内容',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子表';

-- =====================================================
-- 评论表
-- =====================================================
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `content` TEXT NOT NULL COMMENT '评论内容',
  `user_id` BIGINT NOT NULL COMMENT '评论者用户ID',
  `post_id` BIGINT NOT NULL COMMENT '所属帖子ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  KEY `idx_post_id` (`post_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- =====================================================
-- 初始化测试数据
-- =====================================================

-- 插入测试用户 (密码: 123456，使用BCrypt加密)
-- 管理员用户
INSERT INTO `user` (`username`, `password`, `status`, `is_deleted`, `create_time`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, 0, NOW());

-- 普通用户
INSERT INTO `user` (`username`, `password`, `status`, `is_deleted`, `create_time`) VALUES
('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, 0, NOW());

-- 测试帖子
INSERT INTO `post` (`user_id`, `content`, `create_time`) VALUES
(2, '这是第一条测试帖子', NOW()),
(2, '这是第二条测试帖子', NOW()),
(2, '这是第三条测试帖子', NOW());

-- 测试评论
INSERT INTO `comment` (`content`, `user_id`, `post_id`, `create_time`) VALUES
('这是第一条评论', 2, 1, NOW()),
('这是第二条评论', 2, 1, NOW());

-- 设置自增初始值
-- ALTER TABLE `user` AUTO_INCREMENT = 1000;
-- ALTER TABLE `post` AUTO_INCREMENT = 1000;
-- ALTER TABLE `comment` AUTO_INCREMENT = 1000;
