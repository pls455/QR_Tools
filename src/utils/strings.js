export function slugify(value) {
  return String(value || "")
    .trim().toLowerCase().normalize("NFKD")
    .replace(/[\u0300-\u036f]/g, "")
    .replace(/[^\p{L}\p{N}\s_-]/gu, "")
    .replace(/[\s_-]+/g, "-")
    .replace(/^-+|-+$/g, "")
    .slice(0, 80);
}

export function normalizeUrl(value) {
  try {
    const url = new URL(String(value || "").trim());
    url.hash = "";
    if (url.pathname.length > 1 && url.pathname.endsWith("/")) url.pathname = url.pathname.slice(0, -1);
    return url.toString().toLowerCase();
  } catch {
    return String(value || "").trim().replace(/\/+$/, "").toLowerCase();
  }
}

export const errorText = error => error?.code || error?.message || "حدث خطأ غير معروف.";
