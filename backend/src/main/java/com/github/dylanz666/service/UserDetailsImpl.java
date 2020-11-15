package com.github.dylanz666.service;

import com.alibaba.fastjson.JSONArray;
import com.github.dylanz666.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author : dylanz
 * @since : 10/04/2020
 */
@Service
public class UserDetailsImpl implements UserDetails {
    private User currentUser;

    public UserDetailsImpl() {
    }

    public UserDetailsImpl(User user) {
        if (user != null) {
            this.currentUser = user;
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        JSONArray userRoles = this.currentUser.getUserRoles();
        for (Object userRoleItem : userRoles) {
            String userRole = userRoleItem.toString();
            if (!userRole.equals("")) {
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(userRole);
                authorities.add(authority);
            }
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return currentUser.getPassword();
    }

    public String getUsername() {
        return currentUser.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
