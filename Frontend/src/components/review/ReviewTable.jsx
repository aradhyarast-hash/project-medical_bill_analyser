import { AlertTriangle } from "lucide-react";

// Editable inline input that auto-focuses when confidence is low
function EditableCell({ value, onChange, lowConfidence, type = "text" }) {
  return (
    <input
      type={type}
      value={value}
      onChange={(e) => onChange(e.target.value)}
      autoFocus={lowConfidence}
      className={`w-full px-2 py-1.5 rounded-lg border text-sm transition-colors ${
        lowConfidence
          ? "border-amber-300 bg-amber-50 focus:border-amber-400"
          : "border-slate-200 focus:border-primary"
      }`}
    />
  );
}

export default function ReviewTable({ items, onItemChange, onItemDelete, onItemAdd }) {
  return (
    <div className="card overflow-hidden">
      <div className="overflow-x-auto">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-pink-50 border-b border-pink-200 text-left">
              <th className="px-4 py-3 font-heading font-semibold text-ink">Medicine</th>
              <th className="px-4 py-3 font-heading font-semibold text-ink">Strength</th>
              <th className="px-4 py-3 font-heading font-semibold text-ink">Qty</th>
              <th className="px-4 py-3 font-heading font-semibold text-ink">Billed price (₹)</th>
              <th className="px-4 py-3 font-heading font-semibold text-ink">Confidence</th>
              <th className="px-4 py-3 font-heading font-semibold text-ink"></th>
            </tr>
          </thead>
          <tbody>
            {items.map((item) => {
              const low = item.confidence < 0.75;
              return (
                <tr
                  key={item.id}
                  className={`border-b border-pink-100 ${
                    low ? "bg-amber-50/60" : ""
                  }`}
                >
                  <td className="px-4 py-2.5">
                    <EditableCell
                      value={item.extracted_name}
                      lowConfidence={false}
                      onChange={(v) => onItemChange(item.id, "extracted_name", v)}
                    />
                  </td>
                  <td className="px-4 py-2.5">
                    <EditableCell
                      value={item.strength}
                      lowConfidence={false}
                      onChange={(v) => onItemChange(item.id, "strength", v)}
                    />
                  </td>
                  <td className="px-4 py-2.5 w-20">
                    <EditableCell
                      type="number"
                      value={item.quantity}
                      lowConfidence={false}
                      onChange={(v) => onItemChange(item.id, "quantity", Number(v))}
                    />
                  </td>
                  <td className="px-4 py-2.5 w-28">
                    <EditableCell
                      type="number"
                      value={item.billed_unit_price}
                      lowConfidence={low}
                      onChange={(v) => onItemChange(item.id, "billed_unit_price", Number(v))}
                    />
                  </td>
                  <td className="px-4 py-2.5">
                    <div className="flex items-center gap-1.5">
                      <span
                        className={`text-xs font-semibold ${
                          low ? "text-amber-600" : "text-green-600"
                        }`}
                      >
                        {(item.confidence * 100).toFixed(0)}%
                      </span>
                      {low && (
                        <span className="inline-flex items-center gap-1 text-xs text-amber-700 bg-amber-100 px-2 py-0.5 rounded-full font-medium">
                          <AlertTriangle size={11} /> Please verify
                        </span>
                      )}
                    </div>
                  </td>
                  <td className="px-4 py-2.5">
                    <button
                      onClick={() => onItemDelete(item.id)}
                      className="text-xs text-red-600 hover:text-red-700 font-medium"
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
      <div className="px-4 py-3 border-t border-pink-100">
        <button
          onClick={onItemAdd}
          className="text-sm font-medium text-primary hover:text-primary-hover transition-colors"
        >
          + Add a row
        </button>
      </div>
    </div>
  );
}
