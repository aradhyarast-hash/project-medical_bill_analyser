import { Link } from "react-router-dom";
import { Activity, ShieldCheck } from "lucide-react";

export default function Footer() {
  return (
    <footer className="mt-16 border-t border-pink-200 bg-white/60 no-print">
      <div className="max-w-6xl mx-auto px-4 sm:px-6 py-10">
        <div className="flex flex-col sm:flex-row justify-between gap-6">
          <div className="max-w-sm">
            <div className="flex items-center gap-2 mb-3">
              <span className="flex items-center justify-center w-8 h-8 rounded-lg bg-primary text-white">
                <Activity size={18} />
              </span>
              <span className="font-heading font-bold text-ink">
                MedBill Auditor
              </span>
            </div>
            <p className="text-sm text-ink/60 leading-relaxed">
              A consumer-protection tool that helps you check medical bills for
              overcharges and find cheaper generic alternatives.
            </p>
          </div>
          <div className="flex flex-col gap-2">
            <p className="text-sm font-semibold text-ink mb-1">Navigate</p>
            <Link to="/" className="text-sm text-ink/60 hover:text-primary transition-colors">Home</Link>
            <Link to="/upload" className="text-sm text-ink/60 hover:text-primary transition-colors">Upload Bill</Link>
            <Link to="/about" className="text-sm text-ink/60 hover:text-primary transition-colors">About &amp; Methodology</Link>
          </div>
        </div>
        <div className="mt-8 pt-6 border-t border-pink-100 flex flex-col sm:flex-row items-center justify-between gap-3">
          <p className="text-xs text-ink/50">
            For reference only. Not a substitute for professional medical advice.
          </p>
          <div className="flex items-center gap-1.5 text-xs text-ink/50">
            <ShieldCheck size={14} />
            <span>Data: NPPA &amp; Jan Aushadhi (PMBJP)</span>
          </div>
        </div>
      </div>
    </footer>
  );
}
