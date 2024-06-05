package at.along.com.service.impl;

import at.along.com.dao.UploadDataMapper;
import at.along.com.entity.DataTemplate;
import at.along.com.entity.UploadData;
import at.along.com.service.UploadDataService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.Data;
import lombok.extern.java.Log;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.annotation.WebServlet;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.*;
import java.util.List;

@Service
public class UploadDataServiceImpl implements UploadDataService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private UploadDataMapper uploadDataMapper;

    @Override
    public int getCount(String productId, Long start, Long end) {
        Locale locale=Locale.getDefault();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",locale);  //yyyy-MM-dd HH:mm:ss 为时间格式化
        Date sdate= null;
        Date edate=null;
        try {
            sdate = sdf.parse(sdf.format(start));
            edate=sdf.parse(sdf.format(end));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return uploadDataMapper.selectByStartAndEndAndProductId(productId,sdate,edate);
    }

    @Override
    public List<Map<String,Object>> getDataCount(String productId, Long start, Long end) {

//        Long startDate1=Long.parseLong(start);
//        Long endDate1=Long.parseLong(end);
//        Date dstart=new Date(startDate1);
//        Date dend=new Date(endDate1);
        Locale locale=Locale.getDefault();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",locale);//设置时间格式
        SimpleDateFormat format1=new SimpleDateFormat("yyyy-MM-dd",locale);
        Date startDate= null;
        Date endDate=null;
        try {
            startDate=format.parse(format.format(start));
            endDate=format.parse(format.format(end));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar=new GregorianCalendar();
        calendar.setTime(startDate);
        int total=uploadDataMapper.selectAllByProductId(productId,format.format(start),format.format(end));
        List<Map<String,Object>> list=new ArrayList<>();
        while (startDate.before(endDate)){
            int cout1=uploadDataMapper.selectByDateAndProductId(productId,format.format(startDate));
            Map<String,Object> map=new HashMap<>();
            map.put("date",format.format(startDate));
            map.put("newCount",cout1);
            map.put("totalCount",total);
            list.add(map);
            calendar.add(Calendar.DATE,1);
            startDate=calendar.getTime();
        }
        int cout1=uploadDataMapper.selectByDateAndProductId(productId,format.format(endDate));
        Map<String,Object> map=new HashMap<>();
        map.put("date",format.format(endDate));
        map.put("newCount",cout1);
        map.put("totalCount",total);
        list.add(map);

        return list;
    }

    @Override
    public Map<String,Object> getDate(String productId, String deviceId) {
        int stringSum=uploadDataMapper.getAllByData(productId,deviceId);
        List<String> yesterdayData=uploadDataMapper.getyesterdaydate(productId,deviceId);
        int weekData=uploadDataMapper.getWeekdate(productId,deviceId);
        Map<String,Object> map=new HashMap<>();

        map.put("deviceDataSum",stringSum);
        if (weekData==0){
            map.put("weekNew",weekData);
        }else {
            map.put("weekNew",weekData);
        }
        if (yesterdayData==null){
            map.put("yesterdayNew",0);
        }else {
            map.put("yesterdayNew",yesterdayData.size());
        }

        return map;
    }

    @Override
    public List<Map<String,Object>> getDataAndDate(String dataTemplateId, String deviceId, String start, String end) {
        List<UploadData> list=uploadDataMapper.selectByDataTemplateIdUploadData(dataTemplateId,deviceId,start,end);
        Iterator<UploadData> it=list.listIterator();
        List<Map<String,Object>> list1=new ArrayList<>();

        while (it.hasNext()){
            UploadData uploadData=it.next();
            Map<String,Object> map1=new HashMap<>();
            map1.put("date",uploadData.getUploadDate());
            map1.put("data",uploadData.getData());
            list1.add(map1);
        }
        return list1;
    }

    @Override
    public List<Map<String, Object>> getData1(String deviceId, List<String> dateTemplateId) {
        List<Map<String,Object>> list=new ArrayList<>();
        Iterator<String> it=dateTemplateId.listIterator();
        while (it.hasNext()){
            String id=it.next();
            UploadData uploadData=uploadDataMapper.selectDataOrderByDataTemplateIdAndDeviceId(id,deviceId);
            if (uploadData==null){
                Map<String,Object> map=new HashMap<>();
                map.put("dataTemplateId","");
                map.put("latestData","");
                map.put("uploadDate","");
                map.put("isNull",true);
                list.add(map);
                continue;
            }
            Map<String,Object> map=new HashMap<>();
            map.put("dataTemplateId",uploadData.getDataTemplateId());
            map.put("latestData",uploadData.getData());
            map.put("uploadDate",uploadData.getUploadDate());
            map.put("isNull",false);
            list.add(map);
        }
        return list;
    }

    @Override
    public int insertAll(UploadData uploadData) {
        return uploadDataMapper.insertAll(uploadData);
    }

    @Override
    public List<Map<String, Object>> fetchNewData(String deviceId, String dataTemplateId, int count) {
        List<UploadData> list=uploadDataMapper.fetchNewData(deviceId,dataTemplateId,count);
        Collections.reverse(list);
        List<Map<String,Object>> list1=new ArrayList<>();
        Iterator<UploadData> it=list.listIterator();
        while (it.hasNext()){
            UploadData uploadData=it.next();
            Map<String,Object> map=new HashMap<>();
            map.put("data",uploadData.getData());
            map.put("date",uploadData.getUploadDate());
            list1.add(map);
        }
        return list1;
    }

    @Override
    public int getTodayData(String productId) {
        return uploadDataMapper.selectByDateData(productId);
    }

    @Override
    public Map<String, Object> getDataAndDateByPage(String dataTemplateId, String deviceId, int page, int pageSize) {
        PageHelper.startPage(page,pageSize);
        List<UploadData> all=uploadDataMapper.selectAllByDataTemplateIdAndDeviceId(dataTemplateId,deviceId);
        int count=uploadDataMapper.getCount(dataTemplateId,deviceId);
        Map<String,Object> map=new HashMap<>();
        Iterator<UploadData> it=all.listIterator();
        List<Map<String,Object>> list=new ArrayList<>();
        while (it.hasNext()){
            UploadData uploadData= it.next();
            HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put("date",uploadData.getUploadDate());
            hashMap.put("data",uploadData.getData());
            list.add(hashMap);
        }
        map.put("total",count);
        map.put("dataList",list);
        return map;
    }

}
