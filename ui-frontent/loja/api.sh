#!/bin/bash

API="http://localhost:8080"

login_admin () {
TOKEN=$(curl -s -X POST $API/api/auth/login \
-H "Content-Type: application/json" \
-d '{"email":"admin@test.com","password":"123456"}' \
| sed -n 's/.*"token":"\([^"]*\)".*/\1/p')

export TOKEN
echo "Admin token loaded"
}

login_user () {
TOKEN=$(curl -s -X POST $API/api/auth/login \
-H "Content-Type: application/json" \
-d '{"email":"test@test.com","password":"123456"}' \
| sed -n 's/.*"token":"\([^"]*\)".*/\1/p')

export TOKEN
echo "User token loaded"
}

admin_users () {
curl -s $API/api/admin/users \
-H "Authorization: Bearer $TOKEN" | jq
}

admin_orders () {
curl -s $API/api/admin/orders \
-H "Authorization: Bearer $TOKEN" | jq
}

admin_order () {
curl -s $API/api/admin/orders/$1 \
-H "Authorization: Bearer $TOKEN" | jq
}

update_order_status () {
curl -X PATCH $API/api/admin/orders/$1/status \
-H "Authorization: Bearer $TOKEN" \
-H "Content-Type: application/json" \
-d "{\"status\":\"$2\"}"
}

# show specific product
admin_product () {
curl -s $API/api/admin/products/$1 \
  -H "Authorization: Bearer $TOKEN" | jq
}

# show all products
admin_products () {
curl -s $API/api/admin/products \
  -H "Authorization: Bearer $TOKEN" | jq
}


# delete a specific product
admin_delete_product () {
curl -X DELETE $API/api/admin/products/$1 \
  -H "Authorization: Bearer $TOKEN"
  echo
}

# Update stock
admin_stock () {
curl -X PATCH "$API/api/admin/products/$1/stock?unitsInStock=$2" \
  -H "Authorization: Bearer $TOKEN"
  echo
}

# Enable / disable product
admin_active () {
curl -X PATCH "$API/api/admin/products/$1/active?active=$2" \
  -H "Authorization: Bearer $TOKEN"
  echo
}

# paged products
admin_products_page () {
curl -s "$API/api/admin/products?page=$1&size=${2:-10}" \
  -H "Authorization: Bearer $TOKEN" | jq
}

# products by category
admin_products_category () {
curl -s "$API/api/admin/products?categoryId=$1&page=${2:-0}&size=${3:-10}" \
  -H "Authorization: Bearer $TOKEN" | jq
}

# products by active flag
admin_products_active () {
curl -s "$API/api/admin/products?active=$1&page=${2:-0}&size=${3:-10}" \
  -H "Authorization: Bearer $TOKEN" | jq
}

# products by name
admin_products_search () {
curl -s "$API/api/admin/products?name=$1&page=${2:-0}&size=${3:-10}" \
  -H "Authorization: Bearer $TOKEN" | jq
}

# combined filter
admin_products_filter () {
curl -s "$API/api/admin/products?categoryId=$1&active=$2&name=$3&page=${4:-0}&size=${5:-10}" \
  -H "Authorization: Bearer $TOKEN" | jq
}

# paged orders
admin_orders_page () {
curl -s "$API/api/admin/orders?page=$1&size=${2:-10}" \
  -H "Authorization: Bearer $TOKEN" | jq
}

# orders by status
admin_orders_status () {
curl -s "$API/api/admin/orders?status=$1&page=${2:-0}&size=${3:-10}" \
  -H "Authorization: Bearer $TOKEN" | jq
}

# orders by customer email
admin_orders_customer () {
curl -s "$API/api/admin/orders?customerEmail=$1&page=${2:-0}&size=${3:-10}" \
  -H "Authorization: Bearer $TOKEN" | jq
}

# orders by customer email
admin_orders_customer () {
curl -s "$API/api/admin/orders?customerEmail=$1&page=${2:-0}&size=${3:-10}" \
  -H "Authorization: Bearer $TOKEN" | jq
}

# combined order filters
admin_orders_filter () {
curl -s "$API/api/admin/orders?status=$1&customerEmail=$2&page=${3:-0}&size=${4:-10}" \
  -H "Authorization: Bearer $TOKEN" | jq
}

# show one admin order detail
admin_order () {
  curl -s "$API/api/admin/orders/$1" \
    -H "Authorization: Bearer $TOKEN" | jq
}

# admin dashboard backend with totalOrders, totalOrders, pendingOrders, totalProducts
admin_dashboard () {
  curl -s "$API/api/admin/dashboard" \
    -H "Authorization: Bearer $TOKEN" | jq
}


# export admin activity as txt
aadmin_activity_txt () {
  if [ -z "$TOKEN" ]; then
    echo "TOKEN is empty. Run login_admin first."
    return 1
  fi

  curl -sOJ "$API/api/admin/activity/export/txt" \
    -H "Authorization: Bearer $TOKEN"
}
