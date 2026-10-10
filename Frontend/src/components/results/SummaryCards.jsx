import { Receipt, TrendingDown, PiggyBank } from "lucide-react";
import { formatINR } from "../../utils/format.js";

export default function SummaryCards({ totalBilled, totalOvercharge, totalSavings }) {
  const cards = [
    {
      label: "Total Billed",
      value: formatINR(totalBilled),
      icon: Receipt,
      bg: "bg-white",
      text: "text-ink",
      border: "border-pink-200",
    },
    {
      label: "Total Overcharge",
      value: formatINR(totalOvercharge),
      icon: TrendingDown,
      bg: "bg-red-50",
      text: "text-red-700",
      border: "border-red-200",
    },
    {
      label: "Potential Savings",
      value: formatINR(totalSavings),
      icon: PiggyBank,
      bg: "bg-green-50",
      text: "text-green-700",
      border: "border-green-200",
    },
  ];

  return (
    <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
      {cards.map((c) => {
        const Icon = c.icon;
        return (
          <div key={c.label} className={`card p-5 ${c.border}`}>
            <div className="flex items-center gap-2 mb-2">
              <Icon size={18} className={c.text} />
              <span className="text-sm font-medium text-ink/60">{c.label}</span>
            </div>
            <p className={`font-heading font-bold text-2xl ${c.text}`}>
              {c.value}
            </p>
          </div>
        );
      })}
    </div>
  );
}
