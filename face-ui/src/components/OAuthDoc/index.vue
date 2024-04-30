<template>
  <div class="container">
    <h1 class="title">如何接入我们的授权服务</h1>
    <div class="steps-container">
      <div class="step-group">
        <div class="step">
          <h2 class="step-title">步骤 1: 注册应用</h2>
          <p class="step-content">
            第三方网站需要先在我们的平台上注册一个应用,获取 Client ID 和 Client Secret。这些信息将用于后续的授权流程。
          </p>
        </div>
        <div class="step">
          <h2 class="step-title">步骤 3: 获取 Access Token</h2>
          <p class="step-content">
            在授权成功后,第三方网站需要使用授权中心返回的 code 参数,向我们的 Token 端点发送请求,换取 Access Token。
          </p>
          <div class="code-example">
            <h3 class="code-title">获取 Access Token 参数</h3>
            <el-table :data="tokenParams" stripe style="width: 100%">
              <el-table-column prop="name" label="参数名" width="180"></el-table-column>
              <el-table-column prop="description" label="说明" width="300"></el-table-column>
              <el-table-column prop="example" label="示例值" width="180"></el-table-column>
            </el-table>
            <h3 class="code-title">获取 Access Token 接口</h3>
            <p>POST http://localhost:8868/oauth/token</p>
          </div>
        </div>
      </div>
      <div class="step-group">
        <div class="step">
          <h2 class="step-title">步骤 2: 实现授权登录</h2>
          <p class="step-content">
            第三方网站需要在自己的前端页面上添加一个"授权登录"功能,当用户点击该功能时,会跳转到我们的授权中心页面进行身份验证和授权。
          </p>
          <div class="code-example">
            <h3 class="code-title">授权中心 URL 参数</h3>
            <el-table :data="authorizeParams" stripe style="width: 100%">
              <el-table-column prop="name" label="参数名" width="180"></el-table-column>
              <el-table-column prop="description" label="说明" width="300"></el-table-column>
              <el-table-column prop="example" label="示例值" width="180"></el-table-column>
            </el-table>
            <h3 class="code-title">授权中心接口</h3>
            <p>GET http://localhost:8868/oauth/authorize?client_id=${clientId}&redirect_uri=${redirectUri}</p>
          </div>
        </div>
        <div class="step">
          <h2 class="step-title">步骤 4: 获取用户信息</h2>
          <p class="step-content">
            有了 Access Token 后,第三方网站可以使用它来调用我们的 API,获取用户的基本信息,如姓名、手机号、身份证号等。
          </p>
          <div class="code-example">
            <h3 class="code-title">获取用户信息参数</h3>
            <el-table :data="userInfoParams" stripe style="width: 100%">
              <el-table-column prop="name" label="参数名" width="180"></el-table-column>
              <el-table-column prop="description" label="说明" width="300"></el-table-column>
              <el-table-column prop="example" label="示例值" width="180"></el-table-column>
            </el-table>
            <h3 class="code-title">获取用户信息接口</h3>
            <p>GET http://localhost:8868/oauth/user_info</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'IntegrationGuide',
  data() {
    return {
      authorizeParams: [
        { name: 'client_id', description: '第三方应用的 Client ID', example: '233358' },
        { name: 'redirect_uri', description: '第三方应用的回调地址', example: 'http://localhost:8080/otpyy' }
      ],
      tokenParams: [
        { name: 'grant_type', description: '授权类型,固定为 "authorization_code"', example: 'authorization_code' },
        { name: 'code', description: '授权中心返回的 code 参数', example: 'abc123' },
        { name: 'client_id', description: '第三方应用的 Client ID', example: '233358' },
        { name: 'client_secret', description: '第三方应用的 Client Secret', example: 'M3nU1PFOxX' },
        { name: 'redirect_uri', description: '第三方应用的回调地址', example: 'http://localhost:8080/otpyy' }
      ],
      userInfoParams: [
        { name: 'client_id', description: '第三方应用的 Client ID', example: '233358' },
        { name: 'access_token', description: '获取的 Access Token', example: 'abc123xyz' },
        { name: 'fid', description: '用户的唯一标识符', example: 'user123' }
      ]
    }
  }
}
</script>

<style scoped>
.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 50px 0;
}

.title {
  text-align: center;
  font-size: 24px;
  margin-bottom: 30px;
}

.steps-container {
  display: flex;
  justify-content: space-between;
  gap: 30px;
}

.step-group {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.step {
  background-color: #f5f5f5;
  padding: 12px;
  border-radius: 8px;
}

.step-title {
  font-size: 18px;
  margin-bottom: 5px;
}

.step-content {
  font-size: 14px;
}

.code-example {
  background-color: #f0f0f0;
  padding: 12px;
  border-radius: 4px;
  margin-top: 10px;
}

.code-title {
  font-size: 16px;
  margin-bottom: 5px;
}

.contact {
  margin-top: 30px;
  text-align: center;
}

.contact-title {
  font-size: 18px;
  margin-bottom: 5px;
}

.contact-content {
  font-size: 14px;
}
</style>