package at.along.com.controller;

import at.along.com.entity.Users;
//import at.along.com.service.impl.MailServiceImpl;
import at.along.com.service.impl.MailServiceImpl;
import at.along.com.service.impl.UserServiceImpl;
import at.along.com.utils.*;
import com.alibaba.fastjson.JSON;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;



@Slf4j
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private MailServiceImpl mailService;

    @PostMapping("/register")
    public Result add(@RequestBody String str,HttpServletRequest request){

        Map<String,Object> mapType = JSON.parseObject(str,Map.class);
        String email= String.valueOf(mapType.get("userEmail")) ;
        String password= String.valueOf(mapType.get("password"));
        Users user1=userService.selectByEmail(email);
        if(user1!=null){
            return ResponseUtils.failResult(ResultCode.USER_EXIST,"该邮箱已注册");
        }
        String emailCode=String.valueOf(mapType.get("emailCode")) ;
        String emailkey=request.getHeader("emailSession");
        String value= null;
        try {
            value = redisUtils.get(emailkey).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtils.failResult("验证码错误");
        }
        redisUtils.del(emailkey);
        if (!value.equals(emailCode)){
            return ResponseUtils.failResult("验证码错误");
        }

        Users user=new Users();
        user.setUserEmail(email);
        user.setCreateDate(new Date());
        String password_k= MD5util.getMd5(password);
        user.setUserPassword(password_k);
        Random r = new Random();
        String id=String.valueOf(r.nextInt(99999)+300000);
        user.setUserId(id);

        String defaultName="DEFAULTNAME"+Authcodeutil.getUUID();
        user.setUserName(defaultName);
        int result=userService.add(user);
        if(result==1){
            return ResponseUtils.successResult("注册成功");
        }else {
            return ResponseUtils.failResult(ResultCode.register_fail,"注册失败");
        }


    }

    @PostMapping("/updata")
    public Result update(@RequestBody Users user){
        if (user==null){
            return ResponseUtils.failResult("参数传输失败");
        }
        user.setUpdateDate(new Date());
        int result=userService.add(user);
        if(result==1){
            return ResponseUtils.successResult("修改成功");
        }else {
            return ResponseUtils.failResult("保存失败");
        }
    }

    @PostMapping("/emailLogin")
    public Result login(@RequestBody  String str, HttpServletResponse response,HttpServletRequest request){

        if (str==null){
            return ResponseUtils.failResult("参数传输失败");
        }
        Map<String,Object> mapType = JSON.parseObject(str,Map.class);
        String email= String.valueOf(mapType.get("userEmail")) ;
        String code= String.valueOf(mapType.get("emailCode")) ;
        String emailkey=request.getHeader("emailSession");
        String value=redisUtils.get(emailkey).toString();
        redisUtils.del(emailkey);
        if (!value.equals(code)){
            return ResponseUtils.failResult("验证码错误");
        }
        Users users=new Users();
        users.setUserEmail(email);
        Map map1 =userService.log(users);
        if (map1.get("code").equals("0")){
            return ResponseUtils.failResult("用户名或密码错误");
        }
        response.setHeader("Authorization",JWTUtil.USER_TOKEN+"="+(String) map1.get("token"));
        response.setHeader("Access-Control-Expose-Headers","Authorization");
        return ResponseUtils.successResult("登录成功", map1.get("user"));
    }

    @GetMapping("/query")
    public Result query(){
        List<Users> list=userService.select();
        Map<String,Object> map=new HashMap<>();
        map.put("total",list.size());
        map.put("user",list);
        return ResponseUtils.successResult("查询成功",map);
    }


    @PostMapping ("/getEmailCode")
    public Result getcode(@RequestBody String email, HttpServletRequest request,HttpServletResponse response){
        Map<String,Object> mapType = JSON.parseObject(email,Map.class);
        String email1= (String) mapType.get("userEmail");
        String code=Authcodeutil.getUUID();
        String key= Authcodeutil.getUUID();
        redisUtils.set(key,code);
        response.setHeader("emailSession",key);
        response.setHeader("Access-Control-Expose-Headers","emailSession");
        String subject="物联网平台";
        String content="<html lang=\"zh\">\n" +
                "<head>\n" +
                "    <meta charset=utf-8>\n" +
                "    <title>ttt</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div id=\"title\">\n" +
                "        <h2 style=\"color: rgb(6, 171, 255)\">邮箱验证码</h2>\n" +
                "    </div>\n" +
                "    <div id=\"context\">\n" +
                "        <p>欢迎使用AllNet物联网平台</p>\n" +
                "        <p>您本次的验证码为: "+code+"<span>。验证码有效期为2分钟。</span></p>\n" +
                "        <p>如果您没有请求本验证码，请忽略。</p>\n" +
                "    </div>\n" +
                "    <div id=\"sign\" style=\"font-size: 13px\">AllNet物联网平台管理团队</div>\n" +
                "</body>\n" +
                "</html>\n";
        mailService.sendWithHtml(email1,subject,content);
        redisUtils.expire(key,60*2);
        return ResponseUtils.successResult("发送成功");
    }

    @PostMapping("/login")
    public Result login1(@RequestBody  String str, HttpServletResponse response){

        if (str==null){
            return ResponseUtils.failResult("参数传输失败");
        }
        Map<String,Object> mapType = JSON.parseObject(str,Map.class);
        String password= String.valueOf(mapType.get("password"));
        String userName= String.valueOf(mapType.get("userName"));
        Users users=new Users();
        users.setUserPassword(MD5util.getMd5(password));
        users.setUserEmail(userName);
        users.setUserName("userName"+Authcodeutil.getUUID());
        Map map1 =userService.log(users);
        if (map1.get("code").equals("0")){
            return ResponseUtils.failResult("用户名或密码错误");
        }
        response.setHeader("Authorization",JWTUtil.USER_TOKEN+"="+map1.get("token"));
        response.setHeader("Access-Control-Expose-Headers","Authorization");
        return ResponseUtils.successResult("登录成功", map1.get("user"));
    }

    @GetMapping("getUserInfo")
    public Result getUserInfo(HttpServletRequest request){

//        String token= (String) request.getAttribute(JWTUtil.USER_TOKEN);
        String token= (String) request.getHeader("Authorization").substring(18);
        DecodedJWT verify=JWTUtil.verify(token);
        String userId=verify.getSubject();

        Users users=userService.selectByuserId(userId);
        Map<String,Object> map=new HashMap<>();
        map.put("userName",users.getUserName());
        map.put("avatarUrl",users.getAvatarUrl());
        return ResponseUtils.successResult("success",map);
    }

    
}
