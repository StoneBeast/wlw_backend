package at.along.com.service;

import at.along.com.entity.Command;

import java.util.List;
import java.util.Map;

public interface CommandService {
    public int addCommand(String str);
    public int addCommand(Command command);
    public int getCount(String productId,String deviceId);
    List<Map<String,Object>> findAllByPage(int page, int offset, String productId,String deviceId);
}
