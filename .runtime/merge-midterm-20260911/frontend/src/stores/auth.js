import { defineStore } from 'pinia'
import request from '../api/request'

const TOKEN_KEY = 'stray_animal_token'
const USER_KEY = 'stray_animal_user'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem(TOKEN_KEY) || '',
    userInfo: JSON.parse(localStorage.getItem(USER_KEY) || 'null')
  }),
  getters: {
    isLoggedIn: state => Boolean(state.token)
  },
  actions: {
    setSession(token, userInfo) {
      this.token = token
      this.userInfo = userInfo
      localStorage.setItem(TOKEN_KEY, token)
      localStorage.setItem(USER_KEY, JSON.stringify(userInfo))
    },
    clearSession() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(USER_KEY)
    },
    async login(form) {
      const res = await request.post('/auth/login', form)
      this.setSession(res.data.token, res.data.userInfo)
      return res.data
    },
    async register(form) {
      const res = await request.post('/auth/register', form)
      return res.data
    },
    async registerVolunteer(form) {
      const res = await request.post('/auth/register-volunteer', form)
      return res.data
    },
    async fetchMe() {
      const res = await request.get('/auth/me')
      this.userInfo = res.data
      localStorage.setItem(USER_KEY, JSON.stringify(res.data))
      return res.data
    },
    async logout() {
      try {
        await request.post('/auth/logout')
      } finally {
        this.clearSession()
      }
    }
  }
})
