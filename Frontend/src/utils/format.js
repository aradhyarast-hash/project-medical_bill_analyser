// Format a number as Indian Rupees
const inrFormatter = new Intl.NumberFormat("en-IN", {
  style: "currency",
  currency: "INR",
  maximumFractionDigits: 2,
});

export function formatINR(amount) {
  if (amount === null || amount === undefined) return "—";
  return inrFormatter.format(amount);
}

// Format a date string (YYYY-MM-DD) to readable Indian format
export function formatDate(dateStr) {
  if (!dateStr) return "—";
  const d = new Date(dateStr);
  return d.toLocaleDateString("en-IN", {
    day: "numeric",
    month: "short",
    year: "numeric",
  });
}
