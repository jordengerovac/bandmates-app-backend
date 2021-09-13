package com.bandmates.application.api;

import com.bandmates.application.domain.Profile;
import com.bandmates.application.domain.SpotifyData;
import com.bandmates.application.service.ProfileService;
import com.bandmates.application.service.SpotifyDataService;
import com.bandmates.application.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SpotifyDataResource {
    private final SpotifyDataService spotifyDataService;

    private final UserService userService;

    private final ProfileService profileService;

    @PostMapping("/spotifydata/initialize/{username}")
    public ResponseEntity<SpotifyData> initializeSpotifyData(@PathVariable String username, @PathParam("code") String code) throws IOException {
        log.info("Initializing spotify data");
        Map<String, String> tokenMap = spotifyDataService.getSpotifyTokensFromCode(code);
        Profile profile = userService.getUserProfile(username);
        SpotifyData spotifyData = spotifyDataService.saveSpotifyData(new SpotifyData(null, "", new HashSet<>(), new HashSet<>(), new HashSet<>(),tokenMap.get("access_token"), tokenMap.get("refresh_token"), profile));
        profile.setSpotifyData(spotifyData);
        profileService.saveProfile(profile);
        return ResponseEntity.ok(spotifyData);
    }

    @GetMapping("/spotifydata/fetch/{username}")
    public ResponseEntity<SpotifyData> fetchUpdatedSpotifyData(@PathVariable String username) {
        log.info("Fetching spotify data");
        return ResponseEntity.ok(spotifyDataService.fetchUpdatedSpotifyData(username));
    }

    @GetMapping("/spotifydata")
    public ResponseEntity<List<SpotifyData>> getAllSpotifyData() {
        log.info("Fetching all spotify data");
        return ResponseEntity.ok(spotifyDataService.getAllSpotifyData());
    }

    @PostMapping("/spotifydata/{id}/token/refresh")
    public ResponseEntity<SpotifyData> getSpotifyRefreshToken(@PathVariable Long id) {
        log.info("Fetching spotify refresh token");
        return ResponseEntity.ok(spotifyDataService.getSpotifyRefreshToken(id));
    }
}
