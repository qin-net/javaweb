import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getAuthor, getAuthorPapers } from '@/services/api';
import type { Author, Paper } from '@/types';
import PageHeader from '@/components/shared/PageHeader';
import DataTable, { type Column } from '@/components/shared/DataTable';
import StatusBadge from '@/components/shared/StatusBadge';
import EmptyState from '@/components/shared/EmptyState';
import LoadingSkeleton from '@/components/shared/LoadingSkeleton';
import { formatDate, cn } from '@/lib/utils';
import { ArrowLeft, Mail, Building2, GraduationCap, Phone, FileText, User } from 'lucide-react';

export default function AuthorDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [author, setAuthor] = useState<Author | null>(null);
  const [papers, setPapers] = useState<Paper[]>([]);
  const [loadingAuthor, setLoadingAuthor] = useState(true);
  const [loadingPapers, setLoadingPapers] = useState(true);
  const [authorError, setAuthorError] = useState<string | null>(null);
  const [papersError, setPapersError] = useState<string | null>(null);

  useEffect(() => {
    if (id) {
      loadAuthor(id);
      loadPapers(id);
    }
  }, [id]);

  const loadAuthor = async (authorId: string) => {
    setLoadingAuthor(true);
    setAuthorError(null);
    try {
      const data = await getAuthor(authorId);
      setAuthor(data);
    } catch (err) {
      setAuthorError('加载作者信息失败，请稍后重试。');
      console.error('Failed to load author:', err);
    } finally {
      setLoadingAuthor(false);
    }
  };

  const loadPapers = async (authorId: string) => {
    setLoadingPapers(true);
    setPapersError(null);
    try {
      const data = await getAuthorPapers(authorId);
      setPapers(data);
    } catch (err) {
      setPapersError('加载论文列表失败，请稍后重试。');
      console.error('Failed to load papers:', err);
    } finally {
      setLoadingPapers(false);
    }
  };

  const paperColumns: Column<Paper>[] = [
    {
      key: 'title',
      title: '论文标题',
      render: (item) => (
        <span
          className="font-medium text-gray-900 dark:text-gray-100 line-clamp-1 max-w-[320px] block"
          title={item.title}
        >
          {item.title}
        </span>
      ),
    },
    {
      key: 'journal',
      title: '期刊',
      render: (item) => (
        <span className="text-sm px-2 py-0.5 rounded bg-indigo-50 text-indigo-700 dark:bg-indigo-900/30 dark:text-indigo-300">
          {item.journalName || '-'}
        </span>
      ),
    },
    {
      key: 'status',
      title: '状态',
      render: (item) => <StatusBadge status={item.status} />,
    },
    {
      key: 'createdAt',
      title: '投稿日期',
      render: (item) => (
        <span className="text-sm text-gray-500 dark:text-gray-400">
          {item.submissionDate ? formatDate(item.submissionDate) : '-'}
        </span>
      ),
    },
    {
      key: 'updatedAt',
      title: '最后更新',
      render: (item) => (
        <span className="text-sm text-gray-500 dark:text-gray-400">
          {item.submissionDate ? formatDate(item.submissionDate) : '-'}
        </span>
      ),
    },
  ];

  // --- Error state for author ---
  if (!loadingAuthor && authorError && !author) {
    return (
      <div className="space-y-6">
        <PageHeader
          title="作者详情"
          subtitle="查看作者详细信息"
          actions={
            <button
              onClick={() => navigate('/authors')}
              className="inline-flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 bg-white dark:bg-gray-800 border border-gray-300 dark:border-gray-600 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
            >
              <ArrowLeft className="w-4 h-4" />
              返回列表
            </button>
          }
        />
        <div className="min-h-[300px] flex flex-col items-center justify-center gap-4">
          <div className="text-red-500 dark:text-red-400 text-lg font-medium">{authorError}</div>
          <button
            onClick={() => id && loadAuthor(id)}
            className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
          >
            重新加载
          </button>
        </div>
      </div>
    );
  }

  // --- Not found ---
  if (!loadingAuthor && !author && !authorError) {
    return (
      <div className="space-y-6">
        <PageHeader
          title="作者详情"
          subtitle="查看作者详细信息"
          actions={
            <button
              onClick={() => navigate('/authors')}
              className="inline-flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 bg-white dark:bg-gray-800 border border-gray-300 dark:border-gray-600 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
            >
              <ArrowLeft className="w-4 h-4" />
              返回列表
            </button>
          }
        />
        <EmptyState
          icon={User}
          title="作者未找到"
          description="该作者不存在或已被删除"
        />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <PageHeader
        title="作者详情"
        subtitle="查看作者详细信息"
        actions={
          <button
            onClick={() => navigate('/authors')}
            className="inline-flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 bg-white dark:bg-gray-800 border border-gray-300 dark:border-gray-600 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
          >
            <ArrowLeft className="w-4 h-4" />
            返回列表
          </button>
        }
      />

      {/* Author Info Card */}
      <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
        <div className="px-6 py-5 border-b border-gray-200 dark:border-gray-700">
          <h2 className="text-lg font-semibold text-gray-900 dark:text-gray-100">基本信息</h2>
        </div>

        {loadingAuthor ? (
          <div className="p-6">
            <LoadingSkeleton rows={5} />
          </div>
        ) : author ? (
          <div className="p-6">
            <div className="flex items-start gap-5 mb-6">
              <div className="w-16 h-16 rounded-full bg-blue-100 dark:bg-blue-900/40 flex items-center justify-center flex-shrink-0">
                <User className="w-8 h-8 text-blue-600 dark:text-blue-400" />
              </div>
              <div>
                <h3 className="text-2xl font-bold text-gray-900 dark:text-gray-100">
                  {author.name}
                </h3>
                {author.department && (
                  <p className="text-gray-500 dark:text-gray-400 mt-0.5">{author.department}</p>
                )}
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {author.email && (
                <div className="flex items-center gap-3 p-3 rounded-lg bg-gray-50 dark:bg-gray-900/50">
                  <Mail className="w-5 h-5 text-gray-400 dark:text-gray-500 flex-shrink-0" />
                  <div>
                    <div className="text-xs text-gray-500 dark:text-gray-400">邮箱</div>
                    <div className="text-sm font-medium text-gray-900 dark:text-gray-100">
                      {author.email}
                    </div>
                  </div>
                </div>
              )}

              {author.institution && (
                <div className="flex items-center gap-3 p-3 rounded-lg bg-gray-50 dark:bg-gray-900/50">
                  <Building2 className="w-5 h-5 text-gray-400 dark:text-gray-500 flex-shrink-0" />
                  <div>
                    <div className="text-xs text-gray-500 dark:text-gray-400">所属机构</div>
                    <div className="text-sm font-medium text-gray-900 dark:text-gray-100">
                      {author.institution}
                    </div>
                  </div>
                </div>
              )}

              {author.department && (
                <div className="flex items-center gap-3 p-3 rounded-lg bg-gray-50 dark:bg-gray-900/50">
                  <GraduationCap className="w-5 h-5 text-gray-400 dark:text-gray-500 flex-shrink-0" />
                  <div>
                    <div className="text-xs text-gray-500 dark:text-gray-400">部门/院系</div>
                    <div className="text-sm font-medium text-gray-900 dark:text-gray-100">
                      {author.department}
                    </div>
                  </div>
                </div>
              )}

              {author.phone && (
                <div className="flex items-center gap-3 p-3 rounded-lg bg-gray-50 dark:bg-gray-900/50">
                  <Phone className="w-5 h-5 text-gray-400 dark:text-gray-500 flex-shrink-0" />
                  <div>
                    <div className="text-xs text-gray-500 dark:text-gray-400">电话</div>
                    <div className="text-sm font-medium text-gray-900 dark:text-gray-100">
                      {author.phone}
                    </div>
                  </div>
                </div>
              )}
            </div>
          </div>
        ) : null}
      </div>

      {/* Author Papers */}
      <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
        <div className="px-6 py-5 border-b border-gray-200 dark:border-gray-700 flex items-center gap-2">
          <FileText className="w-5 h-5 text-gray-400" />
          <h2 className="text-lg font-semibold text-gray-900 dark:text-gray-100">
            投稿论文
          </h2>
          {!loadingPapers && (
            <span className="text-sm text-gray-500 dark:text-gray-400">
              (共 {papers.length} 篇)
            </span>
          )}
        </div>

        {loadingPapers ? (
          <div className="p-6">
            <LoadingSkeleton rows={5} />
          </div>
        ) : papersError ? (
          <div className="p-12 text-center">
            <div className="text-red-500 dark:text-red-400 mb-3">{papersError}</div>
            <button
              onClick={() => id && loadPapers(id)}
              className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors text-sm"
            >
              重新加载
            </button>
          </div>
        ) : papers.length === 0 ? (
          <div className="p-12">
            <EmptyState
              icon={FileText}
              title="暂无投稿论文"
              description="该作者还没有提交过论文"
            />
          </div>
        ) : (
          <DataTable
            columns={paperColumns}
            data={papers}
            emptyMessage="暂无投稿论文"
          />
        )}
      </div>
    </div>
  );
}
