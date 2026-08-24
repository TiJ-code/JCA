-- Creates the mapping between users and servers, including display names.
-- @since 0.1.0
-- @author TiJ
CREATE TABLE IF NOT EXISTS user_server_mapping
(
    userId                CHAR(32)     NOT NULL,
    serverId              CHAR(32)     NOT NULL,
    userServerDisplayName VARCHAR(255) NULL,
    PRIMARY KEY (userId, serverId),
    CONSTRAINT FK_userServerMapping_userId FOREIGN KEY (userId) REFERENCES users (uId),
    CONSTRAINT FK_userServerMapping_serverId FOREIGN KEY (serverId) REFERENCES servers (sId)
);