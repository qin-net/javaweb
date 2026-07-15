import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  ArrowLeft,
  Send,
  FileText,
  BookOpen,
  Tag,
  User,
  Calendar,
  AlertCircle,
  RefreshCw,
  CheckCircle2,
  XCircle,
  Eye,
} from 'lucide-react';
import { getPaper, getPaperReviews, createReview } from '@/services/api';
import type { Paper, Review } from '@/types';
import StatusBadge from '@/components/shared/StatusBadge';
import EmptyState from '@/components/shared/EmptyState';
import LoadingSkeleton from '@/components/shared/LoadingSkeleton';
import { cn, formatDate } from '@/lib/utils';
import { toast } from 'sonner';
import { useAuth } from '@/contexts/AuthContext';

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

export default function PaperReview() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();

  // --- state ---
  const [paper, setPaper] = useState<Paper | null>(null);
  const [reviews, setReviews] = useState<Review[]>([]);
  const [pageState, setPageState] = useState<PageState>('loading');
  const [errorMessage, setErrorMessage] = useState('');

  // Review form state
  const [decision, setDecision] = useState<'approved' | 'rejected'>('approved');
  const [comments, setComments] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [formError, setFormError] = useState('');

  // Whether current reviewer already reviewed this paper
  const currentReviewerId = String(user?.refId ?? '');
  const alreadyReviewed = reviews.some((r) => r.reviewerId === currentReviewerId);
  const existingReview = reviews.find((r) => r.reviewerId === currentReviewerId);
  // Only reviewers can submit review form
  const canReview = user?.roleCode === 'reviewer';

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

  // --- submit review ---
  const handleSubmitReview = async (e: React.FormEvent) => {
    e.preventDefault();
    setFormError('');

    if (!comments.trim()) {
      setFormError('请输入审稿意见');
      return;
    }

    if (!id) return;

    setSubmitting(true);
    try {
      const newReview = await createReview({
        paperId: id,
        reviewerId: currentReviewerId,
        reviewerName: user?.realName ?? '',
        decision,
        comments: comments.trim(),
      });
      setReviews((prev) => [...prev, newReview]);
      setComments('');
      setDecision('approved');
      toast.success('审稿意见已提交');
    } catch (err) {
      const message = err instanceof Error ? err.message : '提交审稿意见失败';
      toast.error(message);
    } finally {
      setSubmitting(false);
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
        {/* --- left column: paper content (read-only) --- */}
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
            <div className="text-sm text-slate-600 leading-relaxed whitespace-pre-wrap max-h-[500px] overflow-y-auto">
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
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-50">
                    {paper.references.map((ref, idx) => (
                      <tr key={ref.id} className="hover:bg-slate-50/50 transition-colors">
                        <td className="py-2.5 px-3 text-slate-400 text-xs">[{idx + 1}]</td>
                        <td className="py-2.5 px-3 text-slate-700 font-medium max-w-[160px] truncate">
                          {ref.title}
                        </td>
                        <td className="py-2.5 px-3 text-slate-500 max-w-[100px] truncate">
                          {ref.authors}
                        </td>
                        <td className="py-2.5 px-3 text-slate-500 max-w-[120px] truncate">
                          {ref.journal}
                        </td>
                        <td className="py-2.5 px-3 text-slate-500">{ref.year}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}
        </div>

        {/* --- right column: review form + history --- */}
        <div className="space-y-6">
          {/* review form - only visible to reviewers */}
          {canReview && (
          <div className="bg-white rounded-xl border border-slate-200 p-6">
            <h2 className="text-base font-semibold text-slate-800 mb-4 flex items-center gap-2">
              <Send className="w-4 h-4 text-slate-400" />
              {alreadyReviewed ? '您的审稿意见' : '提交审稿意见'}
            </h2>

            {alreadyReviewed ? (
              /* --- already reviewed state --- */
              <div>
                <div className="bg-emerald-50 border border-emerald-200 rounded-lg p-4 mb-4">
                  <div className="flex items-center gap-2 mb-2">
                    <CheckCircle2 className="w-4 h-4 text-emerald-500" />
                    <span className="text-sm font-medium text-emerald-700">审稿意见已提交</span>
                  </div>
                  <p className="text-xs text-emerald-600">
                    您已对该论文提交了审稿意见，感谢您的评审工作。
                  </p>
                </div>

                {existingReview && (
                  <div className="space-y-2">
                    <div className="flex items-center gap-2">
                      <span className="text-xs font-medium text-slate-500">评审决定：</span>
                      <span
                        className={cn(
                          'px-2 py-0.5 rounded-full text-xs font-medium border',
                          existingReview.decision === 'approved'
                            ? 'bg-emerald-50 text-emerald-700 border-emerald-200'
                            : 'bg-red-50 text-red-700 border-red-200',
                        )}
                      >
                        {existingReview.decision === 'approved' ? '同意录用' : '退回修改'}
                      </span>
                    </div>
                    <div>
                      <span className="text-xs font-medium text-slate-500">审稿意见：</span>
                      <p className="text-sm text-slate-600 mt-1 leading-relaxed">
                        {existingReview.comments}
                      </p>
                    </div>
                    <div className="text-xs text-slate-400">
                      {formatDateTime(existingReview.reviewDate)}
                    </div>
                  </div>
                )}
              </div>
            ) : (
              /* --- review form --- */
              <form onSubmit={handleSubmitReview} className="space-y-4">
                {/* decision */}
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-3">
                    评审决定
                  </label>
                  <div className="space-y-2">
                    <label
                      className={cn(
                        'flex items-center gap-3 p-3 rounded-lg border cursor-pointer transition-all',
                        decision === 'approved'
                          ? 'border-emerald-300 bg-emerald-50'
                          : 'border-slate-200 bg-white hover:border-slate-300',
                      )}
                    >
                      <input
                        type="radio"
                        name="decision"
                        value="approved"
                        checked={decision === 'approved'}
                        onChange={() => setDecision('approved')}
                        className="w-4 h-4 text-emerald-600 focus:ring-emerald-500"
                      />
                      <div className="flex items-center gap-2">
                        <CheckCircle2 className="w-4 h-4 text-emerald-500" />
                        <span className="text-sm font-medium text-slate-700">同意录用</span>
                      </div>
                    </label>

                    <label
                      className={cn(
                        'flex items-center gap-3 p-3 rounded-lg border cursor-pointer transition-all',
                        decision === 'rejected'
                          ? 'border-red-300 bg-red-50'
                          : 'border-slate-200 bg-white hover:border-slate-300',
                      )}
                    >
                      <input
                        type="radio"
                        name="decision"
                        value="rejected"
                        checked={decision === 'rejected'}
                        onChange={() => setDecision('rejected')}
                        className="w-4 h-4 text-red-600 focus:ring-red-500"
                      />
                      <div className="flex items-center gap-2">
                        <XCircle className="w-4 h-4 text-red-500" />
                        <span className="text-sm font-medium text-slate-700">退回修改</span>
                      </div>
                    </label>
                  </div>
                </div>

                {/* comments */}
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-2">
                    审稿意见 <span className="text-red-400">*</span>
                  </label>
                  <textarea
                    rows={6}
                    value={comments}
                    onChange={(e) => {
                      setComments(e.target.value);
                      if (formError) setFormError('');
                    }}
                    placeholder="请输入您的审稿意见，包括对论文的总体评价、优点、不足之处以及修改建议..."
                    className={cn(
                      'w-full px-3.5 py-2.5 bg-white border rounded-lg text-sm text-slate-700 placeholder:text-slate-400 focus:outline-none focus:ring-2 transition-all resize-y',
                      formError
                        ? 'border-red-300 focus:border-red-400 focus:ring-red-100'
                        : 'border-slate-200 focus:border-blue-400 focus:ring-blue-100',
                    )}
                  />
                  {formError && (
                    <p className="text-xs text-red-500 mt-1.5 flex items-center gap-1">
                      <AlertCircle className="w-3 h-3" />
                      {formError}
                    </p>
                  )}
                </div>

                {/* submit */}
                <button
                  type="submit"
                  disabled={submitting}
                  className="w-full inline-flex items-center justify-center gap-2 px-4 py-2.5 bg-[#1e3a5f] text-white text-sm font-medium rounded-lg hover:bg-[#162d4a] transition-colors disabled:opacity-50 disabled:cursor-not-allowed shadow-sm"
                >
                  {submitting ? (
                    <>
                      <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                      提交中...
                    </>
                  ) : (
                    <>
                      <Send className="w-4 h-4" />
                      提交审稿意见
                    </>
                  )}
                </button>
              </form>
            )}
          </div>
          )}

          {/* review history */}
          <div className="bg-white rounded-xl border border-slate-200 p-6">
            <h2 className="text-base font-semibold text-slate-800 mb-4 flex items-center gap-2">
              <Eye className="w-4 h-4 text-slate-400" />
              全部审稿记录
              {reviews.length > 0 && (
                <span className="text-xs font-normal text-slate-400 ml-1">
                  ({reviews.length})
                </span>
              )}
            </h2>

            {reviews.length === 0 ? (
              <div className="py-8 text-center">
                <p className="text-sm text-slate-400">暂无审稿记录</p>
                <p className="text-xs text-slate-300 mt-1">您将作为第一位审稿人</p>
              </div>
            ) : (
              <div className="space-y-3">
                {reviews.map((review) => (
                  <div
                    key={review.id}
                    className={cn(
                      'border rounded-lg p-3.5',
                      review.reviewerId === currentReviewerId
                        ? 'border-[#1e3a5f]/20 bg-blue-50/30'
                        : 'border-slate-100 bg-slate-50/50',
                    )}
                  >
                    <div className="flex items-center justify-between mb-2">
                      <div className="flex items-center gap-1.5">
                        <span className="text-sm font-medium text-slate-700">
                          {review.reviewerName}
                        </span>
                        {review.reviewerId === currentReviewerId && (
                          <span className="text-[10px] px-1.5 py-0.5 rounded bg-[#1e3a5f]/10 text-[#1e3a5f] font-medium">
                            您
                          </span>
                        )}
                      </div>
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
    </div>
  );
}
