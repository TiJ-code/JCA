package tij.jca.core.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tij.jca.core.ids.ConversationID;
import tij.jca.core.ids.DeviceID;
import tij.jca.core.ids.MessageID;
import tij.jca.core.ids.ServerID;
import tij.jca.core.ids.UUIDGen;
import tij.jca.core.ids.UserID;

import java.time.Instant;

class EntitiesTest {
    @Test
    void deviceStoresItsIdentityAndType() {
        final String id = UUIDGen.random();

        Device device = new Device(
                new DeviceID(id),
                DeviceType.DESKTOP
        );

        Assertions.assertEquals(id, device.id().id());
        Assertions.assertEquals(DeviceType.DESKTOP, device.type());
    }

    @Test
    void userCanManageDevices() {
        final String userId = UUIDGen.random();
        final String deviceId = UUIDGen.random();

        User user = new User(
                new UserID(userId),
                "linux",
                "Linus Torvalds"
        );

        Device device = new Device(
                new DeviceID(deviceId),
                DeviceType.DESKTOP
        );

        user.addDevice(device);

        Assertions.assertTrue(user.deviceIds().contains(device.id()));

        user.removeDevice(device.id());

        Assertions.assertFalse(user.deviceIds().contains(device.id()));
    }

    @Test
    void messageStoresItsContent() {
        final Instant date = Instant.now();
        final String messageId = UUIDGen.random();
        final String authorId = UUIDGen.random();
        final String text = "Hello, world!";

        Message message = new Message(
                new MessageID(messageId),
                new UserID(authorId),
                date,
                text
        );

        Assertions.assertEquals(messageId, message.id().id());
        Assertions.assertEquals(authorId, message.authorId().id());
        Assertions.assertEquals(date, message.date());
        Assertions.assertEquals(text, message.text());
    }

    @Test
    void messageRejectsBlankText() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Message(
                new MessageID(UUIDGen.random()),
                new UserID(UUIDGen.random()),
                Instant.now(),
                " "
        ));

        Assertions.assertThrows(IllegalArgumentException.class, () -> new Message(
                new MessageID(UUIDGen.random()),
                new UserID(UUIDGen.random()),
                Instant.now(),
                ""
        ));
    }

    @Test
    void chatRoomManagesUsersAndMessages() {
        final UserID userId = new UserID(UUIDGen.random());

        ChatRoom chatRoom = new ChatRoom(
                new ConversationID(UUIDGen.random()),
                "General"
        );

        Message message = new Message(
                new MessageID(UUIDGen.random()),
                userId,
                Instant.now(),
                "Hello, world!"
        );

        chatRoom.addUser(userId);
        chatRoom.addMessage(message);

        Assertions.assertTrue(chatRoom.userIds().contains(userId));
        Assertions.assertEquals(1, chatRoom.messages().size());
        Assertions.assertEquals(message, chatRoom.messages().getFirst());

        chatRoom.removeUser(userId);
        chatRoom.removeMessage(message.id());

        Assertions.assertTrue(chatRoom.userIds().isEmpty());
        Assertions.assertTrue(chatRoom.messages().isEmpty());
    }

    @Test
    void chatRoomRejectsDuplicateMessages() {
        var chatRoom = new ChatRoom(
                new ConversationID(UUIDGen.random()),
                "General"
        );

        Message message = new Message(
                new MessageID(UUIDGen.random()),
                new UserID(UUIDGen.random()),
                Instant.now(),
                "Repeated content"
        );

        chatRoom.addMessage(message);

        Assertions.assertThrows(IllegalArgumentException.class, () -> chatRoom.addMessage(message));
    }

    @Test
    void serverManagesUsersAndChatRooms() {
        Server server = new Server(
                new ServerID(UUIDGen.random()),
                "Main Server"
        );

        UserID userId = new UserID(UUIDGen.random());
        ChatRoom chatRoom = new ChatRoom(
                new ConversationID(UUIDGen.random()),
                "General"
        );

        server.addUser(userId);
        server.addChatRoom(chatRoom);

        Assertions.assertTrue(server.userIds().contains(userId));
        Assertions.assertSame(chatRoom, server.chatRoom(chatRoom.id()));
        Assertions.assertEquals(1, server.chatRooms().size());

        server.removeUser(userId);
        server.removeChatRoom(chatRoom.id());

        Assertions.assertTrue(server.userIds().isEmpty());
        Assertions.assertTrue(server.chatRooms().isEmpty());
    }

    @Test
    void serverRejectsDuplicateChatRooms() {
        final String conversationId = UUIDGen.random();

        Server server = new Server(
                new ServerID(UUIDGen.random()),
                "Main Server"
        );

        ChatRoom first = new ChatRoom(
                new ConversationID(conversationId),
                "General"
        );

        ChatRoom duplicate = new ChatRoom(
                new ConversationID(conversationId),
                "Another name"
        );

        server.addChatRoom(first);

        Assertions.assertThrows(IllegalArgumentException.class, () -> server.addChatRoom(duplicate));
    }

    @Test
    void associationRecordsCompareByValue() {
        final UserID userId = new UserID(UUIDGen.random());
        final DeviceID deviceId = new DeviceID(UUIDGen.random());
        final ServerID serverId = new ServerID(UUIDGen.random());
        final ConversationID roomId = new ConversationID(UUIDGen.random());

        Assertions.assertEquals(
                new UserDeviceAssociation(userId, deviceId),
                new UserDeviceAssociation(userId, deviceId)
        );

        Assertions.assertEquals(
                new UserServerAssociation(userId, serverId, "Linus T."),
                new UserServerAssociation(userId, serverId, "Linus T.")
        );

        Assertions.assertEquals(
                new ServerChatRoomAssociation(serverId, roomId),
                new ServerChatRoomAssociation(serverId, roomId)
        );
    }

    @Test
    void collectionsCannotBeModifiedExternally() {
        User user = new User(
                new UserID(UUIDGen.random()),
                "linus",
                "Linus Torvalds"
        );

        user.addDevice(new Device(
                new DeviceID(UUIDGen.random()),
                DeviceType.DESKTOP
        ));

        Assertions.assertThrows(UnsupportedOperationException.class, () -> user.deviceIds().clear());
    }
}
