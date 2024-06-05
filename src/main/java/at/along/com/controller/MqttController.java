package at.along.com.controller;

import at.along.com.mqtt.MyMQTTClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

@RestController
@RequestMapping("/api/mqtt")
@CrossOrigin(origins = "*")
public class MqttController {

    @Autowired
    private MyMQTTClient myMQTTClient;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private String topic="$platform/command/123/client01";




    Queue<String> msgQueue = new LinkedList<>();
    int count = 1;

    @PostMapping("/getMsg")
    public synchronized void mqttMsg(@RequestBody String  mqttMsg) {


        //map转json
        String sendMsg=mqttMsg;
        //队列添加元素
        boolean flag = msgQueue.offer(sendMsg);
        if (flag) {

                //发布消息  topic2 是你要发送到那个通道里面的主题 比如我要发送到topic2主题消息
                myMQTTClient.publish(msgQueue.poll(), topic);


        }
    }
}
