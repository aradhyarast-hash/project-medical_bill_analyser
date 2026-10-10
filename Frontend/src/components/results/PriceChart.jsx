import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer,
} from "recharts";
import { formatINR } from "../../utils/format.js";

// Shorten long medicine names for the x-axis
function shortName(name) {
  if (!name) return "—";
  const parts = name.split(/\s+/);
  if (parts.length <= 2) return name;
  return parts.slice(0, 2).join(" ");
}

export default function PriceChart({ items }) {
  const data = items
    .filter((i) => i.status !== "NO_REFERENCE")
    .map((i) => ({
      name: shortName(i.matched_name || i.extracted_name),
      Billed: i.billed_unit_price,
      Reference: i.ceiling_unit_price ?? i.mrp_unit_price ?? 0,
      Generic: i.generic?.unit_price ?? 0,
    }));

  if (data.length === 0) {
    return (
      <div className="card p-6 text-center text-sm text-ink/50">
        No chartable price data available.
      </div>
    );
  }

  return (
    <div className="card p-5">
      <h3 className="font-heading font-bold text-ink mb-4">
        Price Comparison per Unit
      </h3>
      <div className="w-full h-72">
        <ResponsiveContainer width="100%" height="100%">
          <BarChart data={data} barGap={4}>
            <CartesianGrid strokeDasharray="3 3" stroke="#fbcfe8" vertical={false} />
            <XAxis
              dataKey="name"
              tick={{ fontSize: 11, fill: "#6b7280" }}
              interval={0}
              angle={-20}
              textAnchor="end"
              height={50}
            />
            <YAxis tick={{ fontSize: 11, fill: "#6b7280" }} />
            <Tooltip
              formatter={(v) => formatINR(v)}
              contentStyle={{
                borderRadius: "12px",
                border: "1px solid #fbcfe8",
                fontSize: "13px",
              }}
            />
            <Legend wrapperStyle={{ fontSize: "13px" }} />
            <Bar dataKey="Billed" fill="#db2777" radius={[4, 4, 0, 0]} />
            <Bar dataKey="Reference" fill="#7c3aed" radius={[4, 4, 0, 0]} />
            <Bar dataKey="Generic" fill="#16a34a" radius={[4, 4, 0, 0]} />
          </BarChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}
