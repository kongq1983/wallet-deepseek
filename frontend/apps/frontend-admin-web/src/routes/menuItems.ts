import {
  Boxes,
  IdCard,
  MonitorSmartphone,
  Network,
  Send,
  WalletCards,
  type LucideIcon,
} from 'lucide-react';

/** 菜单项：路由 path 统一使用 kebab-case。 */
export interface MenuItem {
  label: string;
  path: string;
  icon: LucideIcon;
}

export const MENU_ITEMS: MenuItem[] = [
  { label: '租户钱包设置', path: '/wallet-pair', icon: WalletCards },
  { label: '机构管理', path: '/organization', icon: Network },
  { label: '机构支付参数', path: '/payment-config', icon: Boxes },
  { label: '身份管理', path: '/consumer-identity', icon: IdCard },
  { label: '设备管理', path: '/device', icon: MonitorSmartphone },
  { label: '下发表查询', path: '/device-identity-wallet', icon: Send },
];
