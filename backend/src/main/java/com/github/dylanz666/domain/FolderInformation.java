package com.github.dylanz666.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author : dylanz
 * @since : 11/15/2020
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Component
public class FolderInformation implements Serializable {
    private static final long serialVersionUID = 1L;

    private String currentFolderName;
    private String targetFolderName;

    @Override
    public String toString() {
        return "{" +
                "\"currentFolderName\":\"" + currentFolderName + "\"," +
                "\"targetFolderName\":" + targetFolderName + "" +
                "}";
    }
}