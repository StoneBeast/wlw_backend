package at.along.com.dao;

import at.along.com.entity.DataTemplate;

import java.util.List;

public interface DataTemplateMapper{
    public List<DataTemplate> selectByproduct(String productId);
    public int getcount(String productId);
    public int insertAll(DataTemplate dataTemplate);
    public String getDTid(String productID,String dataName);
    public List<DataTemplate> getDataTemplateId(String productId);
    public int updateByProductIdAnddeviceId(DataTemplate dataTemplate);
    public DataTemplate queryByProductIdAndDataTemplateId(String productId,String dataTemplateId);
    public int delete(String productId,String dataTemplateId);

}
