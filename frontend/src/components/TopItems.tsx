import React, { useEffect, useState } from 'react';

interface ItemStatsDTO {
  itemName: string;
  quantitySold: number;
  price: string;
  imageUrl: string;
}

interface TopItemsProps {
  topN?: number;
}

const getRankSuffix = (rank: number) => {
  if (rank === 1) return 'st';
  if (rank === 2) return 'nd';
  if (rank === 3) return 'rd';
  return 'th';
};

const TopItems: React.FC<TopItemsProps> = ({ topN = 3 }) => {
  const token = sessionStorage.getItem('jwtToken');
  const [items, setItems] = useState<ItemStatsDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  if (!token) return null;

  useEffect(() => {
    fetch(`http://localhost:8080/api/revenues/items/top?topN=${topN}`, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(res => {
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        return res.json();
      })
      .then((data: ItemStatsDTO[]) => {
        setItems(data);
        setLoading(false);
      })
      .catch(err => {
        console.error(err);
        setError('Không tải được dữ liệu.');
        setLoading(false);
      });
  }, [token, topN]);

  if (loading) return <p>Đang tải món bán chạy...</p>;
  if (error) return <p className="text-danger">{error}</p>;

  return (
    <section className="top-items container my-5">
      <h2 className="mb-4">Best Seller</h2>
      <div className="row">
        {items.map((item, index) => {
          const rank = index + 1;
          const suffix = getRankSuffix(rank);

          return (
            <div key={item.itemName} className="col-md-4 mb-4">
              <div className="card h-100 position-relative shadow-sm">
                {/* Badge số thứ tự */}
                <div
                  className="position-absolute top-0 start-0 bg-warning text-dark px-3 py-1 rounded-bottom-end fw-bold"
                  style={{ zIndex: 1 }}
                >
                  {rank}
                  <sup>{suffix}</sup>
                </div>

                <img
                  src={item.imageUrl}
                  className="card-img-top"
                  alt={item.itemName}
                  style={{ objectFit: 'cover', height: '200px' }}
                />

                <div className="card-body d-flex flex-column">
                  <h5 className="card-title">{item.itemName}</h5>
                  <p className="card-text mb-1">Đã bán: {item.quantitySold}</p>
                  <p className="card-text mb-2">
                    Giá: {Number(item.price).toLocaleString('vi-VN')}₫
                  </p>
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </section>
  );
};

export default TopItems;
