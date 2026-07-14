import { useEffect, useState, useMemo } from 'react';
import { UserPlus, Link, Search, CheckCircle2, AlertCircle, RefreshCw, X, FileSearch } from 'lucide-react';
import { getPapers, getReviewers, assignReviewer, getAssignments } from '@/services/api';
import type { Paper, Reviewer } from '@/types';
import PageHeader from '@/components/shared/PageHeader';
import ConfirmDialog from '@/components/shared/ConfirmDialog';
import StatusBadge from '@/components/shared/StatusBadge';
import LoadingSkeleton from '@/components/shared/LoadingSkeleton';
import EmptyState from '@/components/shared/EmptyState';
import { formatDate, cn } from '@/lib/utils';
import { toast } from 'sonner';

interface Assignment {
  id: string;
  paperId: string;
  paperTitle: string;
  reviewerId: string;
  reviewerName: string;
  assignedAt: string;
  status?: string;
}

interface ConfirmState {
  open: boolean;
  paper: Paper | null;
  reviewer: Reviewer | null;
}

export default function AssignmentPage() {
  const [papers, setPapers] = useState<Paper[]>([]);
  const [reviewers, setReviewers] = useState<Reviewer[]>([]);
  const [assignments, setAssignments] = useState<Assignment[]>([]);

  const [loadingPapers, setLoadingPapers] = useState(true);
  const [loadingReviewers, setLoadingReviewers] = useState(true);
  const [loadingAssignments, setLoadingAssignments] = useState(true);
  const [papersError, setPapersError] = useState<string | null>(null);
  const [reviewersError, setReviewersError] = useState<string | null>(null);
  const [assignmentsError, setAssignmentsError] = useState<string | null>(null);

  const [selectedPaperId, setSelectedPaperId] = useState<string | null>(null);
  const [selectedReviewerId, setSelectedReviewerId] = useState<string | null>(null);

  const [paperSearch, setPaperSearch] = useState('');
  const [reviewerSearch, setReviewerSearch] = useState('');

  const [assigning, setAssigning] = useState(false);

  const [confirm, setConfirm] = useState<ConfirmState>({
    open: false,
    paper: null,
    reviewer: null,
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    await Promise.allSettled([
      loadPapers(),
      loadReviewers(),
      loadAssignments(),
    ]);
  };

  const loadPapers = async () => {
    setLoadingPapers(true);
    setPapersError(null);
    try {
      const data = await getPapers({ pageSize: 100 });
      setPapers(data.items);
    } catch (err) {
      setPapersError('加载论文列表失败');
      console.error('Failed to load papers:', err);
    } finally {
      setLoadingPapers(false);
    }
  };

  const loadReviewers = async () => {
    setLoadingReviewers(true);
    setReviewersError(null);
    try {
      const data = await getReviewers();
      setReviewers(data);
    } catch (err) {
      setReviewersError('加载审稿人列表失败');
      console.error('Failed to load reviewers:', err);
    } finally {
      setLoadingReviewers(false);
    }
  };

  const loadAssignments = async () => {
    setLoadingAssignments(true);
    setAssignmentsError(null);
    try {
      const data = await getAssignments();
      const mapped: Assignment[] = data.map((p: Paper) => ({
        id: p.id,
        paperId: p.id,
        paperTitle: p.title,
        reviewerId: p.assignedReviewerId || '',
        reviewerName: p.assignedReviewerName || '',
        assignedAt: p.submissionDate,
        status: p.status,
      }));
      setAssignments(mapped);
    } catch (err) {
      setAssignmentsError('加载指派记录失败');
      console.error('Failed to load assignments:', err);
    } finally {
      setLoadingAssignments(false);
    }
  };

  // Papers that need assignment: status is 'submitted' and no assigned reviewer
  const unassignedPapers = useMemo(() => {
    const assignedPaperIds = new Set(assignments.map((a) => a.paperId));
    return papers.filter(
      (paper) =>
        paper.status === 'submitted' &&
        !paper.assignedReviewerId &&
        !assignedPaperIds.has(paper.id)
    );
  }, [papers, assignments]);

  // Filtered unassigned papers
  const filteredUnassignedPapers = useMemo(() => {
    if (!paperSearch.trim()) return unassignedPapers;
    const term = paperSearch.toLowerCase();
    return unassignedPapers.filter(
      (p) =>
        p.title.toLowerCase().includes(term) ||
        (p.journalName && p.journalName.toLowerCase().includes(term))
    );
  }, [unassignedPapers, paperSearch]);

  // Filtered reviewers
  const filteredReviewers = useMemo(() => {
    if (!reviewerSearch.trim()) return reviewers;
    const term = reviewerSearch.toLowerCase();
    return reviewers.filter(
      (r) =>
        r.name.toLowerCase().includes(term) ||
        (r.email && r.email.toLowerCase().includes(term)) ||
        (r.specialty && r.specialty.toLowerCase().includes(term)) ||
        (r.institution && r.institution.toLowerCase().includes(term))
    );
  }, [reviewers, reviewerSearch]);

  const selectedPaper = useMemo(() => {
    if (!selectedPaperId) return null;
    return papers.find((p) => p.id === selectedPaperId) || null;
  }, [selectedPaperId, papers]);

  const selectedReviewer = useMemo(() => {
    if (!selectedReviewerId) return null;
    return reviewers.find((r) => r.id === selectedReviewerId) || null;
  }, [selectedReviewerId, reviewers]);

  const handleSelectPaper = (paperId: string) => {
    setSelectedPaperId((prev) => (prev === paperId ? null : paperId));
  };

  const handleSelectReviewer = (reviewerId: string) => {
    setSelectedReviewerId((prev) => (prev === reviewerId ? null : reviewerId));
  };

  const handleAssignClick = () => {
    if (!selectedPaper || !selectedReviewer) {
      toast.warning('请先选择一篇论文和一位审稿人');
      return;
    }
    setConfirm({ open: true, paper: selectedPaper, reviewer: selectedReviewer });
  };

  const handleConfirmAssign = async () => {
    if (!confirm.paper || !confirm.reviewer) return;

    setAssigning(true);
    try {
      await assignReviewer(confirm.paper.id, confirm.reviewer.id, confirm.reviewer.name);
      toast.success(
        `已成功将审稿人「${confirm.reviewer.name}」指派给论文「${confirm.paper.title.slice(0, 20)}${confirm.paper.title.length > 20 ? '...' : ''}」`
      );
      setSelectedPaperId(null);
      setSelectedReviewerId(null);
      setConfirm({ open: false, paper: null, reviewer: null });
      await loadAssignments();
    } catch (err) {
      toast.error('指派失败，请稍后重试。');
      console.error('Failed to assign reviewer:', err);
    } finally {
      setAssigning(false);
    }
  };

  const isLoading = loadingPapers || loadingReviewers || loadingAssignments;
  const hasError = papersError || reviewersError || assignmentsError;

  return (
    <div className="space-y-6">
      <PageHeader
        title="审稿指派"
        subtitle="将待审核的论文指派给合适的审稿人进行评审"
        actions={
          <button
            onClick={loadData}
            className="inline-flex items-center gap-2 px-4 py-2 text-sm font-medium text-slate-700 bg-white border border-slate-200 rounded-lg hover:bg-slate-50 transition-colors"
            disabled={isLoading}
          >
            <RefreshCw className={cn('w-4 h-4', isLoading && 'animate-spin')} />
            刷新数据
          </button>
        }
      />

      {/* Error banner */}
      {hasError && (
        <div className="flex items-center gap-3 p-4 rounded-lg bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 text-red-700 dark:text-red-400">
          <AlertCircle className="w-5 h-5 flex-shrink-0" />
          <div className="flex-1 text-sm">
            {papersError && <div>{papersError}</div>}
            {reviewersError && <div>{reviewersError}</div>}
            {assignmentsError && <div>{assignmentsError}</div>}
          </div>
          <button
            onClick={loadData}
            className="px-3 py-1.5 text-xs font-medium bg-red-100 dark:bg-red-800/30 rounded-md hover:bg-red-200 dark:hover:bg-red-800/50 transition-colors"
          >
            重试
          </button>
        </div>
      )}

      {/* Two-column layout */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Left: Papers needing review */}
        <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden flex flex-col">
          <div className="px-5 py-4 border-b border-gray-200 dark:border-gray-700">
            <h2 className="text-base font-semibold text-gray-900 dark:text-gray-100 flex items-center gap-2">
              <Link className="w-4 h-4 text-blue-500" />
              待指派论文
            </h2>
            <p className="text-xs text-gray-500 dark:text-gray-400 mt-0.5">
              选择一篇需要指派审稿人的论文
            </p>
          </div>

          {/* Search */}
          <div className="px-4 py-3 border-b border-gray-100 dark:border-gray-700/50">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
              <input
                type="text"
                value={paperSearch}
                onChange={(e) => setPaperSearch(e.target.value)}
                placeholder="搜索论文标题或期刊..."
                className="w-full pl-9 pr-3 py-2 text-sm border border-gray-200 dark:border-gray-600 rounded-lg bg-gray-50 dark:bg-gray-900 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent dark:text-gray-200 placeholder-gray-400 dark:placeholder-gray-500 transition-all"
              />
              {paperSearch && (
                <button
                  onClick={() => setPaperSearch('')}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
                >
                  <X className="w-3.5 h-3.5" />
                </button>
              )}
            </div>
          </div>

          {/* Paper list */}
          <div className="flex-1 overflow-y-auto max-h-[420px]">
            {loadingPapers ? (
              <div className="p-4">
                <LoadingSkeleton rows={5} />
              </div>
            ) : filteredUnassignedPapers.length === 0 ? (
              <div className="p-8">
                <EmptyState
                  icon={FileSearch}
                  title={paperSearch ? '未找到匹配的论文' : '暂无需指派的论文'}
                  description={
                    paperSearch
                      ? '请尝试调整搜索关键词'
                      : '所有已提交的论文都已指派审稿人'
                  }
                />
              </div>
            ) : (
              <ul className="divide-y divide-gray-100 dark:divide-gray-700/50">
                {filteredUnassignedPapers.map((paper) => (
                  <li
                    key={paper.id}
                    onClick={() => handleSelectPaper(paper.id)}
                    className={cn(
                      'px-4 py-3 cursor-pointer transition-colors hover:bg-gray-50 dark:hover:bg-gray-750',
                      selectedPaperId === paper.id
                        ? 'bg-blue-50 dark:bg-blue-900/20 border-l-2 border-l-blue-500'
                        : 'border-l-2 border-l-transparent'
                    )}
                  >
                    <div className="flex items-start justify-between gap-2">
                      <div className="min-w-0 flex-1">
                        <p className="text-sm font-medium text-gray-900 dark:text-gray-100 line-clamp-2">
                          {paper.title}
                        </p>
                        <div className="flex items-center gap-2 mt-1.5">
                          {paper.journalName && (
                            <span className="text-xs px-1.5 py-0.5 rounded bg-indigo-50 text-indigo-600">
                              {paper.journalName}
                            </span>
                          )}
                          {paper.submissionDate && (
                            <span className="text-xs text-gray-400">
                              {formatDate(paper.submissionDate)}
                            </span>
                          )}
                        </div>
                      </div>
                      {selectedPaperId === paper.id && (
                        <CheckCircle2 className="w-5 h-5 text-blue-500 flex-shrink-0 mt-0.5" />
                      )}
                    </div>
                  </li>
                ))}
              </ul>
            )}
          </div>

          {/* Paper count */}
          <div className="px-4 py-2 border-t border-gray-100 dark:border-gray-700/50 bg-gray-50 dark:bg-gray-800/50 text-xs text-gray-500 dark:text-gray-400">
            共 {unassignedPapers.length} 篇论文待指派
            {paperSearch && filteredUnassignedPapers.length !== unassignedPapers.length && (
              <span> (筛选后 {filteredUnassignedPapers.length} 篇)</span>
            )}
          </div>
        </div>

        {/* Right: Available reviewers */}
        <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden flex flex-col">
          <div className="px-5 py-4 border-b border-gray-200 dark:border-gray-700">
            <h2 className="text-base font-semibold text-gray-900 dark:text-gray-100 flex items-center gap-2">
              <UserPlus className="w-4 h-4 text-emerald-500" />
              可选审稿人
            </h2>
            <p className="text-xs text-gray-500 dark:text-gray-400 mt-0.5">
              选择一位审稿人来审阅选中的论文
            </p>
          </div>

          {/* Search */}
          <div className="px-4 py-3 border-b border-gray-100 dark:border-gray-700/50">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
              <input
                type="text"
                value={reviewerSearch}
                onChange={(e) => setReviewerSearch(e.target.value)}
                placeholder="搜索审稿人姓名、邮箱、专业..."
                className="w-full pl-9 pr-3 py-2 text-sm border border-gray-200 dark:border-gray-600 rounded-lg bg-gray-50 dark:bg-gray-900 focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent dark:text-gray-200 placeholder-gray-400 dark:placeholder-gray-500 transition-all"
              />
              {reviewerSearch && (
                <button
                  onClick={() => setReviewerSearch('')}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
                >
                  <X className="w-3.5 h-3.5" />
                </button>
              )}
            </div>
          </div>

          {/* Reviewer list */}
          <div className="flex-1 overflow-y-auto max-h-[420px]">
            {loadingReviewers ? (
              <div className="p-4">
                <LoadingSkeleton rows={5} />
              </div>
            ) : filteredReviewers.length === 0 ? (
              <div className="p-8">
                <EmptyState
                  icon={UserPlus}
                  title={reviewerSearch ? '未找到匹配的审稿人' : '暂无审稿人'}
                  description={
                    reviewerSearch
                      ? '请尝试调整搜索关键词'
                      : '系统中还没有审稿人数据'
                  }
                />
              </div>
            ) : (
              <ul className="divide-y divide-gray-100 dark:divide-gray-700/50">
                {filteredReviewers.map((reviewer) => (
                  <li
                    key={reviewer.id}
                    onClick={() => handleSelectReviewer(reviewer.id)}
                    className={cn(
                      'px-4 py-3 cursor-pointer transition-colors hover:bg-gray-50 dark:hover:bg-gray-750',
                      selectedReviewerId === reviewer.id
                        ? 'bg-emerald-50 dark:bg-emerald-900/20 border-l-2 border-l-emerald-500'
                        : 'border-l-2 border-l-transparent'
                    )}
                  >
                    <div className="flex items-start justify-between gap-2">
                      <div className="min-w-0 flex-1">
                        <p className="text-sm font-medium text-gray-900 dark:text-gray-100">
                          {reviewer.name}
                        </p>
                        <div className="flex flex-wrap items-center gap-2 mt-1">
                          {reviewer.specialty && (
                            <span className="text-xs px-1.5 py-0.5 rounded bg-emerald-50 text-emerald-600 dark:bg-emerald-900/30 dark:text-emerald-300">
                              {reviewer.specialty}
                            </span>
                          )}
                          {reviewer.institution && (
                            <span className="text-xs text-gray-400 dark:text-gray-500 line-clamp-1">
                              {reviewer.institution}
                            </span>
                          )}
                        </div>
                        <p className="text-xs text-gray-400 dark:text-gray-500 mt-0.5">
                          {reviewer.email}
                        </p>
                      </div>
                      {selectedReviewerId === reviewer.id && (
                        <CheckCircle2 className="w-5 h-5 text-emerald-500 flex-shrink-0 mt-0.5" />
                      )}
                    </div>
                  </li>
                ))}
              </ul>
            )}
          </div>

          {/* Reviewer count */}
          <div className="px-4 py-2 border-t border-gray-100 dark:border-gray-700/50 bg-gray-50 dark:bg-gray-800/50 text-xs text-gray-500 dark:text-gray-400">
            共 {reviewers.length} 位审稿人
            {reviewerSearch && filteredReviewers.length !== reviewers.length && (
              <span> (筛选后 {filteredReviewers.length} 位)</span>
            )}
          </div>
        </div>
      </div>

      {/* Assign button area */}
      {(selectedPaper || selectedReviewer) && (
        <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-4">
          <div className="flex flex-col sm:flex-row items-start sm:items-center gap-4">
            <div className="flex-1 flex flex-wrap items-center gap-3 text-sm">
              <span className="text-gray-500 dark:text-gray-400">当前选择：</span>
              {selectedPaper ? (
                <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-blue-50 dark:bg-blue-900/30 text-blue-700 dark:text-blue-300 text-xs font-medium">
                  <Link className="w-3.5 h-3.5" />
                  {selectedPaper.title.slice(0, 30)}
                  {selectedPaper.title.length > 30 ? '...' : ''}
                  <button
                    onClick={() => setSelectedPaperId(null)}
                    className="ml-1 text-blue-400 hover:text-blue-600 dark:hover:text-blue-200"
                  >
                    <X className="w-3 h-3" />
                  </button>
                </span>
              ) : (
                <span className="text-gray-400 dark:text-gray-500 text-xs">未选择论文</span>
              )}

              <span className="text-gray-300 dark:text-gray-600 text-lg leading-none">+</span>

              {selectedReviewer ? (
                <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-emerald-50 dark:bg-emerald-900/30 text-emerald-700 dark:text-emerald-300 text-xs font-medium">
                  <UserPlus className="w-3.5 h-3.5" />
                  {selectedReviewer.name}
                  <button
                    onClick={() => setSelectedReviewerId(null)}
                    className="ml-1 text-emerald-400 hover:text-emerald-600 dark:hover:text-emerald-200"
                  >
                    <X className="w-3 h-3" />
                  </button>
                </span>
              ) : (
                <span className="text-gray-400 dark:text-gray-500 text-xs">未选择审稿人</span>
              )}
            </div>

            <button
              onClick={handleAssignClick}
              disabled={!selectedPaper || !selectedReviewer}
              className={cn(
                'inline-flex items-center gap-2 px-5 py-2.5 text-sm font-semibold rounded-lg transition-all',
                selectedPaper && selectedReviewer
                  ? 'bg-blue-600 text-white hover:bg-blue-700 shadow-sm shadow-blue-200 dark:shadow-blue-900/30 active:scale-[0.98]'
                  : 'bg-gray-200 dark:bg-gray-700 text-gray-400 dark:text-gray-500 cursor-not-allowed'
              )}
            >
              <Link className="w-4 h-4" />
              指派审稿
            </button>
          </div>
        </div>
      )}

      {/* Current assignments table */}
      <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
        <div className="px-6 py-5 border-b border-gray-200 dark:border-gray-700">
          <h2 className="text-lg font-semibold text-gray-900 dark:text-gray-100">
            当前指派记录
          </h2>
          <p className="text-xs text-gray-500 dark:text-gray-400 mt-0.5">
            已完成的审稿人指派列表
          </p>
        </div>

        {loadingAssignments ? (
          <div className="p-6">
            <LoadingSkeleton rows={5} />
          </div>
        ) : assignments.length === 0 ? (
          <div className="p-12">
            <EmptyState
              icon={FileSearch}
              title="暂无指派记录"
              description="还没有任何审稿指派，请从上方选择论文和审稿人进行指派"
            />
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800/50">
                  <th className="text-left px-5 py-3 text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                    论文标题
                  </th>
                  <th className="text-left px-5 py-3 text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                    审稿人
                  </th>
                  <th className="text-left px-5 py-3 text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                    指派时间
                  </th>
                  <th className="text-left px-5 py-3 text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                    状态
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100 dark:divide-gray-700/50">
                {assignments.map((assignment) => (
                  <tr
                    key={assignment.id}
                    className="hover:bg-gray-50 dark:hover:bg-gray-750 transition-colors"
                  >
                    <td className="px-5 py-3.5">
                      <span
                        className="text-sm font-medium text-gray-900 dark:text-gray-100 line-clamp-1 max-w-[300px] block"
                        title={assignment.paperTitle}
                      >
                        {assignment.paperTitle}
                      </span>
                    </td>
                    <td className="px-5 py-3.5">
                      <span className="text-sm text-emerald-600 dark:text-emerald-400 font-medium">
                        {assignment.reviewerName}
                      </span>
                    </td>
                    <td className="px-5 py-3.5">
                      <span className="text-sm text-gray-500 dark:text-gray-400">
                        {assignment.assignedAt
                          ? formatDate(assignment.assignedAt)
                          : '-'}
                      </span>
                    </td>
                    <td className="px-5 py-3.5">
                      <StatusBadge
                        status={assignment.status || 'assigned'}
                      />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {!loadingAssignments && assignments.length > 0 && (
          <div className="px-5 py-3 border-t border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800/50 text-sm text-gray-500 dark:text-gray-400">
            共 {assignments.length} 条指派记录
          </div>
        )}
      </div>

      {/* Confirmation Dialog */}
      <ConfirmDialog
        open={confirm.open}
        onClose={() => setConfirm({ open: false, paper: null, reviewer: null })}
        onConfirm={handleConfirmAssign}
        title="确认指派审稿"
        message={
          confirm.paper && confirm.reviewer
            ? `确认将审稿人「${confirm.reviewer.name}」指派给论文「${confirm.paper.title.slice(0, 40)}${confirm.paper.title.length > 40 ? '...' : ''}」吗？`
            : ''
        }
        confirmText={assigning ? '指派中...' : '确认指派'}
        variant="info"
      />
    </div>
  );
}
