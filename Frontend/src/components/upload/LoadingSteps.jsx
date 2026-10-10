import { useEffect, useState } from "react";
import { ScanLine, BadgeCheck, Pill } from "lucide-react";

// Shows rotating step-by-step progress text while the audit runs
const STEPS = [
  { icon: ScanLine, text: "Reading bill with OCR..." },
  { icon: BadgeCheck, text: "Verifying prices against NPPA data..." },
  { icon: Pill, text: "Finding generic alternatives..." },
];

export default function LoadingSteps() {
  const [step, setStep] = useState(0);

  useEffect(() => {
    const interval = setInterval(() => {
      setStep((s) => (s < STEPS.length - 1 ? s + 1 : s));
    }, 500);
    return () => clearInterval(interval);
  }, []);

  return (
    <div className="card p-8 text-center">
      <div className="flex flex-col items-center gap-4">
        <div className="relative w-16 h-16">
          <div className="absolute inset-0 rounded-full border-4 border-pink-200" />
          <div className="absolute inset-0 rounded-full border-4 border-primary border-t-transparent animate-spin" />
        </div>
        <div className="flex flex-col gap-3 mt-2 w-full max-w-xs">
          {STEPS.map((s, i) => {
            const Icon = s.icon;
            const done = i < step;
            const active = i === step;
            return (
              <div
                key={i}
                className={`flex items-center gap-3 text-sm transition-opacity duration-300 ${
                  done || active ? "opacity-100" : "opacity-40"
                }`}
              >
                <Icon
                  size={18}
                  className={active ? "text-primary animate-pulse" : done ? "text-green-500" : "text-ink/40"}
                />
                <span className={active ? "font-semibold text-ink" : "text-ink/60"}>
                  {s.text}
                </span>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}
