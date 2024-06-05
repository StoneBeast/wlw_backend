package at.along.com.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;
@AllArgsConstructor
@Data
@NoArgsConstructor
public class UploadData {

    private String productId;
    private String deviceId;
    private Timestamp uploadDate;
    private String data;
    private int messageId;
    private String dataTemplateId;
}
