<template>
    <div class="desktop">
        <div class="desktop-main"></div>
        <div class="author-title">
          <p>基于InsightFace与SpringBoot的身份认证系统</p>
            <p>本系统由<a href="https://github.com/wzunjh">家辉酱</a>全栈开发</p>
        </div>
    </div>
</template>

<script>
export default {
name: "index"
}
</script>

<style>
.desktop{
  width: 100%;
  height: 100%;
  position: relative;
}
.desktop-main{
  position: absolute;
  width: 45%;
  height: 63%;
  transform: translate(-50%,-50%);
  left: 50%;
  top:40%;
  background: url(~@/assets/desktop/desktop.gif) no-repeat;
  background-size: 100% 100%;
}
.author-title{
  width: 100%;
  height: 98px;
  background-color: #008390;
  position: absolute;
  transform: translate(-50%,-50%);
  top: 95%;
  left: 50%;
  text-align: center;
  font-size: 13px;
  color: #fff;
}
</style>