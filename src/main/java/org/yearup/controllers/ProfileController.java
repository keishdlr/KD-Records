package org.yearup.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.Profile;
import org.yearup.models.User;

import java.security.Principal;

    @RestController
    @RequestMapping("/profile")
    @CrossOrigin
    public class ProfileController {

        private final ProfileDao profileDao;
        private final UserDao userDao;

        public ProfileController(ProfileDao profileDao, UserDao userDao) {
            this.profileDao = profileDao;
            this.userDao = userDao;
        }

        @GetMapping("/profile")
        public Profile getProfile(Principal principal) {
            String username = principal.getName();
            User user = userDao.getByUserName(username);

            if (user == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
            }

            Profile profile = profileDao.getByUserId(user.getId());
            if (profile == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found");
            }
            return profile;
        }

        @PutMapping
        public Profile updateProfile(@RequestBody Profile incoming, Principal principal) {
            String username = principal.getName();
            User user = userDao.getByUserName(username);
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
            }

            Profile existing = profileDao.getByUserId(user.getId());
            if (existing == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found");
            }

            // Merge updates
            existing.setFirstName(incoming.getFirstName());
            existing.setLastName(incoming.getLastName());
            existing.setEmail(incoming.getEmail());
            existing.setPhone(incoming.getPhone());
            existing.setAddress(incoming.getAddress());
            existing.setCity(incoming.getCity());
            existing.setState(incoming.getState());
            existing.setZip(incoming.getZip());

            profileDao.update(existing);

            return profileDao.getByUserId(user.getId());
        }
    }