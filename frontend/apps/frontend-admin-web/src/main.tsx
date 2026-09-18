import React from 'react';
import ReactDOM from 'react-dom/client';
import { App } from '@/App';
import { setFailureNotifier } from '@/api/http';
import { useMessageStore } from '@/stores/messageStore';
import { initTheme } from '@/stores/themeStore';
import '@/styles/globals.css';

// 把请求拦截器的失败提示接到全局提示条，避免页面层重复处理
setFailureNotifier((message) => useMessageStore.getState().push(message));
initTheme();

ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
);
