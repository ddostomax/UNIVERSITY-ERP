-- Update ERP DB schema to add deadline and enrollment date columns
-- Run this if you already have the database created

USE univ_erp;

-- Add drop_deadline column to sections (ignore error if already exists)
ALTER TABLE sections 
ADD COLUMN drop_deadline DATE;

-- Add enrollment_date column to enrollments (ignore error if already exists)
ALTER TABLE enrollments 
ADD COLUMN enrollment_date DATE DEFAULT (CURRENT_DATE);

