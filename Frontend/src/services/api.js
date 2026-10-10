import axios from "axios";
import mockResponse from "../mock/sampleResponse.json";

const BASE_URL = import.meta.env.VITE_API_URL || "";
const USE_MOCK = import.meta.env.VITE_USE_MOCK !== "false"; // default true

// Simulate network delay for mock mode
const delay = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

/**
 * Audit a bill file.
 * - Mock mode: returns sampleResponse.json after 1.5s delay
 * - Live mode: POST multipart FormData to {BASE_URL}/api/audit
 */
export async function auditBill(file, onProgress) {
  if (USE_MOCK) {
    await delay(1500);
    if (onProgress) onProgress(100);
    return mockResponse;
  }

  const formData = new FormData();
  formData.append("bill", file);

  const { data } = await axios.post(`${BASE_URL}/api/audit`, formData, {
    headers: { "Content-Type": "multipart/form-data" },
    onUploadProgress: (e) => {
      if (onProgress && e.total) {
        onProgress(Math.round((e.loaded / e.total) * 100));
      }
    },
  });

  return data;
}
