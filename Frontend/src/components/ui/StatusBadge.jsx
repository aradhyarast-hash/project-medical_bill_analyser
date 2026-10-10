// Status badge component — never relies on color alone, always includes an icon or label

const STATUS_CONFIG = {
  OVERCHARGED: {
    label: "Above ceiling price",
    bg: "bg-red-100",
    text: "text-red-700",
    dot: "bg-red-500",
  },
  ABOVE_MRP: {
    label: "Above MRP",
    bg: "bg-red-100",
    text: "text-red-700",
    dot: "bg-red-500",
  },
  FAIR: {
    label: "Within limit",
    bg: "bg-green-100",
    text: "text-green-700",
    dot: "bg-green-500",
  },
  NO_REFERENCE: {
    label: "No reference found",
    bg: "bg-slate-100",
    text: "text-slate-600",
    dot: "bg-slate-400",
  },
};

export default function StatusBadge({ status }) {
  const config = STATUS_CONFIG[status] || STATUS_CONFIG.NO_REFERENCE;
  return (
    <span
      className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold ${config.bg} ${config.text}`}
    >
      <span className={`w-2 h-2 rounded-full ${config.dot}`} />
      {config.label}
    </span>
  );
}
