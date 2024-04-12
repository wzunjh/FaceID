<template>
  <div>
    <el-card header="身份证认证">
      <el-form ref="form" :model="authForm" label-width="120px">
        <el-form-item label="身份证号码" prop="idNo">
          <el-input v-model="authForm.idNo" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm('form')">提交</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-dialog title="认证结果" :visible.sync="dialogVisible">
      <span v-if="authResult === 'already-success'" style="color: green">已认证成功！</span>
      <span v-else-if="authResult === 'success'" style="color: green">认证成功！</span>
      <span v-else-if="authResult === 'failure'" style="color: red">信息不匹配，认证失败！</span>
      <p>{{ authMsg }}</p>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      authForm: {
        idNo: ''      // 身份证号码
      },
      dialogVisible: false,
      authResult: '', // 认证结果
      authMsg: ''     // 认证消息
    };
  },
  mounted() {
    // Retrieve fid from localStorage
    this.authForm.fid = localStorage.getItem('user_id');
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          // 使用GET请求发送数据
          this.$http.get('/face/auth', { params: this.authForm }).then(response => {
            switch(response.data.code) {
              case 202:
                this.authResult = 'already-success';
                break;
              case 200:
                this.authResult = 'success';
                break;
              default:
                this.authResult = 'failure';
                break;
            }
            this.authMsg = response.data.msg;
            this.dialogVisible = true;
          }).catch(error => {
            console.error('Authentication failed:', error);
            this.authResult = 'failure';
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
<template>
  <div>
    <el-card header="身份证认证">
      <el-form ref="form" :model="authForm" label-width="120px">
        <el-form-item label="身份证号码" prop="idNo">
          <el-input v-model="authForm.idNo" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm('form')">提交</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-dialog title="认证结果" :visible.sync="dialogVisible">
      <span v-if="authResult === 'already-success'" style="color: green">已认证成功！</span>
      <span v-else-if="authResult === 'success'" style="color: green">认证成功！</span>
      <span v-else-if="authResult === 'failure'" style="color: red">信息不匹配，认证失败！</span>
      <p>{{ authMsg }}</p>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      authForm: {
        idNo: ''      // 身份证号码
      },
      dialogVisible: false,
      authResult: '', // 认证结果
      authMsg: ''     // 认证消息
    };
  },
  mounted() {
    // Retrieve fid from localStorage
    this.authForm.fid = localStorage.getItem('user_id');
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          // 使用GET请求发送数据
          this.$http.get('/face/auth', { params: this.authForm }).then(response => {
            switch(response.data.code) {
              case 202:
                this.authResult = 'already-success';
                break;
              case 200:
                this.authResult = 'success';
                break;
              default:
                this.authResult = 'failure';
                break;
            }
            this.authMsg = response.data.msg;
            this.dialogVisible = true;
          }).catch(error => {
            console.error('Authentication failed:', error);
            this.authResult = 'failure';
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
