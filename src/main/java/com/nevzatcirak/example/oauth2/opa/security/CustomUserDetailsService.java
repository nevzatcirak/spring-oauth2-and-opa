package com.nevzatcirak.example.oauth2.opa.security;

import com.nevzatcirak.example.oauth2.opa.util.UserUtils;
import com.nevzatcirak.example.oauth2.opa.util.OAuthUser;
import com.nevzatcirak.example.oauth2.opa.util.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author Nevzat ÇIRAK,
 * @mail nevzatcirak17@gmail.com
 * Created by nevzatcirak at 01/07/2020.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private UserUtils userUtils;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        User user = userUtils.getUser(username);
        if (user == null) {
            logger.debug("Username:" + username + " not found!");
            throw new UsernameNotFoundException("Username=" + username + " not found!");
        }
        logger.debug("Loaded: " + user);
        return userUtils.convertUser(user);
    }

    public OAuthUser loadOAuthUserByUsername(String username){
        return userUtils.convertUser((User) loadUserByUsername(username));
    }

}