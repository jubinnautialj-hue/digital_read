<template>
  <div role="main" aria-label="无障碍审计">
    <h2 tabindex="0" style="margin-bottom: 20px;">无障碍合规审计</h2>
    
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>审计结果</span>
          </template>
          <div style="display: flex; flex-direction: column; gap: 15px;">
            <div v-for="(value, key) in auditData.complianceFeatures" :key="key">
              <div style="display: flex; justify-content: space-between; align-items: center;">
                <span>{{ getComplianceLabel(key) }}</span>
                <el-tag :type="value === 'enabled' || value === 'available' ? 'success' : 'warning'" size="small">
                  {{ getStatusLabel(value) }}
                </el-tag>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>活动无障碍设置</span>
          </template>
          <div v-if="activeSettings.length > 0">
            <div
              v-for="setting in activeSettings"
              :key="setting.id"
              style="padding: 10px; border-bottom: 1px solid #eee;"
            >
              <div style="font-weight: bold;">{{ setting.description }}</div>
              <div style="color: #909399; font-size: 13px;">
                {{ setting.settingKey }} = {{ setting.settingValue }}
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无活动设置" />
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px;">
      <template #header>
        <span>改进建议</span>
      </template>
      <el-timeline>
        <el-timeline-item
          v-for="(rec, index) in auditData.recommendations"
          :key="index"
          placement="top"
        >
          <el-card>
            <p>{{ rec }}</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-card>

    <el-card style="margin-top: 20px;">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>全局无障碍设置管理</span>
          <el-button type="primary" size="small" @click="openCreateDialog">
            <i class="el-icon-plus"></i> 添加设置
          </el-button>
        </div>
      </template>
      <el-table :data="allSettings" style="width: 100%;" border aria-label="无障碍设置列表">
        <el-table-column prop="settingKey" label="设置键" width="200" />
        <el-table-column prop="settingValue" label="设置值" min-width="150" />
        <el-table-column prop="description" label="描述" min-width="200" />
        <el-table-column prop="isActive" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.isActive ? 'success' : 'info'" size="small">
              {{ scope.row.isActive ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" @click="openEditDialog(scope.row)" aria-label="编辑">编辑</el-button>
            <el-button size="mini" type="danger" @click="deleteSetting(scope.row)" aria-label="删除">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form ref="settingForm" :model="settingForm" :rules="settingRules" label-width="100px">
        <el-form-item label="设置键" prop="settingKey">
          <el-input v-model="settingForm.settingKey" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="设置值" prop="settingValue">
          <el-input v-model="settingForm.settingValue" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="settingForm.description" />
        </el-form-item>
        <el-form-item label="启用">
          <el-switch
            v-model="settingForm.isActive"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveSetting">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { accessibilityApi } from '@/api'

export default {
  name: 'AccessibilityAudit',
  data() {
    return {
      auditData: {
        activeSettings: 0,
        settings: {},
        complianceFeatures: {},
        recommendations: []
      },
      activeSettings: [],
      allSettings: [],
      dialogVisible: false,
      isEdit: false,
      dialogTitle: '添加设置',
      settingForm: {
        id: null,
        settingKey: '',
        settingValue: '',
        description: '',
        isActive: true
      },
      settingRules: {
        settingKey: [{ required: true, message: '请输入设置键', trigger: 'blur' }],
        settingValue: [{ required: true, message: '请输入设置值', trigger: 'blur' }]
      }
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        const [auditRes, activeRes, allRes] = await Promise.all([
          accessibilityApi.audit(),
          accessibilityApi.getActiveSettings(),
          accessibilityApi.getAllSettings()
        ])
        
        this.auditData = auditRes.data || {}
        this.activeSettings = activeRes.data || []
        this.allSettings = allRes.data || []
      } catch (error) {
        console.error('加载数据失败:', error)
      }
    },
    getComplianceLabel(key) {
      const labels = {
        screenReaderSupport: '屏幕阅读器支持',
        keyboardNavigation: '键盘导航',
        textToSpeech: '文本转语音',
        highContrastMode: '高对比度模式',
        fontAdjustment: '字体调整'
      }
      return labels[key] || key
    },
    getStatusLabel(value) {
      const labels = {
        enabled: '已启用',
        available: '可用',
        enforced: '已强制'
      }
      return labels[value] || value
    },
    openCreateDialog() {
      this.isEdit = false
      this.dialogTitle = '添加设置'
      this.settingForm = {
        id: null,
        settingKey: '',
        settingValue: '',
        description: '',
        isActive: true
      }
      this.dialogVisible = true
    },
    openEditDialog(setting) {
      this.isEdit = true
      this.dialogTitle = '编辑设置'
      this.settingForm = { ...setting }
      this.dialogVisible = true
    },
    async saveSetting() {
      try {
        if (this.isEdit) {
          await accessibilityApi.updateSetting(this.settingForm.id, this.settingForm)
          this.$message.success('更新成功')
        } else {
          await accessibilityApi.createSetting(this.settingForm)
          this.$message.success('创建成功')
        }
        this.dialogVisible = false
        this.loadData()
      } catch (error) {
        console.error('保存失败:', error)
      }
    },
    deleteSetting(setting) {
      this.$confirm(`确定要删除设置 "${setting.settingKey}" 吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await accessibilityApi.deleteSetting(setting.id)
          this.$message.success('删除成功')
          this.loadData()
        } catch (error) {
          console.error('删除失败:', error)
        }
      }).catch(() => {})
    }
  }
}
</script>
