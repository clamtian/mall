package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;

/**
 * Created by lucky on 2019/2/22.
 */
public interface IFileService {

    HashSet upload(MultipartFile[] file, String path);

}
