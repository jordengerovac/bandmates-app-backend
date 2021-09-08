package com.bandmates.application.service;

import com.bandmates.application.domain.Profile;
import java.util.List;

public interface ProfileService {
    Profile saveProfile(Profile profile);

    void addProfileToUser(String username, Long profileId);

    Profile getProfile(Long profileId);

    List<Profile> getAllProfiles();

    Profile updateProfile(Profile profile, Long id);
}
