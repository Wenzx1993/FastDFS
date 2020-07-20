package com.fdfs.study.service;

import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

@Service
@PropertySource("classpath:fastdfs-client.properties")
public class UploadService {

    /**
     * 密钥
     */
   @Value("${http_secret_key}")
   private String secretKey;
    /**
     * 服务器地址
     */
   @Value("${fdfs.remote-url}")
   private String remoteUrl;

    /**
     * 上传逻辑
     * @param file
     * @return
     */
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
//            String srcUrl = new StringBuffer(result[0]).append("/").append(result[1]).toString();
            return tokenUrl(result[0],result[1]);
        } catch (IOException | MyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "上传失败！";
    }

    /**
     * 防盗链地址
     * @param group
     * @param remoteFile
     * @return
     */
    public String tokenUrl(String group,String remoteFile) throws UnsupportedEncodingException, NoSuchAlgorithmException, MyException {
        int ts = (int) (System.currentTimeMillis()/1000);
        String token = ProtoCommon.getToken(remoteFile, ts, secretKey);
        StringBuffer url = new StringBuffer(remoteUrl);
        url.append("/")
            .append(group)
            .append("/")
            .append(remoteFile)
            .append("?")
            .append("token=")
            .append(token)
            .append("&ts=")
            .append(ts);
        return url.toString();
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
