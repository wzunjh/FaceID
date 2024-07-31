package com.face;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.face.bean.Goods;
import com.face.bean.result.ApiResult;
import com.face.bean.result.OtpResult;
import com.face.server.IdAuthenticationServer;
import com.face.service.*;
import com.face.service.impl.OTPService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.face.bean.result.FaceResult;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class FaceEasyApplicationTests {

    @Autowired
    private IdAuthenticationServer idAuthenticationServer;

    @Autowired
    FaceService faceService;

    @Autowired
    ApiLogService apiLogService;

    @Autowired
    OTPService otpService;

    @Autowired
    OauthClientService  oauthClientService;

    @Autowired
    MyFaceDbService myFaceDbService;

    @Resource
    private MinioClient minioClient;

    @Autowired
    private AliPayService aliPayService;

    @Autowired
    private GoodsService goodsService;


    @Test
    void contextLoads() {
    }

    @Test
    public void testAuthenticateId() {
        // Mock data for testing
        String faceName = "聂家辉";
        String idNo = "432503200204098770";

        // Call the method to be tested
        FaceResult faceResult = idAuthenticationServer.authenticateId(faceName, idNo);

        // Check if the result is not null
        assertNotNull(faceResult);

        // Check if code and message are properly set
        System.out.println("Code: " + faceResult.getCode());
        System.out.println("Message: " + faceResult.getMsg());

    }

    @Test
    public void testAuthId() {

        // 准备测试数据
        Integer fid = 1;
        String idNo = "432503200204098770";

        // 调用待测试的方法
        FaceResult result = faceService.authId(fid, idNo);

        // 输出结果
        System.out.println("Code: " + result.getCode());
        System.out.println("Msg: " + result.getMsg());
    }

    @Test
    public void testApi(){

        FaceResult faceResult = faceService.apiKey(1);
        System.out.println(faceResult.getApiTime());
        System.out.println("apiKey: " + faceResult.getApiKey());
        System.out.println("Msg: " + faceResult.getMsg());
    }

    @Test
    public void testUpdateApi(){

        FaceResult faceResult = faceService.updateApiKey(1);

        System.out.println("apiKey: " + faceResult.getApiKey());
        System.out.println("Msg: " + faceResult.getMsg());
    }


    @Test
    public void vufAuth(){

        String Auth = "Auth-*****************************";
        Integer fid = 1;

        ApiResult apiResult = faceService.vefAuth(Auth,fid);
        System.out.println(apiResult.getCode()+apiResult.getMsg());
    }

    @Test
    public void Otp(){

        Integer fid = 1;
        OtpResult otpResult = otpService.generateOTP(String.valueOf(1));
        System.out.println(otpResult.getExpirationSeconds()+otpResult.getOtp());
    }

    @Test
    public void OtpTest(){

        Integer fid = 1;
        oauthClientService.getById(1);

    }

    @Test
    public void testMyDb(){
        myFaceDbService.getById(1);
    }

//-----------------------------------------------minio基本操作测试---


    //	检测是否存在文件夹，返回类型为bool
    @Test
    void test01() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        boolean isexists = minioClient.bucketExists(BucketExistsArgs.builder().bucket("myfile").build());
        System.out.println("文件夹是否存在："+isexists);
    }

    //	新建一个bucket
    @Test
    void test02() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.makeBucket(MakeBucketArgs.builder().bucket("myfile").build());
    }


//	列出当前用户有权限的所有bucket

    @Test
    void test03() throws Exception{
        List<Bucket> buckets = minioClient.listBuckets();
        buckets.forEach(bucket ->{
            System.out.println(bucket.name()+"---"+bucket.creationDate());
        });
    }

    //	删除指定的bucket
    @Test
    void test04() throws Exception{
        minioClient.removeBucket(RemoveBucketArgs.builder().bucket("myfile").build());
    }


//	----------------------object操作-------------------------

    //文件对象的创建和上传(老方法，较为麻烦不用)
    @Test
    void test05() throws Exception{
        File file = new File("C:\\Users\\27877\\Pictures\\574.jpg");
        ObjectWriteResponse objectWriteResponse = minioClient.putObject(PutObjectArgs.builder()
                .bucket("myfile")
                .object("test.jpg")
                .stream(new FileInputStream(file), file.length(), -1)
                .build()
        );
        System.out.println(objectWriteResponse);
    }


    //文件对象的创建和上传(新方法，常用简单)
    @Test
    void test06() throws Exception{
        ObjectWriteResponse objectWriteResponse = minioClient.uploadObject(UploadObjectArgs.builder()
                .bucket("myfile")
                .object("up1.jpg")
                .filename("C:\\Users\\27877\\Pictures\\574.jpg")
                .build()
        );
        System.out.println(objectWriteResponse);
    }

    //查询某个文件对象是否存在
    @Test
    void test07() throws Exception{
        StatObjectResponse statObjectResponse = minioClient.statObject(StatObjectArgs.builder()
                .bucket("myfile")
                .object("up1.jpg")
                .build()
        );
        System.out.println(statObjectResponse);

    }

    //为某个文件生成签名访问路径
    @Test
    void test08() throws Exception{
        String objectUrl = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .bucket("myfile")
                .object("up1.jpg")
                .expiry(3, TimeUnit.DAYS)
                .method(Method.GET)
                .build()
        );
        System.out.println(objectUrl);
//		如果bucket的权限是公共的就不需要后面的签名信息可以直接访问
    }

    //下载某个文件导出到本地路径
    @Test
    void test09() throws Exception{
        GetObjectResponse minioClientObject = minioClient.getObject(GetObjectArgs.builder()
                .bucket("myfile")
                .object("up1.jpg")
                .build()
        );
        System.out.println(minioClientObject.transferTo(new FileOutputStream("D:\\minio\\data\\1.jpg")));
    }

    //查询某个bucket下所有文件对象
    @Test
    void test10() {
        Iterable<Result<Item>> objects = minioClient.listObjects(ListObjectsArgs.builder()
                .bucket("myfile")
                .build()
        );
        objects.forEach(object ->{
            Item item;
            try {
                item = object.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            System.out.println(item.objectName());
        });
    }

    //删除某个bucket中指定文件对象
    @Test
    void test11() throws Exception{
        minioClient.removeObject(RemoveObjectArgs.builder().bucket("myfile").object("up1.jpg").build());
    }


    @Test
    void testPay001() throws Exception {
        String qrCode = aliPayService.createPayQRCode("iphone15", "92313377", "12999");
        System.out.println(qrCode);
    }

    @Test
    void testGoods(){

        LambdaQueryChainWrapper<Goods> eq = goodsService.lambdaQuery().eq(Goods::getGoodsId, 1001);
        System.out.println(eq);

    }

}
