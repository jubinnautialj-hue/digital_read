<template>
  <div role="main" aria-label="用户管理">
    <h2 tabindex="0" style="margin-bottom: 20px;">用户管理</h2>
    
    <div style="margin-bottom: 20px; display: flex; gap: 10px;">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索用户名或邮箱..."
        style="width: 300px;"
        clearable
        @keyup.enter="loadUsers"
      >
        <el-button slot="append" icon="el-icon-search" @click="loadUsers">搜索</el-button>
      </el-input>
      <el-button type="primary" @click="openCreateDialog" aria-label="添加用户">
        <i class="el-icon-plus"></i> 添加用户
      </el-button>
    </div>

    <el-table :data="filteredUsers" style="width: 100%;" border aria-label="用户列表">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" width="150" />
      <el-table-column prop="email" label="邮箱" width="200" />
      <el-table-column prop="realName" label="真实姓名" width="120" />
      <el-table-column prop="role" label="角色" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.role === 'ADMIN' ? 'danger' : 'primary'" size="small">
            {{ scope.row.role === 'ADMIN' ? '管理员' : '普通用户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="visualImpairmentType" label="视力状况" width="120">
        <template slot-scope="scope">
          {{ getVisualTypeLabel(scope.row.visualImpairmentType) }}
        </template>
      </el-table-column>
      <el-table-column prop="enabled" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.enabled ? 'success' : 'info'" size="small">
            {{ scope.row.enabled ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="lastLoginTime" label="最后登录" width="180">
        <template slot-scope="scope">
          {{ formatDate(scope.row.lastLoginTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="primary" @click="openEditDialog(scope.row)" aria-label="编辑">编辑</el-button>
          <el-button size="mini" type="danger" @click="deleteUser(scope.row)" aria-label="删除">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form ref="userForm" :model="userForm" :rules="userRules" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input v-model="userForm.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="userForm.realName" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="userForm.role">
            <el-option label="普通用户" value="USER" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>
        <el-form-item label="视力状况">
          <el-select v-model="userForm.visualImpairmentType">
            <el-option label="正常" value="NORMAL" />
            <el-option label="低视力" value="LOW_VISION" />
            <el-option label="全盲" value="BLIND" />
            <el-option label="色弱/色盲" value="COLOR_BLIND" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch
            v-model="userForm.enabled"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveUser">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { userApi } from '@/api'

export default {
  name: 'UserManagement',
  data() {
    return {
      users: [],
      searchKeyword: '',
      dialogVisible: false,
      isEdit: false,
      dialogTitle: '添加用户',
      userForm: {
        id: null,
        username: '',
        password: '',
        email: '',
        realName: '',
        role: 'USER',
        visualImpairmentType: '',
        enabled: true
      },
      userRules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      }
    }
  },
  computed: {
    filteredUsers() {
      if (!this.searchKeyword) return this.users
      const keyword = this.searchKeyword.toLowerCase()
      return this.users.filter(u =>
        u.username?.toLowerCase().includes(keyword) ||
        u.email?.toLowerCase().includes(keyword)
      )
    }
  },
  mounted() {
    this.loadUsers()
  },
  methods: {
    async loadUsers() {
      try {
        const res = await userApi.getAll()
        this.users = res.data || []
      } catch (error) {
        console.error('加载用户失败:', error)
      }
    },
    openCreateDialog() {
      this.isEdit = false
      this.dialogTitle = '添加用户'
      this.userForm = {
        id: null,
        username: '',
        password: '',
        email: '',
        realName: '',
        role: 'USER',
        visualImpairmentType: '',
        enabled: true
      }
      this.dialogVisible = true
    },
    openEditDialog(user) {
      this.isEdit = true
      this.dialogTitle = '编辑用户'
      this.userForm = { ...user }
      this.dialogVisible = true
    },
    async saveUser() {
      try {
        if (this.isEdit) {
          await userApi.update(this.userForm.id, this.userForm)
          this.$message.success('更新成功')
        } else {
          await userApi.create(this.userForm)
          this.$message.success('创建成功')
        }
        this.dialogVisible = false
        this.loadUsers()
      } catch (error) {
        console.error('保存失败:', error)
      }
    },
    deleteUser(user) {
      this.$confirm(`确定要删除用户 "${user.username}" 吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await userApi.delete(user.id)
          this.$message.success('删除成功')
          this.loadUsers()
        } catch (error) {
          console.error('删除失败:', error)
        }
      }).catch(() => {})
    },
    getVisualTypeLabel(type) {
      const labels = {
        NORMAL: '正常',
        LOW_VISION: '低视力',
        BLIND: '全盲',
        COLOR_BLIND: '色弱/色盲'
      }
      return labels[type] || '未知'
    },
    formatDate(dateStr) {
      if (!dateStr) return '从未登录'
      return new Date(dateStr).toLocaleString('zh-CN')
    }
  }
}
</script>
