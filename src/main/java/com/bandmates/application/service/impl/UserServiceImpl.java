package com.bandmates.application.service.impl;

import com.bandmates.application.domain.AppUser;
import com.bandmates.application.domain.Profile;
import com.bandmates.application.domain.Role;
import com.bandmates.application.repository.RoleRepository;
import com.bandmates.application.repository.UserRepository;
import com.bandmates.application.service.UserService;
import com.bandmates.application.util.MailSenderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final MailSenderUtil mailSenderUtil;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepository.findByUsername(username);
        if (user == null) {
            log.error("User not found");
            throw new UsernameNotFoundException("User not found");
        }
        else {
            log.info("User found: {}", username);
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

    @Override
    public AppUser saveUser(AppUser user) {
        log.info("Adding new user {} to database", user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
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

    @Override
    public List<Role> getAllRoles() {
        log.info("Fetching all roles from database");
        return roleRepository.findAll();
    }

    @Override
    public List<AppUser> searchUsers(Specification<AppUser> specification) {
        log.info("Searching users in database");
        return userRepository.findAll(Specification.where(specification));
    }

    @Override
    public Profile getUserProfile(String username) {
        AppUser user = userRepository.findByUsername(username);
        return user.getProfile();
    }

    @Override
    public AppUser updateUser(AppUser user, Long id) {
        log.info("Updating user {}", user.getUsername());

        Optional<AppUser> oldUser = userRepository.findById(id);
        if (oldUser.isPresent()) {
            if (user.getUsername() != null)
                oldUser.get().setUsername(user.getUsername());
            if (user.getFirstname() != null)
                oldUser.get().setFirstname(user.getFirstname());
            if (user.getLastname() != null)
                oldUser.get().setLastname(user.getLastname());
            if (user.getPassword() != null)
                oldUser.get().setPassword(user.getPassword());
            if (user.getPassword() != null)
                oldUser.get().setProfile(user.getProfile());
            if (!user.getRoles().isEmpty()) {
                oldUser.get().setRoles(user.getRoles());
            }
            if (user.getBotb() != null)
                oldUser.get().setBotb(user.getBotb());
            if (user.getEmailRegistrationToken() != null)
                oldUser.get().setEmailRegistrationToken(user.getEmailRegistrationToken());
            if (user.getUserEnabled() != null)
                oldUser.get().setUserEnabled(user.getUserEnabled());

            return userRepository.save(oldUser.get());
        }
        return null;
    }

    @Override
    public AppUser confirmUserRegistration(String userConfirmationToken) {
        AppUser user = userRepository.findByEmailRegistrationToken(userConfirmationToken);
        if (user != null) {
            user.setUserEnabled(true);
            return userRepository.save(user);
        }
        return null;
    }

    @Override
    public void sendConfirmationEmail(AppUser user) {
        mailSenderUtil.sendConfirmationEmail(user);
    }
}
