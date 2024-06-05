package at.along.com.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.net.ssl.SSLContext;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class HttpClientUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);
    private static final CloseableHttpClient httpClient;

    // 采用静态代码块，初始化超时时间配置，再根据配置生成默认httpClient对象
    static {
        RequestConfig config = RequestConfig.custom().setConnectTimeout(30000).setSocketTimeout(15000).build();
        httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
    }

    /**
     * 发送 HTTP GET请求，不带请求参数和请求头
     *
     * @param url 请求地址
     * @return
     * @throws Exception
     */
    public static String doGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        String Respose = null;
        try {
            Respose = doHttp(httpGet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Respose;
    }

    /**
     * 发送 HTTP 请求
     *
     * @param request
     * @return
     * @throws Exception
     */
    private static String doHttp(HttpRequestBase request) throws Exception {
        // 通过连接池获取连接对象
        return doRequest(httpClient, request);
    }

    /**
     * 处理Http/Https请求，并返回请求结果，默认请求编码方式：UTF-8
     *
     * @param httpClient
     * @param request
     * @return
     */
    private static String doRequest(CloseableHttpClient httpClient, HttpRequestBase request) {
        String result = null;
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            // 获取请求结果
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                request.abort();
                throw new RuntimeException("HttpClient error status code: " + statusCode);
            }
            // 解析请求结果
            HttpEntity entity = response.getEntity();
            // 转换结果
            result = EntityUtils.toString(entity, StandardCharsets.UTF_8.name());
            // 关闭IO流
            EntityUtils.consume(entity);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 发送 HTTP GET，请求带参数，不带请求头
     *
     * @param url    请求地址
     * @param params 请求参数
     * @return
     * @throws Exception
     */
    public static String doGet(String url, Map<String, Object> params) {
        // 转换请求参数
        List<NameValuePair> pairs = covertParamsToList(params);
        // 装载请求地址和参数
        URIBuilder ub = new URIBuilder();
        ub.setPath(url);
        ub.setParameters(pairs);
        String Response = null;
        HttpRequestBase httpGet;
        try {
            httpGet = new HttpGet(ub.build());
            Response = doHttp(httpGet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response;
    }

    /**
     * 转换请求参数
     *
     * @param params
     * @return
     */
    public static List<NameValuePair> covertParamsToList(Map<String, Object> params) {
        if (params == null) {
            return null;
        }
        List<NameValuePair> pairs = new ArrayList<>();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            pairs.add(new BasicNameValuePair(param.getKey(), String.valueOf(param.getValue())));
        }
        return pairs;
    }

    /**
     * 发送 HTTP GET请求，带请求参数和请求头
     *
     * @param url     请求地址
     * @param headers 请求头
     * @param params  请求参数
     * @return
     * @throws Exception
     */
    public static String doGet(String url, Map<String, Object> headers, Map<String, Object> params) {

        // 装载请求地址和参数
        URIBuilder ub = new URIBuilder();
        if (params != null) {
            // 转换请求参数
            List<NameValuePair> pairs = covertParamsToList(params);
            ub.setParameters(pairs);
        }
        ub.setPath(url);
        HttpGet httpGet = null;
        String Response = null;
        try {
            httpGet = new HttpGet(ub.build());
            // 设置请求头
            for (Map.Entry<String, Object> param : headers.entrySet()) {
                httpGet.addHeader(param.getKey(), String.valueOf(param.getValue()));
            }
            Response = doHttp(httpGet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response;
    }

    /**
     * 发送 HTTP POST请求，不带请求参数和请求头
     *
     * @param url 请求地址
     * @return
     * @throws Exception
     */
    public static String doPost(String url) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        return doHttp(httpPost);
    }

    /**
     * 发送 HTTP POST请求，带请求参数，不带请求头
     *
     * @param url    请求地址
     * @param params 请求参数
     * @return
     * @throws Exception
     */
    public static String doPost(String url, Map<String, Object> params) throws Exception {
        // 转换请求参数
        List<NameValuePair> pairs = covertParamsToList(params);
        HttpPost httpPost = new HttpPost(url);
        // 设置请求参数
        httpPost.setEntity(new UrlEncodedFormEntity(pairs, StandardCharsets.UTF_8.name()));

        return doHttp(httpPost);
    }

    /**
     * 发送 HTTP POST请求，带请求参数和请求头
     *
     * @param url     地址
     * @param headers 请求头
     * @param params  参数
     * @return
     * @throws Exception
     */
    public static String doPost(String url, Map<String, Object> headers, Map<String, Object> params) {
        // 转换请求参数

        List<NameValuePair> pairs = covertParamsToList(params);
        HttpPost httpPost = new HttpPost(url);
        String Response = null;
        // 设置请求参数
        try {
            if (params != null) {
                httpPost.setEntity(new UrlEncodedFormEntity(pairs, StandardCharsets.UTF_8.name()));
            }

            // 设置请求头
            for (Map.Entry<String, Object> param : headers.entrySet()) {
                httpPost.addHeader(param.getKey(), String.valueOf(param.getValue()));
            }
            Response = doHttp(httpPost);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response;
    }

    /**
     * 发送 HTTP POST请求，请求参数是JSON格式，数据编码是UTF-8
     *
     * @param url   请求地址
     * @param param 请求参数
     * @return
     * @throws Exception
     */
    public static String doPostJson(String url, String param) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        // 设置请求头
        httpPost.addHeader("Content-Type", "application/json; charset=UTF-8");
        // 设置请求参数
        httpPost.setEntity(new StringEntity(param, StandardCharsets.UTF_8.name()));
        return doHttp(httpPost);
    }


    /**
     * 发送 HTTP POST请求，请求参数是XML格式，数据编码是UTF-8
     *
     * @param url   请求地址
     * @param param 请求参数
     * @return
     * @throws Exception
     */
    public static String doPostXml(String url, String param) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        // 设置请求头
        httpPost.addHeader("Content-Type", "application/xml; charset=UTF-8");
        // 设置请求参数
        httpPost.setEntity(new StringEntity(param, StandardCharsets.UTF_8.name()));

        return doHttp(httpPost);
    }

    /**
     * 发送 HTTPS POST请求，使用指定的证书文件及密码，不带请求头信息<
     *
     * @param url      请求地址
     * @param param    请求参数
     * @param path     证书全路径
     * @param password 证书密码
     * @return
     * @throws Exception
     * @throws Exception
     */
    public static String doHttpsPost(String url, String param, String path, String password) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        // 设置请求参数
        httpPost.setEntity(new StringEntity(param, StandardCharsets.UTF_8.name()));

        return doHttps(httpPost, path, password);
    }

    /**
     * 发送 HTTPS 请求,使用指定的证书文件及密码
     *
     * @param request
     * @param path     证书全路径
     * @param password 证书密码
     * @return
     * @throws Exception
     * @throws Exception
     */
    private static String doHttps(HttpRequestBase request, String path, String password) throws Exception {
        // 获取HTTPS SSL证书
        SSLConnectionSocketFactory csf = getHttpsFactory(path, password);
        // 通过连接池获取连接对象
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
        return doRequest(httpClient, request);
    }

    /**
     * 获取HTTPS SSL连接工厂,使用指定的证书文件及密码
     *
     * @param path     证书全路径
     * @param password 证书密码
     * @return
     * @throws Exception
     * @throws Exception
     */
    private static SSLConnectionSocketFactory getHttpsFactory(String path, String password) throws Exception {

        // 初始化证书，指定证书类型为“PKCS12”
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        // 读取指定路径的证书
        FileInputStream input = new FileInputStream(new File(path));
        try {
            // 装载读取到的证书，并指定证书密码
            keyStore.load(input, password.toCharArray());
        } finally {
            input.close();
        }
        // 获取HTTPS SSL证书连接上下文
        SSLContext sslContext = SSLContexts.custom().loadKeyMaterial(keyStore, password.toCharArray()).build();
        // 获取HTTPS连接工厂，指定TSL版本
        SSLConnectionSocketFactory sslCsf = new SSLConnectionSocketFactory(sslContext, new String[]{"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2"}, null, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
        return sslCsf;
    }

    /**
     * 转换请求参数，将Map键值对拼接成QueryString字符串
     *
     * @param params
     * @return
     */
    public static String covertMapToQueryStr(Map<String, Object> params) {
        List<NameValuePair> pairs = covertParamsToList(params);
        return URLEncodedUtils.format(pairs, StandardCharsets.UTF_8.name());
    }

    /*
         get请求，并用basic Auth认证
     */
    public static String doGet(String url, String username, String password, Map<String, Object> params, Map<String, Object> headers) {
        URIBuilder ub = null;
        JSONObject jsonObject=null;
        String str = "";
        HttpGet httpGet = null;
        try {
            ub = new URIBuilder(url);
            if (params != null) {
                // 转换请求参数
                List<NameValuePair> pairs = covertParamsToList(params);
                ub.setParameters(pairs);
            }
            httpGet = new HttpGet(ub.build());
            // 设置请求头

            if (headers != null) {
                for (Map.Entry<String, Object> param : headers.entrySet()) {
                    httpGet.addHeader(param.getKey(), String.valueOf(param.getValue()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 创建用户信息
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials
                = new UsernamePasswordCredentials(username, password);
        provider.setCredentials(AuthScope.ANY, credentials);

        // 创建客户端的时候进行身份验证
        HttpClient client = HttpClientBuilder.create()
                .setDefaultCredentialsProvider(provider)
                .build();

        HttpResponse response = null;
        try {
            response = client.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int statusCode = response.getStatusLine()
                .getStatusCode();
        if (statusCode >= 200 && statusCode < 300) {

                /**读取服务器返回过来的json字符串数据**/
            try {
                str = EntityUtils.toString(response.getEntity(),"UTF-8");
                // 关闭IO流
                EntityUtils.consume(response.getEntity());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return str;
    }


    /*
        post请求，并用basic Auth认证
    */
    public static String doPost(String url, String username, String password, Map<String, Object> params, Map<String, Object> headers) {
        // 转换请求参数

        String entity = null;
        String jsonPrarms = JSON.toJSONString(params);
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();   //获取浏览器信息
        HttpPost httpPost = new HttpPost(url);
        String encoding = null;  //username  password 自行修改  中间":"不可少
        try {
            encoding = DatatypeConverter.printBase64Binary((username+":"+password).getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        httpPost.setHeader("Authorization", "Basic " + encoding);
        // 设置请求头

        if (headers != null) {
            for (Map.Entry<String, Object> param : headers.entrySet()) {
                httpPost.addHeader(param.getKey(), String.valueOf(param.getValue()));
            }
        }
        HttpEntity entityParam = new StringEntity(jsonPrarms, ContentType.create("application/json", "UTF-8"));  //这里的“application/json” 可以更换因为本人是传的json参数所以用的这个
        httpPost.setEntity(entityParam);     //把参数添加到post请求
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response==null){
            return entity;
        }
        StatusLine statusLine = response.getStatusLine();   //获取请求对象中的响应行对象
        int responseCode = statusLine.getStatusCode();
        if ((responseCode >= 200 && responseCode < 300)) {
            //获取响应信息
            try {
                entity = "success";
                return entity;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return entity;
    }
    /*
       delete请求，并用basic Auth认证
   */
    public static String doDeleteForMqtt(String url, String username, String password) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();   //获取浏览器信息
        HttpDelete httpDelete = new HttpDelete(url);
        String encoding = null;  //username  password 自行修改  中间":"不可少
        try {
            encoding = DatatypeConverter.printBase64Binary((username+":"+password).getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        httpDelete.setHeader("Authorization", "Basic " + encoding);

        HttpResponse response = null;
        try {
            response = httpClient.execute(httpDelete);
        } catch (IOException e) {
            e.printStackTrace();
        }
        StatusLine statusLine = response.getStatusLine();   //获取请求对象中的响应行对象
        int responseCode = statusLine.getStatusCode();
        String entity = null;
        if ((responseCode >= 200 && responseCode < 300)) {
            //获取响应信息
            try {
                if(response.getEntity()==null){
                    entity="sucess";
                    return entity;
                }
                entity = EntityUtils.toString(response.getEntity(), "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return entity;
    }
}