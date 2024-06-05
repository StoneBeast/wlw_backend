package at.along.com.service.impl;

import at.along.com.dao.DataTemplateMapper;
import at.along.com.entity.DataTemplate;
import at.along.com.service.DataTemplateService;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
public class DataTemplateServiceImpl implements DataTemplateService {

    @Autowired
    private DataTemplateMapper dataTemplateMapper;
    @Override
    public Map<String, Object> getStream(String productId, String page, String pageSize) {

        PageHelper.startPage(Integer.parseInt(page), Integer.parseInt(pageSize));
        List<DataTemplate> all=dataTemplateMapper.selectByproduct(productId);
        int count=dataTemplateMapper.getcount(productId);
        Map<String,Object> map=new HashMap<>();
        Iterator<DataTemplate> it=all.listIterator();
        List<Map<String,Object>> list=new ArrayList<>();

        while (it.hasNext()){
            DataTemplate dataTemplate=it.next();
            Map<String,Object> map1=new HashMap<>();
            map1.put("dataTemplateId",dataTemplate.getDataTemplateId());
            map1.put("dataName",dataTemplate.getDataName());
            map1.put("unitSymbols",dataTemplate.getUnitSymbols());
            map1.put("unitName",dataTemplate.getUnitName());
            list.add(map1);
        }
        map.put("streamList",list);
        map.put("totalStream",count);
        return map;
    }

    @Override
    public int addRoUpdate(String str) {

        Map<String,Object> mapType = JSON.parseObject(str,Map.class);
        String productId=String.valueOf(mapType.get("productId"));
        String dataName=String.valueOf(mapType.get("dataName"));
        String unitSymbols=String.valueOf(mapType.get("unitSymbols"));
        String unitName=String.valueOf(mapType.get("unitName"));
        String dataTemplateId1=String.valueOf(mapType.get("dataTemplateId"));
        DataTemplate template=new DataTemplate();
        template.setProductId(productId);
        template.setDataName(dataName);
        template.setUnitName(unitName);
        template.setUnitSymbols(unitSymbols);
        if (!dataTemplateId1.equals("")){
            String dataTemplateId=String.valueOf(mapType.get("dataTemplateId"));
            template.setDataTemplateId(dataTemplateId);
            return dataTemplateMapper.updateByProductIdAnddeviceId(template);
        }else {
            Random r = new Random();
            String id=String.valueOf(r.nextInt(99999)+200000);
            template.setDataTemplateId(id);
            return dataTemplateMapper.insertAll(template);
        }
    }

    @Override
    public String getDTid(String productID, String dataName) {
        return dataTemplateMapper.getDTid(productID,dataName);
    }

    @Override
    public DataTemplate queryByProductIdAndDataTemplateId(String productId, String dataTemplateId) {
        DataTemplate list=dataTemplateMapper.queryByProductIdAndDataTemplateId(productId,dataTemplateId);
        return list;
    }

    @Override
    public int delete(String productId, String dataTemplateId) {
        int res=dataTemplateMapper.delete(productId,dataTemplateId);
        return res;
    }

    @Override
    public String[] getDataTemplateId(String productId) {
        List<DataTemplate> dataTemplates=dataTemplateMapper.getDataTemplateId(productId);
        String[] dataTemplateIds=new String[dataTemplates.size()];
        for(int i=0;i<dataTemplates.size();i++){
            dataTemplateIds[i]=dataTemplates.get(i).getDataTemplateId();
        }
        return dataTemplateIds;
    }
}
