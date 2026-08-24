-- Creates the users table for JCA user identities and display names.
-- @since 0.1.0
-- @author TiJ
CREATE TABLE IF NOT EXISTS users
(
    uId                CHAR(32) PRIMARY KEY,
    uName              VARCHAR(255) NOT NULL,
    uGlobalDisplayName VARCHAR(255) NOT NULL
);