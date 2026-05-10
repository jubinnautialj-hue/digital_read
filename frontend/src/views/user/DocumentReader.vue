<template>
  <div class="document-reader" role="main" aria-label="文档阅读器">
    <h2 tabindex="0" style="margin-bottom: 20px;">{{ document?.title }}</h2>
    
    <div class="reader-toolbar" role="toolbar" aria-label="阅读工具栏">
      <el-button-group>
        <el-button @click="toggleSpeech" :aria-label="isSpeaking ? '暂停朗读' : '开始朗读'">
          <i :class="isSpeaking ? 'el-icon-video-pause' : 'el-icon-video-play'"></i>
          {{ isSpeaking ? '暂停' : '朗读' }}
        </el-button>
        <el-button @click="stopSpeech" aria-label="停止朗读">
          <i class="el-icon-circle-close"></i> 停止
        </el-button>
      </el-button-group>
      
      <el-button-group>
        <el-button @click="prevParagraph" aria-label="上一段">
          <i class="el-icon-arrow-left"></i> 上一段
        </el-button>
        <el-button @click="nextParagraph" aria-label="下一段">
          下一段 <i class="el-icon-arrow-right"></i>
        </el-button>
      </el-button-group>
      
      <el-button-group>
        <el-button @click="increaseSpeed" aria-label="加快语速">
          <i class="el-icon-top"></i> 快
        </el-button>
        <el-button @click="decreaseSpeed" aria-label="减慢语速">
          <i class="el-icon-bottom"></i> 慢
        </el-button>
      </el-button-group>
      
      <el-button-group>
        <el-button @click="increaseFontSize" aria-label="增大字体">
          A+
        </el-button>
        <el-button @click="decreaseFontSize" aria-label="减小字体">
          A-
        </el-button>
      </el-button-group>
      
      <el-button type="text" @click="goBack" aria-label="返回文档列表">
        <i class="el-icon-back"></i> 返回
      </el-button>
    </div>

    <div class="keyboard-hint" style="margin-bottom: 20px;">
      <strong>阅读快捷键：</strong>
      <kbd>Space</kbd> 播放/暂停 |
      <kbd>←</kbd> 上一段 |
      <kbd>→</kbd> 下一段 |
      <kbd>↑</kbd> 加快语速 |
      <kbd>↓</kbd> 减慢语速 |
      <kbd>Esc</kbd> 返回
    </div>

    <div
      class="reader-content"
      ref="readerContent"
      role="article"
      :style="{ fontSize: fontSize + 'px', lineHeight: lineHeight + '%' }"
      tabindex="0"
    >
      <template v-if="paragraphs.length > 0">
        <p
          v-for="(p, idx) in paragraphs"
          :key="idx"
          :class="{ 'current-paragraph': idx === currentParagraphIndex }"
          :style="idx === currentParagraphIndex ? { backgroundColor: '#e6f7ff', padding: '5px 10px', borderRadius: '4px' } : {}"
          tabindex="0"
        >
          {{ p }}
        </p>
      </template>
      <template v-else>
        {{ document?.content || '暂无内容' }}
      </template>
    </div>
  </div>
</template>

<script>
import { documentApi, activityApi } from '@/api'

export default {
  name: 'DocumentReader',
  data() {
    return {
      document: null,
      paragraphs: [],
      currentParagraphIndex: 0,
      isSpeaking: false,
      speechRate: 1,
      fontSize: 18,
      lineHeight: 180,
      activityId: null,
      loading: false
    }
  },
  computed: {
    userSettings() {
      return this.$store.getters.userSettings
    }
  },
  mounted() {
    this.loadDocument()
    this.setupKeyboardHandlers()
  },
  beforeDestroy() {
    this.stopSpeech()
    this.endActivity()
    this.removeKeyboardHandlers()
  },
  methods: {
    async loadDocument() {
      this.loading = true
      try {
        const docId = this.$route.params.id
        const res = await documentApi.getById(docId)
        this.document = res.data
        this.parseContent(res.data?.content)
        
        if (this.userSettings) {
          this.speechRate = (this.userSettings.voiceSpeed || 100) / 100
        }
        
        await this.startActivity(docId)
      } catch (error) {
        console.error('加载文档失败:', error)
        this.$message.error('加载文档失败')
      } finally {
        this.loading = false
      }
    },
    parseContent(content) {
      if (!content) return
      this.paragraphs = content.split(/\n+/).filter(p => p.trim())
    },
    async startActivity(docId) {
      try {
        const userId = this.$store.getters.userId
        const res = await activityApi.start({ userId, documentId: parseInt(docId) })
        this.activityId = res.data?.id
        this.$store.dispatch('setCurrentActivity', res.data)
      } catch (error) {
        console.error('开始活动失败:', error)
      }
    },
    async endActivity() {
      if (!this.activityId) return
      try {
        const progress = Math.round((this.currentParagraphIndex / Math.max(this.paragraphs.length, 1)) * 100)
        await activityApi.end(this.activityId, { progress })
      } catch (error) {
        console.error('结束活动失败:', error)
      }
    },
    toggleSpeech() {
      if (this.isSpeaking) {
        this.pauseSpeech()
      } else {
        this.startSpeech()
      }
    },
    startSpeech() {
      if (!window.speechSynthesis) {
        this.$message.warning('您的浏览器不支持语音合成')
        return
      }
      
      window.speechSynthesis.cancel()
      
      const text = this.paragraphs[this.currentParagraphIndex] || this.document?.content
      if (!text) return
      
      const utterance = new SpeechSynthesisUtterance(text)
      utterance.lang = 'zh-CN'
      utterance.rate = this.speechRate
      utterance.pitch = 1
      utterance.volume = 1
      
      utterance.onstart = () => {
        this.isSpeaking = true
      }
      
      utterance.onend = () => {
        this.isSpeaking = false
        if (this.currentParagraphIndex < this.paragraphs.length - 1) {
          this.currentParagraphIndex++
          this.startSpeech()
        }
      }
      
      utterance.onerror = () => {
        this.isSpeaking = false
      }
      
      window.speechSynthesis.speak(utterance)
    },
    pauseSpeech() {
      if (window.speechSynthesis) {
        window.speechSynthesis.pause()
        this.isSpeaking = false
      }
    },
    stopSpeech() {
      if (window.speechSynthesis) {
        window.speechSynthesis.cancel()
        this.isSpeaking = false
      }
    },
    nextParagraph() {
      if (this.currentParagraphIndex < this.paragraphs.length - 1) {
        this.currentParagraphIndex++
        if (this.isSpeaking) {
          this.startSpeech()
        }
        this.scrollToCurrent()
      }
    },
    prevParagraph() {
      if (this.currentParagraphIndex > 0) {
        this.currentParagraphIndex--
        if (this.isSpeaking) {
          this.startSpeech()
        }
        this.scrollToCurrent()
      }
    },
    increaseSpeed() {
      if (this.speechRate < 2) {
        this.speechRate += 0.2
        if (this.isSpeaking) {
          this.startSpeech()
        }
      }
    },
    decreaseSpeed() {
      if (this.speechRate > 0.5) {
        this.speechRate -= 0.2
        if (this.isSpeaking) {
          this.startSpeech()
        }
      }
    },
    increaseFontSize() {
      if (this.fontSize < 32) {
        this.fontSize += 2
      }
    },
    decreaseFontSize() {
      if (this.fontSize > 12) {
        this.fontSize -= 2
      }
    },
    scrollToCurrent() {
      this.$nextTick(() => {
        const paragraphs = this.$refs.readerContent?.querySelectorAll('p')
        if (paragraphs && paragraphs[this.currentParagraphIndex]) {
          paragraphs[this.currentParagraphIndex].scrollIntoView({ behavior: 'smooth', block: 'center' })
        }
      })
    },
    goBack() {
      this.$router.push('/user/documents')
    },
    setupKeyboardHandlers() {
      this.keyboardHandler = (e) => {
        switch (e.key) {
          case ' ':
            e.preventDefault()
            this.toggleSpeech()
            break
          case 'ArrowRight':
            e.preventDefault()
            this.nextParagraph()
            break
          case 'ArrowLeft':
            e.preventDefault()
            this.prevParagraph()
            break
          case 'ArrowUp':
            e.preventDefault()
            this.increaseSpeed()
            break
          case 'ArrowDown':
            e.preventDefault()
            this.decreaseSpeed()
            break
          case 'Escape':
            this.goBack()
            break
        }
      }
      document.addEventListener('keydown', this.keyboardHandler)
    },
    removeKeyboardHandlers() {
      if (this.keyboardHandler) {
        document.removeEventListener('keydown', this.keyboardHandler)
      }
    }
  }
}
</script>

<style scoped>
.current-paragraph {
  background-color: #e6f7ff;
  padding: 5px 10px;
  border-radius: 4px;
  transition: background-color 0.3s;
}
</style>
