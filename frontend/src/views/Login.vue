<template>
  <div class="login-container" role="main" aria-label="登录页面">
    <div class="login-box">
      <h1 class="login-title" tabindex="0">数字无障碍阅读系统</h1>
      <el-form ref="loginForm" :model="loginForm" :rules="loginRules" label-width="0">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="el-icon-user"
            size="large"
            aria-label="用户名"
            @keydown.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="el-icon-lock"
            size="large"
            show-password
            aria-label="密码"
            @keydown.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            style="width: 100%;"
            @click="handleLogin"
            aria-label="登录"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>
      <div style="text-align: center; margin-top: 20px;">
        <router-link to="/register" aria-label="去注册" tabindex="0">
          还没有账号？立即注册
        </router-link>
      </div>
      <div class="keyboard-hint" style="margin-top: 20px;">
        <strong>键盘快捷键提示：</strong>
        <kbd>Tab</kbd> 切换焦点，<kbd>Enter</kbd> 确认登录，<kbd>Alt + L</kbd> 快速登录
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    const validateUsername = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入用户名'))
      } else {
        callback()
      }
    }
    const validatePassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入密码'))
      } else {
        callback()
      }
    }
    return {
      loginForm: {
        username: '',
        password: ''
      },
      loginRules: {
        username: [{ required: true, validator: validateUsername, trigger: 'blur' }],
        password: [{ required: true, validator: validatePassword, trigger: 'blur' }]
      },
      loading: false
    }
  },
  mounted() {
    this.handleKeyboardShortcuts()
  },
  methods: {
    handleKeyboardShortcuts() {
      document.addEventListener('keydown', (e) => {
        if (e.altKey && e.key === 'l') {
          e.preventDefault()
          this.handleLogin()
        }
      })
    },
    async handleLogin() {
      this.$refs.loginForm.validate(async (valid) => {
        if (valid) {
          this.loading = true
          try {
            await this.$store.dispatch('login', this.loginForm)
            await this.$store.dispatch('loadUserSettings')
            this.$message.success('登录成功')
            const role = this.$store.getters.currentUser?.role
            if (role === 'ADMIN') {
              this.$router.push('/admin/dashboard')
            } else {
              this.$router.push('/user/dashboard')
            }
          } catch (error) {
            console.error('登录失败:', error)
          } finally {
            this.loading = false
          }
        } else {
          return false
        }
      })
    }
  }
}
</script>
