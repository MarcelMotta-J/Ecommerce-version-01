const API_HOST = 'https://ecommerce-version-01.onrender.com';

export const API_BASE = `${API_HOST}/api`;

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

   WS_NOTIFICATIONS: 'wss://ecommerce-version-01.onrender.com/ws-notifications'
};

export const WS_URL = API_ENDPOINTS.WS_NOTIFICATIONS;
