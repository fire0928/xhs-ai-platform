<template>
  <div class="review-page">
    <!-- 顶部筛选栏 -->
    <div class="rev-top-bar">
      <div class="rev-filters-horizontal">
        <span v-for="f in filters" :key="f.value" :class="['ftab', { active: currentFilter === f.value }]" @click="switchFilter(f.value)">{{ f.label }}</span>
      </div>
      <div class="rev-search">
        <svg viewBox="0 0 14 14" fill="none"><circle cx="6" cy="6" r="4.5" stroke="currentColor" stroke-width="1.5"/><path d="M9.5 9.5L13 13" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
        <input type="text" placeholder="搜索..." v-model="keyword" @keydown.enter="loadList">
      </div>
    </div>
    <div class="review-layout">
      <div class="rev-feed">
        <div v-for="item in reviewList" :key="item.id" :class="['rev-fc', { active: selectedId === item.id }]" @click="selectItem(item)">
          <div class="rev-fc-th">
            <img v-if="item.imageUrls && item.imageUrls.length > 0" :src="item.imageUrls[0]" alt="配图" loading="lazy" @error="$event.target.style.display='none'">
            <svg v-else viewBox="0 0 80 100"><rect width="80" height="100" :fill="item.bg"/><circle cx="40" cy="35" r="15" :fill="item.iconBg" opacity=".6"/><rect x="15" y="55" width="50" height="30" rx="4" :fill="item.iconBg" opacity=".4"/></svg>
          </div>
          <div class="rev-fc-info">
            <div class="rev-fc-tt">{{ item.title }}</div>
            <div class="rev-fc-tags"><span v-for="t in item.tags" :key="t" :class="['tag', tagColor(t)]">{{ t }}</span></div>
            <div class="rev-fc-src">{{ item.source }}</div>
          </div>
        </div>
        <div v-if="reviewList.length === 0 && !loading" style="text-align:center;color:var(--cn-400);padding:32px;font-size:13px">暂无内容</div>
      </div>
      <div class="rev-det" v-if="currentContent">
        <div class="rev-det-inner">
          <!-- 图片展示 -->
          <div class="rev-det-img">
            <div v-if="currentContent.imageUrls && currentContent.imageUrls.length > 0" class="image-gallery">
              <div v-for="(img, idx) in currentContent.imageUrls" :key="idx" class="image-item">
                <img :src="img" alt="配图" @error="handleImgError($event, img)">
              </div>
            </div>
            <div v-else class="no-image-placeholder" :style="{background: currentContent.bg}">
              <svg viewBox="0 0 480 360" style="width:100%;height:100%"><rect width="480" height="360" :fill="currentContent.bg"/><rect x="60" y="40" width="200" height="140" rx="12" :fill="currentContent.iconBg" opacity=".4"/></svg>
            </div>
          </div>
          <div class="rev-det-tt">{{ currentContent.title }}</div>
          <div class="rev-det-ct">{{ currentContent.content || '暂无内容描述' }}</div>
          <div class="rev-det-tags"><span v-for="t in currentContent.tags" :key="t" class="tag tag-red">{{ t }}</span></div>

          <!-- AI 内容评分 -->
          <div class="rev-ai-score" v-if="aiScore">
            <h5><svg width="16" height="16" viewBox="0 0 16 16" fill="none"><path d="M8 1L9.5 5H14L10.5 7.5L12 12L8 9L4 12L5.5 7.5L2 5H6.5L8 1Z" fill="#8B5CF6"/></svg>AI 内容评分</h5>
            <div class="score-item">
              <span class="score-label">吸引力</span>
              <div class="score-bar"><div class="score-fill" :style="{width: aiScore.attractiveness + '%', background: '#EF4444'}"></div></div>
              <span class="score-val">{{ aiScore.attractiveness }}</span>
            </div>
            <div class="score-item">
              <span class="score-label">可读性</span>
              <div class="score-bar"><div class="score-fill" :style="{width: aiScore.readability + '%', background: '#22C55E'}"></div></div>
              <span class="score-val">{{ aiScore.readability }}</span>
            </div>
            <div class="score-item">
              <span class="score-label">标签匹配</span>
              <div class="score-bar"><div class="score-fill" :style="{width: aiScore.tagMatch + '%', background: '#8B5CF6'}"></div></div>
              <span class="score-val">{{ aiScore.tagMatch }}</span>
            </div>
            <div class="score-item">
              <span class="score-label">平台适配</span>
              <div class="score-bar"><div class="score-fill" :style="{width: aiScore.platformFit + '%', background: '#3B82F6'}"></div></div>
              <span class="score-val">{{ aiScore.platformFit }}</span>
            </div>
          </div>

          <div class="rev-ai">
            <h5><svg width="16" height="16" viewBox="0 0 16 16" fill="none"><path d="M8 1L9.5 5H14L10.5 7.5L12 12L8 9L4 12L5.5 7.5L2 5H6.5L8 1Z" fill="#8B5CF6"/></svg>内容详情</h5>
            <div style="font-size:12px;color:var(--cn-500);margin-top:8px">
              <div>创作时间：{{ currentContent.createTime || '--' }}</div>
              <div style="margin-top:4px">审核状态：{{ statusLabels[currentContent.auditStatus] || '未知' }}</div>
            </div>
          </div>
          <div class="rev-acts">
            <button class="rev-ab ap" v-if="canApprove" @click="approveItem">✅ 通过</button>
            <button class="rev-ab ed" @click="goEdit">✏️ 编辑</button>
            <button class="rev-ab rj" v-if="canApprove" @click="rejectItem">❌ 拒绝</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { contentApi } from '@/api'

const filters = [
  { label: '全部', value: null },
  { label: '待审核', value: 1 },
  { label: '已通过', value: 2 },
  { label: '已拒绝', value: 3 }
]
const statusLabels = { 0: '草稿', 1: '审核中', 2: '已通过', 3: '已拒绝' }

const currentFilter = ref(1)
const keyword = ref('')
const reviewList = ref([])
const selectedId = ref(null)
const loading = ref(false)
const aiScore = ref(null)

const cardColors = ['#FFE0E5', '#EDE9FE', '#FEF3C7', '#DCFCE7', '#DBEAFE']
const cardIconColors = ['#FF9DAD', '#8B5CF6', '#F59E0B', '#22C55E', '#3B82F6']

const currentContent = computed(() => reviewList.value.find(item => item.id === selectedId.value) || null)

const canApprove = computed(() => {
  if (!currentContent.value) return false
  return currentContent.value.auditStatus === 1
})

function tagColor(tag) {
  const colors = { 'AI': 'tag-purple', '美食': 'tag-red', '旅行': 'tag-orange', '生活': 'tag-green', '健康': 'tag-green', '穿搭': 'tag-orange', '手动': 'tag-blue' }
  for (const [k, v] of Object.entries(colors)) { if (tag.includes(k)) return v }
  return 'tag-red'
}

function handleImgError(e, url) {
  console.warn('Image load failed:', url)
  // 加载失败时显示占位背景
  e.target.style.display = 'none'
  e.target.parentElement.style.background = 'linear-gradient(135deg, #FFE0E5, #EDE9FE)'
}

function selectItem(item) {
  selectedId.value = item.id
  aiScore.value = null
  loadAiScore(item.id)
}

async function loadAiScore(contentId) {
  try {
    const res = await contentApi.aiScore(contentId)
    if (res.data.code === 200 && res.data.data) {
      aiScore.value = res.data.data
    }
  } catch { /* 评分接口可选，失败不报错 */ }
}

function goEdit() {
  if (!currentContent.value) return
  const draftData = {
    id: currentContent.value.id,
    title: currentContent.value.title,
    content: currentContent.value.content,
    imageUrls: currentContent.value.imageUrls,
    tags: currentContent.value.tags ? currentContent.value.tags.join(',') : ''
  }
  sessionStorage.setItem('editDraft', JSON.stringify(draftData))
  window.location.hash = '#/content-edit/' + currentContent.value.id
}

async function loadList() {
  loading.value = true
  try {
    const params = { page: 1, pageSize: 20 }
    // Review 页面只显示 auditStatus >= 1 的内容（待审核/已通过/已拒绝），不显示草稿
    if (currentFilter.value !== null) {
      params.auditStatus = currentFilter.value
    }
    if (keyword.value.trim()) params.keyword = keyword.value.trim()
    const res = await contentApi.myList(params)
    if (res.data.code === 200) {
      const list = res.data.data?.records || []
      // 过滤掉草稿（auditStatus=0）
      const filtered = list.filter(c => c.auditStatus !== 0)
      reviewList.value = filtered.map((c, i) => ({
        id: c.id,
        title: c.title || '无标题',
        content: c.content || '',
        tags: c.tags ? c.tags.split(',').filter(Boolean) : [],
        source: c.agentName || 'AI 创作',
        auditStatus: c.auditStatus,
        createTime: c.createTime ? c.createTime.substring(0, 10) : '',
        imageUrls: c.imageUrls ? c.imageUrls.split(',').filter(Boolean) : [],
        bg: cardColors[i % cardColors.length],
        iconBg: cardIconColors[i % cardColors.length]
      }))
    }
  } catch (e) { console.error('loadList error:', e) }
  finally { loading.value = false }
}

function switchFilter(val) {
  currentFilter.value = val
  loadList()
}

async function approveItem() {
  if (!currentContent.value) return
  try {
    const res = await contentApi.approve(currentContent.value.id)
    if (res.data.code === 200) {
      // 更新本地状态，无需重新加载整个列表
      currentContent.value.auditStatus = 2
      const item = reviewList.value.find(i => i.id === currentContent.value.id)
      if (item) item.auditStatus = 2
    }
  } catch (e) { alert('操作失败: ' + (e.response?.data?.message || e.message)) }
}

async function rejectItem() {
  if (!currentContent.value) return
  try {
    const res = await contentApi.reject(currentContent.value.id, '审核不通过')
    if (res.data.code === 200) {
      currentContent.value.auditStatus = 3
      const item = reviewList.value.find(i => i.id === currentContent.value.id)
      if (item) item.auditStatus = 3
    }
  } catch (e) { alert('操作失败: ' + (e.response?.data?.message || e.message)) }
}

onMounted(loadList)
</script>

<style scoped>
.review-page { display: flex; flex-direction: column; height: calc(100vh - 56px - 64px); margin: -32px; margin-top: 0; }
.review-layout { display: grid; grid-template-columns: 340px 1fr; gap: 0; flex: 1; overflow: hidden; }
.rev-feed { overflow-y: auto; padding: 12px; border-right: 1px solid var(--cn-100); background: #fff; }
.rev-filters { display: flex; align-items: center; gap: 12px; padding: 16px 20px; border-bottom: 1px solid var(--cn-100); }
.ftab { padding: 4px 12px; border-radius: 9999px; font-size: 13px; font-weight: 500; color: var(--cn-500); cursor: pointer; transition: all .15s; }
.ftab:hover { color: var(--cn-700); background: var(--cn-100); }
.ftab.active { background: var(--cp-50); color: var(--cp-600); }
.rev-search { margin-left: auto; display: flex; align-items: center; gap: 8px; background: var(--cn-100); border-radius: 9999px; padding: 4px 12px; }
.rev-search input { border: none; background: none; outline: none; font-size: 11px; width: 120px; }
.rev-search svg { width: 14px; height: 14px; color: var(--cn-400); }
.rev-fc { display: flex; gap: 12px; padding: 12px; border-radius: 8px; cursor: pointer; transition: all .15s; margin-bottom: 8px; }
.rev-fc:hover { background: var(--cn-50); }
.rev-fc.active { background: var(--cp-50); }
.rev-fc-th { width: 80px; height: 100px; border-radius: 6px; overflow: hidden; flex-shrink: 0; background: var(--cn-100); }
.rev-fc-th img { width: 100%; height: 100%; object-fit: cover; display: block; }
.rev-fc-info { flex: 1; min-width: 0; padding-top: 4px; }
.rev-fc-tt { font-size: 13px; font-weight: 600; color: var(--cn-800); margin-bottom: 4px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.rev-fc-tags { display: flex; gap: 4px; flex-wrap: wrap; margin-bottom: 8px; }
.rev-fc-src { font-size: 11px; color: var(--cn-400); display: flex; align-items: center; gap: 4px; }
.tag { display: inline-flex; align-items: center; padding: 2px 8px; border-radius: 9999px; font-size: 11px; font-weight: 500; }
.tag-red { background: var(--cp-50); color: var(--cp-600); }
.tag-purple { background: var(--ca-purple-l); color: var(--ca-purple); }
.tag-orange { background: var(--ca-orange-l); color: #B45309; }
.tag-green { background: #DCFCE7; color: #15803D; }
.tag-blue { background: #DBEAFE; color: #1D4ED8; }

.rev-det { display: flex; justify-content: center; padding: 20px; overflow-y: auto; }
.rev-top-bar { display: flex; align-items: center; justify-content: space-between; padding: 12px 20px; border-bottom: 1px solid var(--cn-100); background: #fff; flex-shrink: 0; }
.rev-filters-horizontal { display: flex; align-items: center; gap: 8px; }
.rev-det-inner { width: 100%; max-width: 680px; display: flex; flex-direction: column; }
.rev-det-tt { font-size: 20px; font-weight: 700; color: var(--cn-900); margin-bottom: 12px; }
.rev-det-ct { font-size: 13px; line-height: 1.8; color: var(--cn-600); margin-bottom: 20px; white-space: pre-line; }
.rev-det-tags { display: flex; gap: 8px; flex-wrap: wrap; margin-bottom: 20px; }
.rev-ai { background: linear-gradient(135deg, #FFE0E5, #EDE9FE); border-radius: 12px; padding: 16px; margin-bottom: 20px; }
.rev-ai h5 { font-size: 13px; font-weight: 600; color: var(--cn-700); margin-bottom: 0; display: flex; align-items: center; gap: 8px; }

/* AI 评分 */
.rev-ai-score { background: linear-gradient(135deg, #FFE0E5, #EDE9FE); border-radius: 12px; padding: 16px; margin-bottom: 20px; }
.rev-ai-score h5 { font-size: 13px; font-weight: 600; color: var(--cn-700); margin-bottom: 12px; display: flex; align-items: center; gap: 8px; }
.score-item { display: flex; align-items: center; gap: 12px; margin-bottom: 10px; }
.score-item:last-child { margin-bottom: 0; }
.score-label { width: 60px; font-size: 12px; color: var(--cn-500); flex-shrink: 0; }
.score-bar { flex: 1; height: 6px; background: rgba(0,0,0,.06); border-radius: 999px; overflow: hidden; }
.score-fill { height: 100%; border-radius: 999px; transition: width .6s ease; }
.score-val { width: 28px; font-size: 12px; font-weight: 600; color: var(--cn-700); text-align: right; flex-shrink: 0; }

/* 图片画廊 */
.rev-det-img { width: 100%; min-height: 200px; border-radius: 12px; overflow: hidden; background: var(--cn-100); margin-bottom: 20px; }
.image-gallery { display: grid; grid-template-columns: repeat(3, 1fr); gap: 8px; padding: 12px; }
.image-item { aspect-ratio: 1; border-radius: 8px; overflow: hidden; background: var(--cn-100); min-height: 120px; }
.image-item img { width: 100%; height: 100%; object-fit: cover; display: block; }

/* 评分条缩小 */
.rev-ai-score { max-width: 480px; }
.no-image-placeholder { width: 100%; aspect-ratio: 4/3; display: flex; align-items: center; justify-content: center; }
.rev-acts { display: flex; gap: 12px; padding-top: 16px; border-top: 1px solid var(--cn-100); margin-top: auto; }
.rev-ab { flex: 1; display: flex; align-items: center; justify-content: center; gap: 8px; padding: 12px; border-radius: 8px; font-size: 13px; font-weight: 600; cursor: pointer; transition: all .15s; border: none; }
.rev-ab.ap { background: #DCFCE7; color: #15803D; }
.rev-ab.ap:hover { background: #BBF7D0; }
.rev-ab.ed { background: #DBEAFE; color: #1D4ED8; }
.rev-ab.ed:hover { background: #BFDBFE; }
.rev-ab.rj { background: #FEE2E2; color: #B91C1C; }
.rev-ab.rj:hover { background: #FECACA; }
</style>
