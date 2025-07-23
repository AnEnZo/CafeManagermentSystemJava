import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import '../css/Login.css';
import { FaGoogle } from 'react-icons/fa';

const Login: React.FC = () => {
  const [username, setUsername] = useState<string>('');
  const [password, setPassword] = useState<string>('');
  const navigate = useNavigate();

  const login = async () => {
    try {
      const res = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
      });
      if (!res.ok) throw new Error('Login failed');
      const data = await res.json();
      sessionStorage.setItem('jwtToken', data.token);
      navigate('/home');
    } catch (err: any) {
      alert(err.message);
    }
  };

  const oauthLogin = () => {
    const redirectUri = "http://localhost:5173/oauth2-redirect";
    document.cookie = `redirect_uri=${redirectUri}; path=/`;
    window.location.href = `http://localhost:8080/oauth2/authorization/google`;
  };

  return (
    <div className="login-page d-flex align-items-center justify-content-center">
      <div className="card login-card w-100" style={{ maxWidth: 420 }}>
        <div className="card-body">
          <p className="login-title text-center">Sign In</p>

          {/* --- Form Login --- */}
          <div className="mb-3">
            <label htmlFor="username" className="form-label">Username</label>
            <input
              id="username"
              type="text"
              className="form-control"
              value={username}
              onChange={e => setUsername(e.target.value)}
              placeholder="Enter username"
            />
          </div>
          <div className="mb-4">
            <label htmlFor="password" className="form-label">Password</label>
            <input
              id="password"
              type="password"
              className="form-control"
              value={password}
              onChange={e => setPassword(e.target.value)}
              placeholder="Enter password"
            />
          </div>
          <div className="d-grid mb-2">
            <button className="btn btn-primary login-btn" onClick={login}>
              Login
            </button>
          </div>

          {/* --- Forgot Password Link --- */}
          <div className="text-end mb-3">
            <Link to="/forgot-password-email" className="btn btn-link p-0">
              Forgot Password?
            </Link>
          </div>

          {/* --- OAuth Login --- */}
          <div className="or-separator mb-3">
            <span className="px-2 bg-white">or</span>
          </div>
          <div className="d-grid mb-3">
            <button className="btn btn-outline-danger login-btn" onClick={oauthLogin}>
              <FaGoogle className="me-2" /> Login with Google
            </button>
          </div>

          {/* --- Link to Register --- */}
          <p className="text-center">
            Don't have an account?{' '}
            <Link to="/register" className="btn btn-link p-0">
              Register here
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Login;
