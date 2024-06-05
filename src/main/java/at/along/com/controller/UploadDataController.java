package at.along.com.controller;

import at.along.com.service.impl.UploadDataServiceImpl;
import at.along.com.utils.ResponseUtils;
import at.along.com.utils.Result;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RequestMapping("/api/data")
@RestController
@CrossOrigin(origins = "*")
public class UploadDataController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private UploadDataServiceImpl uploadDataService;

    @GetMapping("/fetchUploadDeviceCount")
    private Result fetchUploadDeviceCount(@RequestParam("productId") String productId,
                                          @RequestParam("start") Long start, @RequestParam("end") Long  end){



        List<Map<String,Object>> list=uploadDataService.getDataCount(productId,start,end);
        int count= uploadDataService.getTodayData(productId);
        Map<String,Object> map=new HashMap<>();
        map.put("todayUploadDevice",count);
        map.put("datalist",list);
        return ResponseUtils.successResult("success",map);
    }


    @GetMapping("/deviceDataUploadInfo")
    private Result fetchDeviceInfo(@RequestParam String productId,@RequestParam String deviceId){
        Map<String,Object> Map=uploadDataService.getDate(productId,deviceId);
        return ResponseUtils.successResult("success",Map);
    }
    @GetMapping("/fetchStreamData")
    private Result fetchStreamData(@RequestParam String dataTemplateId,
                                   @RequestParam String deviceId,
                                   @RequestParam String start,
                                   @RequestParam String end){

        List<Map<String,Object>> list=uploadDataService.getDataAndDate(dataTemplateId,deviceId,start,end);
        return ResponseUtils.successResult("success",list);
    }

    @PostMapping("/fetchLatestData")
    private Result fetchLatestData(@RequestBody JSONObject param){
        String deviceId=String.valueOf(param.getString("deviceId"));
        List<String> list=JSON.parseArray(param.getString("dataTemplateIdList"),String.class);
        List<Map<String,Object>> list1=uploadDataService.getData1(deviceId,list);
        return ResponseUtils.successResult("success",list1);
    }

    @GetMapping("/fetchNewData")
    public Result fetchNewData(@RequestParam String deviceId,@RequestParam String dataTemplateId,@RequestParam int count){
        List<Map<String,Object>> list=uploadDataService.fetchNewData(deviceId,dataTemplateId,count);
        return ResponseUtils.successResult("success",list);
    }

    @GetMapping("/fetchStreamDataPage")
    public Result fetchStreamDataPage(@RequestParam String dataTemplateId,
                                      @RequestParam String deviceId,
                                      @RequestParam int page,
                                      @RequestParam int pageSize){
        Map<String,Object> map=uploadDataService.getDataAndDateByPage(dataTemplateId,deviceId,page,pageSize);
        return ResponseUtils.successResult("success",map);
    }
}
