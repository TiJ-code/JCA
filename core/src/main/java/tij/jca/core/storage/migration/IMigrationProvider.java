package tij.jca.core.storage.migration;

import java.util.List;

public interface IMigrationProvider {
    List<IMigration> getMigrations();
}
