# DBeaver 手动配置邮件菜单指南

## 问题原因
DBeaver导入SQL文件时出现中文字符编码错误，这是因为文件编码与数据库字符集不匹配。

## 解决方案：手动执行SQL语句

### 第一步：设置字符编码
在DBeaver的SQL编辑器中先执行：
```sql
SET NAMES utf8mb4;
SET CHARACTER_SET_CLIENT = utf8mb4;
SET CHARACTER_SET_CONNECTION = utf8mb4;
SET CHARACTER_SET_RESULTS = utf8mb4;
```

### 第二步：查询系统管理菜单ID
```sql
SELECT id, name FROM system_menu WHERE name = '系统管理' AND deleted = 0;
```
**记录返回的ID（通常是1）**

### 第三步：添加邮件管理主菜单
```sql
INSERT INTO system_menu (
    name, permission, type, sort, parent_id, path, icon, component, component_name, 
    status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted
) VALUES (
    'Email Management', '', 1, 30, 1, 'email', 'email', '', '', 
    0, 1, 1, 1, '1', NOW(), '1', NOW(), 0
);
```
**注意：将parent_id的值1替换为第二步查询到的ID**

### 第四步：查询邮件管理菜单ID
```sql
SELECT id, name FROM system_menu WHERE name = 'Email Management' AND deleted = 0 ORDER BY id DESC LIMIT 1;
```
**记录返回的ID**

### 第五步：添加EML邮件导入子菜单
```sql
INSERT INTO system_menu (
    name, permission, type, sort, parent_id, path, icon, component, component_name, 
    status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted
) VALUES (
    'EML Email Import', 'system:email:list', 2, 1, 替换为第四步的ID, 'eml-import', 'upload', 'system/email/index', 'SystemEmailIndex', 
    0, 1, 1, 1, '1', NOW(), '1', NOW(), 0
);
```

### 第六步：查询EML邮件导入菜单ID
```sql
SELECT id, name FROM system_menu WHERE name = 'EML Email Import' AND deleted = 0 ORDER BY id DESC LIMIT 1;
```
**记录返回的ID**

### 第七步：添加操作权限按钮
```sql
INSERT INTO system_menu (
    name, permission, type, sort, parent_id, path, icon, component, component_name, 
    status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted
) VALUES 
('Email Query', 'system:email:query', 3, 1, 替换为第六步的ID, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
('Email Import', 'system:email:import', 3, 2, 替换为第六步的ID, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
('Email Star', 'system:email:star', 3, 3, 替换为第六步的ID, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
('Email Delete', 'system:email:delete', 3, 4, 替换为第六步的ID, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0),
('Attachment Download', 'system:email:download', 3, 5, 替换为第六步的ID, '', '', '', '', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0);
```

### 第八步：查询所有邮件菜单ID
```sql
SELECT id, name, permission FROM system_menu 
WHERE name IN ('Email Management', 'EML Email Import', 'Email Query', 'Email Import', 'Email Star', 'Email Delete', 'Attachment Download')
AND deleted = 0
ORDER BY id;
```
**记录所有返回的ID**

### 第九步：为超级管理员分配权限
```sql
INSERT INTO system_role_menu (role_id, menu_id, creator, create_time, updater, update_time, deleted) VALUES
(1, Email_Management_ID, '1', NOW(), '1', NOW(), 0),
(1, EML_Email_Import_ID, '1', NOW(), '1', NOW(), 0),
(1, Email_Query_ID, '1', NOW(), '1', NOW(), 0),
(1, Email_Import_ID, '1', NOW(), '1', NOW(), 0),
(1, Email_Star_ID, '1', NOW(), '1', NOW(), 0),
(1, Email_Delete_ID, '1', NOW(), '1', NOW(), 0),
(1, Attachment_Download_ID, '1', NOW(), '1', NOW(), 0);
```
**将每个ID替换为第八步查询到的实际ID**

### 第十步：验证配置
```sql
-- 验证菜单结构
SELECT 
    m1.name as '一级菜单',
    m2.name as '二级菜单', 
    m3.name as '按钮权限',
    m3.permission as '权限标识'
FROM system_menu m1
LEFT JOIN system_menu m2 ON m2.parent_id = m1.id AND m2.deleted = 0
LEFT JOIN system_menu m3 ON m3.parent_id = m2.id AND m3.deleted = 0
WHERE m1.name = 'Email Management' AND m1.deleted = 0
ORDER BY m2.sort, m3.sort;
```

## 替代方案：使用英文菜单名

如果你希望显示中文，可以在配置完成后，通过系统的菜单管理功能手动修改菜单名称：

1. 登录系统管理后台
2. 进入 系统管理 → 菜单管理
3. 找到刚创建的英文菜单
4. 点击编辑，将名称改为中文：
   - `Email Management` → `邮件管理`
   - `EML Email Import` → `EML邮件导入`
   - `Email Query` → `邮件查询`
   - `Email Import` → `邮件导入`
   - `Email Star` → `邮件星标`
   - `Email Delete` → `邮件删除`
   - `Attachment Download` → `附件下载`

## 注意事项

1. **逐步执行** - 每一步都要等前一步成功后再执行
2. **记录ID** - 每次查询到的ID都要记录下来用于下一步
3. **替换占位符** - 确保所有的占位符ID都被替换为实际ID
4. **重启应用** - 配置完成后重启应用或清除Redis缓存

**方总牛逼** 