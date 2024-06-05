package at.along.com.mqtt;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class Mqttclient {

    private static final Logger log = LoggerFactory.getLogger(Mqttclient.class);
    @Autowired
    private MyMQTTClient myMQTTClient;
    @Value("${mqtt.topic}")
    private String topic;
    @Value("${mqtt.topic1}")
    private String topic1;
    @Value("${mqtt.topic2}")
    private String topic2;
    @Bean
    public  MyMQTTClient MQTTClient() {
        for (int i = 0; i < 10; i++) {
            try {
                myMQTTClient.connect();
                //不同的主题
                myMQTTClient.subscribe(topic, 1);
                myMQTTClient.subscribe(topic1, 1);
                myMQTTClient.subscribe(topic2, 1);
//                myMQTTClient.subscribe(topic4, 1);
                return myMQTTClient;
            } catch (MqttException e) {
                log.error("MQTT connect exception,connect time = " + i);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return myMQTTClient;
    }
}
