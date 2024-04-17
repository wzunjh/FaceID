<template>
  <div class="center-container">
    <el-card v-if="!authenticated" class="auth-card">
      <el-steps :active="stepActive" finish-status="success" simple>
        <el-step title="上传图片"></el-step>
        <el-step title="正在认证"></el-step>
        <el-step title="认证结果"></el-step>
      </el-steps>
      <el-form ref="form" :model="authForm" label-width="120px">
        <el-form-item label="上传身份证图片">
          <el-upload
              class="upload-demo"
              list-type="picture-card"
              :file-list="fileList"
              :on-preview="handlePreview"
              :on-remove="handleRemove"
              :before-upload="beforeUpload"
              :http-request="handleUpload"
              :limit="1"
              multiple>
            <i class="el-icon-plus"></i>
          </el-upload>
          <div v-if="imageSrc" style="text-align: center; margin-top: 20px;">
            <img :src="imageSrc" style="max-width: 100%; max-height: 400px;" />
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm('form')" :disabled="submitting">立即认证</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <div v-else class="success-message">
      <el-alert title="您已认证成功" type="success" center show-icon />
      <el-descriptions title="您的认证信息">
        <el-descriptions-item label="姓名">{{ faceData.name }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ faceData.fid }}</el-descriptions-item>
        <el-descriptions-item label="身份证所在城市地区">{{ faceData.city }}</el-descriptions-item>
        <el-descriptions-item label="认证状态">
          <el-tag size="small">已认证</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="18位身份证号码">{{ faceData.idNo }}</el-descriptions-item>
      </el-descriptions>
    </div>
  </div>
</template>


<script>
export default {
  data() {
    return {
      authForm: {
        fid: '',
        imageBase: null
      },
      imageSrc: null,
      fileList: [],
      dialogVisible: false,
      authMsg: '',
      authenticated: false,
      faceData: { fid: '', name: '', idNo: '', city: '' },
      submitting: false,
      stepActive: 0
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
          this.faceData = response.data;
        }
      }).catch(error => {
        console.error('Error fetching user info:', error);
      });
    },
    handleFileChange(event) {
      const file = event.target.files[0];
      if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
          this.imageSrc = e.target.result;
          this.authForm.imageBase = e.target.result.split(',')[1]; // Assuming the result is in base64 format
        };
        reader.readAsDataURL(file);
      }
    },
    submitForm(formName) {
      this.stepActive = 1;
      this.submitting = true;
      this.$refs[formName].validate(valid => {
        if (valid) {
          let formData = new FormData();
          formData.append('fid', this.authForm.fid);
          formData.append('imageBase', this.authForm.imageBase);

          this.$http.post('/face/auth', formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          }).then(response => {
            this.stepActive = 2
            if (response.data.code === 200 || response.data.code === 202) {
              this.authMsg = response.data.msg;
              this.authenticated = true;
              this.fetchAuthInfo();
              this.stepActive = 3; // Move to the final step in the progress bar
            } else {
              this.authMsg = response.data.msg || '认证请求失败';
              this.stepActive = 0; // Reset step on failure
            }
          }).catch(error => {
            console.error('Authentication failed:', error);
            this.authMsg = '认证请求失败';
            this.stepActive = 0; // Reset step on failure
          }).finally(() => {
            this.submitting = false;
          });
        } else {
          return false;
        }
      });
    },
    beforeUpload(file) {
      const isImage = file.type === 'image/jpeg' || file.type === 'image/png';
      if (!isImage) {
        this.$message.error('Only JPEG or PNG images are allowed!');
        return false;
      }
      return true;
    },
    handleUpload(file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        this.imageSrc = e.target.result;
        this.authForm.imageBase = e.target.result.split(',')[1]; // Assuming the result is in base64 format
      };
      reader.readAsDataURL(file.file);
      return false; // Prevent default upload behavior
    },
    handlePreview(file) {
      this.imageSrc = file.url;
    },
    handleRemove(file, fileList) {
      this.imageSrc = fileList.length > 0 ? fileList[0].url : '';
    }
  }
};
</script>

<style scoped>
.auth-card {
  width: 600px;
  margin: auto;
}

.center-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
}

.upload-demo {
  display: flex;
  justify-content: center;
  margin: 20px 0;
}

.success-message {
  color: green;
  padding: 20px;
  border: 1px solid #e0e0e0;
  border-radius: 5px;
  margin-top: -750px; /* 负值表示向上移动 */
}
</style>
