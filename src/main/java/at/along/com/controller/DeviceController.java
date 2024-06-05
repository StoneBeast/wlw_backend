package at.along.com.controller;

import at.along.com.entity.Device;
import at.along.com.service.impl.DeviceServiceImpl;
import at.along.com.utils.*;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequestMapping("/api/data")
@RestController
@CrossOrigin(origins = "*")
public class DeviceController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private DeviceServiceImpl deviceService;

    @Value("${mq.ip}")
    private String ip;

    @PostMapping("/addDevice")
    public Result addDevice(@RequestBody String str) {
        Random r = new Random();
        String id = String.valueOf(r.nextInt(99999) + 200000);
        Device device=new Device();
        JSONObject jSONObject = JSONObject.parseObject(str);
        String productId=jSONObject.getString("productId");
        String deviceId=jSONObject.getString("deviceId");
        String deviceName=jSONObject.getString("deviceName");
        String deviceAuthentication=jSONObject.getString("deviceAuthentication");
        String deviceIntroduction=jSONObject.getString("deviceIntroduction");
        device.setProductId(productId);
        device.setDeviceAuthentication(deviceAuthentication);
        device.setDeviceName(deviceName);
        device.setDeviceIntroduction(deviceIntroduction);
        device.setDeviceId(deviceId);
        String api=ip + ":8080/api/open/device/";
        if (device.getDeviceId().equals("")){
            device.setDeviceId(id);
            api+=id;
            device.setDeviceApi(api);
            int result=deviceService.adddevice(device);
            if (result>0){
                return ResponseUtils.successResult("添加成功");
            }
            return ResponseUtils.failResult(ResultCode.add_fail,"添加失败！");

        }else {
             int result=deviceService.update(device);
            if (result>0){
                return ResponseUtils.successResult("修改成功！");
            }
            return ResponseUtils.failResult(ResultCode.update_fail,"修改数据失败！");
        }

    }

    @GetMapping("fetchDeviceList")
    public Result getListDevice(@RequestParam("productId") String productId, @RequestParam("page") String page, @RequestParam("pageSize") String pageSize) {

        PageInfo<Device> list = deviceService.getListDevice(Integer.parseInt(page), Integer.parseInt(pageSize), productId);
        if (list.getSize() == 0) {
            return ResponseUtils.failResult("该产品未添加设备");
        }

        List<Map<String, Object>> mapList = new ArrayList<>();
        List<Device> deviceList = list.getList();
        for (int i = 0; i < list.getSize(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("deviceId", deviceList.get(i).getDeviceId());
            map.put("deviceName", deviceList.get(i).getDeviceName());
            map.put("lastOnlineTime",deviceList.get(i).getLastOnlineTime());
            map.put("deviceStatus",deviceList.get(i).getDeviceStatus());
            mapList.add(map);
        }
        Map<String, Object> mapo = new HashMap<>();
        mapo.put("total", mapList.size());
        mapo.put("devicelist", mapList);
        return ResponseUtils.successResult("查询成功", mapo);
    }

    @PostMapping("/fetchDeviceStatus")
    private Result fetchDeviceStatus(@RequestBody String str) {
        List<Map<String,Object>> deviceList=deviceService.getdeviceStatus(str);
        return ResponseUtils.successResult("success", deviceList);
    }

    @GetMapping("/fetchDeviceInfo")
    private Result fetchDeviceInfo(@RequestParam String productId, @RequestParam String deviceId) {
        Map<String, Object> map = deviceService.getDevice1(productId, deviceId);
        return ResponseUtils.successResult("success", map);
    }
    @DeleteMapping("/deleteDevice")
    private Result deleteDevice(@RequestParam String productId,@RequestParam String deviceId){

        int result=deviceService.deleteDevice(productId,deviceId);
        if(result==0){
            return ResponseUtils.failResult(ResultCode.delete_fail,"删除数据失败");
        }else {
            return ResponseUtils.successResult("删除成功");
        }
    }

    @GetMapping("/addApiKey")
    private Result addApiKey(@RequestParam String productId,@RequestParam String deviceId){
        StringBuffer str=new StringBuffer();
        String uuid=UUID.randomUUID().toString().replace("-","");
        for(int i=0;i<16;i++){
            str.append(uuid.charAt(i));
        }
        String apiKey=str.toString();
        int result=deviceService.addApiKey(productId,deviceId,apiKey);
        Map<String,Object> map=new HashMap<>();
        map.put("apiKey",apiKey);
        if(result==0){
            return ResponseUtils.failResult("添加失败",null);
        }else {
            return ResponseUtils.successResult("success",map);
        }
    }

    @GetMapping("/fetchConnectedDevice")
    private Result fetchConnectedDevice(@RequestParam("productId") String productId,
                                        @RequestParam("start") Long start,@RequestParam("end") Long  end){
        List<Map<String,Object>> list=deviceService.getNewDevicdCount(productId,start,end);
        return ResponseUtils.successResult("success",list);
    }
}
