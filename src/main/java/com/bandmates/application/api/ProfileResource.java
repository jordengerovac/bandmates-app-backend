package com.bandmates.application.api;

import com.bandmates.application.domain.Profile;
import com.bandmates.application.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProfileResource {
    private final ProfileService profileService;

    @GetMapping("/profiles")
    public ResponseEntity<List<Profile>> getAllProfiles() {
        return ResponseEntity.ok(profileService.getAllProfiles());
    }

    @GetMapping("/profile/{profileId}")
    public ResponseEntity<Profile> getProfile(@PathVariable Long profileId) {
        return ResponseEntity.ok(profileService.getProfile(profileId));
    }

    @PostMapping("/profile/create")
    public ResponseEntity<Profile> createUser(@RequestBody Profile profile) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/profile/create").toUriString());
        return ResponseEntity.created(uri).body(profileService.saveProfile(profile));
    }

    @PostMapping("/profile/{profileId}/add-to-user/{username}")
    public ResponseEntity<Profile> addProfileToUser(@PathVariable Long profileId, @PathVariable String username) {
        profileService.addProfileToUser(username, profileId);
        return ResponseEntity.ok().build();
    }
}
