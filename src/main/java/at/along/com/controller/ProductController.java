package at.along.com.controller;

import at.along.com.entity.Product;
import at.along.com.service.impl.OptionsServiceImpl;
import at.along.com.service.impl.ProductServiceImpl;
import at.along.com.utils.*;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;
import java.util.*;

@RequestMapping("/api/data")
@RestController
@CrossOrigin(origins = "*")
public class ProductController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ProductServiceImpl productService;
    @Autowired
    private OptionsServiceImpl optionsService;


    @PostMapping("/addUpdateProduct")
    public Result add(@RequestBody Product product, HttpServletRequest request){
        if(product==null){
            return ResponseUtils.failResult("系统出错！没接收到数据");
        }
//        String token= (String) request.getAttribute(JWTUtil.USER_TOKEN);
        String token= (String) request.getHeader("Authorization").substring(18);
        DecodedJWT verify=JWTUtil.verify(token);
        String userId=verify.getSubject();
        product.setUserId(userId);
        if (product.getProductId().equals("")){

            Random r = new Random();
            String id=String.valueOf(r.nextInt(99999)+100000);
            product.setProductId(id);
            product.setCreateDate(new Date());
            int res=productService.add(product);
            if (res!=1){
                return ResponseUtils.failResult("存储失败");
            }
            return ResponseUtils.successResult("存储成功");
        }else {
            product.setUpdateDate(new Date());
            int res=productService.update(product);
            if (res!=1){
                return ResponseUtils.failResult("修改失败");
            }
            return ResponseUtils.successResult("修改成功");
        }
    }


    @DeleteMapping("/deleteProduct")
    public Result delete(@RequestParam("productId") String productId,HttpServletRequest request){

//        String token= (String) request.getAttribute(JWTUtil.USER_TOKEN);
        String token= (String) request.getHeader("Authorization").substring(18);
        DecodedJWT verify=JWTUtil.verify(token);
        String userId=verify.getSubject();
        int res=productService.delete(productId,userId);
        if (res!=1){
            return ResponseUtils.failResult("删除失败!");
        }
        return ResponseUtils.successResult("删除成功！");
    }

    @GetMapping("/fetchProductList")
    public Result getlist(@RequestParam(value = "page") int page,@RequestParam(value = "pageSize") int pageSize,HttpServletRequest request){
//        String token= (String) request.getAttribute(JWTUtil.USER_TOKEN);
        String token= (String) request.getHeader("Authorization").substring(18);
        DecodedJWT verify=JWTUtil.verify(token);
        String userId=verify.getSubject();
        PageHelper.startPage(page,pageSize);
        List<Map<String,Object>> list= productService.findAllByPage(page,pageSize,userId);
        int total=productService.getcount(userId);
        Map<String,Object> map=new HashMap<>();
        map.put("total",total);
        map.put("products",list);
        return ResponseUtils.successResult(200,"success",map);
    }


    @GetMapping("/fetchProductData")
    public Result getlistId(@RequestParam("productId") String productId, HttpServletRequest request){
//        String token= (String) request.getAttribute(JWTUtil.USER_TOKEN);
        String token= (String) request.getHeader("Authorization").substring(18);
        DecodedJWT verify=JWTUtil.verify(token);
        String userId=verify.getSubject();
        Product list=productService.getlist(productId,userId);
//        HttpSession session=request.getSession();
//        session.setAttribute("productId",list.getProductId());
        Map<String,Object> map=new HashMap<>();
        return ResponseUtils.successResult(200,"success",list);
    }

    @GetMapping("/fetchEditOptions")
    public Result getEdit(){
        Map<String,Object> map=optionsService.getEdit();
        return ResponseUtils.successResult(200,"success",map);
    }


}
