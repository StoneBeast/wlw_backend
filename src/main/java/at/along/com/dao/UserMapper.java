package at.along.com.dao;


import at.along.com.entity.Users;

import java.util.List;

public interface UserMapper {
    public List<Users> Query();
    public Users selectByUserName(String username);
    public Users selectByEmailAndPassword(String email,String password);
    public int insertAll(Users users);
    public Users selectOneByUserEmail(String email);
    public Users selectOneByUserId(String userId);
    public int deleteByUserId(String userId);
}
