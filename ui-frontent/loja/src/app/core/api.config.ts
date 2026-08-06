const HOSTNAME = window.location.hostname;

const IS_LOOPBACK =
  HOSTNAME === 'localhost' ||
  HOSTNAME === '127.0.0.1';

const IS_PRIVATE_NETWORK =
  HOSTNAME.startsWith('192.168.') ||
  HOSTNAME.startsWith('10.') ||
  /^172\.(1[6-9]|2\d|3[0-1])\./.test(HOSTNAME);

const API_HOST = IS_LOOPBACK
  ? 'http://localhost:8080'
  : IS_PRIVATE_NETWORK
    ? `http://${HOSTNAME}:8080`
    : 'https://ecommerce-version-01.onrender.com';

export const API_BASE = `${API_HOST}/api`;

export const WS_URL = IS_LOOPBACK
  ? 'ws://localhost:8080/ws-notifications'
  : IS_PRIVATE_NETWORK
    ? `ws://${HOSTNAME}:8080/ws-notifications`
    : 'wss://ecommerce-version-01.onrender.com/ws-notifications';

export const API_ENDPOINTS = {
  PRODUCTS: `${API_BASE}/products`,
  PRODUCT_CATEGORY: `${API_BASE}/product-category`,
  COUNTRIES: `${API_BASE}/countries`,
  STATES: `${API_BASE}/states`,
  REVIEWS:`${API_BASE}/reviews`,

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

  PAYMENTS: `${API_BASE}/payments`,

  CHECKOUT: `${API_BASE}/checkout`,
  CHECKOUT_PURCHASE: `${API_BASE}/checkout/purchase`,

    WS_NOTIFICATIONS: WS_URL
};


