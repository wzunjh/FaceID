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
          <el-button @click="sendCode('phone')" :disabled="phoneCountdown > 0">发送验证码{{phoneCountdown ? `(${phoneCountdown}s)` : ''}}</el-button>
          <el-button type="primary" @click="submitPhoneForm('phoneForm')">提交核验</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-else-if="!modifyPhoneVisible" header="手机号绑定成功" class="mb-4">
      <el-alert title="手机号绑定成功" type="success" center show-icon />
      <el-descriptions title="您的绑定信息">
        <el-descriptions-item label="用户ID">{{ phoneData.fid }}</el-descriptions-item>
        <el-descriptions-item label="绑定手机号码">{{ phoneData.phone }}</el-descriptions-item>
        <el-descriptions-item label="绑定状态">
          <el-tag size="small">已绑定</el-tag>
        </el-descriptions-item>
      </el-descriptions>
      <div class="d-flex justify-content-center">
        <el-button type="primary" @click="showModifyPhoneForm">修改手机号</el-button>
      </div>
    </el-card>

    <el-card v-else-if="modifyPhoneStep === 1" header="修改手机号绑定">
      <el-form ref="modifyPhoneForm" :model="modifyPhoneForm" label-width="120px">
        <el-form-item label="原手机号码" prop="oldPhone">
          <el-input v-model="modifyPhoneForm.oldPhone" clearable disabled></el-input>
        </el-form-item>
        <el-form-item label="验证码" prop="oldCode">
          <el-input v-model="modifyPhoneForm.oldCode" clearable></el-input>
          <el-button @click="sendCode('old')" :disabled="oldPhoneCountdown > 0">发送验证码{{oldPhoneCountdown ? `(${oldPhoneCountdown}s)` : ''}}</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitOldPhoneForm('modifyPhoneForm')">提交验证</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-else-if="modifyPhoneStep === 2" header="修改手机号绑定">
      <el-form ref="modifyPhoneForm" :model="modifyPhoneForm" label-width="120px">
        <el-form-item label="新手机号码" prop="newPhone">
          <el-input v-model="modifyPhoneForm.newPhone" clearable></el-input>
        </el-form-item>
        <el-form-item label="验证码" prop="newCode">
          <el-input v-model="modifyPhoneForm.newCode" clearable></el-input>
          <el-button @click="sendCode('new')" :disabled="newPhoneCountdown > 0">发送验证码{{newPhoneCountdown ? `(${newPhoneCountdown}s)` : ''}}</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitNewPhoneForm('modifyPhoneForm')">提交修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
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
      modifyPhoneForm: {
        oldPhone: '',
        oldCode: '',
        newPhone: '',
        newCode: ''
      },
      phoneBound: false,
      phoneData: { fid: '', phone: '' },
      phoneCountdown: 0,
      oldPhoneCountdown: 0,
      newPhoneCountdown: 0,
      modifyPhoneVisible: false,
      modifyPhoneStep: 1
    };
  },
  mounted() {
    this.phoneAuthForm.fid = localStorage.getItem('user_id');
    this.checkPhoneBinding();
  },
  methods: {
    checkPhoneBinding() {
      this.$http.get(`/face/SmsC/${this.phoneAuthForm.fid}`).then(response => {
        if (response.data.code === 200) {
          this.phoneBound = true;
          this.phoneData.phone = response.data.phone;
          this.phoneData.fid = response.data.fid;
          this.modifyPhoneForm.oldPhone = response.data.phone;
        }
      }).catch(error => {
        console.error('Error checking phone binding:', error);
      });
    },
    sendCode(type) {
      let countdown = 0;
      switch (type) {
        case 'phone':
          countdown = this.phoneCountdown;
          break;
        case 'old':
          countdown = this.oldPhoneCountdown;
          break;
        case 'new':
          countdown = this.newPhoneCountdown;
          break;
      }
      if (countdown > 0) return;
      countdown = 60;
      let interval = setInterval(() => {
        countdown--;
        if (countdown === 0) clearInterval(interval);
        switch (type) {
          case 'phone':
            this.phoneCountdown = countdown;
            break;
          case 'old':
            this.oldPhoneCountdown = countdown;
            break;
          case 'new':
            this.newPhoneCountdown = countdown;
            break;
        }
      }, 1000);

      let phone = '';
      switch (type) {
        case 'phone':
          phone = this.phoneAuthForm.phone;
          break;
        case 'old':
          phone = this.modifyPhoneForm.oldPhone;
          break;
        case 'new':
          if (!/^1\d{10}$/.test(this.modifyPhoneForm.newPhone)) {
            alert('非法手机号');
            return;
          }
          phone = this.modifyPhoneForm.newPhone;
          break;
      }
      this.$http.get('/face/Sms', { params: { phone: phone } })
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
    },
    showModifyPhoneForm() {
      this.modifyPhoneVisible = true;
      this.modifyPhoneStep = 1;
    },
    submitOldPhoneForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.$http.get('/face/SmsVef', {
            params: {
              fid: this.phoneAuthForm.fid,
              phone: this.modifyPhoneForm.oldPhone,
              code: this.modifyPhoneForm.oldCode
            }
          }).then(response => {
            if (response.data.code === 200) {
              this.modifyPhoneStep = 2;
            } else {
              alert(response.data.msg);
            }
          }).catch(error => {
            console.error('Old phone verification failed:', error);
          });
        } else {
          return false;
        }
      });
    },
    submitNewPhoneForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.$http.get('/face/SmsVef', {
            params: {
              fid: this.phoneAuthForm.fid,
              phone: this.modifyPhoneForm.newPhone,
              code: this.modifyPhoneForm.newCode
            }
          }).then(response => {
            if (response.data.code === 200) {
              this.phoneBound = true;
              this.checkPhoneBinding();
              this.modifyPhoneVisible = false;
              this.modifyPhoneStep = 1;
            } else {
              alert(response.data.msg);
            }
          }).catch(error => {
            console.error('New phone verification failed:', error);
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

.mb-4 {
  margin-bottom: 1.5rem;
}

.d-flex {
  display: flex;
}

.justify-content-center {
  justify-content: center;
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