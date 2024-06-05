package at.along.com.service.impl;

import at.along.com.dao.CommandMapper;
import at.along.com.entity.Command;
import at.along.com.entity.Product;
import at.along.com.service.CommandService;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
public class CommandServiceImpl implements CommandService {

    @Autowired
    private CommandMapper commandMapper;

    @Override
    public int addCommand(String str) {
        Map<String,Object> mapType= JSON.parseObject(str,Map.class);
        Random r = new Random();
        String commandId = String.valueOf(r.nextInt(99999) + 200000);
        String productId=String.valueOf(mapType.get("productId"));
        String deviceId=String.valueOf(mapType.get("deviceId"));
        String commandType=String.valueOf(mapType.get("commandType"));
        Date createDate=new Date();
        Timestamp time=new Timestamp(createDate.getTime());
        Command command=new Command();
        command.setCommandId(commandId);
        command.setProductId(productId);
        command.setDeviceId(deviceId);
        command.setCreateDate(time);
        if(commandType.equals("number")){
            int Text=Integer.valueOf((Integer) mapType.get("commandText"));
            String commandText=String.valueOf(Text);
            command.setCommandText(commandText);
        }else {
            String commandText=String.valueOf(mapType.get("commandText"));
            command.setCommandText(commandText);
        }
        return commandMapper.insertAll(command);
    }

    @Override
    public int addCommand(Command command) {
        return commandMapper.insertAll(command);
    }

    @Override
    public int getCount(String productId, String deviceId) {
        return commandMapper.getCount(productId,deviceId);
    }

    @Override
    public List<Map<String, Object>> findAllByPage(int page, int offset, String productId, String deviceId) {
        PageHelper.startPage(page,offset);
        List<Command> all=commandMapper.findAllByPage(productId,deviceId);
        Iterator<Command> it=all.listIterator();
        List<Map<String,Object>> list=new ArrayList<>();
        while (it.hasNext()){
            Command command= it.next();
            Map<String,Object> map=new HashMap<>();
            map.put("commandId",command.getCommandId());
            map.put("createDate",command.getCreateDate());
            map.put("commandText",command.getCommandText());
            map.put("commandStatus",command.getCommandStatus());
            map.put("commandResponse",command.getResponse());
            list.add(map);
        }
        return list;
    }
}
