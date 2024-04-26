<template>
  <div>
    <!--条件搜索区域-->
    <el-row>
      <el-col :span="24">
        <el-card header="API日志列表">
          <el-form :inline="true">
            <el-form-item label="请求时间">
              <el-date-picker
                  v-model="searchApi.timeArray"
                  type="daterange"
                  range-separator="至"
                  value-format="yyyy-MM-dd"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期">
              </el-date-picker>
            </el-form-item>
            <el-form-item>
              <el-button icon="el-icon-search" @click="getApiLogList">搜索</el-button>
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
              :data="tableApiData"
              style="width: 100%">
            <el-table-column
                prop="fid"
                label="用户编号"
                sortable>
            </el-table-column>

            <el-table-column
                prop="apiTime"
                label="请求时间"
                sortable>
            </el-table-column>

            <el-table-column
                prop="apiMsg"
                label="响应结果">
            </el-table-column>

            <el-table-column
                prop="apiCode"
                label="响应状态"
                :formatter="statusFormatter">
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
  name: "ApiLogIndex",
  data() {
    return {
      fid: localStorage.getItem('user_id'), // Get fid from localStorage
      size: 5,
      current: 1,
      total: 0,
      searchApi: {
        timeArray: []
      },
      tableApiData: []
    }
  },
  created() {
    this.getApiLogList()
  },
  methods: {
    handleSizeChange(val) {
      this.size = val
      this.getApiLogList()
    },
    handleCurrentChange(val) {
      this.current = val
      this.getApiLogList()
    },
    getApiLogList() {
      this.$http.get(`/face/log/${this.fid}`, {
        params: {
          current: this.current,
          size: this.size,
          startTime: this.searchApi.timeArray[0],
          endTime: this.searchApi.timeArray[1]
        }
      }).then(res => {
        this.tableApiData = res.data.data.records;
        this.size = res.data.data.size;
        this.current = res.data.data.current;
        this.total = res.data.data.total;
      })
    },
    statusFormatter(row, column, value) {
      return value === 200 ? '请求成功:200' : '请求错误';
    }
  }
}
</script>

<style>
.page-row {
  margin-top: 20px;
  text-align: center;
}
</style>
