import React, { useEffect, useState } from 'react';
import axios from 'axios';
import '../css/VoucherManager.css';
import { Link } from 'react-router-dom';
import imgDefault from '../assets/imgdefaultVoucher.png';

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

interface UsedVoucher {
  id: number;
  code: string;
  qrCode: string;
  expiryAt: string;
  voucher: Voucher;
}

const UserVoucherManager: React.FC = () => {
  const [vouchers, setVouchers] = useState<Voucher[]>([]);
  const [filtered, setFiltered] = useState<Voucher[]>([]);
  const [usedVouchers, setUsedVouchers] = useState<UsedVoucher[]>([]);
  const [qrMap, setQrMap] = useState<{ [usedVoucherId: number]: string }>({});
  const [searchTerm, setSearchTerm] = useState<string>('');
  const [error, setError] = useState<string>('');
  const [message, setMessage] = useState<string>('');
  const [loadingUsedVouchers, setLoadingUsedVouchers] = useState<boolean>(false);
  const [usedVoucherLoaded, setUsedVoucherLoaded] = useState<boolean>(false);

  const token = sessionStorage.getItem('jwtToken');
  const headers = {
    Authorization: `Bearer ${token}`,
    'Content-Type': 'application/json',
  };

  const parseJwt = (token: string): any => {
    try {
      const base64 = token.split('.')[1];
      const payload = atob(base64.replace(/-/g, '+').replace(/_/g, '/'));
      return JSON.parse(decodeURIComponent(escape(payload)));
    } catch {
      return null;
    }
  };

  const username: string = parseJwt(token || '')?.sub || '';

  useEffect(() => {
    fetchAll();
  }, []);

  const fetchAll = async () => {
    try {
      const res = await axios.get('http://localhost:8080/api/vouchers', { headers });
      setVouchers(res.data);
      setFiltered(res.data);
    } catch (err) {
      console.error(err);
      setError('❌ Không thể tải danh sách voucher');
    }
  };

  const fetchUsedVouchers = async () => {
    setLoadingUsedVouchers(true);
    setError('');
    try {
      const res = await axios.get(
        `http://localhost:8080/api/user-vouchers/${username}`,
        { headers }
      );
      setUsedVouchers(res.data);
    } catch (err) {
      console.error(err);
      setError('❌ Không thể tải các voucher đã đổi');
    } finally {
      setLoadingUsedVouchers(false);
      setUsedVoucherLoaded(true);
    }
  };

  const handleSearch = () => {
    const keyword = searchTerm.trim().toLowerCase();
    if (!keyword) return fetchAll();
    const result = vouchers.filter(v => v.code.toLowerCase().includes(keyword));
    setFiltered(result);
  };

  const handleExchange = async (voucher: Voucher) => {
    setMessage('');
    setError('');
    try {
      const res = await axios.post(
        `http://localhost:8080/api/user-vouchers/exchange?username=${encodeURIComponent(username)}&voucherCode=${encodeURIComponent(voucher.code)}`,
        {},
        { headers }
      );

      const userVoucher = res.data;
      const userVoucherId = userVoucher.id;

      const qrRes = await axios.get(
        `http://localhost:8080/api/user-vouchers/${userVoucherId}/qrcode`,
        {
          headers,
          responseType: 'arraybuffer',
        }
      );

      const arrayBuffer = qrRes.data;
      const base64 = await arrayBufferToBase64(arrayBuffer);

      setQrMap(prev => ({ ...prev, [voucher.id]: `data:image/png;base64,${base64}` }));
      setMessage('🎉 Đổi voucher thành công!');
    } catch (err: any) {
      console.error(err);
      if (err.response?.status === 400) {
        setError('⚠️ Không đủ điểm hoặc không hợp lệ.');
      } else {
        setError('❌ Lỗi khi đổi voucher.');
      }
    }
  };

  const arrayBufferToBase64 = (buffer: ArrayBuffer): Promise<string> => {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onloadend = () => {
        const base64String = reader.result as string;
        resolve(base64String.split(',')[1]);
      };
      reader.onerror = reject;
      reader.readAsDataURL(new Blob([buffer]));
    });
  };

  const fmtType = (type: string) =>
    type === 'PERCENTAGE_DISCOUNT' ? 'Phần trăm' :
    type === 'FIXED_DISCOUNT' ? 'Giảm cố định' : 'Mua 1 tặng 1';

  const fmtVal = (v: Voucher) =>
    v.type === 'PERCENTAGE_DISCOUNT' ? `${v.discountValue}%` :
    `${v.discountValue.toLocaleString()}₫`;

  return (
    <div className="container">
      <Link to="/home" className="btn btn-secondary mb-3">🏠 Quay lại trang chủ</Link>
      <h2>🎁 Voucher của bạn</h2>

      {message && <p className="message" style={{ color: 'green' }}>{message}</p>}
      {error && <p className="message" style={{ color: 'red' }}>{error}</p>}

      <div style={{ display: 'flex', gap: 8, marginBottom: 16 }}>
        <input
          value={searchTerm}
          onChange={e => setSearchTerm(e.target.value)}
          placeholder="🔍 Tìm voucher..."
        />
        <button onClick={handleSearch}>Tìm</button>
        <button onClick={fetchAll}>Làm mới</button>
      </div>

      <div className="voucher-grid">
        {filtered.map(v => (
          <div className="voucher-card" key={v.id}>
            <img
              src={v.imageUrl || imgDefault}
              alt={v.code}
              className="voucher-image"
              onError={e => { (e.currentTarget as HTMLImageElement).src = imgDefault; }}
            />
            <div className="voucher-info">
              <h4>{v.code}</h4>
              <p><strong>Loại:</strong> {fmtType(v.type)}</p>
              <p><strong>Giảm:</strong> {fmtVal(v)}</p>
              <p><strong>Đơn tối thiểu:</strong> {v.minOrderAmount.toLocaleString()}₫</p>
              <p><strong>Điểm yêu cầu:</strong> {v.requiredPoints}</p>
              <p><strong>Trạng thái:</strong> {v.active ? '✅ Đang hoạt động' : '❌ Ngưng'}</p>
              <button onClick={() => handleExchange(v)}>🎫 Đổi Voucher</button>
            </div>
          </div>
        ))}
      </div>

      <button onClick={fetchUsedVouchers} style={{ marginTop: '16px' }}>
        Xem voucher đã đổi
      </button>

      {loadingUsedVouchers ? (
        <p>Đang tải...</p>
      ) : usedVoucherLoaded && (
        <div className="voucher-grid">
          {usedVouchers.length > 0 ? (
            usedVouchers.map(usedVoucher => {
              const isQrVisible = qrMap[usedVoucher.id];

              const handleShowQr = async () => {
                try {
                  const qrRes = await axios.get(
                    `http://localhost:8080/api/user-vouchers/${usedVoucher.id}/qrcode`,
                    {
                      headers,
                      responseType: 'arraybuffer',
                    }
                  );
                  const base64 = await arrayBufferToBase64(qrRes.data);
                  setQrMap(prev => ({ ...prev, [usedVoucher.id]: `data:image/png;base64,${base64}` }));
                } catch (err) {
                  console.error('Lỗi khi tải QR code', err);
                  setError('Không thể tải mã QR.');
                }
              };

              return (
                <div className="voucher-card" key={usedVoucher.id}>
                  <img
                    src={usedVoucher.voucher.imageUrl || imgDefault}
                    alt={usedVoucher.voucher.code}
                    className="voucher-image"
                    onError={e => { (e.currentTarget as HTMLImageElement).src = imgDefault; }}
                  />
                  <div className="voucher-info">
                    <h4>{usedVoucher.voucher.code}</h4>
                    <p><strong>Loại:</strong> {fmtType(usedVoucher.voucher.type)}</p>
                    <p><strong>Giảm:</strong> {fmtVal(usedVoucher.voucher)}</p>
                    <p><strong>Đơn tối thiểu:</strong> {usedVoucher.voucher.minOrderAmount.toLocaleString()}₫</p>
                    <p><strong>Điểm yêu cầu:</strong> {usedVoucher.voucher.requiredPoints}</p>
                    <p><strong>Hết hạn:</strong> {new Date(usedVoucher.expiryAt).toLocaleString()}</p>

                    {!isQrVisible && (
                      <button onClick={handleShowQr}>🔍 Xem QR</button>
                    )}

                    {isQrVisible && (
                      <img
                        src={qrMap[usedVoucher.id]}
                        alt="QR Code"
                        style={{ width: '120px', marginTop: '8px' }}
                      />
                    )}
                  </div>
                </div>
              );
            })
          ) : (
            <p>Chưa có voucher nào đã đổi.</p>
          )}
        </div>
      )}
    </div>
  );
};

export default UserVoucherManager;
