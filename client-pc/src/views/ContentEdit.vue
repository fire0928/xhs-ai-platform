<template>
  <div class="page">
    <div class="header">
      <div>
        <h2 class="h-xl">编辑内容</h2>
        <p class="sub">修改文案、重新生成或提交审核</p>
      </div>
      <div class="header-actions">
        <button class="btn btn-secondary" @click="$router.back()">返回</button>
      </div>
    </div>

    <div v-if="loading" style="text-align:center;padding:60px;color:var(--cn-400)">加载中...</div>

    <div v-else class="edit-layout">
      <!-- 左侧：文案编辑 -->
      <div class="edit-main">
        <div class="edit-section">
          <div class="section-title">
            <svg width="16" height="16" viewBox="0 0 16 16" fill="none"><path d="M2 3h12v10H2z" stroke="currentColor" stroke-width="1.2"/><path d="M2 6h12M5 3v10" stroke="currentColor" stroke-width="1.2"/></svg>
            标题
          </div>
          <input type="text" v-model="editTitle" class="edit-input" placeholder="输入标题...">
        </div>

        <div class="edit-section">
          <div class="section-title">
            <svg width="16" height="16" viewBox="0 0 16 16" fill="none"><path d="M3 2h10v12H3z" stroke="currentColor" stroke-width="1.2"/><path d="M5 5h6M5 8h4M5 11h3" stroke="currentColor" stroke-width="1.2" stroke-linecap="round"/></svg>
            文案内容
            <button class="btn-ai-regen" @click="aiRegenerate" :disabled="regenerating">
              <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M7 1L8.5 5H12L9 7.5L10 11.5L7 9L4 11.5L5 7.5L2 5H5.5L7 1Z" fill="currentColor"/></svg>
              {{ regenerating ? '生成中...' : 'AI 重新生成' }}
            </button>
          </div>
          <textarea v-model="editContent" class="edit-textarea" rows="12" placeholder="输入文案内容..."></textarea>
          <div v-if="regenError" class="error-msg">{{ regenError }}</div>
        </div>

        <div class="edit-section">
          <div class="section-title">
            <svg width="16" height="16" viewBox="0 0 16 16" fill="none"><rect x="2" y="3" width="12" height="10" rx="1" stroke="currentColor" stroke-width="1.2"/><circle cx="8" cy="8" r="2.5" stroke="currentColor" stroke-width="1.2"/></svg>
            标签
          </div>
          <input type="text" v-model="editTags" class="edit-input" placeholder="用逗号分隔标签，如：美食,探店,推荐">
        </div>
      </div>

      <!-- 右侧：图片和预览 -->
      <div class="edit-side">
        <div class="edit-section">
          <div class="section-title">配图</div>
          <div v-if="editImages.length === 0" class="empty-images">
            <svg width="48" height="48" viewBox="0 0 48 48" fill="none"><rect x="8" y="10" width="32" height="28" rx="4" stroke="var(--cn-300)" stroke-width="2"/><circle cx="20" cy="24" r="4" stroke="var(--cn-300)" stroke-width="2"/><path d="M8 34l10-10 8 8 6-6 8 8" stroke="var(--cn-300)" stroke-width="2" stroke-linecap="round"/></svg>
            <p>暂无图片</p>
          </div>
          <div v-else class="images-grid">
            <div v-for="(img, i) in editImages" :key="i" class="image-item">
              <img :src="img" alt="配图" @error="onImageError(i)">
              <button class="img-remove" @click="removeImage(i)">&times;</button>
            </div>
          </div>
        </div>

        <div class="edit-section">
          <div class="section-title">操作</div>
          <div class="action-btns">
            <button class="btn btn-save" @click="saveDraft" :disabled="saving">
              {{ saving ? '保存中...' : '保存草稿' }}
            </button>
            <button class="btn btn-submit" @click="submitToAudit" :disabled="submitting">
              {{ submitting ? '提交中...' : '加入内容库（提交审核）' }}
            </button>
          </div>
          <div v-if="toast.show" :class="['toast', toast.type]">{{ toast.msg }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { contentApi, aiApi } from '@/api'

const route = useRoute()
const router = useRouter()
const contentId = ref(route.params.id)

const loading = ref(false)
const saving = ref(false)
const submitting = ref(false)
const regenerating = ref(false)
const regenError = ref('')

const editTitle = ref('')
const editContent = ref('')
const editTags = ref('')
const editImages = ref([])
const originalAgentId = ref(null)

const toast = ref({ show: false, msg: '', type: 'info' })

function showToast(msg, type = 'info') {
  toast.value = { show: true, msg, type }
  setTimeout(() => toast.value.show = false, 3000)
}

async function loadContent() {
  if (!contentId.value) {
    // 从 sessionStorage 读取创作记录的数据
    const draft = sessionStorage.getItem('editDraft')
    if (draft) {
      try {
        const d = JSON.parse(draft)
        editTitle.value = d.title || ''
        editContent.value = d.content || ''
        editTags.value = d.tags || ''
        editImages.value = d.imageUrls || []
        originalAgentId.value = d.agentId || null
        sessionStorage.removeItem('editDraft')
        return
      } catch {}
    }
    showToast('没有可编辑的内容', 'error')
    router.back()
    return
  }

  loading.value = true
  try {
    const res = await contentApi.detail(contentId.value)
    if (res.data.code === 200) {
      const c = res.data.data
      editTitle.value = c.title || ''
      editContent.value = c.content || ''
      editTags.value = c.tags || ''
      editImages.value = c.imageUrls ? c.imageUrls.split(',').filter(Boolean) : []
      originalAgentId.value = c.agentId || null
    } else {
      showToast('加载内容失败', 'error')
    }
  } catch (e) {
    showToast('加载内容失败: ' + (e.response?.data?.message || e.message), 'error')
  } finally {
    loading.value = false
  }
}

async function saveDraft() {
  if (!contentId.value) {
    showToast('无法保存：缺少内容ID', 'error')
    return
  }
  saving.value = true
  try {
    const data = {
      title: editTitle.value,
      content: editContent.value,
      tags: editTags.value,
      imageUrls: editImages.value.join(',')
    }
    const res = await contentApi.update(contentId.value, data)
    if (res.data.code === 200) {
      showToast('草稿已保存', 'success')
    } else {
      showToast('保存失败', 'error')
    }
  } catch (e) {
    showToast('保存失败: ' + (e.response?.data?.message || e.message), 'error')
  } finally {
    saving.value = false
  }
}

async function submitToAudit() {
  if (!contentId.value) {
    showToast('无法提交：缺少内容ID', 'error')
    return
  }
  // 先保存
  await saveDraft()
  // 再提交审核
  submitting.value = true
  try {
    const res = await contentApi.submitAudit(contentId.value)
    if (res.data.code === 200) {
      showToast('已提交审核，进入内容库', 'success')
      setTimeout(() => router.push('/review'), 1500)
    } else {
      showToast('提交失败', 'error')
    }
  } catch (e) {
    showToast('提交失败: ' + (e.response?.data?.message || e.message), 'error')
  } finally {
    submitting.value = false
  }
}

async function aiRegenerate() {
  if (!editTitle.value) {
    regenError.value = '请先填写标题'
    return
  }
  regenerating.value = true
  regenError.value = ''
  try {
    const res = await aiApi.generateContent({
      title: editTitle.value,
      agentId: originalAgentId.value,
      prompt: editTitle.value
    })
    if (res.data.code === 200 && res.data.data?.content) {
      editContent.value = res.data.data.content
      showToast('文案已重新生成', 'success')
    } else {
      regenError.value = '生成失败，请重试'
    }
  } catch (e) {
    regenError.value = '生成失败: ' + (e.response?.data?.message || e.message)
  } finally {
    regenerating.value = false
  }
}

function removeImage(index) {
  editImages.value.splice(index, 1)
}

function onImageError(index) {
  editImages.value.splice(index, 1)
}

onMounted(loadContent)
</script>

<style scoped>
.page { padding: 32px; }
.header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px; }
.h-xl { font-size: 20px; font-weight: 700; color: var(--cn-900); margin-bottom: 4px; }
.sub { font-size: 13px; color: var(--cn-500); }
.header-actions { display: flex; gap: 10px; }
.btn { display: inline-flex; align-items: center; justify-content: center; gap: 6px; padding: 8px 16px; border-radius: 8px; font-size: 13px; font-weight: 600; cursor: pointer; transition: all .15s; white-space: nowrap; border: none; }
.btn-secondary { background: var(--cn-100); color: var(--cn-600); }
.btn-secondary:hover { background: var(--cn-200); }

.edit-layout { display: grid; grid-template-columns: 1fr 360px; gap: 24px; }
.edit-main { display: flex; flex-direction: column; gap: 20px; }
.edit-side { display: flex; flex-direction: column; gap: 20px; }
.edit-section { background: #fff; border-radius: 12px; padding: 20px; border: 1px solid var(--cn-100); }
.section-title { font-size: 14px; font-weight: 600; color: var(--cn-700); margin-bottom: 12px; display: flex; align-items: center; gap: 8px; }
.section-title svg { color: var(--cn-400); }
.edit-input { width: 100%; padding: 10px 14px; border: 1px solid var(--cn-200); border-radius: 8px; font-size: 14px; outline: none; transition: border-color .15s; }
.edit-input:focus { border-color: var(--cp-400); }
.edit-textarea { width: 100%; padding: 12px 14px; border: 1px solid var(--cn-200); border-radius: 8px; font-size: 14px; line-height: 1.7; outline: none; resize: vertical; transition: border-color .15s; font-family: inherit; }
.edit-textarea:focus { border-color: var(--cp-400); }
.btn-ai-regen { margin-left: auto; display: inline-flex; align-items: center; gap: 4px; padding: 5px 12px; border-radius: 6px; font-size: 12px; font-weight: 600; cursor: pointer; border: none; background: linear-gradient(135deg, #FFE0E5, #EDE9FE); color: var(--cp-600); transition: all .15s; }
.btn-ai-regen:hover:not(:disabled) { background: linear-gradient(135deg, #FFD1D9, #E0D9FC); }
.btn-ai-regen:disabled { opacity: .6; cursor: not-allowed; }
.error-msg { margin-top: 8px; font-size: 12px; color: var(--ce); }

.empty-images { text-align: center; padding: 32px; color: var(--cn-400); }
.empty-images p { font-size: 13px; margin-top: 8px; }
.images-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 8px; }
.image-item { position: relative; border-radius: 8px; overflow: hidden; aspect-ratio: 1; background: var(--cn-100); }
.image-item img { width: 100%; height: 100%; object-fit: cover; }
.img-remove { position: absolute; top: 4px; right: 4px; width: 22px; height: 22px; border-radius: 50%; background: rgba(0,0,0,.5); color: #fff; border: none; font-size: 14px; cursor: pointer; display: flex; align-items: center; justify-content: center; }
.img-remove:hover { background: rgba(0,0,0,.7); }

.action-btns { display: flex; flex-direction: column; gap: 10px; }
.btn-save { background: var(--cn-100); color: var(--cn-700); width: 100%; }
.btn-save:hover:not(:disabled) { background: var(--cn-200); }
.btn-submit { background: var(--cp-500); color: #fff; width: 100%; }
.btn-submit:hover:not(:disabled) { background: var(--cp-600); }
.btn:disabled { opacity: .5; cursor: not-allowed; }

.toast { margin-top: 12px; padding: 10px 14px; border-radius: 8px; font-size: 13px; font-weight: 500; text-align: center; }
.toast.success { background: #DCFCE7; color: #15803D; }
.toast.error { background: #FEE2E2; color: #B91C1C; }
.toast.info { background: #DBEAFE; color: #1D4ED8; }

@media (max-width: 900px) {
  .edit-layout { grid-template-columns: 1fr; }
}
</style>
