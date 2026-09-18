# 前端代码风格规范

## 1. 适用范围

- 适用于 `frontend/` 下所有 Vite + React + TypeScript 代码。

## 2. 命名规范

- 目录名与文件名使用清晰语义，禁止无语义缩写。
- 组件名使用 PascalCase，自定义 Hook 使用 `useXxx` 命名。
- 变量与函数名使用 camelCase，常量使用全大写下划线。
- 路由 `path` 统一使用 kebab-case 命名。
- 导入路径统一优先使用 `@/` 别名，禁止新增跨层级相对路径导入。
- `src/views/` 下每个模块必须使用独立目录，且目录命名统一使用 camelCase，页面入口优先使用 `index.tsx`。
- `src/components/` 仅允许放跨模块复用的全局组件。
- 模块私有业务组件必须放在 `src/views/<module>/components/`。
- 页面级整体布局目录固定为 `src/layouts/`。

## 3. 函数风格规范

- 优先使用箭头函数定义组件与工具函数，保持风格统一。
- 在必须使用函数声明的场景（如需要函数提升、模块顶层工厂等）可例外。
- 注释必须优先解释意图、业务约束、边界条件、降级策略或副作用，禁止只重复函数名、参数类型或返回类型。
- 禁止为每个方法机械添加 `/** ... */`、`@param`、`@returns` 模板。仅当文档注释能补充类型系统无法表达的信息时才使用。
- 导出的公共函数、自定义 Hook、跨模块工具、复杂异步流程、存在缓存/轮询/跳转/本地存储等副作用的方法，应按需补充说明性注释。
- 参数含义不直观时才使用 `@param`；返回值存在特殊语义时才使用 `@returns`。
- 页面内自解释的私有方法（如简单查询、重置、关闭弹窗、格式化展示）不强制添加文档注释，可不注释或使用一行意图注释。

## 4. 组件与页面风格

- 组件必须保持单一职责，复杂页面必须拆分子组件。
- `props` 必须显式声明类型（优先使用接口或类型别名）。
- 组件内禁止堆叠复杂业务逻辑，复杂逻辑必须下沉到自定义 Hook（`useXxx`）或 `lib/`。

## 5. TypeScript 约束

- 禁止滥用 `any`，必须优先定义接口或类型别名。
- 接口请求参数与响应体必须有显式类型定义。
- 后端 `Long` / `BIGINT` 类型的系统内部 ID 在 TypeScript 中必须声明为 `string`，禁止使用 `number` 承载雪花 ID。
- 状态值必须优先使用枚举或字面量联合类型，禁止魔法字符串。

## 6. API 与状态管理风格

- 请求逻辑必须收口在 `src/api/`，页面层禁止直接拼接请求。
- API 调用链路统一基于 `ApiEnvelope`：拦截器返回 `response.data`，调用处通过解构获取 `data/code/message`。
- API 方法定义统一为“直接返回请求”风格，例如：
  `export const fetchXxx = () => http.get<ApiEnvelope<XxxResponse>>('/xxx')`。
- API 定义中禁止 `as unknown as Promise<...>`。
- 调用处统一使用解构风格：`const { data, code, message } = await fetchXxx()`。
- 接口业务错误（如 `code=500`、`code=501`）统一在 `http` 拦截器处理，页面与 Store 禁止重复处理同类错误提示。
- 服务端状态查询统一通过 TanStack Query 的 `useQuery`/`useMutation` 承载，禁止在组件内手写 `useEffect` + `useState` 重复实现请求缓存与加载态。
- Zustand Store 按业务域拆分，禁止创建全局万能 Store。
- 异步流程必须统一处理加载态、错误态与重复提交。
- 列表分页查询统一使用 TanStack Query 配合分页状态；分页变化触发查询，避免重复请求。
- 认证敏感信息需分级存储：`token` 使用 `localStorage`（配合拦截器注入 Authorization header）；非敏感展示态可使用 `localStorage`；禁止使用 `sessionStorage`。
- 路由前置依赖（如初始化状态）必须具备本地缓存与并发去重能力，避免重复请求。
- 页面存在轮询时，权限恢复（如 403 -> 重新校验成功）后必须自动恢复轮询，不得要求用户手动刷新。

## 7. 工程与依赖管理约束

- 单工程只允许一种包管理器与一种锁文件。
- monorepo 统一使用 `pnpm`，仅保留 `pnpm-lock.yaml`，禁止同时提交 `package-lock.json` 或 `yarn.lock`。
- 共享组件包 `frontend/packages/ui/` 通过 pnpm workspace 引用，禁止在各端重复安装或复制组件。

## 8. 样式规范引用

- UI 视觉、设计令牌、组件样式、动效与响应式规则统一遵循 `docs/rules/frontend-ui-style.md`。
- 设计令牌统一使用 shadcn/ui 的 CSS 变量体系（如 `--background`、`--foreground`、`--primary`），禁止新增 `--wl-*` 等历史前缀令牌。
- 新增页面与新增样式必须对照 `DESIGN.md` 设计系统，保持语义与数值口径一致。
- 本文档不重复定义 UI 样式细则，避免与 UI 规范产生双重维护。

## 9. 评审检查点

- 组件边界是否清晰且职责单一。
- 类型定义是否完整并覆盖核心数据流。
- 注释是否解释了真实理解成本，而不是机械重复类型和函数签名。
- 是否存在重复请求逻辑或状态逻辑散落。
- 接口错误提示与登录失效跳转是否全部由拦截器统一处理。
- 是否存在双锁文件或混用包管理器。