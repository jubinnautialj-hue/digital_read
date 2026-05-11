import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import './assets/styles/global.scss'
import keyboardNav from './utils/keyboardNavigation'

Vue.config.productionTip = false
Vue.use(ElementUI)

new Vue({
  router,
  store,
  render: h => h(App),
  created() {
    keyboardNav.init()
    keyboardNav.registerHandler('navigateTo', (cmd) => {
      if (cmd && cmd.target) {
        this.$router.push(cmd.target)
      }
    })
  }
}).$mount('#app')
