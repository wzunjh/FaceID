package com.face;


import cn.hutool.extra.qrcode.QrCodeUtil;
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
import io.minio.http.Method;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class EasyExcelTests {

    @Autowired
    FaceService faceService;

    @Autowired
    ApiLogService apiLogService;

    @Resource
    private MinioClient minioClient;

    @Autowired
    ExcelService excelService;

    String PATH = "D:\\easyexcel\\";

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


    @Test
    public void simpleWrite() {
        // 写法2
        String fileName = PATH + "LogData.xlsx";
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        // 如果这里想使用03 则 传入excelType参数即可
        EasyExcel.write(fileName, LogData.class).sheet("API请求日志").doWrite(data(1));
    }

    @Test
    public void minioWrite() throws Exception {
        // 使用 EasyExcel 将数据写入 ByteArrayOutputStream 并上传到 MinIO
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            EasyExcel.write(outputStream, LogData.class)
                    .sheet("API请求日志")
                    .doWrite(data(1));

            String objectName = "LogData" + 1 + ".xlsx";
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket("excel")
                    .object(objectName)
                    .stream(new ByteArrayInputStream(outputStream.toByteArray()), outputStream.size(), -1)
                    .build());
            String objectUrl = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket("excel")
                    .object(objectName)
                    .expiry(1, TimeUnit.DAYS)
                    .method(Method.GET)
                    .build()
            );
            System.out.println(objectUrl);
        }
    }

    @Test
    public void test007(){

        String url = excelService.minioExcel(1);
        System.out.println(url);
    }

    @Test
    public void hutool001(){
        QrCodeUtil.generate("http://www.9fai.com",500,500,new File("C://Users//27877//Desktop//qr.jpg"));
    }

}
