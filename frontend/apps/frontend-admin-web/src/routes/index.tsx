import { Suspense, lazy } from 'react';
import { createBrowserRouter, Navigate } from 'react-router-dom';
import { WorkbenchLayout } from '@/layouts/WorkbenchLayout';

const WalletPairPage = lazy(() => import('@/views/walletPair'));
const OrganizationPage = lazy(() => import('@/views/organization'));
const PaymentConfigPage = lazy(() => import('@/views/paymentConfig'));
const ConsumerIdentityPage = lazy(() => import('@/views/consumerIdentity'));
const DevicePage = lazy(() => import('@/views/device'));
const DeviceIdentityWalletPage = lazy(() => import('@/views/deviceIdentityWallet'));

const withSuspense = (node: React.ReactNode) => (
  <Suspense fallback={<div className="p-6 text-sm text-muted-foreground">加载中…</div>}>{node}</Suspense>
);

/** 路由表：全部页面默认懒加载。 */
export const router = createBrowserRouter([
  {
    path: '/',
    element: <WorkbenchLayout />,
    children: [
      { index: true, element: <Navigate to="/wallet-pair" replace /> },
      { path: 'wallet-pair', element: withSuspense(<WalletPairPage />) },
      { path: 'organization', element: withSuspense(<OrganizationPage />) },
      { path: 'payment-config', element: withSuspense(<PaymentConfigPage />) },
      { path: 'consumer-identity', element: withSuspense(<ConsumerIdentityPage />) },
      { path: 'device', element: withSuspense(<DevicePage />) },
      { path: 'device-identity-wallet', element: withSuspense(<DeviceIdentityWalletPage />) },
      { path: '*', element: <div className="p-6 text-sm text-muted-foreground">页面不存在</div> },
    ],
  },
]);
