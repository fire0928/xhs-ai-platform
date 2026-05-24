<template>
  <div class="ai-create-page">
    <!-- 步骤指示器 -->
    <div class="step-indicator-wrapper">
      <div class="step-indicator">
        <StepItem :num="1" label="选择文案AI" :status="stepStatus(1)" />
        <div class="step-line" :class="{ active: step > 1, done: step > 1 }"></div>
        <StepItem :num="2" label="AI 生成标题" :status="stepStatus(2)" />
        <div class="step-line" :class="{ active: step > 2, done: step > 2 }"></div>
        <StepItem :num="3" label="AI 生成文案" :status="stepStatus(3)" />
        <div class="step-line" :class="{ active: step > 3, done: step > 3 }"></div>
        <StepItem :num="4" label="选择图片AI" :status="stepStatus(4)" />
        <div class="step-line" :class="{ active: step > 4, done: step > 4 }"></div>
        <StepItem :num="5" label="AI 生成提示词" :status="stepStatus(5)" />
        <div class="step-line" :class="{ active: step > 5, done: step > 5 }"></div>
        <StepItem :num="6" label="AI 生成图片" :status="stepStatus(6)" />
        <div class="step-line" :class="{ active: step > 6, done: step > 6 }"></div>
        <StepItem :num="7" label="确认入队" :status="stepStatus(7)" />
      </div>
    </div>

    <!-- ===== Step 1: 选择文案 AI + 填写主题 ===== -->
    <div v-if="step === 1" class="step-body">
      <div class="step-header">选择文案创作 Agent 并填写内容主题</div>
      <div class="agent-section">
        <div class="agent-section-title"><svg width="16" height="16" viewBox="0 0 16 16" fill="none"><path d="M3 4.5h10M3 8h7M3 11.5h5" stroke="var(--cp-500)" stroke-width="1.5" stroke-linecap="round"/></svg>文案 Agent</div>
        <div v-if="loadingAgents" class="empty-state">加载中...</div>
        <div v-else-if="textAgents.length === 0" class="empty-state">暂无文案 Agent</div>
        <div v-else class="agent-grid">
          <div v-for="agent in textAgents" :key="agent.id" :class="['agent-card', { selected: selectedAgent?.id === agent.id }]" @click="selectedAgent = agent">
            <div class="agent-icon" :style="{ background: agentBg(agent) }"><svg width="22" height="22" viewBox="0 0 22 22" fill="none"><path d="M11 3L13 8H18L14 11L15.5 16L11 13L6.5 16L8 11L4 8H9L11 3Z" fill="currentColor" opacity=".7"/></svg></div>
            <div class="agent-name">{{ agent.name }}</div><div class="agent-desc">{{ agent.description }}</div>
            <div class="agent-meta"><span class="tag" :style="{ background: tagBg(agent) }">{{ agent.style || '通用' }}</span></div>
          </div>
        </div>
      </div>
      <!-- 主题输入 -->
      <div class="input-card" v-if="selectedAgent">
        <label class="input-label">创作主题 / 关键词 *</label>
        <textarea v-model="userPrompt" placeholder="例如：春日野餐全攻略、周末去哪儿玩、一人食晚餐推荐..." rows="3" class="input-area" @input="autoResize"></textarea>
        <div class="input-hint">描述你想创作的内容主题，越具体 AI 生成的标题越精准</div>
      </div>
      <div class="step-action">
        <span></span>
        <button class="btn btn-primary" :disabled="!selectedAgent || !userPrompt.trim()" @click="goStep(2)">下一步 · AI 生成标题 <svg width="16" height="16" viewBox="0 0 16 16" fill="none"><path d="M5 3l5 5-5 5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg></button>
      </div>
    </div>

    <!-- ===== Step 2: AI 生成标题 ===== -->
    <div v-if="step === 2" class="step-body">
      <div class="selected-badge"><svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M3 7L6 10L11 4" stroke="var(--cs)" stroke-width="1.5" stroke-linecap="round"/></svg><span>文案：{{ selectedAgent?.name }} · 主题：{{ userPrompt }}</span><button class="btn btn-ghost btn-xs" @click="goStep(1)">修改</button></div>
      <!-- 加载中 -->
      <div v-if="generatingTitles" class="ai-zone"><div class="ai-zone-inner loading-card"><div class="shimmer-bar" style="width:80px;height:4px;margin-bottom:12px"></div><span>AI 正在创作标题...</span></div></div>
      <!-- 标题列表 -->
      <div v-else-if="titles.length > 0" class="ai-zone"><div class="ai-zone-inner">
        <div class="zone-header"><svg width="18" height="18" viewBox="0 0 18 18" fill="none"><path d="M9 1L10.5 5H15L11.5 7.5L13 12L9 9L5 12L6.5 7.5L3 5H7.5L9 1Z" fill="#8B5CF6"/></svg><span class="zone-title">AI 已生成 {{ titles.length }} 个标题候选</span><button class="btn btn-ghost btn-sm" :disabled="generatingTitles" @click="generateTitles"><svg width="10" height="10" viewBox="0 0 10 10" fill="none"><path d="M5 1V9M1 5H9" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>重新生成</button></div>
        <div v-for="(title, i) in titles" :key="i" :class="['title-card', { selected: selectedTitle === i }]" @click="selectTitle(i)"><div class="tc-num">{{ i+1 }}</div><div class="tc-text">{{ title }}</div><button :class="['tc-btn', { active: selectedTitle === i }]">{{ selectedTitle===i?'已选用':'选用' }}</button></div>
      </div></div>
      <!-- 失败 -->
      <div v-else-if="titleError" class="title-error-box"><svg width="16" height="16" viewBox="0 0 16 16" fill="none"><circle cx="8" cy="8" r="7" stroke="#B91C1C" stroke-width="1.5"/><path d="M8 5v3.5M8 11v.5" stroke="#B91C1C" stroke-width="1.5" stroke-linecap="round"/></svg><span>{{ titleError }}</span><button class="btn btn-outline btn-sm" @click="generateTitles">重试</button></div>
      <div v-if="!generatingTitles && titles.length > 0" class="step-action">
        <button class="btn btn-ghost" @click="goStep(1)">上一步</button>
        <button class="btn btn-ai-main" :disabled="selectedTitle===null" @click="goStep(3)">确认标题 · 下一步</button>
      </div>
    </div>

    <!-- ===== Step 3: AI 生成文案 ===== -->
    <div v-if="step === 3" class="step-body">
      <div class="selected-badge"><svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M3 7L6 10L11 4" stroke="var(--cs)" stroke-width="1.5" stroke-linecap="round"/></svg><span>已选标题：{{ titles[selectedTitle] }}</span><button class="btn btn-ghost btn-xs" @click="goStep(2)">修改</button></div>
      <!-- 加载 -->
      <div v-if="generatingContent" class="ai-zone"><div class="ai-zone-inner loading-card"><div class="shimmer-bar" style="width:60px;height:4px;margin-bottom:12px"></div><span>AI 正在生成文案...</span></div></div>
      <!-- 文案结果 -->
      <div v-else-if="generatedContent" class="ai-zone"><div class="ai-zone-inner">
        <div class="zone-header"><svg width="18" height="18" viewBox="0 0 18 18" fill="none"><path d="M9 1L10.5 5H15L11.5 7.5L13 12L9 9L5 12L6.5 7.5L3 5H7.5L9 1Z" fill="#8B5CF6"/></svg><span class="zone-title">AI 生成的文案</span></div>
        <div class="content-text-wrap"><div class="content-body" v-html="renderedContent"></div></div>
        <div class="action-bar">
          <button class="btn btn-outline" @click="goStep(2)">返回修改</button>
          <button class="btn btn-outline" :disabled="generatingContent" @click="generateContent">重新生成</button>
          <button class="btn btn-ai-main" @click="doCopyContent">{{ copied?'已复制':'复制文案' }}</button>
        </div>
      </div></div>
      <div v-else-if="contentError" class="ai-zone"><div class="ai-zone-inner error-card"><svg width="20" height="20" viewBox="0 0 20 20" fill="none"><circle cx="10" cy="10" r="9" stroke="var(--ce)" stroke-width="1.5"/><path d="M10 6v5M10 13.5v.5" stroke="var(--ce)" stroke-width="1.5" stroke-linecap="round"/></svg><span>{{ contentError }}</span><button class="btn btn-outline btn-sm" @click="generateContent">重试</button></div></div>
      <div v-if="!generatingContent && generatedContent" class="step-action">
        <button class="btn btn-ghost" @click="goStep(2)">上一步</button>
        <button class="btn btn-ai-main" @click="goStep(4)">下一步 · 选择图片 AI</button>
      </div>
    </div>

    <!-- ===== Step 4: 选择图片 AI + 生成提示语 ===== -->
    <div v-if="step === 4" class="step-body">
      <div class="step-header">选择图片生成 Agent，AI 将根据文案自动生成图片提示词</div>
      <div class="agent-section">
        <div class="agent-section-title"><svg width="16" height="16" viewBox="0 0 16 16" fill="none"><rect x="2.5" y="3.5" width="11" height="9" rx="1.5" stroke="#8B5CF6" stroke-width="1.5"/><circle cx="6" cy="7" r="1" fill="#8B5CF6"/><path d="M2.5 11.5l3-2.5 2 1.5 3-3 3 4" stroke="#8B5CF6" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/></svg>图片 Agent</div>
        <div v-if="loadingImageAgents" class="empty-state">加载中...</div>
        <div v-else-if="imageAgents.length === 0" class="empty-state">暂无图片 Agent</div>
        <div v-else class="agent-grid">
          <div v-for="agent in imageAgents" :key="agent.id" :class="['agent-card', { selected: selectedImageAgent?.id === agent.id }]" @click="selectedImageAgent = agent">
            <div class="agent-icon" style="background:#EDE9FE;color:#8B5CF6"><svg width="22" height="22" viewBox="0 0 22 22" fill="none"><rect x="4" y="5" width="14" height="12" rx="2" stroke="currentColor" stroke-width="1.5"/><circle cx="8" cy="9" r="1.5" fill="currentColor"/><path d="M4 15l4-3 3 2 4-4 3 5" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/></svg></div>
            <div class="agent-name">{{ agent.name }}</div><div class="agent-desc">{{ agent.description }}</div>
            <div class="agent-meta"><span class="tag" style="background:#EDE9FE;color:#8B5CF6">图片</span></div>
          </div>
        </div>
      </div>
      <div class="step-action">
        <button class="btn btn-ghost" @click="goStep(3)">上一步</button>
        <button class="btn btn-ai-main" :disabled="!selectedImageAgent" @click="goStep(5)">确认 · AI 生成图片提示词</button>
      </div>
    </div>

    <!-- ===== Step 5: AI 生成图片提示词（可编辑） ===== -->
    <div v-if="step === 5" class="step-body">
      <div class="selected-badge"><svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M3 7L6 10L11 4" stroke="var(--cs)" stroke-width="1.5" stroke-linecap="round"/></svg><span>图片 Agent：{{ selectedImageAgent?.name }} · 文案标题：{{ titles[selectedTitle] }}</span></div>
      <div v-if="generatingImagePrompt" class="ai-zone"><div class="ai-zone-inner loading-card"><div class="shimmer-bar" style="width:60px;height:4px;margin-bottom:12px"></div><span>AI 正在生成图片提示词...</span></div></div>
      <div v-else-if="imagePromptText" class="ai-zone"><div class="ai-zone-inner">
        <div class="zone-header"><svg width="18" height="18" viewBox="0 0 18 18" fill="none"><path d="M9 1L10.5 5H15L11.5 7.5L13 12L9 9L5 12L6.5 7.5L3 5H7.5L9 1Z" fill="#8B5CF6"/></svg><span class="zone-title">AI 生成的图片提示词</span><button class="btn btn-ghost btn-sm" :disabled="generatingImagePrompt" @click="generateImagePrompt">重新生成</button></div>
        <div class="input-card" style="margin-bottom:0">
          <label class="input-label">图片提示词（可编辑）</label>
          <textarea v-model="imagePromptText" rows="6" class="input-area" @input="autoResize" placeholder="AI 将根据文案自动生成图片提示词，您可以在此修改..."></textarea>
          <div class="input-hint">提示词越详细，生成的图片越符合预期。支持中英文。</div>
        </div>
      </div></div>
      <div v-else-if="imagePromptError" class="ai-zone"><div class="ai-zone-inner error-card"><svg width="20" height="20" viewBox="0 0 20 20" fill="none"><circle cx="10" cy="10" r="9" stroke="var(--ce)" stroke-width="1.5"/><path d="M10 6v5M10 13.5v.5" stroke="var(--ce)" stroke-width="1.5" stroke-linecap="round"/></svg><span>{{ imagePromptError }}</span><button class="btn btn-outline btn-sm" @click="generateImagePrompt">重试</button></div></div>
      <div v-if="!generatingImagePrompt && imagePromptText" class="step-action">
        <button class="btn btn-ghost" @click="goStep(4)">上一步</button>
        <button class="btn btn-ai-main" :disabled="!imagePromptText.trim()" @click="goStep(6)">下一步 · 生成图片</button>
      </div>
    </div>

    <!-- ===== Step 6: AI 生成图片 ===== -->
    <div v-if="step === 6" class="step-body">
      <div class="selected-badge"><svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M3 7L6 10L11 4" stroke="var(--cs)" stroke-width="1.5" stroke-linecap="round"/></svg><span>图片 Agent：{{ selectedImageAgent?.name }} · 文案标题：{{ titles[selectedTitle] }}</span></div>
      <div v-if="generatingImages" class="ai-zone"><div class="ai-zone-inner loading-card"><div class="shimmer-bar" style="width:60px;height:4px;margin-bottom:12px"></div><span>AI 正在生成图片...</span></div></div>
      <div v-else-if="imagePrompts.length > 0" class="ai-zone"><div class="ai-zone-inner">
        <div class="zone-header"><svg width="18" height="18" viewBox="0 0 18 18" fill="none"><rect x="3" y="4" width="12" height="10" rx="1.5" stroke="#8B5CF6" stroke-width="1.5"/><circle cx="7" cy="8" r="1.5" fill="#8B5CF6"/><path d="M3 13l4-3 3 2 4-4 3 5" stroke="#8B5CF6" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/></svg><span class="zone-title">AI 生成的图片（{{ imagePrompts.length }} 张）</span><button class="btn btn-ghost btn-sm" :disabled="generatingImages" @click="generateImages">重新生成</button></div>
        <div class="img-grid">
          <div v-for="(url, i) in imagePrompts" :key="i" class="img-card">
            <img :src="url" :alt="'生成图片 '+(i+1)" loading="lazy" />
          </div>
        </div>
        <div class="action-bar" style="margin-top:16px">
          <button class="btn btn-outline" @click="goStep(5)">返回修改提示词</button>
        </div>
      </div></div>
      <div v-else-if="imageError" class="ai-zone"><div class="ai-zone-inner error-card"><svg width="20" height="20" viewBox="0 0 20 20" fill="none"><circle cx="10" cy="10" r="9" stroke="var(--ce)" stroke-width="1.5"/><path d="M10 6v5M10 13.5v.5" stroke="var(--ce)" stroke-width="1.5" stroke-linecap="round"/></svg><span>{{ imageError }}</span><button class="btn btn-outline btn-sm" @click="generateImages">重试</button></div></div>
      <div v-if="!generatingImages && imagePrompts.length > 0" class="step-action">
        <button class="btn btn-ghost" @click="goStep(5)">上一步</button>
        <button class="btn btn-ai-main" @click="goStep(7)">下一步 · 确认入队</button>
      </div>
    </div>

    <!-- ===== Step 7: 确认文案和图片，加入队列 / 内容库 ===== -->
    <div v-if="step === 7" class="step-body">
      <div class="step-header">请确认以下内容</div>
      <div class="final-review">
        <div class="final-section">
          <h4>文案 Agent</h4><p>{{ selectedAgent?.name }}</p>
        </div>
        <div class="final-section">
          <h4>选中标题</h4><p class="final-title">{{ titles[selectedTitle] }}</p>
        </div>
        <div class="final-section">
          <h4>文案内容</h4><div class="final-content" v-html="renderedContent"></div>
        </div>
        <div class="final-section" v-if="selectedImageAgent">
          <h4>图片 Agent</h4><p>{{ selectedImageAgent?.name }}</p>
        </div>
        <div class="final-section" v-if="imagePrompts.length > 0">
          <h4>生成图片</h4>
          <div class="img-grid" style="margin-top:10px">
            <div v-for="(url, i) in imagePrompts" :key="i" class="img-card">
              <img :src="url" :alt="'图片 '+(i+1)" loading="lazy" />
            </div>
          </div>
        </div>
      </div>
      <div class="step-action">
        <button class="btn btn-ghost" @click="goStep(6)">上一步</button>
        <div class="flex aic g3">
          <button class="btn btn-outline" @click="doCopyContent">复制文案</button>
          <button class="btn btn-outline" :disabled="saving" @click="submitToContentLib">{{ saving?'保存中...':'加入内容库' }}</button>
          <button class="btn btn-ai-main" :disabled="saving" @click="submitToQueue">{{ saving?'提交中...':'确认 · 加入发布队列' }}</button>
        </div>
      </div>
    </div>

    <div v-if="toast.show" :class="['toast', 'toast-'+toast.type]">{{ toast.msg }}</div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import StepItem from './components/StepItem.vue'
import { aiApi, publishApi, contentApi } from '@/api'

const step = ref(1)
const textAgents = ref([])
const imageAgents = ref([])
const loadingAgents = ref(false)
const loadingImageAgents = ref(false)
const selectedAgent = ref(null)
const selectedImageAgent = ref(null)
const userPrompt = ref('')
const titles = ref([])
const selectedTitle = ref(null)
const generatingTitles = ref(false)
const titleError = ref('')
const generatedContent = ref('')
const generatingContent = ref(false)
const contentError = ref('')
const copied = ref(false)
const imagePrompts = ref([])
const generatingImages = ref(false)
const imageError = ref('')
const imagePromptText = ref('')
const generatingImagePrompt = ref(false)
const imagePromptError = ref('')
const saving = ref(false)
const toast = ref({ show: false, msg: '', type: 'info' })
const draftId = ref(null) // 当前草稿ID

const stepStatus = (s) => s < step.value ? 'done' : s === step.value ? 'active' : 'pending'
const agentBg = (agent) => { const c=['var(--cp-50)','var(--ca-orange-l)','#DCFCE7','#EDE9FE']; return c[(agent.id||1)%4] }
const tagBg = (agent) => { const c=['#FFE0E5','#FEF3C7','#DCFCE7','#EDE9FE']; return c[(agent.id||1)%4] }
const renderedContent = computed(() => generatedContent.value ? generatedContent.value.replace(/\n/g,'<br>').replace(/#(\S+)/g,'<span style="color:var(--cp-500)">#$1</span>') : '')

const loadAgents = async (retry = 0) => {
  loadingAgents.value = true; loadingImageAgents.value = true
  try {
    const [textRes, imgRes] = await Promise.all([aiApi.getAgents(), aiApi.getImageAgents()])
    if (textRes.data.code === 200) textAgents.value = textRes.data.data || []
    else if (retry < 2) { setTimeout(() => loadAgents(retry + 1), 1000); return }
    if (imgRes.data.code === 200) imageAgents.value = imgRes.data.data || []
    else if (retry < 2) { setTimeout(() => loadAgents(retry + 1), 1000); return }
  } catch (e) {
    if (retry < 2) {
      setTimeout(() => loadAgents(retry + 1), 1000)
      return
    }
    showToast('加载 Agent 失败: ' + (e.response?.data?.message || e.message || '网络错误'), 'error')
  } finally { loadingAgents.value = false; loadingImageAgents.value = false }
}

// 自动保存草稿到内容库
const autoSaveDraft = async () => {
  if (!selectedAgent.value || !titles.value[selectedTitle.value]) return
  try {
    const data = {
      title: titles.value[selectedTitle.value],
      content: generatedContent.value || '',
      agentId: selectedAgent.value?.id,
      imageUrls: imagePrompts.value.join(','),
      tags: ''
    }
    if (draftId.value) {
      await contentApi.update(draftId.value, data)
    } else {
      const res = await contentApi.create(data)
      if (res.data.code === 200 && res.data.data?.id) {
        draftId.value = res.data.data.id
      }
    }
  } catch { /* 静默保存，不打扰用户 */ }
}

// 从创作记录恢复草稿
const restoreFromDraft = () => {
  const draft = sessionStorage.getItem('creationDraft')
  if (!draft) return false
  try {
    const d = JSON.parse(draft)
    if (d.fromRecord && d.agentId) {
      const agent = textAgents.value.find(a => a.id === d.agentId)
      if (agent) selectedAgent.value = agent
      if (d.title) { titles.value = [d.title]; selectedTitle.value = 0 }
      if (d.content) generatedContent.value = d.content
      if (d.imageUrls && d.imageUrls.length > 0) imagePrompts.value = d.imageUrls
      if (d.id) draftId.value = d.id
      // 恢复到文案生成完成后的步骤
      if (generatedContent.value) step.value = 4
      sessionStorage.removeItem('creationDraft')
      showToast('已恢复创作记录', 'success')
      return true
    }
  } catch { }
  return false
}

const goStep = async (s) => {
  step.value = s
  // 进入 Step 2 时自动生成标题
  if (s === 2 && titles.value.length === 0) await generateTitles()
  // 进入 Step 3 时自动生成文案
  if (s === 3 && !generatedContent.value) await generateContent()
  // 进入 Step 5 时自动生成图片提示词
  if (s === 5 && !imagePromptText.value && selectedImageAgent.value) await generateImagePrompt()
  // 进入 Step 6 时自动生成图片
  if (s === 6 && imagePrompts.value.length === 0 && selectedImageAgent.value) await generateImages()
  // 离开关键步骤时自动保存草稿
  if ((s === 3 || s === 4 || s === 6 || s === 7) && titles.value.length > 0) {
    autoSaveDraft()
  }
}

const generateTitles = async () => {
  if (!selectedAgent.value || !userPrompt.value.trim() || generatingTitles.value) return
  generatingTitles.value = true; titleError.value = ''; titles.value = []; selectedTitle.value = null
  try {
    const res = await aiApi.generateTitles({ agentId: selectedAgent.value.id, prompt: userPrompt.value, count: 3 })
    if (res.data.code === 200 && res.data.data?.length > 0) { titles.value = res.data.data; showToast('标题生成成功', 'success') }
    else { titleError.value = res.data.message || '生成失败' }
  } catch (e) { titleError.value = e.response?.data?.message || 'AI 服务暂时不可用' }
  finally { generatingTitles.value = false }
}

const selectTitle = (i) => { selectedTitle.value = i }

const generateContent = async () => {
  if (!selectedAgent.value || selectedTitle.value === null || generatingContent.value) return
  generatingContent.value = true; contentError.value = ''; generatedContent.value = ''
  try {
    const res = await aiApi.generateContent({ agentId: selectedAgent.value.id.toString(), title: titles.value[selectedTitle.value], prompt: userPrompt.value })
    if (res.data.code === 200 && res.data.data) { generatedContent.value = res.data.data; showToast('文案生成成功', 'success') }
    else { contentError.value = res.data.message || '生成文案失败' }
  } catch (e) { contentError.value = e.response?.data?.message || 'AI 服务暂时不可用' }
  finally { generatingContent.value = false }
}

// 生成图片提示词（用默认文本AI）
const generateImagePrompt = async () => {
  if (!selectedAgent.value || generatingImagePrompt.value) return
  generatingImagePrompt.value = true; imagePromptError.value = ''; imagePromptText.value = ''
  try {
    const res = await aiApi.generateImagePrompt({
      agentId: selectedAgent.value.id,
      title: titles.value[selectedTitle.value],
      content: generatedContent.value
    })
    if (res.data.code === 200 && res.data.data) {
      imagePromptText.value = res.data.data.trim()
      showToast('提示词生成成功', 'success')
    } else {
      imagePromptError.value = res.data.message || '生成提示词失败'
    }
  } catch (e) {
    imagePromptError.value = e.response?.data?.message || 'AI 服务暂时不可用'
  } finally {
    generatingImagePrompt.value = false
  }
}

const generateImages = async () => {
  if (!selectedImageAgent.value || generatingImages.value) return
  generatingImages.value = true; imageError.value = ''; imagePrompts.value = []
  try {
    const prompt = imagePromptText.value.trim() || `${userPrompt.value}，标题：${titles.value[selectedTitle.value]}，文案摘要：${generatedContent.value?.substring(0,200)||''}`
    const res = await aiApi.generateImages({ imageAgentId: selectedImageAgent.value.id, prompt, count: 4 })
    if (res.data.code === 200 && res.data.data?.length > 0) { imagePrompts.value = res.data.data; showToast('图片生成成功', 'success') }
    else { imageError.value = res.data.message || '生成失败' }
  } catch (e) { imageError.value = e.response?.data?.message || 'AI 服务暂时不可用' }
  finally { generatingImages.value = false }
}

// 修复：使用兼容的复制方式
const doCopyContent = async () => {
  const text = generatedContent.value
  if (!text) return showToast('没有可复制的内容', 'error')
  try {
    if (navigator.clipboard && window.isSecureContext) {
      await navigator.clipboard.writeText(text)
    } else {
      // fallback: textarea 方式
      const ta = document.createElement('textarea')
      ta.value = text; ta.style.position = 'fixed'; ta.style.opacity = '0'
      document.body.appendChild(ta); ta.select()
      document.execCommand('copy'); document.body.removeChild(ta)
    }
    copied.value = true; showToast('文案已复制到剪贴板', 'success')
    setTimeout(() => copied.value = false, 2000)
  } catch { showToast('复制失败', 'error') }
}

// 提交到内容库（即提交审核）
const submitToContentLib = async () => {
  saving.value = true
  try {
    const data = {
      title: titles.value[selectedTitle.value],
      content: generatedContent.value,
      agentId: selectedAgent.value?.id,
      imageUrls: imagePrompts.value.join(','),
      tags: ''
    }
    const res = await contentApi.create(data)
    const contentId = res.data.data?.id
    if (!contentId) {
      showToast('创建内容失败', 'error')
      return
    }
    // 创建成功后立即提交审核
    await contentApi.submitAudit(contentId)
    showToast('已加入内容库，进入审核', 'success')
    setTimeout(() => { window.location.hash = '#/review' }, 1200)
  } catch (e) { showToast('保存失败: ' + (e.response?.data?.msg || e.message), 'error') }
  finally { saving.value = false }
}

// 提交到发布队列
const submitToQueue = async () => {
  saving.value = true
  try {
    // 先保存到内容库
    const contentData = {
      title: titles.value[selectedTitle.value],
      content: generatedContent.value,
      agentId: selectedAgent.value?.id,
      imageUrls: imagePrompts.value.join(','),
      tags: '',
      status: 'draft'
    }
    const createRes = await contentApi.create(contentData)
    const contentId = createRes.data.data?.id
    if (!contentId) {
      showToast('创建内容失败', 'error')
      return
    }
    // 提交审核（加入内容库）
    await contentApi.submitAudit(contentId)
    // 再加入发布队列
    await publishApi.add({ contentId })
    showToast('已加入发布队列', 'success')
    setTimeout(() => { window.location.hash = '#/queue' }, 1200)
  } catch (e) { showToast('提交失败: ' + (e.response?.data?.msg || e.message), 'error') }
  finally { saving.value = false }
}

const autoResize = (e) => { e.target.style.height='auto'; e.target.style.height=e.target.scrollHeight+'px' }
let toastTimer
const showToast = (msg, type='info') => { clearTimeout(toastTimer); toast.value={show:true,msg,type}; toastTimer=setTimeout(()=>toast.value.show=false,3000) }
onMounted(async () => {
  await loadAgents()
  // 尝试从创作记录恢复
  restoreFromDraft()
})
</script>

<style scoped>
.ai-create-page { max-width:960px; margin:0 auto }
.step-indicator-wrapper { background:#fff; border-radius:12px; padding:20px 24px; margin-bottom:32px; border:1px solid var(--cn-100); box-shadow:0 1px 3px rgba(0,0,0,.06) }
.step-indicator { display:flex; align-items:center; justify-content:center; gap:0; }
.step-line { width:24px; height:2px; background:var(--cn-200); transition:all .3s }
.step-line.active { background:var(--cp-300) }
.step-line.done { background:var(--cs) }
.step-body { animation:fadeIn .3s ease }
@keyframes fadeIn { from{opacity:0;transform:translateY(8px)} to{opacity:1;transform:translateY(0)} }
.step-header { display:flex;align-items:center;gap:8px;color:var(--cn-600);font-size:14px;margin-bottom:20px;padding:10px 14px;background:var(--cn-50);border-radius:8px;font-weight:500 }
.agent-section { margin-bottom:24px }
.agent-section-title { display:flex;align-items:center;gap:8px;font-size:14px;font-weight:600;color:var(--cn-700);margin-bottom:14px }
.empty-state { display:flex;flex-direction:column;align-items:center;gap:12px;padding:40px 20px;color:var(--cn-400);font-size:13px }
.agent-grid { display:grid;grid-template-columns:repeat(2,1fr);gap:16px }
.agent-card { background:#fff;border-radius:12px;padding:24px;border:2px solid transparent;text-align:left;transition:all .2s;cursor:pointer }
.agent-card:hover { border-color:var(--cn-200);box-shadow:0 2px 12px rgba(0,0,0,.06) }
.agent-card.selected { border-color:var(--cp-500);background:var(--cp-50) }
.agent-icon { width:44px;height:44px;border-radius:10px;display:flex;align-items:center;justify-content:center;margin-bottom:14px }
.agent-name { font-size:15px;font-weight:600;color:var(--cn-800);margin-bottom:6px }
.agent-desc { font-size:12px;color:var(--cn-500);margin-bottom:14px;line-height:1.6 }
.agent-meta { display:flex;gap:6px;flex-wrap:wrap }
.selected-badge { display:flex;align-items:center;gap:8px;margin-bottom:20px;padding:10px 14px;background:#F0FDF4;border:1px solid #BBF7D0;border-radius:8px;font-size:13px;color:var(--cn-700) }
.input-card { background:#fff;border:1px solid var(--cn-200);border-radius:10px;padding:20px;margin-bottom:16px }
.input-card:focus-within { border-color:var(--cp-400);box-shadow:0 0 0 3px rgba(254,44,85,.08) }
.input-label { display:block;font-size:13px;font-weight:600;color:var(--cn-700);margin-bottom:10px }
.input-area { width:100%;border:none;outline:none;font-size:13px;color:var(--cn-800);resize:none;line-height:1.6;background:transparent }
.input-area::placeholder { color:var(--cn-400) }
.input-hint { font-size:11px;color:var(--cn-400);margin-top:8px }
.title-error-box { display:flex;align-items:center;gap:8px;margin-bottom:16px;padding:12px 16px;background:#FEF2F2;border:1px solid #FECACA;border-radius:8px;font-size:12px;color:#B91C1C;flex-wrap:wrap }
.step-action { display:flex;align-items:center;justify-content:space-between;gap:12px;padding-top:4px }
.btn { display:inline-flex;align-items:center;justify-content:center;gap:8px;padding:10px 20px;border-radius:8px;font-size:13px;font-weight:500;transition:all .15s;cursor:pointer;border:none }
.btn:disabled { opacity:.5;cursor:not-allowed }
.btn svg { width:16px;height:16px;flex-shrink:0 }
.btn-ghost { color:var(--cn-500);background:transparent }
.btn-ghost:hover:not(:disabled) { background:var(--cn-100) }
.btn-outline { background:#fff;color:var(--cp-500);border:1px solid var(--cp-300) }
.btn-outline:hover:not(:disabled) { background:var(--cp-50) }
.btn-primary { background:var(--cp-500);color:#fff }
.btn-primary:hover:not(:disabled) { background:var(--cp-600) }
.btn-ai-main { background:var(--gai);color:#fff;min-width:180px;padding:12px 24px }
.btn-ai-main:hover:not(:disabled) { opacity:.92 }
.btn-sm { padding:5px 12px;font-size:11px }
.btn-xs { padding:2px 8px;font-size:11px }
.ai-zone { border-radius:12px;padding:1px;background:var(--gai);margin-bottom:24px }
.ai-zone-inner { background:#fff;border-radius:11px;padding:24px }
.zone-header { display:flex;align-items:center;gap:8px;margin-bottom:20px }
.zone-title { font-size:14px;font-weight:600;color:var(--cn-800) }
.loading-card { display:flex;flex-direction:column;align-items:center;padding:40px 20px }
.error-card { display:flex;flex-direction:column;align-items:center;gap:12px;padding:40px 20px;color:var(--ce);font-size:13px }
.title-card { display:flex;align-items:center;gap:12px;padding:14px 16px;border-radius:8px;border:1px solid var(--cn-200);margin-bottom:10px;cursor:pointer;transition:all .15s }
.title-card:hover { border-color:var(--cn-300);background:var(--cn-50) }
.title-card.selected { border-color:var(--cp-500);background:var(--cp-50) }
.tc-num { width:26px;height:26px;border-radius:50%;background:var(--cn-100);display:flex;align-items:center;justify-content:center;font-size:12px;font-weight:600;color:var(--cn-500);flex-shrink:0 }
.title-card.selected .tc-num { background:var(--cp-500);color:#fff }
.tc-text { flex:1;font-size:14px;color:var(--cn-700);line-height:1.5 }
.tc-btn { padding:5px 16px;border-radius:9999px;font-size:11px;font-weight:600;border:1px solid var(--cn-300);color:var(--cn-500);background:transparent;cursor:pointer;flex-shrink:0;white-space:nowrap }
.tc-btn.active { background:var(--cp-500);color:#fff;border-color:var(--cp-500) }
.content-text-wrap { max-height:400px;overflow-y:auto;margin-bottom:16px }
.content-body { font-size:13px;line-height:2;color:var(--cn-700);white-space:pre-wrap;word-break:break-word }
.img-grid { display:grid;grid-template-columns:repeat(auto-fill, minmax(180px, 1fr));gap:12px }
.img-card { border-radius:10px;overflow:hidden;border:1px solid var(--cn-100);background:var(--cn-25);aspect-ratio:9/16 }
.img-card img { width:100%;height:100%;object-fit:cover;display:block }
.action-bar { display:flex;align-items:center;gap:10px;padding-top:16px;border-top:1px solid var(--cn-100) }
/* Step 6 final review */
.final-review { display:flex;flex-direction:column;gap:16px;margin-bottom:24px }
.final-section { background:#fff;border:1px solid var(--cn-100);border-radius:10px;padding:18px 20px }
.final-section h4 { font-size:12px;font-weight:600;color:var(--cn-400);margin-bottom:8px;text-transform:uppercase }
.final-section p { font-size:14px;color:var(--cn-700);font-weight:500 }
.final-title { font-size:15px!important;font-weight:700!important;color:var(--cn-900)!important }
.final-content { font-size:13px;line-height:2;color:var(--cn-700);white-space:pre-wrap;word-break:break-word;max-height:300px;overflow-y:auto }
.final-prompt { display:flex;align-items:flex-start;gap:8px;font-size:12px;color:var(--cn-600);padding:4px 0 }
.fp-num { display:inline-flex;align-items:center;justify-content:center;width:18px;height:18px;border-radius:50%;background:var(--cn-200);font-size:11px;font-weight:600;color:var(--cn-500);flex-shrink:0;margin-top:1px }
.shimmer-bar { border-radius:4px;background:linear-gradient(90deg,var(--cn-200),var(--cn-100),var(--cn-200));background-size:200% 100%;animation:shimmer 1.5s infinite }
@keyframes shimmer { 0%{background-position:-200% 0} 100%{background-position:200% 0} }
.tag { display:inline-flex;align-items:center;gap:4px;padding:3px 8px;border-radius:9999px;font-size:11px;font-weight:500;color:var(--cn-600) }
.spin { animation:spin 1s linear infinite }
@keyframes spin { from{transform:rotate(0deg)} to{transform:rotate(360deg)} }
.toast { position:fixed;top:24px;left:50%;transform:translateX(-50%);padding:12px 24px;border-radius:8px;font-size:13px;font-weight:500;z-index:9999;animation:slideDown .3s ease;box-shadow:0 4px 16px rgba(0,0,0,.12) }
.toast-success { background:#F0FDF4;color:#15803D;border:1px solid #BBF7D0 }
.toast-error { background:#FEF2F2;color:#B91C1C;border:1px solid #FECACA }
.toast-info { background:#EFF6FF;color:#1D4ED8;border:1px solid #BFDBFE }
@keyframes slideDown { from{transform:translateX(-50%) translateY(-20px);opacity:0} to{transform:translateX(-50%) translateY(0);opacity:1} }
</style>
