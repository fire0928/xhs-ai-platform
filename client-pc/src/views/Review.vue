<template>
  <div class="review-layout">
    <div class="rev-feed">
      <div class="rev-filters">
        <span v-for="f in filters" :key="f" :class="['ftab', { active: currentFilter === f }]" @click="currentFilter = f">{{ f }}</span>
        <div class="rev-search">
          <svg viewBox="0 0 14 14" fill="none"><circle cx="6" cy="6" r="4.5" stroke="currentColor" stroke-width="1.5"/><path d="M9.5 9.5L13 13" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
          <input type="text" placeholder="搜索...">
        </div>
      </div>
      <div v-for="item in reviewList" :key="item.id" :class="['rev-fc', { active: selectedId === item.id }]" @click="selectedId = item.id">
        <div class="rev-fc-th">
          <svg viewBox="0 0 80 100"><rect width="80" height="100" :fill="item.bg"/><circle cx="40" cy="35" r="15" :fill="item.iconBg" opacity=".6"/><rect x="15" y="55" width="50" height="30" rx="4" :fill="item.iconBg" opacity=".4"/></svg>
        </div>
        <div class="rev-fc-info">
          <div class="rev-fc-tt">{{ item.title }}</div>
          <div class="rev-fc-tags"><span v-for="t in item.tags" :key="t" :class="['tag', t.color]">{{ t.text }}</span></div>
          <div class="rev-fc-src">{{ item.source }}</div>
        </div>
      </div>
    </div>
    <div class="rev-det" v-if="currentContent">
      <div class="rev-det-img">
        <svg viewBox="0 0 480 360"><rect width="480" height="360" :fill="currentContent.bg"/><rect x="60" y="40" width="200" height="140" rx="12" :fill="currentContent.iconBg" opacity=".4"/></svg>
      </div>
      <div class="rev-det-tt">{{ currentContent.title }}</div>
      <div class="rev-det-ct">{{ currentContent.content }}</div>
      <div class="rev-det-tags"><span v-for="t in currentContent.tags" :key="t" class="tag tag-red">{{ t }}</span></div>
      <div class="rev-ai">
        <h5><svg width="16" height="16" viewBox="0 0 16 16" fill="none"><path d="M8 1L9.5 5H14L10.5 7.5L12 12L8 9L4 12L5.5 7.5L2 5H6.5L8 1Z" fill="#8B5CF6"/></svg>AI 内容评分</h5>
        <AiScore label="吸引力" :score="88" color="var(--gb)" />
        <AiScore label="可读性" :score="92" color="var(--gg)" />
        <AiScore label="标签匹配" :score="85" color="var(--ca-purple)" />
        <AiScore label="平台适配" :score="90" color="var(--ci)" />
      </div>
      <div class="rev-acts">
        <button class="rev-ab ap"><svg width="16" height="16" viewBox="0 0 16 16" fill="none"><path d="M3 8L6.5 11.5L13 4" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>通过</button>
        <button class="rev-ab ed"><svg width="16" height="16" viewBox="0 0 16 16" fill="none"><path d="M11.5 2.5L13.5 4.5L5 13H3V11L11.5 2.5Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>修改</button>
        <button class="rev-ab rj"><svg width="16" height="16" viewBox="0 0 16 16" fill="none"><path d="M4 4L12 12M12 4L4 12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>拒绝</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import AiScore from './components/AiScore.vue'

const filters = ['全部', '待审核', '已通过', '已拒绝']
const currentFilter = ref('待审核')
const selectedId = ref(0)

const reviewList = [
  { id: 0, bg: '#FFE0E5', iconBg: '#FF9DAD', title: '春日樱花限定甜品，颜值超绝', tags: [{ text: '美食', color: 'tag-red' }, { text: 'AI', color: 'tag-purple' }], source: '生活分享Agent' },
  { id: 1, bg: '#EDE9FE', iconBg: '#8B5CF6', title: '周末city walk路线推荐', tags: [{ text: '旅行', color: 'tag-orange' }, { text: 'AI', color: 'tag-purple' }], source: '旅行攻略Agent' },
  { id: 2, bg: '#FEF3C7', iconBg: '#F59E0B', title: '居家好物分享：提升幸福感', tags: [{ text: '生活', color: 'tag-green' }, { text: '手动', color: 'tag-blue' }], source: '手动上传' },
  { id: 3, bg: '#DCFCE7', iconBg: '#22C55E', title: '健康轻食搭配一周食谱', tags: [{ text: '健康', color: 'tag-green' }, { text: 'AI', color: 'tag-purple' }], source: '美食探店Agent' },
]

const currentContent = computed(() => ({
  bg: '#FFE0E5', iconBg: '#FF9DAD',
  title: '🌸 春日樱花限定甜品，颜值超绝',
  content: '春天来了，怎么能错过限定樱花甜品呢！今天去了三家店，每一家都美到尖叫 🌸\n\n📍 第一家：花见和果子 — 樱花大福，外皮Q弹，内馅细腻\n📍 第二家：Café de Fleur — 樱花拿铁，拉花超美\n📍 第三家：甜品实验室 — 樱花千层，层层叠叠的粉嫩\n\n每一家都值得打卡！快约上闺蜜一起去吧~',
  tags: ['美食探店', '樱花限定', '甜品推荐', '春日打卡']
}))
</script>

<style scoped>
.review-layout { display: grid; grid-template-columns: 340px 1fr; gap: 0; height: calc(100vh - 56px - 64px); margin: -32px; margin-top: 0; }
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

.rev-det { display: flex; flex-direction: column; padding: 20px; overflow-y: auto; }
.rev-det-img { width: 100%; aspect-ratio: 4/3; border-radius: 12px; overflow: hidden; background: var(--cn-100); margin-bottom: 20px; }
.rev-det-tt { font-size: 20px; font-weight: 700; color: var(--cn-900); margin-bottom: 12px; }
.rev-det-ct { font-size: 13px; line-height: 1.8; color: var(--cn-600); margin-bottom: 20px; white-space: pre-line; }
.rev-det-tags { display: flex; gap: 8px; flex-wrap: wrap; margin-bottom: 20px; }
.rev-ai { background: linear-gradient(135deg, #FFE0E5, #EDE9FE); border-radius: 12px; padding: 16px; margin-bottom: 20px; }
.rev-ai h5 { font-size: 13px; font-weight: 600; color: var(--cn-700); margin-bottom: 12px; display: flex; align-items: center; gap: 8px; }
.rev-acts { display: flex; gap: 12px; padding-top: 16px; border-top: 1px solid var(--cn-100); margin-top: auto; }
.rev-ab { flex: 1; display: flex; align-items: center; justify-content: center; gap: 8px; padding: 12px; border-radius: 8px; font-size: 13px; font-weight: 600; cursor: pointer; transition: all .15s; }
.rev-ab.ap { background: #DCFCE7; color: #15803D; }
.rev-ab.ap:hover { background: #BBF7D0; }
.rev-ab.ed { background: #DBEAFE; color: #1D4ED8; }
.rev-ab.ed:hover { background: #BFDBFE; }
.rev-ab.rj { background: #FEE2E2; color: #B91C1C; }
.rev-ab.rj:hover { background: #FECACA; }
</style>
