package tij.jca.infrastructure.storage.h2;

/**
 * Centralises the table and column names used by the H2 storage layer.
 * <p>
 * The constants in this class define the schema contract for all persisted
 * application entities, their relationship tables, and the schema migration
 * tracking table used by the database bootstrap process.
 * </p>
 *
 * @since 0.1.0
 * @author TiJ
 */
public final class H2DatabaseConstants {
    /**
     * Prevents instantiation of this constants class.
     */
    private H2DatabaseConstants() {
    }

    /**
     * Name of the table that stores applied database schema migrations.
     */
    public static final String TABLE__JCA_SCHEMA_MIGRATIONS = "jca_schema_migrations";

    /**
     * Name of the users table.
     */
    public static final String TABLE__USERS = "users";

    /**
     * Name of the devices table.
     */
    public static final String TABLE__DEVICES = "devices";

    /**
     * Name of the messages table.
     */
    public static final String TABLE__MESSAGES = "messages";

    /**
     * Name of the servers table.
     */
    public static final String TABLE__SERVERS = "servers";

    /**
     * Name of the chat rooms table.
     */
    public static final String TABLE__CHAT_ROOMS = "chatrooms";

    /**
     * Name of the table mapping users to the devices they are attached to.
     */
    public static final String TABLE__USER_DEVICE_MAPPING = "user_device_mapping";

    /**
     * Name of the table mapping servers to the chat rooms they host.
     */
    public static final String TABLE__SERVER_CHATROOM_MAPPING = "server_chatroom_mapping";

    /**
     * Name of the table mapping users to the servers they belong to.
     */
    public static final String TABLE__USER_SERVER_MAPPING = "user_server_mapping";

    /**
     * Name of the table mapping messages to the chat rooms they belong to.
     */
    public static final String TABLE__MESSAGE_CHATROOM_MAPPING = "message_chatroom_mapping";

    /**
     * Name of the table mapping chat rooms to the users participating in them.
     */
    public static final String TABLE__CHATROOM_USER_MAPPING = "chatroom_user_mapping";

    /**
     * Name of the schema migration version column.
     */
    public static final String COLUMN__JCA_SCHEMA_MIGRATIONS__VERSION = "version";

    /**
     * Name of the schema migration description column.
     */
    public static final String COLUMN__JCA_SCHEMA_MIGRATIONS__DESCRIPTION = "description";

    /**
     * Name of the schema migration application timestamp column.
     */
    public static final String COLUMN__JCA_SCHEMA_MIGRATIONS__APPLIED_AT = "applied_at";

    /**
     * Name of the users table identifier column.
     */
    public static final String COLUMN__USERS__ID = "uId";

    /**
     * Name of the users table username column.
     */
    public static final String COLUMN__USERS__NAME = "uName";

    /**
     * Name of the users table global display name column.
     */
    public static final String COLUMN__USERS__GLOBAL_DISPLAY_NAME = "uGlobalDisplayName";

    /**
     * Name of the devices table identifier column.
     */
    public static final String COLUMN__DEVICES__ID = "dId";

    /**
     * Name of the devices table type column.
     */
    public static final String COLUMN__DEVICES__TYPE = "dType";

    /**
     * Name of the messages table identifier column.
     */
    public static final String COLUMN__MESSAGES__ID = "mId";

    /**
     * Name of the messages table timestamp column.
     */
    public static final String COLUMN__MESSAGES__DATE = "mDate";

    /**
     * Name of the messages table text content column.
     */
    public static final String COLUMN__MESSAGES__TEXT = "mText";

    /**
     * Name of the messages table author identifier column.
     */
    public static final String COLUMN__MESSAGES__AUTHOR_ID = "mAuthorId";

    /**
     * Name of the servers table identifier column.
     */
    public static final String COLUMN__SERVERS__ID = "sId";

    /**
     * Name of the servers table name column.
     */
    public static final String COLUMN__SERVERS__NAME = "sName";

    /**
     * Name of the chatrooms table identifier column.
     */
    public static final String COLUMN__CHATROOMS__ID = "cId";

    /**
     * Name of the chatrooms table name column.
     */
    public static final String COLUMN__CHATROOMS__NAME = "cName";

    /**
     * Name of the user-device mapping user identifier column.
     */
    public static final String COLUMN__USER_DEVICE_MAPPING__USER_ID = "userId";

    /**
     * Name of the user-device mapping device identifier column.
     */
    public static final String COLUMN__USER_DEVICE_MAPPING__DEVICE_ID = "deviceId";

    /**
     * Name of the server-chatroom mapping server identifier column.
     */
    public static final String COLUMN__SERVER_CHATROOM_MAPPING__SERVER_ID = "serverId";

    /**
     * Name of the server-chatroom mapping chatroom identifier column.
     */
    public static final String COLUMN__SERVER_CHATROOM_MAPPING__CHATROOM_ID = "chatroomId";

    /**
     * Name of the user-server mapping user identifier column.
     */
    public static final String COLUMN__USER_SERVER_MAPPING__USER_ID = "userId";

    /**
     * Name of the user-server mapping server identifier column.
     */
    public static final String COLUMN__USER_SERVER_MAPPING__SERVER_ID = "serverId";

    /**
     * Name of the user-server mapping display-name column.
     */
    public static final String COLUMN__USER_SERVER_MAPPING__SERVER_DISPLAY_NAME = "userServerDisplayName";

    /**
     * Name of the message-chatroom mapping message identifier column.
     */
    public static final String COLUMN__MESSAGE_CHATROOM_MAPPING__MESSAGE_ID = "messageId";

    /**
     * Name of the message-chatroom mapping chatroom identifier column.
     */
    public static final String COLUMN__MESSAGE_CHATROOM_MAPPING__CHATROOM_ID = "chatroomId";

    /**
     * Name of the chatroom-user mapping chatroom identifier column.
     */
    public static final String COLUMN__CHATROOM_USER_MAPPING__CHATROOM_ID = "chatroomId";

    /**
     * Name of the chatroom-user mapping user identifier column.
     */
    public static final String COLUMN__CHATROOM_USER_MAPPING__USER_ID = "userId";
}
