<template>
  <div id="app">
    <h1>OTP Generator</h1>
    <p v-if="error">{{ error }}</p>
    <p v-else>
      OTP: {{ otp }}
      <br>
      Expiration: {{ expirationSeconds }} seconds
    </p>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'OtpPage',
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
</style>