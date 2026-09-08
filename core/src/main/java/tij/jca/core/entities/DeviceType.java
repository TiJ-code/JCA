package tij.jca.core.entities;

public enum DeviceType {
    DESKTOP,
    MOBILE;

    public String databaseValue() {
        return name().toLowerCase();
    }
}
