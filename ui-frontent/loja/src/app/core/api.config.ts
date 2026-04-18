export const API_BASE = '/api';

export const API_ENDPOINTS = {
  // Public
  PRODUCTS: `${API_BASE}/products`,
  PRODUCT_CATEGORY: `${API_BASE}/product-category`,
  COUNTRIES: `${API_BASE}/countries`,
  STATES: `${API_BASE}/states`,

  // Auth
  AUTH: `${API_BASE}/auth`,
  AUTH_LOGIN: `${API_BASE}/auth/login`,
  AUTH_REGISTER: `${API_BASE}/auth/register`,

  // User
  USER: `${API_BASE}/user`,
  USER_FAVORITES: `${API_BASE}/user/favorites`,
  USER_NOTIFICATIONS: `${API_BASE}/user/notifications`,

  // Admin
  ADMIN: `${API_BASE}/admin`,
  ADMIN_PRODUCTS: `${API_BASE}/admin/products`,
  ADMIN_ORDERS: `${API_BASE}/admin/orders`,
  ADMIN_CUSTOMERS: `${API_BASE}/admin/customers`,
  ADMIN_DASHBOARD: `${API_BASE}/admin/dashboard`,
  ADMIN_ACTIVITY: `${API_BASE}/admin/activity`,

  // Checkout
  CHECKOUT: `${API_BASE}/checkout`,
  CHECKOUT_PURCHASE: `${API_BASE}/checkout/purchase`,

  // WebSocket (não é API REST)
  WS_NOTIFICATIONS: '/ws-notifications'
};

export const WS_URL =
  `${window.location.protocol === 'https:' ? 'wss' : 'ws'}://${window.location.host}${API_ENDPOINTS.WS_NOTIFICATIONS}`;