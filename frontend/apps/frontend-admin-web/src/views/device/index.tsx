import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Plus, Trash2 } from 'lucide-react';
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
import { addDevice, deleteDevice, fetchDeviceList, type DeviceTypeCode } from '@/api/device';
import { fetchOrganizationList } from '@/api/organization';
import { PageContainer } from '@/components/PageContainer';

const DEVICE_TYPES: { value: DeviceTypeCode; label: string }[] = [
  { value: 'POS_MACHINE', label: 'POS机' },
  { value: 'FACE_RECOGNITION_MACHINE', label: '多媒体人脸机' },
  { value: 'OTHER', label: '其他' },
];

const DevicePage = () => {
  const queryClient = useQueryClient();
  const [keyword, setKeyword] = useState('');
  const [dialogOpen, setDialogOpen] = useState(false);
  const [serialNo, setSerialNo] = useState('');
  const [deviceType, setDeviceType] = useState<DeviceTypeCode>('POS_MACHINE');
  const [organizationId, setOrganizationId] = useState('');

  // 默认按创建时间倒序，支持按序列号搜索
  const { data: devices, isLoading } = useQuery({
    queryKey: ['device', 'list', keyword],
    queryFn: async () => (await fetchDeviceList(keyword || undefined)).data,
  });

  const { data: organizations } = useQuery({
    queryKey: ['organization', 'list'],
    queryFn: async () => (await fetchOrganizationList()).data,
  });

  const refresh = () => queryClient.invalidateQueries({ queryKey: ['device'] });

  const saveMutation = useMutation({
    mutationFn: () => addDevice({ serialNo, deviceType, organizationId }),
    onSuccess: (envelope) => {
      if (envelope.code === 200) {
        setDialogOpen(false);
        setSerialNo('');
        setOrganizationId('');
        refresh();
      }
    },
  });

  const removeMutation = useMutation({
    mutationFn: (id: string) => deleteDevice(id),
    onSuccess: (envelope) => {
      if (envelope.code === 200) {
        refresh();
      }
    },
  });

  return (
    <PageContainer
      title="设备管理"
      description="登记机构下的消费终端；设备序列号全局唯一，归属机构创建后不可变更。"
      actions={
        <Button onClick={() => setDialogOpen(true)}>
          <Plus className="h-4 w-4" />
          新增设备
        </Button>
      }
    >
      <div className="mb-4 flex items-center gap-2">
        <Input
          className="max-w-xs"
          placeholder="请输入设备序列号"
          value={keyword}
          onChange={(event) => setKeyword(event.target.value)}
        />
      </div>

      <TableContainer>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>设备序列号</TableHead>
              <TableHead>设备类型</TableHead>
              <TableHead>归属机构</TableHead>
              <TableHead className="w-24 text-right">操作</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {isLoading ? (
              <TableEmpty colSpan={4} text="加载中…" />
            ) : !devices || devices.length === 0 ? (
              <TableEmpty colSpan={4} />
            ) : (
              devices.map((item) => (
                <TableRow key={item.id}>
                  <TableCell>{item.serialNo}</TableCell>
                  <TableCell>{item.deviceTypeLabel}</TableCell>
                  <TableCell>
                    {organizations?.find((org) => org.id === item.organizationId)?.name ?? '-'}
                  </TableCell>
                  <TableCell className="text-right">
                    <AlertDialog>
                      <AlertDialogTrigger asChild>
                        <Button variant="ghost" size="sm" className="text-destructive">
                          <Trash2 className="h-4 w-4" />
                          删除
                        </Button>
                      </AlertDialogTrigger>
                      <AlertDialogContent>
                        <AlertDialogHeader>
                          <AlertDialogTitle>确认删除该设备？</AlertDialogTitle>
                          <AlertDialogDescription>
                            删除后该设备的下发表数据会被一并清除，且不可恢复。
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
            <DialogTitle>新增设备</DialogTitle>
            <DialogDescription>序列号全局唯一，最多 64 个字符。</DialogDescription>
          </DialogHeader>
          <div className="grid gap-4">
            <div className="grid gap-2">
              <Label htmlFor="serialNo">设备序列号</Label>
              <Input
                id="serialNo"
                placeholder="请输入"
                value={serialNo}
                onChange={(event) => setSerialNo(event.target.value)}
              />
            </div>
            <div className="grid gap-2">
              <Label>设备类型</Label>
              <Select value={deviceType} onValueChange={(value) => setDeviceType(value as DeviceTypeCode)}>
                <SelectTrigger>
                  <SelectValue placeholder="请选择" />
                </SelectTrigger>
                <SelectContent>
                  {DEVICE_TYPES.map((type) => (
                    <SelectItem key={type.value} value={type.value}>
                      {type.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div className="grid gap-2">
              <Label>归属机构</Label>
              <Select value={organizationId} onValueChange={setOrganizationId}>
                <SelectTrigger>
                  <SelectValue placeholder="请选择" />
                </SelectTrigger>
                <SelectContent>
                  {(organizations ?? []).map((org) => (
                    <SelectItem key={org.id} value={org.id}>
                      {`${'　'.repeat(Number(org.level) - 1)}${org.name}`}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
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

export default DevicePage;
