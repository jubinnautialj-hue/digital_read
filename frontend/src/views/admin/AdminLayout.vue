<template>
  <div class="admin-layout" role="application" aria-label="管理控制台">
    <div class="admin-sidebar">
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/admin/dashboard" tabindex="0">
          <i class="el-icon-s-data"></i>
          <span>管理控制台</span>
        </el-menu-item>
        <el-menu-item index="/admin/users" tabindex="0">
          <i class="el-icon-user"></i>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/documents" tabindex="0">
          <i class="el-icon-document"></i>
          <span>文档管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/analytics" tabindex="0">
          <i class="el-icon-s-data"></i>
          <span>数据分析</span>
        </el-menu-item>
        <el-menu-item index="/admin/accessibility" tabindex="0">
          <i class="el-icon-check"></i>
          <span>无障碍审计</span>
        </el-menu-item>
      </el-menu>
    </div>
    <div class="admin-content" id="main-content">
      <div class="user-header">
        <div>
          <span style="font-weight: bold;">数字无障碍阅读系统 - 管理端</span>
        </div>
        <div>
          <span style="margin-right: 15px;">管理员：{{ currentUser?.username }}</span>
          <el-button type="text" @click="logout" aria-label="退出登录">
            退出登录
          </el-button>
        </div>
      </div>
      <router-view />
    </div>
  </div>
</template>

<script>
export default {
  name: 'AdminLayout',
  computed: {
    currentUser() {
      return this.$store.getters.currentUser
    },
    activeMenu() {
      return this.$route.path
    }
  },
  methods: {
    logout() {
      this.$store.dispatch('logout')
      this.$router.push('/login')
      this.$message.success('已退出登录')
    }
  }
}
</script>
