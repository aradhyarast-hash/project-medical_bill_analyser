import axios from "axios";
import mockResponse from "../mock/sampleResponse.json";

const BASE_URL = import.meta.env.VITE_API_BASE_URL || import.meta.env.VITE_API_URL || "http://localhost:8085";
const USE_MOCK = import.meta.env.VITE_USE_MOCK === "true"; // default false (live mode)

// Simulate network delay for mock mode
const delay = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

/**
 * Audit a bill file or analyze a prescribed medicine.
 * - Live mode: POST multipart FormData to {BASE_URL}/api/audit
 * - Mock mode: returns sampleResponse.json after 1.5s delay
 */
export async function auditBill(file, medicineName = "", onProgress) {
  if (USE_MOCK) {
    await delay(1500);
    if (onProgress) onProgress(100);
    return mockResponse;
  }

  const formData = new FormData();
  if (file) {
    formData.append("bill", file);
  }
  if (medicineName && medicineName.trim()) {
    formData.append("medicineName", medicineName.trim());
  }

  try {
    const { data } = await axios.post(`${BASE_URL}/api/audit`, formData, {
      headers: { "Content-Type": "multipart/form-data" },
      onUploadProgress: (e) => {
        if (onProgress && e.total) {
          onProgress(Math.round((e.loaded / e.total) * 100));
        }
      },
    });
    return data;
  } catch (err) {
    const msg = err.response?.data?.message || err.message || "Failed to analyze bill.";
    throw new Error(msg);
  }
}

/**
 * Search medicines for autocomplete.
 */
export async function searchMedicines(query) {
  if (USE_MOCK) {
    return [];
  }
  try {
    const { data } = await axios.get(`${BASE_URL}/api/medicines/search`, {
      params: { q: query },
    });
    return data;
  } catch {
    return [];
  }
}

/**
 * Health check endpoint.
 */
export async function getHealth() {
  try {
    const { data } = await axios.get(`${BASE_URL}/api/health`);
    return data;
  } catch (err) {
    return { status: "DOWN", message: err.message };
  }
}
