<template>
  <div class="register-container" role="main" aria-label="注册页面">
    <div class="register-box">
      <h1 class="register-title" tabindex="0">用户注册</h1>
      <el-form ref="registerForm" :model="registerForm" :rules="registerRules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            aria-label="用户名"
            @keydown.enter="handleRegister"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
            aria-label="密码"
            @keydown.enter="handleRegister"
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password
            aria-label="确认密码"
            @keydown.enter="handleRegister"
          />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="请输入邮箱"
            aria-label="邮箱"
            @keydown.enter="handleRegister"
          />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input
            v-model="registerForm.realName"
            placeholder="请输入真实姓名（选填）"
            aria-label="真实姓名"
            @keydown.enter="handleRegister"
          />
        </el-form-item>
        <el-form-item label="视力状况">
          <el-select v-model="registerForm.visualImpairmentType" placeholder="请选择">
            <el-option label="正常" value="NORMAL" />
            <el-option label="低视力" value="LOW_VISION" />
            <el-option label="全盲" value="BLIND" />
            <el-option label="色弱/色盲" value="COLOR_BLIND" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            style="width: 100%;"
            @click="handleRegister"
            aria-label="注册"
          >
            注 册
          </el-button>
        </el-form-item>
      </el-form>
      <div style="text-align: center;">
        <router-link to="/login" aria-label="返回登录" tabindex="0">
          已有账号？返回登录
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Register',
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.registerForm.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    return {
      registerForm: {
        username: '',
        password: '',
        confirmPassword: '',
        email: '',
        realName: '',
        visualImpairmentType: ''
      },
      registerRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于 6 位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ]
      },
      loading: false
    }
  },
  methods: {
    async handleRegister() {
      this.$refs.registerForm.validate(async (valid) => {
        if (valid) {
          this.loading = true
          try {
            const formData = { ...this.registerForm }
            delete formData.confirmPassword
            await this.$store.dispatch('register', formData)
            this.$message.success('注册成功，请登录')
            this.$router.push('/login')
          } catch (error) {
            console.error('注册失败:', error)
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
