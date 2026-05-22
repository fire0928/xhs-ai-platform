<template>
  <div>
    <div class="flex aic jbt mb6">
      <div><div style="font-size:20px;font-weight:700;color:var(--cn-900)">Agent 管理</div><div style="font-size:13px;color:var(--cn-500)">创建和管理创作 Agent</div></div>
      <button class="btn btn-p">新增 Agent</button>
    </div>

    <div style="display:grid;grid-template-columns:repeat(3,1fr);gap:20px">
      <div class="agent-card" v-for="a in agents" :key="a.name">
        <div class="flex aic g3" style="margin-bottom:16px">
          <div class="agent-icon" :style="{background:a.bg}"><svg width="18" height="18" viewBox="0 0 18 18" fill="none"><path d="M9 2L10.5 5.5H14.5L11.25 8L12.75 12L9 9.5L5.25 12L6.75 8L3.5 5.5H7.5L9 2Z" :fill="a.starColor" opacity=".7"/></svg></div>
          <div><div style="font-size:14px;font-weight:600;color:var(--cn-800)">{{ a.name }}</div><div style="font-size:11px;color:var(--cn-400)">绑定：{{ a.model }}</div></div>
        </div>
        <div style="font-size:13px;color:var(--cn-600);line-height:1.7;margin-bottom:16px">{{ a.desc }}</div>
        <div class="flex aic jbt">
          <span style="font-size:11px;color:var(--cn-400)">今日调用 {{ a.today }}</span>
          <div class="flex aic g2"><button class="btn btn-sm btn-g">编辑</button><button class="btn btn-sm" style="color:var(--ce)">删除</button></div>
        </div>
      </div>
      <div class="agent-card-add">
        <svg width="32" height="32" viewBox="0 0 32 32" fill="none"><circle cx="16" cy="16" r="12" stroke="#A8A29E" stroke-width="2" stroke-dasharray="4 2"/><path d="M16 10V22M10 16H22" stroke="#A8A29E" stroke-width="2" stroke-linecap="round"/></svg>
        <span style="font-size:13px;color:var(--cn-400);margin-top:12px">添加新 Agent</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const agents = ref([
  { name:'生活分享 Agent', model:'GPT-4o', bg:'var(--cp-50)', starColor:'#FE2C55', today:'1,024次', desc:'擅长日常生活类内容创作，风格轻松自然，适合生活方式博主' },
  { name:'美食探店 Agent', model:'文心一言', bg:'var(--ca-orange-l)', starColor:'#F59E0B', today:'823次', desc:'专注美食领域，写出令人食欲大开的探店内容和食谱分享' },
  { name:'旅行攻略 Agent', model:'GPT-4o', bg:'#DCFCE7', starColor:'#22C55E', today:'567次', desc:'编写详细的旅行攻略和路线推荐，适合旅行博主' },
  { name:'穿搭时尚 Agent', model:'通义千问', bg:'var(--ca-purple-l)', starColor:'#8B5CF6', today:'433次', desc:'时尚穿搭内容创作，了解流行趋势和搭配技巧' }
])
</script>

<style scoped>
.agent-card{background:#fff;border-radius:12px;padding:20px;box-shadow:var(--shs);border:1px solid var(--cn-100)}
.agent-card:hover{box-shadow:var(--shm)}
.agent-icon{width:40px;height:40px;border-radius:8px;display:flex;align-items:center;justify-content:center}
.agent-card-add{border:2px dashed var(--cn-300);border-radius:12px;display:flex;flex-direction:column;align-items:center;justify-content:center;min-height:240px;cursor:pointer;background:transparent}
.agent-card-add:hover{border-color:var(--cp-300);background:var(--cp-50)}
</style>
