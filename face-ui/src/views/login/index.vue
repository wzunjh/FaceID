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
          <el-input v-model="authToken" placeholder="请输入您的API令牌" style="flex-grow: 1; max-width: 400px;"></el-input>
          <el-button type="primary" @click="authLogin"><i class="el-icon-key"></i>一键登录</el-button>
          <el-button type="primary" @click="showSmsLoginModal = true">短信验证登录</el-button>
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

    <!-- SMS Login Modal -->
    <el-dialog title="短信验证登录" :visible.sync="showSmsLoginModal" width="30%">
      <div class="sms-login">
        <el-form :model="smsLoginForm" :rules="smsLoginRules" ref="smsLoginForm">
          <el-form-item label="手机号码" prop="phone">
            <el-input v-model="smsLoginForm.phone" placeholder="请输入手机号码"></el-input>
          </el-form-item>
          <el-form-item label="验证码" prop="code">
            <el-input v-model="smsLoginForm.code" placeholder="请输入验证码">
              <template slot="append">
                <el-button :disabled="smsCodeDisabled" @click="sendSmsCode">{{ smsCodeBtnText }}</el-button>
              </template>
            </el-input>
          </el-form-item>
        </el-form>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="showSmsLoginModal = false">取 消</el-button>
        <el-button type="primary" @click="smsLogin">确 定</el-button>
      </div>
    </el-dialog>

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
      authToken: '',
      clientIP: '',
      showSmsLoginModal: false,
      smsLoginForm: {
        phone: '',
        code: ''
      },
      smsLoginRules: {
        phone: [
          { required: true, message: '请输入手机号码', trigger: 'blur' },
          { pattern: /^1\d{10}$/, message: '手机号码格式不正确', trigger: 'blur' }
        ],
        code: [
          { required: true, message: '请输入验证码', trigger: 'blur' },
          { min: 6, max: 6, message: '验证码长度为6位', trigger: 'blur' }
        ]
      },
      smsCodeDisabled: false,
      smsCodeBtnText: '发送验证码'
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
    this.getClientIP();
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
      this.$http.get('/face/vef', { params: { AuthToken: this.authToken, ip: this.clientIP } }).then(res => {
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
    },
    sendSmsCode() {
      // 调用后端接口发送短信验证码
      this.$http.get('/api/sms', { params: { phone: this.smsLoginForm.phone } }).then(res => {
        if (res.data.code === 200) {
          this.$message.success(res.data.msg);
          this.startSmsCodeTimer();
        } else {
          this.$message.error(res.data.msg);
        }
      });
    },
    startSmsCodeTimer() {
      this.smsCodeDisabled = true;
      let seconds = 60;
      this.smsCodeBtnText = `${seconds}秒后重试`;
      const timer = setInterval(() => {
        seconds--;
        this.smsCodeBtnText = `${seconds}秒后重试`;
        if (seconds === 0) {
          clearInterval(timer);
          this.smsCodeDisabled = false;
          this.smsCodeBtnText = '发送验证码';
        }
      }, 1000);
    },
    smsLogin() {
      this.$refs.smsLoginForm.validate(valid => {
        if (valid) {
          this.$http.get('/api/smsvef', { params: { phone: this.smsLoginForm.phone, code: this.smsLoginForm.code } }).then(res => {
            if (res.data.code === 200) {
              this.$message.success(res.data.msg);
              this.showSmsLoginModal = false;
              // 保存登录信息并跳转到主页
              localStorage.setItem("face_token", res.data.token);
              localStorage.setItem("username", res.data.name);
              localStorage.setItem("user_id", res.data.fid);
              this.$router.push("/home");
            } else {
              this.$message.error(res.data.msg);
            }
          });
        }
      });
    },
    getClientIP() {
      fetch('https://api.ipify.org/?format=json')
          .then(response => response.json())
          .then(data => {
            this.clientIP = data.ip;
          })
          .catch(error => {
            console.error('Error fetching client IP:', error);
          });
    }
  },
};
</script>

<style>
@import "./index.css";
.sms-login {
  padding: 20px;
}
</style>