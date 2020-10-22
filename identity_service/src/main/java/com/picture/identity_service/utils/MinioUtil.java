package com.picture.identity_service.utils;

import io.minio.MinioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * MinIO操作工具类
 *  目前仅使用到基础的文件上传
 *  后续需要复杂业务再进行添加
 *
 * @author Yue Wu
 * @since 2020/10/9
 */
public final class MinioUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MinioUtil.class);

    private static MinioUtil minioUtil;

    private MinioClient minioClient;

    /**
     * 获取MinIO工具类实例
     *
     * @return 返回实例
     */
    public static MinioUtil getInstance() {
        if (null != minioUtil) {
            return minioUtil;
        }
        synchronized (MinioUtil.class) {
            if (null == minioUtil) {
                minioUtil = new MinioUtil();
            }
        }
        return minioUtil;
    }

    private MinioUtil() {
        init();
    }

    private void init() {
        String url = "http://182.92.208.18:9000/";
        String username = "admin";
        String password = "admin0704";
        String region = "project";
        try {
            minioClient = new MinioClient(url, username, password);
        } catch (Exception e) {
            LOGGER.error("restClient.close occur error", e);
        }

    }

    /**
     *  上传文件——通过流的方式进行上传
     *
     * @param file 待操作文件
     * @return 文件全地址
     */
    public String upLoadFile(MultipartFile file) {
        String url = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String ymd = sdf.format(new Date());
        String objectName = ymd + "/" + UUID.randomUUID().toString();
        try {
            minioClient.putObject("project", objectName, file.getInputStream(), file.getContentType());
            url = minioClient.getObjectUrl("project", objectName);
        } catch (Exception e) {
            LOGGER.error("restClient.close occur error", e);
        }
        return url;
    }

}
