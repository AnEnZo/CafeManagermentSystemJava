import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from './components/Login';
import OAuth2RedirectHandler from './components/OAuth2RedirectHandler';
import Home from './components/Home';
import TablesPage from './pages/TablesPage';
import Register from './components/Register';
import OrdersPage from './pages/OrdersPage';
import OrderDetailPage from './pages/OrderDetailPage';
import ForgotPasswordEmail from './components/ForgotPasswordEmail';
import ResetPasswordEmail from './components/ResetPasswordEmail';
import Category from './pages/Category';
import InvoicePage from './pages/Invoice';
import Dishes from './pages/Dishes';
import Revenue from './pages/Revenue';
import UserPage from './pages/UserPage';
import UserVoucherManager from './pages/UserVoucherManager';
import VoucherManager from './pages/VoucherManager';
import ProfilePage from './pages/ProfilePage';
import MenuPage from './pages/MenuPage';
const App: React.FC = () => {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/oauth2-redirect" element={<OAuth2RedirectHandler />} />
        <Route path="/home" element={<Home />} />
        <Route path="/tables" element={<TablesPage />} />
        <Route path="/register" element={<Register />} />
        <Route path="/orders" element={<OrdersPage />} />
        <Route path="/orders/:id" element={<OrderDetailPage />} />
        <Route path="/forgot-password-email" element={<ForgotPasswordEmail />} />
        <Route path="/reset-password-email" element={<ResetPasswordEmail />} />
        <Route path="/categories" element={<Category />} />
        <Route path="/invoices" element={<InvoicePage />} />
        <Route path="/dishes" element={<Dishes />} />
        <Route path="/revenue" element={<Revenue />} />
        <Route path="/userpage" element={<UserPage />} />
        <Route path="/profilepage" element={<ProfilePage />} />
        <Route path="/uservouchermanager" element={<UserVoucherManager />} />
        <Route path="/vouchermanager" element={<VoucherManager />} />
        <Route path="/menu" element={<MenuPage />} />

      </Routes>
    </Router>
  );
};

export default App;
