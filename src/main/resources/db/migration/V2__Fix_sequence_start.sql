-- Fix the sequence to start from 1 instead of 50
-- Clear existing data to ensure clean start
DELETE FROM products;

-- For PostgreSQL only (will be ignored on H2)
-- SELECT setval('products_id_seq', 1, false) WHERE EXISTS (SELECT 1 FROM information_schema.sequences WHERE sequence_name = 'products_id_seq'); 