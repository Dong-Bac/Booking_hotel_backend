package com.demo.service;

import com.demo.model.User;
import com.demo.payloads.UserDto;

import java.util.List;

public interface UserService {

    User registerUser(User user);
    List<User> getUsers();
    void deleteUser(String email);
    User getUser(String email);

}
