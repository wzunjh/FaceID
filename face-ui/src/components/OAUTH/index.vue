<template>
  <div>
    <el-row>
      <el-col :span="24">
        <el-card header="授权应用列表">
          <el-form :inline="true">
            <el-form-item label="应用名称">
              <el-input v-model="searchParams.name" placeholder="名称" clearable></el-input>
            </el-form-item>
            <el-form-item label="所属用户ID">
              <el-input v-model="searchParams.fid" placeholder="用户ID" clearable></el-input>
            </el-form-item>
            <el-form-item>
              <el-button icon="el-icon-search" @click="getAppList">搜索</el-button>
              <el-button v-if="isUserAuthenticated" type="primary" @click="openOtpOauthPage">统一身份信息授权登录中心</el-button>
              <el-button v-if="isUserAuthenticated" type="primary" @click="openOtpYyPage">第三方网站授权登录演示</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <el-row>
      <el-col :span="24">
        <el-card>
          <el-header style="background-color: #ffffff">
            <el-button v-if="isUserAuthenticated" @click="handleSave()" icon="el-icon-circle-plus-outline">添加应用</el-button>
          </el-header>
          <el-table :data="tableData" style="width: 100%">
            <el-table-column prop="clientId" label="客户端ID" sortable></el-table-column>
            <el-table-column prop="clientSecret" label="客户端密钥"></el-table-column>
            <el-table-column prop="webServerRedirectUrl" label="回调URL"></el-table-column>
            <el-table-column prop="name" label="应用名称"></el-table-column>
            <el-table-column prop="fid" label="所属用户ID" sortable></el-table-column>
            <el-table-column label="操作" width="230">
              <template v-slot:default="scope">
                <el-button v-if="isUserAuthenticated" icon="el-icon-edit" size="mini" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
                <el-popconfirm v-if="isUserAuthenticated" :title="'确认删除' + scope.row.name + '应用吗?'" @confirm="handleDelete(scope.$index, scope.row)">
                  <el-button style="margin-left: 20px" icon="el-icon-delete" size="mini" type="danger" slot="reference">删除</el-button>
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row class="page-row">
      <el-pagination class="el-pagination" @size-change="handleSizeChange" @current-change="handleCurrentChange" layout="total, sizes, prev, pager, next, jumper" :page-sizes="[5, 10, 30, 50]" :current-page="current" :page-size="size" :total="total"></el-pagination>
    </el-row>

    <el-dialog :title="saveAndUpdateTitle" :visible.sync="saveAndUpdateDialog" :before-close="handleClose" width="600px">
      <el-form :model="appObj" :rules="rules" ref="ruleForm" label-width="80px">
        <input type="hidden" v-model="appObj.clientId"></input>
        <el-form-item label="应用名称" prop="name">
          <el-input placeholder="必填项" v-model="appObj.name"></el-input>
        </el-form-item>
        <el-form-item label="回调URL" prop="webServerRedirectUrl">
          <el-input placeholder="必填项" v-model="appObj.webServerRedirectUrl"></el-input>
        </el-form-item>
        <el-form-group>
          <el-form-item v-if="isSuperUser" label="所属用户ID" prop="fid">
            <el-input placeholder="必填项" v-model="appObj.fid"></el-input>
          </el-form-item>
          <el-form-item v-else>
            <el-input type="hidden" :value="appObj.fid = currentUserFid"></el-input>
          </el-form-item>
        </el-form-group>
        <el-form-item>
          <el-button v-if="isUserAuthenticated" type="primary" @click="submitForm('ruleForm')">立即保存</el-button>
          <el-button v-if="isUserAuthenticated" @click="resetForm('ruleForm')">重置</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    const validateWebServerRedirectUrl = (rule, value, callback) => {
      const urlRegex = /^(http|https):\/\//;
      if (!urlRegex.test(value)) {
        callback(new Error('请输入以 http:// 或 https:// 开头的合法重定向URL'));
      } else {
        callback();
      }
    };
    return {
      size: 5,
      current: 1,
      total: 0,
      searchParams: {
        name: '',
        fid: ''
      },
      tableData: [],
      saveAndUpdateDialog: false,
      saveAndUpdateTitle: '',
      appObj: {
        clientId: null,
        clientName: '',
        clientSecret: '',
        webServerRedirectUrl: '',
        name: '',
        fid: null
      },
      rules: {
        name: [
          { required: true, message: '请输入应用名称', trigger: 'blur' },
          { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        webServerRedirectUrl: [
          { required: true, message: '请输入重定向URL', trigger: 'blur' },
          { validator: validateWebServerRedirectUrl, trigger: 'blur' }
        ],
        fid: [
          { required: true, message: '请输入所属用户ID', trigger: 'blur' }
        ]
      },
      currentUserFid: null,
      isUserAuthenticated: false
    }
  },
  computed: {
    isSuperUser() {
      return this.currentUserFid === 1;
    }
  },
  created() {
    this.currentUserFid = parseInt(localStorage.getItem("user_id"));
    this.checkUserAuthentication();
  },
  methods: {
    checkUserAuthentication() {
      this.$http.get('/face/orAuth/' + this.currentUserFid).then(res => {
        if (res.data.code === 200) {
          this.isUserAuthenticated = true;
          this.getAppList();
        } else {
          this.isUserAuthenticated = false;
          res.data.msg("未认证用户,无权进行此操作")
          this.$message.warning(res.data.msg);
        }
      });
    },
    getAppList() {
      let params = {
        current: this.current,
        size: this.size,
        fid: this.searchParams.fid,
        name: this.searchParams.name
      };
      this.$http.get('/oau/List', { params }).then(res => {
        let records = res.data.data.records;
        // 如果 currentUserFid 不是 1，过滤数据
        if (this.currentUserFid !== 1) {
          records = records.filter(record => record.fid === this.currentUserFid);
        }
        this.total = records.length;
        this.tableData = records;
        this.size = res.data.data.size;
        this.current = res.data.data.current;
      });
    },
    handleSizeChange(val) {
      this.size = val
      this.getAppList()
    },
    handleCurrentChange(val) {
      this.current = val
      this.getAppList()
    },
    handleClose() {
      this.saveAndUpdateDialog = false
      this.resetForm('ruleForm')
    },
    handleSave() {
      // 先检查用户认证状态
      this.checkUserAuthentication();
      if (this.isUserAuthenticated) {
        this.addAndUpdateState = true
        this.saveAndUpdateDialog = true
        this.saveAndUpdateTitle = '添加应用'
      }
    },
    handleEdit(index, row) {
      // 先检查用户认证状态
      this.checkUserAuthentication();
      if (this.isUserAuthenticated) {
        this.addAndUpdateState = false
        this.saveAndUpdateDialog = true
        this.saveAndUpdateTitle = '修改应用'
        this.appObj = { ...row }
      }
    },
    handleDelete(index, row) {
      // 先检查用户认证状态
      this.checkUserAuthentication();
      if (this.isUserAuthenticated) {
        this.$http.get("/oau/Delete?clientId=" + row.clientId).then(res => {
          this.getAppList();
          this.$message.success(res.data.msg)
        })
      }
    },
    submitForm(formName) {
      // 先检查用户认证状态
      this.checkUserAuthentication();
      if (this.isUserAuthenticated) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            if (!this.isSuperUser) {
              this.appObj.fid = this.currentUserFid;
            }
            if (this.addAndUpdateState) {
              // 添加应用
              this.$http.post("/oau/save", this.appObj).then(res => {
                this.$message({
                  showClose: true,
                  message: res.data.msg,
                  type: 'success',
                  duration: 3000
                })
                this.getAppList()
                this.saveAndUpdateDialog = false
              })
            } else {
              // 修改应用
              this.$http.post("/oau/update", this.appObj).then(res => {
                this.$message({
                  showClose: true,
                  message: res.data.msg,
                  type: 'success',
                  duration: 3000
                })
                this.getAppList()
                this.saveAndUpdateDialog = false
              })
            }
          } else {
            return false;
          }
        });
      }
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
    },
    openOtpOauthPage() {
      window.open('http://localhost:8080/otpaouth', '_blank');
    },
    openOtpYyPage() {
      window.open('http://localhost:8080/otpyy', '_blank');
    }
  }
}
</script>

<style>
@import "../face/index.css";
</style>