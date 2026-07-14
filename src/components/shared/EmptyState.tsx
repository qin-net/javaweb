import { FileSearch } from 'lucide-react';

interface EmptyStateProps {
  icon?: React.ElementType;
  title?: string;
  description?: string;
  action?: React.ReactNode;
}

export default function EmptyState({
  icon: Icon = FileSearch,
  title = '暂无数据',
  description = '当前没有可显示的内容',
  action,
}: EmptyStateProps) {
  return (
    <div className="flex flex-col items-center justify-center py-16 text-center">
      <div className="w-20 h-20 rounded-full bg-slate-100 flex items-center justify-center mb-4">
        <Icon className="w-10 h-10 text-slate-300" />
      </div>
      <h3 className="text-lg font-medium text-slate-600">{title}</h3>
      <p className="text-sm text-slate-400 mt-1 max-w-sm">{description}</p>
      {action && <div className="mt-4">{action}</div>}
    </div>
  );
}
