import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useAuth } from '../hooks/useAuth';
import { Link } from 'react-router-dom';
import '../css/Dishes.css';

interface CategoryDTO {
  id?: number; // id là optional vì lúc tạo mới không cần
  name: string;
}

interface MenuItemDTO {
  id?: number;
  name: string;
  price: number;
  imageUrl: string;
  category: CategoryDTO;
}

const FoodPage: React.FC = () => {
  const { roles } = useAuth();
  const isAdmin = roles.includes('ROLE_ADMIN');
  const isStaff = roles.includes('ROLE_STAFF');

  const [danhSachMon, setDanhSachMon] = useState<MenuItemDTO[]>([]);
  const [categories, setCategories] = useState<CategoryDTO[]>([]);
  const [showCreateForm, setShowCreateForm] = useState<boolean>(false);

  const [newFood, setNewFood] = useState<MenuItemDTO>({
    name: '',
    price: 0,
    imageUrl: '',
    category: { name: '' },
  });

  const [editingId, setEditingId] = useState<number | null>(null);
  const [editFood, setEditFood] = useState<MenuItemDTO>({
    name: '',
    price: 0,
    imageUrl: '',
    category: { id: 0, name: '' },
  });

  const token = sessionStorage.getItem('jwtToken');

  useEffect(() => {
  const fetchAll = async () => {
    try {
      const res = await axios.get<CategoryDTO[]>('http://localhost:8080/api/categories', {
        headers: { Authorization: `Bearer ${token}` }
      });
      setCategories(res.data);
      await fetchFoods(res.data);
    } catch (err) {
      console.error('Lỗi khi load:', err);
    }
  };

  fetchAll();
}, []);

const fetchFoods = async (loadedCategories: CategoryDTO[]) => {
  try {
    const res = await axios.get<any[]>('http://localhost:8080/api/menu-items', {
      headers: { Authorization: `Bearer ${token}` }
    });

    const foodsWithCategory = res.data.map(food => {
      const categoryId = food.category?.id; // ⬅️ kiểm tra an toàn
      const matched = loadedCategories.find(cat => cat.id === categoryId);

      return {
        id: food.id,
        name: food.name,
        price: food.price,
        imageUrl: food.imageUrl,
        category: matched ?? { id: categoryId ?? 0, name: 'Không rõ' } // fallback nếu category null
      };
    });

    setDanhSachMon(foodsWithCategory);
  } catch (err) {
    console.error('Lỗi khi lấy món ăn:', err);
  }
};






  const fetchCategories = async () => {
    try {
      const res = await axios.get<CategoryDTO[]>('http://localhost:8080/api/categories', {
        headers: { Authorization: `Bearer ${token}` }
      });
      setCategories(res.data);
    } catch (err) {
      console.error('Lỗi khi lấy danh mục:', err);
    }
  };

  const createFood = async () => {
    try {
      const res = await axios.post<MenuItemDTO>('http://localhost:8080/api/menu-items', newFood, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setDanhSachMon(prev => [...prev, res.data]);
      setNewFood({ name: '', price: 0, imageUrl: '', category: { name: '' } });
      setShowCreateForm(false);
    } catch (err) {
      console.error('Lỗi khi tạo món ăn:', err);
    }
  };

  const deleteFood = async (id: number) => {
    if (!window.confirm('Bạn có chắc muốn xóa món này?')) return;
    try {
      await axios.delete(`http://localhost:8080/api/menu-items/${id}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setDanhSachMon(prev => prev.filter(m => m.id !== id));
    } catch (err) {
      console.error('Lỗi khi xóa món ăn:', err);
    }
  };

  const updateFood = async () => {
    if (!editingId) return;
    try {
      const res = await axios.put<MenuItemDTO>(
        `http://localhost:8080/api/menu-items/${editingId}`,
        editFood,
        {
          headers: { Authorization: `Bearer ${token}` }
        }
      );
      setDanhSachMon(prev =>
        prev.map(m => (m.id === editingId ? res.data : m))
      );
      setEditingId(null);
      setEditFood({ name: '', price: 0, imageUrl: '', category: { id: 0, name: '' } });
    } catch (err) {
      console.error('Lỗi khi cập nhật món ăn:', err);
    }
  };

  const handleCategoryChange = (name: string, isEdit = false) => {
    const selected = categories.find(c => c.name === name);
    if (!selected) return;
    if (isEdit) {
      setEditFood({ ...editFood, category: selected });
    } else {
      setNewFood({ ...newFood, category: selected });
    }
  };

  if (!isAdmin && !isStaff) {
    return <div className="container-fluid">Bạn không có quyền truy cập trang này.</div>;
  }

  return (
    <div className="container-fluid">
      <Link to="/home" className="btn btn-secondary mb-3">Quay lại trang chủ</Link>
      <h1>Danh sách Món Ăn</h1>

      {isAdmin && (
        <div className="mb-3">
          <button className="btn btn-primary" onClick={() => setShowCreateForm(true)}>
            Thêm món ăn
          </button>
        </div>
      )}

      {isAdmin && showCreateForm && (
        <div className="card mb-4">
          <div className="card-body">
            <h5>Thêm món ăn</h5>
            <input className="form-control mb-2" placeholder="Tên món" value={newFood.name} onChange={e => setNewFood({ ...newFood, name: e.target.value })} />
            <input type="number" className="form-control mb-2" placeholder="Giá" value={newFood.price} onChange={e => setNewFood({ ...newFood, price: Number(e.target.value) })} />
            <input className="form-control mb-2" placeholder="URL hình ảnh" value={newFood.imageUrl} onChange={e => setNewFood({ ...newFood, imageUrl: e.target.value })} />
            <input className="form-control mb-2" placeholder="Tên danh mục" value={newFood.category.name} onChange={e => setNewFood({ ...newFood, category: { name: e.target.value } })} />
            <button className="btn btn-success me-2" onClick={createFood}>Lưu</button>
            <button className="btn btn-secondary" onClick={() => {
              setShowCreateForm(false);
              setNewFood({ name: '', price: 0, imageUrl: '', category: { name: '' } });
            }}>Hủy</button>
          </div>
        </div>
      )}

      <table className="table table-bordered">
        <thead>
          <tr>
            <th>ID</th>
            <th>Ảnh</th>
            <th>Tên món</th>
            <th>Giá</th>
            <th>Danh mục</th>
            {isAdmin && <th>Hành động</th>}
          </tr>
        </thead>
        <tbody>
          {danhSachMon.map(mon => (
            <React.Fragment key={mon.id}>
              <tr>
                <td>{mon.id}</td>
                <td><img src={mon.imageUrl} alt={mon.name} style={{ width: 60, height: 60, objectFit: 'cover' }} /></td>
                <td>{mon.name}</td>
                <td>{mon.price.toLocaleString()}đ</td>
                <td>{mon.category?.name || 'Không có danh mục'}</td>
                {isAdmin && (
                  <td>
                    <button className="btn btn-sm btn-info me-2" onClick={() => { setEditingId(mon.id || 0); setEditFood(mon); }}>Sửa</button>
                    <button className="btn btn-sm btn-danger" onClick={() => deleteFood(mon.id!)}>Xóa</button>
                  </td>
                )}
              </tr>
              {editingId === mon.id && (
                <tr>
                  <td colSpan={6}>
                    <div className="card mt-2">
                      <div className="card-body">
                        <h5>Chỉnh sửa món ăn</h5>
                        <input className="form-control mb-2" value={editFood.name} onChange={e => setEditFood({ ...editFood, name: e.target.value })} />
                        <input type="number" className="form-control mb-2" value={editFood.price} onChange={e => setEditFood({ ...editFood, price: Number(e.target.value) })} />
                        <input className="form-control mb-2" value={editFood.imageUrl} onChange={e => setEditFood({ ...editFood, imageUrl: e.target.value })} />
                        <select className="form-control mb-2" value={editFood.category.name} onChange={e => handleCategoryChange(e.target.value, true)}>
                          <option value="">-- Chọn danh mục --</option>
                          {categories.map(c => (
                            <option key={c.id} value={c.name}>{c.name}</option>
                          ))}
                        </select>
                        <button className="btn btn-success me-2" onClick={updateFood}>Lưu</button>
                        <button className="btn btn-secondary" onClick={() => setEditingId(null)}>Hủy</button>
                      </div>
                    </div>
                  </td>
                </tr>
              )}
            </React.Fragment>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default FoodPage;
