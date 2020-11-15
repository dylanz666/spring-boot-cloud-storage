package com.github.dylanz666.controller;

import com.alibaba.fastjson.JSONArray;
import com.github.dylanz666.domain.SignInResponse;
import com.github.dylanz666.domain.SignOutResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * @author : dylanz
 * @since : 10/09/2020
 */
@RestController
public class AuthController {
    @GetMapping("/api/auth")
    public SignInResponse getAuth() {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        SignInResponse signInResponse = new SignInResponse();
        signInResponse.setCode(200);
        signInResponse.setStatus("success");
        signInResponse.setMessage("success");
        signInResponse.setUsername(username);
        JSONArray userRoles = new JSONArray();
        for (GrantedAuthority authority : authorities) {
            String userRole = authority.getAuthority();
            if (!userRole.equals("")) {
                userRoles.add(userRole);
            }
        }
        signInResponse.setUserRoles(userRoles);

        return signInResponse;
    }
}
