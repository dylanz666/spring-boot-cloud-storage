package com.github.dylanz666.controller;

import com.alibaba.fastjson.JSONArray;
import com.github.dylanz666.constant.APIStatus;
import com.github.dylanz666.domain.FolderInformation;
import com.github.dylanz666.domain.FolderOperationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Arrays;

/**
 * @author : dylanz
 * @since : 11/15/2020
 */
@RestController
@RequestMapping("/api/folder")
public class FolderOperationController {
    @Value(value = "${file.root.dir}")
    private String rootDir;

    @PostMapping("")
    public FolderOperationResponse createFolder(@RequestParam String folderName) {
        FolderOperationResponse folderOperationResponse = new FolderOperationResponse();
        File newFolder = new File(rootDir, folderName);
        try {
            if (newFolder.exists()) {
                folderOperationResponse.setStatus(APIStatus.FAIL.toString());
                folderOperationResponse.setMessage("The target folder name is already exist.");
                folderOperationResponse.setFolderName(folderName);
                return folderOperationResponse;
            }
            newFolder.mkdir();
            folderOperationResponse.setStatus(APIStatus.SUCCESS.toString());
            folderOperationResponse.setMessage(APIStatus.SUCCESS.toString());
            folderOperationResponse.setFolderName(folderName);
        } catch (Exception e) {
            e.printStackTrace();
            folderOperationResponse.setStatus(APIStatus.FAIL.toString());
            folderOperationResponse.setMessage(e.getMessage());
            folderOperationResponse.setFolderName(folderName);
        }
        return folderOperationResponse;
    }

    @DeleteMapping("")
    public FolderOperationResponse deleteFolder(@RequestParam String folderName, @RequestParam String all) {
        FolderOperationResponse folderOperationResponse = new FolderOperationResponse();
        File folder = new File(rootDir, folderName);

        try {
            if (!folder.exists()) {
                folderOperationResponse.setStatus(APIStatus.FAIL.toString());
                folderOperationResponse.setMessage("The folder is not exist.");
                folderOperationResponse.setFolderName(folderName);
                return folderOperationResponse;
            }
            if (!folder.isDirectory()) {
                folderOperationResponse.setStatus(APIStatus.FAIL.toString());
                folderOperationResponse.setMessage("This is not a folder.");
                folderOperationResponse.setFolderName(folderName);
                return folderOperationResponse;
            }
            if (all.equalsIgnoreCase("Y") || all.equalsIgnoreCase("yes") || all.equalsIgnoreCase("true")) {
                deleteFolder(rootDir + "\\" + folderName);
            }

            folderOperationResponse.setStatus(APIStatus.SUCCESS.toString());
            folderOperationResponse.setMessage(APIStatus.SUCCESS.toString());
            folderOperationResponse.setFolderName(folderName);
        } catch (Exception e) {
            e.printStackTrace();
            folderOperationResponse.setStatus(APIStatus.FAIL.toString());
            folderOperationResponse.setMessage(e.getMessage());
            folderOperationResponse.setFolderName(folderName);
        }
        return folderOperationResponse;
    }

    @PutMapping("")
    public FolderOperationResponse updateFolder(@RequestBody FolderInformation folderInformation) {
        FolderOperationResponse folderOperationResponse = new FolderOperationResponse();
        String currentFolderName = folderInformation.getCurrentFolderName();
        String targetFolderName = folderInformation.getTargetFolderName();

        File currentFolder = new File(rootDir, currentFolderName);
        File targetFolder = new File(rootDir, targetFolderName);
        try {
            if (targetFolder.exists()) {
                folderOperationResponse.setStatus(APIStatus.FAIL.toString());
                folderOperationResponse.setMessage("The target folder name is already exist.");
                folderOperationResponse.setFolderName(targetFolderName);
                return folderOperationResponse;
            }
            if (currentFolder.exists()) {
                currentFolder.renameTo(targetFolder);

                folderOperationResponse.setStatus(APIStatus.SUCCESS.toString());
                folderOperationResponse.setMessage(APIStatus.SUCCESS.toString());
                folderOperationResponse.setFolderName(targetFolderName);
                return folderOperationResponse;
            }
            folderOperationResponse.setStatus(APIStatus.FAIL.toString());
            folderOperationResponse.setMessage("The folder is not exist.");
            folderOperationResponse.setFolderName(currentFolderName);
        } catch (Exception e) {
            e.printStackTrace();
            folderOperationResponse.setStatus(APIStatus.FAIL.toString());
            folderOperationResponse.setMessage(e.getMessage());
            folderOperationResponse.setFolderName(currentFolderName);
        }
        return folderOperationResponse;
    }

    @GetMapping("")
    public FolderOperationResponse getFolder(@RequestParam String folderName) {
        FolderOperationResponse folderOperationResponse = new FolderOperationResponse();
        File folder = new File(rootDir, folderName);
        try {
            if (!folder.exists()) {
                folderOperationResponse.setStatus(APIStatus.FAIL.toString());
                folderOperationResponse.setMessage("The folder is not exist.");
                folderOperationResponse.setFolderName(folderName);
                return folderOperationResponse;
            }
            if (folder.isDirectory()) {
                folderOperationResponse.setStatus(APIStatus.FAIL.toString());
                folderOperationResponse.setMessage("This is not a folder.");
                folderOperationResponse.setFolderName(folderName);
                return folderOperationResponse;
            }
            folderOperationResponse.setStatus(APIStatus.SUCCESS.toString());
            folderOperationResponse.setMessage("The folder is exist.");
            folderOperationResponse.setFolderName(folderName);
        } catch (Exception e) {
            e.printStackTrace();
            folderOperationResponse.setStatus(APIStatus.FAIL.toString());
            folderOperationResponse.setMessage(e.getMessage());
            folderOperationResponse.setFolderName(folderName);
        }
        return folderOperationResponse;
    }

    @GetMapping("/children")
    public FolderOperationResponse getFiles(@RequestParam String folderName, @RequestParam String all) {
        FolderOperationResponse folderOperationResponse = new FolderOperationResponse();
        try {
            File folder = new File(rootDir, folderName);
            if (folder.exists()) {
                String[] fileList = folder.list();
                JSONArray list = new JSONArray();
                assert fileList != null;
                if (all.equalsIgnoreCase("Y") || all.equalsIgnoreCase("yes") || all.equalsIgnoreCase("true")) {
                    list.addAll(Arrays.asList(fileList));

                    folderOperationResponse.setStatus(APIStatus.SUCCESS.toString());
                    folderOperationResponse.setMessage(APIStatus.SUCCESS.toString());
                    folderOperationResponse.setFolderName(folderName);
                    folderOperationResponse.setFiles(list);
                    return folderOperationResponse;
                }

                for (String item : fileList) {
                    File sub = new File(rootDir, folderName + "\\" + item);
                    if (sub.isDirectory()) {
                        list.add(item);
                    }
                }
                folderOperationResponse.setStatus(APIStatus.SUCCESS.toString());
                folderOperationResponse.setMessage(APIStatus.SUCCESS.toString());
                folderOperationResponse.setFolderName(folderName);
                folderOperationResponse.setFiles(list);
                return folderOperationResponse;
            }
            folderOperationResponse.setStatus(APIStatus.FAIL.toString());
            folderOperationResponse.setMessage("The folder is not exist.");
            folderOperationResponse.setFolderName(folderName);
            folderOperationResponse.setFiles(new JSONArray());
        } catch (Exception e) {
            e.printStackTrace();
            folderOperationResponse.setStatus(APIStatus.FAIL.toString());
            folderOperationResponse.setMessage(e.getMessage());
            folderOperationResponse.setFolderName(folderName);
            folderOperationResponse.setFiles(new JSONArray());
        }
        return folderOperationResponse;
    }

    private void deleteFolder(String path) {
        File folder = new File(path);
        if (!folder.isFile()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File f : files) {
                    deleteFolder(f.getAbsolutePath());
                }
            }
        }
        folder.delete();
    }
}
