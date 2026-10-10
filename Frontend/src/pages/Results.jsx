import { useEffect, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import { Download, RotateCcw, AlertCircle } from "lucide-react";
import { useAudit } from "../context/AuditContext.jsx";
import SummaryCards from "../components/results/SummaryCards.jsx";
import AuditTable from "../components/results/AuditTable.jsx";
import PriceChart from "../components/results/PriceChart.jsx";
import GenericAlternatives from "../components/results/GenericAlternatives.jsx";
import DisclaimerBanner from "../components/results/DisclaimerBanner.jsx";
import { formatINR, formatDate } from "../utils/format.js";

export default function Results() {
  const navigate = useNavigate();
  const { result, reset } = useAudit();

  // Redirect to upload with a toast if there's no result data
  useEffect(() => {
    if (!result || !result.items) {
      toast.error("No bill data found. Please upload a bill first.");
      navigate("/upload");
    }
  }, [result, navigate]);

  // Compute totals from items — never hardcode
  const totals = useMemo(() => {
    if (!result?.items) return { billed: 0, overcharge: 0, savings: 0 };
    const billed = result.items.reduce(
      (sum, i) => sum + i.billed_unit_price * i.quantity,
      0
    );
    const overcharge = result.items.reduce(
      (sum, i) => sum + (i.overcharge_amount || 0),
      0
    );
    const savings = result.items.reduce(
      (sum, i) => sum + (i.generic?.saving || 0),
      0
    );
    return { billed, overcharge, savings };
  }, [result]);

  if (!result?.items) return null;

  const handleAuditAnother = () => {
    reset();
    navigate("/upload");
  };

  const handleDownload = () => {
    toast.success("Preparing report for printing...");
    setTimeout(() => window.print(), 300);
  };

  return (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 py-8 sm:py-12">
      {/* Header */}
      <div className="mb-6 no-print">
        <h1 className="font-heading font-bold text-2xl sm:text-3xl text-ink mb-1">
          Audit <span className="gradient-text">Results</span>
        </h1>
        {result.bill_meta && (
          <p className="text-sm text-ink/50">
            {result.bill_meta.pharmacy_name} · {formatDate(result.bill_meta.bill_date)}
          </p>
        )}
      </div>

      {/* Print-only header */}
      <div className="hidden print:block mb-6">
        <h1 className="font-heading font-bold text-xl">MedBill Auditor — Report</h1>
        <p className="text-sm">
          {result.bill_meta?.pharmacy_name} · {formatDate(result.bill_meta?.bill_date)}
        </p>
      </div>

      {/* Summary */}
      <div className="mb-6">
        <SummaryCards
          totalBilled={totals.billed}
          totalOvercharge={totals.overcharge}
          totalSavings={totals.savings}
        />
      </div>

      {/* Disclaimer */}
      <div className="mb-6">
        <DisclaimerBanner />
      </div>

      {/* Audit table */}
      <div className="mb-6">
        <h2 className="font-heading font-bold text-lg text-ink mb-3">
          Audit Breakdown
        </h2>
        <AuditTable items={result.items} />
      </div>

      {/* Chart */}
      <div className="mb-6">
        <PriceChart items={result.items} />
      </div>

      {/* Generic alternatives */}
      <div className="mb-8">
        <GenericAlternatives items={result.items} />
      </div>

      {/* Actions */}
      <div className="flex flex-col sm:flex-row gap-3 no-print">
        <button
          onClick={handleDownload}
          className="inline-flex items-center justify-center gap-2 bg-white border border-pink-200 hover:border-primary text-ink font-semibold px-6 py-3 rounded-full transition-colors"
        >
          <Download size={18} /> Download report
        </button>
        <button
          onClick={handleAuditAnother}
          className="inline-flex items-center justify-center gap-2 bg-primary hover:bg-primary-hover text-white font-semibold px-6 py-3 rounded-full transition-all hover:shadow-md"
        >
          <RotateCcw size={18} /> Audit another bill
        </button>
      </div>
    </div>
  );
}
