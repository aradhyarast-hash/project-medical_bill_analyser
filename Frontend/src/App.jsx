import { Routes, Route } from "react-router-dom";
import Layout from "./components/layout/Layout.jsx";
import Landing from "./pages/Landing.jsx";
import Upload from "./pages/Upload.jsx";
import Review from "./pages/Review.jsx";
import Results from "./pages/Results.jsx";
import About from "./pages/About.jsx";
import NotFound from "./pages/NotFound.jsx";

export default function App() {
  return (
    <Layout>
      <Routes>
        <Route path="/" element={<Landing />} />
        <Route path="/upload" element={<Upload />} />
        <Route path="/review" element={<Review />} />
        <Route path="/results" element={<Results />} />
        <Route path="/about" element={<About />} />
        <Route path="*" element={<NotFound />} />
      </Routes>
    </Layout>
  );
}
