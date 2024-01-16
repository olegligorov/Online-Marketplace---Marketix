package com.marketix.util;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.marketix.web.UserController.UPLOADS_DIR;

public class MultiPartFile {
    public static void handleMultipartFile(MultipartFile file) {
        String name = file.getOriginalFilename();
        long size = file.getSize();
//        log.info("File: " + name + ", Size: " + size);
        try {
            File currentDir = new File(UPLOADS_DIR);
            if (!currentDir.exists()) {
                currentDir.mkdirs();
            }
            String path = ("uploads/" + file.getOriginalFilename());
//            String path = currentDir.getAbsolutePath() + "/" + file.getOriginalFilename();
            path = new File(path).getAbsolutePath();
//            log.info(path);
            File f = new File(path);
            FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(f));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
