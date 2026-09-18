import { QueryClientProvider } from '@tanstack/react-query';
import { RouterProvider } from 'react-router-dom';
import { MessageBar } from '@/components/MessageBar';
import { queryClient } from '@/lib/queryClient';
import { router } from '@/routes';

export const App = () => (
  <QueryClientProvider client={queryClient}>
    <RouterProvider router={router} />
    <MessageBar />
  </QueryClientProvider>
);
