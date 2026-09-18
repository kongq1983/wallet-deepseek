import { create } from 'zustand';

type ThemeMode = 'light' | 'dark';

const STORAGE_KEY = 'theme-mode';

const readStoredTheme = (): ThemeMode => {
  const stored = localStorage.getItem(STORAGE_KEY);
  return stored === 'dark' ? 'dark' : 'light';
};

const applyThemeToDocument = (mode: ThemeMode) => {
  document.documentElement.classList.toggle('dark', mode === 'dark');
};

interface ThemeState {
  mode: ThemeMode;
  toggle: () => void;
}

/** 主题状态持久化到 localStorage，通过 .dark 类切换设计令牌。 */
export const useThemeStore = create<ThemeState>((set, get) => ({
  mode: readStoredTheme(),
  toggle: () => {
    const next: ThemeMode = get().mode === 'dark' ? 'light' : 'dark';
    localStorage.setItem(STORAGE_KEY, next);
    applyThemeToDocument(next);
    set({ mode: next });
  },
}));

/** 应用启动时恢复主题，避免首屏闪烁。 */
export const initTheme = () => applyThemeToDocument(readStoredTheme());
