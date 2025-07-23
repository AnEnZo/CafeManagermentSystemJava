import React, { useEffect, useState } from 'react';
import axios from 'axios';
import '../css/VoucherManager.css';
import { Link } from 'react-router-dom';
import imgdefaultVoucher from '../assets/imgdefaultVoucher.png';

interface Voucher {
  id: number;
  code: string;
  type: 'PERCENTAGE_DISCOUNT' | 'BUY_ONE_GET_ONE' | 'FIXED_DISCOUNT';
  discountValue: number;
  minOrderAmount: number;
  active: boolean;
  requiredPoints: number;
  imageUrl?: string;
}

const VoucherManager: React.FC = () => {
  const [vouchers, setVouchers] = useState<Voucher[]>([]);
  const [filtered, setFiltered] = useState<Voucher[]>([]);
  const [error, setError] = useState<string>('');
  const [message, setMessage] = useState<string>('');
  const [searchTerm, setSearchTerm] = useState<string>('');
  const [editingId, setEditingId] = useState<number | null>(null);
  const [form, setForm] = useState<Omit<Voucher, 'id'>>({
    code: '',
    type: 'PERCENTAGE_DISCOUNT',
    discountValue: 0,
    minOrderAmount: 0,
    active: true,
    requiredPoints: 0,
    imageUrl: '',
  });

  const token = sessionStorage.getItem('jwtToken');
  const headers = {
    Authorization: `Bearer ${token}`,
    'Content-Type': 'application/json',
  };

  const fetchAll = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/vouchers', { headers });
      const data: Voucher[] = response.data;
      setVouchers(data);
      setFiltered(data);
    } catch (e) {
      console.error(e);
      setError('❌ Không thể tải danh sách voucher');
    }
  };

  useEffect(() => {
    fetchAll();
  }, []);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value, type, checked } = e.target;
    setForm(f => ({
      ...f,
      [name]: type === 'checkbox' ? checked : ['discountValue', 'minOrderAmount', 'requiredPoints'].includes(name) ? Number(value) : value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setMessage('');

    if (!form.code.trim()) {
      setError('⚠️ Mã voucher không thể trống');
      return;
    }

    try {
      await axios.post('http://localhost:8080/api/vouchers', form, { headers });
      setMessage('✅ Thêm voucher thành công');
      setForm({
        code: '',
        type: 'PERCENTAGE_DISCOUNT',
        discountValue: 0,
        minOrderAmount: 0,
        active: true,
        requiredPoints: 0,
      });
      await fetchAll();
    } catch (e) {
      console.error(e);
      setError('❌ Thêm thất bại');
    }
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('Xác nhận xóa?')) return;

    try {
      await axios.delete(`http://localhost:8080/api/vouchers/${id}`, { headers });
      setMessage('🗑️ Xóa thành công');
      await fetchAll();
    } catch (e) {
      console.error(e);
      setError('❌ Xóa thất bại, kiểm tra ràng buộc');
    }
  };

  const updateVoucherField = (id: number, field: keyof Voucher, value: any) => {
    setFiltered(prev =>
      prev.map(v => (v.id === id ? { ...v, [field]: value } : v))
    );
  };

  const handleSearch = () => {
    const k = searchTerm.trim().toLowerCase();
    if (!k) return fetchAll();
    setFiltered(vouchers.filter(v => v.code.toLowerCase().includes(k)));
  };

  const fmtType = (t: string) =>
    t === 'PERCENTAGE_DISCOUNT' ? 'Phần trăm' : t === 'FIXED_DISCOUNT' ? 'Giảm cố định' : 'Mua 1 tặng 1';

  const fmtVal = (v: Voucher) =>
    v.type === 'PERCENTAGE_DISCOUNT' ? `${v.discountValue}%` : `${v.discountValue.toLocaleString()}₫`;

  return (
    <div className="container">
      <Link to="/home" className="btn btn-secondary mb-3">Quay lại trang chủ</Link>
      <h2>🎟️ Quản lý Voucher</h2>
      {message && <p className="message">{message}</p>}
      {error && <p className="message" style={{ color: 'red' }}>{error}</p>}

      <div style={{ display: 'flex', gap: 8, marginBottom: 16 }}>
        <input value={searchTerm} onChange={e => setSearchTerm(e.target.value)} placeholder="Tìm mã..." />
        <button onClick={handleSearch}>🔍</button>
        <button onClick={fetchAll}>🔄</button>
      </div>

      <form onSubmit={handleSubmit}>
        <h3>➕ Thêm Voucher</h3>
        <label>Mã Voucher:</label>
        <input name="code" value={form.code} onChange={handleChange} required />
        <label>Loại:</label>
        <select name="type" value={form.type} onChange={handleChange}>
          <option value="PERCENTAGE_DISCOUNT">Phần trăm</option>
          <option value="FIXED_DISCOUNT">Giảm cố định</option>
          <option value="BUY_ONE_GET_ONE">Mua 1 tặng 1</option>
        </select>
        <label>Giá trị giảm:</label>
        <input type="number" name="discountValue" value={form.discountValue} onChange={handleChange} required />
        <label>Đơn tối thiểu:</label>
        <input type="number" name="minOrderAmount" value={form.minOrderAmount} onChange={handleChange} required />
        <label>Điểm yêu cầu:</label>
        <input type="number" name="requiredPoints" value={form.requiredPoints} onChange={handleChange} required />
        <label>
          <input type="checkbox" name="active" checked={form.active} onChange={handleChange} /> Kích hoạt
        </label>
        <button type="submit">➕ Thêm</button>
      </form>

      <h3>📋 Danh sách Voucher</h3>
      <div className="voucher-grid">
        {filtered.map(v => (
          <div className="voucher-card" key={v.id}>
            {/* Thay đổi đây: dùng imageUrl từ body */}
            <img
              src={v.imageUrl || imgdefaultVoucher}
              alt={v.code}
              className="voucher-image"
              onError={e => { e.currentTarget.src = imgdefaultVoucher; }}
            />


            {editingId === v.id ? (
              <form
                onSubmit={async (e) => {
                  e.preventDefault();
                  try {
                    await axios.put(`http://localhost:8080/api/vouchers/${v.id}`, v, { headers });
                    setMessage('✏️ Cập nhật thành công');
                    setEditingId(null);
                    await fetchAll();
                  } catch (error) {
                    console.error(error);
                    setError('❌ Cập nhật thất bại');
                  }
                }}
                className="voucher-info"
              >
                <input
                  value={v.code}
                  onChange={e => updateVoucherField(v.id, 'code', e.target.value)}
                  required
                />
                <select
                  value={v.type}
                  onChange={e => updateVoucherField(v.id, 'type', e.target.value)}
                >
                  <option value="PERCENTAGE_DISCOUNT">Phần trăm</option>
                  <option value="FIXED_DISCOUNT">Giảm cố định</option>
                  <option value="BUY_ONE_GET_ONE">Mua 1 tặng 1</option>
                </select>
                <input
                  type="number"
                  value={v.discountValue}
                  onChange={e => updateVoucherField(v.id, 'discountValue', Number(e.target.value))}
                />
                <input
                  type="number"
                  value={v.minOrderAmount}
                  onChange={e => updateVoucherField(v.id, 'minOrderAmount', Number(e.target.value))}
                />
                <input
                  type="number"
                  value={v.requiredPoints}
                  onChange={e => updateVoucherField(v.id, 'requiredPoints', Number(e.target.value))}
                />
                <label>
                  <input
                    type="checkbox"
                    checked={v.active}
                    onChange={e => updateVoucherField(v.id, 'active', e.target.checked)}
                  />
                  Kích hoạt
                </label>
                <div className="voucher-actions">
                  <button type="submit" className="btn-save">💾 Lưu</button>
                  <button type="button" onClick={() => setEditingId(null)} className="btn-cancel">❌ Hủy</button>
                </div>
              </form>
            ) : (
              <div className="voucher-info">
                <h4>{v.code}</h4>
                <p><strong>Loại:</strong> {fmtType(v.type)}</p>
                <p><strong>Giảm:</strong> {fmtVal(v)}</p>
                <p><strong>Đơn tối thiểu:</strong> {v.minOrderAmount.toLocaleString()}₫</p>
                <p><strong>Điểm yêu cầu:</strong> {v.requiredPoints}</p>
                <p><strong>Kích hoạt:</strong> {v.active ? '✅' : '❌'}</p>
                <div className="voucher-actions">
                  <button onClick={() => setEditingId(v.id)} className="btn-edit">✏️</button>
                  <button onClick={() => handleDelete(v.id)} className="btn-delete">🗑️</button>
                </div>
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
};

export default VoucherManager;
