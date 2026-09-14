-- Creates the messages table for message timestamps and text content.
-- @since 0.1.0
-- @author TiJ
CREATE TABLE IF NOT EXISTS messages (
    mId CHAR(32) PRIMARY KEY,
    mDate TIMESTAMP NOT NULL,
    mText VARCHAR(4192) NOT NULL,
    mAuthorId CHAR(32) NOT NULL,
    CONSTRAINT FK_messages_mAuthorId FOREIGN KEY (mAuthorId) REFERENCES users(uId)
);