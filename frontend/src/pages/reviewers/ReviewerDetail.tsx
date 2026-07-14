import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getReviewer, getReviewerReviews } from '@/services/api';
import type { Reviewer, Review } from '@/types';
import PageHeader from '@/components/shared/PageHeader';
import DataTable, { type Column } from '@/components/shared/DataTable';
import StatusBadge from '@/components/shared/StatusBadge';
import EmptyState from '@/components/shared/EmptyState';
import LoadingSkeleton from '@/components/shared/LoadingSkeleton';
import { formatDate, cn } from '@/lib/utils';
import {
  ArrowLeft,
  Mail,
  Building2,
  GraduationCap,
  Phone,
  FileText,
  UserCheck,
  Star,
  MessageSquare,
} from 'lucide-react';

export default function ReviewerDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [reviewer, setReviewer] = useState<Reviewer | null>(null);
  const [reviews, setReviews] = useState<Review[]>([]);
  const [loadingReviewer, setLoadingReviewer] = useState(true);
  const [loadingReviews, setLoadingReviews] = useState(true);
  const [reviewerError, setReviewerError] = useState<string | null>(null);
  const [reviewsError, setReviewsError] = useState<string | null>(null);

  useEffect(() => {
    if (id) {
      loadReviewer(id);
      loadReviews(id);
    }
  }, [id]);

  const loadReviewer = async (reviewerId: string) => {
    setLoadingReviewer(true);
    setReviewerError(null);
    try {
      const data = await getReviewer(reviewerId);
      setReviewer(data);
    } catch (err) {
      setReviewerError('加载审稿人信息失败，请稍后重试。');
      console.error('Failed to load reviewer:', err);
    } finally {
      setLoadingReviewer(false);
    }
  };

  const loadReviews = async (reviewerId: string) => {
    setLoadingReviews(true);
    setReviewsError(null);
    try {
      const data = await getReviewerReviews(reviewerId);
      setReviews(data);
    } catch (err) {
      setReviewsError('加载审稿记录失败，请稍后重试。');
      console.error('Failed to load reviews:', err);
    } finally {
      setLoadingReviews(false);
    }
  };

  const getDecisionBadgeClass = (decision: string | undefined) => {
    switch (decision?.toLowerCase()) {
      case 'approved':
      case 'approve':
      case 'accepted':
      case 'accept':
        return 'bg-green-100 text-green-700 dark:bg-green-900/40 dark:text-green-300';
      case 'rejected':
      case 'reject':
      case 'declined':
      case 'decline':
        return 'bg-red-100 text-red-700 dark:bg-red-900/40 dark:text-red-300';
      case 'revision':
      case 'revisions':
        return 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/40 dark:text-yellow-300';
      default:
        return 'bg-gray-100 text-gray-600 dark:bg-gray-700 dark:text-gray-300';
    }
  };

  const getDecisionLabel = (decision: string | undefined) => {
    switch (decision?.toLowerCase()) {
      case 'approved':
      case 'approve':
      case 'accepted':
      case 'accept':
        return '已通过';
      case 'rejected':
      case 'reject':
      case 'declined':
      case 'decline':
        return '已拒绝';
      case 'revision':
      case 'revisions':
        return '需修改';
      default:
        return decision || '待定';
    }
  };

  const reviewColumns: Column<Review>[] = [
    {
      key: 'paperTitle',
      title: '论文标题',
      render: (item) => (
        <span
          className="font-medium text-gray-900 dark:text-gray-100 line-clamp-1 max-w-[320px] block"
          title={(item as any).paperTitle || ''}
        >
          {(item as any).paperTitle || item.paperId || '-'}
        </span>
      ),
    },
    {
      key: 'decision',
      title: '审稿决定',
      render: (item) => (
        <span
          className={cn(
            'inline-flex items-center px-2.5 py-0.5 text-xs font-semibold rounded-full',
            getDecisionBadgeClass((item as any).decision)
          )}
        >
          {getDecisionLabel((item as any).decision)}
        </span>
      ),
    },
    {
      key: 'comment',
      title: '审稿意见',
      render: (item) => (
        <span
          className="text-sm text-gray-600 dark:text-gray-400 line-clamp-1 max-w-[240px] block"
          title={(item as any).comment || item.comments || ''}
        >
          {(item as any).comment || item.comments || '暂无意见'}
        </span>
      ),
    },
    {
      key: 'reviewDate',
      title: '审稿日期',
      render: (item) => (
        <span className="text-sm text-gray-500 dark:text-gray-400">
          {(item as any).reviewDate
            ? formatDate((item as any).reviewDate)
            : item.reviewDate
              ? formatDate(item.reviewDate)
              : '-'}
        </span>
      ),
    },
  ];

  // --- Error state for reviewer ---
  if (!loadingReviewer && reviewerError && !reviewer) {
    return (
      <div className="space-y-6">
        <PageHeader
          title="审稿人详情"
          subtitle="查看审稿人详细信息"
          actions={
            <button
              onClick={() => navigate('/reviewers')}
              className="inline-flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 bg-white dark:bg-gray-800 border border-gray-300 dark:border-gray-600 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
            >
              <ArrowLeft className="w-4 h-4" />
              返回列表
            </button>
          }
        />
        <div className="min-h-[300px] flex flex-col items-center justify-center gap-4">
          <div className="text-red-500 dark:text-red-400 text-lg font-medium">{reviewerError}</div>
          <button
            onClick={() => id && loadReviewer(id)}
            className="px-4 py-2 bg-emerald-600 text-white rounded-lg hover:bg-emerald-700 transition-colors"
          >
            重新加载
          </button>
        </div>
      </div>
    );
  }

  // --- Not found ---
  if (!loadingReviewer && !reviewer && !reviewerError) {
    return (
      <div className="space-y-6">
        <PageHeader
          title="审稿人详情"
          subtitle="查看审稿人详细信息"
          actions={
            <button
              onClick={() => navigate('/reviewers')}
              className="inline-flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 bg-white dark:bg-gray-800 border border-gray-300 dark:border-gray-600 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
            >
              <ArrowLeft className="w-4 h-4" />
              返回列表
            </button>
          }
        />
        <EmptyState
          icon={UserCheck}
          title="审稿人未找到"
          description="该审稿人不存在或已被删除"
        />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <PageHeader
        title="审稿人详情"
        subtitle="查看审稿人详细信息"
        actions={
          <button
            onClick={() => navigate('/reviewers')}
            className="inline-flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 bg-white dark:bg-gray-800 border border-gray-300 dark:border-gray-600 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
          >
            <ArrowLeft className="w-4 h-4" />
            返回列表
          </button>
        }
      />

      {/* Reviewer Info Card */}
      <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
        <div className="px-6 py-5 border-b border-gray-200 dark:border-gray-700">
          <h2 className="text-lg font-semibold text-gray-900 dark:text-gray-100">基本信息</h2>
        </div>

        {loadingReviewer ? (
          <div className="p-6">
            <LoadingSkeleton rows={5} />
          </div>
        ) : reviewer ? (
          <div className="p-6">
            <div className="flex items-start gap-5 mb-6">
              <div className="w-16 h-16 rounded-full bg-emerald-100 dark:bg-emerald-900/40 flex items-center justify-center flex-shrink-0">
                <UserCheck className="w-8 h-8 text-emerald-600 dark:text-emerald-400" />
              </div>
              <div>
                <h3 className="text-2xl font-bold text-gray-900 dark:text-gray-100">
                  {reviewer.name}
                </h3>
                <div className="flex items-center gap-3 mt-1">
                  {reviewer.specialty && (
                    <span className="inline-flex items-center gap-1 text-sm text-emerald-600 dark:text-emerald-400">
                      <Star className="w-3.5 h-3.5" />
                      {reviewer.specialty}
                    </span>
                  )}
                  {reviewer.department && (
                    <span className="text-sm text-gray-500 dark:text-gray-400">
                      {reviewer.department}
                    </span>
                  )}
                </div>
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {reviewer.email && (
                <div className="flex items-center gap-3 p-3 rounded-lg bg-gray-50 dark:bg-gray-900/50">
                  <Mail className="w-5 h-5 text-gray-400 dark:text-gray-500 flex-shrink-0" />
                  <div>
                    <div className="text-xs text-gray-500 dark:text-gray-400">邮箱</div>
                    <div className="text-sm font-medium text-gray-900 dark:text-gray-100">
                      {reviewer.email}
                    </div>
                  </div>
                </div>
              )}

              {reviewer.institution && (
                <div className="flex items-center gap-3 p-3 rounded-lg bg-gray-50 dark:bg-gray-900/50">
                  <Building2 className="w-5 h-5 text-gray-400 dark:text-gray-500 flex-shrink-0" />
                  <div>
                    <div className="text-xs text-gray-500 dark:text-gray-400">所属机构</div>
                    <div className="text-sm font-medium text-gray-900 dark:text-gray-100">
                      {reviewer.institution}
                    </div>
                  </div>
                </div>
              )}

              {reviewer.department && (
                <div className="flex items-center gap-3 p-3 rounded-lg bg-gray-50 dark:bg-gray-900/50">
                  <GraduationCap className="w-5 h-5 text-gray-400 dark:text-gray-500 flex-shrink-0" />
                  <div>
                    <div className="text-xs text-gray-500 dark:text-gray-400">部门/院系</div>
                    <div className="text-sm font-medium text-gray-900 dark:text-gray-100">
                      {reviewer.department}
                    </div>
                  </div>
                </div>
              )}

              {reviewer.phone && (
                <div className="flex items-center gap-3 p-3 rounded-lg bg-gray-50 dark:bg-gray-900/50">
                  <Phone className="w-5 h-5 text-gray-400 dark:text-gray-500 flex-shrink-0" />
                  <div>
                    <div className="text-xs text-gray-500 dark:text-gray-400">电话</div>
                    <div className="text-sm font-medium text-gray-900 dark:text-gray-100">
                      {reviewer.phone}
                    </div>
                  </div>
                </div>
              )}

              {reviewer.reviewIds?.length !== undefined && (
                <div className="flex items-center gap-3 p-3 rounded-lg bg-gray-50 dark:bg-gray-900/50">
                  <MessageSquare className="w-5 h-5 text-gray-400 dark:text-gray-500 flex-shrink-0" />
                  <div>
                    <div className="text-xs text-gray-500 dark:text-gray-400">已审稿件</div>
                    <div className="text-sm font-medium text-gray-900 dark:text-gray-100">
                      {reviewer.reviewIds?.length} 篇
                    </div>
                  </div>
                </div>
              )}
            </div>
          </div>
        ) : null}
      </div>

      {/* Reviewer Reviews */}
      <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
        <div className="px-6 py-5 border-b border-gray-200 dark:border-gray-700 flex items-center gap-2">
          <FileText className="w-5 h-5 text-gray-400" />
          <h2 className="text-lg font-semibold text-gray-900 dark:text-gray-100">
            审稿记录
          </h2>
          {!loadingReviews && (
            <span className="text-sm text-gray-500 dark:text-gray-400">
              (共 {reviews.length} 条)
            </span>
          )}
        </div>

        {loadingReviews ? (
          <div className="p-6">
            <LoadingSkeleton rows={5} />
          </div>
        ) : reviewsError ? (
          <div className="p-12 text-center">
            <div className="text-red-500 dark:text-red-400 mb-3">{reviewsError}</div>
            <button
              onClick={() => id && loadReviews(id)}
              className="px-4 py-2 bg-emerald-600 text-white rounded-lg hover:bg-emerald-700 transition-colors text-sm"
            >
              重新加载
            </button>
          </div>
        ) : reviews.length === 0 ? (
          <div className="p-12">
            <EmptyState
              icon={FileText}
              title="暂无审稿记录"
              description="该审稿人还没有审稿记录"
            />
          </div>
        ) : (
          <DataTable
            columns={reviewColumns}
            data={reviews}
            emptyMessage="暂无审稿记录"
          />
        )}
      </div>
    </div>
  );
}
