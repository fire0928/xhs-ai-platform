<template>
  <div class="ai-create-page">
    <div class="step-indicator">
      <StepItem :num="1" label="选择 Agent" status="done" />
      <StepItem :num="2" label="创作输入" status="done" />
      <StepItem :num="3" label="标题确认" status="active" />
      <StepItem :num="4" label="文案配图生成" status="pending" />
    </div>

    <div class="current-stage">
      <div class="stage-info">
        <span class="tag tag-green"><svg width="10" height="10" viewBox="0 0 10 10" fill="none"><path d="M2 5L4.5 7.5L8 3" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>已选择</span>
        <span class="stage-text">生活分享 Agent · 主题：春日野餐攻略 · 人群：25-30岁女生</span>
        <button class="btn btn-ghost btn-sm">更换</button>
      </div>
    </div>

    <!-- AI 生成标题候选 -->
    <div class="ai-zone">
      <div class="ai-zone-inner">
        <div class="zone-header">
          <svg width="18" height="18" viewBox="0 0 18 18" fill="none"><path d="M9 1L10.5 5H15L11.5 7.5L13 12L9 9L5 12L6.5 7.5L3 5H7.5L9 1Z" fill="#8B5CF6"/></svg>
          <span class="zone-title">AI 已生成 3 个标题候选</span>
          <button class="btn btn-ghost btn-sm"><svg width="10" height="10" viewBox="0 0 10 10" fill="none"><path d="M5 1V9M1 5H9" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>重新生成</button>
        </div>
        <div v-for="(title, i) in titles" :key="i" :class="['title-card', { selected: selectedTitle === i }]" @click="selectedTitle = i">
          <div class="tc-num">{{ i + 1 }}</div>
          <div class="tc-text">{{ title }}</div>
          <button :class="['tc-btn', { active: selectedTitle === i }]">{{ selectedTitle === i ? '已选用' : '选用' }}</button>
        </div>
      </div>
    </div>

    <!-- AI 生成内容 -->
    <div class="ai-zone">
      <div class="ai-zone-inner">
        <div class="zone-header">
          <svg width="18" height="18" viewBox="0 0 18 18" fill="none"><path d="M9 1L10.5 5H15L11.5 7.5L13 12L9 9L5 12L6.5 7.5L3 5H7.5L9 1Z" fill="#8B5CF6"/></svg>
          <span class="zone-title">AI 正在生成文案与配图...</span>
          <div class="shimmer-bar"></div>
        </div>
        <div class="content-grid">
          <div class="content-text">
            <h4>文案内容</h4>
            <p>🌸 春日野餐全攻略：从装备到美食，一篇文章搞定</p>
            <p>春天来了，怎么能少得了一场美美的野餐呢？🧺</p>
            <p>今天给大家整理了一份超全的野餐攻略，从装备选择到美食准备...</p>
            <p class="typing">📍 地点推荐：城市公园的草坪区是首选...<span class="cursor"></span></p>
          </div>
          <div class="content-images">
            <h4>AI 配图</h4>
            <div class="img-grid">
              <div class="img-placeholder" style="background:#FFE0E5"></div>
              <div class="img-placeholder" style="background:#EDE9FE"></div>
              <div class="img-placeholder" style="background:#FEF3C7"></div>
              <div class="img-placeholder shimmer"></div>
            </div>
          </div>
        </div>
        <div class="action-bar">
          <button class="btn btn-ghost">预览</button>
          <button class="btn btn-outline">重新生成</button>
          <button class="btn btn-outline">修改文案</button>
          <button class="btn btn-ai-main">加入发布队列</button>
        </div>
      </div>
    </div>

    <!-- Agent 选择面板 -->
    <details style="margin-bottom:24px">
      <summary class="panel-summary">▶ 展开 Agent 选择面板</summary>
      <div class="agent-grid">
        <div v-for="agent in agents" :key="agent.id" :class="['agent-card', { selected: agent.selected }]">
          <div class="agent-icon" :style="{ background: agent.bg }">
            <svg width="22" height="22" viewBox="0 0 22 22" fill="none"><path d="M11 3L13 8H18L14 11L15.5 16L11 13L6.5 16L8 11L4 8H9L11 3Z" fill="currentColor" opacity=".7"/></svg>
          </div>
          <div class="agent-name">{{ agent.name }}</div>
          <div class="agent-desc">{{ agent.desc }}</div>
          <div class="agent-tags"><span v-for="t in agent.tags" :key="t" class="tag tag-red">{{ t }}</span></div>
        </div>
      </div>
    </details>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import StepItem from './components/StepItem.vue'

const selectedTitle = ref(0)
const titles = [
  '🌸 春日野餐全攻略：从装备到美食，一篇文章搞定',
  '周末野餐指南 | 这些好吃好拍的绝美地点你必须知道',
  '野餐拍照出片攻略 | 学会这几点朋友圈获赞无数'
]

const agents = [
  { id: 1, selected: true, bg: 'var(--cp-50)', name: '生活分享 Agent', desc: '擅长日常生活类内容创作，风格轻松自然', tags: ['生活方式', '好物推荐'] },
  { id: 2, selected: false, bg: 'var(--ca-orange-l)', name: '美食探店 Agent', desc: '专注美食领域，写出令人食欲大开的探店内容', tags: ['美食探店', '食谱分享'] },
  { id: 3, selected: false, bg: '#DCFCE7', name: '旅行攻略 Agent', desc: '编写详细的旅行攻略和路线推荐', tags: ['旅行攻略', '路线推荐'] },
  { id: 4, selected: false, bg: 'var(--ca-purple-l)', name: '自定义 Agent', desc: '根据你的需求定制专属创作风格', tags: ['自定义', '灵活配置'] },
]
</script>

<style scoped>
.ai-create-page { max-width: 960px; margin: 0 auto; }

.step-indicator { display: flex; align-items: center; justify-content: center; gap: 0; margin-bottom: 32px; }

.current-stage { margin-bottom: 24px; }
.stage-info { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; }
.stage-text { font-size: 13px; color: var(--cn-500); }
.tag { display: inline-flex; align-items: center; gap: 4px; padding: 3px 8px; border-radius: 9999px; font-size: 11px; font-weight: 500; }
.tag-green { background: #DCFCE7; color: #15803D; }

.btn { display: inline-flex; align-items: center; justify-content: center; gap: 8px; padding: 8px 16px; border-radius: 8px; font-size: 13px; font-weight: 500; transition: all .15s; cursor: pointer; }
.btn svg { width: 16px; height: 16px; }
.btn-ghost { color: var(--cn-600); }
.btn-ghost:hover { background: var(--cn-100); }
.btn-outline { background: #fff; color: var(--cp-500); border: 1px solid var(--cp-300); }
.btn-outline:hover { background: var(--cp-50); }
.btn-sm { padding: 4px 12px; font-size: 11px; }
.btn-ai-main { background: var(--gai); color: #fff; min-width: 160px; padding: 12px 20px; margin-left: auto; }

.ai-zone { border-radius: 12px; padding: 1px; background: var(--gai); margin-bottom: 24px; }
.ai-zone-inner { background: #fff; border-radius: 11px; padding: 24px; }
.zone-header { display: flex; align-items: center; gap: 8px; margin-bottom: 20px; }
.zone-title { font-size: 14px; font-weight: 600; color: var(--cn-800); }
.shimmer-bar { width: 60px; height: 8px; border-radius: 4px; background: linear-gradient(90deg, var(--cn-100), var(--cn-50), var(--cn-100)); background-size: 200% 100%; animation: shimmer 1.5s infinite; }

.title-card { display: flex; align-items: center; gap: 12px; padding: 12px 16px; border-radius: 8px; border: 1px solid var(--cn-200); margin-bottom: 8px; cursor: pointer; transition: all .15s; }
.title-card:hover { border-color: var(--cn-300); background: var(--cn-50); }
.title-card.selected { border-color: var(--cp-500); background: var(--cp-50); }
.tc-num { width: 24px; height: 24px; border-radius: 50%; background: var(--cn-100); display: flex; align-items: center; justify-content: center; font-size: 11px; font-weight: 600; color: var(--cn-500); flex-shrink: 0; }
.title-card.selected .tc-num { background: var(--cp-500); color: #fff; }
.tc-text { flex: 1; font-size: 14px; color: var(--cn-700); line-height: 1.5; }
.tc-btn { padding: 4px 14px; border-radius: 9999px; font-size: 11px; font-weight: 600; border: 1px solid var(--cn-300); color: var(--cn-500); background: transparent; cursor: pointer; flex-shrink: 0; }
.tc-btn.active { background: var(--cp-500); color: #fff; border-color: var(--cp-500); }

.content-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-bottom: 24px; }
.content-text h4, .content-images h4 { font-size: 13px; font-weight: 600; color: var(--cn-500); margin-bottom: 12px; }
.content-text { background: #fff; border-radius: 12px; padding: 20px; border: 1px solid var(--cn-100); min-height: 300px; }
.content-text p { font-size: 13px; line-height: 1.8; color: var(--cn-700); }
.typing .cursor { display: inline-block; width: 2px; height: 16px; background: var(--cp-500); animation: blink .8s infinite; vertical-align: text-bottom; }
@keyframes blink { 0%, 100% { opacity: 1; } 50% { opacity: 0; } }
.content-images { background: #fff; border-radius: 12px; padding: 20px; border: 1px solid var(--cn-100); }
.img-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.img-placeholder { aspect-ratio: 3/4; border-radius: 8px; }
.img-placeholder.shimmer { background: linear-gradient(90deg, var(--cn-200), var(--cn-100), var(--cn-200)); background-size: 200% 100%; animation: shimmer 1.5s infinite; }
@keyframes shimmer { 0% { background-position: -200% 0; } 100% { background-position: 200% 0; } }
.action-bar { display: flex; align-items: center; gap: 12px; padding-top: 16px; }

.panel-summary { cursor: pointer; font-size: 13px; font-weight: 600; color: var(--cn-500); margin-bottom: 16px; outline: none; }
.agent-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; }
.agent-card { background: #fff; border-radius: 12px; padding: 20px; border: 1px solid var(--cn-100); text-align: left; transition: all .15s; }
.agent-card.selected { border: 2px solid var(--cp-500); background: var(--cp-50); }
.agent-icon { width: 40px; height: 40px; border-radius: 8px; display: flex; align-items: center; justify-content: center; margin-bottom: 12px; }
.agent-name { font-size: 14px; font-weight: 600; color: var(--cn-800); margin-bottom: 4px; }
.agent-desc { font-size: 11px; color: var(--cn-500); margin-bottom: 12px; line-height: 1.6; }
.agent-tags { display: flex; gap: 4px; flex-wrap: wrap; }
</style>
