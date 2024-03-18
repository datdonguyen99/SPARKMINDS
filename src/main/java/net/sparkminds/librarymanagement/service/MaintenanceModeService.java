package net.sparkminds.librarymanagement.service;

public interface MaintenanceModeService {
    void setMode(boolean enabled);

    boolean isEnable();
}
