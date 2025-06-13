// OrderService.js
const API_BASE = "http://localhost:8080/api/orders";

export async function createOrder(tableId, token) {
  const response = await fetch(API_BASE, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`
    },
    body: JSON.stringify({ tableId })
  });

  if (!response.ok) throw new Error("Failed to create order");
  return await response.json();
}

export async function addItemToOrder(orderId, menuItemId, quantity, token) {
  const response = await fetch(`${API_BASE}/${orderId}/items?menuItemId=${menuItemId}&quantity=${quantity}`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${token}`
    }
  });

  if (!response.ok) throw new Error("Failed to add item");
  return await response.json();
}
