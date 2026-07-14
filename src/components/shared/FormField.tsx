import type { ReactNode } from 'react';

// --- inline helper ---
function cn(...classes: (string | boolean | undefined | null)[]): string {
  return classes.filter(Boolean).join(' ');
}

// --- types ---
interface FormFieldProps {
  label: string;
  required?: boolean;
  error?: string;
  hint?: string;
  children: ReactNode;
  className?: string;
}

// --- component ---
export default function FormField({
  label,
  required = false,
  error,
  hint,
  children,
  className = '',
}: FormFieldProps) {
  return (
    <div className={className}>
      {/* label row */}
      <label
        className={cn(
          'block text-sm font-medium mb-1.5',
          error ? 'text-red-600' : 'text-slate-700',
        )}
      >
        {label}
        {required && (
          <span className="text-red-500 ml-1" aria-hidden="true">
            *
          </span>
        )}
        {required && (
          <span className="sr-only">（必填）</span>
        )}
      </label>

      {/* form control slot */}
      {children}

      {/* hint / error messages */}
      {hint && !error && (
        <p className="text-xs text-slate-400 mt-1.5">{hint}</p>
      )}

      {error && (
        <div className="flex items-center gap-1 mt-1.5">
          {/* inline error icon */}
          <svg
            className="w-3.5 h-3.5 text-red-500 flex-shrink-0"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
            strokeWidth={2}
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
            />
          </svg>
          <p className="text-xs text-red-500">{error}</p>
        </div>
      )}
    </div>
  );
}
