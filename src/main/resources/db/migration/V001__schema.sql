CREATE TABLE IF NOT EXISTS card
(
    id              SERIAL PRIMARY KEY,
    product_id      VARCHAR(6)     NOT NULL,
    card_number     VARCHAR(16)    NOT NULL,
    card_holder     VARCHAR(255),
    balance         DECIMAL(10, 2),
    enroll          BOOLEAN        DEFAULT false,
    block           BOOLEAN        DEFAULT false,
    valid_thru      VARCHAR(7)     NOT NULL,
    expiration_date TIMESTAMP      NOT NULL
);


CREATE TABLE IF NOT EXISTS transaction
(
    id               SERIAL PRIMARY KEY,
    id_card          INT            NOT NULL,
    price            DECIMAL(10, 2) NOT NULL,
    cancel           BOOLEAN        NOT NULL DEFAULT false,
    transaction_date TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT transactions_fk FOREIGN KEY (id_card) REFERENCES card(id)
);
