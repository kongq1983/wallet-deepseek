import { http, type ApiEnvelope } from './http';

/** 设备类型编码。 */
export type DeviceTypeCode = 'POS_MACHINE' | 'FACE_RECOGNITION_MACHINE' | 'OTHER';

/** 设备列表项。 */
export interface DeviceItem {
  id: string;
  serialNo: string;
  deviceType: DeviceTypeCode;
  deviceTypeLabel: string;
  organizationId: string;
}

export interface DeviceAddParams {
  serialNo: string;
  deviceType: DeviceTypeCode;
  organizationId: string;
}

export const fetchDeviceList = (serialNo?: string) =>
  http.post<DeviceItem[]>('/api/admin/device/list', { serialNo });

export const addDevice = (params: DeviceAddParams): Promise<ApiEnvelope<null>> =>
  http.post<null>('/api/admin/device/add', params);

export const deleteDevice = (id: string): Promise<ApiEnvelope<null>> =>
  http.post<null>('/api/admin/device/delete', { id });
