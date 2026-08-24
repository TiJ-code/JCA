-- Creates the mapping between servers and their chatrooms.
-- @since 0.1.0
-- @author TiJ
CREATE TABLE IF NOT EXISTS server_chatroom_mapping
(
    serverId   CHAR(32) NOT NULL,
    chatroomId CHAR(32) NOT NULL,
    PRIMARY KEY (serverId, chatroomId),
    CONSTRAINT FK_serverChatroomMapping_serverId FOREIGN KEY (serverId) REFERENCES servers (sId),
    CONSTRAINT FK_serverChatroomMapping_chatroomId FOREIGN KEY (chatroomId) REFERENCES chatrooms (cId)
);