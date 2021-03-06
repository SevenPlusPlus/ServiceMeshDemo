package com.geely.mesh.demo.userservice.service;

import com.geely.mesh.demo.userservice.domain.User;

import java.util.List;

public interface UserService {
    Long saveUser(User user);
    List<User> getAllUsers();
    User getUserById(Long userId);
    void updateUser(User user);
    Boolean deleteUserById(Long userId);
    Long loginVerify(String loginName, String passwd);
    Long payment(Long userId, Long amount);
    Long deposit(Long userId, Long amount);
}
