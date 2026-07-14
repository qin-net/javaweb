import type { ElementType, ReactNode } from 'react';

// --- inline helper (avoids dependency on @/lib/utils) ---
function cn(...classes: (string | boolean | undefined | null)[]): string {
  return classes.filter(Boolean).join(' ');
}

// --- types ---
interface TimelineItem {
  id: string;
  title: string;
  description?: string;
  date: string;
  icon?: ElementType;
  status?: 'completed' | 'current' | 'pending' | 'rejected';
  content?: ReactNode;
}

interface TimelineProps {
  items: TimelineItem[];
}

// --- inline icons (avoids external icon library dependency) ---
function CheckIcon({ className }: { className?: string }) {
  return (
    <svg className={className} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
      <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
    </svg>
  );
}

function ClockIcon({ className }: { className?: string }) {
  return (
    <svg className={className} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
      <path strokeLinecap="round" strokeLinejoin="round" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
    </svg>
  );
}

function XIcon({ className }: { className?: string }) {
  return (
    <svg className={className} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
      <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
    </svg>
  );
}

function FileTextIcon({ className }: { className?: string }) {
  return (
    <svg className={className} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
      <path strokeLinecap="round" strokeLinejoin="round" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
    </svg>
  );
}

function SendIcon({ className }: { className?: string }) {
  return (
    <svg className={className} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
      <path strokeLinecap="round" strokeLinejoin="round" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
    </svg>
  );
}

function EyeIcon({ className }: { className?: string }) {
  return (
    <svg className={className} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
      <path strokeLinecap="round" strokeLinejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
      <path strokeLinecap="round" strokeLinejoin="round" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
    </svg>
  );
}

// --- icon lookup ---
const fallbackIcons: Record<NonNullable<TimelineItem['status']>, ElementType> = {
  completed: CheckIcon,
  current: ClockIcon,
  pending: ClockIcon,
  rejected: XIcon,
};

// --- component ---
export default function Timeline({ items }: TimelineProps) {
  return (
    <div className="space-y-0">
      {items.map((item, idx) => {
        const isLast = idx === items.length - 1;

        const statusColor =
          item.status === 'completed'
            ? 'bg-emerald-500 text-white'
            : item.status === 'rejected'
              ? 'bg-red-500 text-white'
              : item.status === 'current'
                ? 'bg-[#1e3a5f] text-white'
                : 'bg-slate-200 text-slate-400';

        const Icon =
          item.icon || (item.status ? fallbackIcons[item.status] : ClockIcon);

        return (
          <div key={item.id} className="flex gap-4">
            {/* left column — dot + line */}
            <div className="flex flex-col items-center">
              <div
                className={cn(
                  'w-8 h-8 rounded-full flex items-center justify-center flex-shrink-0 transition-colors',
                  statusColor,
                )}
              >
                <Icon className="w-4 h-4" />
              </div>
              {!isLast && (
                <div className="w-0.5 flex-1 bg-slate-200 my-1 min-h-[20px]" />
              )}
            </div>

            {/* right column — content */}
            <div className={cn('pb-6', isLast && 'pb-0')}>
              <p className="text-sm font-medium text-slate-800">{item.title}</p>
              {item.description && (
                <p className="text-xs text-slate-400 mt-0.5">
                  {item.description}
                </p>
              )}
              {item.content && <div className="mt-2">{item.content}</div>}
              <p className="text-xs text-slate-400 mt-1">{item.date}</p>
            </div>
          </div>
        );
      })}
    </div>
  );
}
