package com.face;

import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class MinioTests {

    @Resource
    private MinioClient minioClient;

    //	检测是否存在文件夹，返回类型为bool
    @Test
    void test01() throws Exception{
        boolean isexists = minioClient.bucketExists(BucketExistsArgs.builder().bucket("myfile").build());
        System.out.println("文件夹是否存在："+isexists);
    }

    //	新建一个bucket
    @Test
    void test02() throws Exception{
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


}
