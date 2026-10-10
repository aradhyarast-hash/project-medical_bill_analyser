import { Link } from "react-router-dom";
import {
  ScanLine, BadgeCheck, Pill, ArrowRight, ShieldCheck, Building2,
} from "lucide-react";
import FAQAccordion from "../components/ui/FAQAccordion.jsx";

const STEPS = [
  {
    icon: ScanLine,
    title: "Read",
    desc: "We use OCR to read every medicine, strength, and price directly from your bill photo or PDF.",
  },
  {
    icon: BadgeCheck,
    title: "Audit",
    desc: "Each item is compared against NPPA ceiling prices and MRP to flag any overcharges.",
  },
  {
    icon: Pill,
    title: "Recommend",
    desc: "We find cheaper, verified generic alternatives from the Jan Aushadhi (PMBJP) catalog.",
  },
];

const FAQ_ITEMS = [
  {
    q: "Is this tool free to use?",
    a: "Yes. MedBill Auditor is a consumer-protection tool. You can audit any number of bills without paying.",
  },
  {
    q: "Where do the price references come from?",
    a: "Ceiling prices are sourced from the National Pharmaceutical Pricing Authority (NPPA). Generic medicine prices come from the Jan Aushadhi (PMBJP) scheme catalog.",
  },
  {
    q: "Can I use this to refuse payment at a pharmacy?",
    a: "The results are for your reference. If you find an overcharge, you can raise it with the pharmacist or file a complaint with NPPA. This tool does not constitute legal advice.",
  },
  {
    q: "Does the tool store my bill?",
    a: "No. Your bill is processed for the audit and not permanently stored. Each session is independent.",
  },
  {
    q: "Should I switch to the generic medicine it suggests?",
    a: "Always consult your doctor or pharmacist before switching. The tool identifies alternatives with the same active ingredient, but your doctor determines what is right for you.",
  },
];

export default function Landing() {
  return (
    <div>
      {/* Hero */}
      <section className="max-w-6xl mx-auto px-4 sm:px-6 pt-12 sm:pt-20 pb-16 text-center">
        <span className="inline-flex items-center gap-2 bg-white border border-pink-200 rounded-full px-4 py-1.5 text-sm text-ink/70 mb-6">
          <ShieldCheck size={16} className="text-primary" />
          Powered by NPPA &amp; Jan Aushadhi data
        </span>
        <h1 className="font-heading font-extrabold text-3xl sm:text-5xl text-ink leading-tight max-w-3xl mx-auto">
          Audit your <span className="gradient-text">medical bill</span> in seconds
        </h1>
        <p className="mt-5 text-base sm:text-lg text-ink/60 max-w-xl mx-auto leading-relaxed">
          Upload a pharmacy or hospital bill. We check every medicine against
          official ceiling prices and suggest cheaper generic alternatives.
        </p>
        <div className="mt-8 flex flex-col sm:flex-row items-center justify-center gap-3">
          <Link
            to="/upload"
            className="inline-flex items-center gap-2 bg-primary hover:bg-primary-hover text-white font-semibold px-6 py-3 rounded-full transition-all hover:shadow-lg"
          >
            Audit a Bill <ArrowRight size={18} />
          </Link>
          <Link
            to="/about"
            className="inline-flex items-center text-ink/70 hover:text-primary font-medium px-6 py-3 transition-colors"
          >
            How it works
          </Link>
        </div>
      </section>

      {/* How it works */}
      <section className="max-w-6xl mx-auto px-4 sm:px-6 py-12">
        <h2 className="font-heading font-bold text-2xl text-ink text-center mb-10">
          How it works
        </h2>
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-6">
          {STEPS.map((step, i) => {
            const Icon = step.icon;
            return (
              <div key={i} className="card p-6 text-center">
                <span className="inline-flex items-center justify-center w-12 h-12 rounded-xl bg-pink-50 text-primary mb-4">
                  <Icon size={24} />
                </span>
                <p className="text-xs font-bold text-primary mb-1">
                  Step {i + 1}
                </p>
                <h3 className="font-heading font-bold text-ink text-lg mb-2">
                  {step.title}
                </h3>
                <p className="text-sm text-ink/60 leading-relaxed">
                  {step.desc}
                </p>
              </div>
            );
          })}
        </div>
      </section>

      {/* Trust strip */}
      <section className="max-w-6xl mx-auto px-4 sm:px-6 py-8">
        <div className="card p-6 flex flex-col sm:flex-row items-center justify-around gap-6">
          <div className="flex items-center gap-3">
            <span className="flex items-center justify-center w-10 h-10 rounded-lg bg-pink-50 text-primary">
              <Building2 size={20} />
            </span>
            <div>
              <p className="font-heading font-semibold text-ink text-sm">NPPA</p>
              <p className="text-xs text-ink/50">Ceiling price registry</p>
            </div>
          </div>
          <div className="hidden sm:block w-px h-10 bg-pink-100" />
          <div className="flex items-center gap-3">
            <span className="flex items-center justify-center w-10 h-10 rounded-lg bg-purple-50 text-accent">
              <Pill size={20} />
            </span>
            <div>
              <p className="font-heading font-semibold text-ink text-sm">Jan Aushadhi (PMBJP)</p>
              <p className="text-xs text-ink/50">Generic medicine catalog</p>
            </div>
          </div>
        </div>
      </section>

      {/* FAQ */}
      <section className="max-w-3xl mx-auto px-4 sm:px-6 py-12">
        <h2 className="font-heading font-bold text-2xl text-ink text-center mb-8">
          Frequently asked questions
        </h2>
        <FAQAccordion items={FAQ_ITEMS} />
      </section>
    </div>
  );
}
