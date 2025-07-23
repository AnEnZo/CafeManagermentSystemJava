import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import '../css/Register.css';

const Register: React.FC = () => {
  const [displayName, setDisplayName] = useState<string>('');
  const [username, setUsername] = useState<string>('');
  const [email, setEmail] = useState<string>('');
  const [phoneNumber, setPhoneNumber] = useState<string>('');
  const [password, setPassword] = useState<string>('');
  const navigate = useNavigate();

  const register = async () => {
    try {
      const res = await fetch('http://localhost:8080/api/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ displayName, username, email, phoneNumber, password })
      });
      if (res.status === 201) {
        alert('User registered successfully!');
        navigate('/');
      } else {
        const errorMsg = await res.text();
        throw new Error(errorMsg);
      }
    } catch (err: any) {
      alert(err.message);
    }
  };

  return (
    <div className="register-page d-flex align-items-center justify-content-center">
      <div className="card register-card w-100" style={{ maxWidth: 420 }}>
        <div className="card-body">
          <p className="register-title text-center">Sign Up</p>

          <div className="mb-3">
            <label htmlFor="displayName" className="form-label">Display Name</label>
            <input
              id="displayName"
              type="text"
              className="form-control"
              value={displayName}
              onChange={e => setDisplayName(e.target.value)}
              placeholder="Enter display name"
            />
          </div>
          <div className="mb-3">
            <label htmlFor="username" className="form-label">Username</label>
            <input
              id="username"
              type="text"
              className="form-control"
              value={username}
              onChange={e => setUsername(e.target.value)}
              placeholder="Choose a username"
            />
          </div>
          <div className="mb-3">
            <label htmlFor="email" className="form-label">Email</label>
            <input
              id="email"
              type="email"
              className="form-control"
              value={email}
              onChange={e => setEmail(e.target.value)}
              placeholder="Enter your email"
            />
          </div>
          <div className="mb-3">
            <label htmlFor="phoneNumber" className="form-label">Phone Number</label>
            <input
              id="phoneNumber"
              type="text"
              className="form-control"
              value={phoneNumber}
              onChange={e => setPhoneNumber(e.target.value)}
              placeholder="Enter phone number"
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
              placeholder="Create a password"
            />
          </div>
          <div className="d-grid mb-3">
            <button className="btn btn-success register-btn" onClick={register}>
              Register
            </button>
          </div>

          <p className="text-center">
            Already have an account?{' '}
            <Link to="/" className="btn btn-link p-0">
              Login here
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Register;
