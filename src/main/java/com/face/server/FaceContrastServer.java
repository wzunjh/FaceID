package com.face.server;

import com.face.bean.result.FaceResult;
import com.face.utils.TimeUtils;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.iai.v20180301.IaiClient;
import com.tencentcloudapi.iai.v20180301.models.CompareFaceRequest;
import com.tencentcloudapi.iai.v20180301.models.CompareFaceResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tanyongpeng
 * <p>调用腾讯接口</p>
 **/
@Component
@Data
@Slf4j
public class FaceContrastServer {

    @Value("${tencentcloudapi.secretId}")
    private String secretId;
    @Value("${tencentcloudapi.secretKey}")
    private String secretKey;
    @Value("${tencentcloudapi.endpoint}")
    private String endpoint;
    @Value("${tencentcloudapi.region}")
    private String region;

    public FaceResult faceContrast(String imageA, String imageB){
        FaceResult faceResult = new FaceResult();
        try{
            Credential cred = new Credential(secretId, secretKey);
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint(endpoint);
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            IaiClient client = new IaiClient(cred, region, clientProfile);
            CompareFaceRequest req = new CompareFaceRequest();
            req.setImageA(imageA);
            req.setImageB(imageB);
            CompareFaceResponse resp = client.CompareFace(req);
            faceResult.setScore(resp.getScore());
            faceResult.setCode(FaceResult.SUCCESS_CODE);
        } catch (TencentCloudSDKException e) {
            faceResult.setCode(FaceResult.FACE_ERROR);
            faceResult.setMsg(e.getMessage());
        }
        return faceResult;
    }
}
