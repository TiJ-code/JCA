package tij.jca.infrastructure.storage.h2;

import tij.jca.core.repositories.IConversationRepository;
import tij.jca.core.repositories.IDeviceRepository;
import tij.jca.core.repositories.IMessageRepository;
import tij.jca.core.repositories.IServerRepository;
import tij.jca.core.repositories.IUserRepository;
import tij.jca.core.storage.ITransactionScope;
import tij.jca.infrastructure.storage.h2.repository.H2ConversationRepository;
import tij.jca.infrastructure.storage.h2.repository.H2DeviceRepository;
import tij.jca.infrastructure.storage.h2.repository.H2MessageRepository;
import tij.jca.infrastructure.storage.h2.repository.H2ServerRepository;
import tij.jca.infrastructure.storage.h2.repository.H2UserRepository;

import java.util.Objects;

public final class H2TransactionScope implements ITransactionScope {
    private final IUserRepository users;
    private final IDeviceRepository devices;
    private final IServerRepository servers;
    private final IConversationRepository conversations;
    private final IMessageRepository messages;

    public H2TransactionScope(H2StorageTransaction transaction) {
        Objects.requireNonNull(transaction, "transaction");

        this.users = new H2UserRepository(transaction);
        this.devices = new H2DeviceRepository(transaction);
        this.servers = new H2ServerRepository(transaction);
        this.conversations = new H2ConversationRepository(transaction);
        this.messages = new H2MessageRepository(transaction);
    }

    @Override
    public IUserRepository users() {
        return users;
    }

    @Override
    public IDeviceRepository devices() {
        return devices;
    }

    @Override
    public IServerRepository servers() {
        return servers;
    }

    @Override
    public IConversationRepository conversations() {
        return conversations;
    }

    @Override
    public IMessageRepository messages() {
        return messages;
    }
}
