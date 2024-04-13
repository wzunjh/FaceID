<template>
  <div>
    <el-card header="API信息中心">
      <el-descriptions title="接口请求地址" border>
        <el-descriptions-item label="URL">{{ apiBaseUrl }}</el-descriptions-item>
      </el-descriptions>

      <div class="api-key-container">
        <el-descriptions title="API Key" border v-if="apiKey">
          <el-descriptions-item label="当前Key">{{ apiKey }}</el-descriptions-item>
        </el-descriptions>
        <el-alert
            title="您没有可用的ApiKey"
            type="error"
            show-icon
            v-else>
        </el-alert>

        <el-button
            icon="el-icon-copy-document"
            type="text"
            v-if="apiKey"
            @click="copyApiKey">复制</el-button>

        <el-popconfirm
            title="Are you sure you want to update the API key?"
            confirm-button-text="Yes, update it"
            cancel-button-text="Cancel"
            @confirm="updateApiKey">
          <template #reference>
            <el-button type="primary">新建/更新ApiKey</el-button>
          </template>
        </el-popconfirm>
      </div>
    </el-card>

    <el-dialog
        :visible.sync="dialogVisible"
        :title="'操作消息'"
        width="30%"
        center>
      <span>{{ msg }}</span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      apiKey: null,
      apiBaseUrl: 'http://localhost:8868/api',
      msg: '',
      dialogVisible: false
    };
  },
  mounted() {
    this.fetchApiKey();
  },
  methods: {
    fetchApiKey() {
      const fid = localStorage.getItem('user_id');
      this.$http.get(`/face/apiKey/${fid}`).then(response => {
        if (response.data.code === 200) {
          this.apiKey = response.data.apiKey;
          if (response.data.msg) {
            this.msg = response.data.msg;
            this.dialogVisible = true;
            setTimeout(() => {
              this.dialogVisible = false;
            }, 700);
          }
        } else {
          this.apiKey = null;
        }
      }).catch(error => {
        console.error('Error fetching API key:', error);
        this.apiKey = null;
      });
    },
    updateApiKey() {
      const fid = localStorage.getItem('user_id');
      this.$http.get(`/face/updateApiKey/${fid}`).then(response => {
        if (response.data.code === 200) {
          this.apiKey = response.data.apiKey;
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
  justify-content: space-between; /* This can be adjusted to flex-start, flex-end, or others as needed */
}
</style>
