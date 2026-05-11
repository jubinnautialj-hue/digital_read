<template>
  <div class="document-reader" role="main" aria-label="文档阅读器">
    <article class="reader-header" role="banner">
      <h1 tabindex="0" class="document-title" :aria-label="'文档标题: ' + (document?.title || '')">
        {{ document?.title }}
      </h1>
      <div class="document-meta" v-if="document" role="contentinfo">
        <span v-if="document.author" class="meta-item">
          <strong>作者：</strong>{{ document.author }}
        </span>
        <span v-if="document.category" class="meta-item">
          <strong>分类：</strong>{{ document.category }}
        </span>
        <span v-if="document.fileType" class="meta-item">
          <strong>格式：</strong>{{ document.fileType.toUpperCase() }}
        </span>
      </div>
    </article>

    <nav class="reader-toolbar" role="toolbar" aria-label="阅读工具栏">
      <div class="toolbar-group" role="group" aria-label="朗读控制">
        <el-button 
          @click="toggleSpeech" 
          :aria-label="isSpeaking ? '暂停朗读' : '开始朗读'"
          :type="isSpeaking ? 'primary' : 'default'"
        >
          <i :class="isSpeaking ? 'el-icon-video-pause' : 'el-icon-video-play'"></i>
          {{ isSpeaking ? '暂停' : '朗读' }}
        </el-button>
        <el-button @click="stopSpeech" aria-label="停止朗读">
          <i class="el-icon-circle-close"></i> 停止
        </el-button>
      </div>

      <div class="toolbar-group" role="group" aria-label="段落导航">
        <el-button @click="prevParagraph" aria-label="上一段">
          <i class="el-icon-arrow-left"></i> 上一段
        </el-button>
        <el-button @click="nextParagraph" aria-label="下一段">
          下一段 <i class="el-icon-arrow-right"></i>
        </el-button>
      </div>

      <div class="toolbar-group" role="group" aria-label="标题导航">
        <el-button @click="prevHeading" aria-label="上一个标题">
          <i class="el-icon-d-arrow-left"></i> 上一标题
        </el-button>
        <el-button @click="nextHeading" aria-label="下一个标题">
          下一标题 <i class="el-icon-d-arrow-right"></i>
        </el-button>
      </div>

      <div class="toolbar-group" role="group" aria-label="语音设置">
        <el-select 
          v-model="selectedVoice" 
          placeholder="选择语音" 
          aria-label="选择语音类型"
          @change="onVoiceChange"
          style="width: 100px;"
        >
          <el-option 
            v-for="voice in availableVoices" 
            :key="voice.id" 
            :label="voice.name" 
            :value="voice.id"
          />
        </el-select>
        <el-button @click="increaseSpeed" aria-label="加快语速">
          <i class="el-icon-top"></i> 快
        </el-button>
        <el-button @click="decreaseSpeed" aria-label="减慢语速">
          <i class="el-icon-bottom"></i> 慢
        </el-button>
      </div>

      <div class="toolbar-group" role="group" aria-label="显示设置">
        <el-button @click="increaseFontSize" aria-label="增大字体">A+</el-button>
        <el-button @click="decreaseFontSize" aria-label="减小字体">A-</el-button>
        <el-button @click="toggleOutline" aria-label="显示/隐藏文档大纲">
          <i class="el-icon-menu"></i> 大纲
        </el-button>
      </div>

      <div class="toolbar-group">
        <el-button type="text" @click="goBack" aria-label="返回文档列表">
          <i class="el-icon-back"></i> 返回
        </el-button>
      </div>
    </nav>

    <div class="keyboard-hint" role="status" aria-live="polite">
      <strong>快捷键：</strong>
      <kbd>Space</kbd> 播放/暂停 |
      <kbd>←</kbd> 上一段 |
      <kbd>→</kbd> 下一段 |
      <kbd>Alt+←</kbd> 上一标题 |
      <kbd>Alt+→</kbd> 下一标题 |
      <kbd>?</kbd> 帮助 |
      <kbd>G</kbd> 导航菜单 |
      <kbd>Esc</kbd> 返回
    </div>

    <div class="reader-container">
      <aside 
        v-if="showOutline" 
        class="document-outline" 
        role="complementary" 
        aria-label="文档大纲"
      >
        <h3 tabindex="0" class="outline-title">文档大纲</h3>
        <nav role="navigation" aria-label="大纲导航">
          <ul class="outline-list">
            <li 
              v-for="(heading, index) in headings" 
              :key="index"
              class="outline-item"
              :class="{ active: currentHeadingIndex === index }"
            >
              <a 
                href="#" 
                @click.prevent="jumpToHeading(index)"
                tabindex="0"
                :aria-label="'跳转到标题: ' + heading"
                @keydown.enter.prevent="jumpToHeading(index)"
              >
                {{ heading }}
              </a>
            </li>
          </ul>
        </nav>
      </aside>

      <main 
        class="reader-content"
        ref="readerContent"
        role="article"
        :style="{ fontSize: fontSize + 'px', lineHeight: lineHeight + '%' }"
        tabindex="0"
        aria-label="文档内容"
      >
        <template v-if="structuredData">
          <div 
            v-for="(node, index) in flatNodes" 
            :key="node.id || index"
            class="structured-node"
          >
            <h1 
              v-if="node.tagName === 'h1'"
              :id="'node-' + index"
              :data-index="index"
              :data-type="node.type"
              tabindex="0"
              role="heading"
              :aria-level="1"
              :class="{ 'current-element': currentParagraphIndex === index }"
              @click="jumpToParagraph(index)"
            >{{ node.content }}</h1>
            
            <h2 
              v-else-if="node.tagName === 'h2'"
              :id="'node-' + index"
              :data-index="index"
              :data-type="node.type"
              tabindex="0"
              role="heading"
              :aria-level="2"
              :class="{ 'current-element': currentParagraphIndex === index }"
              @click="jumpToParagraph(index)"
            >{{ node.content }}</h2>
            
            <h3 
              v-else-if="node.tagName === 'h3'"
              :id="'node-' + index"
              :data-index="index"
              :data-type="node.type"
              tabindex="0"
              role="heading"
              :aria-level="3"
              :class="{ 'current-element': currentParagraphIndex === index }"
              @click="jumpToParagraph(index)"
            >{{ node.content }}</h3>
            
            <h4 
              v-else-if="node.tagName === 'h4'"
              :id="'node-' + index"
              :data-index="index"
              :data-type="node.type"
              tabindex="0"
              role="heading"
              :aria-level="4"
              :class="{ 'current-element': currentParagraphIndex === index }"
              @click="jumpToParagraph(index)"
            >{{ node.content }}</h4>
            
            <h5 
              v-else-if="node.tagName === 'h5'"
              :id="'node-' + index"
              :data-index="index"
              :data-type="node.type"
              tabindex="0"
              role="heading"
              :aria-level="5"
              :class="{ 'current-element': currentParagraphIndex === index }"
              @click="jumpToParagraph(index)"
            >{{ node.content }}</h5>
            
            <h6 
              v-else-if="node.tagName === 'h6'"
              :id="'node-' + index"
              :data-index="index"
              :data-type="node.type"
              tabindex="0"
              role="heading"
              :aria-level="6"
              :class="{ 'current-element': currentParagraphIndex === index }"
              @click="jumpToParagraph(index)"
            >{{ node.content }}</h6>
            
            <p 
              v-else-if="node.tagName === 'p'"
              :id="'node-' + index"
              :data-index="index"
              :data-type="node.type"
              tabindex="0"
              role="paragraph"
              :class="{ 'current-element': currentParagraphIndex === index }"
              @click="jumpToParagraph(index)"
            >{{ node.content }}</p>
            
            <ul v-else-if="node.tagName === 'ul'" role="list">
              <li 
                v-for="(child, childIdx) in node.children" 
                :key="childIdx"
                role="listitem"
                tabindex="0"
                :class="{ 'current-element': currentParagraphIndex === getFlattenedIndex(node, child) }"
              >
                {{ child.content }}
              </li>
            </ul>
            
            <section v-else-if="node.tagName === 'section'" role="region" :aria-label="node.ariaLabel || '章节'">
              <p 
                v-for="(child, childIdx) in node.children" 
                :key="childIdx"
                v-if="child.tagName === 'p'" 
                tabindex="0"
              >{{ child.content }}</p>
            </section>
          </div>
        </template>

        <template v-else>
          <p 
            v-for="(p, idx) in paragraphs" 
            :key="idx"
            :id="'para-' + idx"
            :data-index="idx"
            tabindex="0"
            role="paragraph"
            :class="{ 'current-element': idx === currentParagraphIndex }"
            @click="jumpToParagraph(idx)"
          >
            {{ p }}
          </p>
        </template>

        <div v-if="!document?.content && !loading" role="status" aria-live="polite">
          暂无内容
        </div>
      </main>
    </div>

    <div 
      v-if="loading" 
      class="loading-overlay" 
      role="status" 
      aria-live="polite"
      aria-busy="true"
    >
      <el-loading :visible="true" text="加载中..." />
    </div>

    <nav 
      v-if="showCommandPalette" 
      class="command-palette-overlay" 
      role="dialog"
      aria-label="命令面板"
      @click.self="hideCommandPalette"
    >
      <div class="command-palette" role="document">
        <el-input 
          v-model="commandFilter" 
          placeholder="输入命令搜索..."
          ref="commandInput"
          aria-label="搜索命令"
          @keydown.esc="hideCommandPalette"
        />
        <ul class="command-list" role="listbox">
          <li 
            v-for="(cmd, key) in filteredCommands" 
            :key="key"
            role="option"
            tabindex="0"
            @click="executeCommand(key)"
          >
            <span class="command-key">{{ key }}</span>
            <span class="command-desc">{{ cmd.description }}</span>
          </li>
        </ul>
      </div>
    </nav>
  </div>
</template>

<script>
import { documentApi, activityApi, ttsApi } from '@/api'
import keyboardNav from '@/utils/keyboardNavigation'

export default {
  name: 'DocumentReader',
  data() {
    return {
      document: null,
      structuredData: null,
      flatNodes: [],
      paragraphs: [],
      headings: [],
      headingIndices: [],
      currentParagraphIndex: 0,
      currentHeadingIndex: -1,
      isSpeaking: false,
      speechRate: 1,
      speechPitch: 1,
      selectedVoice: 'male',
      availableVoices: [],
      fontSize: 18,
      lineHeight: 180,
      activityId: null,
      loading: false,
      showOutline: false,
      showCommandPalette: false,
      commandFilter: '',
      currentUtterance: null,
      speechChunkIndex: 0,
      speechChunks: []
    }
  },
  computed: {
    userSettings() {
      return this.$store.getters.userSettings
    },
    filteredCommands() {
      const commands = keyboardNav.getAvailableCommands()
      if (!this.commandFilter) return commands
      const filter = this.commandFilter.toLowerCase()
      const filtered = {}
      Object.entries(commands).forEach(([key, cmd]) => {
        if (cmd.description.toLowerCase().includes(filter) || 
            cmd.action.toLowerCase().includes(filter) ||
            key.toLowerCase().includes(filter)) {
          filtered[key] = cmd
        }
      })
      return filtered
    }
  },
  mounted() {
    this.loadDocument()
    this.loadVoices()
    this.setupKeyboardNavigation()
  },
  beforeDestroy() {
    this.stopSpeech()
    this.endActivity()
    keyboardNav.clearReaderContext()
  },
  methods: {
    async loadDocument() {
      this.loading = true
      try {
        const docId = this.$route.params.id
        
        const [docRes, structRes] = await Promise.all([
          documentApi.getById(docId),
          documentApi.getStructured(docId).catch(() => null)
        ])
        
        this.document = docRes.data
        
        if (structRes && structRes.data) {
          this.structuredData = structRes.data.data
          this.headings = structRes.data.headings || []
          this.paragraphs = structRes.data.paragraphs || []
          this.flatNodes = this.flattenStructure(this.structuredData)
          this.buildHeadingIndex()
        } else {
          this.parseContent(docRes.data?.content)
        }
        
        if (this.userSettings) {
          this.speechRate = (this.userSettings.voiceSpeed || 100) / 100
          if (this.userSettings.voiceType) {
            this.selectedVoice = this.userSettings.voiceType
          }
        }
        
        await this.startActivity(docId)
        
        this.$nextTick(() => {
          if (this.userSettings?.autoRead) {
            this.startSpeech()
          }
        })
      } catch (error) {
        console.error('加载文档失败:', error)
        this.$message.error('加载文档失败')
      } finally {
        this.loading = false
      }
    },

    flattenStructure(node, result = []) {
      if (node && node.content && node.content.trim()) {
        result.push({
          id: node.id,
          type: node.type,
          tagName: node.tagName,
          content: node.content,
          level: node.level,
          ariaRole: node.ariaRole,
          ariaLabel: node.ariaLabel
        })
      }
      if (node && node.children) {
        node.children.forEach(child => this.flattenStructure(child, result))
      }
      return result
    },

    getFlattenedIndex(node, child) {
      const all = this.flattenNodes(this.structuredData)
      return all.findIndex(n => n.id === child.id)
    },

    buildHeadingIndex() {
      this.headingIndices = []
      this.flatNodes.forEach((node, index) => {
        if (node.type === 'heading') {
          this.headingIndices.push(index)
        }
      })
    },

    parseContent(content) {
      if (!content) return
      this.paragraphs = content.split(/\n+/).filter(p => p.trim())
      this.headings = this.paragraphs.filter(p => 
        p.length < 80 && !p.endsWith('。') && !p.endsWith('，')
      )
    },

    async loadVoices() {
      try {
        const res = await ttsApi.getVoices()
        this.availableVoices = res.data || []
      } catch (e) {
        this.availableVoices = [
          { id: 'male', name: '男声', default: true },
          { id: 'female', name: '女声' },
          { id: 'child', name: '童声' },
          { id: 'deep', name: '深沉' }
        ]
      }
    },

    setupKeyboardNavigation() {
      keyboardNav.setReaderContext({
        toggleSpeech: () => this.toggleSpeech(),
        nextParagraph: () => this.nextParagraph(),
        prevParagraph: () => this.prevParagraph(),
        nextHeading: () => this.nextHeading(),
        prevHeading: () => this.prevHeading(),
        nextSection: () => this.nextSection(),
        prevSection: () => this.prevSection(),
        increaseSpeed: () => this.increaseSpeed(),
        decreaseSpeed: () => this.decreaseSpeed(),
        increasePitch: () => this.increasePitch(),
        decreasePitch: () => this.decreasePitch(),
        goToStart: () => this.goToStart(),
        goToEnd: () => this.goToEnd(),
        jumpToHeading: (level) => this.jumpToHeadingLevel(level),
        goBack: () => this.goBack()
      })
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
        const total = Math.max(this.paragraphs.length, this.flatNodes.length, 1)
        const progress = Math.round((this.currentParagraphIndex / total) * 100)
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
      
      const textToRead = this.getCurrentText()
      if (!textToRead) return
      
      this.prepareSpeechChunks(textToRead)
      this.speechChunkIndex = 0
      this.speakNextChunk()
    },

    getCurrentText() {
      if (this.structuredData && this.flatNodes.length > 0) {
        return this.flatNodes[this.currentParagraphIndex]?.content || ''
      }
      return this.paragraphs[this.currentParagraphIndex] || this.document?.content || ''
    },

    prepareSpeechChunks(text) {
      const maxLength = 200
      this.speechChunks = []
      
      if (text.length <= maxLength) {
        this.speechChunks.push(text)
        return
      }
      
      const sentences = text.split(/(?<=[。！？.!?])/)
      let currentChunk = ''
      
      for (const sentence of sentences) {
        if (currentChunk.length + sentence.length > maxLength && currentChunk) {
          this.speechChunks.push(currentChunk)
          currentChunk = sentence
        } else {
          currentChunk += sentence
        }
      }
      
      if (currentChunk) {
        this.speechChunks.push(currentChunk)
      }
    },

    speakNextChunk() {
      if (this.speechChunkIndex >= this.speechChunks.length) {
        this.isSpeaking = false
        this.autoAdvance()
        return
      }
      
      const text = this.speechChunks[this.speechChunkIndex]
      
      const utterance = new SpeechSynthesisUtterance(text)
      utterance.lang = 'zh-CN'
      utterance.rate = this.speechRate
      utterance.pitch = this.speechPitch
      utterance.volume = 1
      
      const voices = window.speechSynthesis.getVoices()
      const zhVoice = voices.find(v => v.lang.includes('zh'))
      if (zhVoice) {
        utterance.voice = zhVoice
      }
      
      utterance.onstart = () => {
        this.isSpeaking = true
      }
      
      utterance.onend = () => {
        this.speechChunkIndex++
        if (this.speechChunkIndex < this.speechChunks.length) {
          this.speakNextChunk()
        } else {
          this.isSpeaking = false
          this.autoAdvance()
        }
      }
      
      utterance.onerror = () => {
        this.isSpeaking = false
      }
      
      this.currentUtterance = utterance
      window.speechSynthesis.speak(utterance)
    },

    autoAdvance() {
      const total = Math.max(this.paragraphs.length, this.flatNodes.length)
      if (this.currentParagraphIndex < total - 1) {
        this.currentParagraphIndex++
        this.scrollToCurrent()
        this.startSpeech()
      }
    },

    pauseSpeech() {
      if (window.speechSynthesis) {
        if (window.speechSynthesis.paused) {
          window.speechSynthesis.resume()
          this.isSpeaking = true
        } else {
          window.speechSynthesis.pause()
          this.isSpeaking = false
        }
      }
    },

    stopSpeech() {
      if (window.speechSynthesis) {
        window.speechSynthesis.cancel()
        this.isSpeaking = false
      }
      this.speechChunks = []
      this.speechChunkIndex = 0
    },

    nextParagraph() {
      const total = Math.max(this.paragraphs.length, this.flatNodes.length)
      if (this.currentParagraphIndex < total - 1) {
        this.currentParagraphIndex++
        this.updateCurrentHeading()
        if (this.isSpeaking) {
          this.startSpeech()
        }
        this.scrollToCurrent()
      }
    },

    prevParagraph() {
      if (this.currentParagraphIndex > 0) {
        this.currentParagraphIndex--
        this.updateCurrentHeading()
        if (this.isSpeaking) {
          this.startSpeech()
        }
        this.scrollToCurrent()
      }
    },

    nextHeading() {
      if (this.headingIndices.length === 0) return
      
      let nextIdx = this.headingIndices.findIndex(idx => idx > this.currentParagraphIndex)
      if (nextIdx === -1) {
        nextIdx = 0
      }
      
      this.currentParagraphIndex = this.headingIndices[nextIdx]
      this.currentHeadingIndex = nextIdx
      
      if (this.isSpeaking) {
        this.startSpeech()
      }
      this.scrollToCurrent()
    },

    prevHeading() {
      if (this.headingIndices.length === 0) return
      
      let prevIdx = -1
      for (let i = this.headingIndices.length - 1; i >= 0; i--) {
        if (this.headingIndices[i] < this.currentParagraphIndex) {
          prevIdx = i
          break
        }
      }
      
      if (prevIdx === -1) {
        prevIdx = this.headingIndices.length - 1
      }
      
      this.currentParagraphIndex = this.headingIndices[prevIdx]
      this.currentHeadingIndex = prevIdx
      
      if (this.isSpeaking) {
        this.startSpeech()
      }
      this.scrollToCurrent()
    },

    nextSection() {
      this.nextHeading()
    },

    prevSection() {
      this.prevHeading()
    },

    updateCurrentHeading() {
      if (this.headingIndices.length === 0) return
      
      for (let i = this.headingIndices.length - 1; i >= 0; i--) {
        if (this.headingIndices[i] <= this.currentParagraphIndex) {
          this.currentHeadingIndex = i
          break
        }
      }
    },

    jumpToParagraph(index) {
      this.currentParagraphIndex = index
      this.updateCurrentHeading()
      if (this.isSpeaking) {
        this.startSpeech()
      }
      this.scrollToCurrent()
    },

    jumpToHeading(index) {
      if (this.headingIndices[index] !== undefined) {
        this.currentParagraphIndex = this.headingIndices[index]
        this.currentHeadingIndex = index
        if (this.isSpeaking) {
          this.startSpeech()
        }
        this.scrollToCurrent()
      }
    },

    jumpToHeadingLevel(level) {
      const targetIndex = this.flatNodes.findIndex((node, idx) => 
        node.type === 'heading' && node.level === level && idx > this.currentParagraphIndex
      )
      
      if (targetIndex !== -1) {
        this.jumpToParagraph(targetIndex)
      }
    },

    goToStart() {
      this.currentParagraphIndex = 0
      this.currentHeadingIndex = this.headingIndices[0] === 0 ? 0 : -1
      if (this.isSpeaking) {
        this.startSpeech()
      }
      this.scrollToCurrent()
    },

    goToEnd() {
      const total = Math.max(this.paragraphs.length, this.flatNodes.length)
      this.currentParagraphIndex = total - 1
      this.currentHeadingIndex = this.headingIndices.length - 1
      if (this.isSpeaking) {
        this.startSpeech()
      }
      this.scrollToCurrent()
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

    increasePitch() {
      if (this.speechPitch < 2) {
        this.speechPitch += 0.2
        if (this.isSpeaking) {
          this.startSpeech()
        }
      }
    },

    decreasePitch() {
      if (this.speechPitch > 0.5) {
        this.speechPitch -= 0.2
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

    toggleOutline() {
      this.showOutline = !this.showOutline
    },

    onVoiceChange() {
      if (this.isSpeaking) {
        this.startSpeech()
      }
    },

    scrollToCurrent() {
      this.$nextTick(() => {
        let targetElement = null
        
        if (this.structuredData) {
          targetElement = document.getElementById(`node-${this.currentParagraphIndex}`)
        } else {
          targetElement = document.getElementById(`para-${this.currentParagraphIndex}`)
        }
        
        if (targetElement) {
          targetElement.scrollIntoView({ behavior: 'smooth', block: 'center' })
          targetElement.focus()
        }
      })
    },

    goBack() {
      this.$router.push('/user/documents')
    },

    showCommandPalette() {
      this.showCommandPalette = true
      this.$nextTick(() => {
        this.$refs.commandInput?.focus()
      })
    },

    hideCommandPalette() {
      this.showCommandPalette = false
      this.commandFilter = ''
    },

    executeCommand(key) {
      const commands = keyboardNav.getAvailableCommands()
      const cmd = commands[key]
      if (cmd) {
        this.hideCommandPalette()
        if (this.$refs.readerContent) {
          this.$refs.readerContent.focus()
        }
      }
    }
  }
}
</script>

<style scoped>
.document-reader {
  padding: 20px;
  max-width: 900px;
  margin: 0 auto;
  background: #fff;
  min-height: 100vh;
}

.reader-header {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #e8e8e8;
}

.document-title {
  font-size: 24px;
  margin: 0 0 10px 0;
  color: #333;
}

.document-meta {
  color: #666;
  font-size: 14px;
}

.meta-item {
  margin-right: 20px;
}

.reader-toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  padding: 15px;
  background: #f5f5f5;
  border-radius: 8px;
  margin-bottom: 15px;
}

.toolbar-group {
  display: flex;
  gap: 5px;
}

.keyboard-hint {
  background: #e6f7ff;
  padding: 10px 15px;
  border-radius: 4px;
  margin-bottom: 15px;
  font-size: 13px;
  color: #666;
}

.keyboard-hint kbd {
  display: inline-block;
  padding: 2px 6px;
  background: #fff;
  border: 1px solid #d9d9d9;
  border-radius: 3px;
  font-family: monospace;
  font-size: 12px;
  margin: 0 2px;
}

.reader-container {
  display: flex;
  gap: 20px;
}

.document-outline {
  width: 200px;
  flex-shrink: 0;
  background: #fafafa;
  padding: 15px;
  border-radius: 8px;
  max-height: 600px;
  overflow-y: auto;
}

.outline-title {
  font-size: 16px;
  margin: 0 0 10px 0;
  color: #333;
}

.outline-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.outline-item {
  padding: 5px 0;
  border-bottom: 1px solid #eee;
}

.outline-item.active a {
  color: #1890ff;
  font-weight: bold;
}

.outline-item a {
  color: #666;
  text-decoration: none;
  display: block;
}

.outline-item a:hover,
.outline-item a:focus {
  color: #1890ff;
}

.reader-content {
  flex: 1;
  padding: 20px;
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  min-height: 400px;
}

.reader-content h1,
.reader-content h2,
.reader-content h3,
.reader-content h4,
.reader-content h5,
.reader-content h6 {
  margin: 20px 0 10px 0;
  padding: 10px;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.reader-content h1:hover,
.reader-content h2:hover,
.reader-content h3:hover,
.reader-content h4:hover,
.reader-content h5:hover,
.reader-content h6:hover {
  background-color: #f5f5f5;
}

.reader-content p {
  margin: 15px 0;
  line-height: inherit;
  padding: 10px;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.reader-content p:hover {
  background-color: #f5f5f5;
}

.reader-content ul {
  margin: 15px 0;
  padding-left: 30px;
}

.reader-content li {
  margin: 8px 0;
  padding: 5px;
  border-radius: 4px;
}

.current-element {
  background-color: #e6f7ff !important;
  border-left: 3px solid #1890ff;
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.2);
}

.current-element:focus {
  outline: 2px solid #1890ff;
  outline-offset: 2px;
}

.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.command-palette-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding-top: 100px;
  z-index: 2000;
}

.command-palette {
  background: white;
  padding: 20px;
  border-radius: 8px;
  width: 500px;
  max-height: 60vh;
  overflow-y: auto;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
}

.command-list {
  list-style: none;
  padding: 0;
  margin: 15px 0 0 0;
}

.command-list li {
  display: flex;
  justify-content: space-between;
  padding: 10px 15px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  border-radius: 4px;
}

.command-list li:hover {
  background: #f5f5f5;
}

.command-key {
  font-family: monospace;
  background: #f0f0f0;
  padding: 2px 8px;
  border-radius: 4px;
  color: #1890ff;
}

.command-desc {
  color: #666;
}
</style>
