package com.dm.docmind.tool;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;

import java.io.File;

public class OssUploader {
    public static String uploadFile(String endpoint, String accessKeyId, String accessKeySecret,
                                    String bucketName, String objectName, String localFilePath) {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ossClient.putObject(new PutObjectRequest(bucketName, objectName, new File(localFilePath)));
        ossClient.shutdown();
        // 返回可访问 URL，必须公开或者签名 URL
        return "https://" + bucketName + "." + endpoint + "/" + objectName;
    }
}
