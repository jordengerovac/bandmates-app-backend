package com.bandmates.application.service.impl;

import com.bandmates.application.domain.AppUser;
import com.bandmates.application.domain.Role;
import com.bandmates.application.repository.RoleRepository;
import com.bandmates.application.repository.UserRepository;
import com.bandmates.application.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    @Override
    public AppUser saveUser(AppUser user) {
        log.info("Adding new user {} to database", user.getUsername());
        return userRepository.save(user);
    }

    @Override
    public Role saveRole(Role role) {
        log.info("Adding new role {} to database", role.getName());
        return roleRepository.save(role);
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        log.info("Adding role {} to user {}", roleName, username);
        AppUser user = userRepository.findByUsername(username);
        Role role = roleRepository.findByName(roleName);
        user.getRoles().add(role);
    }

    @Override
    public AppUser getUser(String username) {
        log.info("Fetching user {} from database", username);
        return userRepository.findByUsername(username);
    }

    @Override
    public List<AppUser> getAllUsers() {
        log.info("Fetching all users from database");
        return userRepository.findAll();
    }
}
