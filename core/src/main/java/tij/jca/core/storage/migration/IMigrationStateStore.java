package tij.jca.core.storage.migration;

import tij.jca.core.storage.IStorageEngine;
import tij.jca.core.storage.IStorageTransaction;

import java.util.List;

public interface IMigrationStateStore {
    List<Integer> getAppliedVersions(IStorageEngine storageEngine);

    void recordApplied(IStorageTransaction transaction, IMigration migration);
}
