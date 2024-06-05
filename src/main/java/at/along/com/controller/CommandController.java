package at.along.com.controller;

import at.along.com.mqtt.MyMQTTClient;
import at.along.com.service.impl.CommandServiceImpl;
import at.along.com.utils.ResponseUtils;
import at.along.com.utils.Result;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/api/data")
@RestController
@CrossOrigin(origins = "*")
public class CommandController {

    @Autowired
    private MyMQTTClient myMQTTClient;

    @Autowired
    private CommandServiceImpl commandService;

    @PostMapping("/command")
    private Result command(@RequestBody String str){
        JSONObject jSONObject = JSONObject.parseObject(str);
        String productId=jSONObject.getString("productId");
        String deviceId=jSONObject.getString("deviceId");
        Object command;
        if (jSONObject.get("command").getClass()==String.class){
            command=jSONObject.getString("command");
        }else {
//            int command=jSONObject.getInteger("command");
        }
        String topic="$platform/command"+productId+"/"+deviceId;
//        myMQTTClient.publish(command,topic);
        return null;
    }

    @PostMapping("/sendCommand")
    private Result sendCommand(@RequestBody String str){
        Map<String,Object> mapType= JSON.parseObject(str,Map.class);
        String productId=String.valueOf(mapType.get("productId"));
        String deviceId=String.valueOf(mapType.get("deviceId"));
        String commandText=String.valueOf(mapType.get("commandText"));
        String topic="$platform/command/"+productId+"/"+deviceId;
        myMQTTClient.publish(commandText,topic);
        int size=commandService.addCommand(str);
        if(size!=1){
            return ResponseUtils.failResult("上传失败",null);
        }else {
            return ResponseUtils.successResult("上传成功",null);
        }
    }

    @GetMapping("/fetchCommandList")
    private Result fetchCommandList(@RequestParam(value = "page") int page,
                                    @RequestParam(value = "pageSize") int pageSize,
                                    @RequestParam(value = "productId") String productId,
                                    @RequestParam(value = "deviceId") String deviceId){
        PageHelper.startPage(page,pageSize);
        List<Map<String,Object>> list=commandService.findAllByPage(page,pageSize,productId,deviceId);
        int count=commandService.getCount(productId,deviceId);
        Map<String,Object> map=new HashMap<>();
        map.put("total",count);
        map.put("commandList",list);
        return ResponseUtils.successResult(200,"success",map);
    }
}
