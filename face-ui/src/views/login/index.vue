<template>
  <div>
    <div class="login">
    </div>


    <!--登录中间块-->
    <div class="login-mid">
      <div class="login-mid-top">
        <div class="shadow-top-left"></div>
        <div class="shadow-top-right"></div>
      </div>
      <div class="login-mid-mid">

        <!--捕获人脸区域-->
        <div class="videoCamera-canvasCamera">
              <video
                  id="videoCamera"
                  :width="videoWidth"
                  :height="videoHeight"
                  autoplay
              ></video>
              <canvas
                  style="display: none"
                  id="canvasCamera"
                  :width="videoWidth"
                  :height="videoHeight"
              ></canvas>

          <!--人脸特效区域-->
          <div v-if="faceImgState" class="face-special-effects-2"></div>
          <div v-else class="face-special-effects"></div>

        </div>

        <!--按钮区域-->
        <div class="face-btn">
          <button @click="faceVef()">{{faceImgState?'正在识别中...':'开始识别'}}</button>
        </div>

        <!--消息区域-->
        <div class="msg">
            <div class="server-msg">{{msg}}</div>
            <div class="welcome">欢迎使用FaceID身份认证系统</div>
        </div>

      </div>
      <div class="login-mid-bot">
        <div class="shadow-bot-left"></div>
        <div class="shadow-bot-right"></div>
      </div>
    </div>


  </div>

</template>
<script>
import $camera from '../../camera/index.js'
export default {
  data() {
    return {
      videoWidth: 200,
      videoHeight: 200,
      msg:'',
      faceImgState:false,
      faceOption:{}
    };
  },
  mounted() {
    //调用摄像头
    this.faceOption = $camera.getCamera({
      videoWidth: 200,
      videoHeight: 200,
      thisCancas: null,
      thisContext: null,
      thisVideo: null,
      canvasId:'canvasCamera',
      videoId:'videoCamera'
    });

    //this.getCompetence();
  },
  methods: {
    // 调用后台接口
    faceVef(){
      // 开始绘制图片
      let imageBase = $camera.draw(this.faceOption)
      if (this.faceImgState){
        return
      }
      this.faceImgState = true
      if (imageBase === '' || imageBase === null || imageBase === undefined){
        this.$message.error("图片数据为空")
      }else {
        this.$http.post("/face/vef",{imageBase}).then(res =>{
          console.log(res)
          this.faceImgState = false
          // 跳转首页
          if (res.data.code === 200){
            // 关闭摄像头
            this.faceOption.thisVideo.srcObject.getTracks()[0].stop();
            localStorage.setItem("face_token",res.data.token);
            localStorage.setItem("username",res.data.name);
            this.$message.success(res.data.msg)
            this.$router.push("/home")
          }
          if (res.data.code === 201){
            this.$message.success(res.data.msg)
          }
        },onerror =>{
          this.faceImgState = false
        })
      }
    }
  },
};
</script>
<style>
@import "./index.css";
</style>