import { addDoc, collection, serverTimestamp } from "https://www.gstatic.com/firebasejs/12.17.1/firebase-firestore.js";
import { auth, db } from "../core/firebase.js";

export async function writeAdminLog(action, collectionName, targetId, details = "", role = "") {
  try {
    await addDoc(collection(db, "adminLogs"), {
      action: String(action || ""),
      collection: String(collectionName || ""),
      targetId: String(targetId || ""),
      details: String(details || ""),
      adminUid: auth.currentUser?.uid || "",
      adminEmail: auth.currentUser?.email || "",
      role: role || "",
      createdAt: serverTimestamp()
    });
  } catch (error) {
    console.warn("[adminLogs] Failed:", error);
  }
}
