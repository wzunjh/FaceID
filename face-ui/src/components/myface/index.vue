<template>
  <div>
    <!--条件搜索区域-->
    <el-row>
      <el-col :span="24">
        <el-card header="自建人脸信息库列表">
          <el-form :inline="true">
            <el-form-item label="人脸名称">
              <el-input v-model="searchFace.faceName" placeholder="名称" clearable></el-input>
            </el-form-item>
            <el-form-item>
              <el-button icon="el-icon-search" @click="getFaceList">搜索</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <!--数据显示区域-->
    <el-row>
      <el-col :span="24">
        <el-card>
          <el-header style="background-color: #ffffff">
            <el-button @click="handleSave()" icon="el-icon-circle-plus-outline">添加人脸</el-button>
          </el-header>
          <el-table :data="tableData" style="width: 100%">
            <el-table-column width="100" prop="faceId" label="编号" sortable></el-table-column>
            <el-table-column width="150" prop="date" label="人脸图片">
              <template v-slot:default="scope">
                <el-button icon="el-icon-picture-outline" size="mini" @click="selectImg(scope.row.faceBase)">查看人脸</el-button>
              </template>
            </el-table-column>
            <el-table-column prop="faceName" label="姓名"></el-table-column>
            <el-table-column label="操作" width="230">
              <template v-slot:default="scope">
                <el-button icon="el-icon-edit" size="mini" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
                <el-popconfirm :title="'确认删除' + scope.row.faceName + '的人脸数据吗?'" @confirm="handleDelete(scope.$index, scope.row)">
                  <el-button style="margin-left: 20px" icon="el-icon-delete" size="mini" type="danger" slot="reference">删除</el-button>
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!--分页-->
    <el-row class="page-row">
      <el-pagination class="el-pagination" @size-change="handleSizeChange" @current-change="handleCurrentChange" layout="total, sizes, prev, pager, next, jumper" :page-sizes="[5, 10, 30, 50]" :current-page="current" :page-size="size" :total="total"></el-pagination>
    </el-row>

    <!--查看人脸图片-->
    <el-dialog title="人脸图片" :visible.sync="selectImgDialog" width="30%">
      <img style="width: 100%;" :src="currentImg">
    </el-dialog>

    <!-- 添加/修改-->
    <el-dialog :title="saveAndUpdateTitle" :visible.sync="saveAndUpdateDialog" :before-close="handleClose" width="600px">
      <el-form :model="faceObj" :rules="rules" ref="ruleForm" label-width="80px">
        <input type="hidden" v-model="faceObj.fid"></input>
        <input type="hidden" v-model="faceObj.faceId"></input>
        <el-form-item label="人脸名称" prop="faceName">
          <el-input placeholder="必填项" v-model="faceObj.faceName"></el-input>
        </el-form-item>
        <el-form-item label="人脸数据" class="login-mid-mid" prop="faceBase">
          <el-upload
              class="avatar-uploader"
              :action="fakeUploadUrl"
              :http-request="handleUpload"
              :before-upload="beforeUpload"
              :show-file-list="false"
              :on-remove="handleRemove"
              :limit="1"
              :on-exceed="handleExceed">
            <img v-if="faceObj.faceBase" :src="faceObj.faceBase" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm('ruleForm')">立即创建</el-button>
          <el-button @click="resetForm('ruleForm')">重置</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      size: 5,
      current: 1,
      total: 0,
      searchFace: {
        faceName: ''
      },
      tableData: [],
      selectImgDialog: false,
      currentImg: '',
      saveAndUpdateDialog: false,
      saveAndUpdateTitle: '',
      faceObj: {
        fid: localStorage.getItem('user_id'),
        faceId: null,
        faceName: '',
        faceBase: '',
        addAndUpdateState: false,
      },
      rules: {
        faceName: [
          { required: true, message: '请输入人脸名称', trigger: 'blur' },
          { min: 2, max: 5, message: '长度在 2 到 5 个字符', trigger: 'blur' }
        ],
        faceBase: [
          { required: true, message: '人脸数据不能为空' }
        ]
      },
      fakeUploadUrl: 'https://jsonplaceholder.typicode.com/posts/'
    }
  },
  created() {
    this.getFaceList();
  },
  methods: {
    handleUpload(file) {
      if (this.faceObj.faceBase) {
        // 如果已经有图片,先删除原有的图片
        this.faceObj.faceBase = '';
      }
      // 上传新的图片
      this.convertToBase64(file.file).then(base64 => {
        this.faceObj.faceBase = base64;
      });
    },
    beforeUpload(file) {
      const isLt1M = file.size / 1024 / 1024 < 1;
      if (!isLt1M) {
        this.$message.error('上传文件大小不能超过 1MB!');
      }
      return isLt1M;
    },
    handleExceed(files, fileList) {
      this.$message.error('只能上传 1 个文件!');
    },
    handleRemove() {
      this.faceObj.faceBase = '';
    },
    convertToBase64(file) {
      return new Promise((resolve, reject) => {
        const fileReader = new FileReader();
        fileReader.readAsDataURL(file);
        fileReader.onload = () => {
          resolve(fileReader.result);
        };
        fileReader.onerror = (error) => {
          reject(error);
        };
      });
    },
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.$http.post("/mydb/" + (this.addAndUpdateState ? 'save' : 'update'), this.faceObj).then(res => {
            this.$message({
              showClose: true,
              message: res.data.msg,
              type: 'success',
              duration: 3000
            })
            this.getFaceList()
            this.saveAndUpdateDialog = false
          })
        } else {
          return false;
        }
      });
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
      this.faceObj.faceBase = '';
    },
    selectImg(faceBase) {
      this.selectImgDialog = true
      this.currentImg = faceBase
    },
    getFaceList() {
      let params = {
        current: this.current,
        size: this.size,
        faceName: this.searchFace.faceName,
        fid: this.faceObj.fid
      };
      this.$http.get('/mydb/faceList', { params }).then(res => {
        this.tableData = res.data.data.records;
        this.total = res.data.data.total;
        this.size = res.data.data.size;
        this.current = res.data.data.current;
      });
    },
    handleSizeChange(val) {
      this.size = val
      this.getFaceList()
    },
    handleCurrentChange(val) {
      this.current = val
      this.getFaceList()
    },
    handleClose() {
      this.saveAndUpdateDialog = false
      this.resetForm('ruleForm')
    },
    handleSave() {
      this.addAndUpdateState = true
      this.saveAndUpdateDialog = true
      this.saveAndUpdateTitle = '添加人脸'
      this.faceObj.faceBase = ''
    },
    handleEdit(index, row) {
      this.addAndUpdateState = false
      this.saveAndUpdateDialog = true
      this.saveAndUpdateTitle = '修改人脸'
      this.$http.get("/mydb/info/" + row.faceId).then(res => {
        this.faceObj = res.data.data
        this.faceObj.faceId = row.faceId
      })
    },
    handleDelete(index, row) {
      this.$http.get("/mydb/faceDelete/" + row.faceId).then(res => {
        this.$message({
          showClose: true,
          message: res.data.msg,
          type: 'success',
          duration: 3000
        })
        this.getFaceList();
      })
    }
  }
}
</script>

<style>
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}
.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style>