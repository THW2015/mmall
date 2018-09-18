package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by 谭皓文 on 2018/8/15.
 */
@Service("iFileService")
@Slf4j
public class FileServiceImpl implements IFileService{

    public String upload(MultipartFile file,String path){
        String fileName = file.getOriginalFilename();
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;

        File fileDir = new File(path);
        if(!fileDir.exists()){
            fileDir.canWrite();
            fileDir.mkdirs(); //创建多层目录
        }
        File targetFile = new File(path,uploadFileName);

        try {
            file.transferTo(targetFile);
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            fileDir.delete();
        } catch (IOException e) {
            log.error("上传文件异常",e);
            e.printStackTrace();
        }
        return targetFile.getName();
    }
}
