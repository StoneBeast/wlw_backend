package at.along.com.dao;

import at.along.com.entity.Device;
import org.apache.ibatis.annotations.Delete;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public interface DeviceMapper
{
    public int insertAll(Device device);
    public int deleteByProductId(String productId);
    public List<Device> selectAllByProductId(String productId);
    public int selectAllByProductIdInt(String productId);
    public Device selectByDeviceId(String productId,String deviceId);
    public Device selectByDeviceIdAanProductId(String productId,String deviceId);
    public int updateStatusUp(String deviceId);
    public int updateStatusDown(String deviceId, Timestamp lastOnlineTime);
    public int deleteByDeviceIdAndProductIdInt(String productId,String deviceId);
    public int updateAll(Device device);
    public int addApiKey(String productId,String deviceId,String apiKey);
    public Device getApiKey(String deviceId);
    public int selectDataByProductId(String productId,Timestamp date);
    public int selectByStartAndEndAndProductId(String productId,Timestamp start,Timestamp end);
    public Device getProductId(String deviceId);
}
