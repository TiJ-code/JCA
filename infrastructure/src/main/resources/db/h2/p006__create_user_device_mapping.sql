-- Creates the mapping between users and their client devices.
-- @since 0.1.0
-- @author TiJ
CREATE TABLE IF NOT EXISTS user_device_mapping
(
    userId   CHAR(32) NOT NULL,
    deviceId CHAR(32) NOT NULL,
    PRIMARY KEY (userId, deviceId),
    CONSTRAINT FK_userDeviceMapping_userId FOREIGN KEY (userId) REFERENCES users (uId),
    CONSTRAINT FK_userDeviceMapping_deviceId FOREIGN KEY (deviceId) REFERENCES devices (dId)
);