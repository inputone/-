# TreeHole 前端重构方案 - Vue 3

## 项目概述

基于 TreeHole（树洞）后端 API，使用 Vue 3 重构前端，采用简约风格和分页 UI，解决当前评论跳到底部、帖子无分页等问题。

## 技术选型

| 选择 | 说明 |
|------|------|
| Vue 3 CDN | `vue.global.prod.min.js`，无需构建工具 |
| 纯前端分离 | HTML + CSS + JS 三文件分离 |
| 路由方案 | `v-if` 视图切换，无 Vue Router |
| 状态管理 | Vue 3 `reactive()` + `provide/inject` |

## 文件结构

```
src/main/resources/static/
├── index.html        # 主页面 HTML 骨架
├── style.css         # 全局样式（简约风格）
└── app.js            # Vue 3 应用逻辑
```

## 视图结构

```
App
├── LoginView         # 登录/注册视图
└── MainLayout        # 主布局
    ├── HeaderBar     # 顶部栏（Logo + 用户信息 + 退出）
    ├── PostView      # 帖子列表 + 分页
    ├── CommentView   # 评论详情（点击帖子后切换）
    └── AdminView     # 管理面板（仅管理员可见）
```

## 页面流程

```
登录页
  ↓ 登录成功
主布局（Header + Tab）
  ├── 树洞 Tab → PostView（帖子列表 + 分页）
  │     ↓ 点击帖子
  │   CommentView（评论详情 + 评论输入）
  │     ↓ 返回
  │   回到 PostView（保留分页位置）
  └── 管理 Tab（仅管理员）→ AdminView
```

## API 接口对应

| 前端操作 | 后端 API | 说明 |
|----------|---------|------|
| 登录 | `POST /user/login` | 表单提交 username/password |
| 注册 | `POST /user/register` | 表单提交 username/password |
| 获取当前用户 | `GET /user/me` | Session 中获取用户信息 |
| 退出 | `GET /user/logout` | 销毁 Session |
| 发帖 | `POST /post/publish` | content 参数 |
| 帖子列表（分页） | `GET /post/page?page=&size=` | 返回 {list, total, page, size} |
| 删除帖子 | `DELETE /post/delete` | id 参数 |
| 管理员删帖 | `DELETE /admin/post/{postId}` | 路径参数 |
| 加载评论 | `GET /comment/post/{postId}` | 路径参数 |
| 发评论 | `POST /comment` | postId + content 参数 |
| 删除评论 | `DELETE /comment/{id}` | 路径参数 |
| 管理员删评论 | `DELETE /admin/comment/{commentId}` | 路径参数 |
| 待审核用户列表 | `GET /admin/waitAuditUserList` | - |
| 审核用户 | `POST /admin/auditUser` | username + status 参数 |

## 数据结构

### User
```javascript
{
  id: Long,
  username: String,
  status: Integer  // 0=待审核, 1=正常, 2=封禁, 99=管理员
}
```

### Post
```javascript
{
  id: Long,
  userId: Long,
  content: String,
  isDeleted: Integer,
  createTime: LocalDateTime  // ISO 字符串
}
```

### Comment
```javascript
{
  id: Long,
  postId: Long,
  userId: Long,
  content: String,
  isDeleted: Integer,
  createTime: LocalDateTime
}
```

### 分页响应
```javascript
{
  code: 200,
  msg: String,
  data: {
    list: Post[],
    total: Long,
    page: Integer,
    size: Integer
  }
}
```

## UI 设计（简约风格）

### 配色方案
```
背景色:     #f5f6f7  (浅灰)
卡片背景:   #ffffff  (白色)
主色:      #4a90d9  (蓝色)
成功色:    #52c41a  (绿色)
危险色:    #ff4d4f  (红色)
文字主色:  #262626  (深灰)
文字次色:  #8c8c8c  (中灰)
边框色:    #e8e8e8  (边框灰)
hover 色:  #f0f7ff  (浅蓝背景)
```

### 字体
```
font-family: -apple-system, BlinkMacSystemFont, 'PingFang SC', 'Microsoft YaHei', sans-serif
```

### 间距
```
卡片内边距:  24px
卡片间距:    16px
卡片圆角:    8px
按钮圆角:    6px
输入框圆角:  6px
```

### 组件样式

#### 卡片
```css
background: #fff;
border: 1px solid #e8e8e8;
border-radius: 8px;
padding: 24px;
box-shadow: 0 1px 3px rgba(0,0,0,0.04);
transition: box-shadow 0.2s;
```
hover: `box-shadow: 0 4px 12px rgba(0,0,0,0.08);`

#### 按钮
```css
.primary:  background #4a90d9, color #fff, hover #3a7fc8
.ghost:   background transparent, border 1px solid #e8e8e8, hover border #4a90d9
.danger:  background #ff4d4f, color #fff, hover #e63e3f
.sm:      padding 4px 12px, font-size 12px
```

#### 输入框
```css
background: #fff;
border: 1px solid #e8e8e8;
border-radius: 6px;
padding: 10px 14px;
font-size: 14px;
transition: border-color 0.2s;
```
focus: `border-color: #4a90d9;`

#### 分页组件
```
[← 上一页]  [1]  [2]  [3]  ...  [8]  [下一页 →]

当前页: background #4a90d9, color #fff
普通页: background transparent, color #262626
disabled: opacity 0.4, cursor not-allowed
```

#### Toast 提示
```
位置: 顶部居中
样式: 圆角卡片 + 图标 + 文字
动画: fadeIn + slideDown
自动消失: 2.5s
```

## 功能详细设计

### 1. 登录/注册视图

- 切换按钮：登录 / 注册 两种模式
- 登录：用户名 + 密码 → `POST /user/login`
- 注册：用户名 + 密码（≥6位）→ `POST /user/register`
- 错误提示：Toast 显示后端返回的 msg
- 注册成功提示：需要管理员审核

### 2. 帖子列表视图（PostView）

- **发布框**：textarea（200字限制）+ 发布按钮
- **帖子卡片**：
  - 内容（最多显示3行，超出省略）
  - 时间（相对时间：刚刚/X分钟前/X小时前）
  - 评论按钮（显示评论数量）
  - 删除按钮（自己的帖子显示，管理员可删除任意）
- **分页控件**：
  - 显示：总条数、当前页/总页数
  - 上一页/下一页 + 页码数字
  - 每页 10 条
- **空状态**：树洞空空如也，来发第一条

### 3. 评论详情视图（CommentView）

- **顶部**：返回按钮 + 帖子内容 + 帖子时间
- **评论列表**：
  - 评论内容 + 时间
  - 删除按钮（自己的评论，管理员可删除任意）
- **评论输入**：textarea（300字限制）+ 发送按钮
- **空状态**：暂无评论，来评论吧
- **返回行为**：回到 PostView，保留分页位置

### 4. 管理视图（AdminView）

- **待审核用户列表**：
  - 用户名 + 注册时间
  - 通过按钮（绿色）+ 封禁按钮（红色）
- **操作反馈**：Toast 提示操作结果
- **空状态**：暂无待审核用户

## 组件核心逻辑（app.js）

### 状态定义
```javascript
const state = reactive({
  view: 'login',          // 'login' | 'main'
  tab: 'posts',           // 'posts' | 'admin'
  user: null,             // 当前登录用户
  posts: [],             // 帖子列表
  total: 0,               // 总条数
  page: 1,                // 当前页
  size: 10,               // 每页条数
  currentPost: null,      // 当前查看的帖子
  comments: [],           // 当前帖子评论
  waitAuditUsers: [],    // 待审核用户
  loading: false,
  toast: null
})
```

### 关键函数
```javascript
// 登录/注册
async function login()
async function register()
async function logout()
async function checkLogin()

// 帖子
async function loadPosts()
async function publishPost()
async function deletePost(postId)
async function adminDeletePost(postId)

// 评论
function openPost(post)
function closeComment()
async function loadComments(postId)
async function publishComment()
async function deleteComment(commentId)
async function adminDeleteComment(commentId)

// 管理
async function loadWaitAuditUsers()
async function auditUser(username, status)

// 工具
function showToast(msg, type)
function formatTime(time)
function escapeHtml(str)
```

### 时间格式化
```javascript
function formatTime(time) {
  if (!time) return ''
  const d = new Date(time)
  const now = new Date()
  const diff = now - d
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  return d.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })
}
```

### XSS 防护
```javascript
function escapeHtml(str) {
  const div = document.createElement('div')
  div.textContent = str
  return div.innerHTML
}
```

## 实现步骤

### Step 1: 创建 style.css
- 定义 CSS 变量（颜色、间距、圆角）
- 基础重置样式
- 布局样式（Header、Main、Card）
- 组件样式（Button、Input、Textarea、Pagination）
- 工具类（hidden、loading、empty-state）

### Step 2: 创建 app.js
- 引入 Vue 3 CDN
- 定义 API 封装函数
- 定义全局状态
- 定义工具函数
- 定义各个视图组件（Login、Header、PostList、PostCard、Pagination、Comment、Admin）
- 挂载 Vue 应用

### Step 3: 创建 index.html
- HTML 骨架结构
- 引入 Vue 3 CDN（生产版）
- 引入 style.css
- 引入 app.js
- Toast 容器

### Step 4: 测试验证
- 登录/注册流程
- 发帖 + 分页
- 评论 + 查看评论
- 删除功能
- 管理员功能
- Toast 提示
- 错误处理

## 注意事项

1. **用户信息暴露**：`userId` 通过 API 返回，前端不显示，但需保留用于权限判断
2. **相对时间**：前端实时刷新已显示的相对时间
3. **分页状态**：进入评论详情后返回，保留分页位置和页码
4. **删除反馈**：删除后立即从列表移除，服务器端已是逻辑删除
5. **敏感词**：后端 ForbiddenWordUtil 已处理，前端只做字数限制
