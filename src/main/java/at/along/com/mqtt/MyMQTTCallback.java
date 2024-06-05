package at.along.com.mqtt;

import at.along.com.dao.EmqxMapper;
import at.along.com.entity.Emqx;
import at.along.com.entity.UploadData;
import at.along.com.service.impl.DataTemplateServiceImpl;
import at.along.com.service.impl.DeviceServiceImpl;
import at.along.com.service.impl.UploadDataServiceImpl;
import at.along.com.utils.HttpClientUtils;
import com.alibaba.fastjson.JSONObject;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.Date;

public class MyMQTTCallback implements MqttCallback {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyMQTTCallback.class);
    private MqttClient client;
    private MqttConnectOptions options;
    private String topic="$platform/data_bus/data_upload";
    private String topic1="$platform/client_status/client_connected";
    private String topic2="$platform/client_status/client_disconnected";
    public MyMQTTCallback(MqttClient client, MqttConnectOptions options) {
        this.client = client;

        this.options=options;
    }
    /**
     * 丢失连接，可在这里做重连
     * 只会调用一次
     *
     * @param throwable
     */
    @Override
    public void connectionLost(Throwable throwable) {
        LOGGER.error("mqtt connectionLost 连接断开，5S之后尝试重连: {}", throwable.getMessage());

        try {
            if (client.isConnected()) {
                client.reconnect();
                LOGGER.warn("尝试重新连接");
            }else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            client.connect(options);
                            LOGGER.warn("尝试建立新连接");
                            client.setCallback(new MyMQTTCallback(client,options));
                            client.subscribe(topic, 1);
                            client.subscribe(topic1, 1);
                            client.subscribe(topic2, 1);
                            LOGGER.info("重连成功");
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
//                LOGGER.warn("mqtt reconnect times = {} try again...", reconnectTimes++);

//                LOGGER.info("重连成功");
        } catch (MqttException e) {
            e.printStackTrace();
        }


    }

    /**
     * @param topic
     * @param mqttMessage
     * @throws Exception
     * subscribe后得到的消息会执行到这里面
     */
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        LOGGER.info("接收消息主题"+topic+"接收消息内容 : "+new String(mqttMessage.getPayload()));
        SubMsg.subMsg.Mqtts(topic,mqttMessage);
    }

    /**
     * 消息到达后
     * subscribe后，执行的回调函数
     *
     * @param s
     * @param mqttMessage
     * @throws Exception
     */
    /**
     * publish后，配送完成后回调的方法
     *
     * @param iMqttDeliveryToken
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        LOGGER.info("==========deliveryComplete={}==========", iMqttDeliveryToken.isComplete());
    }

    @Component
    public  static class SubMsg {

        private int i=0;
        private static SubMsg subMsg;
        private Emqx emqx;
        @Value("${mq.ip}")
        private String ip;
        @Autowired
        private UploadDataServiceImpl uploadDataService;
        @Autowired
        private DataTemplateServiceImpl dataTemplateService;
        @Autowired
        private DeviceServiceImpl deviceService;
        @Autowired
        private EmqxMapper emqxMapper;
        @PostConstruct
        public void init(){
            emqx=emqxMapper.selectOneByIdEmqx("1");
            subMsg = this;
            subMsg.deviceService=this.deviceService;
            subMsg.dataTemplateService=this.dataTemplateService;
            subMsg.uploadDataService=this.uploadDataService;

        }

        public  void Mqtts(String topic, MqttMessage mqttMessage){
            String str=new String(mqttMessage.getPayload());
            if(topic.contains("$platform")&&topic.contains("client_status")&&topic.contains("client_connected")){
                JSONObject map= JSONObject.parseObject(str);
                String deviceId=String.valueOf(map.get("deviceId"));
                String productId=String.valueOf(map.get("productId"));
                String realProductId=deviceService.getProductId(deviceId);
                if(!productId.equals(realProductId)){
                    subMsg.disconnectDevice(deviceId);
                    return;
                }
                subMsg.updateStatusUp(deviceId);
            }else if(topic.contains("$platform")&&topic.contains("client_status")&&topic.contains("client_disconnected")){
                String deviceId=str;
                Timestamp lastOnlineTime=new Timestamp(new Date().getTime());
                subMsg.updateStatusDown(deviceId,lastOnlineTime);
            }else if(topic.contains("$platform")&&topic.contains("data_bus")&&topic.contains("data_upload")){
                JSONObject map= JSONObject.parseObject(new String(mqttMessage.getPayload()));
                String productId=String.valueOf(map.get("username"));
                String deviceID=String.valueOf(map.get("clientid"));
                String data=String.valueOf(map.get("payload"));
                String[] dataN=String.valueOf(map.get("topic")).split("/");
                String dataName= dataN[2];
                String dataTemplateId= null;
                try {
                    dataTemplateId = subMsg.getId(productId,dataName);
                    if (dataTemplateId!=null){
                        Timestamp uploadDate=new Timestamp(new Date().getTime());
                        UploadData uploadData=new UploadData();
                        uploadData.setProductId(productId);
                        uploadData.setDeviceId(deviceID);
                        uploadData.setUploadDate(uploadDate);
                        uploadData.setData(data);
                        uploadData.setDataTemplateId(dataTemplateId);
                        try {
                            subMsg.UploadDataService(uploadData);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        public void UploadDataService(UploadData uploadData){
            subMsg.uploadDataService.insertAll(uploadData);
        }
        public void updateStatusUp(String deviceId){
            subMsg.deviceService.updateStatusUp(deviceId);
        }
        public void updateStatusDown(String deviceId,Timestamp lastOnlineTime){
            subMsg.deviceService.updateStatusDown(deviceId,lastOnlineTime);
        }
        public String getId(String productId,String dataName){
            return dataTemplateService.getDTid(productId,dataName);
        }
        public int disconnectDevice(String deviceId) {
            String url="http://"+ip+":18083/api/v5/clients/"+deviceId;
            HttpClientUtils.doDeleteForMqtt(url,emqx.getUsername(),emqx.getPassword());
            return 0;
        }
    }
}
