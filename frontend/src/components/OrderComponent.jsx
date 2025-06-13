// Example usage (React component)
import React, { useState } from "react";
import { createOrder, addItemToOrder } from "./OrderService";

export default function OrderComponent() {
  const [token] = useState(localStorage.getItem("token")); // JWT lưu trong localStorage
  const [order, setOrder] = useState(null);

  const handleCreate = async () => {
    try {
      const result = await createOrder(1, token);
      setOrder(result);
    } catch (err) {
      console.error(err);
    }
  };

  const handleAddItem = async () => {
    try {
      const result = await addItemToOrder(order.id, 2, 1, token);
      console.log("Added item", result);
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div>
      <button onClick={handleCreate}>Tạo đơn bàn 1</button>
      {order && <button onClick={handleAddItem}>Thêm món vào đơn</button>}
    </div>
  );
}
