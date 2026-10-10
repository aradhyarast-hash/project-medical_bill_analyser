import { Link } from "react-router-dom";
import {
  Database, Cpu, ShieldAlert, Users, ArrowRight, AlertCircle,
} from "lucide-react";

const TEAM = [
  { name: "Team Member 1", role: "Product & Research" },
  { name: "Team Member 2", role: "Backend & OCR" },
  { name: "Team Member 3", role: "Frontend & Design" },
];

export default function About() {
  return (
    <div className="max-w-3xl mx-auto px-4 sm:px-6 py-12">
      <h1 className="font-heading font-bold text-2xl sm:text-3xl text-ink mb-2">
        About &amp; Methodology
      </h1>
      <p className="text-sm text-ink/60 mb-10">
        How MedBill Auditor works, what data it uses, and its limitations.
      </p>

      {/* Data sources */}
      <section className="mb-10">
        <div className="flex items-center gap-2 mb-4">
          <Database size={22} className="text-primary" />
          <h2 className="font-heading font-bold text-lg text-ink">Data Sources</h2>
        </div>
        <ul className="space-y-3 text-sm text-ink/70 leading-relaxed">
          <li className="card p-4">
            <p className="font-semibold text-ink mb-1">NPPA — National Pharmaceutical Pricing Authority</p>
            <p>Ceiling prices for scheduled formulations. Used to detect when a pharmacy charges above the legal maximum.</p>
          </li>
          <li className="card p-4">
            <p className="font-semibold text-ink mb-1">Jan Aushadhi (PMBJP)</p>
            <p>Pradhan Mantri Bhartiya Janaushadhi Pariyojana catalog of generic medicines with subsidized prices for verified alternatives.</p>
          </li>
        </ul>
      </section>

      {/* How matching works */}
      <section className="mb-10">
        <div className="flex items-center gap-2 mb-4">
          <Cpu size={22} className="text-primary" />
          <h2 className="font-heading font-bold text-lg text-ink">How Matching Works</h2>
        </div>
        <div className="card p-5 text-sm text-ink/70 leading-relaxed space-y-3">
          <p>1. <strong>OCR extraction:</strong> The bill image or PDF is processed to read medicine names, strengths, quantities, and prices.</p>
          <p>2. <strong>Name normalization:</strong> Extracted names are matched to a canonical drug database using fuzzy matching on active ingredient and brand name.</p>
          <p>3. <strong>Price audit:</strong> Each item's billed unit price is compared against the NPPA ceiling price and the listed MRP. Items above either threshold are flagged.</p>
          <p>4. <strong>Generic lookup:</strong> For each matched medicine, the Jan Aushadhi catalog is searched for a generic with the same active ingredient, strength, and dosage form.</p>
        </div>
      </section>

      {/* Safety rule */}
      <section className="mb-10">
        <div className="flex items-center gap-2 mb-4">
          <ShieldAlert size={22} className="text-primary" />
          <h2 className="font-heading font-bold text-lg text-ink">Safety Rule</h2>
        </div>
        <div className="card p-5 text-sm text-ink/70 leading-relaxed">
          <p>
            A generic alternative is shown as a <span className="text-green-700 font-semibold">"Verified match"</span> only
            when the active ingredient, strength, and dosage form all match the
            original medicine. If any of these differ, the alternative is shown
            with an amber warning and is <strong>not</strong> presented as a recommendation.
          </p>
        </div>
      </section>

      {/* Limitations */}
      <section className="mb-10">
        <div className="flex items-center gap-2 mb-4">
          <AlertCircle size={22} className="text-primary" />
          <h2 className="font-heading font-bold text-lg text-ink">Limitations</h2>
        </div>
        <div className="card p-5 text-sm text-ink/70 leading-relaxed">
          <ul className="space-y-2 list-disc list-inside">
            <li>OCR accuracy depends on bill image quality. Low-confidence items need manual verification.</li>
            <li>Price data may not cover all brands or newer formulations.</li>
            <li>Some medicines may not have a generic equivalent in the Jan Aushadhi catalog.</li>
            <li>This tool does not provide medical advice. Always consult a qualified doctor or pharmacist.</li>
          </ul>
        </div>
      </section>

      {/* Team */}
      <section className="mb-10">
        <div className="flex items-center gap-2 mb-4">
          <Users size={22} className="text-primary" />
          <h2 className="font-heading font-bold text-lg text-ink">Team</h2>
        </div>
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          {TEAM.map((m, i) => (
            <div key={i} className="card p-5 text-center">
              <div className="w-14 h-14 rounded-full bg-gradient-to-br from-pink-100 to-purple-100 mx-auto mb-3 flex items-center justify-center text-primary font-heading font-bold text-lg">
                {m.name.split(" ").map((w) => w[0]).join("").slice(0, 2)}
              </div>
              <p className="font-semibold text-ink text-sm">{m.name}</p>
              <p className="text-xs text-ink/50">{m.role}</p>
            </div>
          ))}
        </div>
      </section>

      {/* Disclaimer */}
      <div className="flex items-start gap-3 bg-amber-50 border border-amber-200 rounded-xl px-5 py-4 mb-10">
        <AlertCircle size={20} className="text-amber-600 shrink-0 mt-0.5" />
        <p className="text-sm text-amber-800 leading-relaxed">
          MedBill Auditor is a consumer-awareness tool. It does not give medical
          advice and is not a substitute for professional consultation. Always
          consult your doctor or pharmacist before making any changes to your
          medication.
        </p>
      </div>

      <div className="text-center">
        <Link
          to="/upload"
          className="inline-flex items-center gap-2 bg-primary hover:bg-primary-hover text-white font-semibold px-6 py-3 rounded-full transition-all hover:shadow-md"
        >
          Audit a Bill <ArrowRight size={18} />
        </Link>
      </div>
    </div>
  );
}
