package at.along.com.service;


import at.along.com.entity.Users;

import java.util.List;
import java.util.Map;

public interface UserService {
    public List<Users>  select();
    public int add(Users users);
    public Map<String,Object> log(Users users);
    public Users selectByEmail(String email);
    public Users selectByuserId(String userId);
}
