CREATE SEQUENCE IF NOT EXISTS product_id_seq;
CREATE SEQUENCE IF NOT EXISTS cart_id_seq;
CREATE SEQUENCE IF NOT EXISTS cart_item_id_seq;

CREATE TABLE products (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    price DOUBLE PRECISION NOT NULL,
    stock INT NOT NULL
);

CREATE TABLE carts (
    id BIGINT PRIMARY KEY,
    session_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE cart_items (
    id BIGINT PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id)
);