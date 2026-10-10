import { Link } from "react-router-dom";
import { Activity } from "lucide-react";

export default function Navbar() {
  return (
    <header className="sticky top-0 z-50 bg-white/80 backdrop-blur-md border-b border-pink-200 no-print">
      <nav className="max-w-6xl mx-auto px-4 sm:px-6 h-16 flex items-center justify-between">
        <Link to="/" className="flex items-center gap-2">
          <span className="flex items-center justify-center w-9 h-9 rounded-xl bg-primary text-white">
            <Activity size={20} />
          </span>
          <span className="font-heading font-bold text-lg text-ink">
            MedBill <span className="gradient-text">Auditor</span>
          </span>
        </Link>
        <div className="flex items-center gap-2 sm:gap-6">
          <Link
            to="/about"
            className="text-sm font-medium text-ink/70 hover:text-primary transition-colors"
          >
            About
          </Link>
          <Link
            to="/upload"
            className="bg-primary hover:bg-primary-hover text-white text-sm font-semibold px-4 py-2 rounded-full transition-all hover:shadow-md"
          >
            Audit a Bill
          </Link>
        </div>
      </nav>
    </header>
  );
}
