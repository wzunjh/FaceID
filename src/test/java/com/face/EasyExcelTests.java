package com.face;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.ListUtils;
import com.face.bean.ApiLog;
import com.face.bean.Face;
import com.face.bean.LogData;
import com.face.service.ApiLogService;
import com.face.service.FaceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
class EasyExcelTests {

    @Autowired
    FaceService faceService;

    @Autowired
    ApiLogService apiLogService;

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
}
