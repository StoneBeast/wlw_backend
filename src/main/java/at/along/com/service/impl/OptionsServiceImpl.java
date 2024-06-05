package at.along.com.service.impl;

import at.along.com.dao.OptionsMapper;
import at.along.com.entity.Options;
import at.along.com.service.OptionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OptionsServiceImpl implements OptionsService {
    @Autowired
    private OptionsMapper optionsMapper;

    @Override
    public Map<String, Object> getEdit() {
        List<String> list= optionsMapper.selectAllByType_code();
        List<String> list1= optionsMapper.selectAllByType_code1();
        List<String> list2= optionsMapper.selectAllByType_code2();
        Map<String,Object> map=new HashMap<>();
        map.put("industryOptions",list);
        map.put("productTypeOptions",list1);
        map.put("accessAgreementOptions",list2);
        return map;
    }
}
