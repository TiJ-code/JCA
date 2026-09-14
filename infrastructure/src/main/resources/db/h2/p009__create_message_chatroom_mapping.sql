-- Creates the mapping between messages and chatrooms.
-- @since 0.1.0
-- @author TiJ
CREATE TABLE IF NOT EXISTS message_chatroom_mapping (
  messageId CHAR(32) NOT NULL,
  chatroomId CHAR(32) NOT NULL,
  PRIMARY KEY (messageId, chatroomId),
  CONSTRAINT FK_messageChatroomMapping_messageId FOREIGN KEY (messageId) REFERENCES messages(mId),
  CONSTRAINT FK_messageChatroomMapping_chatroomId FOREIGN KEY (chatroomId) REFERENCES chatrooms(cId)
);