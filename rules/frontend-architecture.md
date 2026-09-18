# 前端架构规范

## 1. 适用范围

- 适用于 `frontend/` 下所有 Vite + React + TypeScript 代码。
- 前端工程为 pnpm workspace + Turborepo monorepo，按端拆分为 `frontend/apps/frontend-admin-web/` 与 `frontend/apps/frontend-user-web/`，两端独立构建与发布；共享 shadcn/ui 组件与设计令牌放在 `frontend/packages/ui/`。

## 2. 目录约束

### 2.1 目录结构

```text
frontend/
├── apps/
│   ├── frontend-admin-web/          # [管理端工程] 独立构建与发布
│   │   ├── src/
│   │   │   ├── api/                 # [接口层] 请求封装与接口定义
│   │   │   ├── components/          # [全局组件] 跨模块复用组件
│   │   │   ├── layouts/             # [壳层] 页面级整体布局
│   │   │   ├── routes/              # [路由层] 路由表与导航守卫
│   │   │   ├── stores/              # [状态层] Zustand 客户端状态，按业务域拆分
│   │   │   ├── views/              # [页面层] 按模块组织页面
│   │   │   │   └── <module>/
│   │   │   │       ├── index.tsx    #   模块主页面
│   │   │   │       └── components/  #   模块私有业务组件
│   │   │   ├── styles/             # [样式层] 全局样式与 Tailwind 入口
│   │   │   ├── lib/                # [工具层] 无状态工具函数
│   │   │   ├── mocks/              # [Mock 层] MSW handlers/scenarios
│   │   │   └── main.tsx            # [入口] 应用启动与全局挂载
│   │   ├── public/
│   │   ├── vite.config.ts
│   │   └── tsconfig.json
│   └── frontend-user-web/           # [用户端工程] 独立构建与发布
│       └── ...（结构同管理端）
└── packages/
    └── ui/                          # [共享 UI 包] shadcn/ui 组件 + Tailwind 主题 + 设计令牌
        ├── src/
        │   ├── components/          # shadcn/ui 组件
        │   └── styles/             # 共享 Tailwind 配置与令牌
        └── package.json
```

### 2.2 目录约束

- 页面组件必须放在 `src/views/`。
- `src/views/` 下按模块建目录，目录命名统一使用 camelCase。
- 一个路由页面对应模块目录下的 `index.tsx`。
- `src/components/` 只允许放可被所有模块复用的全局组件。
- 模块内业务组件必须放在 `src/views/<module>/components/`。
- 页面级整体布局必须放在 `src/layouts/`。
- API 请求封装必须放在 `src/api/`，禁止在视图层直接发请求。
- Zustand 客户端状态必须放在 `src/stores/` 并按业务域拆分。
- 跨端共享的 shadcn/ui 组件与设计令牌必须放在 `frontend/packages/ui/`，两端通过 workspace 依赖引用，禁止在各端重复复制组件。

## 3. 路由与权限约束

- 使用 React Router，结合菜单配置实现动态路由与权限治理。
- 路由 `path` 必须使用 kebab-case 命名。
- 登录后必须按权限动态加载路由。
- 无权限页面必须返回明确的权限反馈页，禁止空白页。

## 4. API 层约束

- 请求与响应拦截器必须统一处理 Token 注入与通用错误（基于 fetch 封装或 axios）。
- 拦截器对业务码统一处理：`code=200` 返回 `ApiEnvelope`（即 `response.data`）；`code=500` 统一提示后端 `message`；`code=501` 统一提示登录失效并跳转登录页。
- 业务错误码必须在拦截器统一处理提示与跳转。
- 页面层与业务 Store 不得重复编写接口错误提示 `catch` 逻辑。
- 每个 API 方法必须声明参数类型与响应类型。
- API 定义必须使用直接返回形式，禁止在 API 层二次解包 `response.data` 或 `response.data.data`。
- 调用层统一采用解构写法（如 `const { data, code, message } = await fetchXxx()`）。
- 业务页面禁止拼接鉴权 Header 与错误提示逻辑。

## 5. 状态管理约束

- 服务端状态（接口数据缓存、加载态、错误态、并发覆盖）统一使用 TanStack Query，禁止用 Zustand 承载服务端数据缓存。
- Zustand 仅承载客户端 UI 状态，按业务域拆分，禁止创建全局万能 Store。
- 临时页面态优先组件本地状态，禁止无意义全局化。
- 缓存数据统一存放在 `localStorage`，禁止使用 `sessionStorage`。

## 6. 组件与样式约束

- 组件职责必须单一，复杂页面必须拆分子组件。
- `props` 必须显式声明类型。
- 样式统一使用 Tailwind CSS；全局样式通过 `src/styles/` 引入 Tailwind 指令。
- 共享 shadcn/ui 组件经 `frontend/packages/ui/` 引用，禁止在各端重新实现同语义组件。
- 禁止直接改写 shadcn/ui 组件源码样式，只允许通过 Tailwind 类名或主题变量扩展。

## 7. 质量与性能约束

- 路由必须默认懒加载（`React.lazy` + `Suspense`）。
- 列表场景必须有分页或虚拟滚动策略。
- 关键页面必须覆盖加载态、空态、错误态、权限态。
- 提交前必须完成前端构建与核心流程回归。

## 8. Mock 数据与 MSW 约束

- 前端原型阶段允许使用 mock 数据，但 mock 不得污染页面代码；`src/views/`、`src/stores/`、`src/api/` 中禁止编写 mock 分支逻辑，页面与 Store 仍必须按真实 API 调用。
- Mock 必须采用网络边界拦截方案，统一使用 MSW（Mock Service Worker）拦截 HTTP 请求，不得在组件、Store 或 API 封装中通过环境变量切换假数据。
- Mock 代码必须集中放在 `src/mocks/` 下，并按 `handlers/`、`scenarios/`、`utils/` 等职责拆分；业务页面不得直接导入 `src/mocks/` 内容。
- Mock 仅允许在开发 mock 模式启用，即同时满足 `import.meta.env.DEV` 与 `VITE_API_MODE=mock`；联调、测试环境联后端与生产环境必须走 live API。
- 生产构建不得包含 mock 运行时代码，不得输出 `mockServiceWorker.js` 或相关 mock worker 产物；构建配置必须显式规避或清理这些产物。
- `mockServiceWorker.js` 必须放在应用 `public` 根目录，即 `public/mockServiceWorker.js`。
- 启动 mock worker 前必须自动清理旧的 mock service worker 注册，避免新旧 worker 协议不一致导致运行时报错。
- Mock worker 必须处理浏览器长时间空闲或标签页恢复后 service worker 被回收/重启的场景。
- 后续新增或优化 mock 数据时，必须继续遵守网络边界拦截方案，禁止重新引入页面层、Store 层或 API 层 mock 污染。