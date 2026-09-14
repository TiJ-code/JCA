package tij.jca.infrastructure.storage.h2;

/**
 * Names of tables and columns used by the H2 storage implementation.
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
     * Name of the table that stores applied schema migrations.
     */
    public static final String TABLE__JCA_SCHEMA_MIGRATIONS = "jca_schema_migrations";

    public static final String TABLE__USERS = "users";

    public static final String TABLE__DEVICES = "devices";

    public static final String TABLE__MESSAGES = "messages";

    public static final String TABLE__SERVERS = "servers";

    public static final String TABLE__CHAT_ROOMS = "chatrooms";

    public static final String TABLE__USER_DEVICE_MAPPING = "user_device_mapping";

    public static final String TABLE__SERVER_CHATROOM_MAPPING = "server_chatroom_mapping";

    public static final String TABLE__USER_SERVER_MAPPING = "user_server_mapping";

    public static final String TABLE__MESSAGE_CHATROOM_MAPPING = "message_chatroom_mapping";

    public static final String TABLE__CHATROOM_USER_MAPPING = "chatroom_user_mapping";

    /**
     *  Name of the schema migration version column.
     */
    public static final String COLUMN__JCA_SCHEMA_MIGRATIONS__VERSION = "version";

    /**
     * Name of the schema migration description column.
     */
    public static final String COLUMN__JCA_SCHEMA_MIGRATIONS__DESCRIPTION = "description";

    /**
     *  Name of the schema migration application timestamp column.
     *  */
    public static final String COLUMN__JCA_SCHEMA_MIGRATIONS__APPLIED_AT = "applied_at";

    public static final String COLUMN__USERS__ID = "uId";

    public static final String COLUMN__USERS__NAME = "uName";

    public static final String COLUMN__USERS__GLOBAL_DISPLAY_NAME = "uGlobalDisplayName";

    public static final String COLUMN__DEVICES__ID = "dId";

    public static final String COLUMN__DEVICES__TYPE = "dType";

    public static final String COLUMN__MESSAGES__ID = "mId";

    public static final String COLUMN__MESSAGES__DATE = "mDate";

    public static final String COLUMN__MESSAGES__TEXT = "mText";

    public static final String COLUMN__MESSAGES__AUTHOR_ID = "mAuthorId";

    public static final String COLUMN__SERVERS__ID = "sId";

    public static final String COLUMN__SERVERS__NAME = "sName";

    public static final String COLUMN__CHATROOMS__ID = "cId";

    public static final String COLUMN__CHATROOMS__NAME = "cName";

    public static final String COLUMN__USER_DEVICE_MAPPING__USER_ID = "userId";

    public static final String COLUMN__USER_DEVICE_MAPPING__DEVICE_ID = "deviceId";

    public static final String COLUMN__SERVER_CHATROOM_MAPPING__SERVER_ID = "serverId";

    public static final String COLUMN__SERVER_CHATROOM_MAPPING__CHATROOM_ID = "chatroomId";

    public static final String COLUMN__USER_SERVER_MAPPING__USER_ID = "userId";

    public static final String COLUMN__USER_SERVER_MAPPING__SERVER_ID = "serverId";

    public static final String COLUMN__USER_SERVER_MAPPING__SERVER_DISPLAY_NAME = "userServerDisplayName";

    public static final String COLUMN__MESSAGE_CHATROOM_MAPPING__MESSAGE_ID = "messageId";

    public static final String COLUMN__MESSAGE_CHATROOM_MAPPING__CHATROOM_ID = "chatroomId";

    public static final String COLUMN__CHATROOM_USER_MAPPING__CHATROOM_ID = "chatroomId";

    public static final String COLUMN__CHATROOM_USER_MAPPING__USER_ID = "userId";
}
