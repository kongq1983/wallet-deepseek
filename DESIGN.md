# AI 研发平台前端统一设计系统

> 本文档是前端视觉与组件的唯一事实来源（source of truth）。新增或修改前端页面、组件、样式、视觉资产时，必须先读取并遵循本系统。

## 1. 定位与原则

- AI 研发平台是面向研发人员的内部工具型平台，前端设计语言为“研发工作台”：专业、高效、低干扰、信息密度优先。
- 不追求宣传式视觉，强调可读性、操作效率与一致性。
- 所有视觉决策（色彩、间距、圆角、组件形态）必须可追溯到本系统的令牌与规范，禁止页面内即兴取值。

## 2. 技术基线

- 组件库：shadcn/ui（基于 Radix UI + Tailwind CSS）。
- 样式：Tailwind CSS 工具类 + CSS 变量令牌。
- 图标：lucide-react。
- 主题：亮色（默认）/ 暗色，通过 `.dark` 类切换。
- 共享位置：`frontend/packages/ui/`，管理端与用户端共用，禁止各端重复维护。

## 3. 设计令牌

### 3.1 色彩

- 采用 shadcn/ui 标准 HSL 变量体系，集中定义在 `frontend/packages/ui/src/styles/globals.css`：
  - `--background` / `--foreground`：页面背景与正文
  - `--card` / `--card-foreground`：卡片表面与文字
  - `--popover` / `--popover-foreground`：弹层表面与文字
  - `--primary` / `--primary-foreground`：主操作色（主按钮、链接、聚焦）
  - `--secondary` / `--secondary-foreground`：次要操作色
  - `--muted` / `--muted-foreground`：弱化背景与辅助文本
  - `--accent` / `--accent-foreground`：悬停/选中强调
  - `--destructive` / `--destructive-foreground`：危险/错误
  - `--border`：边框
  - `--input`：输入框边框
  - `--ring`：聚焦环
- 主色（`--primary`）采用沉稳的工具型主色（深蓝或中性色），体现专业感；具体色值在 `packages/ui` 集中定义，禁止在各端散落覆盖。
- 状态语义：成功用绿色、警告用琥珀色、危险用 `--destructive`，通过 Tailwind 语义类或补充变量表达，禁止页面内硬编码色值。
- 暗色模式令牌与亮色同语义对应，通过 `.dark` 类覆盖变量值。

### 3.2 圆角

- 统一使用 `--radius` 变量（默认 `0.5rem`），组件通过 `rounded-md`/`rounded-lg` 消费，避免同页混用过多圆角规格。

### 3.3 字体

- 字体栈：`PingFang SC`、`Microsoft YaHei`、`Segoe UI`、`sans-serif`。
- 字号阶梯遵循 Tailwind（`text-xs`/`text-sm`/`text-base`/`text-lg`/`text-xl`），正文统一 `text-sm`（14px）。

### 3.4 间距

- 统一使用 Tailwind 间距阶梯（4 的倍数，如 `p-2`/`p-3`/`p-4`/`p-6`）。

### 3.5 阴影

- 使用 shadcn/ui 预设阴影语义类（`shadow-sm`/`shadow-md`/`shadow-lg`），服务信息层级而非装饰。

## 4. 组件规范

- 所有基础组件（Button、Input、Select、Checkbox、Table、Dialog、AlertDialog、Card、Form、Tooltip 等）统一使用 shadcn/ui，经 `frontend/packages/ui/` 导出。
- 业务组件在各端 `src/components/` 或 `src/views/<module>/components/` 实现，基于 shadcn/ui 基础组件组合，禁止重新实现同语义基础组件。
- 组件 `variant`/`size` 等变体必须使用 shadcn/ui 预设；扩展变体需在 `packages/ui` 集中定义，禁止页面内临时覆盖。

## 5. 布局壳层

- 管理端与用户端采用同构工作台骨架：顶部头部 + 左侧菜单 + 主体内容区。
- 头部：品牌标识、系统名称、用户信息区（头像 + 名称 + 次级信息）。
- 菜单：按权限动态渲染，支持折叠。
- 内容区：卡片化主容器，统一内边距层级。
- 壳层样式集中维护，业务页面只填充内容区，不得破坏骨架结构。

## 6. 主题模式

- 支持亮色（默认）与暗色，通过 `.dark` 类切换令牌。
- 暗色令牌与亮色同语义对应，不得为暗色单独维护并行组件或类名体系。
- 主题切换状态持久化到 `localStorage`。

## 7. 资产管理

- 图标统一来自 `lucide-react`，禁止引入多套图标库造成风格混乱。
- 自定义插画/Logo 资产放在 `frontend/packages/ui/src/assets/`，两端共享。
- 禁止在页面内散落引用外部 CDN 图片作为 UI 元素。

## 8. 演进与治理

- 设计令牌与基础组件的变更必须集中在 `frontend/packages/ui/`，经评审后同步两端。
- 新增令牌前必须确认现有令牌无法覆盖，避免令牌膨胀；未被引用的冗余令牌必须移除。
- 历史页面按迭代逐步对齐本系统，避免一次性大规模样式重构。