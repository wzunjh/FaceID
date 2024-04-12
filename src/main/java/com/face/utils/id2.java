package com.face.utils;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class id2 {

    public static void main(String[] args) throws IOException {

        String url = "https://idenauthen.market.alicloudapi.com/idenAuthentication";
        String appCode = "03f04ace87a44e548cfab16f04a9743c";

        String name = "聂家辉";
        String idNo = "432503200204098770";

        System.out.println(postData(appCode, url, name, idNo));
    }


    public static String postData(String appCode, String url, String name, String idNo) throws IOException {
        String result = "";
        RequestBody formBody = new FormBody.Builder().add("name", name).add("idNo", idNo).build();
        Request request = new Request.Builder().url(url).addHeader("Authorization", "APPCODE " + appCode).post(formBody).build();

        Call call = new OkHttpClient().newCall(request);
        Response response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            System.out.println("execute failed, message:" + e.getMessage());
        }

        assert response != null;
        if (!response.isSuccessful()) {
            System.out.println("request failed----" + "返回状态码" + response.code()  + ",message:" + response.message());
        }
        assert response.body() != null;
        result = response.body().string();

        return result;
    }
}
