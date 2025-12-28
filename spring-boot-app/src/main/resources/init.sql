CREATE TABLE IF NOT EXISTS address_restriction
(
    id         INT         NOT NULL AUTO_INCREMENT,
    postcode   VARCHAR(20) NOT NULL,
    `from`     DATE        NOT NULL,
    `to`       DATE        NOT NULL,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);
