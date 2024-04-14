<template>
  <div class="face-verification">
    <!-- Page Title -->
    <h1 class="page-title">API在线识别</h1>

    <!-- Divider line -->
    <hr class="divider">

    <el-upload
        class="upload-demo"
        :action="fakeUploadUrl"
        :http-request="handleUpload"
        :before-upload="beforeUpload"
        list-type="picture-card"
        :file-list="fileList"
        :on-remove="handleRemove"
        :limit="2"
        multiple>
      <i class="el-icon-plus"></i>
    </el-upload>
    <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过500kb,最多上传两张</div>

    <el-steps :active="stepActive" finish-status="success" simple style="margin-top: 20px">
      <el-step title="上传图片"></el-step>
      <el-step title="正在识别"></el-step>
      <el-step title="识别结果"></el-step>
    </el-steps>

    <!-- Button centered below steps -->
    <div class="button-container">
      <el-button @click="submitImages" :disabled="submitting" type="primary" round>开始识别</el-button>
    </div>

    <!-- Message enlarged and centered below button -->
    <div class="message-area">
      <el-tag v-if="message" :type="messageType" class="message-content">{{ message }}</el-tag>
    </div>

    <!-- Revised Upload Instructions -->
    <div class="upload-instructions">
      <p>接口使用说明: 当上传单张图片时，系统将该图片与存储在人脸库中的数据进行比对，以验证身份(该功能仅在在线识别模式下支持)。若上传两张图片，系统则比较两者以确定是否为同一人。</p>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      fileList: [],
      imageBase1: '',
      imageBase2: '',
      message: '',
      messageType: 'success',
      submitting: false,
      stepActive: 0,
      fakeUploadUrl: 'https://jsonplaceholder.typicode.com/posts/' // Placeholder to satisfy <el-upload>
    };
  },
  methods: {
    beforeUpload(file) {
      const isJPGorPNG = (file.type === 'image/jpeg' || file.type === 'image/png');
      const isLt500KB = file.size / 1024 / 1024 < 0.5;

      if (!isJPGorPNG) {
        this.message = 'Only JPG/PNG files are allowed!';
        this.messageType = 'error';
      }
      if (!isLt500KB) {
        this.message = 'File size cannot exceed 500KB!';
        this.messageType = 'error';
      }
      if (isJPGorPNG && isLt500KB) {
        this.stepActive = 1;
      }
      return isJPGorPNG && isLt500KB;
    },
    handleUpload(file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        if (this.imageBase1 === '') {
          this.imageBase1 = e.target.result;
        } else {
          this.imageBase2 = e.target.result;
        }
      };
      reader.readAsDataURL(file.file);
      return false; // Prevent default upload behavior
    },
    handleRemove(file, fileList) {
      this.imageBase1 = fileList.length > 0 ? fileList[0].url : '';
      this.imageBase2 = fileList.length > 1 ? fileList[1].url : '';
    },
    submitImages() {
      if (!this.imageBase1) {
        this.message = "第一张图片不能为空";
        this.messageType = 'error';
        return;
      }
      this.stepActive = 2;
      this.submitting = true;

      if (this.imageBase2) {
        let formData = new FormData();
        formData.append('imageBase1', this.imageBase1);
        formData.append('imageBase2', this.imageBase2);

        this.$http.post('/face/api', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        }).then(response => {
          this.message = response.data.msg;
          this.stepActive = 3;
        }).catch(error => {
          this.message = "Error submitting images: " + error.response.data.msg;
        }).finally(() => {
          this.submitting = false;
        });
      } else {
        this.$http.post('/face/vef', JSON.stringify({ imageBase: this.imageBase1 }), {
          headers: {
            'Content-Type': 'application/json'
          }
        }).then(response => {
          this.message = response.data.msg;
          this.stepActive = 3;
        }).catch(error => {
          this.message = "Error submitting image: " + error.response.data.msg;
        }).finally(() => {
          this.submitting = false;
        });
      }
    }
  }
};
</script>

<style scoped>
/* Additional styles for layout */
.page-title {
  font-size: 24px;
  margin-bottom: 20px;
}

.button-container {
  text-align: center;
  margin-top: 20px;
}

.message-area {
  text-align: center;
  margin-top: 20px;
}

.message-content {
  font-size: 18px;
}

.upload-instructions {
  margin-top: 20px;
}

.divider {
  border-top: 1px solid #ccc;
  margin: 20px 0;
}
</style>
