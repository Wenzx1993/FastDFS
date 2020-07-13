package com.fdfs.study.service;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UploadService {

    public String upload(MultipartFile file)  {
        //获取文件名
        String filename = file.getOriginalFilename();
        //获取文件类型
        //TODO 最好根据content-type来判断
        String extName= "";
        if(filename.contains("."))
        {
             extName = filename.substring(filename.lastIndexOf(".")+1);
        }
        try {
            StorageClient storageClient = buildClient();
            //返回结果，第一个为组名，第二个为fdfs磁盘地址
            String[] result = storageClient.upload_file(file.getBytes(), extName, null);
            return new StringBuffer(result[0]).append("/").append(result[1]).toString();
        } catch (IOException | MyException e) {
            e.printStackTrace();
        }
        return "上传失败！";
    }

    /**
     * 构建上传对象
     * @return
     */
    public StorageClient buildClient() throws IOException, MyException {
        ClientGlobal.init("fastdfs-client.properties");
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getTrackerServer();
        StorageClient storageClient = new StorageClient(trackerServer);
        return storageClient;
    }
}
