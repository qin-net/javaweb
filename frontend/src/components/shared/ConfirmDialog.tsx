import { Fragment } from 'react';
import { Dialog, Transition } from '@headlessui/react';
import { AlertTriangle } from 'lucide-react';

interface ConfirmDialogProps {
  open: boolean;
  onClose: () => void;
  onConfirm: () => void;
  title: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
  variant?: 'danger' | 'warning' | 'info';
}

export default function ConfirmDialog({
  open,
  onClose,
  onConfirm,
  title,
  message,
  confirmText = '确认',
  cancelText = '取消',
  variant = 'info',
}: ConfirmDialogProps) {
  const colors: Record<string, { button: string; icon: string }> = {
    danger: { button: 'bg-red-600 hover:bg-red-700', icon: 'text-red-500' },
    warning: { button: 'bg-amber-600 hover:bg-amber-700', icon: 'text-amber-500' },
    info: { button: 'bg-blue-600 hover:bg-blue-700', icon: 'text-blue-500' },
  };
  const color = colors[variant] ?? colors.info;

  const handleConfirm = () => {
    onConfirm();
    onClose();
  };

  return (
    <Transition appear show={open} as={Fragment}>
      <Dialog as="div" className="relative z-50" onClose={onClose}>
        <Transition.Child
          as={Fragment}
          enter="ease-out duration-300"
          enterFrom="opacity-0"
          enterTo="opacity-100"
          leave="ease-in duration-200"
          leaveFrom="opacity-100"
          leaveTo="opacity-0"
        >
          <div className="fixed inset-0 bg-black/40" />
        </Transition.Child>

        <div className="fixed inset-0 overflow-y-auto">
          <div className="flex min-h-full items-center justify-center p-4">
            <Transition.Child
              as={Fragment}
              enter="ease-out duration-300"
              enterFrom="opacity-0 scale-95"
              enterTo="opacity-100 scale-100"
              leave="ease-in duration-200"
              leaveFrom="opacity-100 scale-100"
              leaveTo="opacity-0 scale-95"
            >
              <Dialog.Panel className="w-full max-w-md bg-white rounded-2xl p-6 shadow-xl">
                <div className="flex items-center gap-4">
                  <div
                    className={`w-12 h-12 rounded-full bg-slate-100 flex items-center justify-center ${color.icon}`}
                  >
                    <AlertTriangle className="w-6 h-6" />
                  </div>
                  <div>
                    <Dialog.Title className="text-lg font-semibold text-slate-800">
                      {title}
                    </Dialog.Title>
                    <Dialog.Description className="text-sm text-slate-500 mt-1">
                      {message}
                    </Dialog.Description>
                  </div>
                </div>

                <div className="flex justify-end gap-3 mt-6">
                  <button
                    onClick={onClose}
                    className="px-4 py-2 text-sm font-medium text-slate-600 bg-slate-100 rounded-lg hover:bg-slate-200 transition-colors"
                  >
                    {cancelText}
                  </button>
                  <button
                    onClick={handleConfirm}
                    className={`px-4 py-2 text-sm font-medium text-white rounded-lg transition-colors ${color.button}`}
                  >
                    {confirmText}
                  </button>
                </div>
              </Dialog.Panel>
            </Transition.Child>
          </div>
        </div>
      </Dialog>
    </Transition>
  );
}
