-- file: 20-create-person-table.sql
CREATE TABLE IF NOT EXISTS person (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    address VARCHAR(255) NOT NULL,
    work VARCHAR(255) NOT NULL
);

GRANT ALL PRIVILEGES ON TABLE person TO program;
