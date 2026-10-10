import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import { Toaster } from "react-hot-toast";
import "./index.css";
import App from "./App.jsx";
import { AuditProvider } from "./context/AuditContext.jsx";

ReactDOM.createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    <BrowserRouter>
      <AuditProvider>
        <App />
        <Toaster
          position="top-center"
          toastOptions={{
            duration: 3500,
            style: {
              borderRadius: "12px",
              fontFamily: "Inter, system-ui, sans-serif",
              fontSize: "14px",
            },
          }}
        />
      </AuditProvider>
    </BrowserRouter>
  </React.StrictMode>
);
