<template>
  <el-container class="main">

    <!--头部区域-->
    <el-header>
      <div>
        <img :src="imageLogo" style="width: 40px;height: 40px;margin-left: 20px">
        <router-link to="/desktop" class="logo-title">&nbsp;&nbsp;FaceID</router-link>
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
        <el-menu text-color="#008390"  active-text-color="#ade2e0" class="el-menu-vertical-demo" >
          <el-submenu index="1">

            <template slot="title">
              <i class="el-icon-user" style="color: #008390"></i>
              <span slot="title">人脸管理</span>
            </template>

            <router-link class="router-link-active"  to="/faceList">
                <el-menu-item index="1-1">
                  人脸列表
                </el-menu-item>
            </router-link>
            <router-link class="router-link-active" to="/faceLog">
                <el-menu-item index="1-2">
                  日志列表
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