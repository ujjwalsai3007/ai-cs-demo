-- Fix the sequence to start from 1 instead of 50
-- First, get the sequence name and reset it
ALTER SEQUENCE products_id_seq RESTART WITH 1;

-- Clear existing data to ensure clean start
DELETE FROM products; 