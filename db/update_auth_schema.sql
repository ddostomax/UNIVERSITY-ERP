-- Update Auth DB schema to add login tracking columns
-- Run this if you already have the database created

USE univ_auth;

-- Add login_attempts column (ignore error if already exists)
ALTER TABLE users_auth 
ADD COLUMN login_attempts INT NOT NULL DEFAULT 0;

-- Add locked_until column (ignore error if already exists)
ALTER TABLE users_auth 
ADD COLUMN locked_until TIMESTAMP NULL;

