<template>
  <div class="face-verification">
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
    <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过500kb</div>
    <div class="buttons">
      <button @click="submitImages" :disabled="submitting">开始识别</button>
    </div>
    <div class="message-area">
      <p v-if="message">{{ message }}</p>
    </div>
    <div class="upload-instructions">
      <p>当上传为一张图片时，接口会与人脸库数据对比（仅在线识别支持）。两张图片，则对比这两张。</p>
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
      submitting: false,
      fakeUploadUrl: 'https://jsonplaceholder.typicode.com/posts/' // Placeholder to satisfy <el-upload>
    };
  },
  methods: {
    beforeUpload(file) {
      const isJPGorPNG = (file.type === 'image/jpeg' || file.type === 'image/png');
      const isLt500KB = file.size / 1024 / 1024 < 0.5;

      if (!isJPGorPNG) {
        this.message = 'Only JPG/PNG files are allowed!';
      }
      if (!isLt500KB) {
        this.message = 'File size cannot exceed 500KB!';
      }
      return isJPGorPNG && isLt500KB;
    },
    handleUpload(file) {
      // We handle the upload manually to integrate with existing logic
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
        this.message = "ImageBase1 is required";
        return;
      }
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
          if (response.data.code === 200) {
            // Additional success actions
          }
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
          if (response.data.code === 200) {
            // Additional success actions
          }
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

<style>
/* Add necessary styles */
</style>
