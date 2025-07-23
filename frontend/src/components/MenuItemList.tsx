import React, { useEffect, useState } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import '../css/MenuItemList.css';
import { motion, AnimatePresence } from 'framer-motion';

export interface MenuItem { id: number; name: string; price: number; imageUrl: string; }

interface Props { categoryName: string; orderId: string; onChange: () => void; }

const ITEMS_PER_PAGE = 6;

// Sao chép helper parseJwt từ Home
function parseJwt(token: string): any | null {
  try {
    const base64Payload = token.split('.')[1];
    const payload = atob(base64Payload.replace(/-/g, '+').replace(/_/g, '/'));
    const utf8 = decodeURIComponent(
      Array.prototype.map
        .call(payload, (c: string) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    return JSON.parse(utf8);
  } catch {
    return null;
  }
}


// Simple professional slide-fade animation for large projects
const pageVariants = {
    enter: { opacity: 0 },
    center: { opacity: 1 },
    exit:  { opacity: 0 },
};

const pageTransition = {
  type: 'tween',
  ease: 'easeOut',
  duration: 0.4,
};

const MenuItemList: React.FC<Props> = ({ categoryName, orderId, onChange }) => {
  const [items, setItems] = useState<MenuItem[]>([]);
  const [quantities, setQuantities] = useState<Record<number, number>>({});
  const [page, setPage] = useState<number>(0);
  const token = sessionStorage.getItem('jwtToken') || '';
  const { roles } = React.useMemo(() => {
  const raw = token.startsWith('Bearer ') ? token.slice(7) : token;
  const decoded = parseJwt(raw);
  return { roles: decoded?.roles ?? [] as string[] };
}, [token]);

const isUser = roles.length === 1 && roles.includes('ROLE_USER');

  useEffect(() => {
    if (!categoryName) return;
    fetch(`http://localhost:8080/api/menu-items/category/${categoryName}`, {
      headers: { Authorization: `Bearer ${sessionStorage.getItem('jwtToken')}` }
    })
      .then(res => res.json())
      .then(data => {
        setItems(data);
        const initial: Record<number, number> = {};
        data.forEach((it: MenuItem) => { initial[it.id] = 1; });
        setQuantities(initial);
        setPage(0);
      })
      .catch(console.error);
  }, [categoryName]);

  const handleQtyChange = (id: number, value: number) => {
    setQuantities(prev => ({ ...prev, [id]: value }));
  };

  const addItem = (id: number) => {
    const qty = quantities[id] || 1;
    const url = `http://localhost:8080/api/orders/${orderId}/items?menuItemId=${id}&quantity=${qty}`.replace(/\s+/g, '');
    fetch(url, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${sessionStorage.getItem('jwtToken')}`
      }
    })
      .then(res => { if (!res.ok) throw new Error(); return res.json(); })
      .then(() => onChange())
      .catch(console.error);
  };

  const removeItem = (id: number) => {
    const qty = quantities[id] || 1;
    const url = `http://localhost:8080/api/orders/${orderId}/items?menuItemId=${id}&quantity=${qty}`.replace(/\s+/g, '');
    fetch(url, {
      method: 'DELETE',
      headers: {
        Authorization: `Bearer ${sessionStorage.getItem('jwtToken')}`
      }
    })
      .then(res => res.status === 204 ? null : res.json())
      .then(() => onChange())
      .catch(console.error);
  };

  const pageCount = Math.ceil(items.length / ITEMS_PER_PAGE);
  const pagedItems = items.slice(page * ITEMS_PER_PAGE, (page + 1) * ITEMS_PER_PAGE);

  const changePage = (newPage: number) => {
    if (newPage < 0 || newPage >= pageCount) return;
    setPage(newPage);
  };

  return (
    <div>
      <h5 className="mb-3">Món: <span className="text-primary">{categoryName}</span></h5>
      {/* Pagination Controls */}
            <div className="d-flex justify-content-center align-items-center mt-3">
              <button
                className="btn btn-outline-primary me-2"
                onClick={() => changePage(page - 1)}
                disabled={page === 0}
              >
                &laquo; Trước
              </button>
              <span>Trang {page + 1} / {pageCount}</span>
              <button
                className="btn btn-outline-primary ms-2"
                onClick={() => changePage(page + 1)}
                disabled={page === pageCount - 1}
              >
                Sau &raquo;
              </button>
            </div>

      <div className="container px-0 menu-pagination-container">
        <AnimatePresence exitBeforeEnter>
          <motion.div
            key={page}
            className="row g-3 page-content"
            variants={pageVariants}
            initial="enter"
            animate="center"
            exit="exit"
            transition={pageTransition}
          >
            {pagedItems.map(it => (
              <div className="col-6" key={it.id}>
                <div className="card shadow-sm menu-card h-100">
                  <img src={it.imageUrl} className="card-img-top" alt={it.name} />
                  <div className="card-body d-flex flex-column">
                    <h6 className="card-title">{it.name}</h6>
                    <p className="card-text fw-bold flex-grow-1">{it.price.toLocaleString()}₫</p>

                    {!isUser && (
                    <div className="d-flex align-items-center mb-2">
                      <label htmlFor={`qty-${it.id}`} className="me-2">Số lượng:</label>
                      <input
                        id={`qty-${it.id}`} type="number" min={1}
                        className="form-control form-control-sm" style={{ width: '4rem' }}
                        value={quantities[it.id]}
                        onChange={e => handleQtyChange(it.id, Math.max(1, parseInt(e.target.value)))}
                      />
                    </div>
                     )}
                    {!isUser && (
                      <div className="btn-group">
                        <button className="btn btn-sm btn-success" onClick={() => addItem(it.id)}>Thêm</button>
                        <button className="btn btn-sm btn-danger" onClick={() => removeItem(it.id)}>Xóa</button>
                      </div>
                    )}

                  </div>
                </div>
              </div>
            ))}
          </motion.div>
        </AnimatePresence>
      </div>

    </div>
  );
};

export default MenuItemList;


