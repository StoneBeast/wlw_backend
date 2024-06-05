package at.along.com.dao;

import at.along.com.entity.Command;

import java.util.List;

public interface CommandMapper {
    public int insertAll(Command command);
    public int getCount(String productId,String deviceId);
    List<Command> findAllByPage(String productId, String deviceId);
    public int deleteByDeviceIdInt(String deviceId);
}
