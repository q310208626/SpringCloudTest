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

drop table if exists t_user;
CREATE TABLE t_user(
                       user_id int(12) auto_increment primary key,
                       user_name varchar(255),
                       password varchar(255),
                       create_time timestamp default current_timestamp,
                       status smallint default 0
) AUTO_INCREMENT = 1000001 COMMENT = '用户表';
alter table t_user add constraint unique_username UNIQUE(user_name);
create index idx_user_name on t_user(user_name);

drop table if exists t_role;
CREATE TABLE t_role(
                       role_id int(12) auto_increment primary key,
                       role_name varchar(255),
                       role_desc varchar(255),
                       status smallint default 0
) AUTO_INCREMENT = 1000001 COMMENT = '角色表';
alter table t_role add constraint unique_rolename UNIQUE(role_name);

drop table if exists t_user_role;
CREATE TABLE t_user_role(
                            user_role_id int(12) auto_increment primary key,
                            user_id int(12),
                            role_id int(12)
) AUTO_INCREMENT = 1000001 COMMENT = '用户角色';
create index idx_user_id on t_user(user_id);