const IS_LOCALHOST =
  window.location.hostname === 'localhost' ||
  window.location.hostname === '127.0.0.1';

export const API_BASE = IS_LOCALHOST
  ? 'https://ecommerce-version-01.onrender.com/api'
  : '/api';

export const API_ENDPOINTS = {
  PRODUCTS: `${API_BASE}/products`,
  PRODUCT_CATEGORY: `${API_BASE}/product-category`,
  COUNTRIES: `${API_BASE}/countries`,
  STATES: `${API_BASE}/states`,

  AUTH: `${API_BASE}/auth`,
  AUTH_LOGIN: `${API_BASE}/auth/login`,
  AUTH_REGISTER: `${API_BASE}/auth/register`,

  USER: `${API_BASE}/user`,
  USER_FAVORITES: `${API_BASE}/user/favorites`,
  USER_NOTIFICATIONS: `${API_BASE}/user/notifications`,

  ADMIN: `${API_BASE}/admin`,
  ADMIN_PRODUCTS: `${API_BASE}/admin/products`,
  ADMIN_ORDERS: `${API_BASE}/admin/orders`,
  ADMIN_CUSTOMERS: `${API_BASE}/admin/customers`,
  ADMIN_DASHBOARD: `${API_BASE}/admin/dashboard`,
  ADMIN_ACTIVITY: `${API_BASE}/admin/activity`,

  CHECKOUT: `${API_BASE}/checkout`,
  CHECKOUT_PURCHASE: `${API_BASE}/checkout/purchase`,

  WS_NOTIFICATIONS: IS_LOCALHOST
    ? 'wss://ecommerce-version-01.onrender.com/ws-notifications'
    : '/ws-notifications'
};

export const WS_URL = IS_LOCALHOST
  ? API_ENDPOINTS.WS_NOTIFICATIONS
  : `${window.location.protocol === 'https:' ? 'wss' : 'ws'}://${window.location.host}${API_ENDPOINTS.WS_NOTIFICATIONS}`;
