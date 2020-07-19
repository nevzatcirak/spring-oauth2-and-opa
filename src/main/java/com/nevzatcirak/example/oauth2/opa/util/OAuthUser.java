package com.nevzatcirak.example.oauth2.opa.util;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * @author Nevzat ÇIRAK
 * @mail nevzatcirak17@gmail.com
 * Created by nevzatcirak at 30/06/2020
 */
public class OAuthUser extends User implements OAuth2User, UserDetails {
    private Map<String, Object> attributes;

    public OAuthUser(User user) {
        this.setId(user.getId());
        this.setUsername(user.getUsername());
        this.setPassword(user.getPassword());
        this.setName(user.getName());
        this.setSurname(user.getSurname());
        this.setEmail(user.getEmail());
        this.setAccountNonExpired(user.isAccountNonExpired());
        this.setAccountNonLocked(user.isAccountNonLocked());
        this.setCredentialsNonExpired(user.isCredentialsNonExpired());
    }

    public OAuthUser(UserDetails user) {
        this.setUsername(user.getUsername());
        this.setPassword(user.getPassword());
        this.setAccountNonExpired(user.isAccountNonExpired());
        this.setAccountNonLocked(user.isAccountNonLocked());
        this.setCredentialsNonExpired(user.isCredentialsNonExpired());
    }

    public static OAuthUser create(User user){
        return new OAuthUser(user);
    }

    public static OAuthUser create(User user, Map<String, Object> attributes){
        OAuthUser oAuthUser = create(user);
        oAuthUser.setAttributes(attributes);
        return oAuthUser;
    }

    @Override
    public <A> A getAttribute(String name) {
        return (A) this.attributes.get(name);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }


    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new HashSet<>();
//        getRoles().forEach(role -> {
//            role.getPermissions().forEach(permission -> authorities.add((GrantedAuthority) permission::getName));
//        });
        return authorities;
    }
}
