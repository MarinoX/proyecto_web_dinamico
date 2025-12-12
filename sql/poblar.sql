
INSERT INTO categoria (nombre, descripcion) VALUES
('Bebidas', 'Bebidas variadas y refrescos'),
('Snacks', 'Productos de botana y aperitivos'),
('Confiteria', 'Dulces y golosinas');

INSERT INTO producto (nombre, descripcion, precio, stock, id_categoria) VALUES
('Coca Cola 2L', 'Refresco cola 2 litros', 35.50, 20, 1),
('Sprite 2L', 'Refresco lima limon 2 litros', 32.00, 15, 1),
('Jugo Natural Naranja', 'Jugo de naranja recien exprimido', 25.00, 10, 1),
('Papas Fritas Barbecue', 'Papas crujientes sabor barbecue', 18.50, 30, 2),
('Cacahuates Salados', 'Cacahuates 250 gramos', 22.00, 25, 2),
('Churros de Maiz', 'Churros crujientes de maiz', 15.00, 40, 2),
('Chocolate Amargo', 'Tableta chocolate 70 por ciento cacao', 45.00, 12, 3),
('Caramelos Variados', 'Bolsa 500 gramos caramelos surtidos', 28.00, 35, 3),
('Gomitas Frutal', 'Gomitas sabor frutal 300 gramos', 20.00, 28, 3),
('Galletas Integrales', 'Galletas con avena integral 400 gramos', 32.50, 18, 3);
