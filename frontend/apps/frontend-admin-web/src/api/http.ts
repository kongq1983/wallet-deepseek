/** 后端统一响应结构：HTTP 200 固定返回 {code,message,data}。 */
export interface ApiEnvelope<T> {
  code: number;
  message: string;
  data: T;
}

/** 成功状态码。 */
const CODE_SUCCESS = 200;

/** 通用失败状态码，拦截器统一提示后端 message。 */
const CODE_FAILURE = 500;

/** 登录失效状态码，拦截器统一提示并跳转登录页。 */
const CODE_UNAUTHORIZED = 501;

type FailureNotifier = (message: string) => void;
type UnauthorizedHandler = () => void;

let failureNotifier: FailureNotifier = (message) => console.warn(message);
let unauthorizedHandler: UnauthorizedHandler = () => console.warn('登录状态已失效');

/** 注入全局失败提示实现，避免拦截器直接依赖具体 UI 组件。 */
export const setFailureNotifier = (notifier: FailureNotifier) => {
  failureNotifier = notifier;
};

/** 注入登录失效处理实现（跳转登录页）。 */
export const setUnauthorizedHandler = (handler: UnauthorizedHandler) => {
  unauthorizedHandler = handler;
};

const authHeaders = (): Record<string, string> => {
  const token = localStorage.getItem('token');
  return token ? { Authorization: `Bearer ${token}` } : {};
};

/**
 * 统一请求封装：注入鉴权头、处理业务码。
 * 页面层与 Store 不得重复处理 code=500 / code=501 的提示与跳转。
 */
const post = async <T>(url: string, body?: unknown): Promise<ApiEnvelope<T>> => {
  const response = await fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      ...authHeaders(),
    },
    body: JSON.stringify(body ?? {}),
  });

  if (!response.ok) {
    throw new Error(`请求失败（HTTP ${response.status}）`);
  }

  const envelope = (await response.json()) as ApiEnvelope<T>;
  if (envelope.code === CODE_UNAUTHORIZED) {
    failureNotifier(envelope.message);
    unauthorizedHandler();
  } else if (envelope.code !== CODE_SUCCESS) {
    failureNotifier(envelope.message || '请求失败，请稍后重试');
  }
  return envelope;
};

export const http = { post, CODE_SUCCESS, CODE_FAILURE };
