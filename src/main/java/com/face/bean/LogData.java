package com.face.bean;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class LogData {

    @ExcelProperty("用户ID")
    private Integer fid;
    @ExcelProperty("用户名称")
    private String faceName;
    @ExcelProperty("接口调用次数")
    private Integer apiNum;
    @ExcelProperty("接口调用频率")
    private Integer apiMin;
    @ExcelProperty("请求时间")
    private Date apiTime;
    @ExcelProperty("接口响应结果")
    private String apiMsg;
    @ExcelProperty("数据导出时间")
    private Date date;
    /**
     * 忽略这个字段
     */
    @ExcelIgnore
    private String ignore;
}