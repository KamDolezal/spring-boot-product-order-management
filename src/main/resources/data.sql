create sequence product_id_seq start with 1 increment by 1;
create sequence order_id_seq start with 1 increment by 1;
create sequence item_id_seq start with 1 increment by 1;

-- id, name, description, amount, price
INSERT INTO product VALUES
(next value for product_id_seq, 'Lenovo P51', 'ultrabook', 3, 35000.50),
(next value for product_id_seq, 'Lenovo P52', 'ultrabook', 5, 45000),
(next value for product_id_seq, 'Lenovo L580', 'office notebook', 2, 25799.99);

-- id, paid
INSERT INTO order VALUES
(next value for order_id_seq, true),
(next value for order_id_seq, false);

--item_id, product_id, amount, order_id
INSERT INTO order_item VALUES
(next value for item_id_seq,1,2,1),
(next value for item_id_seq,2,1,1),
(next value for item_id_seq,2,1,2);
