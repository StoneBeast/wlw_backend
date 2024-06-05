package at.along.com.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Device {
    private String deviceId;//设备Id
    private String deviceName;//设备名称
    private String productId;//所属项目Id
    private Timestamp lastOnlineTime;//最后在线时间
    private String deviceAuthentication;//产品鉴权信息
    private String deviceIntroduction;//设备描述
    private String deviceApi;//向用户提供API接口
    private Date createDate;
    private String deviceStatus;
    private String apiKey;
}
