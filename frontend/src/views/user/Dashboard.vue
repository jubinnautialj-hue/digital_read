<template>
  <div role="main" aria-label="用户中心">
    <h2 tabindex="0" style="margin-bottom: 20px;">用户中心</h2>
    
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="stats-card" tabindex="0">
          <div class="stats-label">可用文档</div>
          <div class="stats-value">{{ docStats.accessible || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stats-card" tabindex="0">
          <div class="stats-label">今日阅读</div>
          <div class="stats-value">{{ activityAnalytics.todayActivities || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stats-card" tabindex="0">
          <div class="stats-label">本周阅读</div>
          <div class="stats-value">{{ activityAnalytics.weekActivities || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>快捷操作</span>
            </div>
          </template>
          <div style="display: flex; flex-direction: column; gap: 10px;">
            <el-button type="primary" @click="goToDocuments" aria-label="浏览文档库">
              <i class="el-icon-document"></i> 浏览文档库
            </el-button>
            <el-button @click="goToSettings" aria-label="打开个人设置">
              <i class="el-icon-setting"></i> 个人设置
            </el-button>
            <el-button type="success" @click="startQuickRead" aria-label="快速阅读">
              <i class="el-icon-reading"></i> 快速阅读模式
            </el-button>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最近阅读</span>
            </div>
          </template>
          <el-empty v-if="recentActivities.length === 0" description="暂无阅读记录" />
          <div v-else>
            <div
              v-for="activity in recentActivities"
              :key="activity.id"
              style="padding: 10px; border-bottom: 1px solid #eee; cursor: pointer;"
              @click="goToDocument(activity.document?.id)"
              tabindex="0"
              @keydown.enter="goToDocument(activity.document?.id)"
            >
              <div style="font-weight: bold;">{{ activity.document?.title }}</div>
              <div style="font-size: 12px; color: #909399;">
                进度: {{ activity.progressPercent }}% | {{ formatDate(activity.startTime) }}
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>键盘快捷键</span>
        </div>
      </template>
      <div class="keyboard-hint">
        <div style="margin-bottom: 10px;"><strong>导航快捷键：</strong></div>
        <div>
          <kbd>Tab</kbd> 切换焦点 |
          <kbd>Enter</kbd> 确认/打开 |
          <kbd>Esc</kbd> 取消/关闭 |
          <kbd>Alt + 1</kbd> 用户中心 |
          <kbd>Alt + 2</kbd> 文档库 |
          <kbd>Alt + 3</kbd> 设置
        </div>
        <div style="margin: 10px 0;"><strong>阅读快捷键：</strong></div>
        <div>
          <kbd>Space</kbd> 播放/暂停语音 |
          <kbd>←</kbd> 上一段 |
          <kbd>→</kbd> 下一段 |
          <kbd>↑</kbd> 加快语速 |
          <kbd>↓</kbd> 减慢语速
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { documentApi, activityApi } from '@/api'

export default {
  name: 'UserDashboard',
  data() {
    return {
      docStats: { total: 0, accessible: 0 },
      activityAnalytics: {},
      recentActivities: [],
      documents: []
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        const [docRes, analyticsRes, activityRes, docsRes] = await Promise.all([
          documentApi.stats(),
          activityApi.analytics(),
          activityApi.getRecent(this.$store.getters.userId, 7).catch(() => ({ data: [] })),
          documentApi.getAccessible()
        ])
        this.docStats = docRes.data
        this.activityAnalytics = analyticsRes.data
        this.recentActivities = activityRes.data || []
        this.documents = docsRes.data || []
      } catch (error) {
        console.error('加载数据失败:', error)
      }
    },
    goToDocuments() {
      this.$router.push('/user/documents')
    },
    goToSettings() {
      this.$router.push('/user/settings')
    },
    startQuickRead() {
      if (this.documents.length > 0) {
        this.$router.push(`/user/reader/${this.documents[0].id}`)
      } else {
        this.$message.info('暂无可用文档')
        this.$router.push('/user/documents')
      }
    },
    goToDocument(docId) {
      if (docId) {
        this.$router.push(`/user/reader/${docId}`)
      }
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return date.toLocaleDateString('zh-CN')
    }
  }
}
</script>
