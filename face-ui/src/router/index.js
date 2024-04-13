import Vue from 'vue'
import VueRouter from 'vue-router'
import login from '../views/login/index.vue'
import desktop from '../components/desktop/index.vue'
Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'login',
    component: login
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
      }
    ]
  }
]

const router = new VueRouter({
  routes
})


router.beforeEach((to,from,next)=>{
  if(to.path === '/') return next()
  let face_token = localStorage.getItem("face_token");
  if (!face_token) return next("/")
  next()
})

export default router
