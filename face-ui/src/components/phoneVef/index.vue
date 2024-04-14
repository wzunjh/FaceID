<template>
  <div>
    <el-card v-if="!phoneBound" header="手机核验绑定">
      <el-form ref="phoneForm" :model="phoneAuthForm" label-width="120px">
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="phoneAuthForm.phone" clearable></el-input>
        </el-form-item>
        <el-form-item label="验证码" prop="code">
          <el-input v-model="phoneAuthForm.code" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitPhoneForm('phoneForm')">提交核验</el-button>
          <el-button @click="sendCode">发送验证码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <div v-else class="success-message">
      <el-alert title="手机号绑定成功" type="success" center show-icon />
      <el-descriptions title="您的核验信息">
        <el-descriptions-item label="用户ID">{{ phoneData.fid }}</el-descriptions-item>
        <el-descriptions-item label="绑定手机号码">{{ phoneData.phone }}</el-descriptions-item>
      </el-descriptions>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      phoneAuthForm: {
        phone: '',
        code: '',
        fid: ''
      },
      phoneBound: false,
      phoneData: { fid: '', phone: '' }
    };
  },
  mounted() {
    this.phoneAuthForm.fid = localStorage.getItem('user_id');
    this.checkPhoneBinding();
  },
  methods: {
    checkPhoneBinding() {
      this.$http.get(`/face/SmsVef/${this.phoneAuthForm.fid}`).then(response => {
        if (response.data.code === 200) {
          this.phoneBound = true;
          this.phoneData.phone = response.data.phone;
        }
      }).catch(error => {
        console.error('Error checking phone binding:', error);
      });
    },
    sendCode() {
      this.$http.get('/face/Sms', { params: { phone: this.phoneAuthForm.phone } })
          .then(response => {
            alert(response.data.msg);
          }).catch(error => {
        console.error('Failed to send code:', error);
      });
    },
    submitPhoneForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.$http.get('/face/SmsVef', {
            params: {
              fid: this.phoneAuthForm.fid,
              phone: this.phoneAuthForm.phone,
              code: this.phoneAuthForm.code
            }
          }).then(response => {
            if (response.data.code === 200) {
              this.phoneBound = true;
              this.checkPhoneBinding();
            } else {
              alert(response.data.msg);
            }
          }).catch(error => {
            console.error('Phone verification failed:', error);
          });
        } else {
          return false;
        }
      });
    }
  }
};
</script>

<style scoped>
.center-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.success-message {
  color: green;
  padding: 20px;
  border: 1px solid #e0e0e0;
  border-radius: 5px;
  text-align: center;
}

.success-message p {
  margin: 0;
}
</style>
