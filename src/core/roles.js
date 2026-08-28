export const ROLE_LEVEL = Object.freeze({
  reviewer: 1,
  content_admin: 2,
  superadmin: 3,
  super_admin: 3,
  admin: 3
});

export function can(role, requiredRole) {
  return (ROLE_LEVEL[role] || 0) >= (ROLE_LEVEL[requiredRole] || 0);
}
