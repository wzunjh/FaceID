<template>
  <div class="desktop">
    <div class="desktop-main"></div>
    <el-steps :active="activeStep" align-center>
      <el-step title="人脸信息完善" :description="stepDescriptions[0]" :status="stepStatus[0]"></el-step>
      <el-step title="手机号绑定" :description="stepDescriptions[1]" :status="stepStatus[1]"></el-step>
      <el-step title="身份证号码核验" :description="stepDescriptions[2]" :status="stepStatus[2]"></el-step>
      <el-step title="Auth令牌创建" :description="stepDescriptions[3]" :status="stepStatus[3]"></el-step>
    </el-steps>
    <div class="author-title">
      <p>基于InsightFace与SpringBoot的身份认证系统</p>
      <p>本系统由<a href="https://github.com/wzunjh">家辉酱</a>全栈开发</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: "index",
  data() {
    return {
      activeStep: 0,
      stepDescriptions: [
        '通过完善人脸和姓名等信息，才能获得身份证核验的权限',
        '通过绑定您的手机号码，获得更多的令牌动态信息',
        '通过身份证号码核验，才能创建您的Auth令牌',
        '创建并更新您的令牌，利用它更加便捷的使用API'
      ],
      stepStatus: ['wait', 'wait', 'wait', 'wait']
    };
  },
  methods: {
    async checkApiStatus() {
      const userId = localStorage.getItem('user_id');
      const faceToken = localStorage.getItem('face_token'); // 获取Face_token
      const urls = [
        `/face/info/${userId}`,
        `/face/SmsC/${userId}`,
        `/face/orAuth/${userId}`,
        `/face/apiKey/${userId}`
      ];
      const config = {
        headers: {
          'Face_token': faceToken // 设置请求头
        }
      };

      for (let index = 0; index < urls.length; index++) {
        try {
          const response = await axios.get(urls[index], config);
          if (response.data.code === 200) {
            this.stepStatus[index] = 'finish';
          } else {
            this.stepStatus[index] = 'error';
          }
          this.activeStep = index + 1; // Progress the step visually
        } catch (error) {
          this.stepStatus[index] = 'error';
        }
      }
    }
  },
  created() {
    this.checkApiStatus();
  }
}
</script>

<style>
.desktop {
  width: 100%;
  height: 100%;
  position: relative;
}
.desktop-main {
  position: absolute;
  width: 45%;
  height: 63%;
  transform: translate(-50%, -50%);
  left: 50%;
  top: 40%;
  background: url(~@/assets/desktop/desktop.gif) no-repeat;
  background-size: 100% 100%;
}
.author-title {
  width: 100%;
  height: 98px;
  background-color: #008390;
  position: absolute;
  transform: translate(-50%, -50%);
  top: 95%;
  left: 50%;
  text-align: center;
  font-size: 13px;
  color: #fff;
}
</style>
