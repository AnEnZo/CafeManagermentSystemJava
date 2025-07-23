import { useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';

const OAuth2RedirectHandler = () => {
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    const query = new URLSearchParams(location.search);
    const token = query.get('token');

    if (token) {
      sessionStorage.setItem('jwtToken', token);
      navigate('/home'); // chuyển hướng tới trang home
    } else {
      alert('Login failed: token not found');
    }
  }, [location, navigate]);

  return <div>Logging in...</div>;
};

export default OAuth2RedirectHandler;
