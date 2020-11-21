package com.github.dylanz666.controller;

import com.github.dylanz666.constant.APIStatus;
import com.github.dylanz666.domain.FolderOperationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

/**
 * @author : dylanz
 * @since : 11/22/2020
 */
@RestController
@RequestMapping("/api/space")
public class SpaceController {
    @Value(value = "${file.root.dir}")
    private String rootDir;

    @GetMapping("")
    public FolderOperationResponse getSpaceInfo() {
        File folder = new File(rootDir);

        FolderOperationResponse folderOperationResponse = new FolderOperationResponse();
        try {
            long free = folder.getFreeSpace();
            long total = folder.getTotalSpace();
            String freeString = (free / 1024 / 1024 / 1024) + "G";
            String totalString = (total / 1024 / 1024 / 1024) + "G";

            String message = freeString + "/" + totalString;

            folderOperationResponse.setStatus(APIStatus.SUCCESS.toString());
            folderOperationResponse.setMessage(message);
            return folderOperationResponse;
        } catch (Exception e) {
            e.printStackTrace();
            folderOperationResponse.setStatus(APIStatus.FAIL.toString());
            folderOperationResponse.setMessage(e.getMessage());
        }
        return folderOperationResponse;
    }
}
