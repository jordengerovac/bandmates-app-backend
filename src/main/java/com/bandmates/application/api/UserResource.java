package com.bandmates.application.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bandmates.application.domain.AppUser;
import com.bandmates.application.domain.Profile;
import com.bandmates.application.domain.Role;
import com.bandmates.application.service.UserService;
import com.bandmates.application.util.MailSenderUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sipios.springsearch.anotation.SearchSpec;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserResource {
    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<AppUser>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(userService.getAllRoles());
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<AppUser> getUser(@PathVariable  String username) {
        return ResponseEntity.ok(userService.getUser(username));
    }

    @GetMapping("/users/{username}/profiles")
    public ResponseEntity<Profile> getUserProfile(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserProfile(username));
    }

    @PostMapping("/users/create")
    public ResponseEntity<AppUser> createUser(@RequestBody AppUser user) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/user/create").toUriString());
        AppUser existingUser = userService.getUser(user.getUsername());
        if (existingUser != null) {
            return ResponseEntity.badRequest().body(null);
        }
        AppUser savedUser = userService.saveUser(user);
        userService.addRoleToUser(savedUser.getUsername(), "ROLE_USER");
        return ResponseEntity.created(uri).body(savedUser);
    }

    @PostMapping("/users/register")
    public ResponseEntity<AppUser> registerUser(@RequestBody AppUser user) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/user/register").toUriString());
        AppUser existingUser = userService.getUser(user.getUsername());
        if (existingUser != null) {
            return ResponseEntity.badRequest().body(null);
        }
        AppUser savedUser = userService.saveUser(user);
        userService.addRoleToUser(savedUser.getUsername(), "ROLE_USER");

        // send email with confirmation link
        userService.sendConfirmationEmail(savedUser);

        return ResponseEntity.created(uri).body(savedUser);
    }

    @PutMapping("/users/update/{id}")
    public ResponseEntity<AppUser> updateUser(@RequestBody AppUser user, @PathVariable Long id) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/user/update").toUriString());
        return ResponseEntity.created(uri).body(userService.updateUser(user, id));
    }

    @PostMapping("/roles/create")
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/create-role").toUriString());
        return ResponseEntity.created(uri).body(userService.saveRole(role));
    }

    @PostMapping("/roles/add-to-user")
    public ResponseEntity<?> addRoleToUser(@RequestParam String username, @RequestParam String roleName) {
        userService.addRoleToUser(username, roleName);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/query")
    public ResponseEntity<List<AppUser>> searchForUsers(@SearchSpec Specification<AppUser> specs) {
        return ResponseEntity.ok(userService.searchUsers(Specification.where(specs)));
    }

    @GetMapping("/users/confirm/{emailRegistrationToken}")
    public ResponseEntity<AppUser> confirmUserRegistration(@PathVariable String emailRegistrationToken) {
        return ResponseEntity.ok(userService.confirmUserRegistration(emailRegistrationToken));
    }

    @GetMapping("/token/refresh")
    public void getRefreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refresh_token = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("jwt_secret_bandmates".getBytes());
                JWTVerifier jwtVerifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = jwtVerifier.verify(refresh_token);
                String username = decodedJWT.getSubject();
                AppUser user = userService.getUser(username);
                if (!user.getUserEnabled()) {
                    throw new Exception("User has not been confirmed through email token");
                }
                String access_token = JWT.create().withSubject(user.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", user.getRoles()
                                .stream().map(Role::getName).collect(Collectors.toList()))
                        .sign(algorithm);
                Map<String, String> tokens = new HashMap<>();
                tokens.put("username", username);
                tokens.put("access_token", access_token);
                tokens.put("refresh_token", refresh_token);
                tokens.put("expires_in", String.valueOf(10 * 60 * 1000));
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            } catch (Exception exception) {
                log.error("An error occurred during authorization: {}", exception.getMessage());
                response.setHeader("error", exception.getMessage());
                response.setStatus(FORBIDDEN.value());
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("error_message", exception.getMessage());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), errorMap);
            }
        }
        else {
            throw new RuntimeException("Refresh token is missing");
        }
    }

    @Data
    class RoleToUserForm {
        private String username;
        private String roleName;
    }
}
