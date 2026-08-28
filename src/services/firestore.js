import { collection, doc, getDocs, getDoc, query, where, limit, addDoc, setDoc, updateDoc, deleteDoc, writeBatch, serverTimestamp } from "https://www.gstatic.com/firebasejs/12.17.1/firebase-firestore.js";
import { db } from "../core/firebase.js";

export async function listCollection(name, max = 500) {
  const snapshot = await getDocs(query(collection(db, name), limit(max)));
  return snapshot.docs.map(item => ({ id: item.id, ...item.data() }));
}

export async function safeListCollection(name, max = 500) {
  try { return { ok: true, data: await listCollection(name, max), error: null }; }
  catch (error) { console.error(`[Firestore] ${name}:`, error); return { ok: false, data: [], error }; }
}

export async function getDocument(name, id) {
  const snapshot = await getDoc(doc(db, name, id));
  return snapshot.exists() ? { id: snapshot.id, ...snapshot.data() } : null;
}

export async function createDocument(name, data) {
  const ref = await addDoc(collection(db, name), { ...data, createdAt: serverTimestamp(), updatedAt: serverTimestamp() });
  return ref.id;
}

export async function updateDocument(name, id, data) {
  await updateDoc(doc(db, name, id), { ...data, updatedAt: serverTimestamp() });
}

export async function replaceDocument(name, id, data) {
  await setDoc(doc(db, name, id), { ...data, updatedAt: serverTimestamp() }, { merge: true });
}

export async function removeDocument(name, id) { await deleteDoc(doc(db, name, id)); }

export async function pendingSuggestions(max = 300) {
  const snapshot = await getDocs(query(collection(db, "suggestions"), where("status", "==", "pending"), limit(max)));
  return snapshot.docs.map(item => ({ id: item.id, ...item.data() }));
}

export { db, collection, doc, writeBatch, serverTimestamp };
