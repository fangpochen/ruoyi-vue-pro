-- EML邮件导入系统数据库表结构
-- 作者: 方总牛逼
-- 时间: 2024-12-30

-- 1. 邮件消息主表
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
    deleted BIT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id BIGINT DEFAULT 0 COMMENT '租户编号',
    INDEX idx_email_message_id (email_message_id),
    INDEX idx_filename (filename(200)),
    INDEX idx_content_type (content_type),
    INDEX idx_create_time (create_time),
    INDEX idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='邮件附件表';

-- 3. 导入批次表
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

-- 添加外键约束
ALTER TABLE system_email_attachment ADD CONSTRAINT fk_email_attachment_message_id 
    FOREIGN KEY (email_message_id) REFERENCES system_email_message(id) ON DELETE CASCADE;

ALTER TABLE system_email_message ADD CONSTRAINT fk_email_message_batch_id 
    FOREIGN KEY (import_batch_id) REFERENCES system_import_batch(id) ON DELETE CASCADE;

ALTER TABLE system_import_error_log ADD CONSTRAINT fk_error_log_batch_id 
    FOREIGN KEY (import_batch_id) REFERENCES system_import_batch(id) ON DELETE CASCADE;

-- 插入菜单权限数据
INSERT INTO system_menu (name, permission, type, sort, parent_id, path, icon, component, status, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
('邮件管理', '', 1, 10, 1264, 'email-import', 'email', 'system/email/index', 0, '1', NOW(), '1', NOW(), 0, 0);

-- 获取刚插入的菜单ID
SET @parent_menu_id = LAST_INSERT_ID();

-- 插入子菜单
INSERT INTO system_menu (name, permission, type, sort, parent_id, path, icon, component, status, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
('邮件查询', 'system:email:query', 3, 1, @parent_menu_id, '', '', '', 0, '1', NOW(), '1', NOW(), 0, 0),
('邮件导入', 'system:email:import', 3, 2, @parent_menu_id, '', '', '', 0, '1', NOW(), '1', NOW(), 0, 0),
('邮件删除', 'system:email:delete', 3, 3, @parent_menu_id, '', '', '', 0, '1', NOW(), '1', NOW(), 0, 0),
('附件下载', 'system:email:download', 3, 4, @parent_menu_id, '', '', '', 0, '1', NOW(), '1', NOW(), 0, 0),
('星标切换', 'system:email:star', 3, 5, @parent_menu_id, '', '', '', 0, '1', NOW(), '1', NOW(), 0, 0); 