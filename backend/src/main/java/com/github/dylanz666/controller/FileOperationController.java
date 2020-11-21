package com.github.dylanz666.controller;

import com.github.dylanz666.constant.APIStatus;
import com.github.dylanz666.domain.FileInformation;
import com.github.dylanz666.domain.FileOperationResponse;
import org.springframework.beans.factory.annotation.Value;
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
     */
    @PostMapping("")
    public FileOperationResponse upload(@RequestParam String folderName, @RequestParam("file") MultipartFile file) {
        FileOperationResponse fileOperationMessage = new FileOperationResponse();
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        try {
            File dest = new File(rootDir, folderName + "\\" + fileName);

            //当文件父文件夹不存在，则创建该文件夹
            if(!dest.getParentFile().exists()){
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
                if(!dest.getParentFile().exists()){
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
     * 修改系统中的文件名
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
