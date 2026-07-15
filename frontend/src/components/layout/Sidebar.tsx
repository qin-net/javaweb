import { NavLink, useLocation, useNavigate } from 'react-router-dom';
import {
  LayoutDashboard, FileText, Send, ClipboardCheck,
  Users, UserCheck, UserPlus, BookOpen, LogOut,
  type LucideIcon,
} from 'lucide-react';
import { useAuth } from '@/contexts/AuthContext';
import type { MenuItem } from '@/types';

function cn(...classes: (string | boolean | undefined)[]) {
  return classes.filter(Boolean).join(' ');
}

// Map menu icon string to lucide-react component
const iconMap: Record<string, LucideIcon> = {
  'LayoutDashboard': LayoutDashboard,
  'FileText': FileText,
  'Send': Send,
  'ClipboardCheck': ClipboardCheck,
  'Users': Users,
  'UserCheck': UserCheck,
  'UserPlus': UserPlus,
};

function getMenuIcon(iconName: string): LucideIcon {
  return iconMap[iconName] || FileText;
}

export default function Sidebar() {
  const location = useLocation();
  const navigate = useNavigate();
  const { menus, logout } = useAuth();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  // Sort menus by sortOrder
  const sortedMenus = [...menus].sort((a, b) => a.sortOrder - b.sortOrder);

  return (
    <aside className="fixed left-0 top-0 h-screen w-64 flex flex-col" style={{ backgroundColor: '#1e3a5f' }}>
      {/* Logo area */}
      <div className="flex items-center gap-3 px-6 py-6 border-b border-white/10">
        <div className="w-10 h-10 rounded-lg flex items-center justify-center" style={{ backgroundColor: '#c9a96e' }}>
          <BookOpen className="w-6 h-6 text-white" />
        </div>
        <div>
          <h1 className="text-white font-bold text-sm leading-tight">大黑山大学学报</h1>
          <p className="text-white/50 text-xs">期刊投稿管理系统</p>
        </div>
      </div>

      {/* Nav items - dynamically rendered from menus */}
      <nav className="flex-1 px-3 py-6 space-y-1 overflow-y-auto">
        {sortedMenus.map((menu: MenuItem) => {
          const Icon = getMenuIcon(menu.icon);
          const isActive = location.pathname === menu.path ||
            (menu.path !== '/' && location.pathname.startsWith(menu.path));
          return (
            <NavLink
              key={menu.id}
              to={menu.path}
              className={cn(
                'flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-all duration-200',
                isActive
                  ? 'bg-white/15 text-white'
                  : 'text-white/60 hover:text-white hover:bg-white/10'
              )}
              style={isActive ? { borderLeft: '3px solid #c9a96e' } : { borderLeft: '3px solid transparent' }}
            >
              <Icon className="w-5 h-5 flex-shrink-0" />
              <span>{menu.menuName}</span>
            </NavLink>
          );
        })}
      </nav>

      {/* Footer with logout */}
      <div className="px-3 py-4 border-t border-white/10 space-y-1">
        <button
          onClick={handleLogout}
          className="w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium text-white/60 hover:text-white hover:bg-white/10 transition-all duration-200"
        >
          <LogOut className="w-5 h-5 flex-shrink-0" />
          <span>退出登录</span>
        </button>
        <p className="text-white/40 text-xs text-center pt-2">© 2026 大黑山大学学报</p>
      </div>
    </aside>
  );
}
