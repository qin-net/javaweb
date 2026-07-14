import { useState, type KeyboardEvent, type ChangeEvent, type FocusEvent } from 'react';

// --- inline X icon (avoids external dependency) ---
function XIcon({ className }: { className?: string }) {
  return (
    <svg
      className={className}
      fill="none"
      viewBox="0 0 24 24"
      stroke="currentColor"
      strokeWidth={2.5}
    >
      <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
    </svg>
  );
}

// --- types ---
interface TagInputProps {
  tags: string[];
  onChange: (tags: string[]) => void;
  placeholder?: string;
  label?: string;
}

// --- component ---
export default function TagInput({
  tags,
  onChange,
  placeholder = '输入后按回车添加',
  label,
}: TagInputProps) {
  const [input, setInput] = useState('');

  const addTag = () => {
    const trimmed = input.trim();
    if (trimmed && !tags.includes(trimmed)) {
      onChange([...tags, trimmed]);
    }
    setInput('');
  };

  const handleKeyDown = (e: KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      addTag();
    } else if (e.key === 'Backspace' && !input && tags.length > 0) {
      // Remove the last tag when Backspace is pressed on empty input
      onChange(tags.slice(0, -1));
    }
  };

  const handleBlur = (_e: FocusEvent<HTMLInputElement>) => {
    // Commit any pending text when the input loses focus
    if (input.trim()) {
      addTag();
    }
  };

  return (
    <div>
      {label && (
        <label className="block text-sm font-medium text-slate-700 mb-2">
          {label}
        </label>
      )}

      <div
        className={`
          flex flex-wrap gap-2 p-2 bg-white border border-slate-200 rounded-lg
          focus-within:border-[#c9a96e] focus-within:ring-2 focus-within:ring-amber-50
          transition-all min-h-[42px] cursor-text
        `}
        onClick={() => {
          // Focus the hidden input when the container is clicked
          const inputEl = document.activeElement?.closest('[data-tag-input]')?.querySelector('input');
          inputEl?.focus();
        }}
      >
        {/* rendered tags */}
        {tags.map((tag) => (
          <span
            key={tag}
            className="inline-flex items-center gap-1 px-2.5 py-1 bg-[#eef2f6] text-[#1e3a5f] rounded-md text-sm font-medium"
          >
            {tag}
            <button
              type="button"
              onClick={(e) => {
                e.stopPropagation();
                onChange(tags.filter((t) => t !== tag));
              }}
              className="hover:text-[#c9a96e] transition-colors p-0.5 rounded-sm hover:bg-white/50"
              aria-label={`移除标签: ${tag}`}
            >
              <XIcon className="w-3 h-3" />
            </button>
          </span>
        ))}

        {/* text input */}
        <input
          value={input}
          onChange={(e: ChangeEvent<HTMLInputElement>) => setInput(e.target.value)}
          onKeyDown={handleKeyDown}
          onBlur={handleBlur}
          placeholder={tags.length === 0 ? placeholder : ''}
          className="flex-1 min-w-[120px] outline-none text-sm bg-transparent py-0.5 text-slate-700 placeholder-slate-400"
        />
      </div>

      {/* helper text */}
      <p className="text-xs text-slate-400 mt-1.5">
        输入关键词后按 <kbd className="px-1 py-0.5 bg-slate-100 rounded text-[10px] font-mono">Enter</kbd> 添加
      </p>
    </div>
  );
}
