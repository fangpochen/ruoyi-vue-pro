-- =====================================================
-- EML邮件管理菜单完整配置SQL - 一次性执行版本
-- 注意：执行前请确保数据库字符编码正确
-- =====================================================

-- 设置字符编码
SET NAMES utf8mb4;
SET CHARACTER_SET_CLIENT = utf8mb4;
SET CHARACTER_SET_CONNECTION = utf8mb4;
SET CHARACTER_SET_RESULTS = utf8mb4;

-- 定义变量存储菜单ID
SET @system_menu_id = NULL;
SET @eml_main_menu_id = NULL;
SET @eml_sub_menu_id = NULL;

-- 1. 查找系统管理菜单ID
SELECT id INTO @system_menu_id FROM system_menu 
WHERE (name = '系统管理' OR name = 'System Management') 
AND deleted = 0 LIMIT 1;

-- 如果找不到系统管理菜单，输出错误信息
SELECT 
    CASE 
        WHEN @system_menu_id IS NULL THEN '错误：未找到系统管理菜单，请检查菜单配置'
        ELSE CONCAT('找到系统管理菜单ID：', @system_menu_id)
    END as status;

-- 2. 添加EML邮件管理主菜单
INSERT INTO system_menu (
    name, permission, type, sort, parent_id, path, icon, component, component_name, 
    status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted
) VALUES (
    'EML邮件管理', '', 1, 35, @system_menu_id, 'eml', 'ep:message', '', '', 
    0, 1, 1, 1, '1', NOW(), '1', NOW(), 0
);

-- 获取刚插入的主菜单ID
SET @eml_main_menu_id = LAST_INSERT_ID();

-- 输出主菜单ID
SELECT CONCAT('EML邮件管理主菜单ID：', @eml_main_menu_id) as status;

-- 3. 添加邮件管理子菜单
INSERT INTO system_menu (
    name, permission, type, sort, parent_id, path, icon, component, component_name, 
    status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted
) VALUES (
    '邮件管理', 'system:eml:list', 2, 1, @eml_main_menu_id, 'email-management', 'ep:message-box', 
    'eml/EmailManagement', 'EmailManagement', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0
);

-- 获取刚插入的子菜单ID
SET @eml_sub_menu_id = LAST_INSERT_ID();

-- 输出子菜单ID
SELECT CONCAT('邮件管理子菜单ID：', @eml_sub_menu_id) as status;

-- 4. 添加操作权限按钮
INSERT INTO system_menu (
    name, permission, type, sort, parent_id, path, icon, component, component_name, 
    status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted
) VALUES 
-- 查询权限
('邮件查询', 'system:eml:query', 3, 1, @eml_sub_menu_id, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
-- 导入权限
('邮件导入', 'system:eml:import', 3, 2, @eml_sub_menu_id, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
-- 星标权限
('邮件星标', 'system:eml:star', 3, 3, @eml_sub_menu_id, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
-- 删除权限
('邮件删除', 'system:eml:delete', 3, 4, @eml_sub_menu_id, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
-- 下载权限
('附件下载', 'system:eml:download', 3, 5, @eml_sub_menu_id, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- 5. 为超级管理员角色（通常ID为1）分配权限
INSERT INTO system_role_menu (role_id, menu_id, creator, create_time, updater, update_time, deleted)
SELECT 1, id, '1', NOW(), '1', NOW(), 0
FROM system_menu 
WHERE name IN ('EML邮件管理', '邮件管理', '邮件查询', '邮件导入', '邮件星标', '邮件删除', '附件下载')
AND deleted = 0
AND NOT EXISTS (
    SELECT 1 FROM system_role_menu srm 
    WHERE srm.role_id = 1 AND srm.menu_id = system_menu.id AND srm.deleted = 0
);

-- 6. 输出成功信息
SELECT '✅ EML邮件管理菜单配置完成！' as result;

-- 7. 验证菜单结构
SELECT 
    m1.id as '一级菜单ID',
    m1.name as '一级菜单',
    m2.id as '二级菜单ID',
    m2.name as '二级菜单', 
    m3.id as '按钮ID',
    m3.name as '按钮权限',
    m3.permission as '权限标识',
    m3.component as '组件路径'
FROM system_menu m1
LEFT JOIN system_menu m2 ON m2.parent_id = m1.id AND m2.deleted = 0
LEFT JOIN system_menu m3 ON m3.parent_id = m2.id AND m3.deleted = 0
WHERE m1.name = 'EML邮件管理' AND m1.deleted = 0
ORDER BY m2.sort, m3.sort;

-- 8. 验证角色权限分配
SELECT 
    r.name as '角色名称',
    m.name as '菜单名称',
    m.permission as '权限标识'
FROM system_role r
JOIN system_role_menu rm ON r.id = rm.role_id AND rm.deleted = 0
JOIN system_menu m ON rm.menu_id = m.id AND m.deleted = 0
WHERE r.id = 1 AND m.name LIKE '%邮件%'
AND r.deleted = 0
ORDER BY m.sort;

-- =====================================================
-- 执行完成后的操作说明：
-- 1. 重新登录系统（清除缓存）
-- 2. 在"系统管理"菜单下查看"EML邮件管理"
-- 3. 点击"邮件管理"进入功能页面
-- ===================================================== 