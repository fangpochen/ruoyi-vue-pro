-- EML邮件管理菜单配置
-- 在系统管理下添加EML邮件管理子菜单
-- 执行前请先查询系统管理的菜单ID: SELECT id FROM system_menu WHERE name = '系统管理' AND deleted = 0;

-- 1. 添加EML邮件管理主菜单（在系统管理下）
-- 注意：parent_id 需要替换为实际的系统管理菜单ID，通常是1
INSERT INTO system_menu (
    name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted
) VALUES (
    'EML邮件管理', '', 1, 35, 1, 'eml', 'ep:message', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0
);

-- 2. 查询刚插入的EML邮件管理菜单ID
-- 执行后记录返回的ID，用于下面的parent_id
SELECT id as 'EML邮件管理菜单ID' FROM system_menu WHERE name = 'EML邮件管理' AND deleted = 0 ORDER BY id DESC LIMIT 1;

-- 3. 添加EML邮件管理子菜单
-- 注意：请将下面的 999 替换为上一步查询到的EML邮件管理菜单ID
INSERT INTO system_menu (
    name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted
) VALUES (
    '邮件管理', 'system:eml:list', 2, 1, 999, 'email-management', 'ep:message-box', 'eml/EmailManagement', 'EmailManagement', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0
);

-- 4. 查询邮件管理菜单ID
SELECT id as '邮件管理菜单ID' FROM system_menu WHERE name = '邮件管理' AND parent_id = (SELECT id FROM system_menu WHERE name = 'EML邮件管理' AND deleted = 0) AND deleted = 0 ORDER BY id DESC LIMIT 1;

-- 5. 添加邮件管理的操作按钮权限
-- 注意：请将下面的 888 替换为上一步查询到的邮件管理菜单ID
INSERT INTO system_menu (
    name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted
) VALUES 
-- 查询权限
('邮件查询', 'system:eml:query', 3, 1, 888, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
-- 导入权限
('邮件导入', 'system:eml:import', 3, 2, 888, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
-- 星标权限
('邮件星标', 'system:eml:star', 3, 3, 888, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
-- 删除权限
('邮件删除', 'system:eml:delete', 3, 4, 888, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
-- 下载权限
('附件下载', 'system:eml:download', 3, 5, 888, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);

-- 6. 为超级管理员角色分配EML邮件管理权限
-- 查询所有EML邮件相关菜单ID
SELECT id, name, permission FROM system_menu 
WHERE name IN ('EML邮件管理', '邮件管理', '邮件查询', '邮件导入', '邮件星标', '邮件删除', '附件下载')
AND deleted = 0;

-- 执行说明：
-- 1. 先执行第1步插入EML邮件管理主菜单
-- 2. 执行第2步查询获得菜单ID，记录下这个ID（比如是100）
-- 3. 将第3步SQL中的999替换为实际的ID（100），然后执行
-- 4. 执行第4步查询获得邮件管理菜单ID，记录下这个ID（比如是101）
-- 5. 将第5步SQL中的888替换为实际的ID（101），然后执行
-- 6. 最后为角色分配权限（可选，超级管理员通常有所有权限）

-- 验证菜单结构
SELECT 
    m1.id as '一级菜单ID',
    m1.name as '一级菜单',
    m2.id as '二级菜单ID',
    m2.name as '二级菜单', 
    m3.id as '按钮ID',
    m3.name as '按钮权限',
    m3.permission as '权限标识'
FROM system_menu m1
LEFT JOIN system_menu m2 ON m2.parent_id = m1.id AND m2.deleted = 0
LEFT JOIN system_menu m3 ON m3.parent_id = m2.id AND m3.deleted = 0
WHERE m1.name = 'EML邮件管理' AND m1.deleted = 0
ORDER BY m2.sort, m3.sort; 