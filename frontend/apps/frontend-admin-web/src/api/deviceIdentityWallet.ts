import { http } from './http';

/** 下发表行。 */
export interface DeviceIdentityWalletItem {
  id: string;
  deviceId: string;
  deviceSerialNo: string | null;
  identityId: string;
  identityName: string | null;
  walletType: 'WORK' | 'DEDUCT';
  walletTypeLabel: string;
  walletNo: string;
  deductAllowed: boolean;
  offlineAllowed: boolean;
  validFlag: string;
  validFlagLabel: string;
  version: string;
}

export interface DeviceIdentityWalletListQuery {
  deviceId?: string;
  identityId?: string;
  validFlag?: string;
}

export const fetchDeviceIdentityWalletList = (query: DeviceIdentityWalletListQuery = {}) =>
  http.post<DeviceIdentityWalletItem[]>('/api/admin/device-identity-wallet/list', query);
