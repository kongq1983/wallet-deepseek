import { http, type ApiEnvelope } from './http';

/** 消费身份列表项。 */
export interface ConsumerIdentityItem {
  id: string;
  name: string;
  offlineAllowed: boolean;
}

export interface ConsumerIdentitySaveParams {
  name: string;
  offlineAllowed: boolean;
}

export const fetchConsumerIdentityList = () =>
  http.post<ConsumerIdentityItem[]>('/api/admin/consumer-identity/list');

export const createConsumerIdentity = (params: ConsumerIdentitySaveParams): Promise<ApiEnvelope<null>> =>
  http.post<null>('/api/admin/consumer-identity/add', params);

export const updateConsumerIdentity = (
  id: string,
  params: ConsumerIdentitySaveParams,
): Promise<ApiEnvelope<null>> => http.post<null>('/api/admin/consumer-identity/update', { id, ...params });

export const deleteConsumerIdentity = (id: string): Promise<ApiEnvelope<null>> =>
  http.post<null>('/api/admin/consumer-identity/delete', { id });
