package at.along.com.service;

import at.along.com.entity.Device;
import com.github.pagehelper.PageInfo;

import java.sql.Timestamp;
import java.util.List;
import java.util.Date;
import java.util.Map;

public interface DeviceService {
    public int adddevice(Device device);
    public PageInfo<Device> getListDevice(int page, int offset, String productId);
    public Map<String,Object> getDevice1(String productId, String deviceId);
    public int updateStatusUp(String deviceID);
    public List<Map<String,Object>> getdeviceStatus(String json);
    public int updateStatusDown(String deviceID, Timestamp lastOnlineTime);
    public int deleteDevice(String productId,String deviceId);
    public int update(Device device);
    public int addApiKey(String productId,String deviceId,String apiKey);
    public String getApiKey(String deviceId);
    public List<Map<String,Object>> getNewDevicdCount(String productId ,Long start, Long end);
    public String getProductId(String deviceId);
}
