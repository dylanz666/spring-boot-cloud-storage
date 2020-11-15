package com.github.dylanz666.controller;

import com.github.dylanz666.constant.APIStatus;
import com.github.dylanz666.domain.FileOperationMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author : dylanz
 * @since : 11/02/2020
 */
@RestController
@RequestMapping("/api")
public class FileUploadController {
    @Value(value = "${file.upload.dir}")
    private String rootPath;

    @GetMapping("/upload/ping")
    public String ping() {
        return "success";
    }

    @PostMapping("/file")
    public FileOperationMessage upload(@RequestParam("file") MultipartFile file) {
        FileOperationMessage fileOperationMessage = new FileOperationMessage();
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        try {
            File dest = new File(rootPath, fileName);

            file.transferTo(dest);

            fileOperationMessage.setStatus(APIStatus.SUCCESS.toString());
            fileOperationMessage.setMessage(APIStatus.SUCCESS.toString());
            fileOperationMessage.setFileName(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            fileOperationMessage.setStatus(APIStatus.FAIL.toString());
            fileOperationMessage.setMessage(e.getMessage());
            fileOperationMessage.setFileName(fileName);
        }
        return fileOperationMessage;
    }
}
