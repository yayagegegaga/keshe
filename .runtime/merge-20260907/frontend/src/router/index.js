import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import AnimalList from '../views/animal/AnimalList.vue'
import AnimalDetail from '../views/animal/AnimalDetail.vue'
import AnimalManage from '../views/animal/AnimalManage.vue'
import RescueClueSubmit from '../views/rescue/RescueClueSubmit.vue'
import RescueClueManage from '../views/rescue/RescueClueManage.vue'
import RescueOrderList from '../views/rescue/RescueOrderList.vue'
import RescueOrderDetail from '../views/rescue/RescueOrderDetail.vue'
import RescueOrderManage from '../views/rescue/RescueOrderManage.vue'
import VolunteerTaskList from '../views/task/VolunteerTaskList.vue'
import VolunteerTaskDetail from '../views/task/VolunteerTaskDetail.vue'
import VolunteerTaskManage from '../views/task/VolunteerTaskManage.vue'
import MyVolunteerTask from '../views/task/MyVolunteerTask.vue'
import AdoptionApply from '../views/adoption/AdoptionApply.vue'
import MyAdoptionList from '../views/adoption/MyAdoptionList.vue'
import AdoptionManage from '../views/adoption/AdoptionManage.vue'
import AdoptionDetail from '../views/adoption/AdoptionDetail.vue'
import FollowUpList from '../views/follow/FollowUpList.vue'
import FollowUpCreate from '../views/follow/FollowUpCreate.vue'
import FollowUpDetail from '../views/follow/FollowUpDetail.vue'
import NotificationList from '../views/notification/NotificationList.vue'
import FileUpload from '../views/file/FileUpload.vue'
import { useAuthStore } from '../stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home,
      meta: { requiresAuth: true }
    },
    {
      path: '/login',
      name: 'login',
      component: Login
    },
    {
      path: '/register',
      name: 'register',
      component: Register
    },
    {
      path: '/animals',
      name: 'animals',
      component: AnimalList
    },
    {
      path: '/animals/:id',
      name: 'animalDetail',
      component: AnimalDetail
    },
    {
      path: '/animal-manage',
      name: 'animalManage',
      component: AnimalManage,
      meta: { requiresAuth: true, adminOnly: true }
    },
    {
      path: '/rescue-clues/submit',
      name: 'rescueClueSubmit',
      component: RescueClueSubmit,
      meta: { requiresAuth: true }
    },
    {
      path: '/rescue-clues/manage',
      name: 'rescueClueManage',
      component: RescueClueManage,
      meta: { requiresAuth: true, adminOnly: true }
    },
    {
      path: '/rescue-orders',
      name: 'rescueOrders',
      component: RescueOrderList,
      meta: { requiresAuth: true }
    },
    {
      path: '/rescue-orders/:id',
      name: 'rescueOrderDetail',
      component: RescueOrderDetail,
      meta: { requiresAuth: true }
    },
    {
      path: '/rescue-orders-manage',
      name: 'rescueOrderManage',
      component: RescueOrderManage,
      meta: { requiresAuth: true, adminOnly: true }
    },
    {
      path: '/volunteer-tasks',
      name: 'volunteerTasks',
      component: VolunteerTaskList,
      meta: { requiresAuth: true }
    },
    {
      path: '/volunteer-tasks/my',
      name: 'myVolunteerTasks',
      component: MyVolunteerTask,
      meta: { requiresAuth: true }
    },
    {
      path: '/volunteer-tasks/:id',
      name: 'volunteerTaskDetail',
      component: VolunteerTaskDetail,
      meta: { requiresAuth: true }
    },
    {
      path: '/volunteer-task-manage',
      name: 'volunteerTaskManage',
      component: VolunteerTaskManage,
      meta: { requiresAuth: true, adminOnly: true }
    },
    {
      path: '/adoptions/apply/:animalId',
      name: 'adoptionApply',
      component: AdoptionApply,
      meta: { requiresAuth: true }
    },
    {
      path: '/adoptions/my',
      name: 'myAdoptions',
      component: MyAdoptionList,
      meta: { requiresAuth: true }
    },
    {
      path: '/adoptions/manage',
      name: 'adoptionManage',
      component: AdoptionManage,
      meta: { requiresAuth: true, adminOnly: true }
    },
    {
      path: '/adoptions/:id',
      name: 'adoptionDetail',
      component: AdoptionDetail,
      meta: { requiresAuth: true }
    },
    {
      path: '/follow-ups',
      name: 'followUps',
      component: FollowUpList,
      meta: { requiresAuth: true }
    },
    {
      path: '/follow-ups/create',
      name: 'followUpCreate',
      component: FollowUpCreate,
      meta: { requiresAuth: true, adminOnly: true }
    },
    {
      path: '/follow-ups/:id',
      name: 'followUpDetail',
      component: FollowUpDetail,
      meta: { requiresAuth: true }
    },
    {
      path: '/notifications',
      name: 'notifications',
      component: NotificationList,
      meta: { requiresAuth: true }
    },
    {
      path: '/files/upload',
      name: 'fileUpload',
      component: FileUpload,
      meta: { requiresAuth: true }
    }
  ]
})

router.beforeEach(to => {
  const authStore = useAuthStore()
  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    return '/login'
  }
  if (to.meta.adminOnly) {
    const roles = authStore.userInfo?.roles || []
    if (!roles.includes('ADMIN') && !roles.includes('SUPER_ADMIN')) {
      return '/'
    }
  }
  if ((to.path === '/login' || to.path === '/register') && authStore.isLoggedIn) {
    return '/'
  }
  return true
})

export default router
