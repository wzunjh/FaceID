<template>
  <div>
    <el-card header="API信息中心">
      <!-- 将接口请求地址信息放入API信息中心，并用圆角边框包围 -->
      <el-descriptions title="接口请求地址" border>
        <el-descriptions-item label="URL">{{ apiBaseUrl }}</el-descriptions-item>
      </el-descriptions>

      <div v-if="apiKey">
        <p>当前ApiKey:</p>
        <el-tag
            type="success"
            show-icon>{{ apiKey }}
        </el-tag>
      </div>
      <div v-else>
        <el-alert
            title="您没有可用的ApiKey"
            type="error"
            show-icon>
        </el-alert>
      </div>

      <!-- 使用el-popconfirm组件确认操作 -->
      <el-popconfirm
          title="Are you sure you want to update the API key?"
          confirm-button-text="Yes, update it"
          cancel-button-text="Cancel"
          @confirm="updateApiKey">
        <template #reference>
          <el-button type="primary">新建/更新ApiKey</el-button>
        </template>
      </el-popconfirm>
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
</style>
