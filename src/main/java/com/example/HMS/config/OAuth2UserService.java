package com.example.HMS.config;

import com.example.HMS.entity.User;
import com.example.HMS.enums.AuthProvider;
import com.example.HMS.enums.UserRole;
import com.example.HMS.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        return processOAuth2User(userRequest, oAuth2User);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String providerId = (String) attributes.get("sub");
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String picture = (String) attributes.get("picture");

        User user = userRepository.findByProviderIdAndAuthProvider(providerId, AuthProvider.GOOGLE)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setFullName(name);
                    newUser.setProfilePicture(picture);
                    newUser.setAuthProvider(AuthProvider.GOOGLE);
                    newUser.setProviderId(providerId);
                    newUser.setRole(UserRole.PATIENT);
                    newUser.setEnabled(true);
                    return userRepository.save(newUser);
                });

        // Update user info if changed
        boolean updated = false;
        if (!user.getFullName().equals(name)) {
            user.setFullName(name);
            updated = true;
        }
        if (picture != null && !picture.equals(user.getProfilePicture())) {
            user.setProfilePicture(picture);
            updated = true;
        }
        user.setLastLogin(LocalDateTime.now());

        if (updated) {
            userRepository.save(user);
        }

        return new CustomOAuth2User(user, oAuth2User.getAttributes());
    }
}