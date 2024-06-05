package at.along.com.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Users {
    private String userId;
    private String userName;
    private String userPassword;
    private String userEmail;
    private Date createDate;
    private Date updateDate;
    private String avatarUrl;
}
