package com.bandmates.application.service;

import com.bandmates.application.domain.SpotifyData;

import java.util.List;

public interface SpotifyDataService {
    SpotifyData saveSpotifyData(SpotifyData spotifyData);

    void addSpotifyDataToProfile(Long spotifyDataId, Long profileId);

    SpotifyData getSpotifyData(Long spotifyDataId);

    List<SpotifyData> getAllSpotifyData();

    SpotifyData updateSpotifyData(SpotifyData spotifyData, Long id);
}
