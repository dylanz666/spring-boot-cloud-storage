package com.github.dylanz666.domain;

import com.alibaba.fastjson.JSONArray;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author : dylanz
 * @since : 10/04/2020
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Component
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private JSONArray userRoles;
}
