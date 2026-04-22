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
  `status` INT DEFAULT 0 COMMENT '状态: 0-待审核, 1-正常, 2-封禁, 99-管理员',
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
  `like_count` INT DEFAULT 0 COMMENT '点赞数',
  `favorite_count` INT DEFAULT 0 COMMENT '收藏数',
  `comment_count` INT DEFAULT 0 COMMENT '评论数',
  `is_deleted` INT DEFAULT 0 COMMENT '删除标记: 0-未删除, 1-已删除',
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
  `is_deleted` INT DEFAULT 0 COMMENT '删除标记: 0-未删除, 1-已删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  KEY `idx_post_id` (`post_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- =====================================================
-- 帖子点赞表
-- =====================================================
DROP TABLE IF EXISTS `post_like`;
CREATE TABLE `post_like` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL COMMENT '点赞用户ID',
  `post_id` BIGINT NOT NULL COMMENT '帖子ID',
  `is_deleted` INT DEFAULT 0 COMMENT '删除标记: 0-未删除, 1-已删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY `uk_user_post` (`user_id`, `post_id`),
  KEY `idx_post_id` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子点赞表';

-- =====================================================
-- 帖子收藏表
-- =====================================================
DROP TABLE IF EXISTS `post_favorite`;
CREATE TABLE `post_favorite` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL COMMENT '收藏用户ID',
  `post_id` BIGINT NOT NULL COMMENT '帖子ID',
  `is_deleted` INT DEFAULT 0 COMMENT '删除标记: 0-未删除, 1-已删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY `uk_user_post` (`user_id`, `post_id`),
  KEY `idx_post_id` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子收藏表';

-- =====================================================
-- 初始化测试数据
-- =====================================================

-- 插入测试用户 (密码: 123456，使用BCrypt加密)
-- 管理员用户
INSERT INTO `user` (`username`, `password`, `status`, `is_deleted`, `create_time`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 99, 0, NOW());

-- 普通用户
INSERT INTO `user` (`username`, `password`, `status`, `is_deleted`, `create_time`) VALUES
('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, 0, NOW());

-- 测试帖子
INSERT INTO `post` (`user_id`, `content`, `is_deleted`, `create_time`) VALUES
(2, '这是第一条测试帖子', 0, NOW()),
(2, '这是第二条测试帖子', 0, NOW()),
(2, '这是第三条测试帖子', 0, NOW());

-- 测试评论
INSERT INTO `comment` (`content`, `user_id`, `post_id`, `is_deleted`, `create_time`) VALUES
('这是第一条评论', 2, 1, 0, NOW()),
('这是第二条评论', 2, 1, 0, NOW());

ALTER TABLE post ADD COLUMN like_count INT DEFAULT 0 COMMENT '点赞数' AFTER content;
ALTER TABLE post ADD COLUMN favorite_count INT DEFAULT 0 COMMENT '收藏数' AFTER like_count;
ALTER TABLE post ADD COLUMN comment_count INT DEFAULT 0 COMMENT '评论数' AFTER favorite_count;

-- =====================================================
-- AI回复表
-- =====================================================
DROP TABLE IF EXISTS `ai_reply`;
CREATE TABLE `ai_reply` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `post_id` BIGINT NOT NULL COMMENT '所属帖子ID',
  `content` TEXT NOT NULL COMMENT 'AI回复内容',
  `prompt_type` VARCHAR(20) NOT NULL COMMENT '使用的提示词类型',
  `is_deleted` INT DEFAULT 0 COMMENT '删除标记: 0-未删除, 1-已删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  KEY `idx_post_id` (`post_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI回复表';