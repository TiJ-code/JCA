package tij.jca.core.storage.migration;

import tij.jca.core.storage.IStorageTransaction;

public interface IMigration {
    int version();

    String description();

    void apply(IStorageTransaction transaction);
}
