-- Creates the servers table for server identities and names.
-- @since 0.1.0
-- @author TiJ
CREATE TABLE IF NOT EXISTS servers
(
    sId   CHAR(32) PRIMARY KEY,
    sName VARCHAR(255) NOT NULL
);