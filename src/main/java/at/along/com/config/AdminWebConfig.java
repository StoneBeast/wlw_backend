package at.along.com.config;


import at.along.com.Interceptor.CorsInterceptor;
import at.along.com.Interceptor.TokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;


//@Configuration
public class AdminWebConfig implements WebMvcConfigurer {

    @Resource
    private TokenInterceptor tokenInterceptor;
    @Resource
    private CorsInterceptor corsInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(corsInterceptor).addPathPatterns("/**");

        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**")  //所有请求都被拦截包括静态资源
                .excludePathPatterns("/api/user/login").excludePathPatterns("/api/user/register").excludePathPatterns("/api/user/getEmailCode")
                .excludePathPatterns("/api/user/emailLogin").excludePathPatterns("/api/open/device/{deviceId}/data").excludePathPatterns("/api/open/device/{deviceId}/command");//放行的请求
    }

}
