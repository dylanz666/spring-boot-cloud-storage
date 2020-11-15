package com.github.dylanz666.domain;

import com.alibaba.fastjson.JSONArray;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author : dylanz
 * @since : 10/10/2020
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Component
public class SignOutResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private int code;
    private String status;
    private String message;

    @Override
    public String toString() {
        return "{" +
                "\"code\":" + code + "," +
                "\"status\":\"" + status + "\"," +
                "\"message\":\"" + message + "\"" +
                "}";
    }
}