import React, { useEffect, useState } from 'react';
import axios from 'axios';
import {
  BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid,
  ResponsiveContainer, Legend
} from 'recharts';
import '../css/Revenue.css';
import { useNavigate } from 'react-router-dom';

interface RevenueData {
  label: string;
  revenue: number;
}

const Revenue: React.FC = () => {
  const [mode, setMode] = useState<'year' | 'month'>('year');
  const [year, setYear] = useState<number>(new Date().getFullYear());
  const [month, setMonth] = useState<number>(new Date().getMonth() + 1);
  const [data, setData] = useState<RevenueData[]>([]);
  const navigate = useNavigate();

  const token = sessionStorage.getItem("jwtToken");

  const fetchYearData = async () => {
    const results: RevenueData[] = [];
    for (let m = 1; m <= 12; m++) {
      try {
        const response = await axios.get<number>(
          `http://localhost:8080/api/revenues/month?month=${m}&year=${year}`,
          {
            headers: { Authorization: `Bearer ${token}` }
          }
        );
        results.push({ label: `Tháng ${m}`, revenue: response.data });
      } catch (error) {
        console.error("Lỗi khi lấy dữ liệu tháng", m, error);
        results.push({ label: `Tháng ${m}`, revenue: 0 });
      }
    }
    setData(results);
  };

  const fetchMonthData = async () => {
    try {
      const response = await axios.get(
        `http://localhost:8080/api/revenues/daily?month=${month}&year=${year}`,
        {
          headers: { Authorization: `Bearer ${token}` }
        }
      );
      const mapped: RevenueData[] = response.data.map((d: any) => {
        const day = new Date(d.date).getDate();
        return {
          label: `Ngày ${day}`,
          revenue: d.amount
        };
      });
      setData(mapped);
    } catch (error) {
      console.error("❌ Lỗi khi gọi API /daily:", error);
      setData([]);
    }
  };

  useEffect(() => {
    if (mode === 'year') {
      fetchYearData();
    } else {
      fetchMonthData();
    }
  }, [mode, year, month]);

  return (
    <div className="revenue-container">
      <button className="back-button" onClick={() => navigate('/home')}>
        ← Quay lại trang chủ
      </button>

      <h2 className="title">Biểu đồ doanh thu</h2>

      <div className="revenue-controls">
        {mode !== 'year' && (
          <button
            className="mode-button"
            onClick={() => setMode('year')}
          >
            + Xem theo năm
          </button>
        )}

        {mode !== 'month' && (
          <button
            className="mode-button"
            onClick={() => setMode('month')}
          >
            + Xem theo tháng
          </button>
        )}

        <input
          type="number"
          value={year}
          min={2000}
          max={2100}
          onChange={(e) => setYear(Number(e.target.value))}
          className="year-input"
        />

        {mode === 'month' && (
          <select
            value={month}
            onChange={(e) => setMonth(Number(e.target.value))}
            className="month-select"
          >
            {Array.from({ length: 12 }, (_, i) => (
              <option key={i + 1} value={i + 1}>
                Tháng {i + 1}
              </option>
            ))}
          </select>
        )}
      </div>

      <ResponsiveContainer width="100%" height={400}>
        <BarChart
          data={data}
          onClick={(e) => {
            if (mode === 'year' && e?.activeLabel) {
              const matched = (e.activeLabel as string).match(/Tháng (\d+)/);
              if (matched) {
                const clickedMonth = parseInt(matched[1]);
                setMonth(clickedMonth);
                setMode('month');
              }
            }
          }}
        >
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="label" />
          <YAxis />
          <Tooltip />
          <Legend />
          <Bar dataKey="revenue" fill="#4CAF50" name="Doanh thu" />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
};

export default Revenue;
