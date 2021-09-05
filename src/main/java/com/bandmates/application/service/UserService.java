package com.bandmates.application.service;

import com.bandmates.application.domain.AppUser;
import com.bandmates.application.domain.Role;

import java.util.List;

public interface UserService {
    AppUser saveUser(AppUser user);

    Role saveRole(Role role);

    void addRoleToUser(String username, String roleName);

    AppUser getUser(String username);

    List<AppUser> getAllUsers();
}
