
<h1 align="center">Face-ID</h1>

Face-ID 基于前后端分离Web端项目，主要实现了网页版的人脸登录，通过调取前端摄像头拍照，传入后台进行跟数据库人脸库的相似度比对，技术的用点：Springboot，Mysql，JWT，InsightFace，OAuth2授权，VUE 2.X 等等技术实现，主要功能点：人脸列表CRUD，日志列表CRUD，基于自建人脸库通过base64编码方式存储人脸图片，通过调用基于InsightFace搭建的python人脸对比API场景实现。第三方授权方服务实现，OTP动态口令。


<img width="452" alt="image" src="https://github.com/wzunjh/FaceID/assets/120587094/08cef458-4f62-41fb-9312-b9ff0430c028">

<img width="575" alt="image" src="https://github.com/wzunjh/FaceID/assets/120587094/d4482d5a-1a91-400b-9253-6596266a88a1">

配置 IP 白名单列表，在管理界面中设置允许访问系统的 IP 地址；登录请求验证，获取用户 IP 地址；IP 地址验证，将用户 IP 地址与白名单列表对比允许或拒绝登录；登录记录，记录登录尝试信息包括登录时间、IP 地址、登录结果；安全提醒，拒绝登录请求可向管理员发送安全提醒，确保系统只有经授权的 IP 地址能访问。

<img width="614" alt="image" src="https://github.com/wzunjh/FaceID/assets/120587094/2faf6966-fc3c-4908-a87a-15a6c3b0a6f2">

当用户注册或登录账户时，绑定手机号码是非常重要的一步。为了确保用户信息的安全性和准确性，通常会采用短信验证码的方式来进行绑定。短信验证码是一种简单有效的验证方式，用户只需要输入收到的验证码即可完成手机绑定，同时也可以防止恶意攻击者通过虚假手机号码注册账户

![image](https://github.com/wzunjh/FaceID/assets/120587094/300eb909-12b8-4edc-abc2-5e99a7f015e5)

在上传身份证照片时，利用百度飞浆 OCR 文本识别模型来提取照片中的姓名和身份证号码。一旦成功提取，将请求阿里云身份证二要素核验数据库进行比对，并返回比对结果。这项操作有效地确保了用户身份信息的准确性和安全性，为账户注册和登录提供了可靠的保障

用户在完成多因素认证后，可以获得生成 APIKey 和 Auth 令牌的权限，这样就可以通过远程人脸对比接口进行调用，同时也可以利用页面端强大的在线识别功能进行数据库的比对或者双人脸比对。后台系统会记录用户的每一次调用时间和次数，以便于用户进行接口调用的管理和监控。这样的功能可以帮助用户更好地掌握接口调用情况，及时发现问题并及时解决，从而保障用户的正常业务运营

<img width="592" alt="image" src="https://github.com/wzunjh/FaceID/assets/120587094/b6de88b2-6c90-415f-84d2-521bad544eb1">

针对企业和个人的网站开发者，本身份认证系统提供了 OTP 动态口令校验和第三方授权登录功能，便于所有用户登录型网站进行授权登录，并获取在本系统认证通过以后的认证信息。极大提高了系统的外部连接性和用户扩展性。

![image](https://github.com/wzunjh/FaceID/assets/120587094/34bf1a2e-ab8b-41e5-9872-5ceb15534997)
