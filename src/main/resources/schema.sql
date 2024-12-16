CREATE TABLE IF NOT EXISTS file(
    id      BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name    VARCHAR(255),
    digest  VARCHAR(255),
    PRIMARY KEY (id)
);