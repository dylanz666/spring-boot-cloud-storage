package com.github.dylanz666.config;

import com.alibaba.fastjson.JSONArray;
import com.github.dylanz666.constant.UserRoleEnum;
import com.github.dylanz666.domain.AuthorizationException;
import com.github.dylanz666.domain.SignInResponse;
import com.github.dylanz666.domain.SignOutResponse;
import com.github.dylanz666.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.cors.CorsUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Collection;

/**
 * @author : dylanz
 * @since : 10/04/2020
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private AuthorizationException authorizationException;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest)
                .permitAll()
                .antMatchers("/", "/ping").permitAll()//这些url不用访问认证
                .antMatchers("/admin/**").hasRole(UserRoleEnum.ADMIN.toString())
                .antMatchers("/user/**").hasRole(UserRoleEnum.USER.toString())
                .anyRequest()
                .authenticated()//其他url都需要访问认证
                .and()
                .formLogin()
                .loginProcessingUrl("/login")
                .permitAll()
                .failureHandler((request, response, ex) -> {//登录失败
                    response.setContentType("application/json");
                    response.setStatus(200);

                    SignInResponse signInResponse = new SignInResponse();
                    signInResponse.setCode(400);
                    signInResponse.setStatus("fail");
                    signInResponse.setMessage("Invalid username or password.");
                    signInResponse.setUsername(request.getParameter("username"));

                    PrintWriter out = response.getWriter();
                    out.write(signInResponse.toString());
                    out.flush();
                    out.close();
                })
                .successHandler((request, response, authentication) -> {//登录成功
                    response.setContentType("application/json");
                    response.setStatus(200);

                    SignInResponse signInResponse = new SignInResponse();
                    signInResponse.setCode(200);
                    signInResponse.setStatus("success");
                    signInResponse.setMessage("success");
                    signInResponse.setUsername(request.getParameter("username"));
                    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
                    JSONArray userRoles = new JSONArray();
                    for (GrantedAuthority authority : authorities) {
                        String userRole = authority.getAuthority();
                        if (!userRole.equals("")) {
                            userRoles.add(userRole);
                        }
                    }
                    signInResponse.setUserRoles(userRoles);

                    PrintWriter out = response.getWriter();
                    out.write(signInResponse.toString());
                    out.flush();
                    out.close();
                })
                .and()
                .logout()
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setContentType("application/json");
                    response.setStatus(200);

                    SignOutResponse signOutResponse = new SignOutResponse();
                    signOutResponse.setCode(200);
                    signOutResponse.setStatus("success");
                    signOutResponse.setMessage("success");

                    PrintWriter out = response.getWriter();
                    out.write(signOutResponse.toString());
                    out.flush();
                    out.close();
                })
                .permitAll()
                .and()
                .exceptionHandling()
                .accessDeniedHandler(((httpServletRequest, httpServletResponse, e) -> {
                    e.printStackTrace();
                    httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    httpServletResponse.setContentType("application/json");
                    authorizationException.setCode(HttpServletResponse.SC_FORBIDDEN);
                    authorizationException.setStatus("FAIL");
                    authorizationException.setMessage("FORBIDDEN");
                    authorizationException.setUri(httpServletRequest.getRequestURI());
                    PrintWriter printWriter = httpServletResponse.getWriter();
                    printWriter.write(authorizationException.toString());
                    printWriter.flush();
                    printWriter.close();
                }))
                .authenticationEntryPoint((httpServletRequest, httpServletResponse, e) -> {
                    e.printStackTrace();
                    httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    httpServletResponse.setContentType("application/json");
                    authorizationException.setCode(HttpServletResponse.SC_UNAUTHORIZED);
                    authorizationException.setStatus("FAIL");
                    authorizationException.setMessage("UNAUTHORIZED");
                    authorizationException.setUri(httpServletRequest.getRequestURI());
                    PrintWriter printWriter = httpServletResponse.getWriter();
                    printWriter.write(authorizationException.toString());
                    printWriter.flush();
                    printWriter.close();
                });
        try {
            http.userDetailsService(userDetailsService());
        } catch (Exception e) {
            http.authenticationProvider(authenticationProvider());
        }
        //开启跨域访问
        http.cors().disable();
        //开启模拟请求，比如API POST测试工具的测试，不开启时，API POST为报403错误
        http.csrf().disable();
    }

    @Override
    public void configure(WebSecurity web) {
        //对于在header里面增加token等类似情况，放行所有OPTIONS请求。
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        UserDetails dylanz =
                User.withUsername("dylanz")
                        .password(bCryptPasswordEncoder.encode("123"))
                        .roles(UserRoleEnum.ADMIN.toString())
                        .build();
        UserDetails ritay =
                User.withUsername("ritay")
                        .password(bCryptPasswordEncoder.encode("123"))
                        .roles(UserRoleEnum.USER.toString())
                        .build();
        return new InMemoryUserDetailsManager(dylanz, ritay);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_" + UserRoleEnum.ADMIN.toString() + " > ROLE_" + UserRoleEnum.USER.toString());
        return roleHierarchy;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }
}
