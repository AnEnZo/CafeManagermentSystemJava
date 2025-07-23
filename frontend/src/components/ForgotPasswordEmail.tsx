import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';

const ForgotPasswordEmail: React.FC = () => {
  const [username, setUsername] = useState<string>('');
  const [loading, setLoading] = useState<boolean>(false);
  const [message, setMessage] = useState<string | null>(null);
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!username.trim()) {
      setMessage('Username không được để trống');
      return;
    }
    setLoading(true);
    try {
      const res = await fetch('http://localhost:8080/api/auth/forgot-password-email', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username })
      });
      const data = await res.json();
      if (!res.ok) throw new Error(data.message || 'Lỗi khi gửi OTP');
      setMessage(data.message);
      // Chuyển sang trang nhập OTP sau 2s
      setTimeout(() => navigate('/reset-password-email'), 2000);
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
          <h3 className="login-title text-center">Quên mật khẩu</h3>
          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label htmlFor="username" className="form-label">Username</label>
              <input
                id="username"
                type="text"
                className="form-control"
                value={username}
                onChange={e => setUsername(e.target.value)}
                placeholder="Nhập username"
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
                {loading ? 'Đang gửi...' : 'Gửi OTP qua Email'}
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

export default ForgotPasswordEmail;
