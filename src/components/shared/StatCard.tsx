import type { ElementType } from 'react';

// --- inline helper (avoids dependency on @/lib/utils) ---
function cn(...classes: (string | boolean | undefined | null)[]): string {
  return classes.filter(Boolean).join(' ');
}

// --- types ---
interface StatCardProps {
  icon: ElementType;
  label: string;
  value: number | string;
  /** Positive = upward trend, negative = downward trend */
  trend?: number;
  color?: 'blue' | 'amber' | 'emerald' | 'purple';
}

const colorMap: Record<
  NonNullable<StatCardProps['color']>,
  { bg: string; icon: string; trend: string }
> = {
  blue: {
    bg: 'bg-blue-50',
    icon: 'text-[#1e3a5f]',
    trend: 'text-[#1e3a5f]',
  },
  amber: {
    bg: 'bg-amber-50',
    icon: 'text-[#c9a96e]',
    trend: 'text-[#c9a96e]',
  },
  emerald: {
    bg: 'bg-emerald-50',
    icon: 'text-emerald-600',
    trend: 'text-emerald-600',
  },
  purple: {
    bg: 'bg-purple-50',
    icon: 'text-purple-600',
    trend: 'text-purple-600',
  },
};

// --- trend arrow icons (inline SVGs to avoid extra dependency) ---
function TrendingUp({ className }: { className?: string }) {
  return (
    <svg
      className={cn('w-3 h-3', className)}
      fill="none"
      viewBox="0 0 24 24"
      stroke="currentColor"
      strokeWidth={2}
    >
      <path
        strokeLinecap="round"
        strokeLinejoin="round"
        d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"
      />
    </svg>
  );
}

function TrendingDown({ className }: { className?: string }) {
  return (
    <svg
      className={cn('w-3 h-3', className)}
      fill="none"
      viewBox="0 0 24 24"
      stroke="currentColor"
      strokeWidth={2}
    >
      <path
        strokeLinecap="round"
        strokeLinejoin="round"
        d="M13 17h8m0 0v-8m0 8l-8-8-4 4-6-6"
      />
    </svg>
  );
}

// --- component ---
export default function StatCard({
  icon: Icon,
  label,
  value,
  trend,
  color = 'blue',
}: StatCardProps) {
  const c = colorMap[color];

  return (
    <div className="bg-white rounded-xl border border-slate-200 p-6 hover:shadow-md transition-shadow">
      <div className="flex items-start justify-between">
        {/* icon container */}
        <div
          className={cn(
            'w-12 h-12 rounded-lg flex items-center justify-center',
            c.bg,
          )}
        >
          <Icon className={cn('w-6 h-6', c.icon)} />
        </div>

        {/* trend badge */}
        {trend !== undefined && (
          <span
            className={cn(
              'inline-flex items-center text-xs font-semibold gap-0.5 px-2 py-0.5 rounded-full',
              trend >= 0
                ? 'text-emerald-700 bg-emerald-50'
                : 'text-red-700 bg-red-50',
            )}
          >
            {trend >= 0 ? (
              <TrendingUp />
            ) : (
              <TrendingDown />
            )}
            {Math.abs(trend)}%
          </span>
        )}
      </div>

      <div className="mt-4">
        <p className="text-2xl font-bold text-slate-800">
          {typeof value === 'number' ? value.toLocaleString() : value}
        </p>
        <p className="text-sm text-slate-400 mt-1">{label}</p>
      </div>
    </div>
  );
}
