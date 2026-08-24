-- Creates the chatrooms table for chatroom identities and names.
-- @since 0.1.0
-- @author TiJ
CREATE TABLE IF NOT EXISTS chatrooms
(
    cId   CHAR(32) PRIMARY KEY,
    cName VARCHAR(255) NOT NULL
);