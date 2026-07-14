import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getAuthors } from '@/services/api';
import type { Author } from '@/types';
import PageHeader from '@/components/shared/PageHeader';
import SearchInput from '@/components/shared/SearchInput';
import DataTable, { type Column } from '@/components/shared/DataTable';
import EmptyState from '@/components/shared/EmptyState';
import LoadingSkeleton from '@/components/shared/LoadingSkeleton';
import { formatDate } from '@/lib/utils';
import { Users } from 'lucide-react';

export default function AuthorList() {
  const navigate = useNavigate();
  const [authors, setAuthors] = useState<Author[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    loadAuthors();
  }, []);

  const loadAuthors = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getAuthors();
      setAuthors(data);
    } catch (err) {
      setError('加载作者列表失败，请稍后重试。');
      console.error('Failed to load authors:', err);
    } finally {
      setLoading(false);
    }
  };

  const filteredAuthors = authors.filter((author) => {
    if (!searchTerm.trim()) return true;
    const term = searchTerm.toLowerCase();
    return (
      author.name.toLowerCase().includes(term) ||
      (author.email && author.email.toLowerCase().includes(term)) ||
      (author.institution && author.institution.toLowerCase().includes(term)) ||
      (author.department && author.department.toLowerCase().includes(term))
    );
  });

  const columns: Column<Author>[] = [
    {
      key: 'name',
      title: '姓名',
      render: (item) => (
        <span className="font-medium text-blue-600 dark:text-blue-400">
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
      key: 'department',
      title: '部门/院系',
      render: (item) => (
        <span className="text-sm text-gray-700 dark:text-gray-300">
          {item.department || '-'}
        </span>
      ),
    },
    {
      key: 'paperCount',
      title: '论文数',
      render: (item) => (
        <span className="inline-flex items-center justify-center min-w-[2rem] px-2 py-0.5 text-xs font-semibold rounded-full bg-blue-100 text-blue-700 dark:bg-blue-900/40 dark:text-blue-300">
          {item.paperIds?.length ?? 0}
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
            navigate(`/authors/${item.id}`);
          }}
          className="text-sm text-blue-600 hover:text-blue-800 dark:text-blue-400 dark:hover:text-blue-300 font-medium transition-colors"
        >
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
          onClick={loadAuthors}
          className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
        >
          重新加载
        </button>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <PageHeader
        title="作者管理"
        subtitle="查看和管理所有投稿作者信息"
      />

      <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
        <div className="p-4 border-b border-gray-200 dark:border-gray-700">
          <SearchInput
            value={searchTerm}
            onChange={setSearchTerm}
            placeholder="搜索作者姓名、邮箱、机构..."
          />
        </div>

        {loading ? (
          <div className="p-6">
            <LoadingSkeleton rows={6} />
          </div>
        ) : filteredAuthors.length === 0 ? (
          <div className="p-12">
            <EmptyState
              icon={Users}
              title={searchTerm ? '未找到匹配的作者' : '暂无作者数据'}
              description={
                searchTerm
                  ? '请尝试调整搜索关键词'
                  : '系统中还没有作者数据，请添加作者'
              }
            />
          </div>
        ) : (
          <DataTable
            columns={columns}
            data={filteredAuthors}
            onRowClick={(item) => navigate(`/authors/${item.id}`)}
            emptyMessage="暂无作者数据"
          />
        )}

        {!loading && filteredAuthors.length > 0 && (
          <div className="px-4 py-3 border-t border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800/50 text-sm text-gray-500 dark:text-gray-400">
            共 {filteredAuthors.length} 位作者
          </div>
        )}
      </div>
    </div>
  );
}
