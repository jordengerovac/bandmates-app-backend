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

    @GetMapping("/profiles/{profileId}")
    public ResponseEntity<Profile> getProfile(@PathVariable Long profileId) {
        return ResponseEntity.ok(profileService.getProfile(profileId));
    }

    @PostMapping("/profiles/create")
    public ResponseEntity<Profile> createProfile(@RequestBody Profile profile) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/profile/create").toUriString());
        return ResponseEntity.created(uri).body(profileService.saveProfile(profile));
    }

    @PostMapping("/profiles/users/{username}")
    public ResponseEntity<Profile> createProfileForUser(@RequestBody Profile profile, @PathVariable String username) {
        Profile createdProfile = profileService.saveProfile(profile);
        profileService.addProfileToUser(username, createdProfile.getId());
        return ResponseEntity.ok().build();
    }
}
