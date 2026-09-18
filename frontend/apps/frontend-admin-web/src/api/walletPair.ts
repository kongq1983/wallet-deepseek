import { http, type ApiEnvelope } from './http';

/** 钱包配对列表项。 */
export interface WalletPairItem {
  id: string;
  workWalletNo: string;
  deductWalletNo: string;
}

export interface WalletPairAddParams {
  /** 未填写时传 null（而非 0），由后端返回「请输入工作钱包编号」提示。 */
  workWalletNo: number | null;
  /** 未填写时传 null（而非 0），由后端返回「请输入追扣钱包编号」提示。 */
  deductWalletNo: number | null;
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
