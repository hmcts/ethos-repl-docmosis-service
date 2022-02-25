DROP TABLE IF EXISTS judge;
Drop type emp_status;
CREATE TYPE emp_status AS ENUM('SALARIED', 'FEE_PAID', 'UNKNOWN');


CREATE TABLE judge (
                       id SERIAL PRIMARY KEY,
                       tribunal_office VARCHAR(100),
                       code VARCHAR(100),
                       name VARCHAR(100),
                       employment_status emp_status
);
