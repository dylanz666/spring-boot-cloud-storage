package com.github.dylanz666.controller;

import com.alibaba.fastjson.JSONArray;
import com.github.dylanz666.constant.APIStatus;
import com.github.dylanz666.domain.FileDetail;
import com.github.dylanz666.domain.FileInformation;
import com.github.dylanz666.domain.FileOperationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;

/**
 * @author : dylanz
 * @since : 11/02/2020
 */
@RestController
@RequestMapping("/api/file")
public class FileOperationController {
    @Value(value = "${file.root.dir}")
    private String rootDir;

    private FileInputStream fis = null;
    private BufferedInputStream bis = null;

    /**
     * 向系统增加文件(上传)
     *
     * @param folderName 上传目标子文件夹
     * @param file       上传的文件
     */
    @PostMapping("")
    public FileOperationResponse upload(@RequestParam String folderName, @RequestParam("file") MultipartFile file) {
        FileOperationResponse fileOperationMessage = new FileOperationResponse();
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        try {
            File dest = new File(rootDir, folderName + "\\" + fileName);

            //当文件父文件夹不存在，则创建该文件夹
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdir();
            }

            file.transferTo(dest);

            fileOperationMessage.setStatus(APIStatus.SUCCESS.toString());
            fileOperationMessage.setMessage(APIStatus.SUCCESS.toString());
            fileOperationMessage.setFolderName(folderName);
            fileOperationMessage.setFileName(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            fileOperationMessage.setStatus(APIStatus.FAIL.toString());
            fileOperationMessage.setMessage(e.getMessage());
            fileOperationMessage.setFolderName(folderName);
            fileOperationMessage.setFileName(fileName);
        }
        return fileOperationMessage;
    }

    /**
     * 批量向系统增加文件(上传)
     *
     * @param folderName 上传目标子文件夹
     * @param files      上传的文件们，调用API时，form-data中可传多个file名字的文件
     */
    @PostMapping("/batch")
    public FileOperationResponse uploadFiles(@RequestParam String folderName, @RequestParam("file") List<MultipartFile> files) {
        FileOperationResponse fileOperationMessage = new FileOperationResponse();

        StringBuilder responseFileName = new StringBuilder();
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            assert fileName != null;
            responseFileName.append(fileName).append(";");
            try {
                File dest = new File(rootDir, folderName + "\\" + fileName);
                //当文件父文件夹不存在，则创建该文件夹
                if (!dest.getParentFile().exists()) {
                    dest.getParentFile().mkdir();
                }

                file.transferTo(dest);
            } catch (Exception e) {
                e.printStackTrace();
                fileOperationMessage.setStatus(APIStatus.FAIL.toString());
                fileOperationMessage.setMessage(e.getMessage());
                fileOperationMessage.setFolderName(folderName);
                fileOperationMessage.setFileName(responseFileName.toString());
            }
            fileOperationMessage.setStatus(APIStatus.SUCCESS.toString());
            fileOperationMessage.setMessage(APIStatus.SUCCESS.toString());
            fileOperationMessage.setFolderName(folderName);
            fileOperationMessage.setFileName(responseFileName.toString());
        }

        return fileOperationMessage;
    }

    /**
     * 删除系统中的文件
     *
     * @param folderName 要删除的文件的目标文件夹
     * @param fileName   要删除的文件
     */
    @DeleteMapping("")
    public FileOperationResponse delete(@RequestParam String folderName, @RequestParam String fileName) {
        FileOperationResponse fileOperationMessage = new FileOperationResponse();
        File file = new File(rootDir, folderName + "\\" + fileName);
        try {
            if (file.exists()) {
                file.delete();

                fileOperationMessage.setStatus(APIStatus.SUCCESS.toString());
                fileOperationMessage.setMessage(APIStatus.SUCCESS.toString());
                fileOperationMessage.setFolderName(folderName);
                fileOperationMessage.setFileName(fileName);
                return fileOperationMessage;
            }
            fileOperationMessage.setStatus(APIStatus.FAIL.toString());
            fileOperationMessage.setMessage("The file is not exist.");
            fileOperationMessage.setFileName(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            fileOperationMessage.setStatus(APIStatus.FAIL.toString());
            fileOperationMessage.setMessage(e.getMessage());
            fileOperationMessage.setFolderName(folderName);
            fileOperationMessage.setFileName(fileName);
        }
        return fileOperationMessage;
    }

    /**
     * 批量删除系统中的文件
     *
     * @param files 要删除的文件
     */
    @PostMapping("/batchDelete")
    public FileOperationResponse deleteFiles(@RequestBody FileDetail[] files) {
        FileOperationResponse fileOperationMessage = new FileOperationResponse();
        if (files.length == 0) {
            fileOperationMessage.setStatus(APIStatus.FAIL.toString());
            fileOperationMessage.setMessage("请选择要删除的文件");
            return fileOperationMessage;
        }

        int successQuantity = 0;
        int failQuantity = 0;
        FileDetail file1 = files[0];
        String folderName = file1.getPath();
        for (int i = 0; i < files.length; i++) {
            FileDetail fileDetail = files[i];
            String fileName = fileDetail.getName();
            File file = new File(rootDir, folderName + "\\" + fileName);
            try {
                if (file.exists()) {
                    file.delete();
                    successQuantity += 1;
                    continue;
                }
                failQuantity += 1;
            } catch (Exception e) {
                e.printStackTrace();
                failQuantity += 1;
            }
        }
        if (failQuantity == 0 && successQuantity > 0) {
            fileOperationMessage.setStatus(APIStatus.SUCCESS.toString());
            fileOperationMessage.setMessage(APIStatus.SUCCESS.toString());
            fileOperationMessage.setFolderName(folderName);
        }
        if (failQuantity > 0) {
            fileOperationMessage.setStatus(APIStatus.FAIL.toString());
            fileOperationMessage.setMessage("删除成功：" + successQuantity + ",删除失败：" + failQuantity);
            fileOperationMessage.setFolderName(folderName);
        }
        return fileOperationMessage;
    }

    /**
     * 修改系统中的文件名
     *
     * @param fileInformation 文件信息
     */
    @PutMapping("")
    public FileOperationResponse updateFile(@RequestBody FileInformation fileInformation) {
        FileOperationResponse fileOperationMessage = new FileOperationResponse();
        String folder = rootDir + "\\" + fileInformation.getFolderName();
        String currentFileName = fileInformation.getCurrentFileName();
        String targetFileName = fileInformation.getTargetFileName();

        File currentFile = new File(folder, currentFileName);
        File targetFile = new File(folder, targetFileName);
        try {
            if (targetFile.exists()) {
                fileOperationMessage.setStatus(APIStatus.FAIL.toString());
                fileOperationMessage.setMessage("The target file name is already exist.");
                fileOperationMessage.setFileName(targetFileName);
                return fileOperationMessage;
            }
            if (currentFile.exists()) {
                currentFile.renameTo(targetFile);

                fileOperationMessage.setStatus(APIStatus.SUCCESS.toString());
                fileOperationMessage.setMessage(APIStatus.SUCCESS.toString());
                fileOperationMessage.setFileName(targetFileName);
                return fileOperationMessage;
            }
            fileOperationMessage.setStatus(APIStatus.FAIL.toString());
            fileOperationMessage.setMessage("The file is not exist.");
            fileOperationMessage.setFileName(currentFileName);
        } catch (Exception e) {
            e.printStackTrace();
            fileOperationMessage.setStatus(APIStatus.FAIL.toString());
            fileOperationMessage.setMessage(e.getMessage());
            fileOperationMessage.setFileName(currentFileName);
        }
        return fileOperationMessage;
    }

    /**
     * 下载系统中的文件
     *
     * @param folderName 被下载的目标子文件夹
     * @param fileName   被下载的文件
     */
    @GetMapping("/download")
    public FileOperationResponse download(@RequestParam String folderName, @RequestParam String fileName) {
        FileOperationResponse fileOperationMessage = new FileOperationResponse();
        String folder = rootDir + "\\" + folderName;
        try {
            File file = new File(folder, fileName);

            HttpServletResponse response = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getResponse();
            assert response != null;
            response.setContentType("application/force-download");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(fileName, "UTF-8"));

            if (file.exists()) {
                byte[] buffer = new byte[1024];
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();

                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }

                fileOperationMessage.setStatus(APIStatus.SUCCESS.toString());
                fileOperationMessage.setMessage(APIStatus.SUCCESS.toString());
                fileOperationMessage.setFolderName(folderName);
                fileOperationMessage.setFileName(fileName);
                return fileOperationMessage;
            }
            fileOperationMessage.setStatus(APIStatus.FAIL.toString());
            fileOperationMessage.setMessage("The file is not exist.");
            fileOperationMessage.setFileName(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            fileOperationMessage.setStatus(APIStatus.FAIL.toString());
            fileOperationMessage.setMessage(e.getMessage());
            fileOperationMessage.setFolderName(folderName);
            fileOperationMessage.setFileName(fileName);
        } finally {
            try {
                bis.close();
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return fileOperationMessage;
    }

    /**
     * 验证文件是否存在
     *
     * @param folderName 文件的文件夹名
     * @param fileName   文件名
     */
    @GetMapping("")
    public FileOperationResponse getFile(@RequestParam String folderName, @RequestParam String fileName) {
        FileOperationResponse fileOperationMessage = new FileOperationResponse();
        String folder = rootDir + "\\" + folderName;
        try {
            File file = new File(folder, fileName);
            if (!file.exists()) {
                fileOperationMessage.setStatus(APIStatus.FAIL.toString());
                fileOperationMessage.setMessage("The file is not exist.");
                fileOperationMessage.setFolderName(folderName);
                fileOperationMessage.setFileName(fileName);
                return fileOperationMessage;
            }
            fileOperationMessage.setStatus(APIStatus.SUCCESS.toString());
            fileOperationMessage.setMessage("The file is exist.");
            fileOperationMessage.setFileName(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            fileOperationMessage.setStatus(APIStatus.FAIL.toString());
            fileOperationMessage.setMessage(e.getMessage());
            fileOperationMessage.setFolderName(folderName);
            fileOperationMessage.setFileName(fileName);
        }
        return fileOperationMessage;
    }
}
