# 后端 API 设计规范

## 1. 适用范围

本规范仅适用于标准 HTTP JSON 业务接口。

以下类型不在本规范约束范围内：
- 文件上传/下载接口（`multipart`、binary）。
- SSE/流式响应接口。
- 第三方回调接口。

## 2. HTTP 状态码约束

| HTTP 状态码 | 约束 |
| --- | --- |
| `200` | 必须返回 JSON 响应体，结构固定为 `{code,message,data}` |
| 非 `200` | 不返回响应体（无 body） |

## 3. 响应结构（仅 `HTTP 200`）

响应体固定为以下 3 个字段：
- `code`：数字型状态码。
- `message`：终端用户可直接展示的文案。
- `data`：业务数据。

字段约束：
- `code` 必须为数字类型。
- `message` 不允许为空，至少返回可展示默认文案。
- 失败时 `data` 必须为 `null`。
- 成功时 `data` 允许为 `null`。

示例（成功）：

```json
{
  "code": 200,
  "message": "请求成功",
  "data": {
    "id": "1001"
  }
}
```

示例（失败）：

```json
{
  "code": 500,
  "message": "请求失败，请稍后重试",
  "data": null
}
```

## 4. 成功与失败语义

- `code=200`：唯一成功状态码。
- `code=500`：通用失败状态码。
- `code>=501`：特殊错误码（仅在确有前端特殊处理需求时新增）。

文案约束：
- 成功文案固定为：`请求成功`。
- 通用失败默认文案固定为：`请求失败，请稍后重试`。

## 5. 特殊错误码治理

- 特殊错误码从 `501` 开始递增。
- 特殊错误码不允许复用，只能新增递增。
- 新增特殊错误码需前后端共同评审。

### 5.1 特殊错误码表

| 错误码 | 错误消息 |
| --- | --- |
| `501` | 登录状态已失效，请重新登录 |

## 6. 业务字段表达

- HTTP 接口出入参中的金额字段统一使用“元”作为单位，禁止在接口字段中混用“分”“厘”等其他金额单位。
- HTTP 接口出入参中的时间字段统一使用字符串表达，格式根据时间精度与业务需求选择 `yyyy-MM-dd HH:mm:ss` 或 `yyyy-MM-dd`。
- HTTP 接口出参中的枚举字段必须同时返回枚举 `code` 与中文 `label`，例如状态字段应返回状态编码与状态中文含义。
- HTTP 接口 `data` 内的业务出参应尽量使用字符串表达，包括金额、时间、数量、比例等数值或日期数据；确需返回布尔值、数组、对象等结构化类型时，按其真实 JSON 类型表达。

## 7. ID 字段表达

- 后端 `Long` / 数据库 `BIGINT` 类型的系统内部 ID，在 JSON 中必须以字符串表达。
- 请求参数中的系统内部 ID 也必须允许前端以字符串传入，并在后端边界层完成 `Long` 转换与合法性校验。
- 禁止在 HTTP JSON 接口中用数字类型表达雪花 ID，避免 JavaScript 大整数精度丢失。
- 第三方业务号、外部系统编号按其原始类型表达，但不得伪装成本系统内部 ID。
- 具体 ID 生成与分类规则遵循 `@docs/rules/backend-id-generation.md`。

## 8. API 接口命名参考

`{aggregate}` 表示领域驱动设计（DDD）中的聚合根。

| 操作类型 | 路径结尾 | 示例 |
| --- | --- | --- |
| 单条查询 | `/get` | `POST /{aggregate}/get` |
| 列表查询 | `/list` | `POST /{aggregate}/list` |
| 分页查询 | `/page` | `POST /{aggregate}/page` |
| 树形查询 | `/tree` | `POST /{aggregate}/tree` |
| 新增 | `/add` | `POST /{aggregate}/add` |
| 批量新增 | `/batch/add` | `POST /{aggregate}/batch/add` |
| 修改 | `/update` | `POST /{aggregate}/update` |
| 批量修改 | `/batch/update` | `POST /{aggregate}/batch/update` |
| 删除 | `/delete` | `POST /{aggregate}/delete` |
| 批量删除 | `/batch/delete` | `POST /{aggregate}/batch/delete` |

## 9. 分页与列表

- 分页列表数据结构应包含：`items`、`total`、`pageNo`、`pageSize`。
- 空列表返回空数组，不返回 `null`。
- 字段命名统一使用 `lowerCamelCase`。
