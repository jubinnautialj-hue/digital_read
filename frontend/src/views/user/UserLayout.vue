<template>
  <div class="user-layout" role="application" aria-label="用户中心">
    <div class="user-sidebar">
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/user/dashboard" tabindex="0">
          <i class="el-icon-s-home"></i>
          <span>用户中心</span>
        </el-menu-item>
        <el-menu-item index="/user/documents" tabindex="0">
          <i class="el-icon-document"></i>
          <span>文档库</span>
        </el-menu-item>
        <el-menu-item index="/user/settings" tabindex="0">
          <i class="el-icon-setting"></i>
          <span>个人设置</span>
        </el-menu-item>
      </el-menu>
    </div>
    <div class="user-content" id="main-content">
      <div class="user-header">
        <div>
          <span>欢迎，{{ currentUser?.username || '用户' }}</span>
        </div>
        <div>
          <el-button type="text" @click="toggleHighContrast" aria-label="切换高对比度">
            {{ userSettings?.highContrast ? '关闭高对比度' : '开启高对比度' }}
          </el-button>
          <el-button type="text" @click="logout" aria-label="退出登录">
            退出登录
          </el-button>
        </div>
      </div>
      <router-view />
    </div>
    <div class="keyboard-hint" style="position: fixed; bottom: 10px; left: 50%; transform: translateX(-50%);">
      快捷键：<kbd>Alt + 1</kbd> 用户中心 <kbd>Alt + 2</kbd> 文档库 <kbd>Alt + 3</kbd> 个人设置 <kbd>Alt + Q</kbd> 退出
    </div>
  </div>
</template>

<script>
export default {
  name: 'UserLayout',
  computed: {
    currentUser() {
      return this.$store.getters.currentUser
    },
    userSettings() {
      return this.$store.getters.userSettings
    },
    activeMenu() {
      return this.$route.path
    }
  },
  mounted() {
    this.$store.dispatch('loadUserSettings')
    this.handleKeyboardShortcuts()
  },
  methods: {
    handleKeyboardShortcuts() {
      document.addEventListener('keydown', (e) => {
        if (e.altKey) {
          switch (e.key) {
            case '1':
              e.preventDefault()
              this.$router.push('/user/dashboard')
              break
            case '2':
              e.preventDefault()
              this.$router.push('/user/documents')
              break
            case '3':
              e.preventDefault()
              this.$router.push('/user/settings')
              break
            case 'q':
            case 'Q':
              e.preventDefault()
              this.logout()
              break
          }
        }
      })
    },
    async toggleHighContrast() {
      const currentValue = this.userSettings?.highContrast || false
      await this.$store.dispatch('updateUserSettings', {
        highContrast: !currentValue
      })
    },
    logout() {
      this.$store.dispatch('logout')
      this.$router.push('/login')
      this.$message.success('已退出登录')
    }
  }
}
</script>
