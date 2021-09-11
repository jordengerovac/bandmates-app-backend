package com.bandmates.application.service.impl;

import com.bandmates.application.domain.Profile;
import com.bandmates.application.domain.SpotifyData;
import com.bandmates.application.domain.Track;
import com.bandmates.application.repository.ProfileRepository;
import com.bandmates.application.repository.SpotifyDataRepository;
import com.bandmates.application.repository.TrackRepository;
import com.bandmates.application.service.SpotifyDataService;
import com.bandmates.application.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
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

    private final TrackRepository trackRepository;

    private final UserService userService;

    @Value("${spotify.clientId}")
    private String spotifyClientId;

    @Value("${spotify.clientSecret}")
    private String spotifyClientSecret;

    @Value("${spotify.tokenUrl}")
    private String spotifyTokenUrl;

    @Value("${spotify.recentlyPlayedUrl}")
    private String spotifyRecentlyPlayedUrl;

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
            String auth = spotifyClientId + ":" + spotifyClientSecret;
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

    @Override
    public SpotifyData getSpotifyRefreshToken(Long id) {
        Optional<SpotifyData> spotifyData = spotifyDataRepository.findById(id);
        try {
            // connection
            URL url = new URL(spotifyTokenUrl);
            String urlParams = "grant_type=refresh_token&refresh_token=" + spotifyData.get().getSpotifyRefreshToken();
            byte[] urlData = urlParams.getBytes(StandardCharsets.UTF_8);
            int urlDataLength = urlData.length;
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setInstanceFollowRedirects(false);

            // authentication
            String auth = spotifyClientId + ":" + spotifyClientSecret;
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

            spotifyData.get().setSpotifyAccessToken(tokenMap.get("access_token"));
            return spotifyDataRepository.save(spotifyData.get());

        } catch(IOException exception) {
            log.error(exception.getMessage());
            return null;
        }
    }

    @Override
    public SpotifyData fetchUpdatedSpotifyData(String username) {
        try {
            // connection
            URL url = new URL(spotifyRecentlyPlayedUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.setInstanceFollowRedirects(false);

            // authentication
            Profile profile = userService.getUserProfile(username);
            SpotifyData spotifyData = profile.getSpotifyData();
            getSpotifyRefreshToken(spotifyData.getId());
            String auth = spotifyData.getSpotifyAccessToken();
            String authHeader = "Bearer " + auth;
            con.setRequestProperty("Authorization", authHeader);

            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("charset", "utf-8");
            con.setUseCaches(false);

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
            JSONArray items = new JSONArray();

            Map<String, String> tokenMap = new HashMap<>();
            for(String key : jsonObject.keySet()) {
                tokenMap.put(key, String.valueOf(jsonObject.get(key)));
                if (key.equals("items")) {
                    items = jsonObject.getJSONArray(key);
                }
            }

            spotifyData.setTopGenre(content.toString().substring(0, 50));
            spotifyData.setRecentTracks(addTracksToSpotifyData(items));
            spotifyDataRepository.save(spotifyData);

            return spotifyData;
        } catch(IOException exception) {
            log.error(exception.getMessage());
            return null;
        }
    }

    public Set<Track> addTracksToSpotifyData(JSONArray spotifyDataJSON) {
        log.info("getting tracks");
        JSONObject jsonObject = null;
        JSONObject temp = null;
        Set<Track> trackSet = new HashSet<>();
        Track track = new Track();

        for(int i = 0; i < spotifyDataJSON.length(); i++) {
            track = new Track();
            jsonObject = spotifyDataJSON.getJSONObject(i);
            track.setSongName(jsonObject.getJSONObject("track").getJSONObject("album").get("name").toString());
            track.setUri((String) jsonObject.getJSONObject("track").getString("uri"));

            temp = (JSONObject) jsonObject.getJSONObject("track").getJSONObject("album").getJSONArray("artists").get(0);
            track.setArtist((String) temp.get("name"));

            temp = (JSONObject) jsonObject.getJSONObject("track").getJSONObject("album").getJSONArray("images").get(0);
            track.setArtwork((String) temp.get("url"));

            trackRepository.save(track);
            trackSet.add(track);
        }

        return trackSet;
    }

}
