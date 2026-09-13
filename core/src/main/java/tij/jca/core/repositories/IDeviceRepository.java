package tij.jca.core.repositories;

import tij.jca.core.entities.Device;
import tij.jca.core.ids.DeviceID;
import tij.jca.core.ids.UserID;

import java.util.List;

public interface IDeviceRepository extends IRepository<Device, DeviceID> {
    List<Device> findByUserId(UserID userId);
}
