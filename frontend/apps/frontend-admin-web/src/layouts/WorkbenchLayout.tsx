import { Moon, Sun, Wallet } from 'lucide-react';
import { NavLink, Outlet } from 'react-router-dom';
import { Button, cn } from '@walletconfig/ui';
import { MENU_ITEMS } from '@/routes/menuItems';
import { useThemeStore } from '@/stores/themeStore';

/** 工作台壳层：头部 + 左侧菜单 + 主体内容区，结构保持与管理端其余页面同构。 */
export const WorkbenchLayout = () => {
  const mode = useThemeStore((state) => state.mode);
  const toggleTheme = useThemeStore((state) => state.toggle);

  return (
    <div className="flex min-h-screen flex-col bg-background">
      <header className="flex h-14 items-center justify-between border-b border-border bg-card px-4">
        <div className="flex items-center gap-2">
          <Wallet className="h-5 w-5 text-primary" />
          <span className="text-base font-semibold">钱包配置管理台</span>
        </div>
        <div className="flex items-center gap-3">
          <Button
            variant="ghost"
            size="icon"
            onClick={toggleTheme}
            aria-label={mode === 'dark' ? '切换到亮色模式' : '切换到暗色模式'}
          >
            {mode === 'dark' ? <Sun className="h-4 w-4" /> : <Moon className="h-4 w-4" />}
          </Button>
          <div className="flex items-center gap-2">
            <div className="flex h-8 w-8 items-center justify-center rounded-full bg-secondary text-xs font-medium text-secondary-foreground">
              管
            </div>
            <div className="hidden flex-col leading-tight sm:flex">
              <span className="text-sm font-medium">学校管理员</span>
              <span className="text-xs text-muted-foreground">默认租户</span>
            </div>
          </div>
        </div>
      </header>

      <div className="flex flex-1">
        <aside className="hidden w-56 shrink-0 border-r border-border bg-card p-2 md:block">
          <nav className="flex flex-col gap-1">
            {MENU_ITEMS.map((item) => (
              <NavLink
                key={item.path}
                to={item.path}
                className={({ isActive }) =>
                  cn(
                    'flex items-center gap-2 rounded-md px-3 py-2 text-sm transition-colors',
                    isActive
                      ? 'bg-accent font-medium text-accent-foreground'
                      : 'text-muted-foreground hover:bg-accent hover:text-accent-foreground',
                  )
                }
              >
                <item.icon className="h-4 w-4" />
                {item.label}
              </NavLink>
            ))}
          </nav>
        </aside>

        <main className="flex-1 p-4 md:p-6">
          <div className="mx-auto w-full max-w-6xl">
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  );
};
