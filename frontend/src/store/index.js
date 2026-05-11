import Vue from 'vue'
import Vuex from 'vuex'
import { authApi, settingsApi, userApi } from '../api'

Vue.use(Vuex)

const getStoredUser = () => {
  try {
    const userStr = localStorage.getItem('user')
    return userStr ? JSON.parse(userStr) : null
  } catch (e) {
    return null
  }
}

const getStoredToken = () => {
  return localStorage.getItem('token') || ''
}

export default new Vuex.Store({
  state: {
    user: getStoredUser(),
    token: getStoredToken(),
    userSettings: null,
    currentActivity: null
  },
  getters: {
    isLoggedIn: state => !!state.token && !!state.user,
    isAdmin: state => state.user?.role === 'ADMIN',
    currentUser: state => state.user,
    userId: state => state.user?.id,
    userSettings: state => state.userSettings,
    currentActivity: state => state.currentActivity
  },
  mutations: {
    SET_USER(state, user) {
      state.user = user
      if (user) {
        localStorage.setItem('user', JSON.stringify(user))
      } else {
        localStorage.removeItem('user')
      }
    },
    SET_TOKEN(state, token) {
      state.token = token
      if (token) {
        localStorage.setItem('token', token)
      } else {
        localStorage.removeItem('token')
      }
    },
    SET_USER_SETTINGS(state, settings) {
      state.userSettings = settings
    },
    SET_CURRENT_ACTIVITY(state, activity) {
      state.currentActivity = activity
    },
    LOGOUT(state) {
      state.user = null
      state.token = ''
      state.userSettings = null
      state.currentActivity = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  },
  actions: {
    async login({ commit }, credentials) {
      const response = await authApi.login(credentials)
      commit('SET_TOKEN', response.data.token)
      commit('SET_USER', {
        id: response.data.userId,
        username: response.data.username,
        role: response.data.role
      })
      return response
    },
    async register({ commit }, userData) {
      return await authApi.register(userData)
    },
    async loadUserSettings({ commit, state, getters }) {
      if (getters.userId) {
        const response = await settingsApi.getByUserId(getters.userId)
        commit('SET_USER_SETTINGS', response.data)
      }
    },
    async updateUserSettings({ commit, state, getters }, settings) {
      if (getters.userId) {
        const response = await settingsApi.update(getters.userId, settings)
        commit('SET_USER_SETTINGS', response.data)
        return response
      }
    },
    setCurrentActivity({ commit }, activity) {
      commit('SET_CURRENT_ACTIVITY', activity)
    },
    async updateUserAvatar({ commit, getters }, formData) {
      if (getters.userId) {
        const response = await userApi.uploadAvatar(getters.userId, formData)
        const updatedUser = {
          ...getters.currentUser,
          avatar: response.data.data.avatar
        }
        commit('SET_USER', updatedUser)
        return response
      }
    },
    async refreshUserInfo({ commit, getters }) {
      if (getters.userId) {
        const response = await userApi.getById(getters.userId)
        const user = response.data.data
        const updatedUser = {
          id: user.id,
          username: user.username,
          role: user.role,
          avatar: user.avatar
        }
        commit('SET_USER', updatedUser)
        return response
      }
    },
    logout({ commit }) {
      commit('LOGOUT')
    }
  }
})
