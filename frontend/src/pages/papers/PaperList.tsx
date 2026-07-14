import { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { Plus, ChevronLeft, ChevronRight, FileText, AlertCircle } from 'lucide-react';
import { getPapers } from '@/services/api';
import type { Paper, PaperFilter, JournalName, PaperStatus } from '@/types';
import PageHeader from '@/components/shared/PageHeader';
import SearchInput from '@/components/shared/SearchInput';
import StatusBadge from '@/components/shared/StatusBadge';
import DataTable, { type Column } from '@/components/shared/DataTable';
import EmptyState from '@/components/shared/EmptyState';
import { cn, formatDate } from '@/lib/utils';

const JOURNAL_OPTIONS: { label: string; value: JournalName | '' }[] = [
  { label: '全部期刊', value: '' },
  { label: '工学版', value: '工学版' },
  { label: '理学版', value: '理学版' },
  { label: '文科版', value: '文科版' },
  { label: '生物医学版', value: '生物医学版' },
];

const STATUS_OPTIONS: { label: string; value: PaperStatus | '' }[] = [
  { label: '全部状态', value: '' },
  { label: '已投稿', value: 'submitted' },
  { label: '审稿中', value: 'reviewing' },
  { label: '已收录', value: 'accepted' },
  { label: '已退回', value: 'rejected' },
  { label: '已发表', value: 'published' },
];

const PAGE_SIZE = 10;

export default function PaperList() {
  const navigate = useNavigate();

  // --- state ---
  const [papers, setPapers] = useState<Paper[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [keyword, setKeyword] = useState('');
  const [journalFilter, setJournalFilter] = useState<JournalName | ''>('');
  const [statusFilter, setStatusFilter] = useState<PaperStatus | ''>('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // --- data fetching ---
  const fetchPapers = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const filter: PaperFilter = {
        page,
        pageSize: PAGE_SIZE,
      };
      if (keyword.trim()) {
        filter.keyword = keyword.trim();
      }
      if (journalFilter) {
        filter.journalName = journalFilter as JournalName;
      }
      if (statusFilter) {
        filter.status = statusFilter as PaperStatus;
      }
      const result = await getPapers(filter);
      setPapers(result.items);
      setTotal(result.total);
    } catch (err) {
      const message = err instanceof Error ? err.message : '加载论文列表失败';
      setError(message);
      setPapers([]);
      setTotal(0);
    } finally {
      setLoading(false);
    }
  }, [page, keyword, journalFilter, statusFilter]);

  useEffect(() => {
    fetchPapers();
  }, [fetchPapers]);

  // --- handlers ---
  const handleSearch = (value: string) => {
    setKeyword(value);
    setPage(1);
  };

  const handleJournalChange = (value: JournalName | '') => {
    setJournalFilter(value);
    setPage(1);
  };

  const handleStatusChange = (value: PaperStatus | '') => {
    setStatusFilter(value);
    setPage(1);
  };

  const totalPages = Math.max(1, Math.ceil(total / PAGE_SIZE));

  const handlePrev = () => {
    if (page > 1) setPage(page - 1);
  };

  const handleNext = () => {
    if (page < totalPages) setPage(page + 1);
  };

  // --- columns ---
  const columns: Column<Paper>[] = [
    {
      key: 'title',
      title: '论文题目',
      render: (item: Paper) => (
        <div className="flex items-center gap-2.5">
          <div className="w-8 h-8 rounded-lg bg-blue-50 flex items-center justify-center flex-shrink-0">
            <FileText className="w-4 h-4 text-blue-500" />
          </div>
          <span className="font-medium text-slate-800 line-clamp-1 max-w-[320px]">
            {item.title}
          </span>
        </div>
      ),
    },
    {
      key: 'authorName',
      title: '作者',
      render: (item: Paper) => (
        <span className="text-slate-600">{item.authorName}</span>
      ),
    },
    {
      key: 'journalName',
      title: '期刊',
      render: (item: Paper) => (
        <span className="inline-flex items-center px-2.5 py-0.5 rounded-md bg-slate-100 text-slate-600 text-xs font-medium">
          {item.journalName}
        </span>
      ),
    },
    {
      key: 'status',
      title: '状态',
      render: (item: Paper) => <StatusBadge status={item.status} size="sm" />,
    },
    {
      key: 'submissionDate',
      title: '投稿日期',
      render: (item: Paper) => (
        <span className="text-slate-500 text-xs">{formatDate(item.submissionDate)}</span>
      ),
    },
    {
      key: 'actions',
      title: '操作',
      className: 'text-right',
      render: (item: Paper) => (
        <div className="flex items-center justify-end gap-2">
          <button
            onClick={(e) => {
              e.stopPropagation();
              navigate(`/papers/${item.id}`);
            }}
            className="px-3 py-1.5 text-xs font-medium text-blue-600 bg-blue-50 rounded-lg hover:bg-blue-100 transition-colors"
          >
            查看详情
          </button>
        </div>
      ),
    },
  ];

  // --- render ---
  return (
    <div className="p-6 max-w-7xl mx-auto">
      <PageHeader
        title="论文管理"
        subtitle="管理所有投稿论文的审稿与发表流程"
        actions={
          <button
            onClick={() => navigate('/papers/submit')}
            className="inline-flex items-center gap-2 px-4 py-2.5 bg-[#1e3a5f] text-white text-sm font-medium rounded-lg hover:bg-[#162d4a] transition-colors shadow-sm"
          >
            <Plus className="w-4 h-4" />
            在线投稿
          </button>
        }
      />

      {/* --- filters --- */}
      <div className="bg-white rounded-xl border border-slate-200 p-4 mb-6">
        <div className="flex flex-wrap items-center gap-3">
          <SearchInput
            value={keyword}
            onChange={handleSearch}
            placeholder="搜索论文题目或作者姓名..."
            className="flex-1 min-w-[240px]"
          />

          <select
            value={journalFilter}
            onChange={(e) => handleJournalChange(e.target.value as JournalName | '')}
            className="px-3.5 py-2.5 bg-white border border-slate-200 rounded-lg text-sm text-slate-700 focus:outline-none focus:border-blue-400 focus:ring-2 focus:ring-blue-100 transition-all min-w-[140px]"
          >
            {JOURNAL_OPTIONS.map((opt) => (
              <option key={opt.value} value={opt.value}>
                {opt.label}
              </option>
            ))}
          </select>

          <select
            value={statusFilter}
            onChange={(e) => handleStatusChange(e.target.value as PaperStatus | '')}
            className="px-3.5 py-2.5 bg-white border border-slate-200 rounded-lg text-sm text-slate-700 focus:outline-none focus:border-blue-400 focus:ring-2 focus:ring-blue-100 transition-all min-w-[130px]"
          >
            {STATUS_OPTIONS.map((opt) => (
              <option key={opt.value} value={opt.value}>
                {opt.label}
              </option>
            ))}
          </select>

          {(keyword || journalFilter || statusFilter) && (
            <button
              onClick={() => {
                setKeyword('');
                setJournalFilter('');
                setStatusFilter('');
                setPage(1);
              }}
              className="px-3 py-2.5 text-sm text-slate-500 hover:text-slate-700 transition-colors"
            >
              清除筛选
            </button>
          )}
        </div>
      </div>

      {/* --- error state --- */}
      {error && (
        <div className="bg-red-50 border border-red-200 rounded-xl p-6 mb-6">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-full bg-red-100 flex items-center justify-center">
              <AlertCircle className="w-5 h-5 text-red-500" />
            </div>
            <div>
              <p className="text-sm font-medium text-red-700">加载失败</p>
              <p className="text-xs text-red-500 mt-0.5">{error}</p>
            </div>
            <button
              onClick={fetchPapers}
              className="ml-auto px-4 py-2 text-sm font-medium text-red-600 bg-red-100 rounded-lg hover:bg-red-200 transition-colors"
            >
              重新加载
            </button>
          </div>
        </div>
      )}

      {/* --- data table --- */}
      {!error && (
        <>
          <DataTable<Paper>
            columns={columns}
            data={papers}
            keyField="id"
            onRowClick={(item) => navigate(`/papers/${item.id}`)}
            emptyMessage={loading ? undefined : '暂无论文数据，请尝试调整筛选条件或创建新的投稿'}
            loading={loading}
          />

          {/* --- empty state (non-loading, no error) --- */}
          {!loading && papers.length === 0 && !error && (
            <EmptyState
              icon={FileText}
              title="暂无论文"
              description="还没有任何投稿论文，点击右上角的「在线投稿」按钮开始投稿吧"
              action={
                <button
                  onClick={() => navigate('/papers/submit')}
                  className="inline-flex items-center gap-2 px-4 py-2 bg-[#1e3a5f] text-white text-sm font-medium rounded-lg hover:bg-[#162d4a] transition-colors"
                >
                  <Plus className="w-4 h-4" />
                  在线投稿
                </button>
              }
            />
          )}

          {/* --- pagination --- */}
          {papers.length > 0 && (
            <div className="flex items-center justify-between mt-6 px-2">
              <p className="text-sm text-slate-500">
                共 <span className="font-semibold text-slate-700">{total}</span> 篇论文，
                第 <span className="font-semibold text-slate-700">{page}</span>/{totalPages} 页
              </p>
              <div className="flex items-center gap-1">
                <button
                  onClick={handlePrev}
                  disabled={page <= 1}
                  className={cn(
                    'inline-flex items-center gap-1 px-3 py-2 text-sm font-medium rounded-lg transition-colors',
                    page <= 1
                      ? 'text-slate-300 cursor-not-allowed'
                      : 'text-slate-600 hover:bg-slate-100',
                  )}
                >
                  <ChevronLeft className="w-4 h-4" />
                  上一页
                </button>

                {Array.from({ length: totalPages }, (_, i) => i + 1)
                  .filter((p) => {
                    // Show first, last, and pages around current
                    if (totalPages <= 7) return true;
                    if (p === 1 || p === totalPages) return true;
                    if (Math.abs(p - page) <= 1) return true;
                    return false;
                  })
                  .map((p, idx, arr) => (
                    <span key={p}>
                      {idx > 0 && arr[idx - 1] !== p - 1 && (
                        <span className="px-1 text-slate-300">...</span>
                      )}
                      <button
                        onClick={() => setPage(p)}
                        className={cn(
                          'w-9 h-9 rounded-lg text-sm font-medium transition-colors',
                          p === page
                            ? 'bg-[#1e3a5f] text-white shadow-sm'
                            : 'text-slate-600 hover:bg-slate-100',
                        )}
                      >
                        {p}
                      </button>
                    </span>
                  ))}

                <button
                  onClick={handleNext}
                  disabled={page >= totalPages}
                  className={cn(
                    'inline-flex items-center gap-1 px-3 py-2 text-sm font-medium rounded-lg transition-colors',
                    page >= totalPages
                      ? 'text-slate-300 cursor-not-allowed'
                      : 'text-slate-600 hover:bg-slate-100',
                  )}
                >
                  下一页
                  <ChevronRight className="w-4 h-4" />
                </button>
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
}
