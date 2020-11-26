package com.github.dylanz666.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.dylanz666.constant.APIStatus;
import com.github.dylanz666.domain.FolderInformation;
import com.github.dylanz666.domain.FolderOperationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Date;
import java.util.Objects;

/**
 * @author : dylanz
 * @since : 11/15/2020
 */
@RestController
@RequestMapping("/api/folder")
public class FolderOperationController {
    @Value(value = "${file.root.dir}")
    private String rootDir;

    /**
     * 创建文件夹
     *
     * @param folderName:文件夹名
     */
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

    /**
     * 删除文件夹
     *
     * @param folderName 文件夹名
     * @param all        是否递归删除文件夹下文件
     */
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
                folderOperationResponse.setStatus(APIStatus.SUCCESS.toString());
                folderOperationResponse.setMessage(APIStatus.SUCCESS.toString());
                folderOperationResponse.setFolderName(folderName);
                return folderOperationResponse;
            }
            if (Objects.requireNonNull(folder.listFiles()).length > 0) {
                folderOperationResponse.setStatus(APIStatus.FAIL.toString());
                folderOperationResponse.setMessage("This is not a empty folder, if you still want to delete it, please pass a parameter: all, and it's value to be Y or Yes.");
                folderOperationResponse.setFolderName(folderName);
                return folderOperationResponse;
            }
            folder.delete();

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

    /**
     * 更新除文件夹名字
     *
     * @param folderInformation 文件夹信息
     */
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

    /**
     * 验证文件夹是否存在
     *
     * @param folderName 文件夹名
     */
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

    /**
     * 获取文件夹下的内容
     *
     * @param folderName 文件夹名
     * @param all        是否包含所有内容，是则获取包含文件夹的所有内容，否则只获取文件
     */
    @GetMapping("/children")
    public FolderOperationResponse getFiles(@RequestParam String folderName, @RequestParam String all) {
        FolderOperationResponse folderOperationResponse = new FolderOperationResponse();
        try {
            File folder = new File(rootDir, folderName);
            if (!folder.exists()) {
                folderOperationResponse.setStatus(APIStatus.FAIL.toString());
                folderOperationResponse.setMessage("The folder is not exist.");
                folderOperationResponse.setFolderName(folderName);
                folderOperationResponse.setFiles(new JSONArray());
                return folderOperationResponse;
            }

            String[] fileList = folder.list();
            JSONArray list = new JSONArray();
            assert fileList != null;
            for (String item : fileList) {
                File sub = new File(rootDir, folderName + "\\" + item);
                String subName = sub.getName().toLowerCase();
                Date date = new Date(sub.lastModified());

                JSONObject singleFileItem = new JSONObject();
                singleFileItem.put("name", subName);
                singleFileItem.put("date", String.format("%tF%n", date) + " " + String.format("%tT%n", date));
                if (sub.isDirectory()) {
                    singleFileItem.put("path", folderName.equals("/") ? (folderName + item) : folderName + "/" + item);
                    singleFileItem.put("size", "-");
                    singleFileItem.put("type", "folder");
                    list.add(singleFileItem);
                    continue;
                }
                if (!(all.equalsIgnoreCase("Y") || all.equalsIgnoreCase("yes") || all.equalsIgnoreCase("true"))) {
                    continue;
                }
                singleFileItem.put("path", folderName);
                singleFileItem.put("size", sub.length() / 1000);
                if (subName.endsWith(".jpg") || subName.endsWith(".jpeg") || subName.endsWith(".png") || subName.endsWith(".bmp") || subName.endsWith(".ico") || subName.endsWith(".psd") || subName.endsWith(".psb") || subName.endsWith(".gif")) {
                    singleFileItem.put("type", "image");
                    list.add(singleFileItem);
                    continue;
                }
                if (subName.endsWith(".mp4") || subName.endsWith(".mov") || subName.endsWith(".3gp") || subName.endsWith(".avi") || subName.endsWith(".mpg") || subName.endsWith(".mpeg") || subName.endsWith(".flv") || subName.endsWith(".mkv") || subName.endsWith(".rm") || subName.endsWith(".rmvb") || subName.endsWith(".wmv")) {
                    singleFileItem.put("type", "video");
                    list.add(singleFileItem);
                    continue;
                }
                if (subName.endsWith(".mp3") || subName.endsWith(".wav") || subName.endsWith(".ogg") || subName.endsWith(".ape") || subName.endsWith(".acc")) {
                    singleFileItem.put("type", "audio");
                    list.add(singleFileItem);
                    continue;
                }
                singleFileItem.put("type", "file");
                list.add(singleFileItem);
            }

            folderOperationResponse.setStatus(APIStatus.SUCCESS.toString());
            folderOperationResponse.setMessage(APIStatus.SUCCESS.toString());
            folderOperationResponse.setFolderName(folderName);
            folderOperationResponse.setFiles(list);
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
