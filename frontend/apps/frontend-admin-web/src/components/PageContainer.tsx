import type { ReactNode } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@walletconfig/ui';

export interface PageContainerProps {
  title: string;
  description?: string;
  /** 标题区右侧操作区，例如「新增」按钮。 */
  actions?: ReactNode;
  children: ReactNode;
}

/** 页面级卡片容器：统一标题区与内容区内边距层级。 */
export const PageContainer = ({ title, description, actions, children }: PageContainerProps) => (
  <Card>
    <CardHeader className="flex-row items-start justify-between space-y-0 gap-4">
      <div className="space-y-1">
        <CardTitle>{title}</CardTitle>
        {description ? <p className="text-sm text-muted-foreground">{description}</p> : null}
      </div>
      {actions ? <div className="flex shrink-0 items-center gap-2">{actions}</div> : null}
    </CardHeader>
    <CardContent>{children}</CardContent>
  </Card>
);
