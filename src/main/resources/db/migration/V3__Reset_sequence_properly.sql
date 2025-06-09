-- Reset sequence and data to ensure products start from ID 1
-- Clear all existing data
DELETE FROM products;

-- For PostgreSQL only (will be ignored on H2)
-- SELECT setval('products_id_seq', 1, false) WHERE EXISTS (SELECT 1 FROM information_schema.sequences WHERE sequence_name = 'products_id_seq'); 