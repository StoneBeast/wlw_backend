package at.along.com.dao;

import at.along.com.entity.Product;


import java.util.List;

public interface ProductMapper {
    public int insertOne(Product product);
    public int updateById(Product product);
    public int delByProductIdInt(String productId,String userId);
    public List<Product> query();
    public Product queryId(String id,String userId);
    List<Product> findAllByPage(String userId);
    public int getCount(String userId);
    public int deleteByUserId(String productId);
}
