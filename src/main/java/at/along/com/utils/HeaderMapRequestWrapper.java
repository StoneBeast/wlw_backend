package at.along.com.utils;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.HashMap;
import java.util.Map;

public class HeaderMapRequestWrapper extends HttpServletRequestWrapper {

    private Map headerMap = new HashMap();

    public HeaderMapRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    public void addHeader(String name,String value){
        headerMap.put(name,value);
    }

    @Override
    public String getHeader(String name){
        String headerValue = super.getHeader(name);
        String value = (String) this.headerMap.get(name);
        if(!StringUtils.isEmpty(value)){
            return value;
        }
        return headerValue;
    }


}
