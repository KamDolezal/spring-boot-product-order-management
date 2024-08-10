DROP TABLE IF EXISTS product;
CREATE TABLE product (
  id bigint NOT NULL AUTO_INCREMENT,
  -- name varchar(45) NOT NULL UNIQUE,
  name varchar(45) NOT NULL,
  description varchar(160),
  amount bigint NOT NULL,
  price float(53) NOT NULL,
  PRIMARY KEY (id)
);

DROP TABLE IF EXISTS order;
CREATE TABLE order (
    id bigint NOT NULL AUTO_INCREMENT,
    paid BOOLEAN NOT NULL
);

DROP TABLE IF EXISTS order_items;
CREATE TABLE order_item (
    item_id bigint NOT NULL AUTO_INCREMENT,
    product_id bigint NOT NULL,
    amount bigint NOT NULL,
    order_id bigint NOT NULL
);


