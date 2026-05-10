<template>
  <div role="main" aria-label="个人设置">
    <h2 tabindex="0" style="margin-bottom: 20px;">个人设置</h2>
    
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>语音合成设置</span>
          </template>
          <el-form label-width="120px">
            <el-form-item label="语音类型">
              <el-select v-model="settings.voiceType" aria-label="语音类型">
                <el-option label="男声" value="male" />
                <el-option label="女声" value="female" />
              </el-select>
            </el-form-item>
            <el-form-item label="语速">
              <el-slider
                v-model="settings.voiceSpeed"
                :min="50"
                :max="200"
                :step="10"
                aria-label="语速"
                :format-tooltip="val => val + '%'"
              />
            </el-form-item>
            <el-form-item label="音调">
              <el-slider
                v-model="settings.voicePitch"
                :min="50"
                :max="150"
                :step="10"
                aria-label="音调"
                :format-tooltip="val => val + '%'"
              />
            </el-form-item>
            <el-form-item label="自动朗读">
              <el-switch
                v-model="settings.autoRead"
                aria-label="自动朗读"
                active-text="开启"
                inactive-text="关闭"
              />
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>显示设置</span>
          </template>
          <el-form label-width="120px">
            <el-form-item label="文字大小">
              <el-select v-model="settings.textSize" aria-label="文字大小">
                <el-option label="小" value="small" />
                <el-option label="中" value="medium" />
                <el-option label="大" value="large" />
                <el-option label="特大" value="extra-large" />
              </el-select>
            </el-form-item>
            <el-form-item label="字体">
              <el-select v-model="settings.font" aria-label="字体">
                <el-option label="系统默认" value="system" />
                <el-option label="黑体" value="SimHei" />
                <el-option label="宋体" value="SimSun" />
                <el-option label="微软雅黑" value="Microsoft YaHei" />
              </el-select>
            </el-form-item>
            <el-form-item label="行高">
              <el-slider
                v-model="settings.lineHeight"
                :min="100"
                :max="250"
                :step="10"
                aria-label="行高"
                :format-tooltip="val => val + '%'"
              />
            </el-form-item>
            <el-form-item label="高对比度">
              <el-switch
                v-model="settings.highContrast"
                aria-label="高对比度"
                active-text="开启"
                inactive-text="关闭"
              />
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px;">
      <template #header>
        <span>导航设置</span>
      </template>
      <el-form label-width="120px">
        <el-form-item label="导航模式">
          <el-select v-model="settings.navigationMode" aria-label="导航模式">
            <el-option label="键盘导航" value="keyboard" />
            <el-option label="鼠标导航" value="mouse" />
            <el-option label="混合导航" value="hybrid" />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>

    <div style="margin-top: 20px; display: flex; gap: 10px;">
      <el-button type="primary" @click="saveSettings" aria-label="保存设置">
        <i class="el-icon-check"></i> 保存设置
      </el-button>
      <el-button @click="resetSettings" aria-label="重置设置">
        <i class="el-icon-refresh"></i> 恢复默认
      </el-button>
    </div>

    <el-card style="margin-top: 20px;">
      <template #header>
        <span>键盘快捷键</span>
      </template>
      <div class="keyboard-hint">
        <div style="margin-bottom: 10px;"><strong>全局快捷键：</strong></div>
        <div>
          <kbd>Alt + 1</kbd> 用户中心 |
          <kbd>Alt + 2</kbd> 文档库 |
          <kbd>Alt + 3</kbd> 设置 |
          <kbd>Alt + Q</kbd> 退出登录
        </div>
        <div style="margin: 10px 0;"><strong>阅读器快捷键：</strong></div>
        <div>
          <kbd>Space</kbd> 播放/暂停 |
          <kbd>←</kbd> 上一段 |
          <kbd>→</kbd> 下一段 |
          <kbd>↑</kbd> 加快语速 |
          <kbd>↓</kbd> 减慢语速 |
          <kbd>Esc</kbd> 返回
        </div>
        <div style="margin: 10px 0;"><strong>基础快捷键：</strong></div>
        <div>
          <kbd>Tab</kbd> 切换焦点 |
          <kbd>Enter</kbd> 确认 |
          <kbd>Esc</kbd> 取消/关闭 |
          <kbd>Alt + S</kbd> 搜索
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { settingsApi } from '@/api'

export default {
  name: 'UserSettings',
  data() {
    return {
      settings: {
        voiceType: 'male',
        voiceSpeed: 100,
        voicePitch: 100,
        textSize: 'medium',
        contrastMode: 'normal',
        font: 'system',
        lineHeight: 150,
        navigationMode: 'keyboard',
        autoRead: false,
        highContrast: false
      },
      loading: false
    }
  },
  mounted() {
    this.loadSettings()
  },
  methods: {
    async loadSettings() {
      try {
        const userId = this.$store.getters.userId
        const res = await settingsApi.getByUserId(userId)
        if (res.data) {
          this.settings = { ...this.settings, ...res.data }
        }
      } catch (error) {
        console.error('加载设置失败:', error)
      }
    },
    async saveSettings() {
      this.loading = true
      try {
        const userId = this.$store.getters.userId
        await this.$store.dispatch('updateUserSettings', this.settings)
        this.$message.success('设置已保存')
      } catch (error) {
        console.error('保存设置失败:', error)
        this.$message.error('保存失败')
      } finally {
        this.loading = false
      }
    },
    async resetSettings() {
      this.$confirm('确定要恢复默认设置吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const userId = this.$store.getters.userId
          await settingsApi.reset(userId)
          await this.loadSettings()
          await this.$store.dispatch('loadUserSettings')
          this.$message.success('已恢复默认设置')
        } catch (error) {
          console.error('重置设置失败:', error)
        }
      }).catch(() => {})
    }
  }
}
</script>
