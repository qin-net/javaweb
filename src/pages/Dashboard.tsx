import { useEffect, useState } from 'react';
import { FileText, Clock, CheckCircle2, BookOpen } from 'lucide-react';
import {
  PieChart,
  Pie,
  Cell,
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  Legend,
} from 'recharts';
import { getDashboardStats } from '@/services/api';
import type { DashboardStats } from '@/types';
import StatCard from '@/components/shared/StatCard';
import { formatDate } from '@/lib/utils';
import { useNavigate } from 'react-router-dom';
import StatusBadge from '@/components/shared/StatusBadge';
import EmptyState from '@/components/shared/EmptyState';

// ── Constants ──────────────────────────────────────────────
const PIE_COLORS = ['#3b82f6', '#10b981', '#f59e0b', '#8b5cf6'];

// ── Loading skeleton ───────────────────────────────────────
function DashboardSkeleton() {
  return (
    <div className="space-y-8 animate-pulse">
      {/* Stat card skeletons */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
        {Array.from({ length: 4 }).map((_, i) => (
          <div
            key={i}
            className="bg-white rounded-xl border border-slate-200 p-6"
          >
            <div className="flex items-start justify-between">
              <div className="w-12 h-12 rounded-lg bg-slate-200" />
              <div className="w-14 h-5 rounded-full bg-slate-200" />
            </div>
            <div className="mt-4 space-y-2">
              <div className="h-8 bg-slate-200 rounded w-16" />
              <div className="h-4 bg-slate-100 rounded w-20" />
            </div>
          </div>
        ))}
      </div>

      {/* Chart skeletons */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white rounded-xl border border-slate-200 p-6">
          <div className="h-5 bg-slate-200 rounded w-24 mb-2" />
          <div className="h-4 bg-slate-100 rounded w-40 mb-6" />
          <div className="h-64 bg-slate-100 rounded-lg" />
        </div>
        <div className="bg-white rounded-xl border border-slate-200 p-6">
          <div className="h-5 bg-slate-200 rounded w-24 mb-2" />
          <div className="h-4 bg-slate-100 rounded w-40 mb-6" />
          <div className="h-64 bg-slate-100 rounded-lg" />
        </div>
      </div>

      {/* Table skeleton */}
      <div className="bg-white rounded-xl border border-slate-200 p-6">
        <div className="h-5 bg-slate-200 rounded w-24 mb-2" />
        <div className="h-4 bg-slate-100 rounded w-40 mb-6" />
        <div className="space-y-3">
          {Array.from({ length: 5 }).map((_, i) => (
            <div key={i} className="h-10 bg-slate-50 rounded" />
          ))}
        </div>
      </div>
    </div>
  );
}

// ── Custom tooltips ────────────────────────────────────────
interface PieTooltipPayload {
  name: string;
  value: number;
}

function PieTooltip({
  active,
  payload,
  total,
}: {
  active?: boolean;
  payload?: { payload: PieTooltipPayload }[];
  total: number;
}) {
  if (active && payload && payload.length) {
    const { name, value } = payload[0].payload;
    const pct = total > 0 ? ((value / total) * 100).toFixed(1) : '0';
    return (
      <div className="bg-white border border-slate-200 rounded-lg shadow-lg px-3 py-2">
        <p className="text-sm font-medium text-slate-700">{name}</p>
        <p className="text-xs text-slate-500">投稿数: {value}</p>
        <p className="text-xs text-slate-500">占比: {pct}%</p>
      </div>
    );
  }
  return null;
}

function LineTooltip({
  active,
  payload,
  label,
}: {
  active?: boolean;
  payload?: { color: string; name: string; value: number }[];
  label?: string;
}) {
  if (active && payload && payload.length) {
    return (
      <div className="bg-white border border-slate-200 rounded-lg shadow-lg px-3 py-2">
        <p className="text-sm font-medium text-slate-700 mb-1">{label}</p>
        {payload.map((entry, idx) => (
          <p
            key={idx}
            className="text-xs text-slate-500 flex items-center gap-1.5"
          >
            <span
              className="w-2 h-2 rounded-full inline-block shrink-0"
              style={{ backgroundColor: entry.color }}
            />
            {entry.name}: {entry.value}
          </p>
        ))}
      </div>
    );
  }
  return null;
}

const RADIAN = Math.PI / 180;

// ═══════════════════════════════════════════════════════════
// Dashboard Page
// ═══════════════════════════════════════════════════════════
export default function Dashboard() {
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    getDashboardStats()
      .then((data) => {
        setStats(data);
        setError(false);
      })
      .catch(() => {
        setStats(null);
        setError(true);
      })
      .finally(() => {
        setLoading(false);
      });
  }, []);

  // ── Loading ──────────────────────────────────────────────
  if (loading) return <DashboardSkeleton />;

  // ── Error / empty ────────────────────────────────────────
  if (error || !stats) {
    return (
      <EmptyState
        title="加载失败"
        description="无法获取统计数据，请检查网络连接后重试"
        action={
          <button
            onClick={() => {
              setLoading(true);
              setError(false);
              getDashboardStats()
                .then((data) => setStats(data))
                .catch(() => setError(true))
                .finally(() => setLoading(false));
            }}
            className="px-4 py-2 text-sm font-medium text-white bg-[#1e3a5f] rounded-lg hover:bg-[#162d4a] transition-colors"
          >
            重新加载
          </button>
        }
      />
    );
  }

  // ── Derived values ───────────────────────────────────────
  const pieTotal = stats.byJournal.reduce((sum, item) => sum + item.count, 0);

  // ═════════════════════════════════════════════════════════
  // Render
  // ═════════════════════════════════════════════════════════
  return (
    <div className="space-y-8">
      {/* ── Stat cards ──────────────────────────────────── */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard
          icon={FileText}
          label="总投稿"
          value={stats.totalSubmissions}
          color="blue"
        />
        <StatCard
          icon={Clock}
          label="审稿中"
          value={stats.underReview}
          color="amber"
        />
        <StatCard
          icon={CheckCircle2}
          label="已收录"
          value={stats.accepted}
          color="emerald"
        />
        <StatCard
          icon={BookOpen}
          label="已发表"
          value={stats.published}
          color="purple"
        />
      </div>

      {/* ── Charts row ──────────────────────────────────── */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* ---- Pie: journal distribution ---- */}
        <div className="bg-white rounded-xl border border-slate-200 p-6">
          <h3 className="text-base font-semibold text-slate-800">期刊分布</h3>
          <p className="text-sm text-slate-400 mt-0.5 mb-4">
            各期刊投稿数量占比
          </p>

          {stats.byJournal.length === 0 ? (
            <div className="flex items-center justify-center h-72 text-sm text-slate-400">
              暂无期刊投稿数据
            </div>
          ) : (
            <ResponsiveContainer width="100%" height={320}>
              <PieChart>
                <Pie
                  data={stats.byJournal}
                  dataKey="count"
                  nameKey="name"
                  cx="50%"
                  cy="50%"
                  innerRadius={55}
                  outerRadius={105}
                  paddingAngle={2}
                  label={({ cx, cy, midAngle, innerRadius, outerRadius, percent, name }: any) => {
                    const radius = innerRadius + (outerRadius - innerRadius) * 0.6;
                    const x = cx + radius * Math.cos(-midAngle * RADIAN);
                    const y = cy + radius * Math.sin(-midAngle * RADIAN);

                    if (percent < 0.05) return null;

                    return (
                      <text
                        x={x}
                        y={y}
                        fill="#fff"
                        textAnchor="middle"
                        dominantBaseline="central"
                        style={{ fontSize: 12, fontWeight: 500 }}
                      >
                        <tspan x={x} dy="-0.6em">
                          {name}
                        </tspan>
                        <tspan x={x} dy="1.4em">
                          {(percent * 100).toFixed(0)}%
                        </tspan>
                      </text>
                    );
                  }}
                  labelLine={false}
                >
                  {stats.byJournal.map((_, idx) => (
                    <Cell
                      key={idx}
                      fill={PIE_COLORS[idx % PIE_COLORS.length]}
                      stroke="#fff"
                      strokeWidth={1}
                    />
                  ))}
                </Pie>
                <Tooltip content={<PieTooltip total={pieTotal} />} />
                <Legend
                  iconType="circle"
                  wrapperStyle={{ fontSize: 13 }}
                  formatter={(value: string) => (
                    <span className="text-sm text-slate-600">{value}</span>
                  )}
                />
              </PieChart>
            </ResponsiveContainer>
          )}
        </div>

        {/* ---- Line: monthly trend ---- */}
        <div className="bg-white rounded-xl border border-slate-200 p-6">
          <h3 className="text-base font-semibold text-slate-800">月度趋势</h3>
          <p className="text-sm text-slate-400 mt-0.5 mb-4">
            近12个月投稿与收录趋势
          </p>

          {stats.monthlyTrend.length === 0 ? (
            <div className="flex items-center justify-center h-72 text-sm text-slate-400">
              暂无月度趋势数据
            </div>
          ) : (
            <ResponsiveContainer width="100%" height={320}>
              <LineChart
                data={stats.monthlyTrend}
                margin={{ top: 5, right: 20, left: 0, bottom: 5 }}
              >
                <CartesianGrid
                  strokeDasharray="3 3"
                  stroke="#f1f5f9"
                  vertical={false}
                />
                <XAxis
                  dataKey="month"
                  tick={{ fontSize: 12, fill: '#94a3b8' }}
                  axisLine={{ stroke: '#e2e8f0' }}
                  tickLine={false}
                />
                <YAxis
                  tick={{ fontSize: 12, fill: '#94a3b8' }}
                  axisLine={{ stroke: '#e2e8f0' }}
                  tickLine={false}
                  allowDecimals={false}
                  width={36}
                />
                <Tooltip content={<LineTooltip />} />
                <Legend
                  iconType="line"
                  wrapperStyle={{ fontSize: 13 }}
                  formatter={(value: string) => (
                    <span className="text-sm text-slate-600">{value}</span>
                  )}
                />
                <Line
                  type="monotone"
                  dataKey="submissions"
                  name="投稿数"
                  stroke="#3b82f6"
                  strokeWidth={2.5}
                  dot={{ r: 3, fill: '#3b82f6', strokeWidth: 0 }}
                  activeDot={{
                    r: 5,
                    fill: '#3b82f6',
                    strokeWidth: 2,
                    stroke: '#fff',
                  }}
                />
                <Line
                  type="monotone"
                  dataKey="accepted"
                  name="收录数"
                  stroke="#10b981"
                  strokeWidth={2.5}
                  dot={{ r: 3, fill: '#10b981', strokeWidth: 0 }}
                  activeDot={{
                    r: 5,
                    fill: '#10b981',
                    strokeWidth: 2,
                    stroke: '#fff',
                  }}
                />
              </LineChart>
            </ResponsiveContainer>
          )}
        </div>
      </div>

      {/* ── Recent papers table ─────────────────────────── */}
      <div className="bg-white rounded-xl border border-slate-200 overflow-hidden">
        <div className="px-6 pt-6 pb-4 border-b border-slate-100">
          <h3 className="text-base font-semibold text-slate-800">最近投稿</h3>
          <p className="text-sm text-slate-400 mt-0.5">最新论文投稿列表</p>
        </div>

        {stats.recentPapers.length === 0 ? (
          <div className="py-16 text-center">
            <p className="text-sm text-slate-400">暂无最近投稿</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b border-slate-100">
                  <th className="text-left text-xs font-medium text-slate-400 uppercase tracking-wider px-6 py-3">
                    论文标题
                  </th>
                  <th className="text-left text-xs font-medium text-slate-400 uppercase tracking-wider px-6 py-3">
                    作者
                  </th>
                  <th className="text-left text-xs font-medium text-slate-400 uppercase tracking-wider px-6 py-3">
                    期刊
                  </th>
                  <th className="text-left text-xs font-medium text-slate-400 uppercase tracking-wider px-6 py-3">
                    状态
                  </th>
                  <th className="text-left text-xs font-medium text-slate-400 uppercase tracking-wider px-6 py-3">
                    投稿日期
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-50">
                {stats.recentPapers.map((paper) => (
                  <tr
                    key={paper.id}
                    onClick={() => navigate(`/papers/${paper.id}`)}
                    className="hover:bg-slate-50 transition-colors cursor-pointer group"
                  >
                    <td className="px-6 py-3.5 text-sm font-medium text-slate-800 max-w-[280px] truncate group-hover:text-[#1e3a5f] transition-colors">
                      {paper.title}
                    </td>
                    <td className="px-6 py-3.5 text-sm text-slate-600 whitespace-nowrap">
                      {paper.authorName}
                    </td>
                    <td className="px-6 py-3.5 text-sm text-slate-600 whitespace-nowrap">
                      {paper.journalName}
                    </td>
                    <td className="px-6 py-3.5 whitespace-nowrap">
                      <StatusBadge status={paper.status} size="sm" />
                    </td>
                    <td className="px-6 py-3.5 text-sm text-slate-500 whitespace-nowrap">
                      {formatDate(paper.submissionDate)}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
