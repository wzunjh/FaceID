<template>
  <div>
    <el-card header="API信息中心">
      <el-descriptions title="接口请求地址" border>
        <el-descriptions-item label="URL">{{ apiBaseUrl }}</el-descriptions-item>
        <el-descriptions-item label="请求方式">post</el-descriptions-item>
      </el-descriptions>

      <div class="api-key-container">
        <el-descriptions title="API Key 信息 (请妥善保管好,必要时及时修改或者设置IP白名单)" border v-if="apiKey">
          <el-descriptions-item label="当前为" class="key-item">
            {{ apiKey }}
            <el-button
                icon="el-icon-copy-document"
                type="text"
                @click="copyApiKey">复制</el-button>
          </el-descriptions-item>
          <el-descriptions-item label="累计使用次数(调用接口次数)" class="num-item">{{ apiNum }}</el-descriptions-item>
          <el-descriptions-item label="最新使用时间(一键登录与远程接口调用)" class="time-item">{{ apiTime }}</el-descriptions-item>
        </el-descriptions>
        <el-alert
            title="您没有可用的ApiKey"
            type="error"
            show-icon
            v-else>
        </el-alert>

        <el-popconfirm
            title="Are you sure you want to update the API key?"
            confirm-button-text="Yes, update it"
            cancel-button-text="Cancel"
            @confirm="showPhoneVerification">
          <template #reference>
            <el-button type="primary">新建/更新ApiKey</el-button>
          </template>
        </el-popconfirm>
      </div>

      <!-- 添加请求体格式说明 -->
      <el-card>
        <el-descriptions title="请求体格式说明" border>
          <el-descriptions-item label="Body 类型" :span="3">multipart/form-data</el-descriptions-item>
          <el-descriptions-item label="参数名">AuthToken</el-descriptions-item>
          <el-descriptions-item label="参数值">Auth-123adc456efg789hij</el-descriptions-item>
          <el-descriptions-item label="参数类型">string</el-descriptions-item>
          <el-descriptions-item label="参数名">image1</el-descriptions-item>
          <el-descriptions-item label="参数值">test1.jpg</el-descriptions-item>
          <el-descriptions-item label="参数类型">file</el-descriptions-item>
          <el-descriptions-item label="参数名">image2</el-descriptions-item>
          <el-descriptions-item label="参数值">test2.jpg</el-descriptions-item>
          <el-descriptions-item label="参数类型">file</el-descriptions-item>
        </el-descriptions>
      </el-card>

    </el-card>

    <el-dialog
        :visible.sync="dialogVisible"
        :title="'操作消息'"
        width="30%"
        center>
      <span>{{ msg }}</span>
    </el-dialog>

    <el-dialog
        :visible.sync="phoneVerificationVisible"
        title="手机号验证"
        width="30%"
        center>
      <el-form :model="phoneAuthForm" ref="phoneAuthForm" label-width="100px">
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="phoneAuthForm.phone" disabled></el-input>
        </el-form-item>
        <el-form-item label="验证码" prop="code">
          <el-input v-model="phoneAuthForm.code"></el-input>
          <el-button @click="sendCode('phone')" :disabled="phoneCountdown > 0">
            {{ phoneCountdown > 0 ? `${phoneCountdown}s` : '发送验证码' }}
          </el-button>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="phoneVerificationVisible = false">取消</el-button>
        <el-button type="primary" @click="verifyPhone">验证</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      apiKey: null,
      apiNum: 0,
      apiTime: '',
      apiBaseUrl: 'http://localhost:8868/api/vef',
      msg: '',
      dialogVisible: false,
      phoneAuthForm: {
        phone: '',
        code: '',
        fid: ''
      },
      phoneData: { fid: '', phone: '' },
      phoneCountdown: 0,
      phoneVerificationVisible: false
    };
  },
  mounted() {
    this.fetchApiKey();
    this.checkPhoneBinding();
  },
  methods: {
    fetchApiKey() {
      const fid = localStorage.getItem('user_id');
      this.$http.get(`/face/apiKey/${fid}`).then(response => {
        if (response.data.code === 200) {
          this.apiKey = response.data.apiKey;
          this.apiNum = response.data.apiNum;
          this.apiTime = response.data.apiTime;
          if (response.data.msg) {
            this.msg = response.data.msg;
            this.dialogVisible = true;
            setTimeout(() => {
              this.dialogVisible = false;
            }, 700);
          }
        } else {
          this.apiKey = null;
          this.apiNum = 0;
          this.apiTime = '';
        }
      }).catch(error => {
        console.error('Error fetching API key:', error);
        this.apiKey = null;
        this.apiNum = 0;
        this.apiTime = '';
      });
    },
    showPhoneVerification() {
      const fid = localStorage.getItem('user_id');
      this.phoneAuthForm.fid = fid;
      this.$http.get(`/face/SmsC/${fid}`).then(response => {
        if (response.data.code === 200) {
          this.phoneData.phone = response.data.phone;
          this.phoneData.fid = response.data.fid;
          this.phoneAuthForm.phone = this.phoneData.phone;
          this.phoneVerificationVisible = true;
        }
      }).catch(error => {
        console.error('Error checking phone binding:', error);
      });
    },
    verifyPhone() {
      this.$http.get('/face/SmsVef', {
        params: {
          fid: this.phoneAuthForm.fid,
          phone: this.phoneAuthForm.phone,
          code: this.phoneAuthForm.code
        }
      }).then(response => {
        if (response.data.code === 200) {
          this.phoneVerificationVisible = false;
          this.updateApiKey();
        } else {
          alert(response.data.msg);
        }
      }).catch(error => {
        console.error('Phone verification failed:', error);
      });
    },
    updateApiKey() {
      const fid = localStorage.getItem('user_id');
      this.$http.get(`/face/updateApiKey/${fid}`).then(response => {
        if (response.data.code === 200) {
          this.apiKey = response.data.apiKey;
          this.apiNum = response.data.apiNum;
          this.apiTime = response.data.apiTime;
          if (response.data.msg) {
            this.msg = response.data.msg;
            this.dialogVisible = true;
            setTimeout(() => {
              this.dialogVisible = false;
            }, 1000);
          }
        }
      }).catch(error => {
        console.error('Error updating API key:', error);
      });
    },
    copyApiKey() {
      navigator.clipboard.writeText(this.apiKey).then(() => {
        this.$message({
          message: 'API Key copied to clipboard!',
          type: 'success',
          duration: 1500
        });
      }).catch(err => {
        console.error('Failed to copy API key: ', err);
      });
    },
    checkPhoneBinding() {
      const fid = localStorage.getItem('user_id');
      this.$http.get(`/face/SmsC/${fid}`).then(response => {
        if (response.data.code === 200) {
          this.phoneData.phone = response.data.phone;
          this.phoneData.fid = response.data.fid;
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
        }
      }, 1000);

      let phone = '';
      switch (type) {
        case 'phone':
          phone = this.phoneAuthForm.phone;
          break;
      }
      this.$http.get('/face/Sms', { params: { phone: phone } })
          .then(response => {
            alert(response.data.msg);
          }).catch(error => {
        console.error('Failed to send code:', error);
      });
    }
  }
};
</script>

<style scoped>
.el-alert {
  margin-bottom: 20px;
}
.el-descriptions {
  margin-bottom: 20px;
}
.api-key-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.key-item {
  flex: 1 1 30%;
}
.num-item {
  flex: 1 1 35%;
}
.time-item {
  flex: 1 1 35%;
}
</style>