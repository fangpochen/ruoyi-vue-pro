-- ===================================================
-- EML邮件管理系统完整初始化脚本
-- 作者: 方总牛逼
-- 时间: 2024-12-30
-- 功能: 创建表结构、初始化菜单权限、分配给管理员角色
-- ===================================================

-- 设置字符编码
SET NAMES utf8mb4;
SET CHARACTER_SET_CLIENT = utf8mb4;
SET CHARACTER_SET_CONNECTION = utf8mb4;
SET CHARACTER_SET_RESULTS = utf8mb4;

-- 1. 邮件消息主表
DROP TABLE IF EXISTS system_email_message;
CREATE TABLE system_email_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    import_batch_id BIGINT NOT NULL COMMENT '导入批次ID',
    message_id VARCHAR(500) COMMENT 'Message-ID头',
    sender VARCHAR(200) NOT NULL COMMENT '发件人',
    recipients TEXT COMMENT '收件人列表(JSON)',
    cc_recipients TEXT COMMENT '抄送列表(JSON)',
    bcc_recipients TEXT COMMENT '密送列表(JSON)',
    subject VARCHAR(1000) COMMENT '主题',
    send_date DATETIME COMMENT '发送时间',
    receive_date DATETIME COMMENT '接收时间',
    content_text LONGTEXT COMMENT '纯文本内容',
    content_html LONGTEXT COMMENT 'HTML内容',
    original_path VARCHAR(1000) NOT NULL COMMENT '原始文件路径',
    attachment_count INT DEFAULT 0 COMMENT '附件数量',
    is_starred BOOLEAN DEFAULT FALSE COMMENT '是否标记星标',
    creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BIT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id BIGINT DEFAULT 0 COMMENT '租户编号',
    INDEX idx_import_batch_id (import_batch_id),
    INDEX idx_sender (sender),
    INDEX idx_subject (subject(200)),
    INDEX idx_send_date (send_date),
    INDEX idx_original_path (original_path(200)),
    INDEX idx_is_starred (is_starred),
    INDEX idx_create_time (create_time),
    INDEX idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='邮件消息表';

-- 2. 邮件附件表
DROP TABLE IF EXISTS system_email_attachment;
CREATE TABLE system_email_attachment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    email_message_id BIGINT NOT NULL COMMENT '邮件ID',
    filename VARCHAR(500) NOT NULL COMMENT '文件名',
    content_type VARCHAR(200) COMMENT '文件类型',
    file_size BIGINT COMMENT '文件大小',
    file_url VARCHAR(1000) NOT NULL COMMENT 'MinIO存储URL',
    file_path VARCHAR(1000) NOT NULL COMMENT 'MinIO存储路径',
    creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BIT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id BIGINT DEFAULT 0 COMMENT '租户编号',
    INDEX idx_email_message_id (email_message_id),
    INDEX idx_filename (filename(200)),
    INDEX idx_content_type (content_type),
    INDEX idx_create_time (create_time),
    INDEX idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='邮件附件表';

-- 3. 导入批次表
DROP TABLE IF EXISTS system_import_batch;
CREATE TABLE system_import_batch (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    batch_name VARCHAR(200) NOT NULL COMMENT '批次名称',
    zip_filename VARCHAR(500) NOT NULL COMMENT 'ZIP文件名',
    status TINYINT NOT NULL COMMENT '状态：1-处理中,2-成功,3-部分失败,4-失败',
    total_files INT DEFAULT 0 COMMENT '总文件数',
    success_count INT DEFAULT 0 COMMENT '成功数',
    fail_count INT DEFAULT 0 COMMENT '失败数',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    error_message TEXT COMMENT '错误信息',
    creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BIT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id BIGINT DEFAULT 0 COMMENT '租户编号',
    INDEX idx_batch_name (batch_name),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time),
    INDEX idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='导入批次表';

-- 4. 导入错误日志表
DROP TABLE IF EXISTS system_import_error_log;
CREATE TABLE system_import_error_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    import_batch_id BIGINT NOT NULL COMMENT '导入批次ID',
    file_path VARCHAR(1000) NOT NULL COMMENT '错误文件路径',
    error_type VARCHAR(100) COMMENT '错误类型',
    error_message TEXT NOT NULL COMMENT '错误详情',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    tenant_id BIGINT DEFAULT 0 COMMENT '租户编号',
    INDEX idx_import_batch_id (import_batch_id),
    INDEX idx_error_type (error_type),
    INDEX idx_create_time (create_time),
    INDEX idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='导入错误日志表';

-- 删除现有的邮件管理菜单配置
DELETE FROM system_role_menu WHERE menu_id IN (
    SELECT id FROM system_menu WHERE name LIKE '%邮件%' AND deleted = 0
);

DELETE FROM system_menu WHERE name LIKE '%邮件%' AND deleted = 0;

-- 5. 创建EML邮件管理主菜单
INSERT INTO system_menu (
    name, permission, type, sort, parent_id, path, icon, component, component_name, 
    status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted
) VALUES (
    'EML邮件管理', 'system:eml:list', 2, 25, 0, '/eml/email-management', 'ep:message', 
    'eml/EmailManagement', 'EmailManagement', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0
);

-- 获取刚插入的菜单ID
SET @eml_menu_id = LAST_INSERT_ID();

-- 6. 添加操作权限按钮
INSERT INTO system_menu (
    name, permission, type, sort, parent_id, path, icon, component, component_name, 
    status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted
) VALUES 
-- 查询权限
('邮件查询', 'system:email:query', 3, 1, @eml_menu_id, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
-- 导入权限
('邮件导入', 'system:email:import', 3, 2, @eml_menu_id, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
-- 星标权限
('邮件星标', 'system:email:star', 3, 3, @eml_menu_id, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
-- 删除权限
('邮件删除', 'system:email:delete', 3, 4, @eml_menu_id, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
-- 下载权限
('附件下载', 'system:email:download', 3, 5, @eml_menu_id, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- 7. 为超级管理员角色分配权限
INSERT INTO system_role_menu (role_id, menu_id, creator, create_time, updater, update_time, deleted)
SELECT 1, id, '1', NOW(), '1', NOW(), 0
FROM system_menu 
WHERE name IN ('EML邮件管理', '邮件查询', '邮件导入', '邮件星标', '邮件删除', '附件下载')
AND deleted = 0
AND NOT EXISTS (
    SELECT 1 FROM system_role_menu srm 
    WHERE srm.role_id = 1 AND srm.menu_id = system_menu.id AND srm.deleted = 0
);

-- 8. 插入一些测试数据（可选）
INSERT INTO system_import_batch (batch_name, zip_filename, status, total_files, success_count, fail_count, creator) VALUES
('测试批次1', 'test_emails.zip', 2, 10, 10, 0, '1'),
('测试批次2', 'sample_emails.zip', 2, 5, 4, 1, '1');

-- 获取批次ID
SET @batch1_id = (SELECT id FROM system_import_batch WHERE batch_name = '测试批次1' LIMIT 1);
SET @batch2_id = (SELECT id FROM system_import_batch WHERE batch_name = '测试批次2' LIMIT 1);

-- 插入测试邮件数据
INSERT INTO system_email_message (
    import_batch_id, message_id, sender, recipients, subject, send_date, 
    content_text, original_path, attachment_count, is_starred, creator
) VALUES 
(@batch1_id, '<test1@example.com>', 'sender1@company.com', '["user1@company.com", "user2@company.com"]', 
 '测试邮件1：项目进度汇报', '2024-01-15 09:30:00', '这是一封关于项目进度的邮件内容...', 
 '/emails/2024/01/email1.eml', 2, false, '1'),
(@batch1_id, '<test2@example.com>', 'manager@company.com', '["team@company.com"]', 
 '重要：系统维护通知', '2024-01-15 14:20:00', '系统将于本周六进行维护...', 
 '/emails/2024/01/email2.eml', 0, true, '1'),
(@batch2_id, '<test3@example.com>', 'hr@company.com', '["all@company.com"]', 
 '年终总结会议安排', '2024-01-16 10:15:00', '年终总结会议将于下周举行...', 
 '/emails/2024/01/email3.eml', 1, false, '1');

-- 9. 输出成功信息
SELECT '✅ EML邮件管理系统初始化完成！' as result;

-- 10. 验证菜单结构
SELECT 
    m1.id as '菜单ID',
    m1.name as '菜单名称',
    m1.path as '路径',
    m1.component as '组件',
    m2.name as '子权限',
    m2.permission as '权限标识'
FROM system_menu m1
LEFT JOIN system_menu m2 ON m2.parent_id = m1.id AND m2.deleted = 0
WHERE m1.name = 'EML邮件管理' AND m1.deleted = 0
ORDER BY m2.sort;

-- 11. 验证数据
SELECT 
    (SELECT COUNT(*) FROM system_email_message WHERE deleted = 0) as '邮件总数',
    (SELECT COUNT(*) FROM system_email_message WHERE is_starred = 1 AND deleted = 0) as '星标邮件数',
    (SELECT COUNT(*) FROM system_import_batch WHERE deleted = 0) as '导入批次数';

-- ===================================================
-- 注意事项：
-- 1. 执行前请备份数据库
-- 2. 确保MinIO存储服务已配置
-- 3. 检查权限配置是否正确
-- =================================================== 