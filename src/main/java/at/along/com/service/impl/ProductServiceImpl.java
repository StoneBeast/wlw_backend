package at.along.com.service.impl;

import at.along.com.dao.CommandMapper;
import at.along.com.dao.DeviceMapper;
import at.along.com.dao.UploadDataMapper;
import at.along.com.entity.Device;
import at.along.com.entity.Product;
import at.along.com.dao.ProductMapper;
import at.along.com.entity.UploadData;
import at.along.com.service.ProductService;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.TabableView;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

@Service

public class ProductServiceImpl implements ProductService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DeviceServiceImpl deviceService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CommandMapper commandMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private UploadDataMapper uploadDataMapper;

    @Override
    public int add(Product product) {
        int res=productMapper.insertOne(product);
        return res;
    }

    @Override
    public int update(Product product) {
        int res=productMapper.updateById(product);
        return res;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delete(String productId,String userId) {
        int res= 0;
        try {
            List<Device> list=deviceMapper.selectAllByProductId(productId);
            Iterator<Device> it=list.listIterator();
            while (it.hasNext()){
                Device device=it.next();
                int result = deviceService.deleteDevice(productId,device.getDeviceId());
            }
            res = productMapper.delByProductIdInt(productId,userId);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return res;
    }

    @Override
    public List<Product> getlist() {
        List<Product> list=productMapper.query();
        return list;
    }

    @Override
    public Product getlist(String  id,String userId){
        Product list=productMapper.queryId(id,userId);
        return list;
    }

    @Override
    public List<Map<String,Object>> findAllByPage(int page, int offset, String userId) {


        PageHelper.startPage(page,offset);
        List<Product> all = productMapper.findAllByPage(userId);
        Iterator<Product> it=all.listIterator();
        List<Map<String,Object>> list=new ArrayList<>();

        while (it.hasNext()){
            Product product=it.next();
            Map<String,Object> map=new HashMap<>();
            map.put("productId",product.getProductId());
            map.put("productName",product.getProductName());
            map.put("productType",product.getProductType());
            map.put("deviceCount",deviceMapper.selectAllByProductIdInt(product.getProductId()));
            map.put("createDate",product.getCreateDate());
            list.add(map);
        }

        return list;
    }

    @Override
    public int getcount(String userId) {
        return productMapper.getCount(userId);
    }

}
