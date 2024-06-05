package at.along.com.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Command {
    private String commandId;
    private String productId;
    private String deviceId;
    private Timestamp createDate;
    private String commandText;
    private String commandStatus;
    private String response;
}
