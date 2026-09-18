import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Pencil, Plus } from 'lucide-react';
import { useState } from 'react';
import {
  Badge,
  Button,
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  Input,
  Label,
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableEmpty,
  TableHead,
  TableHeader,
  TableRow,
} from '@walletconfig/ui';
import {
  addOrganization,
  fetchOrganizationList,
  renameOrganization,
  type OrganizationItem,
  type OrganizationTypeCode,
} from '@/api/organization';
import { PageContainer } from '@/components/PageContainer';

const ORG_TYPES: { value: OrganizationTypeCode; label: string }[] = [
  { value: 'RESTAURANT', label: '餐厅' },
  { value: 'STALL', label: '档口' },
  { value: 'SUPERMARKET', label: '超市' },
];

const TOP_LEVEL = '__TOP__';

const OrganizationPage = () => {
  const queryClient = useQueryClient();
  const [createOpen, setCreateOpen] = useState(false);
  const [renameTarget, setRenameTarget] = useState<OrganizationItem | null>(null);
  const [name, setName] = useState('');
  const [orgType, setOrgType] = useState<OrganizationTypeCode>('RESTAURANT');
  const [superiorId, setSuperiorId] = useState(TOP_LEVEL);

  const { data: organizations, isLoading } = useQuery({
    queryKey: ['organization', 'list'],
    queryFn: async () => (await fetchOrganizationList()).data,
  });

  const refresh = () => queryClient.invalidateQueries({ queryKey: ['organization'] });

  const createMutation = useMutation({
    mutationFn: () =>
      addOrganization({
        name,
        orgType,
        superiorId: superiorId === TOP_LEVEL ? undefined : superiorId,
      }),
    onSuccess: (envelope) => {
      if (envelope.code === 200) {
        setCreateOpen(false);
        setName('');
        setSuperiorId(TOP_LEVEL);
        refresh();
      }
    },
  });

  const renameMutation = useMutation({
    mutationFn: () => renameOrganization(renameTarget?.id ?? '', name),
    onSuccess: (envelope) => {
      if (envelope.code === 200) {
        setRenameTarget(null);
        setName('');
        refresh();
      }
    },
  });

  // 只允许选择第 1、2 层机构作为上级，避免一次创建即超过 3 层
  const selectableSuperiors = (organizations ?? []).filter((item) => Number(item.level) < 3);

  const nameById = (id: string | null) =>
    id ? organizations?.find((item) => item.id === id)?.name ?? '-' : '-';

  return (
    <PageContainer
      title="机构管理"
      description="机构树最多 3 层，档口下不能创建子机构；机构创建后仅支持改名。"
      actions={
        <Button
          onClick={() => {
            setName('');
            setSuperiorId(TOP_LEVEL);
            setCreateOpen(true);
          }}
        >
          <Plus className="h-4 w-4" />
          新增机构
        </Button>
      }
    >
      <TableContainer>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>机构名称</TableHead>
              <TableHead>机构类型</TableHead>
              <TableHead>上级机构</TableHead>
              <TableHead>层级</TableHead>
              <TableHead className="w-24 text-right">操作</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {isLoading ? (
              <TableEmpty colSpan={5} text="加载中…" />
            ) : !organizations || organizations.length === 0 ? (
              <TableEmpty colSpan={5} />
            ) : (
              organizations.map((item) => (
                <TableRow key={item.id}>
                  <TableCell>
                    <span style={{ paddingLeft: `${(Number(item.level) - 1) * 16}px` }}>{item.name}</span>
                  </TableCell>
                  <TableCell>
                    <Badge variant="outline">{item.orgTypeLabel}</Badge>
                  </TableCell>
                  <TableCell>{nameById(item.superiorId)}</TableCell>
                  <TableCell>{item.level}</TableCell>
                  <TableCell className="text-right">
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => {
                        setRenameTarget(item);
                        setName(item.name);
                      }}
                    >
                      <Pencil className="h-4 w-4" />
                      改名
                    </Button>
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={createOpen} onOpenChange={setCreateOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>新增机构</DialogTitle>
            <DialogDescription>同一上级机构下机构名称不可重复。</DialogDescription>
          </DialogHeader>
          <div className="grid gap-4">
            <div className="grid gap-2">
              <Label htmlFor="orgName">机构名称</Label>
              <Input
                id="orgName"
                placeholder="请输入"
                value={name}
                onChange={(event) => setName(event.target.value)}
              />
            </div>
            <div className="grid gap-2">
              <Label>机构类型</Label>
              <Select value={orgType} onValueChange={(value) => setOrgType(value as OrganizationTypeCode)}>
                <SelectTrigger>
                  <SelectValue placeholder="请选择" />
                </SelectTrigger>
                <SelectContent>
                  {ORG_TYPES.map((type) => (
                    <SelectItem key={type.value} value={type.value}>
                      {type.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div className="grid gap-2">
              <Label>上级机构</Label>
              <Select value={superiorId} onValueChange={setSuperiorId}>
                <SelectTrigger>
                  <SelectValue placeholder="请选择" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value={TOP_LEVEL}>不选择（创建为顶层机构）</SelectItem>
                  {selectableSuperiors.map((org) => (
                    <SelectItem key={org.id} value={org.id}>
                      {`${'　'.repeat(Number(org.level) - 1)}${org.name}`}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setCreateOpen(false)}>
              取消
            </Button>
            <Button disabled={createMutation.isPending} onClick={() => createMutation.mutate()}>
              保存
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      <Dialog open={renameTarget !== null} onOpenChange={(open) => !open && setRenameTarget(null)}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>更改机构名称</DialogTitle>
            <DialogDescription>机构类型与上级机构不可变更。</DialogDescription>
          </DialogHeader>
          <div className="grid gap-2">
            <Label htmlFor="renameOrgName">机构名称</Label>
            <Input
              id="renameOrgName"
              placeholder="请输入"
              value={name}
              onChange={(event) => setName(event.target.value)}
            />
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setRenameTarget(null)}>
              取消
            </Button>
            <Button disabled={renameMutation.isPending} onClick={() => renameMutation.mutate()}>
              保存
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </PageContainer>
  );
};

export default OrganizationPage;
