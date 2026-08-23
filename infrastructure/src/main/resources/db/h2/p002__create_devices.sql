CREATE TABLE IF NOT EXISTS devices (
    dId CHAR(32) PRIMARY KEY,
    dType ENUM('desktop', 'mobile') NOT NULL
);