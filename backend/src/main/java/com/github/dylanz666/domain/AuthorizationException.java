package com.github.dylanz666.domain;

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
public class AuthorizationException implements Serializable {
    private static final long serialVersionUID = 1L;

    private int code;
    private String status;
    private String uri;
    private String message;

    @Override
    public String toString() {
        return "{" +
                "\"code\":\"" + code + "\"," +
                "\"status\":\"" + status + "\"," +
                "\"message\":\"" + message + "\"," +
                "\"uri\":\"" + uri + "\"" +
                "}";
    }
}
