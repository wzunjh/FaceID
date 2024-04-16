<template>
  <div>
    <el-card>
      <el-descriptions title="用户 IP 登录安全中心" border>
        <el-descriptions-item label="用户ID">{{ userId }}</el-descriptions-item>
        <el-descriptions-item label="当前登录IP地址">{{ ip }}</el-descriptions-item>
        <el-descriptions-item label="IP白名单">
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <ul>
              <li v-for="ip in ipList" :key="ip">{{ ip }}</li>
            </ul>
            <el-button type="primary" @click="showEditDialog = true">修改IP白名单</el-button>
          </div>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
    <el-dialog title="修改IP白名单" :visible.sync="showEditDialog">
      <el-input v-model="newIpList" placeholder="请输入IP白名单,多个IP以英文逗号分隔"></el-input>
      <span slot="footer" class="dialog-footer">
        <el-button @click="showEditDialog = false">取 消</el-button>
        <el-button type="primary" @click="updateIpList">确 认</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      userId: null,
      ip: '',
      ipList: [],
      showEditDialog: false,
      newIpList: ''
    };
  },
  mounted() {
    this.fetchIpInfo();
  },
  methods: {
    fetchIpInfo() {
      const fid = localStorage.getItem('user_id');
      if (fid) {
        this.userId = fid;
        this.$http.get(`/face/ip/${fid}`).then(response => {
          const data = response.data;
          if (data.code === 200) {
            this.ip = data.ip;
            this.ipList = data.ipList.split(',') || [];
          } else {
            this.$message.error('获取IP信息失败');
          }
        }).catch(error => {
          console.error('Error fetching IP information:', error);
          this.$message.error('获取IP信息失败');
        });
      } else {
        this.$message.error('用户ID未找到');
      }
    },
    updateIpList() {
      const fid = this.userId;
      if (!fid) {
        this.$message.error('用户ID未找到');
        return;
      }
      this.$http.get(`/face/SetIP?fid=${fid}&ipList=${this.newIpList}`).then(response => {
        const data = response.data;
        if (data.code === 200) {
          this.ipList = data.ipList.split(',');
          this.$message.success('IP白名单更新成功');
          this.showEditDialog = false;
        } else {
          this.$message.error(data.msg || '更新IP白名单失败');
        }
      }).catch(error => {
        console.error('Error updating IP whitelist:', error);
        this.$message.error('更新IP白名单失败');
      });
    }
  }
};
</script>
