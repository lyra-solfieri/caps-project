package com.lyra.caps.service;

import java.util.List;

import com.lyra.caps.model.User;

public interface IUserService {

    User createUser(User user);

    User getUserById(Long userId);
    
    List<User> getAllUsers();

    User updateUser(User user);

    void deleteUser(Long userId);

}
