CREATE TABLE IF NOT EXISTS user_server_mapping (
    userId BIGINT NOT NULL,
    serverId BIGINT NOT NULL,
    userServerDisplayName VARCHAR(255) NULL,
    PRIMARY KEY (userId, serverId),
    CONSTRAINT FK_userServerMapping_userId FOREIGN KEY (userId) REFERENCES users(uId),
    CONSTRAINT FK_userServerMapping_serverId FOREIGN KEY (serverId) REFERENCES servers(sId)
);