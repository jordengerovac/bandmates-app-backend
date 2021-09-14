package com.bandmates.application.service;

import com.bandmates.application.domain.AppUser;
import com.bandmates.application.domain.Profile;
import com.bandmates.application.domain.Role;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface UserService {
    AppUser saveUser(AppUser user);

    Role saveRole(Role role);

    void addRoleToUser(String username, String roleName);

    AppUser getUser(String username);

    List<AppUser> getAllUsers();

    List<Role> getAllRoles();

    List<AppUser> searchUsers(Specification<AppUser> specification);

    Profile getUserProfile(String username);

    AppUser updateUser(AppUser user, Long id);
}
