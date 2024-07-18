<template>
  <el-container class="main">

    <!--头部区域-->
    <el-header>
      <div>
        <img :src="imageLogo" style="width: 40px;height: 40px;margin-left: 20px">
        <router-link to="/desktop" class="logo-title">&nbsp;&nbsp;FaceID身份认证与API开放平台</router-link>
      </div>
      <el-dropdown>
        <span class="el-dropdown-link" >
          {{username}}<i class="el-icon-arrow-down el-icon--right"></i>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item @click.native="loginOut">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </el-header>

    <!--页面主体区域-->
    <el-container>

      <!--侧边栏-->
      <el-aside style="width: 200px">
        <el-menu text-color="#008390" active-text-color="#ade2e0" class="el-menu-vertical-demo" :default-openeds="['1', '2', '3', '4']">

          <!-- 第一个主菜单 -->
          <el-submenu index="1">
            <template slot="title">
              <i class="el-icon-info" style="color: #008390"></i>
              <span slot="title">用户信息管理</span>
            </template>
            <router-link to="/faceList" style="text-decoration: none;">
              <el-menu-item index="1-1">登录人脸信息</el-menu-item>
            </router-link>
            <router-link to="/faceLog" style="text-decoration: none;">
              <el-menu-item index="1-2">识别登录日志</el-menu-item>
            </router-link>
            <router-link to="/ip" style="text-decoration: none;">
              <el-menu-item index="1-3">IP安全登录</el-menu-item>
            </router-link>
          </el-submenu>

          <!-- 第二个主菜单 -->
          <el-submenu index="2">
            <template slot="title">
              <i class="el-icon-mobile" style="color: #008390"></i>
              <span slot="title">多因素身份认证</span>
            </template>
            <router-link to="/phone" style="text-decoration: none;">
              <el-menu-item index="2-1">手机号绑定</el-menu-item>
            </router-link>
            <router-link to="/auth" style="text-decoration: none;">
              <el-menu-item index="2-2">身份证号码核验</el-menu-item>
            </router-link>
          </el-submenu>

          <!-- 第三个主菜单 -->
          <el-submenu index="3">
            <template slot="title">
              <i class="el-icon-setting" style="color: #008390"></i>
              <span slot="title">人脸API开放平台</span>
            </template>
            <router-link to="/myface" style="text-decoration: none;">
              <el-menu-item index="3-1">我的人脸数据库</el-menu-item>
            </router-link>
            <router-link to="/apikey" style="text-decoration: none;">
              <el-menu-item index="3-2">API信息中心</el-menu-item>
            </router-link>
            <router-link to="/faceApi" style="text-decoration: none;">
              <el-menu-item index="3-3">API在线识别</el-menu-item>
            </router-link>
            <router-link to="/apiLog" style="text-decoration: none;">
              <el-menu-item index="3-4">API请求日志</el-menu-item>
            </router-link>
          </el-submenu>

          <!-- 第四个主菜单 -->
          <el-submenu index="4">
            <template slot="title">
              <i class="el-icon-key" style="color: #008390"></i>
              <span slot="title">OAuth2 授权中心</span>
            </template>
            <router-link to="/otp" style="text-decoration: none;">
              <el-menu-item index="4-1">OTP动态口令</el-menu-item>
            </router-link>
            <router-link to="/oauth" style="text-decoration: none;">
              <el-menu-item index="4-2">OAuth2.0 授权服务</el-menu-item>
            </router-link>
            <router-link to="/oauthdoc" style="text-decoration: none;">
              <el-menu-item index="4-3">如何接入授权服务</el-menu-item>
            </router-link>
          </el-submenu>
        </el-menu>
      </el-aside>


      <!--右侧内容主体-->
      <el-main>
        <router-view></router-view>
      </el-main>

    </el-container>
  </el-container>
</template>

<script>


export default {
  name: "home",
  data() {
    return {
      username:'admin',
      imageLogo:require('@/assets/face_logo.png')
    }
  },
  created() {
    this.username = localStorage.getItem("username")
  },
  methods: {
    //退出登录
    loginOut() {
      localStorage.clear()
      this.$router.push("/")
    },
  }
}
</script>

<style>
@import "./index.css";
</style>