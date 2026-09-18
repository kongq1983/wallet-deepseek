import { http, type ApiEnvelope } from './http';

/** 钱包配对列表项。 */
export interface WalletPairItem {
  id: string;
  workWalletNo: string;
  deductWalletNo: string;
}

export interface WalletPairAddParams {
  workWalletNo: number;
  deductWalletNo: number;
}

export interface WalletPairUpdateParams extends WalletPairAddParams {
  id: string;
}

export const fetchWalletPairList = () => http.post<WalletPairItem[]>('/api/admin/wallet-pair/list');

export const addWalletPair = (params: WalletPairAddParams): Promise<ApiEnvelope<null>> =>
  http.post<null>('/api/admin/wallet-pair/add', params);

export const updateWalletPair = (params: WalletPairUpdateParams): Promise<ApiEnvelope<null>> =>
  http.post<null>('/api/admin/wallet-pair/update', params);

export const deleteWalletPair = (id: string): Promise<ApiEnvelope<null>> =>
  http.post<null>('/api/admin/wallet-pair/delete', { id });
