package org.yearup.controllers;

import com.fasterxml.jackson.dataformat.yaml.util.StringQuotingChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.models.User;
import org.yearup.repository.UserRepository;
import org.yearup.service.ProfileService;
import org.yearup.service.UserService;

import java.security.Principal;

@RestController
@RequestMapping("profile")
@CrossOrigin
@PreAuthorize("isAuthenticated()") // Restrict to logged-in users
public class ProfileController {

    private final ProfileService profileService;
    private final UserRepository userRepository; // Used to get the current user's ID from their username

    @Autowired
    public ProfileController(ProfileService profileService, UserRepository userRepository) {
        this.profileService = profileService;
        this.userRepository = userRepository;
    }

    @GetMapping("")
    public Profile getProfile(Principal principal) {
        try {
            String username = principal.getName();
            User user = userRepository.getByUsername(username);
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
            }

            Profile profile = profileService.getByUserId(user.getId());
            if (profile == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found.");
            }

            return profile;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.", e);
        }
    }

    @PutMapping("")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204 No Content for successful updates
    public void updateProfile(Principal principal, @RequestBody Profile profile) {
        try {
            // 1. Get the current logged-in username
            String username = principal.getName();

            // 2. Look up the user to find their unique ID
            User user = userRepository.getByUsername(username);
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
            }

            // 3. Update the profile using the authenticated user's ID
            profileService.update(user.getId(), profile);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.", e);
        }
    }
}
