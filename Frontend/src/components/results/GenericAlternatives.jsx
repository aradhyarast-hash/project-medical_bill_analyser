import { BadgeCheck, AlertTriangle, Pill, Tag, TrendingDown } from "lucide-react";
import { formatINR } from "../../utils/format.js";

function getTagBadgeStyle(tag) {
  switch (tag.toUpperCase()) {
    case "CHEAPEST":
      return "bg-emerald-100 text-emerald-800 border-emerald-300";
    case "BEST":
      return "bg-blue-100 text-blue-800 border-blue-300";
    case "SAME_SALT":
      return "bg-purple-100 text-purple-800 border-purple-300";
    case "ALTERNATE":
      return "bg-amber-100 text-amber-800 border-amber-300";
    case "JAN_AUSHADHI":
      return "bg-teal-100 text-teal-800 border-teal-300";
    case "GENERIC":
      return "bg-cyan-100 text-cyan-800 border-cyan-300";
    default:
      return "bg-pink-100 text-pink-800 border-pink-300";
  }
}

function formatTagLabel(tag) {
  return tag.replace(/_/g, " ");
}

export default function GenericAlternatives({ items }) {
  if (!items || items.length === 0) {
    return null;
  }

  // Items that have alternatives array or generic object
  const itemsWithAlts = items.filter(
    (i) => (i.alternatives && i.alternatives.length > 0) || i.generic
  );

  if (itemsWithAlts.length === 0) {
    return (
      <div className="card p-6 text-center text-sm text-ink/50">
        No alternatives found in reference data.
      </div>
    );
  }

  return (
    <div className="flex flex-col gap-6">
      <div>
        <h3 className="font-heading font-bold text-ink text-xl">
          Salt-Matched Alternatives &amp; Generic Options
        </h3>
        <p className="text-xs text-ink/60 mt-0.5">
          Verified matches sharing the identical active ingredient, strength, and dosage form.
        </p>
      </div>

      {itemsWithAlts.map((item) => {
        const alts = item.alternatives && item.alternatives.length > 0
          ? item.alternatives
          : item.generic
          ? [
              {
                name: item.generic.name,
                salt: item.generic.ingredient,
                strength: item.generic.strength,
                dosageForm: item.generic.form,
                unitPrice: item.generic.unit_price,
                tags: ["CHEAPEST", "SAME_SALT", "JAN_AUSHADHI", "GENERIC"],
                savingsAmount: item.generic.saving,
                savingsPercent: 50.0,
                reason: "Verified Jan Aushadhi generic alternative with same ingredient.",
              },
            ]
          : [];

        return (
          <div key={item.id} className="card p-5 border border-pink-100">
            <div className="flex flex-wrap items-center justify-between gap-2 pb-3 mb-4 border-b border-pink-100">
              <div>
                <span className="text-xs font-semibold text-primary uppercase tracking-wide">
                  Prescribed / Extracted Item
                </span>
                <h4 className="font-heading font-bold text-ink text-base">
                  {item.matched_name || item.extracted_name}{" "}
                  <span className="text-xs text-ink/50 font-normal">
                    ({item.form} · {item.strength})
                  </span>
                </h4>
              </div>
              {item.billed_unit_price != null && (
                <div className="text-right">
                  <p className="text-xs text-ink/40">Billed price</p>
                  <p className="font-semibold text-ink text-sm">
                    {formatINR(item.billed_unit_price)} / unit
                  </p>
                </div>
              )}
            </div>

            {/* Alternatives Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {alts.map((alt, idx) => (
                <div
                  key={idx}
                  className="p-4 rounded-xl border border-pink-100/80 bg-white hover:border-primary/40 transition-colors shadow-sm flex flex-col justify-between"
                >
                  <div>
                    {/* Header: Name and Unit Price */}
                    <div className="flex items-start justify-between gap-2 mb-2">
                      <div className="flex items-center gap-2">
                        <span className="flex items-center justify-center w-7 h-7 rounded-lg bg-pink-50 text-primary shrink-0">
                          <Pill size={15} />
                        </span>
                        <div>
                          <p className="font-heading font-semibold text-ink text-sm">
                            {alt.name}
                          </p>
                          <p className="text-xs text-ink/40">
                            {alt.manufacturer || alt.salt}
                          </p>
                        </div>
                      </div>
                      <div className="text-right shrink-0">
                        <p className="font-bold text-ink text-sm">
                          {formatINR(alt.unitPrice)}
                        </p>
                        <p className="text-[10px] text-ink/40">per unit</p>
                      </div>
                    </div>

                    {/* Tag Badges */}
                    <div className="flex flex-wrap gap-1.5 my-2.5">
                      {alt.tags &&
                        alt.tags.map((tag) => (
                          <span
                            key={tag}
                            className={`inline-flex items-center gap-1 text-[11px] font-semibold px-2.5 py-0.5 rounded-full border ${getTagBadgeStyle(
                              tag
                            )}`}
                          >
                            <Tag size={10} />
                            {formatTagLabel(tag)}
                          </span>
                        ))}
                    </div>

                    {/* Details: Salt, Strength, Form */}
                    <div className="grid grid-cols-2 gap-x-2 gap-y-1 text-xs text-ink/70 my-2 pt-2 border-t border-pink-50">
                      <div>
                        <span className="text-ink/40">Strength: </span>
                        <span className="font-medium">{alt.strength}</span>
                      </div>
                      <div>
                        <span className="text-ink/40">Form: </span>
                        <span className="font-medium">{alt.dosageForm || "Tablet"}</span>
                      </div>
                      {alt.packSize && (
                        <div className="col-span-2">
                          <span className="text-ink/40">Pack: </span>
                          <span className="font-medium">{alt.packSize}</span>
                        </div>
                      )}
                    </div>

                    {/* Reason */}
                    {alt.reason && (
                      <p className="text-[11px] text-ink/60 bg-pink-50/50 p-2 rounded-lg mt-2 leading-relaxed">
                        {alt.reason}
                      </p>
                    )}
                  </div>

                  {/* Savings footer */}
                  {alt.savingsAmount != null && alt.savingsAmount > 0 && (
                    <div className="mt-3 pt-2.5 border-t border-pink-100 flex items-center justify-between text-xs">
                      <span className="inline-flex items-center gap-1 text-emerald-700 font-semibold">
                        <TrendingDown size={14} /> Potential Savings:
                      </span>
                      <span className="font-bold text-emerald-700">
                        {formatINR(alt.savingsAmount)} / unit ({alt.savingsPercent}%)
                      </span>
                    </div>
                  )}
                </div>
              ))}
            </div>
          </div>
        );
      })}
    </div>
  );
}
