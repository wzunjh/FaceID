<!-- OtpYy.vue -->
<template>
  <div>
    <h1>第三方应用</h1>
    <button @click="login">登录</button>
    <div v-if="userInfo.name">
      <h2>用户信息</h2>
      <p>姓名: {{ userInfo.name }}</p>
      <p>手机号: {{ userInfo.phone }}</p>
      <p>身份证号: {{ userInfo.idNo }}</p>
      <img :src="faceImageUrl" alt="Face Image" />
    </div>
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
      faceImageUrl: ''
    }
  },
  methods: {
    login() {
      const clientId = '100086';
      const redirectUri = 'http://localhost:8080/otpyy';
      window.location.href = `http://localhost:8868/oauth/authorize?client_id=${clientId}&redirect_uri=${redirectUri}`;
    },
    getToken(code) {
      const clientId = '100086';
      const clientSecret = '100086';
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
            // 处理获取到的 access_token
            console.log(response.data);
            this.getUserInfo(response.data.access_token);
          })
          .catch(error => {
            console.error(error);
          });
    },
    getUserInfo(accessToken) {
      const clientId = '100086';
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
            // 更新用户信息
            this.userInfo.name = response.data.name;
            this.userInfo.phone = response.data.phone;
            this.userInfo.idNo = response.data.idNo;
            this.userInfo.faceBase = response.data.faceBase;

            // 将 faceBase 转换为图片 URL
            this.faceImageUrl = this.convertBase64ToImageUrl(response.data.faceBase);
          })
          .catch(error => {
            console.error(error);
          });
    },
    convertBase64ToImageUrl(base64) {
      return `data:image/jpeg;base64,${base64}`;
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