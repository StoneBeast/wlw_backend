package at.along.com.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

  private String productId; //产品id
  private String productName; // 产品名
  private String userId;//用户id
  private String productIndustry; //#产品类别
  private Date createDate;
  private Date updateDate;
  private String productType;
  private String productAbout;
  private String accessNetworks;
  private String accessAgreement;
}
