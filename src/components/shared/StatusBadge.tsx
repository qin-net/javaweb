import { cn } from '@/lib/utils';

const statusConfig: Record<string, { label: string; className: string }> = {
  submitted: { label: '已投稿', className: 'bg-blue-50 text-blue-700 border-blue-200' },
  reviewing: { label: '审稿中', className: 'bg-amber-50 text-amber-700 border-amber-200' },
  accepted: { label: '已收录', className: 'bg-emerald-50 text-emerald-700 border-emerald-200' },
  rejected: { label: '已退回', className: 'bg-red-50 text-red-700 border-red-200' },
  published: { label: '已发表', className: 'bg-purple-50 text-purple-700 border-purple-200' },
};

interface StatusBadgeProps {
  status: string;
  size?: 'sm' | 'md';
}

export default function StatusBadge({ status, size = 'md' }: StatusBadgeProps) {
  const config = statusConfig[status] || {
    label: status,
    className: 'bg-slate-50 text-slate-700 border-slate-200',
  };

  return (
    <span
      className={cn(
        'inline-flex items-center border rounded-full font-medium',
        size === 'sm' ? 'px-2 py-0.5 text-xs' : 'px-3 py-1 text-sm',
        config.className,
      )}
    >
      {config.label}
    </span>
  );
}
