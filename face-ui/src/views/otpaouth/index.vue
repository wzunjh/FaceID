<template>
  <div class="flex justify-center items-center h-screen">
    <el-card class="box-card w-full max-w-md">
      <div slot="header" class="clearfix">
        <span class="text-2xl font-bold">授权登录</span>
      </div>
      <el-form @submit.native.prevent="authorizeAndRedirect">
        <el-form-item label="用户 ID" label-width="100px">
          <el-input v-model="fid" placeholder="请输入用户 ID" required></el-input>
        </el-form-item>
        <el-form-item label="动态口令" label-width="100px">
          <el-input v-model="otp" placeholder="请输入动态口令" required></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit">登录并授权</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'AuthorizationPage',
  data() {
    return {
      fid: '',
      otp: '',
      clientId: '',
      redirectUri: ''
    }
  },
  created() {
    this.clientId = this.$route.query.client_id;
    this.redirectUri = this.$route.query.redirect_uri;
    this.$http = axios;
  },
  methods: {
    authorizeAndRedirect() {
      this.$http.post('/oauth/authorize', null, {
        params: {
          client_id: this.clientId,
          redirect_uri: this.redirectUri,
          fid: this.fid,
          otp: this.otp
        }
      })
          .then(response => {
            window.location.href = response.data;
          })
          .catch(error => {
            if (error.response && error.response.data && error.response.data.error) {
              this.$message.error(`Error: ${error.response.data.error}`);
            } else {
              console.error(error);
            }
          });
    }
  }
}
</script>