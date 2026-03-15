-- Add country column to users table for admin country-distribution stats
-- Run this if Hibernate auto-ddl is set to 'validate' or 'none'

ALTER TABLE users ADD COLUMN IF NOT EXISTS country VARCHAR(100);
