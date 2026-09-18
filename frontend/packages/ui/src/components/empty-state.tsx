import { Inbox } from 'lucide-react';
import type { ReactNode } from 'react';
import { cn } from '../lib/utils';

export interface EmptyStateProps {
  /** 空态提示文案，默认与列表空态口径一致。 */
  text?: string;
  /** 可选的操作区，例如「新增」按钮。 */
  action?: ReactNode;
  className?: string;
}

/** 列表页统一空态组件。 */
const EmptyState = ({ text = '暂无数据', action, className }: EmptyStateProps) => (
  <div className={cn('flex flex-col items-center justify-center gap-3 p-10 text-center', className)}>
    <Inbox className="h-8 w-8 text-muted-foreground" />
    <p className="text-sm text-muted-foreground">{text}</p>
    {action}
  </div>
);

export { EmptyState };
