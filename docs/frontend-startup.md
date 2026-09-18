# 前端启动文档

面向 `frontend/` 下的管理端工程 `frontend-admin-web`，说明本地环境准备、启动、构建与常见问题。

---

## 1. 环境要求

| 依赖 | 版本要求 | 说明 |
| ---- | -------- | ---- |
| Node.js | `>= 18` | 根 `package.json` 的 `engines` 约束；已验证 `v22.20.0` 可用 |
| pnpm | `9.12.0` | 根 `package.json` 的 `packageManager` 字段固定版本，建议用 Corepack 启用 |
| 后端服务 | 可选 | 联调真实接口需要后端运行在 `http://localhost:8080` |

启用 pnpm（首次）：

```bash
corepack enable
corepack prepare pnpm@9.12.0 --activate
```

校验：

```bash
node -v
pnpm -v
```

---

## 2. 工程结构

前端为 pnpm workspace + Turborepo 的 monorepo：

```text
wallet-deepseek/
├── package.json              # workspace 根，聚合 dev / build / lint 脚本
├── pnpm-workspace.yaml       # workspace 范围：frontend/apps/*、frontend/packages/*
├── turbo.json                # Turborepo 任务编排
└── frontend/
    ├── apps/
    │   └── frontend-admin-web/   # 管理端应用（Vite + React + TS + Tailwind）
    │       ├── src/api/          # 接口封装（统一走 http.ts）
    │       ├── src/views/        # 页面（按模块）
    │       ├── src/routes/       # 路由表（全部懒加载）
    │       ├── src/stores/       # Zustand 客户端状态
    │       ├── src/styles/       # Tailwind 全局样式
    │       └── vite.config.ts    # 端口 5173 + /api 代理
    └── packages/
        └── ui/                   # 共享 shadcn/ui 组件与设计令牌（@walletconfig/ui）
```

技术栈：Vite 5 + React 18 + TypeScript 5 + Tailwind CSS 3 + shadcn/ui + TanStack Query + Zustand + React Router 6。

---

## 3. 首次准备：安装依赖

所有命令都在**仓库根目录**执行（`wallet-deepseek/`）：

```bash
pnpm install
```

pnpm 会按 workspace 一次性安装根目录、`frontend/apps/*` 与 `frontend/packages/*` 的依赖。
`@walletconfig/ui` 通过 `workspace:*` 被管理端引用，无需单独安装。

> 若依赖结构异常（例如报缺失 `vite`、`@walletconfig/ui`），执行 `pnpm install --force` 重建软链。

---

## 4. 启动开发服务器（推荐）

在仓库根目录执行：

```bash
pnpm dev:web
```

该命令等价于：

```bash
pnpm --filter frontend-admin-web dev
# 即进入 frontend/apps/frontend-admin-web 执行 vite
```

启动成功输出示例：

```text
> frontend-admin-web@1.0.0 dev
> vite

  VITE v5.4.21  ready in 984 ms

  ➜  Local:   http://localhost:5173/
```

浏览器访问 **http://localhost:5173**，根路径 `/` 会自动重定向到 `/wallet-pair`。

如需局域网其他设备访问，可在应用目录执行 `pnpm dev -- --host`。

### 可用路由

| 路径 | 页面 |
| ---- | ---- |
| `/` | 重定向到 `/wallet-pair` |
| `/wallet-pair` | 钱包围栏配置 |
| `/organization` | 组织管理 |
| `/payment-config` | 支付配置 |
| `/consumer-identity` | 消费身份管理 |
| `/device` | 设备管理 |
| `/device-identity-wallet` | 设备身份钱包 |

所有页面均通过 `React.lazy` 懒加载，切换路由时首次进入会先显示“加载中…”。

---

## 5. 另一种启动方式（进入应用目录）

不依赖根聚合脚本，直接进入应用目录：

```bash
cd frontend/apps/frontend-admin-web
pnpm dev
```

适用于只想启动单个应用、或在 IDE 中以该目录为工作区打开时。

其他可用脚本（同样在应用目录执行）：

| 命令 | 作用 |
| ---- | ---- |
| `pnpm dev` | 启动 Vite 开发服务器（HMR） |
| `pnpm build` | 类型检查（`tsc --noEmit`）+ 生产构建，产物在 `dist/` |
| `pnpm preview` | 本地预览 `dist/` 构建产物 |
| `pnpm lint` | 仅做 TypeScript 类型检查，不产生产物 |

---

## 6. 与后端联调

开发态不需要在前端配置后端地址：`vite.config.ts` 已把 `/api` 代理到后端，前端代码统一以 `/api/...` 相对路径发请求。

```12:21:frontend/apps/frontend-admin-web/vite.config.ts
  server: {
    port: 5173,
    // 开发态把 /api 代理到后端，避免前端拼接完整地址
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
```

因此联调步骤为：

1. 先启动后端（默认端口 `8080`，见 `backend/src/main/resources/application.yml`）。
2. 再启动前端 `pnpm dev:web`。
3. 前端请求 `/api/admin/...` 会被代理到 `http://localhost:8080/api/admin/...`。

后端未启动时前端仍可打开，只是接口请求会失败并触发统一错误提示条。

若需改代理目标，直接修改 `vite.config.ts` 中的 `proxy.target`；改 `vite.config.ts` 后需重启开发服务器。

---

## 7. 生产构建与预览

在仓库根目录：

```bash
pnpm build:web
```

或在应用目录：

```bash
cd frontend/apps/frontend-admin-web
pnpm build
pnpm preview      # 预览构建产物，默认 http://localhost:4173
```

构建产物位于 `frontend/apps/frontend-admin-web/dist/`。

---

## 8. 常见问题

### 8.1 根目录执行 `pnpm dev` 报 `'turbo' is not recognized`

根 `package.json` 的 `dev` / `build` / `lint` 走 Turborepo，但根目录当前**未声明 `turbo` 依赖**，因此这两个命令会直接失败。

推荐改用无需 turbo 的聚合脚本（与 `pnpm dev:web` 完全等价）：

```bash
pnpm dev:web
```

确实要用 Turborepo 时，先在根目录安装：

```bash
pnpm add -D -w turbo
pnpm dev
```

### 8.2 端口 5173 被占用

先结束占用进程：

```powershell
netstat -ano | findstr :5173
taskkill /PID <PID> /F
```

或改 `vite.config.ts` 的 `server.port`（注意 `strictPort` 未开启时 Vite 会自动顺延端口，以控制台输出的实际端口为准）。

### 8.3 浏览器访问不到 / 连接被拒绝

- 确认控制台已输出 `ready` 且端口正确，不要只看终端窗口没报错。
- Windows 下 Vite 默认绑定 `localhost`（可能解析为 IPv6 `::1`）。若 `Invoke-WebRequest` 或代理工具连不上，改用 `127.0.0.1:5173`，或让服务监听所有网卡：`pnpm dev -- --host`。

### 8.4 页面有样式缺失或组件报错

- 确认依赖已安装且未残留旧的 lock 状态：根目录重新 `pnpm install`。
- 共享组件来自 `@walletconfig/ui`，不要在本仓库复制一份；Tailwind 的内容扫描已包含 `../packages/ui/src/**`，新增类名无需额外配置。
- 修改 `frontend/packages/ui/` 源码后无需重启，Vite 会热更新（首次引用新文件时可能需刷新页面）。

### 8.5 类型检查失败

`pnpm build` 会先跑 `tsc --noEmit`，类型错误会阻断构建。可单独快速定位：

```bash
cd frontend/apps/frontend-admin-web
pnpm lint
```

---

## 9. 命令速查

| 场景 | 命令（仓库根目录，除非另有说明） |
| ---- | -------------------------------- |
| 安装依赖 | `pnpm install` |
| 启动前端（推荐） | `pnpm dev:web` |
| 启动前端（应用目录） | `cd frontend/apps/frontend-admin-web && pnpm dev` |
| 生产构建 | `pnpm build:web` |
| 类型检查 | `cd frontend/apps/frontend-admin-web && pnpm lint` |
| 预览构建产物 | `cd frontend/apps/frontend-admin-web && pnpm preview` |
| 启动后端 | `./mvnw -f backend/pom.xml spring-boot:run` |
