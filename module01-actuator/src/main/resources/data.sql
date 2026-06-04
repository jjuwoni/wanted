INSERT INTO products (id, name, price, stock, created_at) VALUES
    (1, 'MacBook Pro', 3000000, 100, NOW()),
    (2, 'iPhone 16', 1500000, 100, NOW()),
    (3, 'AirPods Pro', 350000, 100, NOW()),
    (4, 'iPad Air', 1000000, 100, NOW()),
    (5, 'Apple Watch', 650000, 100, NOW()),
    (6, 'Magic Keyboard', 150000, 100, NOW()),
    (7, 'Studio Display', 2200000, 100, NOW());

INSERT INTO orders (id, created_at) VALUES
    (1, DATE_SUB(NOW(), INTERVAL 1 DAY)),
    (2, DATE_SUB(NOW(), INTERVAL 2 DAY)),
    (3, DATE_SUB(NOW(), INTERVAL 3 DAY)),
    (4, DATE_SUB(NOW(), INTERVAL 4 DAY)),
    (5, DATE_SUB(NOW(), INTERVAL 5 DAY)),
    (6, DATE_SUB(NOW(), INTERVAL 8 DAY));

INSERT INTO order_items (id, order_id, product_id, quantity) VALUES
    (1, 1, 1, 1),
    (2, 1, 2, 1),
    (3, 2, 1, 2),
    (4, 2, 3, 1),
    (5, 3, 1, 1),
    (6, 3, 3, 2),
    (7, 4, 2, 1),
    (8, 4, 4, 1),
    (9, 5, 1, 1),
    (10, 5, 5, 1),
    (11, 6, 6, 1);
