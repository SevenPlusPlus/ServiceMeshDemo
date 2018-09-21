package com.geely.mesh.demo.userservice.service.impl;

import com.geely.mesh.demo.userservice.domain.User;
import com.geely.mesh.demo.userservice.exception.UserNotFoundException;
import com.geely.mesh.demo.userservice.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserServiceImpl implements UserService {

    static Map<Long, User> users = new ConcurrentHashMap<Long, User>();
    static AtomicLong userIdCounter = new AtomicLong(0);

    @Override
    public Long saveUser(User user) {
        Long userId = userIdCounter.addAndGet(1);
        user.setUserId(userId);
        users.put(userId, user);
        return userId;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Long userId) {
        return users.get(userId);
    }

    @Override
    public void updateUser(User user) {
        if(!users.containsKey(user.getUserId())) {
            throw new UserNotFoundException(user.getUserId());
        }
        users.put(user.getUserId(), user);
    }

    @Override
    public Boolean deleteUserById(Long userId) {
        return users.remove(userId) != null;
    }

    @Override
    public Long loginVerify(String loginName, String passwd) {
        for(User user : users.values())
        {
            if(user.getLoginName().equals(loginName) &&
                    user.getPasswd().equals(passwd)){
                return user.getUserId();
            }
        }
        return -1l;
    }
}
