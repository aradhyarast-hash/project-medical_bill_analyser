import { BadgeCheck, AlertTriangle, Pill } from "lucide-react";
import { formatINR } from "../../utils/format.js";

export default function GenericAlternatives({ items }) {
  // Only show items that actually have a generic object
  const withGenerics = items.filter((i) => i.generic);

  if (withGenerics.length === 0) {
    return (
      <div className="card p-6 text-center text-sm text-ink/50">
        No generic alternatives found for the medicines on this bill.
      </div>
    );
  }

  return (
    <div className="flex flex-col gap-4">
      <h3 className="font-heading font-bold text-ink text-lg">
        Generic Alternatives
      </h3>
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        {withGenerics.map((item) => {
          const g = item.generic;
          return (
            <div key={item.id} className="card p-5">
              <div className="flex items-start justify-between gap-2 mb-3">
                <div className="flex items-center gap-2">
                  <span className="flex items-center justify-center w-8 h-8 rounded-lg bg-green-50 text-green-600 shrink-0">
                    <Pill size={16} />
                  </span>
                  <div>
                    <p className="font-heading font-semibold text-ink text-sm">
                      {g.name}
                    </p>
                    <p className="text-xs text-ink/40">{g.ingredient}</p>
                  </div>
                </div>
                <span className="text-sm font-bold text-green-600 shrink-0">
                  Save {formatINR(g.saving)}
                </span>
              </div>

              <div className="grid grid-cols-2 gap-x-3 gap-y-1.5 text-xs mb-3">
                <div>
                  <p className="text-ink/40">Strength</p>
                  <p className="text-ink/80 font-medium">{g.strength}</p>
                </div>
                <div>
                  <p className="text-ink/40">Form</p>
                  <p className="text-ink/80 font-medium">{g.form}</p>
                </div>
                <div>
                  <p className="text-ink/40">Unit price</p>
                  <p className="text-ink/80 font-medium">{formatINR(g.unit_price)}</p>
                </div>
                <div>
                  <p className="text-ink/40">For</p>
                  <p className="text-ink/80 font-medium truncate">
                    {item.matched_name || item.extracted_name}
                  </p>
                </div>
              </div>

              {g.match_verified ? (
                <span className="inline-flex items-center gap-1.5 text-xs font-semibold text-green-700 bg-green-100 px-3 py-1 rounded-full">
                  <BadgeCheck size={14} />
                  Verified match: same ingredient, strength and form
                </span>
              ) : (
                <span className="inline-flex items-center gap-1.5 text-xs font-semibold text-amber-700 bg-amber-100 px-3 py-1 rounded-full">
                  <AlertTriangle size={14} />
                  Unverified — consult your pharmacist before switching
                </span>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}
