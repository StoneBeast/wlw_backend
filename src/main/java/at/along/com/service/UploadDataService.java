package at.along.com.service;

import at.along.com.entity.UploadData;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface UploadDataService {
    public int getCount(String productId, Long start, Long end);
    public List<Map<String,Object>> getDataCount(String productId, Long start, Long end);
    public Map<String,Object> getDate(String productId,String deviceId);
    public List<Map<String,Object>> getDataAndDate(String dataTemplateId,String deviceId,String start,String end);
    public List<Map<String,Object>> getData1(String deviceId,List<String> dateTemplateId);
    public int insertAll(UploadData uploadData);
    public List<Map<String,Object>> fetchNewData(String deviceId,String dataTemplateId,int count);
    public int getTodayData(String productId);
    public Map<String,Object> getDataAndDateByPage(String dataTemplateId,String deviceId,int page,int pageSize);
 }
