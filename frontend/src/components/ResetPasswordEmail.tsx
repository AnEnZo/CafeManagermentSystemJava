import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';

interface ResetReq {
  username: string;
  otp: string;
  newPassword: string;
}

const ResetPasswordEmail: React.FC = () => {
  const [form, setForm] = useState<ResetReq>({ username: '', otp: '', newPassword: '' });
  const [loading, setLoading] = useState<boolean>(false);
  const [message, setMessage] = useState<string | null>(null);
  const navigate = useNavigate();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const { username, otp, newPassword } = form;
    if (!username || !otp || !newPassword) {
      setMessage('Vui lòng điền đầy đủ thông tin');
      return;
    }
    setLoading(true);
    try {
      const res = await fetch('http://localhost:8080/api/auth/reset-password-email', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(form)
      });
      const data = await res.json();
      if (!res.ok) throw new Error(data.message || 'Đặt lại mật khẩu thất bại');
      setMessage(data.message);
      // Chuyển về login sau 2s
      setTimeout(() => navigate('/'), 2000);
    } catch (err: any) {
      setMessage(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page d-flex align-items-center justify-content-center">
      <div className="card login-card w-100" style={{ maxWidth: 420 }}>
        <div className="card-body">
          <h3 className="login-title text-center">Đặt lại mật khẩu</h3>
          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label htmlFor="username" className="form-label">Username</label>
              <input
                id="username"
                name="username"
                type="text"
                className="form-control"
                value={form.username}
                onChange={handleChange}
                placeholder="Nhập username"
                disabled={loading}
              />
            </div>
            <div className="mb-3">
              <label htmlFor="otp" className="form-label">OTP</label>
              <input
                id="otp"
                name="otp"
                type="text"
                className="form-control"
                value={form.otp}
                onChange={handleChange}
                placeholder="Nhập mã OTP"
                disabled={loading}
              />
            </div>
            <div className="mb-4">
              <label htmlFor="newPassword" className="form-label">Mật khẩu mới</label>
              <input
                id="newPassword"
                name="newPassword"
                type="password"
                className="form-control"
                value={form.newPassword}
                onChange={handleChange}
                placeholder="Nhập mật khẩu mới"
                disabled={loading}
              />
            </div>
            {message && (
              <div className="alert alert-info" role="alert">
                {message}
              </div>
            )}
            <div className="d-grid mb-3">
              <button
                type="submit"
                className="btn btn-primary"
                disabled={loading}
              >
                {loading ? 'Đang xử lý...' : 'Xác nhận đặt lại'}
              </button>
            </div>
          </form>
          <p className="text-center">
            <Link to="/">← Quay lại Đăng nhập</Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default ResetPasswordEmail;
