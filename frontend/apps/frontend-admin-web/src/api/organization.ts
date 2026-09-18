import { http, type ApiEnvelope } from './http';

/** 机构类型编码。 */
export type OrganizationTypeCode = 'RESTAURANT' | 'STALL' | 'SUPERMARKET';

/** 机构列表项。 */
export interface OrganizationItem {
  id: string;
  name: string;
  orgType: OrganizationTypeCode;
  orgTypeLabel: string;
  superiorId: string | null;
  level: string;
}

export interface OrganizationAddParams {
  name: string;
  orgType: OrganizationTypeCode;
  superiorId?: string;
}

export const fetchOrganizationList = () => http.post<OrganizationItem[]>('/api/admin/organization/list');

export const addOrganization = (params: OrganizationAddParams): Promise<ApiEnvelope<null>> =>
  http.post<null>('/api/admin/organization/add', params);

export const renameOrganization = (id: string, name: string): Promise<ApiEnvelope<null>> =>
  http.post<null>('/api/admin/organization/update', { id, name });
