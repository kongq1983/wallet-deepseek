import { ChevronDown, ChevronRight } from 'lucide-react';
import { useState, type ReactNode } from 'react';
import { Card, CardContent, CardHeader, CardTitle, EmptyState, cn } from '@walletconfig/ui';
import type { OrganizationItem } from '@/api/organization';

interface OrganizationTreeNode {
  id: string;
  name: string;
  children: OrganizationTreeNode[];
}

/**
 * 由扁平列表组装机构树。
 * 上级机构缺失时该机构按顶层处理，避免个别脏数据导致整棵子树不可见。
 */
const buildTree = (organizations: OrganizationItem[]): OrganizationTreeNode[] => {
  const nodeById = new Map<string, OrganizationTreeNode>();
  organizations.forEach((organization) => {
    nodeById.set(organization.id, { id: organization.id, name: organization.name, children: [] });
  });

  const roots: OrganizationTreeNode[] = [];
  organizations.forEach((organization) => {
    const node = nodeById.get(organization.id);
    if (!node) {
      return;
    }
    const parent = organization.superiorId ? nodeById.get(organization.superiorId) : undefined;
    (parent ? parent.children : roots).push(node);
  });
  return roots;
};

export interface OrganizationTreeProps {
  organizations: OrganizationItem[];
  /** 数据加载中：展示加载态而非空态。 */
  loading?: boolean;
  selectedId: string;
  onSelect: (organizationId: string) => void;
}

/**
 * 机构树选择器：按层级缩进展示机构，点击节点即切换配置对象。
 * 默认全部展开——机构树最多 3 层，全展开比逐层点开更省操作。
 */
export const OrganizationTree = ({
  organizations,
  loading = false,
  selectedId,
  onSelect,
}: OrganizationTreeProps) => {
  // 记录「被折叠」的节点，这样新出现的机构默认就是展开的
  const [collapsedIds, setCollapsedIds] = useState<string[]>([]);

  const toggleCollapsed = (nodeId: string) => {
    setCollapsedIds((previous) =>
      previous.includes(nodeId) ? previous.filter((id) => id !== nodeId) : [...previous, nodeId],
    );
  };

  const renderNodes = (nodes: OrganizationTreeNode[], depth: number): ReactNode =>
    nodes.map((node) => {
      const hasChildren = node.children.length > 0;
      const collapsed = collapsedIds.includes(node.id);
      const selected = node.id === selectedId;

      return (
        <div
          key={node.id}
          role="treeitem"
          aria-selected={selected}
          aria-expanded={hasChildren ? !collapsed : undefined}
          aria-level={depth + 1}
        >
          <div className="flex items-center gap-0.5" style={{ paddingLeft: `${depth * 12}px` }}>
            {hasChildren ? (
              <button
                type="button"
                aria-label={`${collapsed ? '展开' : '折叠'}${node.name}`}
                onClick={() => toggleCollapsed(node.id)}
                className="flex h-5 w-5 shrink-0 items-center justify-center rounded-sm text-muted-foreground transition-colors hover:bg-accent hover:text-accent-foreground focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
              >
                {collapsed ? (
                  <ChevronRight className="h-3.5 w-3.5" />
                ) : (
                  <ChevronDown className="h-3.5 w-3.5" />
                )}
              </button>
            ) : (
              <span className="h-5 w-5 shrink-0" />
            )}
            <button
              type="button"
              title={node.name}
              onClick={() => onSelect(node.id)}
              className={cn(
                'min-w-0 flex-1 truncate rounded-md px-2 py-1.5 text-left text-sm transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring',
                selected ? 'bg-accent font-medium text-accent-foreground' : 'hover:bg-accent/60',
              )}
            >
              {node.name}
            </button>
          </div>
          {hasChildren && !collapsed ? (
            <div role="group">{renderNodes(node.children, depth + 1)}</div>
          ) : null}
        </div>
      );
    });

  const tree = buildTree(organizations);

  return (
    <Card className="lg:sticky lg:top-6">
      <CardHeader>
        <CardTitle className="text-sm">机构</CardTitle>
      </CardHeader>
      <CardContent className="p-2">
        {loading ? (
          <p className="p-3 text-sm text-muted-foreground">加载中…</p>
        ) : tree.length === 0 ? (
          <EmptyState className="p-6" />
        ) : (
          <div role="tree" aria-label="机构树" className="max-h-[26rem] overflow-y-auto">
            {renderNodes(tree, 0)}
          </div>
        )}
      </CardContent>
    </Card>
  );
};
