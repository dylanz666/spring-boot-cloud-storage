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
public class FileOperationResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String status;
    private String message;
    private String folderName;
    private String fileName;

    @Override
    public String toString() {
        return "{" +
                "\"status\":" + status + "," +
                "\"message\":\"" + message + "\"," +
                "\"folderName\":\"" + folderName + "\"," +
                "\"fileName\":" + fileName + "" +
                "}";
    }
}
