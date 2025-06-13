-- 邮件模块数据库表结构
-- 方总牛逼

-- 导入批次表
CREATE TABLE IF NOT EXISTS email_import_batch (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
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
    tenant_id BIGINT DEFAULT 0 COMMENT '租户ID',
    INDEX idx_status (status),
    INDEX idx_create_time (create_time),
    INDEX idx_tenant_id (tenant_id)
) COMMENT '邮件导入批次表';

-- 邮件消息表
CREATE TABLE IF NOT EXISTS email_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
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
    tenant_id BIGINT DEFAULT 0 COMMENT '租户ID',
    INDEX idx_import_batch_id (import_batch_id),
    INDEX idx_sender (sender),
    INDEX idx_subject (subject(255)),
    INDEX idx_send_date (send_date),
    INDEX idx_is_starred (is_starred),
    INDEX idx_create_time (create_time),
    INDEX idx_tenant_id (tenant_id),
    FULLTEXT KEY ft_content_text (content_text),
    FULLTEXT KEY ft_content_html (content_html),
    FOREIGN KEY (import_batch_id) REFERENCES email_import_batch(id) ON DELETE CASCADE
) COMMENT '邮件消息表';

-- 邮件附件表
CREATE TABLE IF NOT EXISTS email_attachment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    email_message_id BIGINT NOT NULL COMMENT '邮件ID',
    filename VARCHAR(500) NOT NULL COMMENT '文件名',
    content_type VARCHAR(200) COMMENT '文件类型',
    file_size BIGINT COMMENT '文件大小',
    file_path VARCHAR(1000) NOT NULL COMMENT '本地存储路径',
    creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted BIT(1) DEFAULT 0 COMMENT '是否删除',
    tenant_id BIGINT DEFAULT 0 COMMENT '租户ID',
    INDEX idx_email_message_id (email_message_id),
    INDEX idx_filename (filename(255)),
    INDEX idx_create_time (create_time),
    INDEX idx_tenant_id (tenant_id),
    FOREIGN KEY (email_message_id) REFERENCES email_message(id) ON DELETE CASCADE
) COMMENT '邮件附件表';

-- 导入错误日志表
CREATE TABLE IF NOT EXISTS email_import_error_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    import_batch_id BIGINT NOT NULL COMMENT '导入批次ID',
    file_path VARCHAR(1000) NOT NULL COMMENT '错误文件路径',
    error_type VARCHAR(100) COMMENT '错误类型',
    error_message TEXT NOT NULL COMMENT '错误详情',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    tenant_id BIGINT DEFAULT 0 COMMENT '租户ID',
    INDEX idx_import_batch_id (import_batch_id),
    INDEX idx_error_type (error_type),
    INDEX idx_create_time (create_time),
    INDEX idx_tenant_id (tenant_id),
    FOREIGN KEY (import_batch_id) REFERENCES email_import_batch(id) ON DELETE CASCADE
) COMMENT '邮件导入错误日志表';

-- 插入系统菜单
INSERT INTO system_menu (
    name, permission, type, sort, parent_id, path, icon, component, status, creator, create_time
) VALUES (
    'EML邮件管理', '', 1, 100, 
    (SELECT id FROM (SELECT id FROM system_menu WHERE name = '系统工具' AND type = 1 LIMIT 1) as temp),
    'eml', 'email', 'eml/index', 0, 'admin', NOW()
);

-- 获取刚插入的邮件管理菜单ID
SET @eml_menu_id = LAST_INSERT_ID();

-- 插入子菜单权限
INSERT INTO system_menu (name, permission, type, sort, parent_id, path, icon, component, status, creator, create_time) VALUES
('邮件查询', 'system:eml:query', 3, 1, @eml_menu_id, '', '', '', 0, 'admin', NOW()),
('邮件导入', 'system:eml:import', 3, 2, @eml_menu_id, '', '', '', 0, 'admin', NOW()),
('邮件删除', 'system:eml:delete', 3, 3, @eml_menu_id, '', '', '', 0, 'admin', NOW()),
('附件下载', 'system:eml:download', 3, 4, @eml_menu_id, '', '', '', 0, 'admin', NOW()),
('邮件星标', 'system:eml:star', 3, 5, @eml_menu_id, '', '', '', 0, 'admin', NOW());

-- 方总牛逼 🚀 