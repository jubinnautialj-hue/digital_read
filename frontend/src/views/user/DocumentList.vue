<template>
  <div role="main" aria-label="文档列表">
    <h2 tabindex="0" style="margin-bottom: 20px;">文档库</h2>
    
    <el-card style="margin-bottom: 20px;">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索文档标题或描述..."
        clearable
        aria-label="搜索文档"
        size="large"
        @keyup.enter="searchDocuments"
        @clear="loadDocuments"
      >
        <el-button slot="append" icon="el-icon-search" @click="searchDocuments" aria-label="搜索">搜索</el-button>
      </el-input>
    </el-card>

    <el-empty v-if="documents.length === 0" description="暂无可用文档" />
    
    <el-row :gutter="20" v-else>
      <el-col :span="12" v-for="doc in documents" :key="doc.id" style="margin-bottom: 20px;">
        <el-card
          @click="openDocument(doc.id)"
          @keydown.enter="openDocument(doc.id)"
          tabindex="0"
          style="cursor: pointer; height: 100%;"
          role="button"
          :aria-label="`打开文档: ${doc.title}`"
        >
          <div style="display: flex; justify-content: space-between; align-items: start;">
            <div style="flex: 1;">
              <h3 style="margin: 0 0 10px 0; font-size: 18px;">{{ doc.title }}</h3>
              <p style="color: #606266; margin: 0 0 10px 0; line-height: 1.6;">
                {{ doc.description || '暂无描述' }}
              </p>
              <div style="display: flex; gap: 15px; color: #909399; font-size: 13px;">
                <span v-if="doc.author">作者: {{ doc.author }}</span>
                <span v-if="doc.category">分类: {{ doc.category }}</span>
                <span>阅读: {{ doc.viewCount }} 次</span>
              </div>
            </div>
            <el-tag v-if="doc.isAccessible" type="success" size="small">无障碍</el-tag>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <div class="keyboard-hint" style="margin-top: 20px;">
      <strong>提示：</strong>使用 <kbd>Tab</kbd> 切换文档卡片，<kbd>Enter</kbd> 打开文档，<kbd>Alt + S</kbd> 聚焦搜索框
    </div>
  </div>
</template>

<script>
import { documentApi } from '@/api'

export default {
  name: 'DocumentList',
  data() {
    return {
      documents: [],
      searchKeyword: '',
      loading: false
    }
  },
  mounted() {
    this.loadDocuments()
    this.handleKeyboardShortcuts()
  },
  methods: {
    async loadDocuments() {
      this.loading = true
      try {
        const res = await documentApi.getAccessible()
        this.documents = res.data || []
      } catch (error) {
        console.error('加载文档失败:', error)
      } finally {
        this.loading = false
      }
    },
    async searchDocuments() {
      if (!this.searchKeyword.trim()) {
        this.loadDocuments()
        return
      }
      this.loading = true
      try {
        const res = await documentApi.search(this.searchKeyword)
        this.documents = res.data || []
      } catch (error) {
        console.error('搜索失败:', error)
      } finally {
        this.loading = false
      }
    },
    openDocument(docId) {
      this.$router.push(`/user/reader/${docId}`)
    },
    handleKeyboardShortcuts() {
      document.addEventListener('keydown', (e) => {
        if (e.altKey && (e.key === 's' || e.key === 'S')) {
          e.preventDefault()
          this.$nextTick(() => {
            const searchInput = document.querySelector('.el-input__inner')
            if (searchInput) searchInput.focus()
          })
        }
      })
    }
  }
}
</script>
