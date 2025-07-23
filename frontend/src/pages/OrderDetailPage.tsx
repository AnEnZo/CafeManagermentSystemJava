import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import CategoryList, { Category } from '../components/CategoryList';
import MenuItemList from '../components/MenuItemList';
import '../css/OrderDetailPage.css';

interface OrderItem {
  id?: number;
  quantity: number;
  menuItem: {
    id?: number;
    name: string;
    price?: number;
    imageUrl?: string;
  };
}

interface OrderDetail {
  id?: number;
  orderTime: string;
  status: string;
  orderType: string;
  table?: { id?: number; name: string };
  staff: { id?: number; displayName: string };
  orderItems: OrderItem[];
}

const OrderDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [order, setOrder] = useState<OrderDetail | null>(null);
  const [selectedCat, setSelectedCat] = useState<Category | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [voucherCode, setVoucherCode] = useState<string>('');
  const [paymentMethod, setPaymentMethod] = useState<string>('');
  const [phoneNumber, setPhoneNumber] = useState<string>('');
  const [cashierId, setCashierId] = useState<number | null>(null);
  const navigate = useNavigate();
  const [discountAmount, setDiscountAmount] = useState<number>(0);
  const [finalAmount, setFinalAmount] = useState<number>(0);

  const fetchOrder = () => {
    setLoading(true);
    fetch(`http://localhost:8080/api/orders/${id}`, {
      headers: { Authorization: `Bearer ${sessionStorage.getItem('jwtToken')}` },
    })
      .then(res => {
        if (!res.ok) throw new Error('Không lấy được đơn');
        return res.json();
      })
      .then((data: OrderDetail) => {
            setOrder(data);
            if (data.staff?.id) setCashierId(data.staff.id);
          })
      .catch(err => setError(err.message))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchOrder();
  }, [id]);

  const handleCreateInvoice = () => {

    if (cashierId === null) {
      setError('Không có ID thu ngân, vui lòng đăng nhập lại');
      return;
    }
    if (!paymentMethod) {
      setError('Vui lòng chọn phương thức thanh toán');
      return;
    }

    const params = new URLSearchParams();
    params.append('orderId', id!);
    if (voucherCode) params.append('voucherCode', voucherCode);
    params.append('cashierId', cashierId.toString());
    params.append('paymentMethod', paymentMethod);
    if (phoneNumber) params.append('phoneNumber', phoneNumber);

    fetch('http://localhost:8080/api/invoices', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
        Authorization: `Bearer ${sessionStorage.getItem('jwtToken')}`,
      },
      body: params,
    })
      .then(res => {
        if (!res.ok) throw new Error('Không thể tạo hóa đơn');
        return res.json();
      })
      .then(() => {
        alert('Hóa đơn đã được tạo thành công');
        navigate('/orders');
      })
      .catch(err => setError(err.message));
  };

    const handleApplyVoucher = () => {
      if (!voucherCode) {
        setError('Vui lòng nhập mã voucher');
        return;
      }

      fetch(`http://localhost:8080/api/invoices/calculate?orderId=${id}&voucherCode=${voucherCode}`, {
        headers: {
          Authorization: `Bearer ${sessionStorage.getItem('jwtToken')}`,
        },
      })
        .then(res => {
          if (!res.ok) throw new Error('Voucher không hợp lệ hoặc đã hết hạn');
          return res.json();
        })
        .then(data => {
          setDiscountAmount(data.discountAmount);
          setFinalAmount(data.totalAmount);
          setError(null); // reset lỗi nếu có
        })
        .catch(err => {
          setError(err.message);
          setDiscountAmount(0);
          setFinalAmount(0);
        });
    };


  if (loading) return <div className="order-detail-page p-4">Loading...</div>;
  if (error) return <div className="order-detail-page p-4 text-danger">{error}</div>;
  if (!order) return <div className="order-detail-page p-4">No order found.</div>;

  const totalAmount = order.orderItems.reduce(
    (sum, it) => sum + (it.menuItem.price || 0) * it.quantity,
    0
  );

  return (
    <div className="container-fluid p-4">
      <button className="btn btn-link mb-3" onClick={() => navigate('/orders')}>
        ← Back
      </button>
      <h2>Order #{order.id}</h2>

      <div className="row">
        <div className="col-md-4">
          <CategoryList onSelect={setSelectedCat} />
          {selectedCat && (
            <MenuItemList
              categoryName={selectedCat.name}
              orderId={id!}
              onChange={fetchOrder}
            />
          )}
        </div>

        <div className="col-md-8">
          <p>Time: {new Date(order.orderTime).toLocaleString()}</p>
          <p>Type: {order.orderType}</p>
          <p>Status: {order.status}</p>
          <p>Table: {order.table?.name || 'N/A'}</p>
          <p>Staff: {order.staff.displayName}</p>

          <h4>Items</h4>
          <table className="table table-bordered">
            <thead>
              <tr>
                <th>Name</th>
                <th>Quantity</th>
                <th>Unit Price</th>
                <th>Subtotal</th>
              </tr>
            </thead>

            <tbody>
              {order.orderItems.map((it, idx) => (
                <tr key={it.id ?? idx}>
                  <td>{it.menuItem.name}</td>
                  <td>{it.quantity}</td>
                  <td>{it.menuItem.price?.toLocaleString() || '0'}</td>
                  <td>{((it.menuItem.price || 0) * it.quantity).toLocaleString()}</td>
                </tr>
              ))}
            </tbody>

            <tfoot>
              <tr>
                <th colSpan={3} className="text-end">Total:</th>
                <th>{totalAmount.toLocaleString()}</th>
              </tr>
              {discountAmount > 0 && (
                <>
                  <tr>
                    <th colSpan={3} className="text-end">Giảm giá:</th>
                    <th>-{discountAmount.toLocaleString()} VND</th>
                  </tr>
                  <tr>
                    <th colSpan={3} className="text-end">Tổng sau giảm:</th>
                    <th>{finalAmount.toLocaleString()} VND</th>
                  </tr>
                </>
              )}
            </tfoot>
          </table>


          {order.status === 'SERVED' && (
            <>
              <h4>Tạo hóa đơn</h4>
              <div className="mb-3">
                <label htmlFor="voucherCode" className="form-label">Mã voucher</label>
                <div className="input-group">
                  <input
                    type="text"
                    className="form-control"
                    id="voucherCode"
                    value={voucherCode}
                    onChange={e => setVoucherCode(e.target.value)}
                    placeholder="Nhập mã voucher (nếu có)"
                  />
                  <button className="btn btn-outline-secondary" type="button" onClick={handleApplyVoucher}>
                    Áp dụng
                  </button>
                </div>
              </div>

              <div className="mb-3">
                <label htmlFor="paymentMethod" className="form-label">
                  Phương thức thanh toán
                </label>
                <select
                  className="form-select"
                  id="paymentMethod"
                  value={paymentMethod}
                  onChange={e => setPaymentMethod(e.target.value)}
                >
                  <option value="">Chọn phương thức</option>
                  <option value="CASH">Tiền mặt</option>
                  <option value="TRANSFER">Chuyển khoản</option>
                  <option value="CARD">Thẻ tín dụng</option>
                </select>
              </div>
              <div className="mb-3">
                <label htmlFor="phoneNumber" className="form-label">
                  Số điện thoại khách hàng
                </label>
                <input
                  type="text"
                  className="form-control"
                  id="phoneNumber"
                  value={phoneNumber}
                  onChange={e => setPhoneNumber(e.target.value)}
                  placeholder="Nhập số điện thoại (nếu có)"
                />
              </div>
              <button
                className="btn btn-primary"
                onClick={handleCreateInvoice}
                disabled={!paymentMethod}
              >
                Tạo hóa đơn
              </button>
            </>
          )}
        </div>
      </div>
    </div>
  );
};

export default OrderDetailPage;