import { useNavigate } from 'react-router-dom';
import { useForm, useFieldArray } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { ArrowLeft, Plus, Trash2, Save, BookOpen, FileText, Tag, List, Send } from 'lucide-react';
import { createPaper } from '@/services/api';
import type { Paper, JournalName } from '@/types';
import TagInput from '@/components/shared/TagInput';
import FormField from '@/components/shared/FormField';
import { cn } from '@/lib/utils';
import { toast } from 'sonner';
import { useAuth } from '@/contexts/AuthContext';

// --- zod schema ---
const referenceSchema = z.object({
  title: z.string().min(1, '请输入文献标题'),
  authors: z.string().min(1, '请输入作者'),
  journal: z.string().min(1, '请输入期刊名称'),
  year: z.coerce.number().int().min(1900, '年份必须>=1900').max(2100, '年份必须<=2100'),
  volume: z.string().optional(),
  issue: z.string().optional(),
  pages: z.string().optional(),
  doi: z.string().optional(),
});

const paperSchema = z.object({
  title: z.string().min(1, '请输入论文题目'),
  journalName: z.string().refine(v => ['工学版','理学版','文科版','生物医学版'].includes(v), '请选择投稿期刊'),
  abstract: z.string().min(10, '摘要至少10个字'),
  keywords: z.array(z.string()).min(3, '至少添加3个关键词'),
  content: z.string().min(50, '正文至少50个字'),
  authorId: z.string().min(1, '请选择作者'),
  authorName: z.string().min(1, '请选择作者'),
  references: z.array(referenceSchema).default([]),
});

type PaperFormData = z.infer<typeof paperSchema>;

const JOURNAL_OPTIONS: { label: string; value: JournalName }[] = [
  { label: '工学版', value: '工学版' },
  { label: '理学版', value: '理学版' },
  { label: '文科版', value: '文科版' },
  { label: '生物医学版', value: '生物医学版' },
];

export default function PaperSubmit() {
  const navigate = useNavigate();
  const { user } = useAuth();

  const {
    register,
    handleSubmit,
    control,
    setValue,
    watch,
    formState: { errors, isSubmitting },
  } = useForm<PaperFormData>({
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    resolver: zodResolver(paperSchema) as any,
    defaultValues: {
      title: '',
      journalName: '工学版',
      abstract: '',
      keywords: [],
      content: '',
      authorId: String(user?.refId ?? ''),
      authorName: user?.realName ?? '',
      references: [],
    },
  });

  const { fields, append, remove } = useFieldArray({
    control,
    name: 'references',
  });

  const keywords = watch('keywords');

  const onSubmit = async (data: PaperFormData) => {
    try {
      // Map form references to API format
      const references = data.references?.map((ref, idx) => ({
        id: `ref-${Date.now()}-${idx}`,
        title: ref.title,
        authors: ref.authors,
        journal: ref.journal,
        year: ref.year,
        volume: ref.volume || undefined,
        issue: ref.issue || undefined,
        pages: ref.pages || undefined,
        doi: ref.doi || undefined,
      })) || [];

      await createPaper({
        ...data,
        journalName: data.journalName as JournalName,
        references,
      } as Partial<Paper>);
      toast.success('论文投稿成功！您的论文已提交，请等待审稿。');
      navigate('/papers');
    } catch (err) {
      const message = err instanceof Error ? err.message : '投稿失败，请稍后重试';
      toast.error(message);
    }
  };

  const handleAddReference = () => {
    append({
      title: '',
      authors: '',
      journal: '',
      year: new Date().getFullYear(),
      volume: '',
      issue: '',
      pages: '',
      doi: '',
    });
  };

  // --- styles ---
  const inputClass =
    'w-full px-3.5 py-2.5 bg-white border border-slate-200 rounded-lg text-sm text-slate-700 placeholder:text-slate-400 focus:outline-none focus:border-blue-400 focus:ring-2 focus:ring-blue-100 transition-all';
  const inputErrorClass =
    'w-full px-3.5 py-2.5 bg-white border border-red-300 rounded-lg text-sm text-slate-700 placeholder:text-slate-400 focus:outline-none focus:border-red-400 focus:ring-2 focus:ring-red-100 transition-all';
  const textareaClass =
    'w-full px-3.5 py-2.5 bg-white border border-slate-200 rounded-lg text-sm text-slate-700 placeholder:text-slate-400 focus:outline-none focus:border-blue-400 focus:ring-2 focus:ring-blue-100 transition-all resize-y';
  const textareaErrorClass =
    'w-full px-3.5 py-2.5 bg-white border border-red-300 rounded-lg text-sm text-slate-700 placeholder:text-slate-400 focus:outline-none focus:border-red-400 focus:ring-2 focus:ring-red-100 transition-all resize-y';

  return (
    <div className="p-6 max-w-4xl mx-auto">
      {/* --- back --- */}
      <button
        onClick={() => navigate('/papers')}
        className="inline-flex items-center gap-2 text-sm text-slate-500 hover:text-slate-700 mb-4 transition-colors"
      >
        <ArrowLeft className="w-4 h-4" />
        返回论文列表
      </button>

      {/* --- header --- */}
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-slate-800">在线投稿</h1>
        <p className="text-sm text-slate-400 mt-1">填写论文信息并提交，我们将尽快安排审稿</p>
      </div>

      {/* --- form --- */}
      {/* eslint-disable-next-line @typescript-eslint/no-explicit-any */}
      <form onSubmit={handleSubmit(onSubmit as any)} className="space-y-6">
        {/* section: basic info */}
        <div className="bg-white rounded-xl border border-slate-200 p-6">
          <div className="flex items-center gap-2 mb-6 pb-4 border-b border-slate-100">
            <div className="w-8 h-8 rounded-lg bg-blue-50 flex items-center justify-center">
              <FileText className="w-4 h-4 text-blue-500" />
            </div>
            <h2 className="text-lg font-semibold text-slate-800">基本信息</h2>
          </div>

          <div className="space-y-5">
            {/* title */}
            <FormField label="论文题目" required error={errors.title?.message}>
              <input
                type="text"
                placeholder="请输入论文题目"
                className={errors.title ? inputErrorClass : inputClass}
                {...register('title')}
              />
            </FormField>

            {/* journal */}
            <FormField label="投稿期刊" required error={errors.journalName?.message}>
              <select
                className={cn(
                  'w-full px-3.5 py-2.5 bg-white rounded-lg text-sm text-slate-700 focus:outline-none focus:border-blue-400 focus:ring-2 focus:ring-blue-100 transition-all',
                  errors.journalName ? 'border border-red-300' : 'border border-slate-200',
                )}
                {...register('journalName')}
              >
                {JOURNAL_OPTIONS.map((opt) => (
                  <option key={opt.value} value={opt.value}>
                    {opt.label}
                  </option>
                ))}
              </select>
            </FormField>

            {/* author (read-only, from login user) */}
            <FormField label="作者" required hint="当前登录作者，不可修改">
              <input
                type="text"
                readOnly
                value={user?.realName ?? ''}
                className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-lg text-sm text-slate-500 cursor-not-allowed"
              />
            </FormField>
          </div>
        </div>

        {/* section: content */}
        <div className="bg-white rounded-xl border border-slate-200 p-6">
          <div className="flex items-center gap-2 mb-6 pb-4 border-b border-slate-100">
            <div className="w-8 h-8 rounded-lg bg-amber-50 flex items-center justify-center">
              <BookOpen className="w-4 h-4 text-amber-500" />
            </div>
            <h2 className="text-lg font-semibold text-slate-800">论文内容</h2>
          </div>

          <div className="space-y-5">
            {/* abstract */}
            <FormField label="摘要" required error={errors.abstract?.message} hint="请简要描述论文的研究内容与结论">
              <textarea
                rows={4}
                placeholder="请输入论文摘要（至少10个字）"
                className={errors.abstract ? textareaErrorClass : textareaClass}
                {...register('abstract')}
              />
            </FormField>

            {/* keywords */}
            <FormField label="关键词" required error={errors.keywords?.message}>
              <TagInput
                tags={keywords}
                onChange={(tags) => {
                  setValue('keywords', tags, { shouldValidate: true });
                }}
                placeholder="输入关键词后按回车添加"
                label=""
              />
            </FormField>

            {/* content */}
            <FormField label="正文内容" required error={errors.content?.message} hint="请输入论文正文（至少50个字）">
              <textarea
                rows={12}
                placeholder="请输入论文正文内容，包括引言、方法、结果、讨论等部分..."
                className={errors.content ? textareaErrorClass : textareaClass}
                {...register('content')}
              />
            </FormField>
          </div>
        </div>

        {/* section: references */}
        <div className="bg-white rounded-xl border border-slate-200 p-6">
          <div className="flex items-center justify-between mb-6 pb-4 border-b border-slate-100">
            <div className="flex items-center gap-2">
              <div className="w-8 h-8 rounded-lg bg-purple-50 flex items-center justify-center">
                <List className="w-4 h-4 text-purple-500" />
              </div>
              <h2 className="text-lg font-semibold text-slate-800">参考文献</h2>
            </div>
            <button
              type="button"
              onClick={handleAddReference}
              className="inline-flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium text-[#1e3a5f] bg-[#eef2f6] rounded-lg hover:bg-[#dde4ed] transition-colors"
            >
              <Plus className="w-3.5 h-3.5" />
              添加文献
            </button>
          </div>

          {fields.length === 0 && (
            <div className="text-center py-8">
              <p className="text-sm text-slate-400">暂无参考文献，点击上方按钮添加</p>
            </div>
          )}

          <div className="space-y-4">
            {fields.map((field, index) => (
              <div
                key={field.id}
                className="relative border border-slate-200 rounded-lg p-4 bg-slate-50/30"
              >
                {/* remove button */}
                <button
                  type="button"
                  onClick={() => remove(index)}
                  className="absolute top-3 right-3 p-1.5 text-slate-400 hover:text-red-500 hover:bg-red-50 rounded-lg transition-colors"
                  aria-label={`删除第 ${index + 1} 条参考文献`}
                >
                  <Trash2 className="w-4 h-4" />
                </button>

                <div className="flex items-center gap-2 mb-3">
                  <span className="inline-flex items-center justify-center w-6 h-6 rounded-full bg-slate-200 text-slate-600 text-xs font-semibold">
                    {index + 1}
                  </span>
                  <span className="text-sm font-medium text-slate-600">
                    {`参考文献 ${index + 1}`}
                  </span>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                  {/* title */}
                  <div className="md:col-span-2">
                    <label className="block text-xs font-medium text-slate-500 mb-1">
                      文献标题 <span className="text-red-400">*</span>
                    </label>
                    <input
                      type="text"
                      placeholder="请输入文献标题"
                      className={cn(
                        'w-full px-3 py-2 bg-white border border-slate-200 rounded-lg text-sm focus:outline-none focus:border-blue-400 focus:ring-1 focus:ring-blue-100 transition-all',
                        errors.references?.[index]?.title && 'border-red-300 focus:border-red-400 focus:ring-red-100',
                      )}
                      {...register(`references.${index}.title`)}
                    />
                    {errors.references?.[index]?.title && (
                      <p className="text-xs text-red-500 mt-1">{errors.references[index]?.title?.message}</p>
                    )}
                  </div>

                  {/* authors */}
                  <div>
                    <label className="block text-xs font-medium text-slate-500 mb-1">
                      作者 <span className="text-red-400">*</span>
                    </label>
                    <input
                      type="text"
                      placeholder="文献作者"
                      className={cn(
                        'w-full px-3 py-2 bg-white border border-slate-200 rounded-lg text-sm focus:outline-none focus:border-blue-400 focus:ring-1 focus:ring-blue-100 transition-all',
                        errors.references?.[index]?.authors && 'border-red-300 focus:border-red-400 focus:ring-red-100',
                      )}
                      {...register(`references.${index}.authors`)}
                    />
                    {errors.references?.[index]?.authors && (
                      <p className="text-xs text-red-500 mt-1">{errors.references[index]?.authors?.message}</p>
                    )}
                  </div>

                  {/* journal */}
                  <div>
                    <label className="block text-xs font-medium text-slate-500 mb-1">
                      期刊名称 <span className="text-red-400">*</span>
                    </label>
                    <input
                      type="text"
                      placeholder="文献来源期刊"
                      className={cn(
                        'w-full px-3 py-2 bg-white border border-slate-200 rounded-lg text-sm focus:outline-none focus:border-blue-400 focus:ring-1 focus:ring-blue-100 transition-all',
                        errors.references?.[index]?.journal && 'border-red-300 focus:border-red-400 focus:ring-red-100',
                      )}
                      {...register(`references.${index}.journal`)}
                    />
                    {errors.references?.[index]?.journal && (
                      <p className="text-xs text-red-500 mt-1">{errors.references[index]?.journal?.message}</p>
                    )}
                  </div>

                  {/* year */}
                  <div>
                    <label className="block text-xs font-medium text-slate-500 mb-1">
                      年份 <span className="text-red-400">*</span>
                    </label>
                    <input
                      type="number"
                      placeholder="发表年份"
                      min={1900}
                      max={2100}
                      className={cn(
                        'w-full px-3 py-2 bg-white border border-slate-200 rounded-lg text-sm focus:outline-none focus:border-blue-400 focus:ring-1 focus:ring-blue-100 transition-all',
                        errors.references?.[index]?.year && 'border-red-300 focus:border-red-400 focus:ring-red-100',
                      )}
                      {...register(`references.${index}.year`)}
                    />
                    {errors.references?.[index]?.year && (
                      <p className="text-xs text-red-500 mt-1">{errors.references[index]?.year?.message}</p>
                    )}
                  </div>

                  {/* volume */}
                  <div>
                    <label className="block text-xs font-medium text-slate-500 mb-1">卷号</label>
                    <input
                      type="text"
                      placeholder="卷号（可选）"
                      className="w-full px-3 py-2 bg-white border border-slate-200 rounded-lg text-sm focus:outline-none focus:border-blue-400 focus:ring-1 focus:ring-blue-100 transition-all"
                      {...register(`references.${index}.volume`)}
                    />
                  </div>

                  {/* issue */}
                  <div>
                    <label className="block text-xs font-medium text-slate-500 mb-1">期号</label>
                    <input
                      type="text"
                      placeholder="期号（可选）"
                      className="w-full px-3 py-2 bg-white border border-slate-200 rounded-lg text-sm focus:outline-none focus:border-blue-400 focus:ring-1 focus:ring-blue-100 transition-all"
                      {...register(`references.${index}.issue`)}
                    />
                  </div>

                  {/* pages */}
                  <div>
                    <label className="block text-xs font-medium text-slate-500 mb-1">页码</label>
                    <input
                      type="text"
                      placeholder="页码（可选）"
                      className="w-full px-3 py-2 bg-white border border-slate-200 rounded-lg text-sm focus:outline-none focus:border-blue-400 focus:ring-1 focus:ring-blue-100 transition-all"
                      {...register(`references.${index}.pages`)}
                    />
                  </div>

                  {/* doi */}
                  <div>
                    <label className="block text-xs font-medium text-slate-500 mb-1">DOI</label>
                    <input
                      type="text"
                      placeholder="DOI（可选）"
                      className="w-full px-3 py-2 bg-white border border-slate-200 rounded-lg text-sm focus:outline-none focus:border-blue-400 focus:ring-1 focus:ring-blue-100 transition-all"
                      {...register(`references.${index}.doi`)}
                    />
                  </div>
                </div>
              </div>
            ))}
          </div>

          {fields.length > 0 && (
            <div className="mt-4">
              <button
                type="button"
                onClick={handleAddReference}
                className="inline-flex items-center gap-1.5 px-3 py-2 text-sm text-slate-500 border border-dashed border-slate-300 rounded-lg hover:border-slate-400 hover:text-slate-600 transition-colors w-full justify-center"
              >
                <Plus className="w-4 h-4" />
                继续添加参考文献
              </button>
            </div>
          )}
        </div>

        {/* section: submit actions */}
        <div className="bg-white rounded-xl border border-slate-200 p-6">
          <div className="flex items-center gap-2 mb-6 pb-4 border-b border-slate-100">
            <div className="w-8 h-8 rounded-lg bg-emerald-50 flex items-center justify-center">
              <Send className="w-4 h-4 text-emerald-500" />
            </div>
            <h2 className="text-lg font-semibold text-slate-800">提交投稿</h2>
          </div>

          <p className="text-sm text-slate-500 mb-5">
            请检查以上信息确认无误后再提交。提交后论文将进入审稿流程，您可以在论文列表中查看审稿进度。
          </p>

          <div className="flex items-center gap-3">
            <button
              type="submit"
              disabled={isSubmitting}
              className="inline-flex items-center gap-2 px-6 py-2.5 bg-[#1e3a5f] text-white text-sm font-medium rounded-lg hover:bg-[#162d4a] transition-colors disabled:opacity-50 disabled:cursor-not-allowed shadow-sm"
            >
              {isSubmitting ? (
                <>
                  <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                  提交中...
                </>
              ) : (
                <>
                  <Save className="w-4 h-4" />
                  提交投稿
                </>
              )}
            </button>

            <button
              type="button"
              onClick={() => navigate('/papers')}
              className="px-6 py-2.5 text-sm font-medium text-slate-600 bg-slate-100 rounded-lg hover:bg-slate-200 transition-colors"
            >
              取消
            </button>
          </div>
        </div>
      </form>
    </div>
  );
}
