# EML邮件管理系统配置指南

## 概述
EML邮件管理系统已经开发完成，包含前端界面和后端接口。现在需要配置菜单权限来显示该功能模块。

## 前端组件已就绪
✅ **组件路径**：
- 邮件管理列表：`src/views/eml/EmailManagement.vue`
- 邮件详情页面：`src/views/eml/EmailDetail.vue`
- 相关工具函数：`src/utils/eml.ts`
- 类型定义：`src/types/eml/`

✅ **路由配置**：已添加到 `src/router/modules/remaining.ts`

## 配置步骤

### 第一步：执行数据库菜单配置
运行SQL脚本 `sql/mysql/eml_email_menu_config.sql` 来添加菜单：

```sql
-- 1. 查询系统管理菜单ID
SELECT id FROM system_menu WHERE name = '系统管理' AND deleted = 0;

-- 2. 添加EML邮件管理主菜单（将parent_id替换为上一步查到的ID）
INSERT INTO system_menu (
    name, permission, type, sort, parent_id, path, icon, component, component_name, 
    status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted
) VALUES (
    'EML邮件管理', '', 1, 35, 1, 'eml', 'ep:message', '', '', 
    0, 1, 1, 1, '1', NOW(), '1', NOW(), 0
);

-- 3. 查询刚插入的菜单ID
SELECT id as 'EML邮件管理菜单ID' FROM system_menu WHERE name = 'EML邮件管理' AND deleted = 0 ORDER BY id DESC LIMIT 1;

-- 4. 添加子菜单（将parent_id替换为上一步查到的ID）
INSERT INTO system_menu (
    name, permission, type, sort, parent_id, path, icon, component, component_name, 
    status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted
) VALUES (
    '邮件管理', 'system:eml:list', 2, 1, [EML邮件管理菜单ID], 'email-management', 'ep:message-box', 
    'eml/EmailManagement', 'EmailManagement', 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0
);
```

### 第二步：为角色分配权限
如果您的用户不是超级管理员，需要为角色分配相应权限：

1. 登录系统管理后台
2. 进入"系统管理" → "角色管理"
3. 编辑需要使用该功能的角色
4. 在菜单权限中勾选"EML邮件管理"相关权限

### 第三步：启动前端项目
```bash
cd yudao-ui-admin-vue3
pnpm install
pnpm dev
```

### 第四步：验证功能
1. 重新登录系统
2. 在左侧菜单栏的"系统管理"下应该能看到"EML邮件管理"菜单
3. 点击进入邮件管理页面

## 功能特性

### 主要功能
- ✅ EML文件批量导入（ZIP压缩包）
- ✅ 邮件列表查看和搜索
- ✅ 邮件详情查看
- ✅ 附件下载
- ✅ 邮件标星收藏
- ✅ 批量删除操作
- ✅ 导入历史记录

### 技术架构
- **前端**: Vue3 + Element Plus + TypeScript
- **状态管理**: Pinia
- **路由**: Vue Router
- **权限**: 基于菜单的RBAC权限控制

## 权限说明

| 权限标识 | 权限名称 | 说明 |
|---------|---------|------|
| `system:eml:list` | 邮件列表 | 查看邮件列表页面 |
| `system:eml:query` | 邮件查询 | 查询邮件详情 |
| `system:eml:import` | 邮件导入 | 上传和导入EML文件 |
| `system:eml:star` | 邮件星标 | 标记/取消星标 |
| `system:eml:delete` | 邮件删除 | 删除邮件 |
| `system:eml:download` | 附件下载 | 下载邮件附件 |

## 菜单结构
```
系统管理
└── EML邮件管理
    └── 邮件管理 (/eml/email-management)
        ├── 邮件查询
        ├── 邮件导入  
        ├── 邮件星标
        ├── 邮件删除
        └── 附件下载
```

## 常见问题

### Q: 菜单不显示怎么办？
A: 检查以下几点：
1. 确认数据库菜单配置是否正确执行
2. 确认当前用户角色是否有相应权限
3. 清除浏览器缓存后重新登录

### Q: 页面报404错误？
A: 确认路由配置是否正确添加到 `src/router/modules/remaining.ts`

### Q: 没有操作按钮权限？
A: 确认操作按钮的权限配置是否正确，检查 `system_role_menu` 表中的权限分配

## 后端接口
确保后端已实现以下接口：
- `POST /admin-api/system/email/upload-zip` - 上传ZIP文件
- `GET /admin-api/system/email/page` - 分页查询邮件
- `GET /admin-api/system/email/{id}` - 获取邮件详情
- `PUT /admin-api/system/email/{id}/toggle-star` - 切换星标
- `DELETE /admin-api/system/email` - 批量删除邮件
- `GET /admin-api/system/email/attachment/{id}/download` - 下载附件

## 总结
完成以上配置后，EML邮件管理功能就可以正常使用了。如有问题请检查控制台错误信息或联系开发人员。

---
**方总牛逼** 🚀 