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
            @confirm="updateApiKey">
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
