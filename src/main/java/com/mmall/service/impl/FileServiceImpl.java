package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.common.ResponseCode;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by lucky on 2019/2/22.
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService {

    //日志
    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file, String path){
        String fileName = file.getOriginalFilename();

        //可能是abc.abc.jpg,所以从最后一个“.”开始获取拓展名
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
        String uploadFileName = UUID.randomUUID().toString() + "." + fileExtensionName;
        logger.info("开始上传文件,上传文件的文件名:{},上传的路径:{},新文件名:{}",
                fileName, path, uploadFileName);

        File fileDir = new File(path);
        if(!fileDir.exists()){
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path, uploadFileName);

        try {
            //将file保存到targetFile中
            file.transferTo(targetFile);
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            //删除文件，防止累积过多
            targetFile.delete();
        } catch (IOException e) {
            logger.error(ResponseCode.ERROR.getDesc(),e);
            return null;
        }
        return targetFile.getName();
    }
}
