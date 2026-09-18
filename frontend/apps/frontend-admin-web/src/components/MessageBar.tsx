import { AlertCircle } from 'lucide-react';
import { useMessageStore } from '@/stores/messageStore';

/** 全局提示条：展示请求拦截器统一处理的失败文案。 */
export const MessageBar = () => {
  const messages = useMessageStore((state) => state.messages);
  if (messages.length === 0) {
    return null;
  }
  return (
    <div className="fixed right-4 top-4 z-[100] flex w-80 flex-col gap-2">
      {messages.map((message) => (
        <div
          key={message.id}
          className="flex items-start gap-2 rounded-md border border-destructive/40 bg-card p-3 text-sm text-foreground shadow-md"
        >
          <AlertCircle className="mt-0.5 h-4 w-4 shrink-0 text-destructive" />
          <span>{message.text}</span>
        </div>
      ))}
    </div>
  );
};
