import Vue from 'vue'
import Router from 'vue-router'
import login from '@/views/login/index'
import home from '@/views/home/index'
import test from '@/views/test/index'
import upload from '@/views/upload/index'
import notFoundPage from '@/views/404'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'index',
      component: login
    },
    {
      path: '/login.html',
      name: 'login',
      component: login
    },
    {
      path: '/home.html',
      name: 'home',
      component: home
    },
    {
      path: '/upload.html',
      name: 'upload',
      component: upload
    },
    {
      path: '/test.html',
      name: 'test',
      component: test
    },
    {
      path: '/404',
      component: notFoundPage,
      hidden: true
    },
    {
      path: '*',
      redirect: '/404',
      hidden: true
    }
  ]
})
