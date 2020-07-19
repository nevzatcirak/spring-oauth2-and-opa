package com.nevzatcirak.example.oauth2.opa.util;

import org.springframework.stereotype.Component;

/**
 * @author Nevzat ÇIRAK
 * @mail nevzatcirak17@gmail.com
 * Created by nevzatcirak at 09/07/2020
 */
@Component
public class UserUtils {

    public User getUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setName("Dummy");
        user.setEmail("dummy@user.com");
        user.setSurname("User");
        user.setId(1L);
        user.setPassword("123456");
        return user;
    }

    public OAuthUser convertUser(User user) {
        return new OAuthUser(user);
    }
}
