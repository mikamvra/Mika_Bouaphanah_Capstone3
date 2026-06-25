package org.yearup.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yearup.models.Profile;
import org.yearup.repository.ProfileRepository;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;

    @Autowired
    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Profile getByUserId(int userId) {
        return profileRepository.findById(userId).orElse(null);
    }

    public void update(int userId, Profile profile) {
        profile.setUserId(userId);
        profileRepository.save(profile);
    }
}