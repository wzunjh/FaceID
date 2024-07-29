package com.face.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.ListUtils;
import com.face.bean.ApiLog;
import com.face.bean.Face;
import com.face.bean.LogData;
import com.face.service.ApiLogService;
import com.face.service.ExcelService;
import com.face.service.FaceService;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ExcelServiceImpl implements ExcelService {

    @Autowired
    FaceService faceService;

    @Autowired
    ApiLogService apiLogService;

    @Resource
    private MinioClient minioClient;


    @Override
    public String minioExcel(Integer fid) {
        // 使用 EasyExcel 将数据写入 ByteArrayOutputStream 并上传到 MinIO
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            EasyExcel.write(outputStream, LogData.class)
                    .sheet("API请求日志")
                    .doWrite(data(fid));

            String objectName = "LogData" + fid + ".xlsx";
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket("excel")
                    .object(objectName)
                    .stream(new ByteArrayInputStream(outputStream.toByteArray()), outputStream.size(), -1)
                    .build());
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket("excel")
                    .object(objectName)
                    .expiry(1, TimeUnit.DAYS)
                    .method(Method.GET)
                    .build()
            );
        } catch (IOException | ErrorResponseException | InsufficientDataException | InternalException |
                 InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new RuntimeException(e);
        }

    }



    private List<LogData> data(Integer fid) {
        List<LogData> list = ListUtils.newArrayList();
        Face face = faceService.getById(fid);
        List<ApiLog> Apilist = apiLogService.lambdaQuery().eq(ApiLog::getFid, face.getFid()).list();
        for (ApiLog apiLog : Apilist) {
            LogData data = new LogData();
            data.setFid(face.getFid());
            data.setFaceName(face.getFaceName());
            data.setApiNum(face.getApiNum());
            data.setApiMin(face.getApiMin());
            data.setApiTime(apiLog.getApiTime());
            data.setApiMsg(apiLog.getApiMsg());
            data.setDate(new Date());
            list.add(data);
        }
        return list;
    }


}
