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
    <div v-else style="color: green;">您已认证成功</div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      authForm: {
        idNo: '', // 身份证号码
        fid: ''   // 用户ID
      },
      dialogVisible: false,
      authMsg: '',     // 认证消息
      authenticated: false // 是否已认证
    };
  },
  mounted() {
    // Retrieve fid from localStorage
    this.authForm.fid = localStorage.getItem('user_id');
    // 发送查询是否认证过的请求
    this.checkAuthentication();
  },
  methods: {
    checkAuthentication() {
      this.$http.get(`/face/orAuth/${this.authForm.fid}`).then(response => {
        if (response.data.code === 200) {
          // 如果已认证成功，则不显示表单
          this.authenticated = true;
        }
      }).catch(error => {
        console.error('Error checking authentication:', error);
      });
    },
    submitForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          // 使用GET请求发送数据
          this.$http.get('/face/auth', { params: this.authForm }).then(response => {
            if (response.data.code === 200 || response.data.code === 202) {
              this.authMsg = response.data.msg;
              // 认证成功后跳转到对应的消息显示部分
              this.authenticated = true;
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
