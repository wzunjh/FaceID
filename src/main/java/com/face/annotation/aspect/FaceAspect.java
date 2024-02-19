package com.face.annotation.aspect;


import com.face.bean.Face;
import com.face.bean.FaceVefLog;
import com.face.bean.result.FaceResult;
import com.face.service.FaceVefLogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author tanyongpeng
 * <p>方法执行后，添加日志</p>
 **/
@Component
@Aspect
public class FaceAspect {

    @Autowired
    FaceVefLogService faceVefLogService;

    @AfterReturning(value = "@annotation(com.face.annotation.FaceLog)",returning = "res")
    public void addLog(JoinPoint joinPoint ,Object res){
        FaceResult result = (FaceResult)res;
        FaceVefLog faceVefLog = new FaceVefLog();
        faceVefLog.setVefTime(new Date());
        faceVefLog.setVefCode(result.getCode());
        faceVefLog.setVefMsg(result.getMsg());
        faceVefLog.setLoginName(result.getName());
        faceVefLogService.save(faceVefLog);
    }


}
