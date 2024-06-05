package at.along.com;

import at.along.com.dao.UploadDataMapper;
import at.along.com.utils.RedisUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TTT {

    @Autowired
    private UploadDataMapper mapper;
    public static void main(String[] args) {
        RedisUtils redisUtils=new RedisUtils();
        redisUtils.set("1212","shibi");
        System.out.println(redisUtils.get("1212"));
    }

    @Test
    public void test(){
        Date date =new Date();//

        System.out.println(date);//输出结果 Sat Jul 30 14:42:54 CST 2022

        long time1=date.getTime();//获取当前时间的毫秒表现格式
        System.out.println(time1);

        Locale locale=Locale.getDefault();//类名.方法名--是静态方法的调用方式
        //获取当前的时区，--也可以使用该类获取当前的语言环境

        String p="yyyy-MM-dd HH:mm:ss";//设置要转换的日期格式
        //也可以是 yyyy-MM-dd HH:mm:ss  yyyy\MM\dd HH:mm:ss 中间的字符可以任意修改
        SimpleDateFormat sformat=new SimpleDateFormat(p,locale);

        String time2=sformat.format(time1);

        System.out.println(time2);//输出结果  2022年07月30 14:42:54
    }
    @Test
    public void test2(){

        Map<String,Object> map=new HashMap<>();
        Map<String,Object> map1=new HashMap<>();
        map.put("username","ssss");
        map.put("password","ccccc");
        map.put("sanly","zhansan1");
        map1.put("zongti",map);
        String st=map1.toString();

    }
}
