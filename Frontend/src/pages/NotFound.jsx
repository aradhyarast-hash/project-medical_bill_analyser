import { Link } from "react-router-dom";
import { Compass } from "lucide-react";

export default function NotFound() {
  return (
    <div className="max-w-xl mx-auto px-4 sm:px-6 py-24 text-center">
      <Compass size={56} className="mx-auto text-primary mb-6" />
      <h1 className="font-heading font-bold text-3xl text-ink mb-3">
        Page not found
      </h1>
      <p className="text-sm text-ink/60 mb-8">
        The page you're looking for doesn't exist or has moved.
      </p>
      <Link
        to="/"
        className="inline-flex items-center gap-2 bg-primary hover:bg-primary-hover text-white font-semibold px-6 py-3 rounded-full transition-all hover:shadow-md"
      >
        Back to home
      </Link>
    </div>
  );
}
