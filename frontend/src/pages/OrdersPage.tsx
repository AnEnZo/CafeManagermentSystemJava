import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import '../css/OrdersPage.css';

interface OrderItem {
  id?: number;
  quantity: number;
  menuItem: { id?: number; name: string; price?: number; imageUrl?: string; category?: { name: string } };
}

interface Order {
  id?: number;
  orderTime: string;
  status: string;
  orderType: string;
  table?: { id?: number; name: string; available?: boolean; capacity?: number };
  staff: { id?: number; displayName: string; username?: string };
  orderItems: OrderItem[];
}

interface PageResponse<T> {
  content: T[];
  totalPages: number;
  number: number;
}

const OrdersPage: React.FC = () => {
  const [orders, setOrders] = useState<Order[]>([]);
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [tableId, setTableId] = useState<number | ''>('');
  const [totalPages, setTotalPages] = useState(0);
  const navigate = useNavigate();

  const fetchOrders = async () => {
    const token = sessionStorage.getItem('jwtToken') || '';
    const res = await fetch(
      `http://localhost:8080/api/orders?page=${page}&size=${size}`,
      {
        headers: { Authorization: `Bearer ${token}` }
      }
    );
    if (res.ok) {
      const data: PageResponse<Order> = await res.json();
      setOrders(data.content);
      setTotalPages(data.totalPages);
    } else {
      console.error('Failed to load orders');
    }
  };

  useEffect(() => {
    fetchOrders();
  }, [page]);

  const createOrder = async () => {
    if (tableId === '') return;
    const token = sessionStorage.getItem('jwtToken') || '';
    const res = await fetch('http://localhost:8080/api/orders', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`
      },
      body: JSON.stringify({ tableId })
    });
    if (res.ok) fetchOrders();
    else alert('Unable to create order');
  };

  const createTakeaway = async () => {
    const token = sessionStorage.getItem('jwtToken') || '';
    const res = await fetch('http://localhost:8080/api/orders/takeaway', {
      method: 'POST',
      headers: { Authorization: `Bearer ${token}` }
    });
    if (res.ok) fetchOrders();
    else alert('Unable to create takeaway');
  };

  const serveOrder = async (id?: number) => {
    if (id == null) return;
    const token = sessionStorage.getItem('jwtToken') || '';
    const res = await fetch(
      `http://localhost:8080/api/orders/${id}/serve`,
      {
        method: 'PUT',
        headers: { Authorization: `Bearer ${token}` }
      }
    );
    if (res.ok) fetchOrders();
    else alert('Unable to serve order');
  };

  const deleteOrder = async (id?: number) => {
    if (id == null) return;
    if (!window.confirm('Are you sure to delete this order?')) return;
    const token = sessionStorage.getItem('jwtToken') || '';
    const res = await fetch(`http://localhost:8080/api/orders/${id}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${token}` }
    });
    if (res.ok) fetchOrders();
    else alert('Unable to delete order');
  };


  return (
    <div className="orders-page container-fluid p-4">
    <Link to="/home" className="btn btn-secondary mb-3">Quay lại trang chủ</Link>
      <h2>Orders</h2>

      <div className="order-actions mb-3">
        <div className="d-inline-block me-2">
          <input
            type="number"
            placeholder="Table ID"
            value={tableId}
            min="1"
            onChange={e => {
                const value = e.target.value;
                setTableId(value === '' ? '' : Math.max(1, +value));
              }}
            className="form-control d-inline-block w-auto"
          />
          <button onClick={createOrder} className="btn btn-primary ms-2">
            New Dine-In
          </button>
        </div>
        <button onClick={createTakeaway} className="btn btn-secondary">
          New Takeaway
        </button>
      </div>

      <table className="table table-striped">
        <thead>
          <tr>
            <th>ID</th>
            <th>Time</th>
            <th>Type</th>
            <th>Status</th>
            <th>Table</th>
            <th>Staff</th>
            <th>Details</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {orders.map((o, idx) => (
            <tr key={o.id ?? idx}>
              <td>{o.id ?? '-'}</td>
              <td>{new Date(o.orderTime).toLocaleString()}</td>
              <td>{o.orderType}</td>
              <td>{o.status}</td>
              <td>{o.table?.name || '-'}</td>
              <td>{o.staff.displayName}</td>
              {/* Details button navigates to detail page */}
              <td>
                {o.id != null ? (
                  <button
                    className="btn btn-info btn-sm"
                    onClick={() => navigate(`/orders/${o.id}`)}
                  >
                    View Items
                  </button>
                ) : '-'}
              </td>
              <td>
                <button
                  onClick={() => {
                      if (window.confirm('Are you sure you want to serve this order?')) {
                        serveOrder(o.id);
                      }
                    }}
                  className="btn btn-success btn-sm me-1"
                >
                  Serve
                </button>
                <button
                  onClick={() => deleteOrder(o.id)}
                  className="btn btn-danger btn-sm"
                >
                  Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <div className="pagination-controls">
        <button
          className="btn btn-outline-primary me-2"
          onClick={() => setPage(p => Math.max(p - 1, 0))}
          disabled={page === 0}
        >
          Previous
        </button>
        <span>Page {page + 1} of {totalPages}</span>
        <button
          className="btn btn-outline-primary ms-2"
          onClick={() => setPage(p => Math.min(p + 1, totalPages - 1))}
          disabled={page + 1 >= totalPages}
        >
          Next
        </button>
      </div>
    </div>
  );
};

export default OrdersPage;
