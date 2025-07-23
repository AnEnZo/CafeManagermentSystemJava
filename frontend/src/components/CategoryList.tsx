// src/components/CategoryList.tsx
import React, { useEffect, useState } from 'react';

export interface Category { id: number; name: string; }

interface Props { onSelect: (cat: Category) => void; }


const CategoryList: React.FC<Props> = ({ onSelect }) => {
  const [categories, setCategories] = useState<Category[]>([]);
  const [selectedId, setSelectedId] = useState<number | null>(null);

  useEffect(() => {
    fetch('http://localhost:8080/api/categories', {
      headers: { Authorization: `Bearer ${sessionStorage.getItem('jwtToken')}` }
    })
      .then(res => res.json())
      .then(setCategories)
      .catch(console.error);
  }, []);

  return (
    <ul className="list-group mb-4 ">
      {categories.map(cat => (
        <li
          key={cat.id}
          className={`list-group-item list-group-item-action ${selectedId === cat.id ? 'active' : ''}`}
          onClick={() => { setSelectedId(cat.id); onSelect(cat); }}
          style={{ cursor: 'pointer' }}
        >
          {cat.name}
        </li>
      ))}
    </ul>
  );
};
export default CategoryList;
