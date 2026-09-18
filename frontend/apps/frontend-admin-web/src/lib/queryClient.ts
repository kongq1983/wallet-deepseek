import { QueryClient } from '@tanstack/react-query';

/**
 * 服务端状态统一由 TanStack Query 承载。
 * 业务错误提示已由请求拦截器统一处理，因此默认不重试，避免重复弹窗。
 */
export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: false,
      refetchOnWindowFocus: false,
      staleTime: 10_000,
    },
    mutations: {
      retry: false,
    },
  },
});
