-- Create database if not exists
CREATE DATABASE IF NOT EXISTS logindemo;
USE logindemo;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    uid BIGINT AUTO_INCREMENT PRIMARY KEY,
    uname VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- Insert some test data
INSERT INTO users (uname, password) VALUES 
('admin', 'admin123'),
('test', 'test123')
ON DUPLICATE KEY UPDATE uname=uname; 