<template>
  <div>
    <div class="login">
    </div>

    <div class="login-mid">
      <div class="login-mid-top">
        <div class="shadow-top-left"></div>
        <div class="shadow-top-right"></div>
      </div>
      <div class="login-mid-mid">

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
          <div v-if="faceImgState" class="face-special-effects-2"></div>
          <div v-else class="face-special-effects"></div>
        </div>

        <div class="face-btn">
          <button @click="faceVef()">{{ faceImgState ? '正在识别中...' : '开始识别' }}</button>
        </div>

        <!-- Auth Token Input Section -->
        <div class="auth-token" style="display: flex; justify-content: center; align-items: center; gap: 20px;">
          <el-input v-model="authToken" placeholder="请输入您的Auth令牌" style="flex-grow: 1; max-width: 400px;"></el-input>
          <el-button type="primary" @click="authLogin"><i class="el-icon-key"></i>一键登录</el-button>
        </div>




        <div class="msg">
          <div class="server-msg">{{ msg }}</div>
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
      msg: '',
      faceImgState: false,
      faceOption: {},
      authToken: ''  // Auth token data property
    };
  },
  mounted() {
    this.faceOption = $camera.getCamera({
      videoWidth: 200,
      videoHeight: 200,
      thisCancas: null,
      thisContext: null,
      thisVideo: null,
      canvasId: 'canvasCamera',
      videoId: 'videoCamera'
    });
  },
  methods: {
    faceVef() {
      let imageBase = $camera.draw(this.faceOption);
      if (this.faceImgState) {
        return;
      }
      this.faceImgState = true;
      if (imageBase === '' || imageBase === null || imageBase === undefined) {
        this.$message.error("图片数据为空");
      } else {
        this.$http.post("/face/vef", {imageBase}).then(res => {
          console.log(res);
          this.faceImgState = false;
          if (res.data.code === 200) {
            this.faceOption.thisVideo.srcObject.getTracks()[0].stop();
            localStorage.setItem("face_token", res.data.token);
            localStorage.setItem("username", res.data.name);
            localStorage.setItem("user_id", res.data.fid);
            this.$message.success(res.data.msg);
            this.$router.push("/home");
          }
          if (res.data.code === 201) {
            this.$message.success(res.data.msg);
          }
        }, error => {
          this.faceImgState = false;
          this.$message.error("识别失败，请重试");
        });
      }
    },
    authLogin() {
      this.$http.get('/face/vef', { params: { AuthToken: this.authToken } }).then(res => {
        if (res.data.code === 200) {
          localStorage.setItem("face_token", res.data.token);
          localStorage.setItem("username", res.data.name);
          localStorage.setItem("user_id", res.data.fid);
          this.$message.success(res.data.msg);
          this.$router.push("/home");
        } else {
          this.$message.error(res.data.msg);
        }
      });
    }
  },
};
</script>
<style>
@import "./index.css";
</style>