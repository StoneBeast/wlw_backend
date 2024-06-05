package at.along.com.dao;

import at.along.com.entity.UploadData;

import java.util.Date;
import java.util.List;

public interface UploadDataMapper {

    public int selectByDateAndProductId(String productId,String date);
    public int selectByStartAndEndAndProductId(String productId,Date start,Date end);
    public int selectAllByProductId(String productId,String start,String end);
    public int getAllByData(String productId, String deviceId);
    public List<String> getyesterdaydate(String productId,String deviceId);
    public int getWeekdate(String productId,String deviceId);
    public List<UploadData> selectByDataTemplateIdUploadData(String dataTemplateId,String deviceId,String start,String end);
    public UploadData selectDataOrderByDataTemplateIdAndDeviceId(String dataTemplateId,String deviceId);
    public int insertAll(UploadData uploadData);
    public List<UploadData> fetchNewData(String deviceId,String dataTemplateId,int count);
    public int selectByDateData(String productId);
    public int deleteByDeviceIdInt(String deviceId);
    public List<UploadData> selectAllByDataTemplateIdAndDeviceId(String dataTemplateId,String deviceId);
    public int getCount(String dataTemplateId,String deviceId);
}
