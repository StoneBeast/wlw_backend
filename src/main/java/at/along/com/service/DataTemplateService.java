package at.along.com.service;

import at.along.com.entity.DataTemplate;

import java.util.Map;

public interface DataTemplateService {
    public Map<String,Object> getStream(String productId,String page,String pageSize);
    public int addRoUpdate(String  str);
    public String getDTid(String productID,String deviceID);
    public DataTemplate queryByProductIdAndDataTemplateId(String productId,String dataTemplateId);
    public int delete(String productId,String dataTemplateId);
    public String[] getDataTemplateId(String productId);
}
