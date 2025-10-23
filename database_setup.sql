CREATE DATABASE v2_20bytex_database;
USE v2_20bytex_database;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `order_items`;
DROP TABLE IF EXISTS `repair_parts`;
DROP TABLE IF EXISTS `attachments`;
DROP TABLE IF EXISTS `responses`;
DROP TABLE IF EXISTS `part_requests`;
DROP TABLE IF EXISTS `purchase_orders`;
DROP TABLE IF EXISTS `repairs`;
DROP TABLE IF EXISTS `tickets`;
DROP TABLE IF EXISTS `activity_logs`;
DROP TABLE IF EXISTS `password_reset_tokens`;
DROP TABLE IF EXISTS `parts`;
DROP TABLE IF EXISTS `suppliers`;
DROP TABLE IF EXISTS `users`;
DROP TABLE IF EXISTS `roles`;

SET FOREIGN_KEY_CHECKS = 1;

-- ===================================================================
-- ROLE MANAGEMENT (ISA Mapping for Stakeholders)
-- ===================================================================
CREATE TABLE `roles` (
  `role_id` INT NOT NULL AUTO_INCREMENT,
  `role_name` VARCHAR(50) NOT NULL UNIQUE,
  PRIMARY KEY (`role_id`)
);

-- ===================================================================
-- USERS
-- ===================================================================
CREATE TABLE `users` (
  `user_id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `email` VARCHAR(100) NOT NULL UNIQUE,
  `full_name` VARCHAR(100) NOT NULL,
  `phone_number` VARCHAR(20),
  `created_at` DATETIME(6) NOT NULL,
  `last_login` DATETIME(6),
  `role_id` INT NOT NULL,
  PRIMARY KEY (`user_id`),
  FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`)
);

-- ===================================================================
-- SUPPLIERS
-- ===================================================================
CREATE TABLE `suppliers` (
  `supplier_id` BIGINT NOT NULL AUTO_INCREMENT,
  `supplier_name` VARCHAR(100) NOT NULL UNIQUE,
  `contact_info` VARCHAR(255),
  `address` TEXT,
  `created_at` DATETIME(6) NOT NULL,
  PRIMARY KEY (`supplier_id`)
);

-- ===================================================================
-- PARTS
-- ===================================================================
CREATE TABLE `parts` (
  `part_id` BIGINT NOT NULL AUTO_INCREMENT,
  `part_number` VARCHAR(50) NOT NULL UNIQUE,
  `part_name` VARCHAR(100) NOT NULL,
  `description` TEXT,
  `category` VARCHAR(50) NOT NULL,
  `unit_price` DECIMAL(10,2) NOT NULL,
  `current_stock` INT NOT NULL,
  `minimum_stock` INT NOT NULL,
  `status` ENUM('ACTIVE','LOW_STOCK','OUT_OF_STOCK','DISCONTINUED') NOT NULL,
  PRIMARY KEY (`part_id`)
);

-- ===================================================================
-- WAREHOUSE STOCK
-- ===================================================================
CREATE TABLE `warehouse_stock` (
  `part_id` BIGINT NOT NULL,
  `quantity` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`part_id`),
  FOREIGN KEY (`part_id`) REFERENCES `parts` (`part_id`)
);

-- ===================================================================
-- TICKETS
-- ===================================================================
CREATE TABLE `tickets` (
  `ticket_id` BIGINT NOT NULL AUTO_INCREMENT,
  `customer_id` BIGINT NOT NULL,
  `assigned_to_id` BIGINT, -- staff_id
  `technician_id` BIGINT, -- new field
  `subject` VARCHAR(100) NOT NULL,
  `description` TEXT NOT NULL,
  `priority` ENUM('LOW','MEDIUM','HIGH') NOT NULL,
  `status` ENUM('OPEN','IN_PROGRESS','PENDING','CLOSED') NOT NULL,
  `stage` ENUM('WITH_CUSTOMER','WITH_STAFF','WITH_TECHNICIAN','RESOLVED') NOT NULL,
  `created_at` DATETIME(6) NOT NULL,
  `updated_at` DATETIME(6),
  `closed_at` DATETIME(6),
  `archived` BOOLEAN NOT NULL DEFAULT FALSE,
  `archived_at` DATETIME(6),
  PRIMARY KEY (`ticket_id`),
  FOREIGN KEY (`customer_id`) REFERENCES `users` (`user_id`),
  FOREIGN KEY (`assigned_to_id`) REFERENCES `users` (`user_id`),
  FOREIGN KEY (`technician_id`) REFERENCES `users` (`user_id`)
);

-- ===================================================================
-- REPAIRS
-- ===================================================================
CREATE TABLE `repairs` (
  `repair_id` BIGINT NOT NULL AUTO_INCREMENT,
  `ticket_id` BIGINT NOT NULL,
  `technician_id` BIGINT NOT NULL,
  `diagnosis` TEXT NOT NULL,
  `repair_details` TEXT,
  `status` ENUM('PENDING','IN_PROGRESS','COMPLETED') NOT NULL,
  `start_date` DATETIME(6),
  `completion_date` DATETIME(6),
  PRIMARY KEY (`repair_id`),
  FOREIGN KEY (`ticket_id`) REFERENCES `tickets` (`ticket_id`),
  FOREIGN KEY (`technician_id`) REFERENCES `users` (`user_id`)
);

-- ===================================================================
-- PART REQUESTS (by Product Manager or Technician)
-- ===================================================================
CREATE TABLE `part_requests` (
  `request_id` BIGINT NOT NULL AUTO_INCREMENT,
  `part_id` BIGINT NOT NULL,
  `requestor_id` BIGINT NOT NULL,
  `repair_id` BIGINT,
  `quantity` INT NOT NULL,
  `reason` TEXT,
  `status` ENUM('PENDING','APPROVED','REJECTED','FULFILLED') NOT NULL,
  `request_date` DATETIME(6),
  `fulfillment_date` DATETIME(6),
  PRIMARY KEY (`request_id`),
  FOREIGN KEY (`part_id`) REFERENCES `parts` (`part_id`),
  FOREIGN KEY (`requestor_id`) REFERENCES `users` (`user_id`),
  FOREIGN KEY (`repair_id`) REFERENCES `repairs` (`repair_id`)
);

-- ===================================================================
-- RESPONSES (Staff → Customer Communication)
-- ===================================================================
CREATE TABLE `responses` (
  `response_id` BIGINT NOT NULL AUTO_INCREMENT,
  `ticket_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `message` TEXT NOT NULL,
  `created_at` DATETIME(6) NOT NULL,
  PRIMARY KEY (`response_id`),
  FOREIGN KEY (`ticket_id`) REFERENCES `tickets` (`ticket_id`),
  FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
);

-- ===================================================================
-- ATTACHMENTS
-- ===================================================================
CREATE TABLE `attachments` (
  `attachment_id` BIGINT NOT NULL AUTO_INCREMENT,
  `file_name` VARCHAR(255) NOT NULL,
  `file_path` VARCHAR(255) NOT NULL,
  `file_size` INT NOT NULL,
  `file_type` VARCHAR(100) NOT NULL,
  `ticket_id` BIGINT,
  `response_id` BIGINT,
  `uploaded_by` BIGINT NOT NULL,
  `uploaded_at` DATETIME(6) NOT NULL,
  PRIMARY KEY (`attachment_id`),
  FOREIGN KEY (`ticket_id`) REFERENCES `tickets` (`ticket_id`),
  FOREIGN KEY (`response_id`) REFERENCES `responses` (`response_id`),
  FOREIGN KEY (`uploaded_by`) REFERENCES `users` (`user_id`)
);

-- ===================================================================
-- REPAIR PARTS (M:N Mapping)
-- ===================================================================
CREATE TABLE `repair_parts` (
  `repair_id` BIGINT NOT NULL,
  `part_id` BIGINT NOT NULL,
  `quantity` INT NOT NULL,
  PRIMARY KEY (`repair_id`, `part_id`),
  FOREIGN KEY (`repair_id`) REFERENCES `repairs` (`repair_id`),
  FOREIGN KEY (`part_id`) REFERENCES `parts` (`part_id`)
);

-- ===================================================================
-- STOCK REQUESTS (Product Manager -> Warehouse Manager)
-- ===================================================================
CREATE TABLE `stock_requests` (
  `request_id` BIGINT NOT NULL AUTO_INCREMENT,
  `part_id` BIGINT NOT NULL,
  `requestor_id` BIGINT NOT NULL,
  `quantity_requested` INT NOT NULL,
  `reason` TEXT,
  `status` ENUM('PENDING','ACKNOWLEDGED','APPROVED','REFILLED','REJECTED') NOT NULL,
  `request_date` DATETIME(6) NOT NULL,
  PRIMARY KEY (`request_id`),
  FOREIGN KEY (`part_id`) REFERENCES `parts` (`part_id`),
  FOREIGN KEY (`requestor_id`) REFERENCES `users` (`user_id`)
);

-- ===================================================================
-- PURCHASE ORDERS
-- ===================================================================
CREATE TABLE `purchase_orders` (
  `order_id` BIGINT NOT NULL AUTO_INCREMENT,
  `supplier_id` BIGINT NOT NULL,
  `created_by_id` BIGINT NOT NULL,
  `order_date` DATETIME(6),
  `expected_delivery` DATE,
  `actual_delivery` DATE,
  `status` ENUM('PENDING','ORDERED','DELIVERED','CANCELLED') NOT NULL,
  `total_amount` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`order_id`),
  FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`supplier_id`),
  FOREIGN KEY (`created_by_id`) REFERENCES `users` (`user_id`)
);

-- ===================================================================
-- SUPPLIER PARTS (M:N Mapping)
-- ===================================================================
CREATE TABLE `supplier_parts` (
  `supplier_id` BIGINT NOT NULL,
  `part_id` BIGINT NOT NULL,
  PRIMARY KEY (`supplier_id`, `part_id`),
  FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`supplier_id`),
  FOREIGN KEY (`part_id`) REFERENCES `parts` (`part_id`)
);

-- ===================================================================
-- ORDER ITEMS (M:N Purchase Orders → Parts)
-- ===================================================================
CREATE TABLE `order_items` (
  `order_id` BIGINT NOT NULL,
  `part_id` BIGINT NOT NULL,
  `quantity` INT NOT NULL,
  `unit_price` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`order_id`, `part_id`),
  FOREIGN KEY (`order_id`) REFERENCES `purchase_orders` (`order_id`),
  FOREIGN KEY (`part_id`) REFERENCES `parts` (`part_id`)
);

-- ===================================================================
-- PASSWORD RESET TOKENS
-- ===================================================================
CREATE TABLE `password_reset_tokens` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `token` VARCHAR(255) NOT NULL UNIQUE,
  `user_id` BIGINT NOT NULL,
  `otp` VARCHAR(6) NOT NULL,
  `expiry_date` DATETIME(6) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
);

-- ===================================================================
-- ACTIVITY LOGS (Admin Monitoring)
-- ===================================================================
CREATE TABLE `activity_logs` (
  `log_id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT,
  `action_type` VARCHAR(50) NOT NULL,
  `entity_type` VARCHAR(50) NOT NULL,
  `entity_id` BIGINT,
  `description` TEXT,
  `ip_address` VARCHAR(50),
  `created_at` DATETIME(6) NOT NULL,
  PRIMARY KEY (`log_id`),
  FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
);

-- ===================================================================
-- SAMPLE DATA
-- ===================================================================

-- ROLES
INSERT INTO roles (role_name) VALUES
('ADMIN'), ('STAFF'), ('TECHNICIAN'), ('PRODUCT_MANAGER'), ('WAREHOUSE_MANAGER'), ('CUSTOMER');

-- USERS
INSERT INTO users (username, password, email, full_name, phone_number, role_id, created_at)
VALUES
('admin', 'admin123', 'admin@bytex.com', 'System Administrator', '+94711234567', 1, NOW()),
('sf1', 'staff123', 'sf1@bytex.com', 'Staff Member 1', '+94712345678', 2, NOW()),
('sf2', 'staff123', 'sf2@bytex.com', 'Staff Member 2', '+94713456789', 2, NOW()),
('tech1', 'tech123', 'tech1@bytex.com', 'Technician 1', '+94714567890', 3, NOW()),
('tech2', 'tech123', 'tech2@bytex.com', 'Technician 2', '+94715678901', 3, NOW()),
('pm1', 'pm123', 'pm1@bytex.com', 'Product Manager', '+94716789012', 4, NOW()),
('wm', 'wm123', 'pm2@bytex.com', 'Warehouse Manager', '+94717890123', 5, NOW()),
('customer1', 'pass123', 'janiya@gmail.com', 'Janith Deshan', '+94703638365', 6, NOW()),
('customer2', 'pass123', 'kaviya@outlook.com', 'Kavindu Sahan', '+94719012345', 6, NOW());

-- PARTS
INSERT INTO parts (part_number, part_name, description, current_stock, minimum_stock, unit_price, category, status)
VALUES
('CPU001', 'Intel Core i7-12700K', 'High performance CPU', 15, 5, 399.99, 'CPU', 'ACTIVE'),
('RAM001', 'Corsair Vengeance 16GB DDR4', 'High performance RAM', 30, 10, 79.99, 'RAM', 'ACTIVE'),
('GPU001', 'NVIDIA RTX 3080', 'High-end graphics card', 5, 3, 699.99, 'GPU', 'LOW_STOCK'),
('SSD001', 'Samsung 970 EVO 1TB', 'NVMe SSD', 0, 5, 149.99, 'Storage', 'OUT_OF_STOCK');

-- TICKETS
INSERT INTO tickets (customer_id, subject, description, priority, status, stage, assigned_to_id, created_at, archived)
VALUES
(8, 'My computer is running slow', 'After last update, my PC boots very slowly.', 'MEDIUM', 'IN_PROGRESS', 'WITH_STAFF', 2, NOW(), FALSE),
(9, 'Graphics card not detected', 'Monitor is blank, system does not see GPU.', 'HIGH', 'IN_PROGRESS', 'WITH_TECHNICIAN', 4, NOW(), FALSE);

-- REPAIRS
INSERT INTO repairs (ticket_id, technician_id, diagnosis, status, start_date)
VALUES
(2, 4, 'GPU may be faulty. Needs replacement.', 'PENDING', NOW());

-- PART REQUESTS
INSERT INTO part_requests (part_id, requestor_id, quantity, reason, status, repair_id, request_date)
VALUES
(3, 4, 1, 'Replace faulty GPU for Ticket #2.', 'PENDING', 1, NOW());