import React, { useMemo } from 'react';
import { Link } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import logoTheCoffeHouse from '../assets/logoTheCoffeHouse.png';
import logoTheCoffeHouseV2 from '../assets/logoTheCoffeHouseV2.png';
import img1 from '../assets/img1.png';
import img2 from '../assets/img2.png';
import img3 from '../assets/img3.png';
import '../css/Home.css';
import TopItems from '../components/TopItems';

// Manual JWT parse helper (logic unchanged)
function parseJwt(token: string): any | null {
  try {
    const base64Payload = token.split('.')[1];
    const payload = atob(base64Payload.replace(/-/g, '+').replace(/_/g, '/'));
    const utf8 = decodeURIComponent(
      Array.prototype.map
        .call(payload, (c: string) => '%'+('00'+c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    return JSON.parse(utf8);
  } catch {
    return null;
  }
}

interface JwtPayload { sub: string; displayName?: string; roles: string[]; }

const Home: React.FC = () => {
  const token = sessionStorage.getItem('jwtToken');
  const { roles, displayName } = useMemo(() => {
    if (!token) return { roles: [], displayName: '' };
    const raw = token.startsWith('Bearer ') ? token.slice(7) : token;
    const decoded = parseJwt(raw) as JwtPayload | null;
    return {
      roles: decoded?.roles ?? [],
      displayName: decoded?.displayName ?? ''
    };
  }, [token]);


const navigate = useNavigate();

const handleLogout = () => {
  sessionStorage.removeItem('jwtToken');
  navigate('/');
};

  const isAdmin = roles.includes('ROLE_ADMIN');
  const isStaff = roles.includes('ROLE_STAFF');
  const isUser = roles.includes('ROLE_USER');

const images = [img1, img2, img3];

const [currentIndex, setCurrentIndex] = useState(0);

useEffect(() => {
  const interval = setInterval(() => {
    setCurrentIndex((prevIndex) => (prevIndex + 1) % images.length);
  }, 2000); // mỗi 2 giây

  return () => clearInterval(interval); // cleanup
}, []);

  return (
    <div className="home container-fluid p-0">
      <section className="roles">
        <div className="roles-container">
          {/* Logo bên trái */}
          <div className="logo-section">
            <img src={logoTheCoffeHouse} alt="The Coffee House" className="header-logo" />
          </div>

          {/* Auth info bên phải */}

            <div className="roles-info">
              {token ? (
                          <div className="auth-info">
                            <span>Xin chào, <strong>{displayName || 'Quý khách'}!</strong></span>
                            <button onClick={handleLogout} className="btn-primary" style={{ marginLeft: '1rem' }}>
                              Đăng xuất
                            </button>
                          </div>
                        ) : (
                          <div className="auth-actions">
                            <Link to="/" className="btn-primary">Đăng nhập</Link>
                            <Link to="/register" className="btn-primary" style={{ marginLeft: '0.2rem' }}>
                              Đăng ký
                            </Link>
                          </div>
                        )}
            </div>

        </div>
      </section>

      {/* Navigation Dashboard - Now appears before hero */}
      <section className="dashboard">
        {isAdmin && (
          <DashboardCard
            title={<img src={logoTheCoffeHouse} alt="The Coffee House" style={{ height: '60px' }} />}
            links={[
              { to: '/categories', text: 'Danh mục' },
              { to: '/dishes', text: 'Món ăn' },
              { to: '/tables', text: 'Bàn' },
              { to: '/orders', text: 'Đơn hàng' },
              { to: '/invoices', text: 'Hóa đơn' },
              { to: '/userpage', text: 'Quản lý tài khoản' },
              { to: '/vouchermanager', text: 'Quản lý voucher' },
              { to: '/revenue', text: 'Doanh thu' },
              { to: '/profilepage', text: 'thông tin của tôi' },
              { to: '/uservouchermanager', text: 'Voucher của tôi' },
            ]}
          />
        )}

        {isStaff && !isAdmin && (
          <DashboardCard
           title={<img src={logoTheCoffeHouse} alt="The Coffee House" style={{ height: '60px' }} />}
           links={[
            { to: '/orders', text: 'Xử lý đơn' },
            { to: '/tables', text: 'Xem bàn' },
          ]} />
        )}

        {isUser && !isAdmin && !isStaff && (
          <DashboardCard
           title={<img src={logoTheCoffeHouse} alt="The Coffee House" style={{ height: '60px' }} />}
           links={[
            { to: '/menu', text: 'Xem Menu' },
            { to: '/profilepage', text: 'thông tin của tôi' },
            { to: '/uservouchermanager', text: 'Voucher của tôi' },
          ]} />
        )}

        {!isAdmin && !isStaff && !isUser && token && (
          <p className="no-role">Không có quyền truy cập đặc biệt.</p>
        )}
      </section>

      {/* Hero Section - Now appears after navigation */}
      <header className="hero">
       <div className="hero-carousel">
         <img
           src={images[currentIndex]}
           alt={`Slide ${currentIndex + 1}`}
           className="carousel-img"
         />
       </div>
      </header>

      {/* Top 3 Best-Selling Items */}
           <TopItems topN={3} />

      <footer className="footer">
              <div className="footer-container">
                <div className="footer-content">
                  {/* Logo và thông tin chính */}
                  <div className="footer-brand">
                    <img src={logoTheCoffeHouseV2} alt="The Coffee House" className="footer-logo" />
                    <h3>The Coffee House</h3>
                  </div>

                  {/* Thông tin liên hệ */}
                  <div className="footer-section">
                    <h4>Liên hệ</h4>
                    <p>📍 394 Mỹ Đình, Nam Từ Liêm, Hà Nội</p>
                    <p>📞 (028) 1234 5678</p>
                  </div>

                  {/* Giờ hoạt động */}
                  <div className="footer-section">
                    <h4>Giờ mở cửa</h4>
                    <p>T2-T6: 7:00 - 22:00</p>
                    <p>T7-CN: 6:30 - 23:00</p>
                  </div>

                  {/* Mạng xã hội */}
                  <div className="footer-section">
                    <h4>Theo dõi</h4>
                    <div className="social-links">
                      <a href="#" className="social-link">👥 Facebook</a>
                      <a href="#" className="social-link">📷 Instagram</a>
                    </div>
                  </div>
                </div>

                {/* Copyright */}
                <div className="footer-bottom">
                  <p>&copy; 2024 The Coffee House. All rights reserved.</p>
                </div>
              </div>
            </footer>

    </div>
  );
};

interface CardLink { to: string; text: string; }

const DashboardCard: React.FC<{ title: string; links: CardLink[] }> = ({ title, links }) => (
  <div className="card">
    <h3>{title}</h3>
    <div className="card-links">
      {links.map((l) => (
        <Link key={l.to} to={l.to} className="btn-action square">
          {l.text}
        </Link>
      ))}
    </div>
  </div>
);

export default Home;