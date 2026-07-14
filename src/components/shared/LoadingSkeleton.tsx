interface LoadingSkeletonProps {
  /** Render a full-screen centered spinner */
  fullScreen?: boolean;
  /** Number of skeleton rows to display (default 5) */
  rows?: number;
}

export default function LoadingSkeleton({
  fullScreen = false,
  rows = 5,
}: LoadingSkeletonProps) {
  /* ---- full-screen spinner ---- */
  if (fullScreen) {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center">
        <div className="text-center">
          <div className="w-12 h-12 border-4 border-blue-200 border-t-[#1e3a5f] rounded-full animate-spin mx-auto mb-4" />
          <p className="text-slate-500 text-sm">加载中...</p>
        </div>
      </div>
    );
  }

  /* ---- inline skeleton rows ---- */
  return (
    <div className="bg-white rounded-xl border border-slate-200 overflow-hidden">
      <div className="p-8 space-y-5">
        {Array.from({ length: rows }).map((_, i) => (
          <div key={i} className="animate-pulse">
            {/* title line */}
            <div className="h-4 bg-slate-200 rounded w-3/4 mb-2.5" />
            {/* subtitle line — shorter */}
            <div className="h-3 bg-slate-100 rounded w-1/2" />
          </div>
        ))}
      </div>
    </div>
  );
}
