import { create } from 'zustand';

/** 全局提示消息。 */
export interface MessageItem {
  id: number;
  text: string;
}

interface MessageState {
  messages: MessageItem[];
  push: (text: string) => void;
  remove: (id: number) => void;
}

let nextId = 1;

/**
 * 全局提示仅承载请求拦截器抛出的失败文案，业务页面不得自行重复实现接口错误提示。
 */
export const useMessageStore = create<MessageState>((set) => ({
  messages: [],
  push: (text) => {
    const id = nextId;
    nextId += 1;
    set((state) => ({ messages: [...state.messages, { id, text }] }));
    window.setTimeout(() => {
      set((state) => ({ messages: state.messages.filter((item) => item.id !== id) }));
    }, 4000);
  },
  remove: (id) => set((state) => ({ messages: state.messages.filter((item) => item.id !== id) })),
}));
