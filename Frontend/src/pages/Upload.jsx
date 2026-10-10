import { useState } from "react";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import { useAudit } from "../context/AuditContext.jsx";
import { auditBill } from "../services/api.js";
import DropZone from "../components/upload/DropZone.jsx";
import LoadingSteps from "../components/upload/LoadingSteps.jsx";

export default function Upload() {
  const navigate = useNavigate();
  const { file, setFile, setAuditResult, setExtractedItems, reset } = useAudit();
  const [error, setError] = useState(null);
  const [analyzing, setAnalyzing] = useState(false);

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
    if (!file) return;
    setAnalyzing(true);
    setError(null);
    try {
      const result = await auditBill(file);
      setAuditResult(result);
      setExtractedItems(result.items);
      toast.success("Bill analyzed successfully!");
      navigate("/review");
    } catch (err) {
      setError(err.message || "Something went wrong. Please try again.");
      toast.error("Failed to analyze bill.");
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

  return (
    <div className="max-w-xl mx-auto px-4 sm:px-6 py-12">
      <div className="text-center mb-8">
        <h1 className="font-heading font-bold text-2xl sm:text-3xl text-ink mb-2">
          Upload your bill
        </h1>
        <p className="text-sm text-ink/60">
          Take a clear photo or upload a PDF of your medical or pharmacy bill.
        </p>
      </div>

      <DropZone
        file={file}
        onFileSelected={handleFileSelected}
        onRemove={handleRemove}
        error={error}
      />

      <div className="mt-6 flex flex-col sm:flex-row gap-3">
        <button
          onClick={handleAnalyze}
          disabled={!file}
          className="flex-1 bg-primary hover:bg-primary-hover text-white font-semibold py-3 rounded-full transition-all disabled:opacity-40 disabled:cursor-not-allowed hover:shadow-md disabled:hover:shadow-none"
        >
          Analyze bill
        </button>
        <button
          onClick={() => {
            reset();
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
