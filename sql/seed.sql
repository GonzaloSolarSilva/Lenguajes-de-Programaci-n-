-- =====================================================================
-- Datos de ejemplo para "Sabor Criollo" (ejecutar DESPUÉS de schema.sql)
-- Equivale a la carga que hace DataSeeder al iniciar la aplicación.
-- Contraseña de todos los usuarios: 123456 (guardada como hash BCrypt).
-- Si ejecutas este script, la aplicación no vuelve a cargar datos de demostración.
-- =====================================================================

INSERT INTO usuario (nombres, apellidos, username, password_hash, rol, turno, estacion) VALUES
 ('Carlos', 'Quispe',        'mesero1',   '$2a$10$0dkg9Bt8uJZJS3fN6T8J1ObbzYBcFlN9tw/HTO8LMJCkd12OT1gsC', 'MESERO',        'MANANA', NULL),
 ('Lucía',  'Rojas',         'mesero2',   '$2a$10$0dkg9Bt8uJZJS3fN6T8J1ObbzYBcFlN9tw/HTO8LMJCkd12OT1gsC', 'MESERO',        'TARDE',  NULL),
 ('Pedro',  'Huamán',        'cocinero1', '$2a$10$0dkg9Bt8uJZJS3fN6T8J1ObbzYBcFlN9tw/HTO8LMJCkd12OT1gsC', 'COCINERO',      NULL,     'FONDOS'),
 ('Ana',    'Torres',        'cajero1',   '$2a$10$0dkg9Bt8uJZJS3fN6T8J1ObbzYBcFlN9tw/HTO8LMJCkd12OT1gsC', 'CAJERO',        NULL,     NULL),
 ('Admin',  'Sabor Criollo', 'admin',     '$2a$10$0dkg9Bt8uJZJS3fN6T8J1ObbzYBcFlN9tw/HTO8LMJCkd12OT1gsC', 'ADMINISTRADOR', NULL,     NULL);

INSERT INTO mesa (numero, capacidad)
SELECT n, CASE WHEN n % 3 = 0 THEN 6 ELSE 4 END FROM generate_series(1, 18) AS n;

INSERT INTO categoria (nombre) VALUES ('Entradas'), ('Platos de fondo'), ('Postres'), ('Bebidas');

INSERT INTO insumo (nombre, unidad_medida, stock_actual, stock_minimo) VALUES
 ('Pescado',          'kg',   20, 3),
 ('Limón',            'kg',   15, 2),
 ('Camote',           'kg',   10, 2),
 ('Papa amarilla',    'kg',   25, 4),
 ('Lomo de res',      'kg',   18, 3),
 ('Cebolla roja',     'kg',   20, 3),
 ('Arroz',            'kg',   40, 5),
 ('Pollo',            'kg',   30, 5),
 ('Ají amarillo',     'kg',    8, 1),
 ('Maíz morado',      'kg',    6, 1),
 ('Leche condensada', 'lata', 24, 4);

INSERT INTO producto (nombre, descripcion, precio, estacion, id_categoria) VALUES
 ('Ceviche clásico',  'Pescado fresco en leche de tigre',               28.00, 'ENTRADAS',        (SELECT id_categoria FROM categoria WHERE nombre = 'Entradas')),
 ('Causa limeña',     'Causa rellena de pollo',                         18.00, 'ENTRADAS',        (SELECT id_categoria FROM categoria WHERE nombre = 'Entradas')),
 ('Lomo saltado',     'Lomo fino con papas fritas y arroz',             32.00, 'FONDOS',          (SELECT id_categoria FROM categoria WHERE nombre = 'Platos de fondo')),
 ('Ají de gallina',   'Crema de ají amarillo con pollo deshilachado',   26.00, 'FONDOS',          (SELECT id_categoria FROM categoria WHERE nombre = 'Platos de fondo')),
 ('Arroz con pollo',  'Arroz verde con presa de pollo',                 24.00, 'FONDOS',          (SELECT id_categoria FROM categoria WHERE nombre = 'Platos de fondo')),
 ('Suspiro limeño',   'Postre de manjar blanco y merengue',             12.00, 'POSTRES_BEBIDAS', (SELECT id_categoria FROM categoria WHERE nombre = 'Postres')),
 ('Chicha morada',    'Jarra de chicha morada',                          8.00, 'POSTRES_BEBIDAS', (SELECT id_categoria FROM categoria WHERE nombre = 'Bebidas')),
 ('Inca Kola 500 ml', 'Gaseosa personal',                                6.00, 'POSTRES_BEBIDAS', (SELECT id_categoria FROM categoria WHERE nombre = 'Bebidas'));

-- Recetas (producto, insumo, cantidad por unidad)
INSERT INTO receta_item (id_producto, id_insumo, cantidad)
SELECT p.id_producto, i.id_insumo, r.cantidad
FROM (VALUES
 ('Ceviche clásico', 'Pescado',          0.250), ('Ceviche clásico', 'Limón',         0.100),
 ('Ceviche clásico', 'Camote',           0.100), ('Ceviche clásico', 'Cebolla roja',  0.050),
 ('Causa limeña',    'Papa amarilla',    0.300), ('Causa limeña',    'Limón',         0.030),
 ('Causa limeña',    'Pollo',            0.100),
 ('Lomo saltado',    'Lomo de res',      0.250), ('Lomo saltado',    'Cebolla roja',  0.100),
 ('Lomo saltado',    'Papa amarilla',    0.200), ('Lomo saltado',    'Arroz',         0.150),
 ('Ají de gallina',  'Pollo',            0.250), ('Ají de gallina',  'Ají amarillo',  0.050),
 ('Ají de gallina',  'Arroz',            0.150), ('Ají de gallina',  'Papa amarilla', 0.100),
 ('Arroz con pollo', 'Arroz',            0.200), ('Arroz con pollo', 'Pollo',         0.300),
 ('Suspiro limeño',  'Leche condensada', 0.500),
 ('Chicha morada',   'Maíz morado',      0.100)
) AS r(producto, insumo, cantidad)
JOIN producto p ON p.nombre = r.producto
JOIN insumo   i ON i.nombre = r.insumo;

-- ---------------------------------------------------------------------
-- Pedido de ejemplo ya cobrado (sirve para ver datos en pedido, detalle_pedido,
-- pago y en la vista vw_ventas_turno_producto). Mesa 1, mesero1, cajero1.
-- ---------------------------------------------------------------------
INSERT INTO pedido (id_mesa, id_mesero, estado, turno, total)
VALUES ((SELECT id_mesa FROM mesa WHERE numero = 1),
        (SELECT id_usuario FROM usuario WHERE username = 'mesero1'),
        'PAGADO', 'TARDE', 88.00);

INSERT INTO detalle_pedido (id_pedido, id_producto, cantidad, precio_unitario, subtotal, observacion)
SELECT (SELECT MAX(id_pedido) FROM pedido), p.id_producto, d.cantidad, p.precio, p.precio * d.cantidad, d.obs
FROM (VALUES ('Ceviche clásico', 2, 'sin ají'), ('Lomo saltado', 1, NULL)) AS d(producto, cantidad, obs)
JOIN producto p ON p.nombre = d.producto;

INSERT INTO pago (id_pedido, id_cajero, metodo_pago, monto)
VALUES ((SELECT MAX(id_pedido) FROM pedido),
        (SELECT id_usuario FROM usuario WHERE username = 'cajero1'),
        'EFECTIVO', 88.00);

-- Descuento de stock equivalente al pedido de ejemplo
UPDATE insumo SET stock_actual = stock_actual - 0.500 WHERE nombre = 'Pescado';
UPDATE insumo SET stock_actual = stock_actual - 0.200 WHERE nombre = 'Limón';
UPDATE insumo SET stock_actual = stock_actual - 0.200 WHERE nombre = 'Camote';
UPDATE insumo SET stock_actual = stock_actual - 0.200 WHERE nombre = 'Cebolla roja';
UPDATE insumo SET stock_actual = stock_actual - 0.250 WHERE nombre = 'Lomo de res';
UPDATE insumo SET stock_actual = stock_actual - 0.200 WHERE nombre = 'Papa amarilla';
UPDATE insumo SET stock_actual = stock_actual - 0.150 WHERE nombre = 'Arroz';

-- Consultas para las capturas de evidencia
-- SELECT * FROM vw_ventas_turno_producto;
-- SELECT numero, capacidad, estado FROM mesa ORDER BY numero;
