-- JCA H2 Users Creation Table
CREATE TABLE IF NOT EXISTS users (
    uId CHAR(32) PRIMARY KEY,
    uName VARCHAR(255) NOT NULL,
    uGlobalDisplayName VARCHAR(255) NOT NULL
);