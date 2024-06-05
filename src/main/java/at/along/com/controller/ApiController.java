package at.along.com.controller;


import at.along.com.entity.Command;
import at.along.com.mqtt.MyMQTTClient;
import at.along.com.service.impl.CommandServiceImpl;
import at.along.com.service.impl.DataTemplateServiceImpl;
import at.along.com.service.impl.DeviceServiceImpl;
import at.along.com.service.impl.UploadDataServiceImpl;
import at.along.com.utils.ResponseUtils;
import at.along.com.utils.Result;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RequestMapping("/api/open")
@RestController
@CrossOrigin(origins = "*")
public class ApiController {

    @Autowired
    private MyMQTTClient myMQTTClient;

    @Autowired
    private DeviceServiceImpl deviceService;

    @Autowired
    private UploadDataServiceImpl uploadDataService;

    @Autowired
    private CommandServiceImpl commandService;

    @Autowired
    private DataTemplateServiceImpl dataTemplateService;

    @GetMapping("/device/{deviceId}/data")
    private Result fetchStreamData(@PathVariable String deviceId,
                                   @RequestParam String apiKey,
                                   @RequestParam String start,
                                   @RequestParam String end,
                                   @RequestParam String dataTemplateId){
        String realKey=deviceService.getApiKey(deviceId);
        if(realKey.equals("")){
            return ResponseUtils.successResult(403,"权限不足",null);
        }
        if(!apiKey.equals(realKey)){
            return ResponseUtils.successResult(403,"权限不足",null);
        }
        String productId=deviceService.getProductId(deviceId);
        String[] realDataTemplateIds=dataTemplateService.getDataTemplateId(productId);
        for(int i=0;i<realDataTemplateIds.length;i++){
            if(realDataTemplateIds[i].equals(dataTemplateId)){
                List<Map<String,Object>> list=uploadDataService.getDataAndDate(dataTemplateId,deviceId,start,end);
                return ResponseUtils.successResult("success",list);
            }
        }
        return ResponseUtils.successResult(403,"权限不足",null);
    }

    @PostMapping("/device/{deviceId}/command")
    private Result addCommand(@PathVariable String deviceId,
                              @RequestBody String str){
        Map<String,Object> mapType= JSON.parseObject(str,Map.class);
        String productId=String.valueOf(mapType.get("productId"));
        String command=String.valueOf(mapType.get("command"));
        String apiKey=String.valueOf(mapType.get("apiKey"));
        String realKey=deviceService.getApiKey(deviceId);
        String realProductId=deviceService.getProductId(deviceId);
        if(!productId.equals(realProductId)){
            return ResponseUtils.successResult(403,"权限不足",null);
        }
        else if(realProductId.equals((""))){
            return ResponseUtils.successResult(403,"权限不足",null);
        }
        else if(realKey.equals("")){
            return ResponseUtils.successResult(403,"权限不足",null);
        }
        else if(!apiKey.equals(realKey)){
            return ResponseUtils.successResult(403,"权限不足",null);
        }
        String topic="$platform/command/"+productId+"/"+deviceId;
        myMQTTClient.publish(command,topic);
        Command realCommand=new Command();
        Random r = new Random();
        String commandId = String.valueOf(r.nextInt(99999) + 200000);
        Date createDate=new Date();
        Timestamp time=new Timestamp(createDate.getTime());
        realCommand.setCommandId(commandId);
        realCommand.setProductId(productId);
        realCommand.setDeviceId(deviceId);
        realCommand.setCreateDate(time);
        realCommand.setCommandText(command);
        int size=commandService.addCommand(realCommand);
        if(size!=1){
            return ResponseUtils.failResult("下发失败",null);
        }else {
            return ResponseUtils.successResult("下发成功",null);
        }
    }

}
