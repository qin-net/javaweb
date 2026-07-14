import { NavLink, useLocation } from 'react-router-dom';
import {
  LayoutDashboard, FileText, Users, UserCheck,
  UserPlus, BookOpen, Send
} from 'lucide-react';

function cn(...classes: (string | boolean | undefined)[]) {
  return classes.filter(Boolean).join(' ');
}

const navItems = [
  { to: '/', icon: LayoutDashboard, label: '工作台' },
  { to: '/papers', icon: FileText, label: '稿件管理' },
  { to: '/papers/submit', icon: Send, label: '在线投稿' },
  { to: '/authors', icon: Users, label: '作者管理' },
  { to: '/reviewers', icon: UserCheck, label: '审稿人管理' },
  { to: '/assignments', icon: UserPlus, label: '审稿指派' },
];

export default function Sidebar() {
  const location = useLocation();

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

      {/* Nav items */}
      <nav className="flex-1 px-3 py-6 space-y-1 overflow-y-auto">
        {navItems.map((item) => {
          const isActive = location.pathname === item.to ||
            (item.to !== '/' && location.pathname.startsWith(item.to));
          return (
            <NavLink
              key={item.to}
              to={item.to}
              className={cn(
                'flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-all duration-200',
                isActive
                  ? 'bg-white/15 text-white'
                  : 'text-white/60 hover:text-white hover:bg-white/10'
              )}
              style={isActive ? { borderLeft: '3px solid #c9a96e' } : { borderLeft: '3px solid transparent' }}
            >
              <item.icon className="w-5 h-5 flex-shrink-0" />
              <span>{item.label}</span>
            </NavLink>
          );
        })}
      </nav>

      {/* Footer */}
      <div className="px-6 py-4 border-t border-white/10">
        <p className="text-white/40 text-xs text-center">© 2026 大黑山大学学报</p>
      </div>
    </aside>
  );
}
