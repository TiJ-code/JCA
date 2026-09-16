package tij.jca.core.storage;

import tij.jca.core.repositories.IConversationRepository;
import tij.jca.core.repositories.IDeviceRepository;
import tij.jca.core.repositories.IMessageRepository;
import tij.jca.core.repositories.IServerRepository;
import tij.jca.core.repositories.IUserRepository;

public interface ITransactionScope {
    IUserRepository users();

    IDeviceRepository devices();

    IServerRepository servers();

    IConversationRepository conversations();

    IMessageRepository messages();
}
