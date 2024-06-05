package at.along.com.service.impl;

import at.along.com.dao.DeviceMapper;
import at.along.com.dao.ProductMapper;
import at.along.com.entity.Users;
import at.along.com.dao.UserMapper;
import at.along.com.service.UserService;
import at.along.com.utils.JWTUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private DeviceMapper deviceMapper;
    @Override
    public List<Users> select() {
        return userMapper.Query();
    }

    @Override
    public int add(Users users) {
        int result=userMapper.insertAll(users);
        return result;
    }

    @Override
    public Map<String, Object> log(Users users) {


        String email=users.getUserEmail();
        String password=users.getUserPassword();
        Users user;
        if (password==null){
            user=userMapper.selectOneByUserEmail(email);
        }else {
            user=userMapper.selectByEmailAndPassword(email,password);
        }
        Map<String,Object> map = new HashMap<>();
        if (user==null){
            map.put("code","0");
            return map;
        }
        //将userId存入token中
        Integer integer=new Integer(user.getUserId());
        String s3=integer.toString();
        String token = JWTUtil.createToken(s3);
        map = new HashMap<>();
        map.put("user",user);
        map.put("token",token);
        map.put("code","1");
        map.put("exp",JWTUtil.getDxp(token));
        return map;
    }

    @Override
    public Users selectByEmail(String email) {
        Users user=userMapper.selectOneByUserEmail(email);
        return user;
    }

    @Override
    public Users selectByuserId(String userId) {
        return userMapper.selectOneByUserId(userId);
    }


}
