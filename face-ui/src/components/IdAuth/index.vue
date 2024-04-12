<template>
  <div>
    <el-card v-if="!authenticated" header="身份证认证">
      <el-form ref="form" :model="authForm" label-width="120px">
        <el-form-item label="身份证号码" prop="idNo">
          <el-input v-model="authForm.idNo" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm('form')">提交</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <div v-else style="color: green;">
      您已认证成功
      <div>
        <p>用户ID: {{ faceData.fid }}</p>
        <p>姓名: {{ faceData.faceName }}</p>
        <p>身份证号码: {{ faceData.idNo }}</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      authForm: {
        idNo: '',
        fid: ''
      },
      dialogVisible: false,
      authMsg: '',
      authenticated: false,
      faceData: { fid: '', faceName: '', idNo: '' }
    };
  },
  mounted() {
    this.authForm.fid = localStorage.getItem('user_id');
    this.checkAuthentication();
  },
  methods: {
    checkAuthentication() {
      this.$http.get(`/face/orAuth/${this.authForm.fid}`).then(response => {
        if (response.data.code === 200) {
          this.authenticated = true;
          this.fetchAuthInfo();
        }
      }).catch(error => {
        console.error('Error checking authentication:', error);
      });
    },
    fetchAuthInfo() {
      this.$http.get(`/face/authUser/${this.authForm.fid}`).then(response => {
        if (response.data.code === 200) {
          this.faceData.fid = response.data.fid;
          this.faceData.faceName = response.data.name;
          this.faceData.idNo = response.data.idNo;
        }
      }).catch(error => {
        console.error('Error fetching user info:', error);
      });
    },
    submitForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.$http.get('/face/auth', { params: this.authForm }).then(response => {
            if (response.data.code === 200 || response.data.code === 202) {
              this.authMsg = response.data.msg;
              this.authenticated = true;
              this.fetchAuthInfo();
            } else {
              this.authMsg = response.data.msg || '认证请求失败';
            }
            this.dialogVisible = true;
          }).catch(error => {
            console.error('Authentication failed:', error);
            this.authMsg = '认证请求失败';
            this.dialogVisible = true;
          });
        } else {
          return false;
        }
      });
    }
  }
};
</script>