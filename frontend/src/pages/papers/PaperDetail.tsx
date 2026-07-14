import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  ArrowLeft,
  CheckCircle2,
  UserPlus,
  FileText,
  Send,
  Eye,
  BookOpen,
  Calendar,
  User,
  Tag,
  AlertCircle,
  RefreshCw,
} from 'lucide-react';
import { getPaper, getPaperReviews, acceptPaper } from '@/services/api';
import type { Paper, Review } from '@/types';
import StatusBadge from '@/components/shared/StatusBadge';
import Timeline from '@/components/shared/Timeline';
import ConfirmDialog from '@/components/shared/ConfirmDialog';
import EmptyState from '@/components/shared/EmptyState';
import LoadingSkeleton from '@/components/shared/LoadingSkeleton';
import { cn, formatDate } from '@/lib/utils';
import { toast } from 'sonner';

function formatDateTime(dateStr: string): string {
  if (!dateStr) return '-';
  const date = new Date(dateStr);
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  });
}

type PageState = 'loading' | 'error' | 'ready';

export default function PaperDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  // --- state ---
  const [paper, setPaper] = useState<Paper | null>(null);
  const [reviews, setReviews] = useState<Review[]>([]);
  const [pageState, setPageState] = useState<PageState>('loading');
  const [errorMessage, setErrorMessage] = useState('');
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [accepting, setAccepting] = useState(false);

  // --- data fetching ---
  useEffect(() => {
    if (!id) return;
    let cancelled = false;

    async function load() {
      setPageState('loading');
      setErrorMessage('');
      try {
        const [paperData, reviewsData] = await Promise.all([
          getPaper(id!),
          getPaperReviews(id!),
        ]);
        if (!cancelled) {
          setPaper(paperData);
          setReviews(reviewsData);
          setPageState('ready');
        }
      } catch (err) {
        if (!cancelled) {
          const message = err instanceof Error ? err.message : '加载论文详情失败';
          setErrorMessage(message);
          setPageState('error');
        }
      }
    }

    load();
    return () => {
      cancelled = true;
    };
  }, [id]);

  // --- handle accept ---
  const handleAccept = async () => {
    if (!id) return;
    setAccepting(true);
    try {
      const updated = await acceptPaper(id);
      setPaper(updated);
      toast.success('论文已标记为已收录');
    } catch (err) {
      const message = err instanceof Error ? err.message : '操作失败';
      toast.error(message);
    } finally {
      setAccepting(false);
    }
  };

  // --- loading state ---
  if (pageState === 'loading') {
    return (
      <div className="p-6 max-w-5xl mx-auto">
        <div className="mb-6">
          <div className="h-8 w-48 bg-slate-200 rounded animate-pulse" />
        </div>
        <LoadingSkeleton rows={8} />
      </div>
    );
  }

  // --- error state ---
  if (pageState === 'error' || !paper) {
    return (
      <div className="p-6 max-w-5xl mx-auto">
        <button
          onClick={() => navigate('/papers')}
          className="inline-flex items-center gap-2 text-sm text-slate-500 hover:text-slate-700 mb-6 transition-colors"
        >
          <ArrowLeft className="w-4 h-4" />
          返回论文列表
        </button>
        <div className="bg-white rounded-xl border border-slate-200 p-12">
          <EmptyState
            icon={AlertCircle}
            title="加载失败"
            description={errorMessage || '无法加载论文详情'}
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

  // --- build timeline items ---
  const timelineItems: Array<{
    id: string;
    title: string;
    description?: string;
    date: string;
    icon?: React.ComponentType<{ className?: string }>;
    status?: 'completed' | 'current' | 'pending' | 'rejected';
  }> = [];

  // 1. Submitted
  timelineItems.push({
    id: 'submitted',
    title: '投稿',
    description: `作者 ${paper.authorName} 提交了论文`,
    date: formatDateTime(paper.submissionDate),
    icon: Send,
    status: 'completed',
  });

  // 2. Reviewing
  if (paper.assignedReviewerId) {
    const isReviewing = paper.status === 'reviewing';
    const isReviewed = paper.status === 'accepted' || paper.status === 'rejected' || paper.status === 'published';
    timelineItems.push({
      id: 'reviewing',
      title: '审稿中',
      description: `审稿人: ${paper.assignedReviewerName || paper.assignedReviewerId}`,
      date: paper.reviewDate ? formatDateTime(paper.reviewDate) : (isReviewing ? '进行中...' : '-'),
      icon: Eye,
      status: isReviewed ? 'completed' : isReviewing ? 'current' : 'pending',
    });
  }

  // 3. Accepted / Rejected
  if (paper.status === 'accepted' || paper.status === 'published') {
    timelineItems.push({
      id: 'accepted',
      title: '已收录',
      description: '论文通过审稿，已被收录',
      date: paper.acceptanceDate ? formatDateTime(paper.acceptanceDate) : '-',
      icon: CheckCircle2,
      status: 'completed',
    });
  } else if (paper.status === 'rejected') {
    timelineItems.push({
      id: 'rejected',
      title: '已退回',
      description: '论文未通过审稿，已退回修改',
      date: paper.reviewDate ? formatDateTime(paper.reviewDate) : '-',
      icon: AlertCircle,
      status: 'rejected',
    });
  } else {
    timelineItems.push({
      id: 'decision',
      title: paper.assignedReviewerId ? '等待审稿结果' : '等待指派审稿人',
      description: paper.assignedReviewerId ? '审稿人正在审阅论文' : '请指派审稿人开始审稿流程',
      date: '-',
      icon: Eye,
      status: 'pending',
    });
  }

  // 4. Published
  if (paper.status === 'published') {
    timelineItems.push({
      id: 'published',
      title: '已发表',
      description: '论文已正式发表',
      date: paper.publicationDate ? formatDateTime(paper.publicationDate) : '-',
      icon: BookOpen,
      status: 'completed',
    });
  }

  // --- render ---
  return (
    <div className="p-6 max-w-5xl mx-auto">
      {/* --- back + header --- */}
      <button
        onClick={() => navigate('/papers')}
        className="inline-flex items-center gap-2 text-sm text-slate-500 hover:text-slate-700 mb-4 transition-colors"
      >
        <ArrowLeft className="w-4 h-4" />
        返回论文列表
      </button>

      {/* --- title row --- */}
      <div className="bg-white rounded-xl border border-slate-200 p-6 mb-6">
        <div className="flex items-start justify-between gap-4">
          <div className="flex-1 min-w-0">
            <h1 className="text-xl font-bold text-slate-800 leading-tight mb-3">
              {paper.title}
            </h1>
            <div className="flex flex-wrap items-center gap-4 text-sm text-slate-500">
              <span className="inline-flex items-center gap-1.5">
                <User className="w-4 h-4 text-slate-400" />
                {paper.authorName}
              </span>
              <span className="inline-flex items-center gap-1.5">
                <BookOpen className="w-4 h-4 text-slate-400" />
                {paper.journalName}
              </span>
              <span className="inline-flex items-center gap-1.5">
                <Calendar className="w-4 h-4 text-slate-400" />
                投稿于 {formatDate(paper.submissionDate)}
              </span>
            </div>
          </div>
          <StatusBadge status={paper.status} size="md" />
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* --- left column: main content --- */}
        <div className="lg:col-span-2 space-y-6">
          {/* abstract */}
          <div className="bg-white rounded-xl border border-slate-200 p-6">
            <h2 className="text-base font-semibold text-slate-800 mb-3 flex items-center gap-2">
              <FileText className="w-4 h-4 text-slate-400" />
              摘要
            </h2>
            <p className="text-sm text-slate-600 leading-relaxed whitespace-pre-wrap">
              {paper.abstract}
            </p>
          </div>

          {/* keywords */}
          <div className="bg-white rounded-xl border border-slate-200 p-6">
            <h2 className="text-base font-semibold text-slate-800 mb-3 flex items-center gap-2">
              <Tag className="w-4 h-4 text-slate-400" />
              关键词
            </h2>
            <div className="flex flex-wrap gap-2">
              {paper.keywords.map((kw) => (
                <span
                  key={kw}
                  className="inline-flex items-center px-3 py-1 bg-[#eef2f6] text-[#1e3a5f] rounded-md text-sm font-medium"
                >
                  {kw}
                </span>
              ))}
            </div>
          </div>

          {/* content */}
          <div className="bg-white rounded-xl border border-slate-200 p-6">
            <h2 className="text-base font-semibold text-slate-800 mb-3 flex items-center gap-2">
              <FileText className="w-4 h-4 text-slate-400" />
              正文内容
            </h2>
            <div className="text-sm text-slate-600 leading-relaxed whitespace-pre-wrap max-h-[600px] overflow-y-auto">
              {paper.content}
            </div>
          </div>

          {/* references */}
          {paper.references && paper.references.length > 0 && (
            <div className="bg-white rounded-xl border border-slate-200 p-6">
              <h2 className="text-base font-semibold text-slate-800 mb-4 flex items-center gap-2">
                <BookOpen className="w-4 h-4 text-slate-400" />
                参考文献
                <span className="text-xs font-normal text-slate-400 ml-1">
                  ({paper.references.length} 篇)
                </span>
              </h2>
              <div className="overflow-x-auto">
                <table className="w-full text-sm">
                  <thead>
                    <tr className="border-b border-slate-100">
                      <th className="text-left py-2 px-3 text-xs font-semibold text-slate-400 uppercase">序号</th>
                      <th className="text-left py-2 px-3 text-xs font-semibold text-slate-400 uppercase">标题</th>
                      <th className="text-left py-2 px-3 text-xs font-semibold text-slate-400 uppercase">作者</th>
                      <th className="text-left py-2 px-3 text-xs font-semibold text-slate-400 uppercase">期刊</th>
                      <th className="text-left py-2 px-3 text-xs font-semibold text-slate-400 uppercase">年份</th>
                      <th className="text-left py-2 px-3 text-xs font-semibold text-slate-400 uppercase">卷/期/页</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-50">
                    {paper.references.map((ref, idx) => (
                      <tr key={ref.id} className="hover:bg-slate-50/50 transition-colors">
                        <td className="py-2.5 px-3 text-slate-400 text-xs">[{idx + 1}]</td>
                        <td className="py-2.5 px-3 text-slate-700 font-medium max-w-[180px] truncate">
                          {ref.title}
                        </td>
                        <td className="py-2.5 px-3 text-slate-500 max-w-[120px] truncate">
                          {ref.authors}
                        </td>
                        <td className="py-2.5 px-3 text-slate-500 max-w-[120px] truncate">
                          {ref.journal}
                        </td>
                        <td className="py-2.5 px-3 text-slate-500">{ref.year}</td>
                        <td className="py-2.5 px-3 text-slate-400 text-xs">
                          {[ref.volume, ref.issue, ref.pages].filter(Boolean).join('/') || '-'}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}
        </div>

        {/* --- right column: timeline + reviews + actions --- */}
        <div className="space-y-6">
          {/* status timeline */}
          <div className="bg-white rounded-xl border border-slate-200 p-6">
            <h2 className="text-base font-semibold text-slate-800 mb-4 flex items-center gap-2">
              <Send className="w-4 h-4 text-slate-400" />
              审稿进度
            </h2>
            <Timeline items={timelineItems} />
          </div>

          {/* action buttons */}
          <div className="bg-white rounded-xl border border-slate-200 p-6">
            <h2 className="text-base font-semibold text-slate-800 mb-4">操作</h2>
            <div className="space-y-2.5">
              {paper.status === 'reviewing' && (
                <button
                  onClick={() => setConfirmOpen(true)}
                  disabled={accepting}
                  className="w-full inline-flex items-center justify-center gap-2 px-4 py-2.5 bg-emerald-600 text-white text-sm font-medium rounded-lg hover:bg-emerald-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  <CheckCircle2 className="w-4 h-4" />
                  {accepting ? '处理中...' : '标记为已收录'}
                </button>
              )}

              {!paper.assignedReviewerId && (
                <button
                  onClick={() => toast.info('指派审稿人功能开发中')}
                  className="w-full inline-flex items-center justify-center gap-2 px-4 py-2.5 border border-amber-200 bg-amber-50 text-amber-700 text-sm font-medium rounded-lg hover:bg-amber-100 transition-colors"
                >
                  <UserPlus className="w-4 h-4" />
                  指派审稿人
                </button>
              )}
            </div>
          </div>

          {/* review history */}
          <div className="bg-white rounded-xl border border-slate-200 p-6">
            <h2 className="text-base font-semibold text-slate-800 mb-4 flex items-center gap-2">
              <Eye className="w-4 h-4 text-slate-400" />
              审稿记录
              {reviews.length > 0 && (
                <span className="text-xs font-normal text-slate-400 ml-1">
                  ({reviews.length})
                </span>
              )}
            </h2>

            {reviews.length === 0 ? (
              <div className="py-6 text-center">
                <p className="text-sm text-slate-400">暂无审稿记录</p>
              </div>
            ) : (
              <div className="space-y-3">
                {reviews.map((review) => (
                  <div
                    key={review.id}
                    className="border border-slate-100 rounded-lg p-3.5 bg-slate-50/50"
                  >
                    <div className="flex items-center justify-between mb-2">
                      <span className="text-sm font-medium text-slate-700">
                        {review.reviewerName}
                      </span>
                      <span
                        className={cn(
                          'px-2 py-0.5 rounded-full text-xs font-medium border',
                          review.decision === 'approved'
                            ? 'bg-emerald-50 text-emerald-700 border-emerald-200'
                            : 'bg-red-50 text-red-700 border-red-200',
                        )}
                      >
                        {review.decision === 'approved' ? '同意录用' : '退回修改'}
                      </span>
                    </div>
                    {review.comments && (
                      <p className="text-sm text-slate-500 leading-relaxed mb-2">
                        {review.comments}
                      </p>
                    )}
                    <p className="text-xs text-slate-400">
                      {formatDateTime(review.reviewDate)}
                    </p>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>

      {/* --- confirm dialog --- */}
      <ConfirmDialog
        open={confirmOpen}
        onClose={() => setConfirmOpen(false)}
        onConfirm={handleAccept}
        title="确认收录"
        message={`确定要将论文「${paper.title}」标记为已收录吗？收录后该论文将进入发表流程。`}
        confirmText="确认收录"
        cancelText="取消"
        variant="info"
      />
    </div>
  );
}
