import { useCallback } from "react";
import { useDropzone } from "react-dropzone";
import { UploadCloud, FileText, ImageIcon, X, Camera } from "lucide-react";

const MAX_SIZE = 10 * 1024 * 1024; // 10 MB

export default function DropZone({ file, onFileSelected, onRemove, error }) {
  const onDrop = useCallback(
    (accepted, rejections) => {
      if (rejections.length > 0) {
        const firstErr = rejections[0]?.errors?.[0];
        if (firstErr?.code === "file-too-large") {
          onFileSelected(null, "File is larger than 10 MB");
        } else if (firstErr?.code === "file-invalid-type") {
          onFileSelected(null, "Only JPG, PNG, and PDF files are accepted");
        } else {
          onFileSelected(null, firstErr?.message || "Invalid file");
        }
        return;
      }
      onFileSelected(accepted[0] || null);
    },
    [onFileSelected]
  );

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: {
      "image/jpeg": [".jpg", ".jpeg"],
      "image/png": [".png"],
      "application/pdf": [".pdf"],
    },
    maxSize: MAX_SIZE,
    multiple: false,
    disabled: !!file,
  });

  const isImage = file && file.type.startsWith("image/");
  const isPdf = file && file.type === "application/pdf";
  const previewUrl = isImage ? URL.createObjectURL(file) : null;

  return (
    <div className="w-full">
      {!file && (
        <>
          <div
            {...getRootProps()}
            className={`card border-2 border-dashed cursor-pointer transition-all px-6 py-12 text-center ${
              isDragActive
                ? "border-primary bg-pink-50"
                : "border-pink-200 hover:border-primary hover:bg-pink-50/50"
            }`}
          >
            <input {...getInputProps()} />
            <UploadCloud
              size={40}
              className="mx-auto text-primary mb-3"
            />
            <p className="font-heading font-semibold text-ink mb-1">
              {isDragActive
                ? "Drop your bill here"
                : "Drag & drop your bill here"}
            </p>
            <p className="text-sm text-ink/50">
              JPG, PNG, or PDF — up to 10 MB
            </p>
          </div>

          {/* Mobile: take a photo directly */}
          <div className="mt-4 flex justify-center sm:hidden">
            <label className="inline-flex items-center gap-2 bg-white border border-pink-200 text-ink text-sm font-medium px-5 py-2.5 rounded-full hover:border-primary transition-colors cursor-pointer">
              <Camera size={18} className="text-primary" />
              Take a photo
              <input
                type="file"
                accept="image/*"
                capture="environment"
                className="hidden"
                onChange={(e) => {
                  const f = e.target.files?.[0];
                  if (f) onFileSelected(f);
                }}
              />
            </label>
          </div>
        </>
      )}

      {/* File preview */}
      {file && (
        <div className="card p-5">
          {isImage && (
            <div className="mb-4">
              <img
                src={previewUrl}
                alt="Uploaded bill preview"
                className="w-full max-h-64 object-contain rounded-xl bg-slate-50"
              />
            </div>
          )}
          {isPdf && (
            <div className="flex items-center gap-3 bg-pink-50 rounded-xl p-4 mb-4">
              <FileText size={32} className="text-primary shrink-0" />
              <div className="min-w-0">
                <p className="text-sm font-semibold text-ink truncate">
                  {file.name}
                </p>
                <p className="text-xs text-ink/50">
                  PDF · {(file.size / 1024).toFixed(0)} KB
                </p>
              </div>
            </div>
          )}
          {isImage && (
            <p className="text-xs text-ink/50 mb-4 flex items-center gap-1">
              <ImageIcon size={12} /> {file.name} · {(file.size / 1024).toFixed(0)} KB
            </p>
          )}
          <button
            onClick={onRemove}
            className="inline-flex items-center gap-1.5 text-sm font-medium text-red-600 hover:text-red-700 transition-colors"
          >
            <X size={16} /> Remove and upload a different file
          </button>
        </div>
      )}

      {error && (
        <p className="mt-3 text-sm text-red-600 font-medium">{error}</p>
      )}
    </div>
  );
}
