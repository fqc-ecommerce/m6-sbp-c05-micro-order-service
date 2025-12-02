-- ============================================
-- Migration: V3__INSERT_DATA.sql (H2)
-- ============================================
-- Datos de prueba para entorno local (H2)

-- Insertar datos en la tabla orders
-- Nota: En H2, necesitamos usar la cláusula IDENTITY_INSERT para insertar valores en columnas autoincrementales
SET REFERENTIAL_INTEGRITY FALSE;

-- Vaciar las tablas primero para evitar duplicados
TRUNCATE TABLE order_items;
TRUNCATE TABLE orders;

-- Reiniciar los contadores de identidad
ALTER TABLE orders ALTER COLUMN id RESTART WITH 1;
ALTER TABLE order_items ALTER COLUMN id RESTART WITH 1;

-- Insertar órdenes
INSERT INTO orders (id, order_number, user_id, status, total_amount, created_at, updated_at) VALUES
(1, 'ORD-2025-001', 1, 'CONFIRMED', 2849.97, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'ORD-2025-002', 2, 'PENDING', 1199.98, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'ORD-2025-003', 1, 'SHIPPED', 149.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insertar items de órdenes
INSERT INTO order_items (id, order_id, product_id, quantity, unit_price, subtotal) VALUES
(1, 1, 1, 1, 1299.99, 1299.99),
(2, 1, 2, 1, 999.99, 999.99),
(3, 1, 3, 1, 399.99, 399.99),
(4, 2, 4, 1, 799.99, 799.99),
(5, 2, 5, 1, 399.00, 399.00),
(6, 3, 7, 1, 149.99, 149.99);

-- Restaurar la integridad referencial
SET REFERENTIAL_INTEGRITY TRUE;