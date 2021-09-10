package com.bandmates.application.service.impl;

import com.bandmates.application.domain.Profile;
import com.bandmates.application.domain.SpotifyData;
import com.bandmates.application.repository.ProfileRepository;
import com.bandmates.application.repository.SpotifyDataRepository;
import com.bandmates.application.service.SpotifyDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SpotifyDataImpl implements SpotifyDataService {
    private final SpotifyDataRepository spotifyDataRepository;

    private final ProfileRepository profileRepository;

    @Value("${spotify.tokenUrl}")
    private String spotifyTokenUrl;

    @Value("${spotify.clientId}")
    private String spotifyClientId;

    @Value("${spotify.clientSecret}")
    private String spotifyClientSecret;

    @Override
    public SpotifyData saveSpotifyData(SpotifyData spotifyData) {
        log.info("Adding new spotify data {} to database", spotifyData.getId());
        return spotifyDataRepository.save(spotifyData);
    }

    @Override
    public void addSpotifyDataToProfile(Long spotifyDataId, Long profileId) {
        log.info("Adding spotify data {} to profile {}", spotifyDataId, profileId);
        Optional<Profile> profile = profileRepository.findById(profileId);
        Optional<SpotifyData> spotifyData = spotifyDataRepository.findById(spotifyDataId);
        if (profile.isPresent() && spotifyData.isPresent()) {
            profile.get().setSpotifyData(spotifyData.get());
        }
    }

    @Override
    public SpotifyData getSpotifyData(Long spotifyDataId) {
        return spotifyDataRepository.findById(spotifyDataId).get();
    }

    @Override
    public List<SpotifyData> getAllSpotifyData() {
        return spotifyDataRepository.findAll();
    }

    @Override
    public SpotifyData updateSpotifyData(SpotifyData spotifyData, Long id) {
        Optional<SpotifyData> oldSpotifyData = spotifyDataRepository.findById(id);
        if (oldSpotifyData.isPresent()) {
            if (spotifyData.getProfile() != null)
                oldSpotifyData.get().setProfile(spotifyData.getProfile());
            if (spotifyData.getTopGenre() != null)
                oldSpotifyData.get().setTopGenre(spotifyData.getTopGenre());

            return spotifyDataRepository.save(oldSpotifyData.get());
        }
        return null;
    }

    @Override
    public Map<String, String> getSpotifyTokensFromCode(String code) {
        try {
            // connection
            URL url = new URL(spotifyTokenUrl);
            String urlParams = "code=" + code + "&grant_type=authorization_code&redirect_uri=http://localhost:3000/connect-spotify";
            byte[] urlData = urlParams.getBytes(StandardCharsets.UTF_8);
            int urlDataLength = urlData.length;
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setInstanceFollowRedirects(false);

            // authentication
            String auth = spotifyClientId + ":" + spotifyClientId;
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
            String authHeader = "Basic " + new String(encodedAuth);
            con.setRequestProperty("Authorization", authHeader);

            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("charset", "utf-8");
            con.setRequestProperty("Content-Length", Integer.toString(urlDataLength));
            con.setUseCaches(false);
            try (DataOutputStream dataOutputStream = new DataOutputStream(con.getOutputStream())) {
                dataOutputStream.write(urlData);
            }

            // reading response
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            JSONObject jsonObject = new JSONObject(content.toString());
            Map<String, String> tokenMap = new HashMap<>();
            for(String key : jsonObject.keySet()) {
                tokenMap.put(key, String.valueOf(jsonObject.get(key)));
            }

            return tokenMap;
        } catch(IOException exception) {
            log.error(exception.getMessage());
            return null;
        }
    }

    public SpotifyData getUpdatedSpotifyData(Long id) {
        return null;
    }

}
