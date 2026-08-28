import { onAuthStateChanged, signInWithEmailAndPassword, signOut } from "https://www.gstatic.com/firebasejs/12.17.1/firebase-auth.js";
import { auth } from "../core/firebase.js";
import { state } from "./state.js";

export function watchAuth(callback) {
  return onAuthStateChanged(auth, user => callback(user, state));
}
export function login(email, password) { return signInWithEmailAndPassword(auth, email, password); }
export function logout() { return signOut(auth); }
