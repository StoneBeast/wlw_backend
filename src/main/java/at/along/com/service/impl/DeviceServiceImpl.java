package at.along.com.service.impl;

import at.along.com.dao.CommandMapper;
import at.along.com.dao.DeviceMapper;
import at.along.com.dao.EmqxMapper;
import at.along.com.dao.UploadDataMapper;
import at.along.com.entity.Device;
import at.along.com.entity.Emqx;
import at.along.com.service.DeviceService;
import at.along.com.utils.HttpClientUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    private EmqxMapper emqxMapper;
    @Autowired
    private CommandMapper commandMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private UploadDataMapper uploadDataMapper;
    @Value("${mq.ip}")
    private String ip;

    private Emqx emqx;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public int adddevice(Device device) {
        emqx=emqxMapper.selectOneByIdEmqx("1");
        String url = "http://"+ip+":18083/api/v5/authentication/password_based:built_in_database/users";
        Map<String, Object> map = new HashMap<>();
        map.put("user_id", device.getDeviceId());
        map.put("password", device.getDeviceAuthentication());
        String Response = HttpClientUtils.doPost(url, emqx.getUsername(), emqx.getPassword(), map, null);

        if (Response != null) {
            int result = deviceMapper.insertAll(device);
            if (result < 1) {
                return 0;
            }
            return 1;
        }
        return 1;
    }

    @Override
    public PageInfo<Device> getListDevice(int page,int offset,String productId) {
        PageHelper.startPage(page,offset);

        List<Device> all=deviceMapper.selectAllByProductId(productId);
        return new PageInfo<Device>(all);
    }



    @Override
    public Map<String,Object> getDevice1(String productId, String deviceId) {
        Device device=deviceMapper.selectByDeviceIdAanProductId(productId,deviceId);
        Map<String,Object> map=new HashMap<>();
        map.put("deviceId",device.getDeviceId());
        map.put("deviceName",device.getDeviceName());
        map.put("createDate",device.getCreateDate());
        map.put("deviceAuthentication",device.getDeviceAuthentication());
        map.put("deviceStatus",device.getDeviceStatus());
        map.put("deviceAPI",device.getDeviceApi());
        String apiKey=device.getApiKey();
        if(apiKey==null){
            apiKey="";
        }
        map.put("apiKey",apiKey);
        map.put("deviceIntroduction",device.getDeviceIntroduction());
        return map;
    }

    @Override
    public int updateStatusUp(String deviceId) {
        return deviceMapper.updateStatusUp(deviceId);
    }


    @Override
    public int updateStatusDown(String deviceId, Timestamp lastOnlineTime) {
        return deviceMapper.updateStatusDown(deviceId,lastOnlineTime);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteDevice(String productId, String deviceId) {
        emqx=emqxMapper.selectOneByIdEmqx("1");
        String url="http://"+ip+":18083/api/v5/authentication/password_based:built_in_database/users/"+deviceId;
        String response= HttpClientUtils.doDeleteForMqtt(url,emqx.getUsername(),emqx.getPassword());
        if (response!=null){
            int result= 0;
            try {
                result = deviceMapper.deleteByDeviceIdAndProductIdInt(productId,deviceId);
                uploadDataMapper.deleteByDeviceIdInt(deviceId);
                commandMapper.deleteByDeviceIdInt(deviceId);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
            if (result<1){
                return 0;
            }
            String url1="http://"+ip+":18083/api/v5/clients/"+deviceId;
            String response1= HttpClientUtils.doDeleteForMqtt(url1,emqx.getUsername(),emqx.getPassword());
            return 1;
        }
        return 0;
    }

    @Override
    public int update(Device device) {

        return deviceMapper.updateAll(device);
    }

    @Override
    public int addApiKey(String productId, String deviceId, String apiKey) {
        return deviceMapper.addApiKey(productId,deviceId,apiKey);
    }

    @Override
    public String getApiKey(String deviceId) {
        Device device=deviceMapper.getApiKey(deviceId);
        if(device==null){
            return "";
        }
        return device.getApiKey();
    }

    @Override
    public List<Map<String, Object>> getNewDevicdCount(String productId, Long start, Long end) {
        Locale locale=Locale.getDefault();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",locale);//设置时间格式
        SimpleDateFormat format1=new SimpleDateFormat("yyyy-MM-dd",locale);
        Date startDate= null;
        Date endDate=null;
        try {
            startDate=format.parse(format.format(start));
            endDate=format.parse(format.format(end));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar=new GregorianCalendar();
        calendar.setTime(startDate);
        int total=deviceMapper.selectByStartAndEndAndProductId(productId,new Timestamp(startDate.getTime()),new Timestamp(endDate.getTime()));
        List<Map<String,Object>> list=new ArrayList<>();
        while (startDate.before(endDate)){
            int cout1=deviceMapper.selectDataByProductId(productId,new Timestamp(startDate.getTime()));
            Map<String,Object> map=new HashMap<>();
            map.put("date",format.format(startDate));
            map.put("newCount",cout1);
            map.put("totalCount",total);
            list.add(map);
            calendar.add(Calendar.DATE,1);
            startDate=calendar.getTime();
        }
        int cout1=deviceMapper.selectDataByProductId(productId,new Timestamp(endDate.getTime()));
        Map<String,Object> map=new HashMap<>();
        map.put("date",format.format(endDate));
        map.put("newCount",cout1);
        map.put("totalCount",total);
        list.add(map);

        return list;
    }

    @Override
    public String getProductId(String deviceId) {
        Device device=deviceMapper.getProductId(deviceId);
        if (device==null){
            return "";
        }
        return device.getProductId();
    }

    @Override
    public List<Map<String, Object>> getdeviceStatus(String json) {
        JSONObject jSONObject = JSONObject.parseObject(json);
        String productId = jSONObject.getString("productId");
        List<String> deviceIdList = JSON.parseArray(jSONObject.getString("deviceId"), String.class);
        List<Map<String, Object>> deviceList = new ArrayList<>();
        Iterator<String> it = deviceIdList.listIterator();
        Iterator<Device> itdevice = null;
        while (it.hasNext()) {
            String deviceId = it.next();
            if (deviceId.equals("*")) {
                List<Device> deviceList1 = deviceMapper.selectAllByProductId(productId);
                itdevice = deviceList1.listIterator();
                while (itdevice.hasNext()) {
                    Device device = itdevice.next();
                    Map<String, Object> map = new HashMap<>();
                    map.put("deviceId", device.getDeviceId());
                    map.put("lastOnlineTime", device.getLastOnlineTime());
                    map.put("deviceStatus", device.getDeviceStatus());
                    deviceList.add(map);
                }
                return deviceList;
            }else {
                Device device=deviceMapper.selectByDeviceIdAanProductId(productId,deviceId);
                if (device==null){
                   continue;
                }
                Map<String, Object> map = new HashMap<>();
                map.put("deviceId", device.getDeviceId());
                map.put("lastOnlineTime", device.getLastOnlineTime());
                map.put("deviceStatus", device.getDeviceStatus());
                deviceList.add(map);
            }
        }
        return deviceList;
    }


}
