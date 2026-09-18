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
  Button,
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  Input,
  Label,
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
  addWalletPair,
  deleteWalletPair,
  fetchWalletPairList,
  updateWalletPair,
  type WalletPairItem,
} from '@/api/walletPair';
import { PageContainer } from '@/components/PageContainer';

interface PairFormState {
  workWalletNo: string;
  deductWalletNo: string;
}

const EMPTY_FORM: PairFormState = { workWalletNo: '', deductWalletNo: '' };

/**
 * 空输入必须提交 null：若折算为 0 会被后端判定为「超出 1~8 范围」，与「未填写」的提示不符。
 */
const toWalletNo = (value: string): number | null => {
  const trimmed = value.trim();
  if (trimmed === '') {
    return null;
  }
  const parsed = Number(trimmed);
  return Number.isNaN(parsed) ? null : parsed;
};

const WalletPairPage = () => {
  const queryClient = useQueryClient();
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editing, setEditing] = useState<WalletPairItem | null>(null);
  const [form, setForm] = useState<PairFormState>(EMPTY_FORM);

  const { data: pairs, isLoading } = useQuery({
    queryKey: ['wallet-pair', 'list'],
    queryFn: async () => (await fetchWalletPairList()).data,
  });

  const refresh = () => queryClient.invalidateQueries({ queryKey: ['wallet-pair'] });

  const saveMutation = useMutation({
    mutationFn: async () => {
      const params = {
        workWalletNo: toWalletNo(form.workWalletNo),
        deductWalletNo: toWalletNo(form.deductWalletNo),
      };
      return editing ? updateWalletPair({ id: editing.id, ...params }) : addWalletPair(params);
    },
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
    mutationFn: (id: string) => deleteWalletPair(id),
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

  const openEdit = (item: WalletPairItem) => {
    setEditing(item);
    setForm({ workWalletNo: item.workWalletNo, deductWalletNo: item.deductWalletNo });
    setDialogOpen(true);
  };

  return (
    <PageContainer
      title="租户钱包设置"
      description="维护「工作钱包 ↔ 追扣钱包」配对；机构只需选择工作钱包，追扣钱包由配对关系自动推导。"
      actions={
        <Button onClick={openCreate}>
          <Plus className="h-4 w-4" />
          新增配对
        </Button>
      }
    >
      <TableContainer>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>工作钱包</TableHead>
              <TableHead>追扣钱包</TableHead>
              <TableHead className="w-32 text-right">操作</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {isLoading ? (
              <TableEmpty colSpan={3} text="加载中…" />
            ) : !pairs || pairs.length === 0 ? (
              <TableEmpty colSpan={3} />
            ) : (
              pairs.map((item) => (
                <TableRow key={item.id}>
                  <TableCell>{item.workWalletNo}</TableCell>
                  <TableCell>{item.deductWalletNo}</TableCell>
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
                          <AlertDialogTitle>确认删除该钱包配对？</AlertDialogTitle>
                          <AlertDialogDescription>
                            删除后引用该工作钱包的机构将不再具备追扣能力，其追扣钱包下发数据会变为无效。
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

      <Dialog
        open={dialogOpen}
        onOpenChange={(open) => {
          setDialogOpen(open);
          if (!open) {
            setEditing(null);
            setForm(EMPTY_FORM);
          }
        }}
      >
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{editing ? '修改钱包配对' : '新增钱包配对'}</DialogTitle>
            <DialogDescription>编号可手工填写，取值 1~8，且在租户内不可重复。</DialogDescription>
          </DialogHeader>
          <div className="grid gap-4">
            <div className="grid gap-2">
              <Label htmlFor="workWalletNo">
                工作钱包编号
                <span className="ml-0.5 text-destructive">*</span>
              </Label>
              <Input
                id="workWalletNo"
                type="number"
                inputMode="numeric"
                min={1}
                max={8}
                placeholder="请输入"
                value={form.workWalletNo}
                onChange={(event) => setForm({ ...form, workWalletNo: event.target.value })}
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="deductWalletNo">
                追扣钱包编号
                <span className="ml-0.5 text-destructive">*</span>
              </Label>
              <Input
                id="deductWalletNo"
                type="number"
                inputMode="numeric"
                min={1}
                max={8}
                placeholder="请输入"
                value={form.deductWalletNo}
                onChange={(event) => setForm({ ...form, deductWalletNo: event.target.value })}
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

export default WalletPairPage;
