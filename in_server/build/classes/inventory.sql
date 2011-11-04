CREATE DATABASE IF NOT EXISTS inventory;

USE inventory;

DROP TABLE IF EXISTS producttype;
CREATE TABLE producttype
(
   product_type_pid BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
   product_type VARCHAR(40) NOT NULL,
   PRIMARY KEY (product_type_pid)
);

DROP TABLE IF EXISTS vendor;
CREATE TABLE vendor
(
   vendor_pid BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
   vendor_name VARCHAR(50),
   vendor_addr VARCHAR(100),
   vendor_number VARCHAR(40),
   PRIMARY KEY (vendor_pid)
);

DROP TABLE IF EXISTS customer;
CREATE TABLE customer
(
   customer_pid BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
   customer_name VARCHAR(50),
   customer_addr VARCHAR(100),
   customer_number VARCHAR(40),
   PRIMARY KEY (customer_pid)
);

DROP TABLE IF EXISTS expensetype;
CREATE TABLE expensetype
(
   expense_type_pid BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
   expense_type VARCHAR(50),
   PRIMARY KEY (expense_type_pid)
);

DROP TABLE IF EXISTS product;
CREATE TABLE product
(
   product_pid BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
   product_type_id BIGINT UNSIGNED NOT NULL,
   product_name VARCHAR(50),
   rate VARCHAR(50),
   available INT,
   start_date DATETIME,
   PRIMARY KEY (product_pid),
   FOREIGN KEY (product_type_id) REFERENCES producttype (product_type_pid) on delete cascade
);

DROP TABLE IF EXISTS purchase;
CREATE TABLE purchase
(
   purchase_pid BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
   product_id BIGINT UNSIGNED NOT NULL,
   vendor_id BIGINT UNSIGNED NOT NULL,
   purchase_date DATETIME,
   cost VARCHAR(50),
   quantity VARCHAR(50),
   PRIMARY KEY (purchase_pid),
   FOREIGN KEY (product_id) REFERENCES product (product_pid) on delete cascade,
   FOREIGN KEY (vendor_id) REFERENCES vendor (vendor_pid) on delete cascade
);

DROP TABLE IF EXISTS sales;
CREATE TABLE sales
(
   sales_pid BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
   product_id BIGINT UNSIGNED NOT NULL,
   customer_id BIGINT UNSIGNED NOT NULL,
   sales_date DATETIME,
   price VARCHAR(50),
   quantity VARCHAR(50),
   PRIMARY KEY (sales_pid),
   FOREIGN KEY (product_id) REFERENCES product (product_pid) on delete cascade,
   FOREIGN KEY (customer_id) REFERENCES customer (customer_pid) on delete cascade
);

DROP TABLE IF EXISTS expense;
CREATE TABLE expense
(
   expense_pid BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
   expense_type_id BIGINT UNSIGNED NOT NULL,
   expense_date DATETIME,
   amount VARCHAR(50),
   PRIMARY KEY (expense_pid),
   FOREIGN KEY (expense_type_id) REFERENCES expensetype (expense_type_pid) on delete cascade
);