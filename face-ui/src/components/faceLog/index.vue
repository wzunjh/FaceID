<template>
    <div>


      <!--条件搜索区域-->
      <el-row>
        <el-col :span="24">
          <el-card header="日志列表">
            <el-form :inline="true">
              <el-form-item label="人脸名称">
                <el-input
                    v-model="searchFace.loginName"
                    placeholder="名称"
                    clearable>
                </el-input>
              </el-form-item>
              <el-form-item label="人脸状态">
                <el-select v-model="searchFace.vefCode" placeholder="请选择">
                  <el-option label="请选择" value=""></el-option>
                  <el-option label="成功" value="200"></el-option>
                  <el-option label="腾讯云接口发生的异常" value="-1"></el-option>
                  <el-option label="数据库没有该人脸" value="-2"></el-option>
                  <el-option label="人脸数据为空" value="-3"></el-option>
                  <el-option label="当前人脸被禁用" value="-4"></el-option>
                  <el-option label="初始化人脸" value="201"></el-option>
                </el-select>
              </el-form-item>
              <br/>
              <el-form-item label="验证时间">
                <el-date-picker
                    v-model="searchFace.timeArray"
                    type="daterange"
                    range-separator="至"
                    value-format="yyyy-MM-dd"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期">
                </el-date-picker>
              </el-form-item>
              <el-form-item>
                <el-button icon="el-icon-search" @click="getFaceVefLogList">搜索</el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>
      </el-row>

        <!--数据表格-->
      <el-row>
        <el-col :span="24">
          <el-card>
            <el-table
                :data="tableLogData"
                style="width: 100%">
              <el-table-column
                  width="100"
                  prop="lid"
                  label="编号" sortable>
              </el-table-column>


              <el-table-column
                  prop="vefTime"
                  label="验证时间"  sortable>
              </el-table-column>


              <el-table-column
                  prop="loginName"
                  label="验证人">
              </el-table-column>

              <el-table-column
                  prop="vefMsg"
                  label="返回消息">
              </el-table-column>

              <el-table-column
                  width="150"
                  prop="vefCode"
                  label="返回code" sortable>
                <template slot-scope="scope">
                  <el-tag v-if="scope.row.vefCode === 200 || scope.row.vefCode === 201" type="success">
                    {{scope.row.vefCode}}
                  </el-tag>
                  <el-tag v-else type="danger">
                    {{scope.row.vefCode}}
                  </el-tag>
                </template>
              </el-table-column>


            </el-table>
          </el-card>
        </el-col>
      </el-row>


      <!--分页-->
      <el-row class="page-row">
        <el-pagination
            class="el-pagination"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            layout="total, sizes, prev, pager, next, jumper"
            :page-sizes="[5, 10, 30, 50]"
            :current-page="current"
            :page-size="size"
            :total="total">
        </el-pagination>
      </el-row>

    </div>
</template>

<script>
export default {
name: "index",
  data(){
      return{
        size:5,
        current:1,
        total:0,
        searchFace:{
          loginName:'',
          vefCode:'',
          timeArray:[]
        },
        tableLogData:[]
      }
  },
  // 获取人脸数据
  created() {
    this.getFaceVefLogList()
  },
  methods:{
    // 分页
    handleSizeChange(val) {
      this.size = val
      this.getFaceVefLogList()
    },
    // 分页
    handleCurrentChange(val) {
      this.current = val
      this.getFaceVefLogList()
    },
    // 日志列表
    getFaceVefLogList(){
      if(!this.searchFace.timeArray) {
        this.searchFace.timeArray = [];
      }
      this.$http.get('/face/log/list',{
        params:{
          current:this.current,
          size:this.size,
          startTime:this.searchFace.timeArray[0],
          endTime:this.searchFace.timeArray[1],
          vefCode:this.searchFace.vefCode,
          loginName:this.searchFace.loginName
        }
      }).then(res =>{
        this.tableLogData = res.data.data.records
        this.size = res.data.data.size
        this.current = res.data.data.current
        this.total = res.data.data.total
      })
    },
  }
}
</script>

<style >
.page-row{
  margin-top: 20px;
  text-align: center;
}
</style>