package com.bandmates.application.service;

import com.bandmates.application.domain.SpotifyData;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public interface SpotifyDataService {
    Map<String, String> getSpotifyTokensFromCode(String code);

    SpotifyData getSpotifyRefreshToken(Long id);

    SpotifyData fetchUpdatedSpotifyData(String username);

    SpotifyData saveSpotifyData(SpotifyData spotifyData);

    void addSpotifyDataToProfile(Long spotifyDataId, Long profileId);

    SpotifyData getSpotifyData(Long spotifyDataId);

    List<SpotifyData> getAllSpotifyData();

    SpotifyData updateSpotifyData(SpotifyData spotifyData, Long id);
}
