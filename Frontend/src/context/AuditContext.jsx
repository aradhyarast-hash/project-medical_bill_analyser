import { createContext, useContext, useState, useEffect, useCallback } from "react";

const AuditContext = createContext(null);

export function useAudit() {
  const ctx = useContext(AuditContext);
  if (!ctx) throw new Error("useAudit must be used within AuditProvider");
  return ctx;
}

// Key for sessionStorage persistence
const RESULT_KEY = "medbill_audit_result";
const ITEMS_KEY = "medbill_extracted_items";

export function AuditProvider({ children }) {
  const [file, setFile] = useState(null);
  const [items, setItems] = useState([]);
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Restore result from sessionStorage on mount so refresh doesn't lose data
  useEffect(() => {
    const saved = sessionStorage.getItem(RESULT_KEY);
    if (saved) {
      try {
        setResult(JSON.parse(saved));
      } catch {
        sessionStorage.removeItem(RESULT_KEY);
      }
    }
    const savedItems = sessionStorage.getItem(ITEMS_KEY);
    if (savedItems) {
      try {
        setItems(JSON.parse(savedItems));
      } catch {
        sessionStorage.removeItem(ITEMS_KEY);
      }
    }
  }, []);

  const setAuditResult = useCallback((data) => {
    setResult(data);
    sessionStorage.setItem(RESULT_KEY, JSON.stringify(data));
  }, []);

  const setExtractedItems = useCallback((data) => {
    setItems(data);
    sessionStorage.setItem(ITEMS_KEY, JSON.stringify(data));
  }, []);

  const reset = useCallback(() => {
    setFile(null);
    setItems([]);
    setResult(null);
    setLoading(false);
    setError(null);
    sessionStorage.removeItem(RESULT_KEY);
    sessionStorage.removeItem(ITEMS_KEY);
  }, []);

  const value = {
    file,
    setFile,
    items,
    setExtractedItems,
    result,
    setAuditResult,
    loading,
    setLoading,
    error,
    setError,
    reset,
  };

  return <AuditContext.Provider value={value}>{children}</AuditContext.Provider>;
}
