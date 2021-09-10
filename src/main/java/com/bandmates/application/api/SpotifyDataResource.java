package com.bandmates.application.api;

import com.bandmates.application.domain.Profile;
import com.bandmates.application.domain.SpotifyData;
import com.bandmates.application.service.SpotifyDataService;
import com.bandmates.application.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.io.*;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SpotifyDataResource {
    private final SpotifyDataService spotifyDataService;

    private final UserService userService;

    @PostMapping("/spotifydata/initialize/{username}")
    public ResponseEntity<SpotifyData> initializeSpotifyData(@PathVariable String username, @PathParam("code") String code) throws IOException {
        log.info("Initializing spotify data");
        Map<String, String> tokenMap = spotifyDataService.getSpotifyTokensFromCode(code);
        Profile profile = userService.getUserProfile(username);
        SpotifyData spotifyData = spotifyDataService.saveSpotifyData(new SpotifyData(null, "", tokenMap.get("access_token"), tokenMap.get("refresh_token"), profile));
        profile.setSpotifyData(spotifyData);
        return ResponseEntity.ok(spotifyData);
    }

    @GetMapping("/spotifydata/fetch/{id}")
    public ResponseEntity<SpotifyData> fetchSpotifyData(@PathVariable Long id) {
        log.info("Fetching spotify data");
        return null;
    }
}
