import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Pencil, Plus, Trash2 } from 'lucide-react';
import { useState } from 'react';
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
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
  Switch,
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
  createConsumerIdentity,
  deleteConsumerIdentity,
  fetchConsumerIdentityList,
  updateConsumerIdentity,
  type ConsumerIdentityItem,
} from '@/api/consumerIdentity';
import { PageContainer } from '@/components/PageContainer';

interface IdentityFormState {
  name: string;
  offlineAllowed: boolean;
}

const EMPTY_FORM: IdentityFormState = { name: '', offlineAllowed: false };

const ConsumerIdentityPage = () => {
  const queryClient = useQueryClient();
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editing, setEditing] = useState<ConsumerIdentityItem | null>(null);
  const [form, setForm] = useState<IdentityFormState>(EMPTY_FORM);

  const { data: identities, isLoading } = useQuery({
    queryKey: ['consumer-identity', 'list'],
    queryFn: async () => (await fetchConsumerIdentityList()).data,
  });

  const refresh = () => queryClient.invalidateQueries({ queryKey: ['consumer-identity'] });

  const saveMutation = useMutation({
    mutationFn: async () =>
      editing ? updateConsumerIdentity(editing.id, form) : createConsumerIdentity(form),
    onSuccess: (envelope) => {
      if (envelope.code === 200) {
        setDialogOpen(false);
        setEditing(null);
        setForm(EMPTY_FORM);
        refresh();
      }
    },
  });

  const removeMutation = useMutation({
    mutationFn: (id: string) => deleteConsumerIdentity(id),
    onSuccess: (envelope) => {
      if (envelope.code === 200) {
        refresh();
      }
    },
  });

  const openCreate = () => {
    setEditing(null);
    setForm(EMPTY_FORM);
    setDialogOpen(true);
  };

  const openEdit = (item: ConsumerIdentityItem) => {
    setEditing(item);
    setForm({ name: item.name, offlineAllowed: item.offlineAllowed });
    setDialogOpen(true);
  };

  return (
    <PageContainer
      title="身份管理"
      description="维护消费身份字典；身份是否允许脱机消费需与机构参数同时允许才会生效。"
      actions={
        <Button onClick={openCreate}>
          <Plus className="h-4 w-4" />
          创建身份
        </Button>
      }
    >
      <TableContainer>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>身份名称</TableHead>
              <TableHead>是否允许脱机消费</TableHead>
              <TableHead className="w-32 text-right">操作</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {isLoading ? (
              <TableEmpty colSpan={3} text="加载中…" />
            ) : !identities || identities.length === 0 ? (
              <TableEmpty colSpan={3} />
            ) : (
              identities.map((item) => (
                <TableRow key={item.id}>
                  <TableCell>{item.name}</TableCell>
                  <TableCell>
                    <Badge variant={item.offlineAllowed ? 'success' : 'muted'}>
                      {item.offlineAllowed ? '允许' : '不允许'}
                    </Badge>
                  </TableCell>
                  <TableCell className="text-right">
                    <Button variant="ghost" size="sm" onClick={() => openEdit(item)}>
                      <Pencil className="h-4 w-4" />
                      修改
                    </Button>
                    <AlertDialog>
                      <AlertDialogTrigger asChild>
                        <Button variant="ghost" size="sm" className="text-destructive">
                          <Trash2 className="h-4 w-4" />
                          删除
                        </Button>
                      </AlertDialogTrigger>
                      <AlertDialogContent>
                        <AlertDialogHeader>
                          <AlertDialogTitle>确认删除该身份？</AlertDialogTitle>
                          <AlertDialogDescription>
                            删除后该身份的查询下发行会全部变为无效，且会从各机构的可消费身份中移除。
                          </AlertDialogDescription>
                        </AlertDialogHeader>
                        <AlertDialogFooter>
                          <AlertDialogCancel />
                          <AlertDialogAction onClick={() => removeMutation.mutate(item.id)} />
                        </AlertDialogFooter>
                      </AlertDialogContent>
                    </AlertDialog>
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{editing ? '更改身份' : '创建身份'}</DialogTitle>
            <DialogDescription>身份名称在租户内唯一，最多 50 个字符。</DialogDescription>
          </DialogHeader>
          <div className="grid gap-4">
            <div className="grid gap-2">
              <Label htmlFor="identityName">身份名称</Label>
              <Input
                id="identityName"
                placeholder="请输入"
                value={form.name}
                onChange={(event) => setForm({ ...form, name: event.target.value })}
              />
            </div>
            <div className="flex items-center justify-between rounded-md border border-border p-3">
              <div className="space-y-0.5">
                <Label htmlFor="offlineAllowed">是否允许脱机消费</Label>
                <p className="text-xs text-muted-foreground">需同时满足机构允许，才会下发为允许。</p>
              </div>
              <Switch
                id="offlineAllowed"
                checked={form.offlineAllowed}
                onCheckedChange={(checked) => setForm({ ...form, offlineAllowed: checked })}
              />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setDialogOpen(false)}>
              取消
            </Button>
            <Button disabled={saveMutation.isPending} onClick={() => saveMutation.mutate()}>
              保存
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </PageContainer>
  );
};

export default ConsumerIdentityPage;
