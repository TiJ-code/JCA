CREATE TABLE IF NOT EXISTS message_chatroom_mapping (
  messageId CHAR(32) NOT NULL,
  chatroomId CHAR(32) NOT NULL,
  PRIMARY KEY (messageId, chatroomId),
  CONSTRAINT FK_messageChatroomMapping_messageId FOREIGN KEY (messageId) REFERENCES messages(mId),
  CONSTRAINT FK_messageChatroomMapping_chatroomId FOREIGN KEY (chatroomId) REFERENCES chatrooms(cId)
);