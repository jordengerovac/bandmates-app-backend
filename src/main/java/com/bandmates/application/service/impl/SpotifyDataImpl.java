package com.bandmates.application.service.impl;

import com.bandmates.application.domain.AppUser;
import com.bandmates.application.domain.Profile;
import com.bandmates.application.domain.Role;
import com.bandmates.application.domain.SpotifyData;
import com.bandmates.application.repository.ProfileRepository;
import com.bandmates.application.repository.SpotifyDataRepository;
import com.bandmates.application.service.SpotifyDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SpotifyDataImpl implements SpotifyDataService {
    private final SpotifyDataRepository spotifyDataRepository;

    private final ProfileRepository profileRepository;

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
}
