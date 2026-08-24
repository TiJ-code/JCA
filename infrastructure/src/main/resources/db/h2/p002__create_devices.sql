-- Creates the devices table used to associate users with client devices.
-- @since 0.1.0
-- @author TiJ
CREATE TABLE IF NOT EXISTS devices
(
    dId   CHAR(32) PRIMARY KEY,
    dType ENUM ('desktop', 'mobile') NOT NULL
);