import { useMutation, useQuery } from '@tanstack/react-query';
import { Save } from 'lucide-react';
import { useEffect, useState } from 'react';
import {
  Badge,
  Button,
  Card,
  CardContent,
  CardHeader,
  CardTitle,
  Checkbox,
  Label,
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
  Switch,
} from '@walletconfig/ui';
import { fetchConsumerIdentityList } from '@/api/consumerIdentity';
import { fetchOrganizationList } from '@/api/organization';
import { fetchPaymentConfig, savePaymentConfig } from '@/api/paymentConfig';
import { fetchWalletPairList } from '@/api/walletPair';
import { PageContainer } from '@/components/PageContainer';
import { OrganizationTree } from '@/views/paymentConfig/components/OrganizationTree';

interface ConfigFormState {
  deductAllowed: boolean;
  workWalletNo: string;
  identityRestricted: boolean;
  allowedIdentityIds: string[];
  offlineAllowed: boolean;
}

const EMPTY_FORM: ConfigFormState = {
  deductAllowed: false,
  workWalletNo: '',
  identityRestricted: false,
  allowedIdentityIds: [],
  offlineAllowed: false,
};

const PaymentConfigPage = () => {
  const [organizationId, setOrganizationId] = useState('');
  const [form, setForm] = useState<ConfigFormState>(EMPTY_FORM);

  const { data: organizations, isLoading: organizationsLoading } = useQuery({
    queryKey: ['organization', 'list'],
    queryFn: async () => (await fetchOrganizationList()).data,
  });

  const { data: walletPairs } = useQuery({
    queryKey: ['wallet-pair', 'list'],
    queryFn: async () => (await fetchWalletPairList()).data,
  });

  const { data: identities } = useQuery({
    queryKey: ['consumer-identity', 'list'],
    queryFn: async () => (await fetchConsumerIdentityList()).data,
  });

  const { data: detail, isLoading: detailLoading } = useQuery({
    queryKey: ['payment-config', organizationId],
    queryFn: async () => (await fetchPaymentConfig(organizationId)).data,
    enabled: organizationId !== '',
  });

  // 切换到机构后回填已配置的参数；未配置则回到初始值
  useEffect(() => {
    if (!detail) {
      return;
    }
    if (!detail.configured) {
      setForm(EMPTY_FORM);
      return;
    }
    setForm({
      deductAllowed: detail.deductAllowed,
      workWalletNo: detail.workWalletNo ?? '',
      identityRestricted: detail.identityRestricted,
      allowedIdentityIds: detail.allowedIdentityIds ?? [],
      offlineAllowed: detail.offlineAllowed,
    });
  }, [detail]);

  // 机构树可随时切换，必须立即清空表单，否则详情返回前会把上一个机构的值保存到新机构
  const selectOrganization = (nextOrganizationId: string) => {
    setOrganizationId(nextOrganizationId);
    setForm(EMPTY_FORM);
  };

  const saveMutation = useMutation({
    mutationFn: () =>
      savePaymentConfig({
        organizationId,
        deductAllowed: form.deductAllowed,
        workWalletNo: Number(form.workWalletNo),
        identityRestricted: form.identityRestricted,
        allowedIdentityIds: form.allowedIdentityIds,
        offlineAllowed: form.offlineAllowed,
      }),
  });

  const toggleIdentity = (identityId: string, checked: boolean) => {
    setForm((previous) => ({
      ...previous,
      allowedIdentityIds: checked
        ? [...previous.allowedIdentityIds, identityId]
        : previous.allowedIdentityIds.filter((item) => item !== identityId),
    }));
  };

  const organizationName =
    detail?.organizationName ?? organizations?.find((item) => item.id === organizationId)?.name ?? '';

  return (
    <PageContainer
      title="机构支付参数"
      description="每个机构独立配置；工作钱包选定后，追扣钱包由租户钱包配对自动推导。"
    >
      <div className="grid gap-6 lg:grid-cols-[15rem_minmax(0,1fr)]">
        <OrganizationTree
          organizations={organizations ?? []}
          loading={organizationsLoading}
          selectedId={organizationId}
          onSelect={selectOrganization}
        />

        {organizationId === '' ? (
          <Card className="self-start">
            <CardContent className="p-10 text-center text-sm text-muted-foreground">
              请在左侧机构树中选择机构后配置支付参数
            </CardContent>
          </Card>
        ) : (
          <div className="grid gap-4">
            <div className="flex flex-wrap items-center justify-between gap-2">
              <h2 className="text-sm font-medium">{organizationName}</h2>
              {detail?.configured === false ? <Badge variant="muted">该机构尚未配置支付参数</Badge> : null}
            </div>

            <Card>
              <CardHeader>
                <CardTitle>钱包与追扣</CardTitle>
              </CardHeader>
              <CardContent className="grid gap-4">
                <div className="flex items-center justify-between rounded-md border border-border p-3">
                  <div className="space-y-0.5">
                    <Label htmlFor="deductAllowed">是否可追扣</Label>
                    <p className="text-xs text-muted-foreground">
                      开启后，工作钱包不足时设备可继续扣减追扣钱包。
                    </p>
                  </div>
                  <Switch
                    id="deductAllowed"
                    checked={form.deductAllowed}
                    onCheckedChange={(checked) => setForm({ ...form, deductAllowed: checked })}
                  />
                </div>

                <div className="grid gap-2">
                  <Label>工作钱包</Label>
                  <Select
                    value={form.workWalletNo}
                    onValueChange={(value) => setForm({ ...form, workWalletNo: value })}
                  >
                    <SelectTrigger>
                      <SelectValue placeholder="请选择" />
                    </SelectTrigger>
                    <SelectContent>
                      {(walletPairs ?? []).map((pair) => (
                        <SelectItem key={pair.id} value={pair.workWalletNo}>
                          {`工作钱包 ${pair.workWalletNo}（追扣钱包 ${pair.deductWalletNo}）`}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                  {detail?.deductWalletNo ? (
                    <p className="text-xs text-muted-foreground">当前推导的追扣钱包：{detail.deductWalletNo}</p>
                  ) : null}
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>身份与脱机</CardTitle>
              </CardHeader>
              <CardContent className="grid gap-4">
                <div className="flex items-center justify-between rounded-md border border-border p-3">
                  <div className="space-y-0.5">
                    <Label htmlFor="identityRestricted">身份限制</Label>
                    <p className="text-xs text-muted-foreground">关闭时该机构所有身份均可消费。</p>
                  </div>
                  <Switch
                    id="identityRestricted"
                    checked={form.identityRestricted}
                    onCheckedChange={(checked) => setForm({ ...form, identityRestricted: checked })}
                  />
                </div>

                <div className="grid gap-2">
                  <Label>可消费身份</Label>
                  <div className="grid gap-2 rounded-md border border-border p-3 sm:grid-cols-2">
                    {(identities ?? []).length === 0 ? (
                      <p className="text-sm text-muted-foreground">暂无身份，请先在身份管理中创建</p>
                    ) : (
                      identities?.map((identity) => (
                        <label key={identity.id} className="flex items-center gap-2 text-sm">
                          <Checkbox
                            checked={form.allowedIdentityIds.includes(identity.id)}
                            onCheckedChange={(checked) => toggleIdentity(identity.id, checked === true)}
                          />
                          {identity.name}
                        </label>
                      ))
                    )}
                  </div>
                </div>

                <div className="flex items-center justify-between rounded-md border border-border p-3">
                  <div className="space-y-0.5">
                    <Label htmlFor="offlineAllowed">是否允许脱机消费</Label>
                    <p className="text-xs text-muted-foreground">需与身份参数同时允许才会下发为允许。</p>
                  </div>
                  <Switch
                    id="offlineAllowed"
                    checked={form.offlineAllowed}
                    onCheckedChange={(checked) => setForm({ ...form, offlineAllowed: checked })}
                  />
                </div>
              </CardContent>
            </Card>

            <div className="flex justify-end">
              <Button
                disabled={saveMutation.isPending || detailLoading}
                onClick={() => saveMutation.mutate()}
              >
                <Save className="h-4 w-4" />
                保存
              </Button>
            </div>
          </div>
        )}
      </div>
    </PageContainer>
  );
};

export default PaymentConfigPage;
