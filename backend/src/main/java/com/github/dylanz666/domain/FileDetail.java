package com.github.dylanz666.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author : dylanz
 * @since : 11/28/2020
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Component
public class FileDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    private String date;
    private String name;
    private String path;
    private String size;
    private String type;

    @Override
    public String toString() {
        return "{" +
                "\"date\":" + date + "," +
                "\"name\":\"" + name + "\"," +
                "\"path\":\"" + path + "\"," +
                "\"size\":\"" + size + "\"," +
                "\"type\":" + type + "" +
                "}";
    }
}
