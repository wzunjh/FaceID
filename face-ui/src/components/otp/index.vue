<template>
  <div id="app">
    <h1 style="font-size: 48px;">OTP Generator</h1>
    <p v-if="error" style="font-size: 24px;">{{ error }}</p>
    <div v-else class="otp-container">
      <div class="otp-digits">
        <div class="otp-digit" v-for="digit in otp.split('')" :key="digit" style="font-size: 48px; width: 80px; height: 80px; line-height: 80px;">{{ digit }}</div>
      </div>
      <div class="countdown">
        <el-progress :percentage="(expirationSeconds / 30) * 100" :show-text="false" :stroke-width="24" color="#67C23A" />
        <div class="countdown-text" style="font-size: 24px; font-weight: bold; color: #67C23A;">剩余有效时间: {{ expirationSeconds }} seconds</div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { Progress } from 'element-ui';

export default {
  name: 'OtpPage',
  components: {
    'el-progress': Progress
  },
  data() {
    return {
      otp: '',
      expirationSeconds: 0,
      intervalId: null,
      error: null
    }
  },
  mounted() {
    this.fetchOtp();
    this.intervalId = setInterval(this.fetchOtp, 1000);
  },
  beforeDestroy() {
    clearInterval(this.intervalId);
  },
  methods: {
    fetchOtp() {
      const fid = localStorage.getItem('user_id');
      this.$http.get(`/mfa/otp/${fid}`, {
        params: {}
      })
          .then(response => {
            this.otp = response.data.otp;
            this.expirationSeconds = response.data.expirationSeconds;
            this.error = null;
          })
          .catch(error => {
            console.error(error);
            this.error = error.response.data.msg;
          });
    }
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}

.otp-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 40px;
}

.otp-digits {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.otp-digit {
  font-size: 36px;
  font-weight: bold;
  width: 50px;
  height: 50px;
  line-height: 50px;
  border: 1px solid #ccc;
  border-radius: 4px;
  margin: 0 5px;
}

.countdown {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.countdown-text {
  margin-top: 10px;
  font-size: 18px;
  font-weight: bold;
}
</style>