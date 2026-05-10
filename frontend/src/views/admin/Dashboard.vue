<template>
  <div role="main" aria-label="管理控制台">
    <h2 tabindex="0" style="margin-bottom: 20px;">管理控制台</h2>
    
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stats-card" tabindex="0">
          <div class="stats-label">总用户数</div>
          <div class="stats-value">{{ stats.totalUsers }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card" tabindex="0">
          <div class="stats-label">总文档数</div>
          <div class="stats-value">{{ stats.totalDocuments }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card" tabindex="0">
          <div class="stats-label">无障碍文档</div>
          <div class="stats-value">{{ stats.accessibleDocuments }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card" tabindex="0">
          <div class="stats-label">今日活跃用户</div>
          <div class="stats-value">{{ stats.todayActiveUsers }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>快捷操作</span>
          </template>
          <div style="display: flex; flex-wrap: wrap; gap: 10px;">
            <el-button type="primary" @click="$router.push('/admin/users')" aria-label="用户管理">
              <i class="el-icon-user"></i> 用户管理
            </el-button>
            <el-button type="success" @click="$router.push('/admin/documents')" aria-label="文档管理">
              <i class="el-icon-document"></i> 文档管理
            </el-button>
            <el-button type="warning" @click="$router.push('/admin/analytics')" aria-label="数据分析">
              <i class="el-icon-s-data"></i> 数据分析
            </el-button>
            <el-button type="info" @click="$router.push('/admin/accessibility')" aria-label="无障碍审计">
              <i class="el-icon-check"></i> 无障碍审计
            </el-button>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>使用趋势</span>
          </template>
          <div style="display: flex; flex-direction: column; gap: 15px;">
            <div>
              <div style="display: flex; justify-content: space-between; margin-bottom: 5px;">
                <span>今日阅读活动</span>
                <span style="font-weight: bold;">{{ activityAnalytics.todayActivities || 0 }} 次</span>
              </div>
              <el-progress :percentage="Math.min(100, (activityAnalytics.todayActivities || 0) * 10)" />
            </div>
            <div>
              <div style="display: flex; justify-content: space-between; margin-bottom: 5px;">
                <span>本周阅读活动</span>
                <span style="font-weight: bold;">{{ activityAnalytics.weekActivities || 0 }} 次</span>
              </div>
              <el-progress :percentage="Math.min(100, (activityAnalytics.weekActivities || 0) * 2)" />
            </div>
            <div>
              <div style="display: flex; justify-content: space-between; margin-bottom: 5px;">
                <span>本月阅读活动</span>
                <span style="font-weight: bold;">{{ activityAnalytics.monthActivities || 0 }} 次</span>
              </div>
              <el-progress :percentage="Math.min(100, (activityAnalytics.monthActivities || 0))" />
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px;">
      <template #header>
        <span>活跃用户</span>
      </template>
      <div style="display: flex; gap: 30px; font-size: 16px;">
        <div>
          <strong>今日活跃用户：</strong>{{ activityAnalytics.todayActiveUsers || 0 }} 人
        </div>
        <div>
          <strong>本周活跃用户：</strong>{{ activityAnalytics.weekActiveUsers || 0 }} 人
        </div>
        <div>
          <strong>本月活跃用户：</strong>{{ activityAnalytics.monthActiveUsers || 0 }} 人
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { userApi, documentApi, activityApi } from '@/api'

export default {
  name: 'AdminDashboard',
  data() {
    return {
      stats: {
        totalUsers: 0,
        totalDocuments: 0,
        accessibleDocuments: 0,
        todayActiveUsers: 0
      },
      activityAnalytics: {}
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        const [usersRes, docStatsRes, analyticsRes] = await Promise.all([
          userApi.getAll(),
          documentApi.stats(),
          activityApi.analytics()
        ])
        
        this.stats.totalUsers = usersRes.data?.length || 0
        this.stats.totalDocuments = docStatsRes.data?.total || 0
        this.stats.accessibleDocuments = docStatsRes.data?.accessible || 0
        this.activityAnalytics = analyticsRes.data || {}
        this.stats.todayActiveUsers = analyticsRes.data?.todayActiveUsers || 0
      } catch (error) {
        console.error('加载数据失败:', error)
      }
    }
  }
}
</script>
