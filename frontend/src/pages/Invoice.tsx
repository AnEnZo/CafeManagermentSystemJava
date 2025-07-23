import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import '../css/Invoice.css';

interface Invoice {
  id: number;
  discountAmount: number;
  originalAmount: number;
  paymentMethod: string;
  paymentTime: string;
  totalAmount: number;
  cashierId: number | null;
  orderId: number | null;
  voucherId: number | null;
  tableName: string | null;
}

interface ApiResponse {
  content: Invoice[];
  pageable: any;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

const InvoicePage: React.FC = () => {
  const [invoices, setInvoices] = useState<Invoice[]>([]);
  const [invoiceId, setInvoiceId] = useState<number | null>(null);
  const [error, setError] = useState<string | null>(null);

  const mapInvoiceData = (item: any): Invoice => ({
    id: item.id,
    discountAmount: item.discountAmount ?? item.discount_amount ?? 0,
    originalAmount: item.originalAmount ?? item.original_amount ?? 0,
    paymentMethod: item.paymentMethod ?? item.payment_method ?? '',
    paymentTime: item.paymentTime ?? item.payment_time ?? '',
    totalAmount: item.totalAmount ?? item.total_amount ?? 0,
    orderId: item.order?.id ?? null,
    voucherId: item.voucher?.id ?? null,
    cashierId: item.order?.staff?.id ?? null,
    tableName: item.order?.table?.name ?? null,
  });

  useEffect(() => {
    fetchInvoices();
  }, []);

  const fetchInvoices = async () => {
    try {
      const response = await axios.get<ApiResponse>(
        `http://localhost:8080/api/invoices?_=${new Date().getTime()}`,
        {
          headers: {
            Authorization: `Bearer ${sessionStorage.getItem('jwtToken')}`,
          },
        }
      );

      const invoiceData = response.data.content || [];
      const formatted = invoiceData.map(mapInvoiceData);
      setInvoices(formatted);
    } catch (error: any) {
      console.error('Lỗi khi lấy hóa đơn:', error.response?.data || error.message);
      setError('Không thể tải danh sách hóa đơn.');
    }
  };

  const fetchInvoiceById = async (id: number) => {
    try {
      const response = await axios.get<Invoice>(
        `http://localhost:8080/api/invoices/${id}`,
        {
          headers: {
            Authorization: `Bearer ${sessionStorage.getItem('jwtToken')}`,
          },
        }
      );
      const formatted = mapInvoiceData(response.data);
      setInvoices([formatted]);
      setError(null);
    } catch (error: any) {
      console.error('Không tìm thấy hóa đơn:', error);
      setInvoices([]);
      setError(`Không tìm thấy hóa đơn với ID: ${id}`);
    }
  };

  const exportExcel = async (id: number) => {
    try {
      const response = await axios.get(
        `http://localhost:8080/api/invoices/${id}/export/excel`,
        {
          headers: {
            Authorization: `Bearer ${sessionStorage.getItem('jwtToken')}`,
          },
          responseType: 'blob',
        }
      );

      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `invoice_${id}.xlsx`);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error('Lỗi khi xuất Excel:', error);
    }
  };

  const exportWord = async (id: number) => {
    try {
      const response = await axios.get(
        `http://localhost:8080/api/invoices/${id}/export/word`,
        {
          headers: {
            Authorization: `Bearer ${sessionStorage.getItem('jwtToken')}`,
          },
          responseType: 'blob',
        }
      );

      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `invoice_${id}.docx`);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (error: any) {
      console.error('Lỗi khi xuất Word:', error);
      setError(`Không thể xuất file Word: ${error.response?.statusText || error.message}`);
    }
  };

  const deleteInvoice = async (id: number) => {
    if (!window.confirm(`Bạn có chắc muốn xóa hóa đơn #${id}?`)) return;

    try {
      await axios.delete(`http://localhost:8080/api/invoices?invoiceId=${id}`, {
        headers: {
          Authorization: `Bearer ${sessionStorage.getItem('jwtToken')}`,
        },
      });

      setInvoices((prev) => prev.filter((invoice) => invoice.id !== id));
    } catch (error: any) {
      console.error('Lỗi khi xóa hóa đơn:', error);
      setError(`Không thể xóa hóa đơn: ${error.response?.statusText || error.message}`);
    }
  };

  return (
    <div className="invoice-page container-fluid p-4">
      <Link to="/home" className="btn btn-secondary mb-3">Quay lại trang chủ</Link>
      <h2 className="mb-4">Danh sách Hóa đơn</h2>
      {error && <div className="alert alert-danger">{error}</div>}
      <div className="d-flex align-items-center gap-2 mb-3">
        <input
          type="number"
          value={invoiceId || ''}
          onChange={(e) => setInvoiceId(Number(e.target.value))}
          placeholder="Nhập ID hóa đơn"
          className="form-control w-auto"
        />
        <button className="btn btn-outline-secondary" onClick={() => invoiceId && fetchInvoiceById(invoiceId)}>Tìm</button>
        <button className="btn btn-outline-warning" onClick={() => { setInvoiceId(null); fetchInvoices(); }}>Reset</button>
      </div>

      <div className="table-responsive shadow bg-white p-3 rounded">
        <table className="table">
          <thead className="table-dark">
            <tr>
              <th>ID</th>
              <th>Giá gốc</th>
              <th>Giảm giá</th>
              <th>Tổng tiền</th>
              <th>Phương thức thanh toán</th>
              <th>Thời gian</th>
              <th>Thu ngân (Cashier ID)</th>
              <th>Order ID</th>
              <th>Voucher ID</th>
              <th>Tên bàn</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            {invoices.length > 0 ? (
              invoices.map((invoice) => (
                <tr key={invoice.id}>
                  <td>{invoice.id}</td>
                  <td>{invoice.originalAmount.toLocaleString()}</td>
                  <td>{invoice.discountAmount.toLocaleString()}</td>
                  <td>{invoice.totalAmount.toLocaleString()}</td>
                  <td>{invoice.paymentMethod}</td>
                  <td>{new Date(invoice.paymentTime).toLocaleString()}</td>
                  <td>{invoice.cashierId ?? '-'}</td>
                  <td>{invoice.orderId ?? '-'}</td>
                  <td>{invoice.voucherId ?? '-'}</td>
                  <td>{invoice.tableName ?? '-'}</td>
                  <td>
                    <button className="btn btn-sm btn-outline-success me-1" onClick={() => exportExcel(invoice.id)}>Excel</button>
                    <button className="btn btn-sm btn-outline-primary me-1" onClick={() => exportWord(invoice.id)}>Word</button>
                    <button className="btn btn-sm btn-outline-danger" onClick={() => deleteInvoice(invoice.id)}>Xóa</button>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={11}>Không có hóa đơn nào.</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      <footer className="text-center mt-5 text-muted">
        &copy; {new Date().getFullYear()} Nhóm 2. All rights reserved.
      </footer>
    </div>
  );
};

export default InvoicePage;
