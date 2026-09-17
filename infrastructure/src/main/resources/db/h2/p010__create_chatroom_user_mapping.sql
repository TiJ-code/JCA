-- Creates the mapping between users and chatrooms.
-- @since 0.1.0
-- @author TiJ
CREATE TABLE IF NOT EXISTS chatroom_user_mapping (
  chatroomId CHAR(32) NOT NULL,
  userId CHAR(32) NOT NULL,
  PRIMARY KEY (chatroomId, userId),
  CONSTRAINT FK_chatroomUserMapping_chatroomId FOREIGN KEY (chatroomId) REFERENCES chatrooms(cId),
  CONSTRAINT FK_chatroomUserMapping_userId FOREIGN KEY (userId) REFERENCES users(uId)
);