import Vue from 'vue'
import VueRouter from 'vue-router'
import login from '../views/login/index.vue'
import desktop from '../components/desktop/index.vue'
import otpaouth from "../views/otpaouth/index.vue";
import otpyy from "../views/otpyy/index.vue";

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'login',
    component: login
  },
  {
    path: '/otpaouth',
    name: 'otpaouth',
    component: otpaouth
  },
  {
    path: '/otpyy',
    name: 'otpyy',
    component: otpyy
  },
  {
    path: '/home',
    name: 'home',
    component: () => import('@/views/home/index.vue'),
    redirect: '/desktop',
    children:[
      {
        path: '/desktop',
        name: 'desktop',
        component: desktop
      },
      {
        path: '/faceList',
        name: 'face',
        component: () => import('@/components/face/index.vue')
      },
      {
        path: '/faceLog',
        name: 'log',
        component: () => import('@/components/faceLog/index.vue')
      },
      {
        path: '/auth',
        name: 'auth',
        component: () => import('@/components/IdAuth/index.vue')
      },
      {
        path: '/apikey',
        name: 'apikey',
        component: () => import('@/components/apiKey/index.vue')
      },
      {
        path: '/faceApi',
        name: 'faceApi',
        component: () => import('@/components/faceApi/index.vue')
      },
      {
        path: '/phone',
        name: 'phone',
        component: () => import('@/components/phoneVef/index.vue')
      },
      {
        path: '/ip',
        name: 'ip',
        component: () => import('@/components/ipList/index.vue')
      },
      {
        path: '/apiLog',
        name: 'apiLog',
        component: () => import('@/components/apiLog/index.vue')
      },
      {
        path: '/otp',
        name: 'otp',
        component: () => import('@/components/otp/index.vue')
      },
      {
        path: '/oauth',
        name: 'oauth',
        component: () => import('@/components/OAUTH/index.vue')
      },
      {
        path: '/oauthdoc',
        name: 'oauthdoc',
        component: () => import('@/components/OAuthDoc/index.vue')
      },
      {
        path: '/myface',
        name: 'myface',
        component: () => import('@/components/myface/index.vue')
      }
    ]
  }
]

const router = new VueRouter({
  mode: 'history',
  routes
})

router.beforeEach((to, from, next) => {
  if (to.path === '/' || to.path === '/otpaouth' || to.path === '/otpyy') {
    return next();
  }
  let face_token = localStorage.getItem("face_token");
  if (!face_token) {
    return next("/");
  }
  next();
})

export default router