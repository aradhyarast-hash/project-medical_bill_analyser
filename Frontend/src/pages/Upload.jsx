import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import { Search, Pill, FileText } from "lucide-react";
import { useAudit } from "../context/AuditContext.jsx";
import { auditBill, searchMedicines } from "../services/api.js";
import DropZone from "../components/upload/DropZone.jsx";
import LoadingSteps from "../components/upload/LoadingSteps.jsx";

export default function Upload() {
  const navigate = useNavigate();
  const { file, setFile, setAuditResult, setExtractedItems, reset } = useAudit();
  const [error, setError] = useState(null);
  const [analyzing, setAnalyzing] = useState(false);
  const [medicineName, setMedicineName] = useState("");
  const [suggestions, setSuggestions] = useState([]);
  const [showSuggestions, setShowSuggestions] = useState(false);

  useEffect(() => {
    if (medicineName.trim().length >= 2) {
      searchMedicines(medicineName.trim()).then((data) => {
        setSuggestions(data || []);
      });
    } else {
      setSuggestions([]);
    }
  }, [medicineName]);

  const handleFileSelected = (f, err) => {
    if (err) {
      setError(err);
      setFile(null);
      return;
    }
    setError(null);
    setFile(f);
  };

  const handleRemove = () => {
    setFile(null);
    setError(null);
  };

  const handleAnalyze = async () => {
    if (!file && !medicineName.trim()) {
      setError("Please provide either a medicine name or upload a prescription/bill file.");
      return;
    }
    setAnalyzing(true);
    setError(null);
    try {
      const result = await auditBill(file, medicineName);
      setAuditResult(result);
      setExtractedItems(result.items);
      toast.success("Bill / medicine analyzed successfully!");
      navigate("/review");
    } catch (err) {
      setError(err.message || "Something went wrong. Please try again.");
      toast.error(err.message || "Failed to analyze.");
    } finally {
      setAnalyzing(false);
    }
  };

  if (analyzing) {
    return (
      <div className="max-w-xl mx-auto px-4 sm:px-6 py-16">
        <LoadingSteps />
      </div>
    );
  }

  const isSubmitDisabled = !file && !medicineName.trim();

  return (
    <div className="max-w-xl mx-auto px-4 sm:px-6 py-12">
      <div className="text-center mb-8">
        <h1 className="font-heading font-bold text-2xl sm:text-3xl text-ink mb-2">
          Analyze Medicine or Bill
        </h1>
        <p className="text-sm text-ink/60">
          Upload a prescription photo / PDF, or type a medicine name to find salt-matched cheaper alternatives.
        </p>
      </div>

      {/* Option A: Medicine Name Input with Autocomplete */}
      <div className="card p-5 mb-6">
        <label className="block text-xs font-bold text-ink/70 uppercase tracking-wider mb-2">
          Option 1: Search by Medicine Name
        </label>
        <div className="relative">
          <div className="flex items-center gap-2 border border-pink-200 rounded-xl px-3 py-2.5 focus-within:border-primary transition-colors bg-white">
            <Search size={18} className="text-ink/40" />
            <input
              type="text"
              value={medicineName}
              onChange={(e) => {
                setMedicineName(e.target.value);
                setShowSuggestions(true);
              }}
              onFocus={() => setShowSuggestions(true)}
              placeholder="e.g. Dolo 650, Calpol 500, Pan 40, Azee 500..."
              className="w-full text-sm outline-none text-ink placeholder:text-ink/30"
            />
            {medicineName && (
              <button
                type="button"
                onClick={() => setMedicineName("")}
                className="text-xs text-ink/40 hover:text-ink font-semibold"
              >
                Clear
              </button>
            )}
          </div>

          {/* Autocomplete dropdown */}
          {showSuggestions && suggestions.length > 0 && (
            <div className="absolute z-20 left-0 right-0 mt-1 bg-white border border-pink-100 rounded-xl shadow-lg max-h-56 overflow-y-auto">
              {suggestions.map((s) => (
                <div
                  key={s.id}
                  onClick={() => {
                    setMedicineName(s.brandName);
                    setShowSuggestions(false);
                  }}
                  className="px-4 py-2.5 hover:bg-pink-50/60 cursor-pointer border-b border-pink-50 last:border-0 flex items-center justify-between text-xs"
                >
                  <div>
                    <p className="font-semibold text-ink">{s.brandName}</p>
                    <p className="text-ink/50">{s.saltComposition}</p>
                  </div>
                  <span className="font-bold text-primary">₹{s.pricePerUnitInr}/unit</span>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      <div className="flex items-center gap-3 my-4">
        <div className="flex-1 h-px bg-pink-100" />
        <span className="text-xs font-semibold text-ink/40 uppercase">OR Upload File</span>
        <div className="flex-1 h-px bg-pink-100" />
      </div>

      {/* Option B: File Upload */}
      <DropZone
        file={file}
        onFileSelected={handleFileSelected}
        onRemove={handleRemove}
        error={error}
      />

      <div className="mt-6 flex flex-col sm:flex-row gap-3">
        <button
          onClick={handleAnalyze}
          disabled={isSubmitDisabled}
          className="flex-1 bg-primary hover:bg-primary-hover text-white font-semibold py-3 rounded-full transition-all disabled:opacity-40 disabled:cursor-not-allowed hover:shadow-md disabled:hover:shadow-none"
        >
          {file && medicineName.trim()
            ? "Analyze bill & medicine"
            : file
            ? "Analyze prescription / bill"
            : "Search medicine alternatives"}
        </button>
        <button
          onClick={() => {
            reset();
            setMedicineName("");
            navigate("/");
          }}
          className="text-ink/60 hover:text-primary font-medium py-3 px-6 transition-colors"
        >
          Cancel
        </button>
      </div>
    </div>
  );
}
