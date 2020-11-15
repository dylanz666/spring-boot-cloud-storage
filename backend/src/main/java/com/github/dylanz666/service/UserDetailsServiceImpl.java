package com.github.dylanz666.service;

import com.alibaba.fastjson.JSONArray;
import com.github.dylanz666.constant.UserRoleEnum;
import com.github.dylanz666.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : dylanz
 * @since : 10/04/2020
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserDetailsImpl userService;
    @Autowired
    private UserDetails userDetails;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //Spring Security要求必须加密密码
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        //模拟从数据库中取出用户信息，使用的sql如: SELECT * FROM USER WHERE USER_NAME='cherrys'
        //第一个用户，双角色
        List<User> userList = new ArrayList<>();
        User firstUser = new User();
        firstUser.setUsername("cherrys");
        firstUser.setPassword(passwordEncoder.encode("123"));
        JSONArray firstUserRoles = new JSONArray();
        firstUserRoles.add("ROLE_" + UserRoleEnum.ADMIN.toString());
        firstUserRoles.add("ROLE_" + UserRoleEnum.USER.toString());
        firstUser.setUserRoles(firstUserRoles);
        userList.add(firstUser);
        //第二个用户，单角色
        User secondUser = new User();
        secondUser.setUsername("randyh");
        secondUser.setPassword(passwordEncoder.encode("456"));
        JSONArray secondUserRoles = new JSONArray();
        secondUserRoles.add("ROLE_" + UserRoleEnum.USER.toString());
        secondUser.setUserRoles(secondUserRoles);
        userList.add(secondUser);

        List<User> mappedUsers = userList.stream().filter(s -> s.getUsername().equals(username)).collect(Collectors.toList());

        //判断用户是否存在
        User user;
        if (CollectionUtils.isEmpty(mappedUsers)) {
            logger.info(String.format("The user is not found: %s", username));
            throw new UsernameNotFoundException(String.format("The user is not found: %s", username));
        }
        user = mappedUsers.get(0);
        return new UserDetailsImpl(user);
    }
}
