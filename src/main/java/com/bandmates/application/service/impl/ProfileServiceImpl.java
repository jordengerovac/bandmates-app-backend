package com.bandmates.application.service.impl;

import com.bandmates.application.domain.AppUser;
import com.bandmates.application.domain.Profile;
import com.bandmates.application.domain.Role;
import com.bandmates.application.repository.ProfileRepository;
import com.bandmates.application.repository.UserRepository;
import com.bandmates.application.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;

    private final UserRepository userRepository;

    @Override
    public Profile saveProfile(Profile profile) {
        log.info("Adding new profile {} to database", profile.getId());
        return profileRepository.save(profile);
    }

    @Override
    public void addProfileToUser(String username, Long profileId) {
        log.info("Adding profile {} to user {}", profileId, username);
        AppUser user = userRepository.findByUsername(username);
        Optional<Profile> profile = profileRepository.findById(profileId);
        if (profile.isPresent()) {
            profile.get().setUser(user);
            user.setProfile(profile.get());
        }
        else {
            log.error("Profile not found");
        }
    }

    @Override
    public Profile getProfile(Long profileId) {
        log.info("Fetching profile {} from database", profileId);
        return profileRepository.findById(profileId).get();
    }

    @Override
    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }

    @Override
    public Profile updateProfile(Profile profile, Long id) {
        Optional<Profile> oldProfile = profileRepository.findById(id);
        if (oldProfile.isPresent()) {
            if (profile.getUser() != null)
                oldProfile.get().setUser(profile.getUser());
            if (profile.getBio() != null)
                oldProfile.get().setBio(profile.getBio());
            if (profile.getSpotifyData() != null)
                oldProfile.get().setSpotifyData(profile.getSpotifyData());

            return profileRepository.save(oldProfile.get());
        }
        return null;
    }

    @Override
    public Profile addImageToProfile(Long id, MultipartFile image) {
        try {
            Profile profile = profileRepository.findById(id).get();

            byte[] byteObjects = new byte[image.getBytes().length];

            int i = 0;

            for (byte b : image.getBytes()){
                byteObjects[i++] = b;
            }

            String encodedImage = Base64.getEncoder().encodeToString(byteObjects);

            profile.setImage(encodedImage);

            return profileRepository.save(profile);

        } catch (IOException exception) {
            log.error(exception.getMessage());
            return null;
        }
    }
}
