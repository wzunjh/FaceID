package com.face.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;

/**
 * Utility class for sending SMS using Aliyun (Alibaba Cloud SMS Service).
 */
public class SmsUtils {

    private static IAcsClient initializeClient() {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", );
        return new DefaultAcsClient(profile);
    }

    private static SendSmsRequest createSmsRequest(String phone, String templateCode, String templateParam) {
        SendSmsRequest request = new SendSmsRequest();
        request.setSignName("智通全");
        request.setTemplateCode(templateCode);
        request.setPhoneNumbers(phone);
        request.setTemplateParam(templateParam);
        return request;
    }

    private static void printSmsResponse(SendSmsRequest request, IAcsClient client) {
        System.out.println("阿里云短信服务response信息:");
        try {
            SendSmsResponse response = client.getAcsResponse(request);
            System.out.println(new Gson().toJson(response));
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
        }
    }

    public static void sendSms(String phone, String code) {
        IAcsClient client = initializeClient();
        SendSmsRequest request = createSmsRequest(phone, "SMS_465424620", "{\"code\":\"" + code + "\"}");
        printSmsResponse(request, client);
    }

    public static void ApiKey(String phone) {
        IAcsClient client = initializeClient();
        SendSmsRequest request = createSmsRequest(phone, "SMS_465580443", null);
        printSmsResponse(request, client);
    }

    public static void id2Auth(String phone) {
        IAcsClient client = initializeClient();
        SendSmsRequest request = createSmsRequest(phone, "SMS_465424605", null);
        printSmsResponse(request, client);
    }
}
