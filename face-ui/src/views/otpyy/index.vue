<template>
  <div class="container mx-auto my-8 flex flex-col items-center justify-center h-screen">
    <div class="text-center">
      <h1 class="text-3xl font-bold mb-4">第三方应用</h1>
      <el-button v-if="!isLoggedIn" type="primary" @click="login" class="mb-8">授权登录</el-button>
      <div v-if="isLoggedIn" class="mt-8">
        <h2 class="text-2xl font-bold mb-4">授权登录成功,用户信息如下:</h2>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="姓名">{{ userInfo.name }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ userInfo.phone }}</el-descriptions-item>
          <el-descriptions-item label="身份证号">{{ userInfo.idNo }}</el-descriptions-item>
          <el-descriptions-item label="人脸图像">
            <el-image
                :src="faceImageUrl"
                :preview-src-list="[faceImageUrl]"
                fit="contain"
                style="width: 200px; height: 200px; cursor: pointer;"
                @click="openImageDialog"
            >
              <div slot="error" class="image-slot">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </div>
    <el-dialog :visible.sync="imageDialogVisible" title="人脸图片预览">
      <el-image :src="faceImageUrl" fit="contain"></el-image>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'OtpYy',
  data() {
    return {
      userInfo: {
        name: '',
        phone: '',
        idNo: '',
        faceBase: ''
      },
      faceImageUrl: '',
      imageDialogVisible: false,
      isLoggedIn: false
    }
  },
  methods: {
    login() {
      const clientId = '233358';
      const redirectUri = 'http://localhost:8080/otpyy';
      window.location.href = `http://localhost:8868/oauth/authorize?client_id=${clientId}&redirect_uri=${redirectUri}`;
    },
    getToken(code) {
      const clientId = '233358';
      const clientSecret = 'M3nU1PFOxX';
      const redirectUri = 'http://localhost:8080/otpyy';

      axios.post('http://localhost:8868/oauth/token', null, {
        params: {
          grant_type: 'authorization_code',
          code: code,
          client_id: clientId,
          client_secret: clientSecret,
          redirect_uri: redirectUri
        }
      })
          .then(response => {
            this.getUserInfo(response.data.access_token);
          })
          .catch(error => {
            console.error(error);
          });
    },
    getUserInfo(accessToken) {
      const clientId = '233358';
      const code = this.$route.query.code;
      const fid = this.$route.query.fid;

      axios.get('http://localhost:8868/oauth/user_info', {
        params: {
          client_id: clientId,
          code: code,
          fid: fid
        },
        headers: {
          'Authorization': `Bearer ${accessToken}`
        }
      })
          .then(response => {
            this.userInfo.name = response.data.name;
            this.userInfo.phone = response.data.phone;
            this.userInfo.idNo = response.data.idNo;
            this.userInfo.faceBase = response.data.faceBase;
            this.faceImageUrl = this.convertBase64ToImageUrl(response.data.faceBase);
            this.isLoggedIn = true;
          })
          .catch(error => {
            console.error(error);
          });
    },
    convertBase64ToImageUrl(base64) {
      if (base64) {
        return `${base64}`;
      } else {
        return '';
      }
    },
    openImageDialog() {
      this.imageDialogVisible = true;
    }
  },
  created() {
    const code = this.$route.query.code;
    if (code) {
      this.getToken(code);
    }
  }
}
</script>

<style>
.container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.text-center {
  text-align: center;
}
</style>