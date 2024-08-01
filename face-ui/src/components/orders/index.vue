<template>
  <div>
    <el-table :data="paginatedOrders" style="width: 100%">
      <el-table-column prop="orderId" label="订单ID" />
      <el-table-column prop="orderAmount" label="订单金额" />
      <el-table-column prop="orderSubject" label="订单主题" />
      <el-table-column prop="orderDate" label="订单日期" />
      <el-table-column label="支付状态">
        <template slot-scope="scope">
          <el-tag :type="scope.row.payStatus === 1 ? 'success' : 'danger'">
            {{ scope.row.payStatus === 1 ? '已支付' : '未支付' }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="totalOrders"
        layout="total, prev, pager, next, jumper"
        style="margin-top: 20px;"
    />
  </div>
</template>

<script>
export default {
  data() {
    return {
      orders: [], // 存储所有订单
      paginatedOrders: [], // 存储当前页的订单
      currentPage: 1,
      pageSize: 10,
      totalOrders: 0,
    };
  },
  methods: {
    fetchOrders() {
      const fid = localStorage.getItem('user_id');

      this.$http.get(`/orders?fid=${fid}`)
          .then(response => {
            this.orders = response.data; // 假设返回的数据是订单数组
            this.totalOrders = this.orders.length; // 总订单数
            this.updatePaginatedOrders(); // 更新分页数据
          })
          .catch(error => {
            console.error("获取订单失败:", error);
          });
    },
    updatePaginatedOrders() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      this.paginatedOrders = this.orders.slice(start, end); // 根据当前页码和每页数量进行切片
    },
    handleCurrentChange(page) {
      this.currentPage = page;
      this.updatePaginatedOrders(); // 更新当前页的订单
    }
  },
  mounted() {
    this.fetchOrders();
  }
};
</script>

<style scoped>
/* 添加样式 */
</style>
