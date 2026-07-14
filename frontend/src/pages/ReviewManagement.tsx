import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Eye, FileText, AlertCircle, RefreshCw } from 'lucide-react';
import { getPapers } from '@/services/api';
import type { Paper } from '@/types';
import PageHeader from '@/components/shared/PageHeader';
import StatusBadge from '@/components/shared/StatusBadge';
import DataTable, { type Column } from '@/components/shared/DataTable';
import EmptyState from '@/components/shared/EmptyState';
import LoadingSkeleton from '@/components/shared/LoadingSkeleton';
import { formatDate } from '@/lib/utils';

type PageState = 'loading' | 'error' | 'ready';

export default function ReviewManagement() {
  const navigate = useNavigate();
  const [papers, setPapers] = useState<Paper[]>([]);
  const [pageState, setPageState] = useState<PageState>('loading');
  const [errorMessage, setErrorMessage] = useState('');

  useEffect(() => {
    let cancelled = false;

    async function load() {
      setPageState('loading');
      setErrorMessage('');
      try {
        // 获取审稿中的稿件
        const reviewingData = await getPapers({ status: 'reviewing', pageSize: 100 });
        // 也获取已完成审稿的（accepted/rejected）
        const acceptedData = await getPapers({ status: 'accepted', pageSize: 100 });
        const rejectedData = await getPapers({ status: 'rejected', pageSize: 100 });

        if (!cancelled) {
          const allPapers = [
            ...reviewingData.items,
            ...acceptedData.items,
            ...rejectedData.items,
          ];
          setPapers(allPapers);
          setPageState('ready');
        }
      } catch (err) {
        if (!cancelled) {
          const message = err instanceof Error ? err.message : '加载审稿列表失败';
          setErrorMessage(message);
          setPageState('error');
        }
      }
    }

    load();
    return () => {
      cancelled = true;
    };
  }, []);

  const columns: Column<Paper>[] = [
    {
      key: 'title',
      header: '论文标题',
      render: (paper) => (
        <div className="max-w-[280px]">
          <p className="text-sm font-medium text-slate-700 truncate">{paper.title}</p>
          <p className="text-xs text-slate-400 mt-0.5">{paper.journalName}</p>
        </div>
      ),
    },
    {
      key: 'authorName',
      header: '作者',
      render: (paper) => (
        <span className="text-sm text-slate-600">{paper.authorName}</span>
      ),
    },
    {
      key: 'assignedReviewerName',
      header: '审稿人',
      render: (paper) => (
        <span className="text-sm text-slate-600">
          {paper.assignedReviewerName || '-'}
        </span>
      ),
    },
    {
      key: 'status',
      header: '状态',
      render: (paper) => <StatusBadge status={paper.status} />,
    },
    {
      key: 'submissionDate',
      header: '投稿日期',
      render: (paper) => (
        <span className="text-sm text-slate-500">{formatDate(paper.submissionDate)}</span>
      ),
    },
    {
      key: 'action',
      header: '操作',
      render: (paper) => (
        <button
          onClick={(e) => {
            e.stopPropagation();
            navigate(`/papers/${paper.id}/review`);
          }}
          className="inline-flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium text-blue-700 bg-blue-50 border border-blue-200 rounded-lg hover:bg-blue-100 transition-colors"
        >
          <Eye className="w-3.5 h-3.5" />
          {paper.status === 'reviewing' ? '审稿' : '查看'}
        </button>
      ),
    },
  ];

  if (pageState === 'loading') {
    return (
      <div className="p-6">
        <div className="mb-6">
          <div className="h-8 w-48 bg-slate-200 rounded animate-pulse" />
        </div>
        <LoadingSkeleton rows={6} />
      </div>
    );
  }

  if (pageState === 'error') {
    return (
      <div className="p-6">
        <PageHeader title="审稿管理" description="审稿人在线审阅稿件，给出审稿意见" />
        <div className="bg-white rounded-xl border border-slate-200 p-12 mt-6">
          <EmptyState
            icon={AlertCircle}
            title="加载失败"
            description={errorMessage || '无法加载审稿列表'}
            action={
              <button
                onClick={() => window.location.reload()}
                className="inline-flex items-center gap-2 px-4 py-2 bg-[#1e3a5f] text-white text-sm font-medium rounded-lg hover:bg-[#162d4a] transition-colors"
              >
                <RefreshCw className="w-4 h-4" />
                重新加载
              </button>
            }
          />
        </div>
      </div>
    );
  }

  return (
    <div className="p-6">
      <PageHeader
        title="审稿管理"
        description="审稿人在线审阅稿件，提交审稿意见（同意录用 / 退回修改）"
      />

      <div className="mt-6">
        {papers.length === 0 ? (
          <div className="bg-white rounded-xl border border-slate-200 p-12">
            <EmptyState
              icon={FileText}
              title="暂无待审稿件"
              description="当前没有需要审稿的稿件"
            />
          </div>
        ) : (
          <DataTable
            data={papers}
            columns={columns}
            onRowClick={(paper) => navigate(`/papers/${paper.id}/review`)}
            keyExtractor={(paper) => paper.id}
          />
        )}
      </div>
    </div>
  );
}
