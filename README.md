# 红书智创 — 小红书 AI 自动创作更新系统

> 多端适配的小红书 AI 创作与自动发布平台

## 项目简介

红书智创是一套完整的 **小红书内容创作与自动发布系统**，提供从 AI 选题、文案生成、配图制作、内容审核到定时自动发布的全流程自动化能力。

系统采用 **C/S 架构 + 微服务** 设计，后端基于 Spring Boot 提供服务，发布引擎使用 Python + Playwright 浏览器自动化实现小红书内容发布。

### 核心功能

- **AI 智能创作** — Agent 驱动的标题生成、文案撰写、配图制作
- **多级审核流程** — AI 初筛 + 人工审核的内容质量保障
- **定时自动发布** — Redis Stream 队列 + Playwright 浏览器自动化
- **多端覆盖** — PC 客户端、鸿蒙 App、微信小程序、Web 管理后台
- **数据看板** — 阅读量、互动量、粉丝增长等数据可视化
- **账号管理** — 安全的 Cookie 加密存储（AES-256-GCM）

---

## 技术架构

```
┌──────────────────────────────────────────────────────┐
│                    客户端层                            │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌─────────┐ │
│  │ PC客户端 │ │ 鸿蒙App  │ │ 微信小程序│ │ 管理后台│ │
│  │ Electron │ │ ArkTS    │ │ UniApp   │ │ Vue3    │ │
│  └────┬─────┘ └────┬─────┘ └────┬─────┘ └────┬────┘ │
└───────┼────────────┼────────────┼────────────┼───────┘
        │            │            │            │
   ┌────▼────────────▼────────────▼────────────▼───┐
   │              Nginx (反向代理)                    │
   └────────────┬──────────────────┬────────────────┘
                │                  │
   ┌────────────▼──────┐  ┌───────▼──────────────┐
   │  Spring Boot API  │  │  Python 发布引擎      │
   │  (Java 21)        │  │  (FastAPI +Playwright)│
   │  ┌──────┐ ┌─────┐ │  │  ┌──────┐ ┌───────┐  │
   │  │用户  │ │AI   │ │  │  │发布器│ │队列消费│  │
   │  │模块  │ │模块 │ │  │  └──────┘ └───────┘  │
   │  └──────┘ └─────┘ │  └──────────────────────┘
   └──────┬────────────┘
          │
   ┌──────▼──────┐ ┌──────▼──────┐ ┌──────▼──────┐
   │   MySQL 8.0  │ │  Redis 7.x  │ │    MinIO     │
   │   (主数据库)  │ │  (缓存/队列) │ │  (对象存储)   │
   └─────────────┘ └─────────────┘ └─────────────┘
```

---

## 技术栈

| 层级 | 技术 | 说明 |
|------|------|------|
| **PC 客户端** | Electron + Vue3 + Pinia + Vite | 桌面应用，完整功能体验 |
| **鸿蒙 App** | HarmonyOS ArkTS | 原生鸿蒙应用 |
| **微信小程序** | UniApp (Vue3) | 微信小程序端 |
| **管理后台** | Vue3 + Pinia + Vite | Web 管理后台 |
| **API 服务** | Spring Boot 3.x + MyBatis-Plus | JDK 21，JWT 认证 |
| **发布引擎** | Python + FastAPI + Playwright | 浏览器自动化发布 |
| **数据库** | MySQL 8.0 | 主数据库（10 张表） |
| **缓存/队列** | Redis 7.x | 缓存 + Stream 消息队列 |
| **对象存储** | MinIO | 图片/文件存储 |
| **反向代理** | Nginx | 路由分发 |
| **容器化** | Docker Compose | 一键部署 |

---

## 项目结构

```
小红书项目/
├── client-pc/                  # PC 客户端 (Electron + Vue3)
│   ├── electron/               # Electron 主进程
│   └── src/
│       ├── views/              # 页面组件 (Dashboard/AiCreate/Review/Queue 等)
│       ├── components/         # 公共组件 (Sidebar/Topbar)
│       ├── router/             # 路由配置
│       ├── store/              # Pinia 状态管理
│       └── api/                # HTTP 请求封装
│
├── client-harmony/             # 鸿蒙客户端 (ArkTS)
│   └── entry/src/main/ets/
│       ├── pages/              # 页面 (Index/Login/Accounts/Settings)
│       ├── common/             # 公共工具 (Api/Theme)
│       └── entryability/       # Ability 入口
│
├── client-mini/                # 微信小程序 (UniApp)
│   └── pages/                  # 页面 (index/create/messages/profile 等)
│
├── client-admin/               # 管理后台 (Vue3 + Vite)
│   └── src/views/              # 视图 (Overview/Users/AiModels/Agents 等 9 页)
│
├── server/                     # 后端服务 (Spring Boot)
│   ├── sql/init.sql            # 数据库初始化脚本
│   └── xhs-server/src/main/java/com/hongshu/
│       ├── common/             # 公共模块 (安全/配置/工具)
│       └── modules/            # 业务模块
│           ├── user/           # 用户系统
│           ├── account/        # 账号管理
│           ├── ai/             # AI 服务
│           ├── content/        # 内容管理
│           ├── publish/        # 发布队列
│           ├── analytics/      # 数据分析
│           └── admin/          # 管理后台
│
├── publish-engine/             # Python 发布引擎
│   └── app/
│       ├── main.py             # FastAPI 入口
│       ├── api/routes.py       # REST API
│       └── engine/
│           ├── publisher.py    # Playwright 发布器
│           └── queue_consumer.py # Redis Stream 消费者
│
├── nginx/                      # Nginx 配置
├── docker-compose.yml          # Docker 编排
├── .env.example                # 环境变量模板
├── architecture-design.md       # 架构设计文档
├── 小红书AI自动创作更新系统开发文档.md  # 需求开发文档
└── *.html                      # 产品原型 (5 个)
```

---

## 快速启动

### 前置要求

- JDK 21+
- Node.js 18+
- Python 3.11+
- Docker & Docker Compose
- MySQL 8.0 / Redis 7.x (或使用 Docker 提供)

### Docker 一键部署

```bash
# 1. 复制并配置环境变量
cp .env.example .env
# 编辑 .env 填入实际配置

# 2. 启动所有服务
docker-compose up -d

# 3. 查看服务状态
docker-compose ps
```

服务启动后：
- API 服务：`http://localhost:8080`
- 发布引擎：`http://localhost:8001`
- MinIO Console：`http://localhost:9001`
- PC 客户端（开发模式）：`http://localhost:5173`
- 管理后台（开发模式）：`http://localhost:3002`

### 本地开发

#### 1. 启动后端

```bash
cd server/xhs-server
mvn spring-boot:run
```

#### 2. 启动发布引擎

```bash
cd publish-engine
pip install -r requirements.txt
python -m app.main
```

#### 3. 启动 PC 客户端

```bash
cd client-pc
npm install
npm run dev
```

#### 4. 启动管理后台

```bash
cd client-admin
npm install
npm run dev
```

#### 5. 启动小程序

```bash
cd client-mini
npm install
npm run dev:mp-weixin
# 在微信开发者工具中导入 dist/dev/mp-weixin
```

---

## 数据库初始化

首次启动后，执行 `server/sql/init.sql` 初始化数据库：

- **10 张业务表**：user、xiaohongshu_account、agent、ai_model_api、ai_content、publish_queue、publish_log、xh_account_data、api_call_log、operation_log、system_config
- **4 个预设 Agent**：生活分享、美食探店、旅行攻略、穿搭时尚
- **7 条系统配置**：注册方式、验证码有效期等

---

## 核心流程

### AI 创作流程

```
选择 Agent → 输入主题 → AI 生成候选标题 → 确认标题 →
AI 生成文案 + 配图 → 内容审核 → 加入发布队列 → 定时发布
```

### 发布流程

```
用户提交发布任务 → 存入 publish_queue →
Quartz 定时扫描 → 写入 Redis Stream →
Python engine 消费 → Playwright 自动化发布 →
更新状态 & 回调记录日志
```

---

## 安全设计

| 项目 | 方案 |
|------|------|
| 用户密码 | MD5 + 随机 Salt |
| 小红书 Cookie | AES-256-GCM 加密存储 |
| API 认证 | JWT (HS256，7 天过期) |
| 管理后台 | JWT + 内部密钥双重验证 |
| 发布限流 | 单账号每日 ≤5 篇，间隔 ≥30 分钟 |
| 传输安全 | HTTPS (Nginx 终止 SSL) |

---

## 发展路线

| 阶段 | 时期 | 目标 |
|------|------|------|
| Phase 1 | 1-3 月 | MVP：核心链路跑通 |
| Phase 2 | 3-6 月 | 多端覆盖 + 生产级稳定性 |
| Phase 3 | 6-12 月 | 微服务拆分 + 多 Agent 市场 |
| Phase 4 | 12 月+ | 开放 API + 多平台分发 |

---

## License

MIT License
