<template>
  <el-container class="main">

    <!--头部区域-->
    <el-header>
      <div>
        <img :src="imageLogo" style="width: 40px;height: 40px;margin-left: 20px">
        <router-link to="/desktop" class="logo-title">&nbsp;&nbsp;FaceID 身份认证系统</router-link>
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
        <el-menu text-color="#008390" active-text-color="#ade2e0" class="el-menu-vertical-demo" default-openeds="['1']">
          <el-submenu index="1">
            <template slot="title">
              <i class="el-icon-user" style="color: #008390"></i>
              <span slot="title">Auth身份鉴权中心</span>
            </template>

            <router-link class="router-link-active" to="/faceList">
              <el-menu-item index="1-1">
                登录人脸信息
              </el-menu-item>
            </router-link>
            <router-link class="router-link-active" to="/faceLog">
              <el-menu-item index="1-2">
                识别登录日志
              </el-menu-item>
            </router-link>
            <router-link class="router-link-active" to="/ip">
              <el-menu-item index="1-3">
                IP安全登录
              </el-menu-item>
            </router-link>
            <router-link class="router-link-active" to="/phone">
              <el-menu-item index="1-4">
                手机号绑定
              </el-menu-item>
            </router-link>
            <router-link class="router-link-active" to="/auth">
              <el-menu-item index="1-5">
                身份证号码核验
              </el-menu-item>
            </router-link>
            <router-link class="router-link-active" to="/myface">
              <el-menu-item index="1-6">
                我的人脸信息库
              </el-menu-item>
            </router-link>
            <router-link class="router-link-active" to="/apikey">
              <el-menu-item index="1-7">
                API信息中心
              </el-menu-item>
            </router-link>
            <router-link class="router-link-active" to="/faceApi">
              <el-menu-item index="1-8">
                API在线识别
              </el-menu-item>
            </router-link>
            <router-link class="router-link-active" to="/apiLog">
              <el-menu-item index="1-9">
                API请求日志
              </el-menu-item>
            </router-link>
            <router-link class="router-link-active" to="/otp">
              <el-menu-item index="1-10">
                OTP动态口令
              </el-menu-item>
            </router-link>
            <router-link class="router-link-active" to="/oauth">
              <el-menu-item index="1-11">
                OAuth2.0 授权服务
              </el-menu-item>
            </router-link>
            <router-link class="router-link-active" to="/oauthdoc">
              <el-menu-item index="1-12">
                如何接入授权服务
              </el-menu-item>
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