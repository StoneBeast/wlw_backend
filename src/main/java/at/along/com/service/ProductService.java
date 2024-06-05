package at.along.com.service;



import at.along.com.entity.Product;


import java.util.List;
import java.util.Map;

public interface ProductService {
    public int add(Product product);
    public int update(Product product);
    public int delete(String productId,String userId);
    public List<Product> getlist();
    public Product getlist(String id,String userId);
    List<Map<String,Object>> findAllByPage(int page, int offset, String userId);
    public int getcount(String userId);
}
