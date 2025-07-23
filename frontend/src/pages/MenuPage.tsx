import React, { useState } from 'react';
import { Container, Row, Col, Alert, Card, InputGroup, FormControl, Breadcrumb } from 'react-bootstrap';
import { FaSearch } from 'react-icons/fa';
import CategoryList, { Category } from '../components/CategoryList';
import MenuItemList from '../components/MenuItemList';

// Page for browsing menu: categories on left, items on right (no order creation)
const MenuPage: React.FC = () => {
  const [selectedCategory, setSelectedCategory] = useState<Category | null>(null);
  const [searchTerm, setSearchTerm] = useState<string>('');

  const handleCategorySelect = (cat: Category) => {
    setSelectedCategory(cat);
    setSearchTerm('');
  };

  return (
    <Container fluid className="mt-4">
      <Row className="mb-3">
        <Col>
          <Breadcrumb>
            <Breadcrumb.Item href="/home">Trang chủ</Breadcrumb.Item>
            <Breadcrumb.Item active>Xem Menu</Breadcrumb.Item>
          </Breadcrumb>
        </Col>
      </Row>
      <Row>
        <Col md={3} className="border-end">
          <Card className="p-3">
            <Card.Title>Danh mục</Card.Title>
            <CategoryList onSelect={handleCategorySelect} />
          </Card>
        </Col>
        <Col md={9}>
          {selectedCategory ? (
            <>
              <Row className="mb-2 align-items-center">
                <Col>
                  <h4 className="text-primary">{selectedCategory.name}</h4>
                </Col>
                <Col md={6}>
                  <InputGroup>
                    <InputGroup.Text><FaSearch /></InputGroup.Text>
                    <FormControl
                      placeholder="Tìm món..."
                      value={searchTerm}
                      onChange={e => setSearchTerm(e.target.value)}
                    />
                  </InputGroup>
                </Col>
              </Row>
              <MenuItemList
                categoryName={selectedCategory.name}
                searchTerm={searchTerm}
              />
            </>
          ) : (
            <Alert variant="info" className="text-center">
              Vui lòng chọn một danh mục để xem món.
            </Alert>
          )}
        </Col>
      </Row>
    </Container>
  );
};

export default MenuPage;
