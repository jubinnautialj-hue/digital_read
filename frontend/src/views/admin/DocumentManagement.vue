<template>
  <div role="main" aria-label="文档管理">
    <h2 tabindex="0" style="margin-bottom: 20px;">文档管理</h2>
    
    <div style="margin-bottom: 20px; display: flex; gap: 10px;">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索文档..."
        style="width: 300px;"
        clearable
        @keyup.enter="loadDocuments"
      >
        <el-button slot="append" icon="el-icon-search" @click="loadDocuments">搜索</el-button>
      </el-input>
      <el-button type="primary" @click="openCreateDialog" aria-label="添加文档">
        <i class="el-icon-plus"></i> 添加文档
      </el-button>
    </div>

    <el-table :data="filteredDocuments" style="width: 100%;" border aria-label="文档列表">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" min-width="200" />
      <el-table-column prop="category" label="分类" width="120" />
      <el-table-column prop="author" label="作者" width="120" />
      <el-table-column prop="viewCount" label="阅读次数" width="100" />
      <el-table-column prop="isStructured" label="结构化" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isStructured ? 'success' : 'info'" size="small">
            {{ scope.row.isStructured ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="isAccessible" label="无障碍" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isAccessible ? 'success' : 'warning'" size="small">
            {{ scope.row.isAccessible ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="180">
        <template slot-scope="scope">
          {{ formatDate(scope.row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="primary" @click="openEditDialog(scope.row)" aria-label="编辑">编辑</el-button>
          <el-button size="mini" type="danger" @click="deleteDocument(scope.row)" aria-label="删除">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form ref="docForm" :model="docForm" :rules="docRules" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="docForm.title" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="docForm.description" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="docForm.author" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="docForm.category" style="width: 100%;">
            <el-option label="使用指南" value="使用指南" />
            <el-option label="技术文档" value="技术文档" />
            <el-option label="教程" value="教程" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="docForm.content" type="textarea" :rows="10" />
        </el-form-item>
        <el-form-item label="文件类型">
          <el-select v-model="docForm.fileType">
            <el-option label="文本" value="TEXT" />
            <el-option label="PDF" value="PDF" />
            <el-option label="Word" value="DOCX" />
          </el-select>
        </el-form-item>
        <el-form-item label="结构化">
          <el-switch
            v-model="docForm.isStructured"
            active-text="是"
            inactive-text="否"
          />
        </el-form-item>
        <el-form-item label="无障碍">
          <el-switch
            v-model="docForm.isAccessible"
            active-text="是"
            inactive-text="否"
          />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveDocument">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { documentApi } from '@/api'

export default {
  name: 'DocumentManagement',
  data() {
    return {
      documents: [],
      searchKeyword: '',
      dialogVisible: false,
      isEdit: false,
      dialogTitle: '添加文档',
      docForm: {
        id: null,
        title: '',
        description: '',
        author: '',
        category: '',
        content: '',
        fileType: 'TEXT',
        isStructured: true,
        isAccessible: true
      },
      docRules: {
        title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
        content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
      }
    }
  },
  computed: {
    filteredDocuments() {
      if (!this.searchKeyword) return this.documents
      const keyword = this.searchKeyword.toLowerCase()
      return this.documents.filter(d =>
        d.title?.toLowerCase().includes(keyword) ||
        d.description?.toLowerCase().includes(keyword)
      )
    }
  },
  mounted() {
    this.loadDocuments()
  },
  methods: {
    async loadDocuments() {
      try {
        const res = await documentApi.getAll()
        this.documents = res.data || []
      } catch (error) {
        console.error('加载文档失败:', error)
      }
    },
    openCreateDialog() {
      this.isEdit = false
      this.dialogTitle = '添加文档'
      this.docForm = {
        id: null,
        title: '',
        description: '',
        author: '',
        category: '',
        content: '',
        fileType: 'TEXT',
        isStructured: true,
        isAccessible: true
      }
      this.dialogVisible = true
    },
    openEditDialog(doc) {
      this.isEdit = true
      this.dialogTitle = '编辑文档'
      this.docForm = { ...doc }
      this.dialogVisible = true
    },
    async saveDocument() {
      try {
        if (this.isEdit) {
          await documentApi.update(this.docForm.id, this.docForm)
          this.$message.success('更新成功')
        } else {
          await documentApi.create(this.docForm)
          this.$message.success('创建成功')
        }
        this.dialogVisible = false
        this.loadDocuments()
      } catch (error) {
        console.error('保存失败:', error)
      }
    },
    deleteDocument(doc) {
      this.$confirm(`确定要删除文档 "${doc.title}" 吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await documentApi.delete(doc.id)
          this.$message.success('删除成功')
          this.loadDocuments()
        } catch (error) {
          console.error('删除失败:', error)
        }
      }).catch(() => {})
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      return new Date(dateStr).toLocaleString('zh-CN')
    }
  }
}
</script>
