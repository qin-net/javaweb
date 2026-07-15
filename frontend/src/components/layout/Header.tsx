import { useLocation, useNavigate } from 'react-router-dom';
import { Bell, LogOut } from 'lucide-react';
import { useAuth } from '@/contexts/AuthContext';

const pageTitles: Record<string, string> = {
  '/': '工作台',
  '/papers': '稿件管理',
  '/papers/submit': '在线投稿',
  '/authors': '作者管理',
  '/reviewers': '审稿人管理',
  '/assignments': '审稿指派',
  '/review-management': '审稿管理',
};

export default function Header() {
  const location = useLocation();
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  const basePath = '/' + (location.pathname.split('/')[1] || '');
  const title = pageTitles[basePath] ||
    (location.pathname.includes('/papers/') && !location.pathname.includes('/submit') && !location.pathname.includes('/review') ? '稿件详情' :
     location.pathname.includes('/review') ? '审稿' :
     location.pathname.includes('/authors/') ? '作者详情' :
     location.pathname.includes('/reviewers/') ? '审稿人详情' : '页面');

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  // Get first character of realName for avatar
  const avatarChar = user?.realName ? user.realName.charAt(0) : '?';

  return (
    <header className="h-16 bg-white border-b border-slate-200 flex items-center justify-between px-8 sticky top-0 z-10">
      <div>
        <h2 className="text-lg font-semibold text-slate-800">{title}</h2>
        <p className="text-xs text-slate-400 mt-0.5">
          {location.pathname === '/' ? '概览系统数据与统计' :
           location.pathname.includes('/submit') ? '提交新的学术论文稿件' :
           location.pathname.includes('/review') ? '审阅稿件并给出审稿意见' : ''}
        </p>
      </div>
      <div className="flex items-center gap-4">
        <button className="relative p-2 text-slate-400 hover:text-slate-600 transition-colors">
          <Bell className="w-5 h-5" />
          <span className="absolute top-1.5 right-1.5 w-2 h-2 bg-red-500 rounded-full"></span>
        </button>
        <div className="flex items-center gap-3 pl-4 border-l border-slate-200">
          <div className="w-8 h-8 rounded-full flex items-center justify-center text-white text-sm font-medium" style={{ backgroundColor: '#1e3a5f' }}>
            {avatarChar}
          </div>
          <div className="text-sm">
            <p className="font-medium text-slate-700">{user?.realName || '未登录'}</p>
            <p className="text-xs text-slate-400">{user?.email || ''}</p>
          </div>
          <button
            onClick={handleLogout}
            className="ml-2 p-2 text-slate-400 hover:text-red-500 transition-colors rounded-lg hover:bg-red-50"
            title="退出登录"
          >
            <LogOut className="w-4 h-4" />
          </button>
        </div>
      </div>
    </header>
  );
}
