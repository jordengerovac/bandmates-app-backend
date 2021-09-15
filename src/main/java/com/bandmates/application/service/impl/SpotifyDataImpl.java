package com.bandmates.application.service.impl;

import com.bandmates.application.domain.*;
import com.bandmates.application.repository.ArtistRepository;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SpotifyDataImpl implements SpotifyDataService {
    private final SpotifyDataRepository spotifyDataRepository;

    private final ProfileRepository profileRepository;

    private final TrackRepository trackRepository;

    private final ArtistRepository artistRepository;

    private final UserService userService;

    @Value("${spotify.clientId}")
    private String spotifyClientId;

    @Value("${spotify.clientSecret}")
    private String spotifyClientSecret;

    @Value("${spotify.tokenUrl}")
    private String spotifyTokenUrl;

    @Value("${spotify.recentlyPlayedUrl}")
    private String spotifyRecentlyPlayedUrl;

    @Value("${spotify.topArtistsUrl}")
    private String spotifyTopArtistsUrl;

    @Value("${spotify.topTracksUrl}")
    private String spotifyTopTracksUrl;

    @Value("${spotify.recommendationsUrl}")
    private String spotifyRecommendationsUrl;

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
            if (spotifyData.getTopGenres() != null)
                oldSpotifyData.get().setTopGenres(spotifyData.getTopGenres());

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
        AppUser user = userService.getUser(username);
        Profile profile = userService.getUserProfile(username);
        List<Role> roles = userService.getAllRoles();
        SpotifyData spotifyData = profile.getSpotifyData();
        getSpotifyRefreshToken(spotifyData.getId());


        // setting recent tracks and deleting old ones
        String recentTracks = getRecentTracksFromSpotifyApi(spotifyData);
        JSONObject jsonObject = new JSONObject(recentTracks);
        JSONArray items = new JSONArray();
        Map<String, String> tokenMap = new HashMap<>();
        for(String key : jsonObject.keySet()) {
            tokenMap.put(key, String.valueOf(jsonObject.get(key)));
            if (key.equals("items")) {
                items = jsonObject.getJSONArray(key);
            }
        }
        for (Track t : spotifyData.getRecentTracks()) {
            trackRepository.delete(t);
        }
        Set<Track> recentTracksSet = addRecentTracksToSpotifyData(items);
        spotifyData.setRecentTracks(recentTracksSet);


        // setting top tracks and deleting old ones
        String topTracks = getTopTracksFromSpotifyApi(spotifyData);
        jsonObject = new JSONObject(topTracks);
        items = new JSONArray();

        tokenMap = new HashMap<>();
        for(String key : jsonObject.keySet()) {
            tokenMap.put(key, String.valueOf(jsonObject.get(key)));
            if (key.equals("items")) {
                items = jsonObject.getJSONArray(key);
            }
        }
        for (Track t : spotifyData.getTopTracks()) {
            trackRepository.delete(t);
        }
        Set<Track> topTracksSet = addTopTracksToSpotifyData(items);
        spotifyData.setTopTracks(topTracksSet);


        // setting top artists and deleting old ones
        String topArtists = getTopArtistsFromSpotifyApi(spotifyData);
        jsonObject = new JSONObject(topArtists);
        items = new JSONArray();
        tokenMap = new HashMap<>();
        for(String key : jsonObject.keySet()) {
            tokenMap.put(key, String.valueOf(jsonObject.get(key)));
            if (key.equals("items")) {
                items = jsonObject.getJSONArray(key);
            }
        }
        for (Artist a : spotifyData.getTopArtists()) {
            artistRepository.delete(a);
        }
        Set<Artist> topArtistsSet = addTopArtistsToSpotifyData(items);
        spotifyData.setTopArtists(topArtistsSet);


        // setting genres
        Set<String> genresSet = addTopGenresToSpotifyData(topArtistsSet);
        spotifyData.setTopGenres(genresSet);


        // setting recommendations
        String recommendedTracks = getRecommendationsFromSpotifyApi(spotifyData, topTracksSet, topArtistsSet, genresSet);
        jsonObject = new JSONObject(recommendedTracks);
        items = new JSONArray();

        tokenMap = new HashMap<>();
        for(String key : jsonObject.keySet()) {
            tokenMap.put(key, String.valueOf(jsonObject.get(key)));
            if (key.equals("tracks")) {
                items = jsonObject.getJSONArray(key);
            }
        }
        for (Track t : spotifyData.getRecommendedTracks()) {
            trackRepository.delete(t);
        }
        spotifyData.setRecommendedTracks(addTopTracksToSpotifyData(items));


        return spotifyDataRepository.save(spotifyData);
    }

    public String getRecentTracksFromSpotifyApi(SpotifyData spotifyData) {
        try {
            // connection
            URL url = new URL(spotifyRecentlyPlayedUrl + "?limit=10");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.setInstanceFollowRedirects(false);

            // authentication
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

            return content.toString();
        } catch(IOException exception) {
            log.error(exception.getMessage());
            return null;
        }
    }

    public String getTopArtistsFromSpotifyApi(SpotifyData spotifyData) {
        try {
            // connection
            URL url = new URL(spotifyTopArtistsUrl + "?limit=5");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.setInstanceFollowRedirects(false);

            // authentication
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

            return content.toString();
        } catch(IOException exception) {
            log.error(exception.getMessage());
            return null;
        }
    }

    public String getRecommendationsFromSpotifyApi(SpotifyData spotifyData, Set<Track> trackSeeds,
                                                   Set<Artist> artistSeeds, Set<String> genreSeeds) {
        // building url params
        String urlWithSeeds = spotifyRecommendationsUrl + "?seed_artists=";
        int size = artistSeeds.size();
        int randomItem = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this
        int i = 0;
        for(Artist artist : artistSeeds)
        {
            if (i == randomItem)
                urlWithSeeds += artist.getSeedId();
            i++;
        }

        urlWithSeeds += "&seed_tracks=";
        size = trackSeeds.size();
        randomItem = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this
        i = 0;
        for(Track track : trackSeeds)
        {
            if (i == randomItem)
                urlWithSeeds += track.getSeedId();
            i++;
        }
        urlWithSeeds += "&seed_genres=";
        size = genreSeeds.size();
        randomItem = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this
        i = 0;
        for(String genre : genreSeeds)
        {
            if (i == randomItem)
                urlWithSeeds += genre;
            i++;
        }
        urlWithSeeds += "&limit=10";

        try {
            // connection
            URL url = new URL(urlWithSeeds);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.setInstanceFollowRedirects(false);

            // authentication
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

            return content.toString();
        } catch(IOException exception) {
            log.error(exception.getMessage());
            return null;
        }
    }

    public String getTopTracksFromSpotifyApi(SpotifyData spotifyData) {
        try {
            // connection
            URL url = new URL(spotifyTopTracksUrl + "?limit=5");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.setInstanceFollowRedirects(false);

            // authentication
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

            return content.toString();
        } catch(IOException exception) {
            log.error(exception.getMessage());
            return null;
        }
    }

    public Set<Track> addRecentTracksToSpotifyData(JSONArray spotifyDataJSON) {
        log.info("getting recently played tracks");
        JSONObject jsonObject = null;
        JSONObject temp = null;
        Set<Track> trackSet = new HashSet<>();
        Track track = null;

        for(int i = 0; i < spotifyDataJSON.length(); i++) {
            jsonObject = spotifyDataJSON.getJSONObject(i);
            String songName = jsonObject.getJSONObject("track").get("name").toString();
            String uri = jsonObject.getJSONObject("track").get("uri").toString();
            String seedId = jsonObject.getJSONObject("track").get("id").toString();

            temp = (JSONObject) jsonObject.getJSONObject("track").getJSONObject("album").getJSONArray("artists").get(0);
            String name = temp.get("name").toString();

            temp = (JSONObject) jsonObject.getJSONObject("track").getJSONObject("album").getJSONArray("images").get(0);
            String artwork = temp.get("url").toString();

            trackSet.add(new Track(null, name, songName, uri, artwork, seedId, 0));
        }
        trackRepository.saveAll(trackSet);
        return trackSet;
    }

    public Set<Track> addTopTracksToSpotifyData(JSONArray spotifyDataJSON) {
        log.info("getting top tracks");
        JSONObject jsonObject = null;
        JSONObject temp = null;
        Set<Track> trackSet = new HashSet<>();
        Track track = new Track();

        for(int i = 0; i < spotifyDataJSON.length(); i++) {
            track = new Track();
            jsonObject = spotifyDataJSON.getJSONObject(i);
            track.setSongName(jsonObject.get("name").toString());
            track.setUri(jsonObject.get("uri").toString());
            track.setSeedId(jsonObject.get(("id")).toString());

            temp = (JSONObject) jsonObject.getJSONObject("album").getJSONArray("artists").get(0);
            track.setArtist(temp.get("name").toString());

            temp = (JSONObject) jsonObject.getJSONObject("album").getJSONArray("images").get(0);
            track.setArtwork(temp.get("url").toString());

            trackRepository.save(track);
            trackSet.add(track);
        }

        return trackSet;
    }

    public Set<Artist> addTopArtistsToSpotifyData(JSONArray spotifyDataJSON) {
        log.info("getting top artists");
        JSONObject jsonObject = null;
        JSONObject temp = null;
        Set<Artist> artistSet = new HashSet<>();
        Artist artist = new Artist();

        for(int i = 0; i < spotifyDataJSON.length(); i++) {
            artist = new Artist();
            jsonObject = spotifyDataJSON.getJSONObject(i);
            artist.setName(jsonObject.get("name").toString());
            artist.setUri(jsonObject.get("uri").toString());
            artist.setSeedId(jsonObject.get(("id")).toString());

            Set<String> genreSet = new HashSet<>();
            for (int j = 0; j < jsonObject.getJSONArray("genres").length(); j++) {
                genreSet.add(jsonObject.getJSONArray("genres").get(j).toString());
            }
            artist.setGenres(genreSet);

            temp = (JSONObject) jsonObject.getJSONArray("images").get(0);
            artist.setImageUrl(temp.get("url").toString());

            artistRepository.save(artist);
            artistSet.add(artist);
        }

        return artistSet;
    }

    public Set<String> addTopGenresToSpotifyData(Set<Artist> topArtists) {
        Map<String, Integer> topGenresCount = new HashMap<>();
        for (Artist artist : topArtists) {
            for (String genre : artist.getGenres()) {
                if (topGenresCount.containsKey(genre)) {
                    topGenresCount.put(genre, topGenresCount.get(genre) + 1);
                }
                else {
                    topGenresCount.put(genre, 0);
                }
            }
        }
        // sorting
        Set<String> keys = topGenresCount.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).limit(3).map(Map.Entry::getKey).collect(Collectors.toSet());

        return keys;
    }

}
