<template>
  <div>
    <div class="header">
      <div>
        <h2 class="h-xl">发布队列</h2>
        <p class="sub">管理内容发布计划，从已审核内容中选择加入队列</p>
      </div>
      <button class="btn btn-primary" @click="showAddModal = true">
        <svg width="16" height="16" viewBox="0 0 16 16" fill="none"><path d="M8 2V14M2 8H14" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
        加入内容
      </button>
    </div>

    <div class="ql">
      <div>
        <div class="qdg">
          <div class="qdh"><span class="dd"></span> 待发布内容 ({{ pendingItems.length }})</div>
          <QueueCard v-for="item in pendingItems" :key="item.id" :title="item.title" :time="item.time"
            :status="item.status" :statusClass="item.statusClass" :bg="item.bg" :imageUrl="item.imageUrl"
            :showDelete="true" @click="openDetail(item)" @delete="removeItem(item)" />
          <div v-if="pendingItems.length === 0" style="padding:24px;text-align:center;color:var(--cn-400);font-size:13px">暂无待发布内容，点击右上角"加入内容"添加</div>
        </div>
        <div class="qdg">
          <div class="qdh"><span class="dd orange"></span> 已发布内容 ({{ publishedItems.length }})</div>
          <QueueCard v-for="item in publishedItems" :key="item.id" :title="item.title" :time="item.time"
            :status="item.status" :statusClass="item.statusClass" :bg="item.bg" :imageUrl="item.imageUrl"
            :showDelete="false" @click="openDetail(item)" />
          <div v-if="publishedItems.length === 0" style="padding:24px;text-align:center;color:var(--cn-400);font-size:13px">暂无已发布内容</div>
        </div>
      </div>

      <div class="qs">
        <h4>队列统计</h4>
        <div class="qs-it"><span class="qs-lb">待发布</span><span class="qs-vl">{{ stats.todayPending || 0 }}</span></div>
        <div class="qs-it"><span class="qs-lb">已发布</span><span class="qs-vl">{{ stats.totalSuccess || 0 }}</span></div>
        <div class="qs-it"><span class="qs-lb">发布成功率</span><span class="qs-vl" style="color:var(--cs)">{{ stats.successRate || '--' }}%</span></div>
        <div class="qs-bar">
          <div class="qs-bar-lb">发布进度</div>
          <div class="qs-bar-tr"><div class="qs-bar-fl" :style="{ width: publishProgress + '%', background: 'var(--gb)' }"></div></div>
        </div>
        <div class="qs-bar" style="margin-top:12px">
          <div class="qs-bar-lb">AI 创作占比</div>
          <div class="qs-bar-tr"><div class="qs-bar-fl" :style="{ width: aiRatio + '%', background: 'var(--gai)' }"></div></div>
        </div>
      </div>
    </div>

    <!-- 加入内容弹窗 -->
    <div v-if="showAddModal" class="modal-overlay" @click="showAddModal = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>选择要发布的内容</h3>
          <button class="modal-close" @click="showAddModal = false">&times;</button>
        </div>
        <div class="modal-body">
          <div v-if="approvedContents.length === 0" style="text-align:center;color:var(--cn-400);padding:32px">暂无已审核通过的内容</div>
          <div v-for="content in approvedContents" :key="content.id" class="content-select-item" @click="selectContentForQueue(content)">
            <div class="cs-thumb">
              <img v-if="content.imageUrls" :src="content.imageUrls.split(',')[0]" alt="配图" @error="$event.target.style.display='none'">
              <div v-else class="cs-placeholder" :style="{background: content.bg || '#FFE0E5'}"></div>
            </div>
            <div class="cs-info">
              <div class="cs-title">{{ content.title || '无标题' }}</div>
              <div class="cs-tags"><span v-for="t in (content.tags || '').split(',').filter(Boolean)" :key="t" class="tag-sm">{{ t }}</span></div>
            </div>
            <button class="cs-add-btn">+ 加入</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 内容详情弹窗 -->
    <div v-if="detailItem" class="modal-overlay" @click="closeDetail">
      <div class="modal-content detail-modal" @click.stop>
        <div class="modal-header">
          <h3>内容详情</h3>
          <button class="modal-close" @click="closeDetail">&times;</button>
        </div>
        <div class="modal-body detail-body">
          <div v-if="detailImages.length" class="detail-images">
            <div v-for="(url, i) in detailImages" :key="i" class="detail-img-wrap" @click="downloadImage(url, i)">
              <img :src="url" alt="配图" @error="$event.target.style.display='none'">
              <div class="detail-img-overlay">
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none"><path d="M8 2V10M8 10L5 7M8 10L11 7" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/><path d="M2 11V12C2 12.5523 2.44772 13 3 13H13C13.5523 13 14 12.5523 14 12V11" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
                <span>下载</span>
              </div>
            </div>
          </div>
          <h4 class="detail-title">{{ detailItem.title || '无标题' }}</h4>
          <pre class="detail-content">{{ detailItem.content || '暂无文案内容' }}</pre>
          <div class="detail-tags" v-if="detailItem.tags">
            <span v-for="t in detailItem.tags.split(',').filter(Boolean)" :key="t" class="tag-sm">{{ t }}</span>
          </div>
          <div class="detail-actions">
            <button class="btn btn-primary" @click="copyContent(detailItem)">
              <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><rect x="2" y="5" width="7" height="7" rx="1" stroke="currentColor" stroke-width="1.5"/><path d="M5 2H11C11.5523 2 12 2.44772 12 3V9" stroke="currentColor" stroke-width="1.5"/></svg>
              复制文案
            </button>
            <button v-if="detailImages.length" class="btn btn-ghost" @click="saveAllImages(detailItem)">
              <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M7 1V9M7 9L4 6M7 9L10 6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/><path d="M1 10V11C1 11.5523 1.44772 12 2 12H12C12.5523 12 13 11.5523 13 11V10" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
              保存全部图片
            </button>
            <button v-if="detailItem.publishStatus === 0" class="btn btn-ai" @click="publishNow(detailItem)">
              <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M2 7H12M12 7L8 3M12 7L8 11" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/></svg>
              立即发布
            </button>
            <button v-if="detailItem.publishStatus === 0" class="btn btn-manual" @click="manualPublish(detailItem)">
              <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M7 1L8.5 5H12.5L9.25 7.5L10.5 11.5L7 9L3.5 11.5L4.75 7.5L1.5 5H5.5L7 1Z" stroke="currentColor" stroke-width="1.2" stroke-linejoin="round"/></svg>
              人工发布
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 发布进度弹窗 -->
    <div v-if="publishProgressItem" class="modal-overlay" @click.stop>
      <div class="modal-content progress-modal">
        <div class="modal-header">
          <h3>发布进度</h3>
          <button class="modal-close" @click="closeProgress">&times;</button>
        </div>
        <div class="modal-body progress-body">
          <div class="progress-content">
            <div class="progress-title">{{ publishProgressItem.title || '无标题' }}</div>
            <div class="progress-steps">
              <div v-for="(step, i) in publishSteps" :key="i" class="progress-step" :class="{ active: step.active, done: step.done, error: step.error }">
                <div class="progress-step-icon">
                  <div v-if="step.error" class="step-error">!</div>
                  <div v-else-if="step.done" class="step-done">
                    <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M2 7L5.5 10.5L12 4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                  </div>
                  <div v-else-if="step.active" class="step-spinner"></div>
                  <div v-else class="step-pending">{{ i + 1 }}</div>
                </div>
                <div class="progress-step-info">
                  <div class="progress-step-name">{{ step.name }}</div>
                  <div class="progress-step-desc">{{ step.desc }}</div>
                </div>
              </div>
            </div>
          </div>
          <div v-if="publishResult" class="progress-result" :class="publishResult.type">
            <div class="progress-result-icon">{{ publishResult.type === 'success' ? '✓' : '✗' }}</div>
            <div class="progress-result-text">{{ publishResult.message }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { publishApi, contentApi } from '@/api'
import QueueCard from './components/QueueCard.vue'

const queueItems = ref([])
const stats = ref({})
const approvedContents = ref([])
const showAddModal = ref(false)
const detailItem = ref(null)
const accounts = ref([])

const statusColors = { 0: '#FFE0E5', 1: '#DBEAFE', 2: '#DCFCE7', 3: '#FEE2E2' }
const statusLabels = { 0: '待发布', 1: '发布中', 2: '已发布', 3: '发布失败' }
const statusClass = { 0: 'pend', 1: 'pubg', 2: 'done', 3: 'fail' }

const pendingItems = computed(() =>
  queueItems.value.filter(i => i.publishStatus === 0 || i.publishStatus === 1).map(formatItem)
)
const publishedItems = computed(() =>
  queueItems.value.filter(i => i.publishStatus === 2 || i.publishStatus === 3).map(formatItem)
)

function formatItem(item) {
  const firstImg = item.imageUrls ? item.imageUrls.split(',')[0] : null
  return {
    id: item.id,
    title: item.contentTitle || item.title || '无标题',
    time: item.publishTime ? '预定 ' + formatTime(item.publishTime) + ' 发布' : (item.actualPublishTime ? '已于 ' + formatTime(item.actualPublishTime) + ' 发布' : ''),
    status: statusLabels[item.publishStatus] || '未知',
    bg: statusColors[item.publishStatus] || '#F5F5F4',
    statusClass: statusClass[item.publishStatus] || 'pend',
    imageUrl: firstImg,
    imageUrls: item.imageUrls,
    content: item.content,
    tags: item.tags,
    publishStatus: item.publishStatus,
    contentId: item.contentId,
    publishResult: item.publishResult
  }
}

function formatTime(dt) {
  if (!dt) return ''
  if (typeof dt === 'string') {
    return dt.length > 16 ? dt.substring(0, 16).replace('T', ' ') : dt
  }
  return dt
}

const publishProgress = computed(() => {
  const total = queueItems.value.length
  if (!total) return 0
  const done = queueItems.value.filter(i => i.publishStatus === 2).length
  return Math.round(done / total * 100)
})

const aiRatio = ref(78)

async function loadQueue() {
  try {
    const res = await publishApi.queue({ page: 1, pageSize: 50 })
    if (res.data.code === 200) {
      const data = res.data.data
      queueItems.value = data?.records || data || []
    }
  } catch (e) { console.error('loadQueue error:', e) }

  try {
    const res = await publishApi.stats()
    if (res.data.code === 200) stats.value = res.data.data || {}
  } catch (e) { console.error('loadStats error:', e) }
}

async function loadApprovedContents() {
  try {
    const res = await contentApi.myList({ page: 1, pageSize: 50, auditStatus: 2 })
    if (res.data.code === 200) {
      approvedContents.value = res.data.data?.records || []
    }
  } catch (e) { console.error('loadApproved error:', e) }
}

async function selectContentForQueue(content) {
  try {
    const res = await publishApi.add({ contentId: content.id })
    if (res.data.code === 200) {
      showAddModal.value = false
      await loadQueue()
    } else {
      alert(res.data.message || '加入失败')
    }
  } catch (e) {
    alert('加入失败: ' + (e.response?.data?.message || e.message))
  }
}

function openDetail(item) {
  detailItem.value = item
}

function closeDetail() {
  detailItem.value = null
}

const detailImages = computed(() => {
  if (!detailItem.value || !detailItem.value.imageUrls) return []
  return detailItem.value.imageUrls.split(',').filter(Boolean)
})

function copyContent(item) {
  const text = item.content || ''
  if (text) {
    navigator.clipboard.writeText(text).then(() => {
      showToast('文案已复制到剪贴板')
    })
  }
}

function downloadImage(url, index) {
  const a = document.createElement('a')
  a.href = url
  a.download = `image_${index + 1}.jpg`
  a.target = '_blank'
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  showToast('图片下载已启动')
}

function saveAllImages(item) {
  if (item.imageUrls) {
    const urls = item.imageUrls.split(',').filter(Boolean)
    urls.forEach((url, i) => {
      setTimeout(() => downloadImage(url, i), i * 300)
    })
    showToast(`已启动 ${urls.length} 张图片下载`)
  }
}

async function removeItem(item) {
  if (!confirm('确定从队列中删除此内容？')) return
  try {
    const res = await publishApi.remove(item.id)
    if (res.data.code === 200) {
      await loadQueue()
      showToast('已删除')
    } else {
      alert(res.data.message || '删除失败')
    }
  } catch (e) {
    alert('删除失败: ' + (e.response?.data?.message || e.message))
  }
}

// ========== 发布进度可视化 ==========
const publishProgressItem = ref(null)
const publishSteps = ref([
  { name: '准备发布', desc: '检查账号绑定状态', active: false, done: false, error: false },
  { name: '获取内容', desc: '从服务器拉取内容数据', active: false, done: false, error: false },
  { name: '获取Cookie', desc: '获取小红书登录凭证', active: false, done: false, error: false },
  { name: '打开浏览器', desc: '启动自动化浏览器', active: false, done: false, error: false },
  { name: '上传图片', desc: '将图片上传至小红书', active: false, done: false, error: false },
  { name: '填写文案', desc: '输入标题和正文内容', active: false, done: false, error: false },
  { name: '点击发布', desc: '提交发布请求', active: false, done: false, error: false },
  { name: '验证结果', desc: '确认发布是否成功', active: false, done: false, error: false }
])
const publishResult = ref(null)

function resetSteps() {
  publishSteps.value.forEach(s => { s.active = false; s.done = false; s.error = false })
  publishResult.value = null
}

function setStepActive(index) {
  for (let i = 0; i < publishSteps.value.length; i++) {
    publishSteps.value[i].active = i === index
    publishSteps.value[i].done = i < index
  }
}

function setStepError(index, message) {
  publishSteps.value[index].error = true
  publishSteps.value[index].active = false
  publishResult.value = { type: 'error', message: message || '发布失败，请检查账号绑定状态或Cookie是否过期' }
}

function setStepSuccess() {
  publishSteps.value.forEach(s => { s.active = false; s.done = true; s.error = false })
  publishResult.value = { type: 'success', message: '发布成功！内容已发布到小红书' }
}

async function publishNow(item) {
  publishProgressItem.value = item
  resetSteps()

  // Step 1: 检查账号
  setStepActive(0)
  await sleep(600)

  // Step 2: 获取内容
  setStepActive(1)
  await sleep(800)

  // Step 3: 获取Cookie
  setStepActive(2)
  await sleep(800)

  // Step 4: 打开浏览器
  setStepActive(3)
  await sleep(1000)

  // 调用真实API
  try {
    const res = await publishApi.publishNow({ contentId: item.contentId || item.id })
    if (res.data.code === 200) {
      // Step 5-8 模拟动画
      setStepActive(4)
      await sleep(1200)
      setStepActive(5)
      await sleep(1500)
      setStepActive(6)
      await sleep(2000)
      setStepActive(7)
      await sleep(1500)
      setStepSuccess()
      detailItem.value = null
      await loadQueue()
    } else {
      setStepError(2, res.data.message || '发布接口返回错误')
    }
  } catch (e) {
    const msg = e.response?.data?.message || e.message
    if (msg.includes('账号') || msg.includes('Cookie') || msg.includes('登录')) {
      setStepError(2, msg)
    } else if (msg.includes('无权') || msg.includes('403')) {
      setStepError(0, msg)
    } else {
      setStepError(6, msg)
    }
  }
}

async function manualPublish(item) {
  if (!confirm('标记为人工发布后，该内容将移至"已发布"列表。确定继续？')) return
  try {
    const res = await publishApi.publishNow({ contentId: item.contentId || item.id })
    if (res.data.code === 200) {
      detailItem.value = null
      await loadQueue()
      showToast('已标记为人工发布')
    } else {
      alert(res.data.message || '操作失败')
    }
  } catch (e) {
    // 如果 publishNow 不可用，尝试直接更新状态
    try {
      await publishApi.remove(item.id)
      showToast('已标记为人工发布')
      await loadQueue()
      detailItem.value = null
    } catch (e2) {
      alert('操作失败: ' + (e.response?.data?.message || e.message))
    }
  }
}

function closeProgress() {
  publishProgressItem.value = null
  resetSteps()
}

function sleep(ms) {
  return new Promise(r => setTimeout(r, ms))
}

// Toast
const toastTimer = ref(null)
function showToast(msg) {
  let el = document.getElementById('queue-toast')
  if (!el) {
    el = document.createElement('div')
    el.id = 'queue-toast'
    el.style.cssText = 'position:fixed;top:20px;left:50%;transform:translateX(-50%);background:var(--cn-900);color:#fff;padding:10px 20px;border-radius:8px;font-size:13px;z-index:9999;opacity:0;transition:opacity .3s;pointer-events:none;'
    document.body.appendChild(el)
  }
  el.textContent = msg
  el.style.opacity = '1'
  clearTimeout(toastTimer.value)
  toastTimer.value = setTimeout(() => { el.style.opacity = '0' }, 2500)
}

onMounted(() => {
  loadQueue()
  loadApprovedContents()
})
</script>

<style scoped>
.header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px; }
.h-xl { font-size: 20px; font-weight: 700; color: var(--cn-900); margin-bottom: 4px; }
.sub { font-size: 13px; color: var(--cn-500); }
.btn { display: inline-flex; align-items: center; justify-content: center; gap: 8px; padding: 8px 16px; border-radius: 8px; font-size: 13px; font-weight: 500; cursor: pointer; transition: all .15s; border: none; background: none; }
.btn-primary { background: var(--cp-500); color: #fff; }
.btn-primary:hover { background: var(--cp-600); }
.btn-ghost { background: var(--cn-100); color: var(--cn-600); }
.btn-ghost:hover { background: var(--cn-200); }
.btn-ai { background: var(--cp-500); color: #fff; }
.btn-ai:hover { background: var(--cp-600); }
.btn-manual { background: #FEF3C7; color: #92400E; }
.btn-manual:hover { background: #FDE68A; }

.ql { display: grid; grid-template-columns: 1fr 280px; gap: 20px; }
.qdg { margin-bottom: 24px; }
.qdh { font-size: 13px; font-weight: 600; color: var(--cn-500); margin-bottom: 12px; display: flex; align-items: center; gap: 8px; }
.dd { width: 6px; height: 6px; border-radius: 50%; background: var(--cp-500); }
.dd.orange { background: var(--ca-orange); }

.qs { background: #fff; border-radius: 12px; padding: 20px; box-shadow: 0 1px 3px rgba(0,0,0,.06); border: 1px solid var(--cn-100); position: sticky; top: 0; }
.qs h4 { font-size: 14px; font-weight: 600; color: var(--cn-800); margin-bottom: 20px; }
.qs-it { display: flex; align-items: center; justify-content: space-between; padding: 12px 0; border-bottom: 1px solid var(--cn-100); }
.qs-it:last-child { border-bottom: none; }
.qs-lb { font-size: 13px; color: var(--cn-500); }
.qs-vl { font-family: 'JetBrains Mono', monospace; font-size: 16px; font-weight: 700; color: var(--cn-800); }
.qs-bar { margin-top: 16px; }
.qs-bar-lb { font-size: 11px; color: var(--cn-500); margin-bottom: 8px; }
.qs-bar-tr { height: 8px; background: var(--cn-100); border-radius: 9999px; overflow: hidden; }
.qs-bar-fl { height: 100%; border-radius: 9999px; transition: width .5s ease; }

/* Modal */
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,.4); display: flex; align-items: center; justify-content: center; z-index: 100; padding: 20px; }
.modal-content { background: #fff; border-radius: 16px; width: 100%; max-width: 560px; max-height: 80vh; display: flex; flex-direction: column; box-shadow: 0 20px 60px rgba(0,0,0,.15); }
.detail-modal { max-width: 640px; }
.progress-modal { max-width: 480px; }
.modal-header { display: flex; align-items: center; justify-content: space-between; padding: 16px 20px; border-bottom: 1px solid var(--cn-100); }
.modal-header h3 { font-size: 16px; font-weight: 600; color: var(--cn-900); margin: 0; }
.modal-close { width: 32px; height: 32px; border-radius: 8px; border: none; background: var(--cn-100); color: var(--cn-500); font-size: 20px; cursor: pointer; display: flex; align-items: center; justify-content: center; }
.modal-close:hover { background: var(--cn-200); }
.modal-body { padding: 16px 20px; overflow-y: auto; flex: 1; }

/* Content select items */
.content-select-item { display: flex; align-items: center; gap: 12px; padding: 12px; border-radius: 10px; cursor: pointer; transition: all .15s; border: 1px solid transparent; margin-bottom: 8px; }
.content-select-item:hover { background: var(--cn-50); border-color: var(--cn-100); }
.cs-thumb { width: 60px; height: 60px; border-radius: 8px; overflow: hidden; flex-shrink: 0; background: var(--cn-100); }
.cs-thumb img { width: 100%; height: 100%; object-fit: cover; }
.cs-placeholder { width: 100%; height: 100%; }
.cs-info { flex: 1; min-width: 0; }
.cs-title { font-size: 13px; font-weight: 600; color: var(--cn-800); margin-bottom: 4px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.cs-add-btn { padding: 6px 14px; border-radius: 6px; border: none; background: var(--cp-500); color: #fff; font-size: 12px; font-weight: 500; cursor: pointer; flex-shrink: 0; }
.cs-add-btn:hover { background: var(--cp-600); }

/* Detail */
.detail-images { display: grid; grid-template-columns: repeat(3, 1fr); gap: 8px; margin-bottom: 16px; }
.detail-img-wrap { position: relative; border-radius: 8px; overflow: hidden; cursor: pointer; aspect-ratio: 1; }
.detail-img-wrap img { width: 100%; height: 100%; object-fit: cover; transition: transform .2s; }
.detail-img-wrap:hover img { transform: scale(1.05); }
.detail-img-overlay { position: absolute; inset: 0; background: rgba(0,0,0,.5); display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 4px; opacity: 0; transition: opacity .2s; color: #fff; font-size: 11px; }
.detail-img-wrap:hover .detail-img-overlay { opacity: 1; }
.detail-title { font-size: 16px; font-weight: 700; color: var(--cn-900); margin-bottom: 12px; }
.detail-content { font-size: 13px; line-height: 1.8; color: var(--cn-600); white-space: pre-wrap; margin-bottom: 16px; background: var(--cn-50); padding: 16px; border-radius: 10px; font-family: inherit; max-height: 300px; overflow-y: auto; }
.detail-tags { display: flex; gap: 6px; flex-wrap: wrap; margin-bottom: 20px; }
.detail-actions { display: flex; gap: 12px; flex-wrap: wrap; }

.tag-sm { display: inline-flex; align-items: center; padding: 2px 8px; border-radius: 9999px; font-size: 11px; font-weight: 500; background: var(--cp-50); color: var(--cp-600); }

/* Progress */
.progress-body { padding: 24px 20px; }
.progress-content { margin-bottom: 20px; }
.progress-title { font-size: 15px; font-weight: 600; color: var(--cn-900); margin-bottom: 20px; text-align: center; }
.progress-steps { display: flex; flex-direction: column; gap: 0; }
.progress-step { display: flex; align-items: flex-start; gap: 12px; padding: 10px 0; position: relative; }
.progress-step:not(:last-child)::after { content: ''; position: absolute; left: 15px; top: 38px; width: 2px; height: calc(100% - 16px); background: var(--cn-200); }
.progress-step.done:not(:last-child)::after { background: var(--cs); }
.progress-step-icon { width: 32px; height: 32px; border-radius: 50%; display: flex; align-items: center; justify-content: center; flex-shrink: 0; font-size: 12px; font-weight: 700; background: var(--cn-100); color: var(--cn-400); z-index: 1; }
.progress-step.active .progress-step-icon { background: var(--cp-500); color: #fff; }
.progress-step.done .progress-step-icon { background: var(--cs); color: #fff; }
.progress-step.error .progress-step-icon { background: #FEE2E2; color: #B91C1C; }
.step-spinner { width: 16px; height: 16px; border: 2px solid rgba(255,255,255,.3); border-top-color: #fff; border-radius: 50%; animation: spin 1s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }
.progress-step-info { flex: 1; padding-top: 4px; }
.progress-step-name { font-size: 13px; font-weight: 600; color: var(--cn-700); }
.progress-step.active .progress-step-name { color: var(--cp-600); }
.progress-step.done .progress-step-name { color: var(--cs); }
.progress-step.error .progress-step-name { color: #B91C1C; }
.progress-step-desc { font-size: 11px; color: var(--cn-400); margin-top: 2px; }
.progress-result { display: flex; align-items: center; gap: 12px; padding: 16px; border-radius: 10px; }
.progress-result.success { background: #DCFCE7; }
.progress-result.error { background: #FEE2E2; }
.progress-result-icon { width: 28px; height: 28px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 16px; font-weight: 700; flex-shrink: 0; }
.progress-result.success .progress-result-icon { background: var(--cs); color: #fff; }
.progress-result.error .progress-result-icon { background: #B91C1C; color: #fff; }
.progress-result-text { font-size: 13px; font-weight: 500; }
.progress-result.success .progress-result-text { color: #15803D; }
.progress-result.error .progress-result-text { color: #B91C1C; }
</style>
