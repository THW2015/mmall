package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by 谭皓文 on 2018/8/15.
 */
public interface IFileService {
    String upload(MultipartFile file, String path);
}
