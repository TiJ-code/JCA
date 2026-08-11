package tij.jca.core.storage.page;

import java.util.List;

public record Page<T>(List<T> content, boolean hasNext) {}
