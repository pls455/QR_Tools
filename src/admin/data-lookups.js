import { cache } from "./state.js";
import { toArray } from "../utils/dom.js";

export const findById = (name, id) => cache(name).find(item => item.id === id || item.stableId === id);
export const branchName = id => findById("branches", id)?.name || id || "غير محدد";
export const categoryName = id => findById("categories", id)?.name || id || "غير مصنف";
export const subjectName = id => findById("subjects", id)?.name || id || "غير محدد";
export const branchesOfSubject = subject => subject ? (toArray(subject.branchIds).length ? toArray(subject.branchIds) : toArray(subject.branchId)) : [];
export const branchesOf = item => item ? (toArray(item.branchIds).length ? toArray(item.branchIds) : toArray(item.branchId)) : [];
