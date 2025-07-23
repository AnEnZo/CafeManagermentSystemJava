import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import '../css/User.css';

interface User {
  id: number;
  displayName: string;
  email: string;
  phoneNumber: string;
  provider: 'FACEBOOK' | 'GOOGLE' | 'LOCAL';
  providerId: string;
  rewardPoints: number;
  username: string;
}

const UserPage: React.FC = () => {
  const { roles } = useAuth();
  const isAdmin = roles.includes('ROLE_ADMIN');
  const token = sessionStorage.getItem('jwtToken');

  const [users, setUsers] = useState<User[]>([]);
  const [showForm, setShowForm] = useState(false);
  const [isEdit, setIsEdit] = useState(false);
  const [editId, setEditId] = useState<number | null>(null);

  const [formUser, setFormUser] = useState({
    displayName: '',
    email: '',
    phoneNumber: '',
    username: '',
    password: '',
    provider: 'LOCAL',
  });

  const fetchUsers = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/users', {
        headers: { Authorization: `Bearer ${token}` },
      });
      const data = response.data.content || [];
      setUsers(data);
    } catch (error) {
      console.error('Lỗi khi tải người dùng:', error);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, [token]);

  const handleDeleteUser = async (id: number) => {
    try {
      await axios.delete(`http://localhost:8080/api/users/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setUsers(users.filter((user) => user.id !== id));
    } catch (error) {
      console.error('Lỗi khi xoá người dùng:', error);
      alert('Lỗi khi xoá người dùng.');
    }
  };

  const handleEditUser = (user: User) => {
    setShowForm(true);
    setIsEdit(true);
    setEditId(user.id);
    setFormUser({
      displayName: user.displayName,
      email: user.email,
      phoneNumber: user.phoneNumber,
      username: user.username,
      password: '',
      provider: user.provider,
    });
  };

  const handleSubmitUser = async () => {
    try {
      const url = isEdit
        ? `http://localhost:8080/api/users/${editId}`
        : 'http://localhost:8080/api/users/user';

      const method = isEdit ? axios.put : axios.post;

      const response = await method(url, formUser, {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (isEdit) {
        setUsers(users.map(u => (u.id === editId ? response.data : u)));
      } else {
        setUsers([...users, response.data]);
      }

      // Reset form
      setShowForm(false);
      setIsEdit(false);
      setEditId(null);
      setFormUser({
        displayName: '',
        email: '',
        phoneNumber: '',
        username: '',
        password: '',
        provider: 'LOCAL',
      });
    } catch (error: any) {
      console.error('Lỗi khi lưu người dùng:', error);
      if (error.response?.data) {
        const errMsg = typeof error.response.data === 'string'
          ? error.response.data
          : JSON.stringify(error.response.data);
        alert(`Lỗi: ${errMsg}`);
      } else {
        alert('Lỗi không xác định khi lưu người dùng.');
      }
    }
  };

  if (!isAdmin) {
    return <div>Bạn không có quyền truy cập trang này.</div>;
  }

  return (
    <div className="container-fluid user-page">
      <Link to="/home" className="btn btn-secondary mb-3">Quay lại trang chủ</Link>
      <h1 className="user-page-title">Quản lý người dùng</h1>

      <div style={{ textAlign: 'right', marginBottom: '1rem' }}>
        <button className="btn-add-user" onClick={() => {
          setShowForm(!showForm);
          setIsEdit(false);
          setFormUser({
            displayName: '',
            email: '',
            phoneNumber: '',
            username: '',
            password: '',
            provider: 'LOCAL',
          });
        }}>
          {showForm ? 'Đóng form' : '+ Thêm người dùng'}
        </button>
      </div>

      {showForm && (
        <div className="add-user-form">
          <input
            type="text"
            placeholder="Tên hiển thị"
            value={formUser.displayName}
            onChange={(e) => setFormUser({ ...formUser, displayName: e.target.value })}
          />
          <input
            type="email"
            placeholder="Email"
            value={formUser.email}
            onChange={(e) => setFormUser({ ...formUser, email: e.target.value })}
          />
          <input
            type="text"
            placeholder="Số điện thoại"
            value={formUser.phoneNumber}
            onChange={(e) => setFormUser({ ...formUser, phoneNumber: e.target.value })}
          />
          <input
            type="text"
            placeholder="Username"
            value={formUser.username}
            onChange={(e) => setFormUser({ ...formUser, username: e.target.value })}
          />
          {!isEdit && (
            <input
              type="password"
              placeholder="Password"
              value={formUser.password}
              onChange={(e) => setFormUser({ ...formUser, password: e.target.value })}
            />
          )}
          <select
            value={formUser.provider}
            onChange={(e) => setFormUser({ ...formUser, provider: e.target.value as any })}
          >
            <option value="LOCAL">LOCAL</option>
            <option value="GOOGLE">GOOGLE</option>
            <option value="FACEBOOK">FACEBOOK</option>
          </select>
          <button className="btn-user-action" onClick={handleSubmitUser}>
            {isEdit ? 'Cập nhật' : 'Lưu'}
          </button>
        </div>
      )}

      <table className="user-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Tên hiển thị</th>
            <th>Email</th>
            <th>SĐT</th>
            <th>Provider</th>
            <th>Username</th>
            <th>Điểm thưởng</th>
            <th>Hành động</th>
          </tr>
        </thead>
        <tbody>
          {users.length > 0 ? (
            users.map((user) => (
              <tr key={user.id}>
                <td data-label="ID">{user.id}</td>
                <td data-label="Tên hiển thị">{user.displayName}</td>
                <td data-label="Email">{user.email}</td>
                <td data-label="SĐT">{user.phoneNumber}</td>
                <td data-label="Provider">
                  <span className={`provider-badge provider-${(user.provider || 'LOCAL').toLowerCase()}`}>
                    {user.provider || 'LOCAL'}
                  </span>
                </td>
                <td data-label="Username">{user.username}</td>
                <td data-label="Điểm thưởng" className="reward-points">{user.rewardPoints}</td>
                <td data-label="Hành động">
                  <button className="btn-user-action" onClick={() => handleEditUser(user)}>Sửa</button>
                  <button className="btn-user-action" onClick={() => handleDeleteUser(user.id)}>Xoá</button>
                </td>
              </tr>
            ))
          ) : (
            <tr><td colSpan={8}>Không có người dùng nào.</td></tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default UserPage;
