import { createContext, useContext, useState, useEffect, type ReactNode } from 'react';
import { login as apiLogin, logout as apiLogout, getCurrentUser, getUserMenus } from '@/services/api';
import type { AuthUser, MenuItem } from '@/types';

interface AuthContextType {
  user: AuthUser | null;
  menus: MenuItem[];
  loading: boolean;
  login: (username: string, password: string) => Promise<void>;
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
      try {
        const currentUser = await getCurrentUser();
        if (cancelled) return;

        if (currentUser) {
          setUser(currentUser);
          const userMenus = await getUserMenus();
          if (cancelled) return;
          setMenus(userMenus);
        } else {
          setUser(null);
          setMenus([]);
        }
      } catch {
        if (!cancelled) {
          setUser(null);
          setMenus([]);
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    }

    checkAuth();
    return () => {
      cancelled = true;
    };
  }, []);

  const login = async (username: string, password: string) => {
    const loggedInUser = await apiLogin(username, password);
    setUser(loggedInUser);
    const userMenus = await getUserMenus();
    setMenus(userMenus);
  };

  const logout = async () => {
    try {
      await apiLogout();
    } catch {
      // ignore errors on logout
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
