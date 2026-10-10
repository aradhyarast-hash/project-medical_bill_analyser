import { Stethoscope } from "lucide-react";

// Non-dismissible safety disclaimer
export default function DisclaimerBanner() {
  return (
    <div className="flex items-start gap-3 bg-amber-50 border border-amber-200 rounded-xl px-5 py-4">
      <Stethoscope size={22} className="text-amber-600 shrink-0 mt-0.5" />
      <p className="text-sm text-amber-800 leading-relaxed">
        Prices are for reference. Consult your doctor or pharmacist before
        switching any medicine. This tool does not give medical advice.
      </p>
    </div>
  );
}
