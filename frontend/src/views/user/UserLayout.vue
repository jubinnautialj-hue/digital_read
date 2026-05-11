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
        <div style="display: flex; align-items: center; gap: 15px;">
          <div class="avatar-container" @click="triggerAvatarUpload" tabindex="0" @keydown.enter="triggerAvatarUpload" aria-label="点击上传头像" role="button" style="cursor: pointer;">
            <el-avatar
              :size="48"
              :src="avatarUrl"
              style="border: 2px solid #409EFF;"
            >
              <i class="el-icon-user"></i>
            </el-avatar>
            <div class="avatar-overlay">
              <i class="el-icon-plus"></i>
            </div>
          </div>
          <span>欢迎，{{ currentUser?.username || '用户' }}</span>
          <input
            ref="avatarInput"
            type="file"
            accept="image/jpeg,image/jpg,image/png,image/gif,image/webp"
            style="display: none;"
            @change="handleAvatarChange"
          />
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
  data() {
    return {
      uploadingAvatar: false
    }
  },
  computed: {
    currentUser() {
      return this.$store.getters.currentUser
    },
    userSettings() {
      return this.$store.getters.userSettings
    },
    activeMenu() {
      return this.$route.path
    },
    avatarUrl() {
      if (this.currentUser?.avatar) {
        return '/api/files' + this.currentUser.avatar
      }
      return ''
    }
  },
  mounted() {
    this.$store.dispatch('loadUserSettings')
    this.$store.dispatch('refreshUserInfo').catch(() => {})
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
    triggerAvatarUpload() {
      if (!this.uploadingAvatar) {
        this.$refs.avatarInput.click()
      }
    },
    async handleAvatarChange(event) {
      const file = event.target.files?.[0]
      if (!file) return

      const validTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp']
      if (!validTypes.includes(file.type)) {
        this.$message.error('请选择有效的图片文件（JPG、PNG、GIF、WEBP）')
        this.clearFileInput()
        return
      }

      if (file.size > 5 * 1024 * 1024) {
        this.$message.error('图片大小不能超过5MB')
        this.clearFileInput()
        return
      }

      this.uploadingAvatar = true
      const loading = this.$loading({
        lock: true,
        text: '上传中...',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })

      try {
        const formData = new FormData()
        formData.append('file', file)

        await this.$store.dispatch('updateUserAvatar', formData)
        this.$message.success('头像上传成功')
      } catch (error) {
        console.error('头像上传失败:', error)
        const errorMsg = error.response?.data?.message || error.message || '头像上传失败'
        this.$message.error(errorMsg)
      } finally {
        loading.close()
        this.uploadingAvatar = false
        this.clearFileInput()
      }
    },
    clearFileInput() {
      if (this.$refs.avatarInput) {
        this.$refs.avatarInput.value = ''
      }
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

<style scoped>
.avatar-container {
  position: relative;
  display: inline-block;
  transition: transform 0.2s ease;
}

.avatar-container:hover {
  transform: scale(1.05);
}

.avatar-container:hover .avatar-overlay {
  opacity: 1;
}

.avatar-container:focus {
  outline: 2px solid #409EFF;
  outline-offset: 2px;
  border-radius: 50%;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 50%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s ease;
  color: white;
  font-size: 24px;
}
</style>
