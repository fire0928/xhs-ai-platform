<template>
  <view class="page">
    <view class="nav-bar">
      <view class="nav-status"><text>9:41</text><view class="nav-signal"><view class="signal-bar s1"></view><view class="signal-bar s2"></view><view class="signal-bar s3"></view><view class="signal-bar s4"></view></view></view>
      <view class="nav-title-bar">
        <text class="nav-title">AI创作</text>
        <view class="nav-capsule"><view class="capsule-dot"></view><view class="capsule-line"></view></view>
      </view>
    </view>

    <scroll-view scroll-y class="content">
      <!-- 步骤指示 -->
      <view class="steps">
        <view class="step-item" v-for="(s,i) in steps" :key="i">
          <view class="step-dot" :class="stepClass(i)">{{ i + 1 }}</view>
          <view v-if="i < 3" class="step-line" :class="{ done: i < currentStep }"></view>
        </view>
      </view>
      <text class="step-label">{{ currentStep === 0 ? '选择创作风格' : currentStep === 1 ? '输入创作要求' : currentStep === 2 ? '确认标题' : '生成内容' }}</text>

      <!-- Step 1: 选择 Agent -->
      <view class="body" v-if="currentStep === 0">
        <view class="agent-grid">
          <view class="agent-card" v-for="a in agents" :key="a.name" :class="{ selected: selectedAgent === a.name }" @tap="selectAgent(a.name)">
            <view class="agent-icon" :style="{ background: a.bg }"></view>
            <text class="agent-name">{{ a.name }}</text>
            <text class="agent-tag">{{ a.tag }}</text>
          </view>
        </view>
      </view>

      <!-- Step 2: 输入 -->
      <view class="body" v-if="currentStep === 1">
        <view class="agent-summary">
          <view class="as-icon"></view>
          <view>
            <text class="as-name">{{ selectedAgent }} Agent</text>
            <text class="as-domain">日常 / 好物 / 探店</text>
          </view>
        </view>
        <view class="input-group">
          <text class="input-label">创作主题</text>
          <input class="input" v-model="topic" placeholder="如：周末咖啡探店" />
        </view>
        <view class="input-group">
          <text class="input-label">关键词（选填）</text>
          <input class="input" v-model="keywords" placeholder="多个关键词用逗号分隔" />
        </view>
        <view class="input-group">
          <text class="input-label">补充说明（选填）</text>
          <textarea class="textarea" v-model="notes" placeholder="对内容有任何特殊要求..." />
        </view>
      </view>

      <!-- Step 3: 标题确认 -->
      <view class="body" v-if="currentStep === 2">
        <view class="title-card" v-for="(t,i) in titles" :key="i" :class="{ selected: selectedTitle === i }" @tap="selectedTitle = i">
          <view class="tc-num">{{ i + 1 }}</view>
          <text class="tc-text">{{ t }}</text>
          <button class="tc-btn" size="mini">选用</button>
        </view>
        <button class="btn-ghost">重新生成标题</button>
      </view>

      <!-- Step 4: 生成内容 -->
      <view class="body" v-if="currentStep === 3">
        <view class="chosen-title">{{ titles[selectedTitle] || '' }}</view>
        <view class="ai-zone">
          <text class="ai-text">周末的午后，阳光透过玻璃窗洒进这间藏在巷子深处的咖啡店。手冲的耶加雪菲散发着花香，搭配一块柠檬塔，就是完美的下午时光...</text>
        </view>
        <text class="img-label">AI配图</text>
        <scroll-view scroll-x class="img-row">
          <view class="img-thumb" v-for="i in 4" :key="i" :style="{ background: ['#FFE0E5','#FEF3C7','#EDE9FE',''][i-1] || '#E7E5E4' }"></view>
        </scroll-view>
        <button class="btn-share">转发给朋友</button>
      </view>
    </scroll-view>

    <!-- 底部操作 -->
    <view class="footer">
      <button v-if="currentStep > 0" class="btn-ghost" @tap="currentStep--">上一步</button>
      <button class="btn-primary" @tap="nextStep" v-if="currentStep < 3">{{ currentStep === 2 ? '确认标题，生成内容' : '下一步' }}</button>
      <template v-if="currentStep === 3">
        <button class="btn-ghost">修改文案</button>
        <button class="btn-ai">加入发布队列</button>
      </template>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'

const currentStep = ref(0)
const selectedAgent = ref('')
const topic = ref('')
const keywords = ref('')
const notes = ref('')
const selectedTitle = ref(0)

const steps = [{ label: '选择Agent' }, { label: '输入' }, { label: '标题确认' }, { label: '生成内容' }]

const agents = ref([
  { name: '生活分享', tag: '日常/好物/探店', bg: '#FFF1F3' },
  { name: '美食探店', tag: '美食/食谱/探店', bg: '#FEF3C7' },
  { name: '旅行攻略', tag: '旅行/攻略/路线', bg: '#EDE9FE' },
  { name: '穿搭时尚', tag: '穿搭/时尚/潮流', bg: '#DBEAFE' }
])

const titles = ref([
  '周末探店｜这家隐藏的宝藏咖啡店你必须知道！',
  '城市角落里的温暖时光，一杯咖啡治愈周末',
  '咖啡控必打卡！这家小店绝了'
])

function stepClass(i) {
  if (i < currentStep.value) return 'done'
  if (i === currentStep.value) return 'active'
  return 'pending'
}
function selectAgent(name) { selectedAgent.value = name }
function nextStep() {
  if (currentStep.value === 0 && !selectedAgent.value) {
    uni.showToast({ title: '请选择创作风格', icon: 'none' })
    return
  }
  if (currentStep.value === 1 && !topic.value) {
    uni.showToast({ title: '请输入创作主题', icon: 'none' })
    return
  }
  currentStep.value++
}
</script>

<style scoped>
.page { display: flex; flex-direction: column; height: 100vh; background: #FAFAF9; }
.nav-bar { background: #fff; flex-shrink: 0; }
.nav-status { height: 44px; display: flex; align-items: flex-end; justify-content: space-between; padding: 0 24px 4px; font-size: 13px; font-weight: 600; color: #1C1917; }
.nav-signal { display: flex; gap: 2px; align-items: flex-end; }
.signal-bar { width: 3px; border-radius: 1px; background: #1C1917; }
.s1{height:4px}.s2{height:7px}.s3{height:9px}.s4{height:12px;opacity:.3}
.nav-title-bar { height: 44px; display: flex; align-items: center; justify-content: center; position: relative; }
.nav-title { font-size: 17px; font-weight: 600; color: #1C1917; }
.nav-capsule { position: absolute; right: 12px; top: 6px; width: 87px; height: 32px; background: #E7E5E4; border-radius: 16px; display: flex; align-items: center; justify-content: center; gap: 6px; }
.capsule-dot { width: 6px; height: 6px; border-radius: 50%; background: #A8A29E; }
.capsule-line { width: 12px; height: 2px; border-radius: 1px; background: #A8A29E; }

.content { flex: 1; overflow-y: auto; }
.steps { display: flex; align-items: center; padding: 12px 24px 0; }
.step-item { display: flex; align-items: center; flex: 1; }
.step-dot { width: 26px; height: 26px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 11px; font-weight: 700; flex-shrink: 0; }
.step-dot.done { background: #22C55E; color: #fff; }
.step-dot.active { background: #FE2C55; color: #fff; box-shadow: 0 0 0 4px #FFE0E5; }
.step-dot.pending { background: #E7E5E4; color: #A8A29E; }
.step-line { flex: 1; height: 3px; background: #E7E5E4; margin: 0 -1px; }
.step-line.done { background: #22C55E; }
.step-label { display: block; text-align: center; font-size: 11px; color: #78716C; padding: 8px 0 4px; }

.body { padding: 16px; }
.agent-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; }
.agent-card { padding: 14px 10px; border-radius: 12px; border: 1.5px solid #E7E5E4; text-align: center; }
.agent-card.selected { border-color: #FE2C55; background: #FFF1F3; }
.agent-icon { width: 44px; height: 44px; border-radius: 12px; margin: 0 auto 6px; }
.agent-name { font-size: 13px; font-weight: 600; color: #292524; display: block; }
.agent-tag { font-size: 10px; color: #78716C; }
.agent-summary { display: flex; gap: 8px; align-items: center; padding: 10px; border-radius: 10px; background: #FFF1F3; border: 1px solid #FFC6CF; margin-bottom: 14px; }
.as-icon { width: 32px; height: 32px; border-radius: 8px; background: #FFE0E5; }
.as-name { font-size: 13px; font-weight: 600; color: #292524; display: block; }
.as-domain { font-size: 10px; color: #78716C; }
.input-group { margin-bottom: 14px; }
.input-label { font-size: 13px; font-weight: 600; color: #44403C; display: block; margin-bottom: 6px; }
.input { width: 100%; padding: 12px 14px; border-radius: 10px; border: 1.5px solid #E7E5E4; font-size: 14px; }
.textarea { width: 100%; padding: 12px 14px; border-radius: 10px; border: 1.5px solid #E7E5E4; font-size: 14px; height: 70px; }

.title-card { display: flex; align-items: flex-start; gap: 10px; padding: 12px; border-radius: 10px; border: 1.5px solid #E7E5E4; margin-bottom: 8px; }
.title-card.selected { border-color: #FE2C55; background: #FFF1F3; }
.tc-num { width: 22px; height: 22px; border-radius: 50%; background: #F5F5F4; display: flex; align-items: center; justify-content: center; font-size: 11px; font-weight: 700; color: #78716C; flex-shrink: 0; }
.title-card.selected .tc-num { background: #FE2C55; color: #fff; }
.tc-text { flex: 1; font-size: 13px; color: #292524; line-height: 1.5; }
.tc-btn { padding: 3px 10px; border-radius: 14px; font-size: 11px; font-weight: 600; border: 1px solid #FE2C55; color: #FE2C55; background: transparent; }
.title-card.selected .tc-btn { background: #FE2C55; color: #fff; }

.chosen-title { font-size: 14px; font-weight: 600; color: #292524; padding: 12px; background: #FFF1F3; border-radius: 8px; border-left: 3px solid #FE2C55; margin-bottom: 14px; }
.ai-zone { padding: 14px; border-radius: 12px; background: linear-gradient(135deg,#FFE0E5,#EDE9FE); margin-bottom: 14px; }
.ai-text { font-size: 13px; color: #44403C; line-height: 1.7; }
.img-label { font-size: 11px; color: #78716C; display: block; margin-bottom: 8px; }
.img-row { display: flex; gap: 8px; }
.img-thumb { width: 90px; height: 90px; border-radius: 10px; flex-shrink: 0; }
.btn-share { width: 100%; padding: 14px; border-radius: 12px; background: #07C160; color: #fff; font-size: 14px; font-weight: 600; border: none; margin-top: 16px; box-shadow: 0 4px 12px rgba(7,193,96,.3); }
.btn-ghost { background: transparent; color: #57534E; border: none; font-size: 14px; padding: 10px 20px; }

.footer { display: flex; gap: 10px; padding: 12px 16px; background: #fff; border-top: 1px solid #F5F5F4; flex-shrink: 0; }
.btn-primary { flex: 1; padding: 12px; border-radius: 24px; background: linear-gradient(135deg,#FE2C55,#FF6480); color: #fff; font-size: 14px; font-weight: 600; border: none; box-shadow: 0 4px 12px rgba(254,44,85,.3); }
.btn-ai { flex: 1; padding: 12px; border-radius: 24px; background: linear-gradient(135deg,#FE2C55,#8B5CF6); color: #fff; font-size: 14px; font-weight: 600; border: none; box-shadow: 0 4px 12px rgba(254,44,85,.25); }
</style>
