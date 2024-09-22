CREATE DATABASE test_db character set utf8mb4;
CREATE user test_db@'%' identified by 'test_db';

GRANT ALL PRIVILEGES on test_db.* to test_db@'%';

use test_db;

drop table if exists t_product;
CREATE TABLE t_product(
    id int(12) auto_increment primary key,
    product_name varchar(128)
) AUTO_INCREMENT = 100001 COMMENT = '产品';

drop table if exists t_product_stock;
CREATE TABLE t_product_stock(
    id int(12) auto_increment primary key,
    product_id int(12),
    stock_num int
) AUTO_INCREMENT = 100001 COMMENT = '产品库存';
