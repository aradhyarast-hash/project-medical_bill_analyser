import { useState, useMemo } from "react";
import { ChevronDown, ArrowUpDown } from "lucide-react";
import StatusBadge from "../ui/StatusBadge.jsx";
import { formatINR } from "../../utils/format.js";

const STATUS_FILTERS = ["ALL", "OVERCHARGED", "ABOVE_MRP", "FAIR", "NO_REFERENCE"];

// Get the reference price for comparison (prefer ceiling, fall back to MRP)
function getReference(item) {
  if (item.ceiling_unit_price != null) return item.ceiling_unit_price;
  if (item.mrp_unit_price != null) return item.mrp_unit_price;
  return null;
}

export default function AuditTable({ items }) {
  const [sortDir, setSortDir] = useState("desc"); // sort by difference
  const [filter, setFilter] = useState("ALL");
  const [expanded, setExpanded] = useState(null);

  const filtered = useMemo(() => {
    let list = filter === "ALL" ? items : items.filter((i) => i.status === filter);
    return [...list].sort((a, b) => {
      const diffA = a.overcharge_amount || 0;
      const diffB = b.overcharge_amount || 0;
      return sortDir === "desc" ? diffB - diffA : diffA - diffB;
    });
  }, [items, filter, sortDir]);

  return (
    <div className="card overflow-hidden">
      {/* Filter chips */}
      <div className="flex flex-wrap gap-2 p-4 border-b border-pink-100">
        {STATUS_FILTERS.map((f) => (
          <button
            key={f}
            onClick={() => setFilter(f)}
            className={`px-3 py-1 rounded-full text-xs font-semibold transition-all ${
              filter === f
                ? "bg-primary text-white"
                : "bg-pink-50 text-ink/60 hover:bg-pink-100"
            }`}
          >
            {f === "ALL" ? "All" : f.replace(/_/g, " ")}
          </button>
        ))}
        <button
          onClick={() => setSortDir((d) => (d === "desc" ? "asc" : "desc"))}
          className="ml-auto inline-flex items-center gap-1 text-xs font-medium text-ink/60 hover:text-primary transition-colors"
        >
          <ArrowUpDown size={14} />
          Sort by difference
        </button>
      </div>

      {/* Desktop table */}
      <div className="hidden md:block overflow-x-auto">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-pink-50/50 border-b border-pink-100 text-left">
              <th className="px-4 py-3 font-heading font-semibold text-ink">Medicine</th>
              <th className="px-4 py-3 font-heading font-semibold text-ink text-center">Qty</th>
              <th className="px-4 py-3 font-heading font-semibold text-ink text-right">Billed / unit</th>
              <th className="px-4 py-3 font-heading font-semibold text-ink text-right">Reference / unit</th>
              <th className="px-4 py-3 font-heading font-semibold text-ink text-right">Difference</th>
              <th className="px-4 py-3 font-heading font-semibold text-ink">Status</th>
              <th className="px-4 py-3"></th>
            </tr>
          </thead>
          <tbody>
            {filtered.map((item) => {
              const ref = getReference(item);
              const isExpanded = expanded === item.id;
              return (
                <>
                  <tr
                    key={item.id}
                    className="border-b border-pink-50 hover:bg-pink-50/30 transition-colors"
                  >
                    <td className="px-4 py-3">
                      <p className="font-medium text-ink">{item.matched_name || item.extracted_name}</p>
                      <p className="text-xs text-ink/40">{item.form}</p>
                    </td>
                    <td className="px-4 py-3 text-center text-ink/70">{item.quantity}</td>
                    <td className="px-4 py-3 text-right font-medium">{formatINR(item.billed_unit_price)}</td>
                    <td className="px-4 py-3 text-right text-ink/70">{formatINR(ref)}</td>
                    <td className={`px-4 py-3 text-right font-semibold ${item.overcharge_amount > 0 ? "text-red-600" : "text-green-600"}`}>
                      {item.overcharge_amount > 0 ? `+${formatINR(item.overcharge_amount)}` : formatINR(0)}
                    </td>
                    <td className="px-4 py-3"><StatusBadge status={item.status} /></td>
                    <td className="px-4 py-3">
                      <button
                        onClick={() => setExpanded(isExpanded ? null : item.id)}
                        className="text-ink/40 hover:text-primary transition-colors"
                        aria-label="Expand calculation"
                      >
                        <ChevronDown size={18} className={`transition-transform ${isExpanded ? "rotate-180" : ""}`} />
                      </button>
                    </td>
                  </tr>
                  {isExpanded && (
                    <tr key={item.id + "-exp"} className="bg-pink-50/30">
                      <td colSpan={7} className="px-4 py-3">
                        <div className="text-sm text-ink/70 leading-relaxed">
                          <span className="font-semibold">Calculation: </span>
                          ({formatINR(item.billed_unit_price)} billed − {formatINR(ref)} reference) × {item.quantity} qty ={" "}
                          <span className={item.overcharge_amount > 0 ? "text-red-600 font-semibold" : "text-green-600 font-semibold"}>
                            {formatINR(item.overcharge_amount)}
                          </span>
                        </div>
                      </td>
                    </tr>
                  )}
                </>
              );
            })}
          </tbody>
        </table>
      </div>

      {/* Mobile stacked cards */}
      <div className="md:hidden divide-y divide-pink-50">
        {filtered.map((item) => {
          const ref = getReference(item);
          const isExpanded = expanded === item.id;
          return (
            <div key={item.id} className="p-4">
              <div className="flex items-start justify-between gap-2 mb-2">
                <div>
                  <p className="font-medium text-ink text-sm">
                    {item.matched_name || item.extracted_name}
                  </p>
                  <p className="text-xs text-ink/40">{item.form} · Qty: {item.quantity}</p>
                </div>
                <StatusBadge status={item.status} />
              </div>
              <div className="grid grid-cols-3 gap-2 text-xs mt-2">
                <div>
                  <p className="text-ink/40">Billed/unit</p>
                  <p className="font-medium">{formatINR(item.billed_unit_price)}</p>
                </div>
                <div>
                  <p className="text-ink/40">Reference</p>
                  <p className="font-medium">{formatINR(ref)}</p>
                </div>
                <div>
                  <p className="text-ink/40">Difference</p>
                  <p className={`font-semibold ${item.overcharge_amount > 0 ? "text-red-600" : "text-green-600"}`}>
                    {item.overcharge_amount > 0 ? `+${formatINR(item.overcharge_amount)}` : formatINR(0)}
                  </p>
                </div>
              </div>
              {isExpanded && (
                <p className="text-xs text-ink/60 mt-2 leading-relaxed">
                  ({formatINR(item.billed_unit_price)} − {formatINR(ref)}) × {item.quantity} = {formatINR(item.overcharge_amount)}
                </p>
              )}
              <button
                onClick={() => setExpanded(isExpanded ? null : item.id)}
                className="text-xs text-primary font-medium mt-2"
              >
                {isExpanded ? "Hide calculation" : "Show calculation"}
              </button>
            </div>
          );
        })}
      </div>

      {filtered.length === 0 && (
        <p className="text-center text-ink/50 py-8 text-sm">No items match this filter.</p>
      )}
    </div>
  );
}
