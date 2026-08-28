export const $ = (selector, root = document) => root.querySelector(selector);
export const $$ = (selector, root = document) => [...root.querySelectorAll(selector)];

export const escapeHtml = value => String(value ?? "")
  .replaceAll("&", "&amp;")
  .replaceAll("<", "&lt;")
  .replaceAll(">", "&gt;")
  .replaceAll('"', "&quot;")
  .replaceAll("'", "&#039;");

export const toArray = value => Array.isArray(value) ? value : (value ? [value] : []);

export function setMessage(element, text, error = false) {
  if (!element) return;
  element.textContent = text || "";
  element.className = error ? "message error" : "message success";
}
