<template>
  <div>
    <el-card header="API信息中心">
      <div v-if="apiKey">
        <el-alert
            :title="'当前ApiKey: ' + apiKey"
            type="success"
            show-icon>
        </el-alert>
      </div>
      <div v-else>
        <el-alert
            title="您没有可用的ApiKey"
            type="error"
            show-icon>
        </el-alert>
      </div>
      <el-button type="primary" @click="updateApiKey">新建/更新ApiKey</el-button>
      <el-descriptions title="接口请求地址">
        <el-descriptions-item label="URL">{{ apiBaseUrl }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
    <!-- 弹窗显示msg信息 -->
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
      dialogVisible: false  // 控制弹窗显示
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
          if (response.data.msg) {  // 仅当存在msg时显示弹窗
            this.msg = response.data.msg;
            this.dialogVisible = true;
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
          if (response.data.msg) {  // 仅当存在msg时显示弹窗
            this.msg = response.data.msg;
            this.dialogVisible = true;
          }
        }
      }).catch(error => {
        console.error('Error updating API key:', error);
      });
    }
  }
};
</script>

<style scoped>
.el-alert {
  margin-bottom: 20px;
}
</style>
