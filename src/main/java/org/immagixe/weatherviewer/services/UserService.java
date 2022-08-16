package org.immagixe.weatherviewer.services;

import org.immagixe.weatherviewer.models.User;
import org.immagixe.weatherviewer.repositories.UserRepository;
import org.immagixe.weatherviewer.util.BCryptPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }

    @Transactional
    public void deleteLocationFromList(User user, String locationName) {
        user.deleteLocation(locationName);
        save(user);
    }

    public User findByLogin(User user) {
        String login = user.getLogin();
        return userRepository.findByLogin(login).orElse(null);
    }

    public User findByLoginAndPassword(User user) {
        String login = user.getLogin();
        String originalPassword = user.getPassword();
        String securedPasswordHash = getSecuredPasswordHashFromDB(user);

        boolean matched = BCryptPassword.checkSecuredPassword(originalPassword, securedPasswordHash);
        if (matched) {
            return userRepository.findByLoginAndPassword(login, securedPasswordHash).orElse(null);
        } else {
            return null;
        }
    }

    public String getSecuredPasswordHashFromDB(User user) {
        User foundUser = findByLogin(user);
        return foundUser.getPassword();
    }
}


