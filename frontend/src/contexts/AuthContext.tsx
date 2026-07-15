import { createContext, useContext, useState, useEffect, type ReactNode } from 'react';
import { login as apiLogin, logout as apiLogout, getCurrentUser, getUserMenus } from '@/services/api';
import type { AuthUser, MenuItem } from '@/types';

interface AuthContextType {
  user: AuthUser | null;
  menus: MenuItem[];
  loading: boolean;
  login: (username: string, password: string) => Promise<AuthUser>;
  logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(null);
  const [menus, setMenus] = useState<MenuItem[]>([]);
  const [loading, setLoading] = useState(true);

  // On mount: check if already logged in via session cookie
  useEffect(() => {
    let cancelled = false;

    async function checkAuth() {
      setLoading(true);
      console.log('[AuthContext] 开始检查登录状态...');
      try {
        const currentUser = await getCurrentUser();
        if (cancelled) return;

        if (currentUser) {
          console.log('[AuthContext] 已登录用户:', { id: currentUser.id, roleCode: currentUser.roleCode, realName: currentUser.realName });
          setUser(currentUser);
          const userMenus = await getUserMenus();
          if (cancelled) return;
          console.log('[AuthContext] 获取菜单成功:', userMenus.map(m => m.menuName));
          setMenus(userMenus);
        } else {
          console.log('[AuthContext] 未登录，getCurrentUser 返回 null');
          setUser(null);
          setMenus([]);
        }
      } catch (err) {
        console.warn('[AuthContext] 检查登录状态异常:', err);
        if (!cancelled) {
          setUser(null);
          setMenus([]);
        }
      } finally {
        if (!cancelled) {
          console.log('[AuthContext] 检查完成，loading -> false');
          setLoading(false);
        }
      }
    }

    checkAuth();
    return () => {
      cancelled = true;
    };
  }, []);

  const login = async (username: string, password: string): Promise<AuthUser> => {
    console.log('[AuthContext] 调用 apiLogin...');
    const loggedInUser = await apiLogin(username, password);
    console.log('[AuthContext] apiLogin 成功:', { id: loggedInUser.id, roleCode: loggedInUser.roleCode, realName: loggedInUser.realName });
    setUser(loggedInUser);
    const userMenus = await getUserMenus();
    console.log('[AuthContext] 菜单加载成功:', userMenus.map(m => m.menuName));
    setMenus(userMenus);
    return loggedInUser;
  };

  const logout = async () => {
    console.log('[AuthContext] 执行登出...');
    try {
      await apiLogout();
      console.log('[AuthContext] 登出API调用成功');
    } catch (err) {
      console.warn('[AuthContext] 登出API异常(已忽略):', err);
    }
    setUser(null);
    setMenus([]);
  };

  return (
    <AuthContext.Provider value={{ user, menus, loading, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
