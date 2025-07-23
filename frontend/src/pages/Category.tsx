import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import '../css/Category.css';

export interface Category {
  id: number;
  name: string;
}

const Category: React.FC = () => {
  const [categories, setCategories] = useState<Category[]>([]);
  const [filteredCategories, setFilteredCategories] = useState<Category[]>([]);
  const [form, setForm] = useState<Category>({ id: 0, name: '' });
  const [showForm, setShowForm] = useState(false);
  const [search, setSearch] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const token = sessionStorage.getItem('jwtToken');

  const fetchCategories = async () => {
    if (!token) {
      setError('Bạn chưa đăng nhập.');
      return;
    }

    setLoading(true);
    try {
      const response = await fetch('http://localhost:8080/api/categories', {
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
          'Cache-Control': 'no-cache',
        },
      });

      if (!response.ok) {
        throw new Error('Không thể lấy danh sách danh mục');
      }

      const data: Category[] = await response.json();
      setCategories(data);
      setSearch('');
      setError(null);
    } catch (err) {
      setError('Lỗi khi tải danh mục. Vui lòng thử lại.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCategories();
  }, []);

  useEffect(() => {
    const keyword = search.toLowerCase();
    const filtered = keyword
      ? categories.filter((c) => c.name.toLowerCase().includes(keyword))
      : categories;
    setFilteredCategories(filtered);
  }, [categories, search]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async () => {
    if (form.name.trim() === '') {
      setError('Tên danh mục không được để trống.');
      return;
    }

    if (!token) {
      setError('Bạn chưa đăng nhập.');
      return;
    }

    setLoading(true);
    try {
      const method = form.id === 0 ? 'POST' : 'PUT';
      const url =
        form.id === 0
          ? 'http://localhost:8080/api/categories'
          : `http://localhost:8080/api/categories/${form.id}`;

      const response = await fetch(url, {
        method,
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
          'Cache-Control': 'no-store',
        },
        body: JSON.stringify(form),
      });

      if (!response.ok) {
        throw new Error('Thao tác thất bại');
      }

      setForm({ id: 0, name: '' });
      setError(null);
      setSearch('');
      setShowForm(false);
      await fetchCategories();
    } catch (err) {
      setError('Lỗi khi gửi dữ liệu. Vui lòng thử lại.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = (cat: Category) => {
    setForm(cat);
    setError(null);
    setShowForm(true);
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('Bạn có chắc muốn xóa danh mục này?')) return;

    if (!token) {
      setError('Bạn chưa đăng nhập.');
      return;
    }

    setLoading(true);
    try {
      const response = await fetch(`http://localhost:8080/api/categories/${id}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${token}`,
          'Cache-Control': 'no-cache',
        },
      });

      if (!response.ok) {
        throw new Error('Không thể xóa danh mục');
      }

      setForm({ id: 0, name: '' });
      setError(null);
      setSearch('');
      await fetchCategories();
    } catch (err) {
      setError('Lỗi khi xóa danh mục.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearch(e.target.value);
  };

  return (
    <div className="category-container">
      <Link to="/home" className="btn btn-secondary mb-3">Quay lại trang chủ</Link>
      <h2 className="category-title">Quản lý Danh mục</h2>

      <button
        className="btn btn-primary mb-3"
        onClick={() => { setShowForm(true); setForm({ id: 0, name: '' }); setError(null); }}
        disabled={loading}
      >
        Thêm danh mục
      </button>

      {error && <div className="category-error">{error}</div>}
      {loading && <div className="category-loading">Đang tải...</div>}

      {showForm && (
        <div className="category-form">
          <input
            name="name"
            value={form.name}
            onChange={handleChange}
            placeholder="Tên danh mục"
            className="form-input"
          />
          <button
            onClick={handleSubmit}
            className="form-button add"
            disabled={loading}
          >
            {form.id === 0 ? 'Thêm' : 'Cập nhật'}
          </button>
          <button
            onClick={() => { setShowForm(false); setForm({ id: 0, name: '' }); setError(null); }}
            className="form-button cancel"
            disabled={loading}
          >
            Hủy
          </button>
        </div>
      )}

      <input
        type="text"
        value={search}
        onChange={handleSearch}
        placeholder="Tìm kiếm danh mục..."
        className="form-input search"
      />

      {filteredCategories.length === 0 ? (
        <p className="category-empty">Không có danh mục nào.</p>
      ) : (
        <table className="category-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Tên danh mục</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody>
            {filteredCategories.map((cat) => (
              <tr key={cat.id}>
                <td>{cat.id}</td>
                <td>{cat.name}</td>
                <td>
                  <button
                    onClick={() => handleEdit(cat)}
                    className="action-button edit"
                    disabled={loading}
                  >
                    Sửa
                  </button>
                  <button
                    onClick={() => handleDelete(cat.id)}
                    className="action-button delete"
                    disabled={loading}
                  >
                    Xóa
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      <footer className="text-center mt-5 text-muted">
        &copy; {new Date().getFullYear()} Nhóm 2. All rights reserved.
      </footer>
    </div>
  );
};

export default Category;
