import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from '../hooks/useAuth';
import { Link, useNavigate } from 'react-router-dom';
import '../css/TablesPage.css';

interface Table {
  id: number;
  name: string;
  capacity: number;
  available: boolean;
}

const TablesPage: React.FC = () => {
  const { roles } = useAuth();
  const isAdmin = roles.includes('ROLE_ADMIN');
  const isStaff = roles.includes('ROLE_STAFF');
  const navigate = useNavigate();

  const [tables, setTables] = useState<Table[]>([]);
  const [showAvailableOnly, setShowAvailableOnly] = useState<boolean>(false);
  const [showCreateForm, setShowCreateForm] = useState<boolean>(false);
  const [newTable, setNewTable] = useState<{ name: string; capacity: number; available: boolean }>({
    name: '',
    capacity: 0,
    available: true,
  });

  const token = sessionStorage.getItem('jwtToken');

  useEffect(() => {
    const fetchTables = async () => {
      try {
        const response = await axios.get<Table[]>('http://localhost:8080/api/tables', {
          headers: { Authorization: `Bearer ${token}` },
        });
        setTables(response.data);
      } catch (error) {
        console.error('Lỗi khi tải danh sách bàn:', error);
      }
    };
    fetchTables();
  }, [token]);

  const createTable = async () => {
    try {
      const response = await axios.post<Table>('http://localhost:8080/api/tables', newTable, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setTables([...tables, response.data]);
      setShowCreateForm(false);
      setNewTable({ name: '', capacity: 0, available: true });
    } catch (error) {
      console.error('Lỗi khi tạo bàn mới:', error);
    }
  };

  const createOrder = async (tableId: number) => {
    try {
      const res = await axios.post('http://localhost:8080/api/orders', { tableId }, {
        headers: { Authorization: `Bearer ${token}` },
      });
      const order = res.data;
      navigate(`/orders/${order.id}`);
    } catch (error) {
      console.error('Lỗi khi tạo đơn:', error);
      alert('Không thể tạo đơn cho bàn này.');
    }
  };

    const viewOrderDetails = async (tableId: number) => {
      try {
        const res = await axios.get(`http://localhost:8080/api/orders/latest-by-table/${tableId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        const order = res.data;
        navigate(`/orders/${order.id}`);
      } catch (error) {
        console.error('Không tìm thấy đơn hàng:', error);
        alert('Không tìm thấy đơn hàng cho bàn này.');
      }
    };


  if (!isAdmin && !isStaff) {
    return <div>Bạn không có quyền truy cập trang này.</div>;
  }

  const displayedTables = showAvailableOnly ? tables.filter(t => t.available) : tables;

  return (
    <div className="container-fluid">
      <Link to="/home" className="btn btn-secondary mb-3">Quay lại trang chủ</Link>
      <h1>Danh sách bàn</h1>

      {isAdmin && (
        <div className="mb-3">
          <button className="btn btn-primary" onClick={() => setShowCreateForm(true)}>
            Tạo bàn mới
          </button>
          <div className="form-check mt-2">
            <input
              className="form-check-input"
              type="checkbox"
              checked={showAvailableOnly}
              onChange={() => setShowAvailableOnly(!showAvailableOnly)}
            />
            <label className="form-check-label">Chỉ hiển thị bàn trống</label>
          </div>
        </div>
      )}

      {isAdmin && showCreateForm && (
        <div className="card mb-3">
          <div className="card-body">
            <h5 className="card-title">Tạo bàn mới</h5>
            <div className="mb-3">
              <label htmlFor="tableName" className="form-label">Tên bàn</label>
              <input
                type="text"
                id="tableName"
                className="form-control"
                value={newTable.name}
                onChange={e => setNewTable({ ...newTable, name: e.target.value })}
                placeholder="Nhập tên bàn"
              />
            </div>
            <div className="mb-3">
              <label htmlFor="tableCapacity" className="form-label">Sức chứa</label>
              <input
                type="number"
                id="tableCapacity"
                className="form-control"
                value={newTable.capacity}
                onChange={e => setNewTable({ ...newTable, capacity: Number(e.target.value) })}
                placeholder="Nhập sức chứa"
                min={1}
              />
            </div>
            <div className="form-check mb-3">
              <input
                className="form-check-input"
                type="checkbox"
                id="tableAvailable"
                checked={newTable.available}
                onChange={e => setNewTable({ ...newTable, available: e.target.checked })}
              />
              <label htmlFor="tableAvailable" className="form-check-label">Bàn còn trống</label>
            </div>
            <button type="button" className="btn btn-success" onClick={createTable}>Tạo</button>
            <button
              type="button"
              className="btn btn-secondary ms-2"
              onClick={() => { setShowCreateForm(false); setNewTable({ name: '', capacity: 0, available: true }); }}
            >
              Hủy
            </button>
          </div>
        </div>
      )}

      <table className="table table-striped">
        <thead>
          <tr>
            <th>ID</th>
            <th>Tên</th>
            <th>Sức chứa</th>
            <th>Trạng thái</th>
            <th>Hành động</th>
          </tr>
        </thead>
        <tbody>
          {displayedTables.length > 0 ? displayedTables.map(table => (
            <tr key={table.id}>
              <td>{table.id}</td>
              <td>{table.name}</td>
              <td>{table.capacity}</td>
              <td>{table.available ? 'Trống' : 'Đã đặt'}</td>
              <td>
                {table.available ? (
                  <button className="btn btn-primary btn-sm" onClick={() => createOrder(table.id)}>Tạo đơn</button>
                ) : (
                  <button className="btn btn-info btn-sm" onClick={() => viewOrderDetails(table.id)}>Chi tiết đơn</button>
                )}

              </td>
            </tr>
          )) : (
            <tr><td colSpan={5}>Không có bàn nào.</td></tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default TablesPage;
