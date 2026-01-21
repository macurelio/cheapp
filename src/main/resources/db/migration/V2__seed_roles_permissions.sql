-- Seed IAM roles & permissions (idempotente)

-- Roles del sistema (nombres estables)
INSERT INTO roles(name) VALUES
    ('SUPER_USER'),
    ('ROLE_ADMIN'),
    ('ROLE_EDITOR'),
    ('ROLE_VIEWER')
ON CONFLICT (name) DO NOTHING;

-- Permisos atómicos (según prompt)
INSERT INTO permissions(name) VALUES
    ('IAM_USER_CREATE'),
    ('IAM_USER_READ'),
    ('IAM_ROLE_ASSIGN'),
    ('IAM_ROLE_READ'),
    ('MENU_ITEM_DISABLE'),
    ('MENU_ITEM_REMOVE'),
    ('ORDER_ITEM_REMOVE_WITH_AUTH'),
    ('DISCOUNT_APPLY'),
    ('PURCHASE_REQUEST_MANAGE'),
    ('SCHEDULE_MANAGE'),
    ('PAYROLL_READ'),
    ('STOCK_REQUEST_CREATE_SMALL'),
    ('POS_PLACE_ORDER')
ON CONFLICT (name) DO NOTHING;

-- Mapeo roles -> permisos
-- SUPER_USER: todos los permisos (lista completa por ahora)
INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'SUPER_USER'
ON CONFLICT DO NOTHING;

-- ROLE_ADMIN
INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.name IN (
    'IAM_USER_READ',
    'IAM_ROLE_READ',
    'IAM_ROLE_ASSIGN',
    'MENU_ITEM_DISABLE',
    'MENU_ITEM_REMOVE',
    'ORDER_ITEM_REMOVE_WITH_AUTH',
    'DISCOUNT_APPLY',
    'PURCHASE_REQUEST_MANAGE',
    'SCHEDULE_MANAGE'
)
WHERE r.name = 'ROLE_ADMIN'
ON CONFLICT DO NOTHING;

-- ROLE_EDITOR
INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.name IN (
    'MENU_ITEM_DISABLE'
)
WHERE r.name = 'ROLE_EDITOR'
ON CONFLICT DO NOTHING;

-- ROLE_VIEWER
INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.name IN (
    'IAM_USER_READ',
    'SCHEDULE_MANAGE',
    'PAYROLL_READ',
    'STOCK_REQUEST_CREATE_SMALL'
)
WHERE r.name = 'ROLE_VIEWER'
ON CONFLICT DO NOTHING;
