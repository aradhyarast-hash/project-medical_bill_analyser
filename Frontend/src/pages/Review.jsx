import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import { ArrowLeft, ArrowRight, FileSearch } from "lucide-react";
import { useAudit } from "../context/AuditContext.jsx";
import ReviewTable from "../components/review/ReviewTable.jsx";

export default function Review() {
  const navigate = useNavigate();
  const { items, setExtractedItems, setAuditResult, result, reset } = useAudit();

  // No items to review — redirect to upload
  if (items.length === 0) {
    return (
      <div className="max-w-xl mx-auto px-4 sm:px-6 py-16 text-center">
        <FileSearch size={48} className="mx-auto text-ink/20 mb-4" />
        <h1 className="font-heading font-bold text-xl text-ink mb-2">
          Nothing to review yet
        </h1>
        <p className="text-sm text-ink/60 mb-6">
          Upload a bill first so we can extract the medicines for you to verify.
        </p>
        <button
          onClick={() => navigate("/upload")}
          className="bg-primary hover:bg-primary-hover text-white font-semibold px-6 py-3 rounded-full transition-all"
        >
          Go to upload
        </button>
      </div>
    );
  }

  const handleItemChange = (id, field, value) => {
    const updated = items.map((i) =>
      i.id === id ? { ...i, [field]: value } : i
    );
    setExtractedItems(updated);
  };

  const handleItemDelete = (id) => {
    setExtractedItems(items.filter((i) => i.id !== id));
  };

  const handleItemAdd = () => {
    const newItem = {
      id: `new-${Date.now()}`,
      extracted_name: "",
      matched_name: "",
      strength: "",
      form: "Tablet",
      quantity: 1,
      billed_unit_price: 0,
      mrp_unit_price: null,
      ceiling_unit_price: null,
      is_scheduled: false,
      status: "NO_REFERENCE",
      overcharge_amount: 0,
      confidence: 1.0,
      generic: null,
    };
    setExtractedItems([...items, newItem]);
  };

  const handleConfirm = () => {
    // Re-run audit with edited items (mock mode just re-uses the response)
    if (result) {
      const updatedResult = { ...result, items };
      setAuditResult(updatedResult);
    }
    toast.success("Items confirmed. Running audit...");
    navigate("/results");
  };

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 py-12">
      <div className="mb-6">
        <h1 className="font-heading font-bold text-2xl sm:text-3xl text-ink mb-2">
          Review extracted items
        </h1>
        <p className="text-sm text-ink/60">
          Check the medicines we found. Items highlighted in amber need your
          attention — please verify the price before continuing.
        </p>
      </div>

      <ReviewTable
        items={items}
        onItemChange={handleItemChange}
        onItemDelete={handleItemDelete}
        onItemAdd={handleItemAdd}
      />

      <div className="mt-6 flex flex-col sm:flex-row gap-3">
        <button
          onClick={() => navigate("/upload")}
          className="inline-flex items-center justify-center gap-2 text-ink/70 hover:text-primary font-medium px-6 py-3 rounded-full border border-pink-200 hover:border-primary transition-colors"
        >
          <ArrowLeft size={18} /> Back
        </button>
        <button
          onClick={handleConfirm}
          className="flex-1 inline-flex items-center justify-center gap-2 bg-primary hover:bg-primary-hover text-white font-semibold py-3 rounded-full transition-all hover:shadow-md"
        >
          Confirm and audit <ArrowRight size={18} />
        </button>
      </div>
    </div>
  );
}
