package at.along.com.controller;

import at.along.com.entity.DataTemplate;
import at.along.com.service.impl.DataTemplateServiceImpl;
import at.along.com.utils.ResponseUtils;
import at.along.com.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RequestMapping("/api/data")
@RestController
@CrossOrigin(origins = "*")
public class DatatemplateController {

    @Autowired
    private DataTemplateServiceImpl dataTemplateService;


    @GetMapping("/fetchDataStream")
    private Result fetchDataSteam(@RequestParam String productId,@RequestParam String page,@RequestParam String pageSize){
        Map<String,Object> map=dataTemplateService.getStream(productId,page,pageSize);
        return ResponseUtils.successResult("success",map);
    }

    @PostMapping("/addDataStream")
    private Result addDataStream(@RequestBody String str) {
        int size = dataTemplateService.addRoUpdate(str);
        if (size == 0) {
            return ResponseUtils.failResult("添加或修改失败");
        } else {
            return ResponseUtils.successResult("success", null);
        }
    }

    @GetMapping("/fetchDataStreamInfo")
    public Result fetchDataStreamInfo(@RequestParam String productId,@RequestParam String dataTemplateId){
        DataTemplate list=dataTemplateService.queryByProductIdAndDataTemplateId(productId,dataTemplateId);
        return ResponseUtils.successResult("success",list);
    }

    @DeleteMapping("/deleteDataStream")
    public Result deleteDataStream(@RequestParam String productId,@RequestParam String dataTemplateId){
        int res= dataTemplateService.delete(productId,dataTemplateId);
        if (res!=1){
            return ResponseUtils.failResult("unsuccess",null);
        }
        return ResponseUtils.successResult("success",null);
    }
}
