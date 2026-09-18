import { useQuery } from '@tanstack/react-query';
import { useState } from 'react';
import {
  Badge,
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
  fetchDeviceIdentityWalletList,
  type DeviceIdentityWalletListQuery,
} from '@/api/deviceIdentityWallet';
import { fetchDeviceList } from '@/api/device';
import { PageContainer } from '@/components/PageContainer';

const ALL = '__ALL__';

const DeviceIdentityWalletPage = () => {
  const [deviceId, setDeviceId] = useState(ALL);
  const [validFlag, setValidFlag] = useState(ALL);
  const [serialNo, setSerialNo] = useState('');

  const { data: devices } = useQuery({
    queryKey: ['device', 'list', serialNo],
    queryFn: async () => (await fetchDeviceList(serialNo || undefined)).data,
  });

  const query: DeviceIdentityWalletListQuery = {
    deviceId: deviceId === ALL ? undefined : deviceId,
    validFlag: validFlag === ALL ? undefined : validFlag,
  };

  // 默认按版本号倒序返回，便于先看到最近一次变更
  const { data: rows, isLoading } = useQuery({
    queryKey: ['device-identity-wallet', 'list', query],
    queryFn: async () => (await fetchDeviceIdentityWalletList(query)).data,
  });

  return (
    <PageContainer
      title="下发表查询"
      description="下发表由配置自动生成，不支持人工录入；失效行会永久保留用于追溯。"
    >
      <div className="mb-4 grid gap-3 sm:grid-cols-3">
        <div className="grid gap-2">
          <Label>设备序列号</Label>
          <Input placeholder="请输入" value={serialNo} onChange={(event) => setSerialNo(event.target.value)} />
        </div>
        <div className="grid gap-2">
          <Label>设备</Label>
          <Select value={deviceId} onValueChange={setDeviceId}>
            <SelectTrigger>
              <SelectValue placeholder="请选择" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value={ALL}>全部设备</SelectItem>
              {(devices ?? []).map((device) => (
                <SelectItem key={device.id} value={device.id}>
                  {device.serialNo}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
        <div className="grid gap-2">
          <Label>有效标记</Label>
          <Select value={validFlag} onValueChange={setValidFlag}>
            <SelectTrigger>
              <SelectValue placeholder="请选择" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value={ALL}>全部</SelectItem>
              <SelectItem value="1">有效</SelectItem>
              <SelectItem value="0">无效</SelectItem>
            </SelectContent>
          </Select>
        </div>
      </div>

      <TableContainer>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>设备序列号</TableHead>
              <TableHead>交易身份</TableHead>
              <TableHead>钱包类型</TableHead>
              <TableHead>钱包</TableHead>
              <TableHead>是否允许追扣</TableHead>
              <TableHead>是否允许脱机消费</TableHead>
              <TableHead>有效标记</TableHead>
              <TableHead>版本号</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {isLoading ? (
              <TableEmpty colSpan={8} text="加载中…" />
            ) : !rows || rows.length === 0 ? (
              <TableEmpty colSpan={8} />
            ) : (
              rows.map((row) => (
                <TableRow key={row.id}>
                  <TableCell>{row.deviceSerialNo ?? '-'}</TableCell>
                  <TableCell>{row.identityName ?? '-'}</TableCell>
                  <TableCell>{row.walletTypeLabel}</TableCell>
                  <TableCell>{row.walletNo}</TableCell>
                  <TableCell>{row.deductAllowed ? '是' : '否'}</TableCell>
                  <TableCell>{row.offlineAllowed ? '是' : '否'}</TableCell>
                  <TableCell>
                    <Badge variant={row.validFlag === '1' ? 'success' : 'muted'}>{row.validFlagLabel}</Badge>
                  </TableCell>
                  <TableCell>{row.version}</TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </TableContainer>
    </PageContainer>
  );
};

export default DeviceIdentityWalletPage;
