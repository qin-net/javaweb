import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { BookOpen, User, Lock, LogIn, Eye, EyeOff } from 'lucide-react';
import { useAuth } from '@/contexts/AuthContext';
import { toast } from 'sonner';

const TEST_ACCOUNTS = [
  { username: 'admin', role: '管理员', desc: '全功能管理' },
  { username: 'reviewer1', role: '审稿人', desc: '审稿管理' },
  { username: 'author1', role: '作者', desc: '在线投稿' },
];

export default function Login() {
  const navigate = useNavigate();
  const { login } = useAuth();

  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!username.trim() || !password.trim()) {
      toast.error('请输入用户名和密码');
      return;
    }

    setLoading(true);
    try {
      await login(username.trim(), password);
      toast.success('登录成功');
      navigate('/');
    } catch (err) {
      const message = err instanceof Error ? err.message : '登录失败，请稍后重试';
      toast.error(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex">
      {/* ===== Left brand panel ===== */}
      <div
        className="hidden lg:flex lg:w-1/2 flex-col justify-between p-12 relative overflow-hidden"
        style={{ backgroundColor: '#1e3a5f' }}
      >
        {/* Decorative circles */}
        <div className="absolute top-0 right-0 w-96 h-96 rounded-full opacity-5" style={{ backgroundColor: '#c9a96e', transform: 'translate(30%, -30%)' }} />
        <div className="absolute bottom-0 left-0 w-64 h-64 rounded-full opacity-5" style={{ backgroundColor: '#c9a96e', transform: 'translate(-30%, 30%)' }} />

        {/* Logo */}
        <div className="flex items-center gap-3 relative z-10">
          <div className="w-12 h-12 rounded-lg flex items-center justify-center" style={{ backgroundColor: '#c9a96e' }}>
            <BookOpen className="w-7 h-7 text-white" />
          </div>
          <div>
            <h1 className="text-white font-bold text-lg leading-tight">大黑山大学学报</h1>
            <p className="text-white/50 text-sm">期刊投稿管理系统</p>
          </div>
        </div>

        {/* Center text */}
        <div className="relative z-10">
          <h2 className="text-white text-4xl font-bold leading-tight mb-4">
            学术期刊<br />投稿管理平台
          </h2>
          <p className="text-white/60 text-base leading-relaxed max-w-md">
            集成在线投稿、审稿指派、审稿管理于一体，为作者、审稿人和管理员提供高效便捷的期刊投稿全流程服务。
          </p>
        </div>

        {/* Bottom features */}
        <div className="flex items-center gap-6 relative z-10">
          <div className="flex items-center gap-2">
            <div className="w-2 h-2 rounded-full" style={{ backgroundColor: '#c9a96e' }} />
            <span className="text-white/50 text-sm">在线投稿</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-2 h-2 rounded-full" style={{ backgroundColor: '#c9a96e' }} />
            <span className="text-white/50 text-sm">审稿管理</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-2 h-2 rounded-full" style={{ backgroundColor: '#c9a96e' }} />
            <span className="text-white/50 text-sm">流程追踪</span>
          </div>
        </div>
      </div>

      {/* ===== Right login form ===== */}
      <div className="w-full lg:w-1/2 flex items-center justify-center p-8 bg-slate-50">
        <div className="w-full max-w-md">
          {/* Mobile logo */}
          <div className="flex lg:hidden items-center gap-3 mb-8 justify-center">
            <div className="w-10 h-10 rounded-lg flex items-center justify-center" style={{ backgroundColor: '#1e3a5f' }}>
              <BookOpen className="w-6 h-6 text-white" />
            </div>
            <div>
              <h1 className="text-slate-800 font-bold text-sm">大黑山大学学报</h1>
              <p className="text-slate-400 text-xs">期刊投稿管理系统</p>
            </div>
          </div>

          <div className="bg-white rounded-2xl shadow-sm border border-slate-200 p-8">
            <div className="mb-8">
              <h2 className="text-2xl font-bold text-slate-800 mb-1">欢迎登录</h2>
              <p className="text-sm text-slate-400">请输入您的账号信息</p>
            </div>

            <form onSubmit={handleSubmit} className="space-y-5">
              {/* Username */}
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-2">
                  用户名
                </label>
                <div className="relative">
                  <User className="absolute left-3.5 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400" />
                  <input
                    type="text"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    placeholder="请输入用户名"
                    autoComplete="username"
                    className="w-full pl-11 pr-4 py-3 bg-slate-50 border border-slate-200 rounded-lg text-sm text-slate-700 placeholder:text-slate-400 focus:outline-none focus:border-blue-400 focus:ring-2 focus:ring-blue-100 focus:bg-white transition-all"
                  />
                </div>
              </div>

              {/* Password */}
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-2">
                  密码
                </label>
                <div className="relative">
                  <Lock className="absolute left-3.5 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400" />
                  <input
                    type={showPassword ? 'text' : 'password'}
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="请输入密码"
                    autoComplete="current-password"
                    className="w-full pl-11 pr-11 py-3 bg-slate-50 border border-slate-200 rounded-lg text-sm text-slate-700 placeholder:text-slate-400 focus:outline-none focus:border-blue-400 focus:ring-2 focus:ring-blue-100 focus:bg-white transition-all"
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute right-3.5 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 transition-colors"
                  >
                    {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                  </button>
                </div>
              </div>

              {/* Submit */}
              <button
                type="submit"
                disabled={loading}
                className="w-full inline-flex items-center justify-center gap-2 px-4 py-3 text-white text-sm font-medium rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed shadow-sm"
                style={{ backgroundColor: '#1e3a5f' }}
                onMouseEnter={(e) => { if (!loading) e.currentTarget.style.backgroundColor = '#162d4a'; }}
                onMouseLeave={(e) => { e.currentTarget.style.backgroundColor = '#1e3a5f'; }}
              >
                {loading ? (
                  <>
                    <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                    登录中...
                  </>
                ) : (
                  <>
                    <LogIn className="w-4 h-4" />
                    登录
                  </>
                )}
              </button>
            </form>

            {/* Hint area */}
            <div className="mt-6 pt-6 border-t border-slate-100">
              <div className="flex items-center gap-2 mb-4">
                <div className="w-1.5 h-1.5 rounded-full" style={{ backgroundColor: '#c9a96e' }} />
                <p className="text-xs text-slate-400">默认密码: <span className="font-mono font-medium text-slate-600">123456</span></p>
              </div>

              <div className="bg-slate-50 rounded-lg p-4">
                <p className="text-xs font-medium text-slate-500 mb-3">可用测试账号</p>
                <div className="space-y-2">
                  {TEST_ACCOUNTS.map((account) => (
                    <button
                      key={account.username}
                      type="button"
                      onClick={() => {
                        setUsername(account.username);
                        setPassword('123456');
                      }}
                      className="w-full flex items-center justify-between px-3 py-2 bg-white rounded-lg border border-slate-200 hover:border-slate-300 hover:bg-slate-50 transition-all text-left"
                    >
                      <div className="flex items-center gap-3">
                        <span className="text-xs font-mono text-slate-700 bg-slate-100 px-2 py-0.5 rounded">{account.username}</span>
                        <span className="text-xs text-slate-500">{account.role}</span>
                      </div>
                      <span className="text-xs text-slate-400">{account.desc}</span>
                    </button>
                  ))}
                </div>
              </div>
            </div>
          </div>

          <p className="text-center text-xs text-slate-400 mt-6">
            © 2026 大黑山大学学报 · 期刊投稿管理系统
          </p>
        </div>
      </div>
    </div>
  );
}
