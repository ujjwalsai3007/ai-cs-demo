-- Reset sequence and data to ensure products start from ID 1
-- Clear all existing data
DELETE FROM products;

-- Reset the sequence to start from 1
ALTER SEQUENCE products_id_seq RESTART WITH 1; 