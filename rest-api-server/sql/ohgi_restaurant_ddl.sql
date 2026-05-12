-- --------------------------------------------------------
-- db 생성 및 유저 권한 할당
-- --------------------------------------------------------
-- create database ohgi_restaurant;
-- show databases;

-- grant all privileges on ohgi_restaurant.* to 'wanted'@'%';
-- show grants for 'wanted'@'%';


-- --------------------------------------------------------
-- ddl
-- --------------------------------------------------------
-- tbl_category(상품 카테고리)

use ohgi_restaurant;

drop table if exists tbl_category;
create table if not exists tbl_category
(
    -- column level constraints
    category_code int auto_increment comment '카테고리식별코드',
    category_name varchar(255) not null comment '카테고리명',
    -- table level constraints
    constraint pk_category_code primary key (category_code)
) engine=innodb comment '상품카테고리';


-- tbl_member(회원)
drop table if exists tbl_member;
create table if not exists tbl_member
(
    -- column level constraints
    member_code int auto_increment comment '회원식별코드',
    member_id varchar(255) unique not null comment '아이디',
    member_name varchar(255) not null comment '회원이름',
    member_password varchar(255) not null comment '비밀번호',
    member_email varchar(255) not null comment '이메일',
    -- table level constraints
    constraint pk_member_code primary key (member_code)
) engine=innodb comment '회원';


-- tbl_authority(권한)
drop table if exists tbl_authority;
create table if not exists tbl_authority
(
    -- column level constraints
    authority_code int auto_increment comment '권한식별코드',
    authority_name varchar(255) not null comment '권한명',
    authority_desc varchar(4000) not null comment '권한설명',
    -- table level constraints
    constraint pk_authority_code primary key (authority_code)
) engine=innodb comment '권한';


-- tbl_member_role(회원별권한)
drop table if exists tbl_member_role;
create table if not exists tbl_member_role
(
    -- column level constraints
    member_code int auto_increment comment '회원식별코드',
    authority_code int not null comment '권한식별코드',
    -- table level constraints
    constraint pk_member_role primary key (member_code, authority_code)
) engine=innodb comment '회원별권한';


-- tbl_order(주문)
drop table if exists tbl_order;
create table if not exists tbl_order
(
    -- column level constraints
    order_code int auto_increment comment '주문식별번호',
    product_code int not null comment '상품코드',
    order_member int not null comment '회원식별코드',
    order_phone varchar(255) not null comment '휴대전화연락처',
    order_email varchar(255) not null comment '이메일주소',
    order_receiver varchar(255) not null comment '받는사람',
    order_address varchar(500) not null comment '배송주소',
    order_amount varchar(50) not null comment '주문개수',
    order_date varchar(255) not null comment '주문일자',
    -- table level constraints
    constraint pk_order_code primary key (order_code)
) engine=innodb comment '주문';


-- tbl_product(상품)
drop table if exists tbl_product;
create table if not exists tbl_product
(
    -- column level constraints
    product_code int auto_increment comment '상품코드',
    product_name varchar(255) not null comment '상품명',
    product_price int not null comment '상품가격',
    product_description varchar(1000) comment '상품설명',
    product_orderable varchar(5) not null comment '구매가능여부',
    category_code int comment '카테고리식별코드',
    product_image_url varchar(255) not null comment '상품이미지경로',
    product_stock int not null comment '상품재고',
    -- table level constraints
    constraint pk_product_code primary key (product_code)
) engine=innodb comment '상품';


-- tbl_review(상품리뷰)
drop table if exists tbl_review;
create table if not exists tbl_review
(
    -- column level constraints
    review_code int auto_increment comment '리뷰식별번호',
    product_code int not null comment '상품코드',
    member_code int not null comment '회원식별코드',
    review_title varchar(255) not null comment '리뷰제목',
    review_content varchar(1000) not null comment '리뷰내용',
    review_create_date varchar(255) not null comment '작성일자',
    -- table level constraints
    constraint pk_review_code primary key (review_code)
) engine=innodb comment '상품리뷰';
