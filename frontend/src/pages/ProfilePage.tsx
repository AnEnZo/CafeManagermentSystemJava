import React, { useEffect, useState } from 'react';
import axios from 'axios';
import '../css/ProfilePage.css';
import { FaUser, FaEnvelope, FaPhone, FaAward, FaUserTag, FaKey, FaEdit, FaSave, FaTimes } from 'react-icons/fa';
import { Link, useNavigate } from 'react-router-dom';

interface User {
  id: number;
  displayName: string;
  username: string;
  email: string;
  phoneNumber: string;
  rewardPoints: number;
  roles: string[];
  provider: string;
  providerId: string;
}

const ProfilePage: React.FC = () => {
  const [user, setUser] = useState<User | null>(null);
  const [formUser, setFormUser] = useState<Partial<User>>({});
  const [editing, setEditing] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    const token = sessionStorage.getItem('jwtToken');
    if (!token) {
      setError('Bạn chưa đăng nhập.');
      return;
    }

    axios
      .get('http://localhost:8080/api/users/me', {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => {
        setUser(res.data);
        setFormUser(res.data);
      })
      .catch((err) => {
        console.error(err);
        setError('Không thể tải thông tin người dùng.');
      });
  }, []);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormUser({
      ...formUser,
      [e.target.name]: e.target.value,
    });
  };

  const saveChanges = () => {
    const token = sessionStorage.getItem('jwtToken');
    axios
      .patch('http://localhost:8080/api/users/me', formUser, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => {
        setUser(res.data);
        setEditing(false);
      })
      .catch((err) => {
        console.error(err);
        alert('Lỗi khi cập nhật thông tin!');
      });
  };

  if (error) return <div className="profile-error">{error}</div>;
  if (!user) return <div className="profile-loading">Đang tải thông tin...</div>;

  return (
    <div className="profile-container d-flex justify-content-center align-items-center">
      <div className="profile-card card shadow p-4" style={{ minWidth: 400 }}>
        <h3 className="text-center mb-4">👤 Thông tin cá nhân</h3>
        <div className="mb-2 profile-item">
          <FaUser className="me-2 text-primary" />
          <strong>Họ tên:</strong>{' '}
          {editing ? (
            <input
              name="displayName"
              value={formUser.displayName || ''}
              onChange={handleChange}
              className="form-control d-inline w-75"
            />
          ) : (
            user.displayName
          )}
        </div>

        <div className="mb-2 profile-item">
          <FaUserTag className="me-2 text-secondary" />
          <strong>Tên đăng nhập:</strong> {user.username}
        </div>

        <div className="mb-2 profile-item">
          <FaEnvelope className="me-2 text-danger" />
          <strong>Email:</strong>{' '}
          {editing ? (
            <input
              name="email"
              value={formUser.email || ''}
              onChange={handleChange}
              className="form-control d-inline w-75"
            />
          ) : (
            user.email
          )}
        </div>

        <div className="mb-2 profile-item">
          <FaPhone className="me-2 text-success" />
          <strong>SĐT:</strong>{' '}
          {editing ? (
            <input
              name="phoneNumber"
              value={formUser.phoneNumber || ''}
              onChange={handleChange}
              className="form-control d-inline w-75"
            />
          ) : (
            user.phoneNumber
          )}
        </div>

        <div className="mb-2 profile-item">
          <FaAward className="me-2 text-warning" />
          <strong>Điểm thưởng:</strong> {user.rewardPoints}
        </div>

        <div className="mb-2 profile-item">
          <FaKey className="me-2 text-info" />
          <strong>Vai trò:</strong> {user.roles.join(', ')}
        </div>

        <div className="mb-2 profile-item">
          <strong>Đăng nhập qua:</strong> {user.provider}
        </div>

        {/* --- Buttons --- */}
        <div className="mt-4 text-center">
          {editing ? (
            <>
              <button className="btn btn-success me-2" onClick={saveChanges}>
                <FaSave className="me-1" /> Lưu
              </button>
              <button className="btn btn-secondary" onClick={() => setEditing(false)}>
                <FaTimes className="me-1" /> Hủy
              </button>
            </>
          ) : (
            <button className="btn btn-primary" onClick={() => setEditing(true)}>
              <FaEdit className="me-1" /> Sửa thông tin
            </button>
          )}
        </div>
        <div className="mt-3 text-center">
          <Link to="/home" className="btn btn-outline-dark">
            Quay lại trang chủ
          </Link>
        </div>

      </div>
    </div>
  );
};

export default ProfilePage;
