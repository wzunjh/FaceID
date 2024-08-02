<template>
  <div>
    <el-table :data="paginatedOrders" style="width: 100%">
      <el-table-column prop="orderId" label="订单编号" />
      <el-table-column prop="orderAmount" label="订单金额(元)" />
      <el-table-column prop="orderSubject" label="商品名称" />
      <el-table-column prop="orderDate" label="下单日期" />
      <el-table-column label="支付状态">
        <template slot-scope="scope">
          <el-tag :type="scope.row.payStatus === 1 ? 'success' : 'danger'">
            {{ scope.row.payStatus === 1 ? '已支付' : '未支付' }}
          </el-tag>
          <el-button v-if="scope.row.payStatus !== 1" type="primary" size="small" @click="payAgain(scope.row.orderId)">去支付</el-button>
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

    <el-dialog v-if="selectedOrder" title="扫码支付" :visible.sync="selectedOrder" width="400px">
      <div class="dialog-content">
        <template v-if="paymentStatus === '支付成功'">
          <el-result class="result" icon="success" title="支付成功" sub-title="感谢您的支付！">
            <template slot="extra">
              <el-button type="primary" size="medium" @click="goToOrders">返回</el-button>
            </template>
          </el-result>
        </template>
        <template v-else>
          <div class="qr-container">
            <img v-if="qrCode" :src="qrCode" alt="支付二维码" />
            <p v-if="qrCode">请使用支付宝扫描二维码进行支付</p>
            <p v-if="paymentStatus">{{ paymentStatus }}</p>
          </div>
        </template>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="selectedOrder = null">关闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      orders: [],
      paginatedOrders: [],
      currentPage: 1,
      pageSize: 10,
      totalOrders: 0,
      selectedOrder: null,
      qrCode: null,
      paymentStatus: null,
      paymentInterval: null,
    };
  },
  methods: {
    fetchOrders() {
      const fid = localStorage.getItem('user_id');

      this.$http.get(`/orders?fid=${fid}`)
          .then(response => {
            this.orders = response.data;
            this.totalOrders = this.orders.length;
            this.updatePaginatedOrders();
          })
          .catch(error => {
            console.error("获取订单失败:", error);
          });
    },
    updatePaginatedOrders() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      this.paginatedOrders = this.orders.slice(start, end);
    },
    handleCurrentChange(page) {
      this.currentPage = page;
      this.updatePaginatedOrders();
    },
    goToOrders() {
      this.$router.push('/orders');
    },
    payAgain(orderId) {
      this.selectedOrder = orderId;
      this.qrCode = null;
      this.paymentStatus = null;

      this.$http.get(`/payagain?outTradeNo=${orderId}`).then(response => {
        this.qrCode = response.data.qrBase64;
        this.startPaymentQuery(orderId);
      }).catch(error => {
        console.error('获取支付二维码失败:', error);
      });
    },
    startPaymentQuery(orderId) {
      this.paymentInterval = setInterval(() => {
        this.$http.get(`/notify?outTradeNo=${orderId}`).then(response => {
          if (response.data.code === 200) {
            this.paymentStatus = '支付成功';
            this.qrCode = null;
            clearInterval(this.paymentInterval);
          } else {
            this.paymentStatus = '支付中...';
          }
        }).catch(error => {
          console.error('查询支付状态失败:', error);
        });
      }, 2000);
    },
  },
  mounted() {
    this.fetchOrders();
  },
  beforeDestroy() {
    if (this.paymentInterval) {
      clearInterval(this.paymentInterval);
    }
  },
};
</script>
<style>

.goods-card .el-row {
  padding: 20px; /* 增加内边距 */
}

.dialog-content {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column; /* 让内容垂直排列 */
}

.qr-container {
  text-align: center; /* 确保二维码和文本居中 */
}

.result {
  width: 80%; /* 让 el-result 占满弹窗的宽度 */
}

/* 隐藏左右滑动条 */
</style>