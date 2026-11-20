package tij.jca.application.services;

import tij.jca.core.repositories.IConversationRepository;
import tij.jca.core.repositories.IDeviceRepository;
import tij.jca.core.repositories.IMessageRepository;
import tij.jca.core.repositories.IServerRepository;
import tij.jca.core.repositories.IUserRepository;
import tij.jca.core.storage.ITransactionScope;

import java.util.Objects;

record FakeTransactionScope(
        IUserRepository users,
        IDeviceRepository devices,
        IServerRepository servers,
        IConversationRepository conversations, IMessageRepository messages
) implements ITransactionScope {
    FakeTransactionScope(
            IUserRepository users,
            IDeviceRepository devices,
            IServerRepository servers,
            IConversationRepository conversations,
            IMessageRepository messages
    ) {
        this.users = Objects.requireNonNull(users, "users");
        this.devices = Objects.requireNonNull(devices, "devices");
        this.servers = Objects.requireNonNull(servers, "servers");
        this.conversations =
                Objects.requireNonNull(conversations, "conversations");
        this.messages = Objects.requireNonNull(messages, "messages");
    }
}
