-- =====================================================
-- EML邮件管理独立菜单配置 - 作为一级菜单
-- =====================================================

-- 设置字符编码
SET NAMES utf8mb4;
SET CHARACTER_SET_CLIENT = utf8mb4;
SET CHARACTER_SET_CONNECTION = utf8mb4;
SET CHARACTER_SET_RESULTS = utf8mb4;

-- 1. 删除现有的邮件管理菜单配置
DELETE FROM system_role_menu WHERE menu_id IN (
    SELECT id FROM system_menu WHERE name LIKE '%邮件%' AND deleted = 0
);

DELETE FROM system_menu WHERE name LIKE '%邮件%' AND deleted = 0;

-- 2. 创建独立的EML邮件管理一级菜单
INSERT INTO system_menu (
    name, permission, type, sort, parent_id, path, icon, component, component_name, 
    status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted
) VALUES (
    'EML邮件管理', 'system:eml:list', 2, 20, 0, '/eml/email-management', 'ep:message', 
    'eml/EmailManagement', 'EmailManagement', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0
);

-- 获取刚插入的菜单ID
SET @eml_menu_id = LAST_INSERT_ID();

-- 输出菜单ID
SELECT CONCAT('EML邮件管理菜单ID：', @eml_menu_id) as status;

-- 3. 添加操作权限按钮
INSERT INTO system_menu (
    name, permission, type, sort, parent_id, path, icon, component, component_name, 
    status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted
) VALUES 
-- 查询权限
('邮件查询', 'system:eml:query', 3, 1, @eml_menu_id, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
-- 导入权限
('邮件导入', 'system:eml:import', 3, 2, @eml_menu_id, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
-- 星标权限
('邮件星标', 'system:eml:star', 3, 3, @eml_menu_id, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
-- 删除权限
('邮件删除', 'system:eml:delete', 3, 4, @eml_menu_id, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
-- 下载权限
('附件下载', 'system:eml:download', 3, 5, @eml_menu_id, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- 4. 为超级管理员角色分配权限
INSERT INTO system_role_menu (role_id, menu_id, creator, create_time, updater, update_time, deleted)
SELECT 1, id, '1', NOW(), '1', NOW(), 0
FROM system_menu 
WHERE name IN ('EML邮件管理', '邮件查询', '邮件导入', '邮件星标', '邮件删除', '附件下载')
AND deleted = 0
AND NOT EXISTS (
    SELECT 1 FROM system_role_menu srm 
    WHERE srm.role_id = 1 AND srm.menu_id = system_menu.id AND srm.deleted = 0
);

-- 5. 输出成功信息
SELECT '✅ EML邮件管理独立菜单配置完成！' as result;

-- 6. 验证菜单结构
SELECT 
    m1.id as '菜单ID',
    m1.name as '菜单名称',
    m1.parent_id as '父菜单ID',
    m1.path as '路径',
    m1.component as '组件',
    m2.name as '子权限',
    m2.permission as '权限标识'
FROM system_menu m1
LEFT JOIN system_menu m2 ON m2.parent_id = m1.id AND m2.deleted = 0
WHERE m1.name = 'EML邮件管理' AND m1.deleted = 0
ORDER BY m2.sort;

-- =====================================================
-- 说明：
-- parent_id = 0 表示这是一级菜单（根菜单）
-- sort = 20 控制菜单在左侧的显示顺序
-- ===================================================== 