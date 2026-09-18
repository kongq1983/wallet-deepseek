import { http, type ApiEnvelope } from './http';

/** 机构支付参数。 */
export interface PaymentConfigDetail {
  organizationId: string;
  organizationName: string;
  configured: boolean;
  deductAllowed: boolean;
  workWalletNo: string | null;
  deductWalletNo: string | null;
  identityRestricted: boolean;
  allowedIdentityIds: string[] | null;
  allowedIdentityNames: string[] | null;
  offlineAllowed: boolean;
}

export interface PaymentConfigSaveParams {
  organizationId: string;
  deductAllowed: boolean;
  workWalletNo: number;
  identityRestricted: boolean;
  allowedIdentityIds: string[];
  offlineAllowed: boolean;
}

export const fetchPaymentConfig = (organizationId: string) =>
  http.post<PaymentConfigDetail>('/api/admin/organization-payment-config/get', { organizationId });

export const savePaymentConfig = (params: PaymentConfigSaveParams): Promise<ApiEnvelope<null>> =>
  http.post<null>('/api/admin/organization-payment-config/update', params);
