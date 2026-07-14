import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getReviewers } from '@/services/api';
import type { Reviewer } from '@/types';
import PageHeader from '@/components/shared/PageHeader';
import SearchInput from '@/components/shared/SearchInput';
import DataTable, { type Column } from '@/components/shared/DataTable';
import EmptyState from '@/components/shared/EmptyState';
import LoadingSkeleton from '@/components/shared/LoadingSkeleton';
import { Eye } from 'lucide-react';

export default function ReviewerList() {
  const navigate = useNavigate();
  const [reviewers, setReviewers] = useState<Reviewer[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    loadReviewers();
  }, []);

  const loadReviewers = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getReviewers();
      setReviewers(data);
    } catch (err) {
      setError('加载审稿人列表失败，请稍后重试。');
      console.error('Failed to load reviewers:', err);
    } finally {
      setLoading(false);
    }
  };

  const filteredReviewers = reviewers.filter((reviewer) => {
    if (!searchTerm.trim()) return true;
    const term = searchTerm.toLowerCase();
    return (
      reviewer.name.toLowerCase().includes(term) ||
      (reviewer.email && reviewer.email.toLowerCase().includes(term)) ||
      (reviewer.institution && reviewer.institution.toLowerCase().includes(term)) ||
      (reviewer.specialty && reviewer.specialty.toLowerCase().includes(term))
    );
  });

  const columns: Column<Reviewer>[] = [
    {
      key: 'name',
      title: '姓名',
      render: (item) => (
        <span className="font-medium text-emerald-600 dark:text-emerald-400">
          {item.name}
        </span>
      ),
    },
    {
      key: 'email',
      title: '邮箱',
      render: (item) => (
        <span className="text-sm text-gray-600 dark:text-gray-400">
          {item.email || '-'}
        </span>
      ),
    },
    {
      key: 'institution',
      title: '所属机构',
      render: (item) => (
        <span className="text-sm text-gray-700 dark:text-gray-300">
          {item.institution || '-'}
        </span>
      ),
    },
    {
      key: 'specialty',
      title: '专业领域',
      render: (item) => (
        <span className="inline-flex items-center px-2 py-0.5 text-xs font-medium rounded-full bg-emerald-50 text-emerald-700 dark:bg-emerald-900/30 dark:text-emerald-300">
          {item.specialty || '-'}
        </span>
      ),
    },
    {
      key: 'reviewCount',
      title: '审稿数',
      render: (item) => (
        <span className="inline-flex items-center justify-center min-w-[2rem] px-2 py-0.5 text-xs font-semibold rounded-full bg-emerald-100 text-emerald-700 dark:bg-emerald-900/40 dark:text-emerald-300">
          {item.reviewIds?.length ?? 0}
        </span>
      ),
    },
    {
      key: 'actions',
      title: '操作',
      render: (item) => (
        <button
          onClick={(e) => {
            e.stopPropagation();
            navigate(`/reviewers/${item.id}`);
          }}
          className="inline-flex items-center gap-1 text-sm text-emerald-600 hover:text-emerald-800 dark:text-emerald-400 dark:hover:text-emerald-300 font-medium transition-colors"
        >
          <Eye className="w-3.5 h-3.5" />
          查看详情
        </button>
      ),
    },
  ];

  if (error) {
    return (
      <div className="min-h-[400px] flex flex-col items-center justify-center gap-4">
        <div className="text-red-500 dark:text-red-400 text-lg font-medium">{error}</div>
        <button
          onClick={loadReviewers}
          className="px-4 py-2 bg-emerald-600 text-white rounded-lg hover:bg-emerald-700 transition-colors"
        >
          重新加载
        </button>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <PageHeader
        title="审稿人管理"
        subtitle="查看和管理所有审稿人信息"
      />

      <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
        <div className="p-4 border-b border-gray-200 dark:border-gray-700">
          <SearchInput
            value={searchTerm}
            onChange={setSearchTerm}
            placeholder="搜索审稿人姓名、邮箱、机构、专业领域..."
          />
        </div>

        {loading ? (
          <div className="p-6">
            <LoadingSkeleton rows={6} />
          </div>
        ) : filteredReviewers.length === 0 ? (
          <div className="p-12">
            <EmptyState
              icon={Eye}
              title={searchTerm ? '未找到匹配的审稿人' : '暂无审稿人数据'}
              description={
                searchTerm
                  ? '请尝试调整搜索关键词'
                  : '系统中还没有审稿人数据，请添加审稿人'
              }
            />
          </div>
        ) : (
          <DataTable
            columns={columns}
            data={filteredReviewers}
            onRowClick={(item) => navigate(`/reviewers/${item.id}`)}
            emptyMessage="暂无审稿人数据"
          />
        )}

        {!loading && filteredReviewers.length > 0 && (
          <div className="px-4 py-3 border-t border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800/50 text-sm text-gray-500 dark:text-gray-400">
            共 {filteredReviewers.length} 位审稿人
          </div>
        )}
      </div>
    </div>
  );
}
