<template>
  <div role="main" aria-label="数据分析">
    <h2 tabindex="0" style="margin-bottom: 20px;">数据分析</h2>
    
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stats-card" tabindex="0">
          <div class="stats-label">今日阅读活动</div>
          <div class="stats-value">{{ analytics.todayActivities || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card" tabindex="0">
          <div class="stats-label">本周阅读活动</div>
          <div class="stats-value">{{ analytics.weekActivities || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card" tabindex="0">
          <div class="stats-label">本月阅读活动</div>
          <div class="stats-value">{{ analytics.monthActivities || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card" tabindex="0">
          <div class="stats-label">今日活跃用户</div>
          <div class="stats-value">{{ analytics.todayActiveUsers || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>用户活动趋势</span>
          </template>
          <div style="display: flex; flex-direction: column; gap: 15px;">
            <div>
              <div style="display: flex; justify-content: space-between; margin-bottom: 5px;">
                <span>今日</span>
                <span style="font-weight: bold;">{{ analytics.todayActivities || 0 }} 次阅读</span>
              </div>
              <el-progress :percentage="Math.min(100, (analytics.todayActivities || 0) * 10)" />
            </div>
            <div>
              <div style="display: flex; justify-content: space-between; margin-bottom: 5px;">
                <span>本周</span>
                <span style="font-weight: bold;">{{ analytics.weekActivities || 0 }} 次阅读</span>
              </div>
              <el-progress :percentage="Math.min(100, (analytics.weekActivities || 0) * 2)" />
            </div>
            <div>
              <div style="display: flex; justify-content: space-between; margin-bottom: 5px;">
                <span>本月</span>
                <span style="font-weight: bold;">{{ analytics.monthActivities || 0 }} 次阅读</span>
              </div>
              <el-progress :percentage="Math.min(100, (analytics.monthActivities || 0))" />
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>活跃用户统计</span>
          </template>
          <div style="display: flex; flex-direction: column; gap: 15px;">
            <div>
              <div style="display: flex; justify-content: space-between; margin-bottom: 5px;">
                <span>今日活跃用户</span>
                <span style="font-weight: bold;">{{ analytics.todayActiveUsers || 0 }} 人</span>
              </div>
              <el-progress :percentage="Math.min(100, (analytics.todayActiveUsers || 0) * 20)" />
            </div>
            <div>
              <div style="display: flex; justify-content: space-between; margin-bottom: 5px;">
                <span>本周活跃用户</span>
                <span style="font-weight: bold;">{{ analytics.weekActiveUsers || 0 }} 人</span>
              </div>
              <el-progress :percentage="Math.min(100, (analytics.weekActiveUsers || 0) * 5)" />
            </div>
            <div>
              <div style="display: flex; justify-content: space-between; margin-bottom: 5px;">
                <span>本月活跃用户</span>
                <span style="font-weight: bold;">{{ analytics.monthActiveUsers || 0 }} 人</span>
              </div>
              <el-progress :percentage="Math.min(100, (analytics.monthActiveUsers || 0))" />
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px;">
      <template #header>
        <span>文档统计</span>
      </template>
      <el-row :gutter="20">
        <el-col :span="8">
          <div style="text-align: center; padding: 20px;">
            <div style="font-size: 32px; font-weight: bold; color: #409EFF;">{{ docStats.total || 0 }}</div>
            <div style="color: #909399;">总文档数</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div style="text-align: center; padding: 20px;">
            <div style="font-size: 32px; font-weight: bold; color: #67C23A;">{{ docStats.accessible || 0 }}</div>
            <div style="color: #909399;">无障碍文档</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div style="text-align: center; padding: 20px;">
            <div style="font-size: 32px; font-weight: bold; color: #E6A23C;">
              {{ docStats.total > 0 ? Math.round((docStats.accessible || 0) * 100 / docStats.total) : 0 }}%</div>
            <div style="color: #909399;">无障碍率</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-card style="margin-top: 20px;">
      <template #header>
        <span>最近阅读活动</span>
      </template>
      <el-table :data="recentActivities" style="width: 100%;" border aria-label="最近活动">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="用户" min-width="150">
          <template slot-scope="scope">
            {{ scope.row.user?.username || '未知' }}
          </template>
        </el-table-column>
        <el-table-column label="文档" min-width="200">
          <template slot-scope="scope">
            {{ scope.row.document?.title || '未知' }}
          </template>
        </el-table-column>
        <el-table-column prop="progressPercent" label="进度" width="100">
          <template slot-scope="scope">
            {{ scope.row.progressPercent }}%
          </template>
        </el-table-column>
        <el-table-column label="开始时间" width="180">
          <template slot-scope="scope">
            {{ formatDate(scope.row.startTime) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { activityApi, documentApi } from '@/api'

export default {
  name: 'Analytics',
  data() {
    return {
      analytics: {},
      docStats: {},
      recentActivities: []
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        const [analyticsRes, docStatsRes, activitiesRes] = await Promise.all([
          activityApi.analytics(),
          documentApi.stats(),
          activityApi.getAll()
        ])
        
        this.analytics = analyticsRes.data || {}
        this.docStats = docStatsRes.data || {}
        this.recentActivities = (activitiesRes.data || []).slice(0, 10)
      } catch (error) {
        console.error('加载数据失败:', error)
      }
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      return new Date(dateStr).toLocaleString('zh-CN')
    }
  }
}
</script>
