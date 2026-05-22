<template>
  <div>
    <div class="atr">
      <span v-for="t in ['7天','30天','90天','自定义']" :key="t" :class="['att', { active: current === t }]" @click="current = t">{{ t }}</span>
    </div>

    <div class="am-row">
      <AnalyticsStat label="总阅读量" value="128.5K" change="↑ 24.3%" color="var(--cs)" />
      <AnalyticsStat label="总互动量" value="42.8K" change="↑ 18.5%" color="var(--cs)" />
      <AnalyticsStat label="平均互动率" value="5.7%" change="↑ 0.8%" color="var(--cs)" />
      <AnalyticsStat label="爆款数量" value="8" change="→ 持平" color="var(--ca-orange)" />
    </div>

    <div class="cc" style="margin-bottom:24px">
      <div class="cc-h"><div class="cc-t">阅读量 & 互动量趋势</div></div>
      <svg viewBox="0 0 520 180" style="width:100%">
        <line x1="40" y1="155" x2="510" y2="155" stroke="#E7E5E4" stroke-width=".5"/>
        <polyline points="107,110 241,75 375,55 509,35" fill="none" stroke="#FE2C55" stroke-width="2.5" stroke-linecap="round"/>
        <polyline points="107,125 241,100 375,80 509,65" fill="none" stroke="#F59E0B" stroke-width="2" stroke-dasharray="6 3"/>
        <text x="107" y="172" text-anchor="middle" fill="#A8A29E" font-size="10">第1周</text>
        <text x="241" y="172" text-anchor="middle" fill="#A8A29E" font-size="10">第2周</text>
        <text x="375" y="172" text-anchor="middle" fill="#A8A29E" font-size="10">第3周</text>
        <text x="509" y="172" text-anchor="middle" fill="#A8A29E" font-size="10">第4周</text>
      </svg>
    </div>

    <div class="ab-row">
      <div class="cc">
        <div class="cc-h"><div class="cc-t">内容效果 Top 10</div></div>
        <RankItem v-for="(item, i) in topContent" :key="i" :rank="i+1" :title="item.title" :value="item.value" :bg="item.bg" />
      </div>
      <div class="cc">
        <div class="cc-h"><div class="cc-t">AI 创作 vs 手动创作</div></div>
        <div class="hb-row">
          <div class="hb-label">AI 创作</div>
          <div class="hb-bar"><div class="hb-fill" style="width:78%;background:var(--gai)">78%</div></div>
        </div>
        <div class="hb-row">
          <div class="hb-label">手动创作</div>
          <div class="hb-bar"><div class="hb-fill" style="width:22%;background:var(--cn-300)">22%</div></div>
        </div>
        <div class="ai-compare">
          <div class="comp-title">AI 创作效果对比</div>
          <div style="display:grid;grid-template-columns:1fr 1fr;gap:12px">
            <div class="comp-stat">
              <div class="comp-val" style="color:var(--ca-purple)">6.2%</div>
              <div class="comp-label">AI 平均互动率</div>
            </div>
            <div class="comp-stat">
              <div class="comp-val" style="color:var(--cn-600)">4.8%</div>
              <div class="comp-label">手动平均互动率</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import AnalyticsStat from './components/AnalyticsStat.vue'
import RankItem from './components/RankItem.vue'
import HBar from './components/HBar.vue'

const current = ref('30天')
const topContent = [
  { title: '春日穿搭灵感合集', value: '2,847', bg: '#FFE0E5' },
  { title: '咖啡探店：隐藏菜单', value: '2,156', bg: '#EDE9FE' },
  { title: '护肤分享：换季必备', value: '1,923', bg: '#FEF3C7' },
  { title: '周末出游：近郊推荐', value: '1,654', bg: '#DCFCE7' },
  { title: '居家好物提升幸福感', value: '1,432', bg: '#DBEAFE' },
]
</script>

<style scoped>
.atr { display: flex; gap: 8px; margin-bottom: 24px; }
.att { padding: 8px 16px; border-radius: 9999px; font-size: 13px; font-weight: 500; color: var(--cn-500); cursor: pointer; transition: all .15s; }
.att:hover { color: var(--cn-700); background: var(--cn-100); }
.att.active { background: var(--cn-800); color: #fff; }
.am-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 24px; }
.ab-row { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }

.cc { background: #fff; border-radius: 12px; padding: 20px; box-shadow: 0 1px 3px rgba(0,0,0,.06); border: 1px solid var(--cn-100); }
.cc-h { display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px; }
.cc-t { font-size: 14px; font-weight: 600; color: var(--cn-800); }

.hb-row { margin-bottom: 16px; }
.hb-label { font-size: 11px; color: var(--cn-500); margin-bottom: 6px; }
.hb-bar { height: 20px; background: var(--cn-100); border-radius: 9999px; overflow: hidden; }
.hb-fill { height: 100%; border-radius: 9999px; display: flex; align-items: center; padding-left: 8px; font-size: 11px; font-weight: 600; color: #fff; min-width: 32px; }

.ai-compare { margin-top: 20px; padding: 16px; background: linear-gradient(135deg, #FFE0E5, #EDE9FE); border-radius: 12px; }
.comp-title { font-size: 13px; font-weight: 600; color: var(--cn-700); margin-bottom: 8px; }
.comp-stat { text-align: center; }
.comp-val { font-family: 'JetBrains Mono', monospace; font-size: 20px; font-weight: 700; }
.comp-label { font-size: 11px; color: var(--cn-500); }
</style>
