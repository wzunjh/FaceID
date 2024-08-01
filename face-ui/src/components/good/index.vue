<template>
  <el-container id="app">
    <el-main>
      <el-list>
        <el-list-item v-for="item in goodsList" :key="item.goodsId" header="套餐信息">
          <el-card class="goods-card" :body-style="{ padding: '20px' }">
            <el-row>
              <el-col :span="12">
                <span class="goods-name">{{ item.goodsName }}</span>
                <el-tag class="goods-price" type="info">{{ item.goodsPrice }} 元</el-tag>
              </el-col>
              <el-col :span="12" class="text-right">
                <el-button type="primary" @click="selectGoods(item.goodsId)">下单</el-button>
              </el-col>
            </el-row>
          </el-card>
        </el-list-item>
      </el-list>

      <el-dialog v-if="selectedGoods" title="扫码支付" :visible.sync="selectedGoods" width="400px">
        <div class="dialog-content">
          <template v-if="paymentStatus === '支付成功'">
            <el-result class="result" icon="success" title="支付成功" sub-title="感谢您的支付！">
              <template slot="extra">
                <el-button type="primary" size="medium" @click="selectedGoods = null">返回</el-button>
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
          <el-button @click="selectedGoods = null">关闭</el-button>
        </span>
      </el-dialog>
    </el-main>
  </el-container>
</template>

<script>
export default {
  data() {
    return {
      goodsList: [],
      selectedGoods: null,
      qrCode: null,
      paymentStatus: null,
      paymentInterval: null,
    };
  },
  created() {
    this.loadGoods();
  },
  methods: {
    loadGoods() {
      this.$http.get('/goods').then(response => {
        this.goodsList = response.data;
      }).catch(error => {
        console.error('获取商品列表失败:', error);
      });
    },
    selectGoods(goodsId) {
      this.selectedGoods = goodsId;
      this.qrCode = null;
      this.paymentStatus = null;

      const fid = localStorage.getItem('user_id'); // 从 localStorage 获取 fid
      this.$http.get(`/payment?fid=${fid}&GoodsId=${goodsId}`).then(response => {
        this.qrCode = response.data.qrBase64;
        this.startPaymentQuery(response.data.orderId);
      }).catch(error => {
        console.error('下单失败:', error);
      });
    },
    startPaymentQuery(orderId) {
      this.paymentInterval = setInterval(() => {
        this.$http.get(`/notify?outTradeNo=${orderId}`).then(response => {
          if (response.data.code === 200) {
            this.paymentStatus = '支付成功';
            this.qrCode = null; // 隐藏二维码
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
  beforeDestroy() {
    if (this.paymentInterval) {
      clearInterval(this.paymentInterval);
    }
  },
};
</script>

<style>
#app {
  max-width: 600px;
  margin: 0 auto;
  text-align: center;
}

.el-list-item {
  margin: 10px 0;
}

.goods-card {
  margin-bottom: 10px; /* 为每个卡片增加底部间距 */
  border-radius: 10px; /* 增加圆角 */
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); /* 添加阴影效果 */
  transition: transform 0.2s; /* 增加过渡效果 */
  background-color: #ffffff; /* 设置背景色 */
}

.goods-card:hover {
  transform: scale(1.02); /* 鼠标悬停时放大效果 */
}

.goods-card .el-row {
  padding: 20px; /* 增加内边距 */
}

.goods-name {
  font-size: 18px; /* 调整商品名称字体大小 */
  display: inline-block; /* 使得标签和价格在同一行 */
  vertical-align: middle; /* 垂直对齐 */
}

.goods-price {
  margin-left: 10px; /* 标签与商品名称之间的间距 */
  vertical-align: middle; /* 垂直对齐 */
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
  width: 100%; /* 让 el-result 占满弹窗的宽度 */
}
</style>
