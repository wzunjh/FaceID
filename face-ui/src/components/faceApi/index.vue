<template>
  <div class="face-verification">
    <div class="upload-area">
      <input type="file" @change="handleFileChange($event, 'imageBase1')" placeholder="Upload Image 1" />
      <input type="file" @change="handleFileChange($event, 'imageBase2')" placeholder="Upload Image 2 (optional)" />
    </div>
    <div class="buttons">
      <button @click="submitImages" :disabled="submitting">Verify Faces</button>
    </div>
    <div class="message-area">
      <p v-if="message">{{ message }}</p>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      imageBase1: '',
      imageBase2: '',
      message: '',
      submitting: false
    };
  },
  methods: {
    handleFileChange(event, imageBaseKey) {
      const file = event.target.files[0];
      if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
          this[imageBaseKey] = e.target.result;
        };
        reader.readAsDataURL(file);
      } else {
        // Clear the corresponding imageBase if no file is selected
        this[imageBaseKey] = '';
      }
    },
    submitImages() {
      if (!this.imageBase1) {
        this.message = "ImageBase1 is required";
        return;
      }
      this.submitting = true;
      let formData = new FormData();
      formData.append('imageBase1', this.imageBase1);
      formData.append('imageBase2', this.imageBase2 || '');

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
    }
  }
};
</script>

<style>
/* Add necessary styles */
</style>
