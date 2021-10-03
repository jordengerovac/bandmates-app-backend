package com.bandmates.application.service;

import com.bandmates.application.domain.Profile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProfileService {
    Profile saveProfile(Profile profile);

    void addProfileToUser(String username, Long profileId);

    Profile getProfile(Long profileId);

    List<Profile> getAllProfiles();

    List<Profile> getNearbyProfiles(String username);

    Profile updateProfile(Profile profile, Long id);

    Profile addImageToProfile(Long id, MultipartFile file);

    void deleteProfile(Long id);
}
