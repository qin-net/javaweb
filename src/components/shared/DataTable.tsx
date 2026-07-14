import type { ReactNode } from 'react';

// --- helpers ---
function cn(...classes: (string | boolean | undefined | null)[]): string {
  return classes.filter(Boolean).join(' ');
}

// --- types ---
export interface Column<T> {
  key: string;
  title: string;
  render?: (item: T) => ReactNode;
  className?: string;
  sortable?: boolean;
}

interface DataTableProps<T> {
  columns: Column<T>[];
  data: T[];
  keyField?: string;
  onRowClick?: (item: T) => void;
  emptyMessage?: string;
  loading?: boolean;
}

// --- component ---
export default function DataTable<T>({
  columns,
  data,
  keyField = 'id',
  onRowClick,
  emptyMessage,
  loading,
}: DataTableProps<T>) {
  /* ---- loading skeleton ---- */
  if (loading) {
    return (
      <div className="bg-white rounded-xl border border-slate-200 overflow-hidden">
        <div className="p-8 space-y-4">
          {[1, 2, 3, 4, 5].map((i) => (
            <div
              key={i}
              className="h-12 bg-slate-100 rounded-lg animate-pulse"
            />
          ))}
        </div>
      </div>
    );
  }

  /* ---- empty state ---- */
  if (data.length === 0) {
    return (
      <div className="bg-white rounded-xl border border-slate-200 p-12 text-center">
        <svg
          className="mx-auto h-12 w-12 text-slate-300"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
          strokeWidth={1.5}
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4"
          />
        </svg>
        <p className="mt-3 text-slate-400 text-sm">
          {emptyMessage || '暂无数据'}
        </p>
      </div>
    );
  }

  /* ---- data table ---- */
  return (
    <div className="bg-white rounded-xl border border-slate-200 overflow-hidden">
      <div className="overflow-x-auto">
        <table className="w-full">
          <thead>
            <tr className="border-b border-slate-200 bg-slate-50/50">
              {columns.map((col) => (
                <th
                  key={col.key}
                  className={cn(
                    'px-4 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider select-none',
                    col.sortable &&
                      'cursor-pointer hover:text-[#1e3a5f] transition-colors',
                    col.className,
                  )}
                >
                  <span className="inline-flex items-center gap-1">
                    {col.title}
                    {col.sortable && (
                      <svg
                        className="w-3 h-3 text-slate-300"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                        strokeWidth={2}
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          d="M7 16V4m0 0L3 8m4-4l4 4m6 0v12m0 0l4-4m-4 4l-4-4"
                        />
                      </svg>
                    )}
                  </span>
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {data.map((item, idx) => (
              <tr
                key={String((item as any)[keyField] ?? idx)}
                onClick={() => onRowClick?.(item)}
                className={cn(
                  'transition-colors',
                  onRowClick && 'cursor-pointer hover:bg-blue-50/50',
                  idx % 2 === 0 ? 'bg-white' : 'bg-slate-50/30',
                )}
              >
                {columns.map((col) => (
                  <td
                    key={col.key}
                    className={cn(
                      'px-4 py-3.5 text-sm text-slate-700',
                      col.className,
                    )}
                  >
                    {col.render
                      ? col.render(item)
                      : ((item as any)[col.key] as ReactNode)}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* footer with row count */}
      <div className="px-4 py-2.5 border-t border-slate-100 bg-slate-50/30 text-xs text-slate-400">
        共 {data.length} 条记录
      </div>
    </div>
  );
}
