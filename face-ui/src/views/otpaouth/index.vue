<template>
  <div class="flex justify-center items-center h-screen">
    <div class="w-full max-w-md">
      <el-card class="box-card p-8 shadow-lg">
        <div class="text-center mb-8">
          <span class="text-5xl font-bold">统一身份信息授权登录中心</span>
        </div>
        <el-form @submit.native.prevent="authorizeAndRedirect" class="form-center">
          <el-form-item label="用户 ID" label-width="100px" class="mb-6">
            <el-input v-model="fid" placeholder="请输入用户 ID" required></el-input>
          </el-form-item>
          <el-form-item label="动态口令" label-width="100px" class="mb-6">
            <el-input v-model="otp" placeholder="请输入动态口令" required></el-input>
          </el-form-item>
          <el-form-item class="mb-6">
            <div class="flex justify-center">
              <el-checkbox v-model="isHuman" class="mr-2">同意授权人脸、身份证号码、手机号、姓名等信息</el-checkbox>
            </div>
          </el-form-item>
          <el-form-item>
            <div class="text-center">
              <el-button type="primary" native-type="submit" @click="validateForm" :disabled="!isHuman" class="submit-button text-lg">
                <i class="el-icon-user-solid mr-2"></i>同意并授权
              </el-button>
            </div>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
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
      redirectUri: '',
      isHuman: false
    }
  },
  created() {
    this.clientId = this.$route.query.client_id;
    this.redirectUri = this.$route.query.redirect_uri;
    this.$http = axios;
  },
  methods: {
    validateForm() {
      // 在这里添加表单验证逻辑
      if (this.fid.trim() === '' || this.otp.trim() === '') {
        this.$message.error('请填写完整的用户 ID 和动态口令');
        return;
      }
      if (!this.isHuman) {
        this.$message.error('请确认您不是机器人');
        return;
      }
      this.authorizeAndRedirect();
    },
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

<style scoped>
.form-center {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.submit-button {
  width: 100%;
}
</style>