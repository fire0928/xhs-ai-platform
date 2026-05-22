<template>
  <div>
    <div class="flex aic jbt mb6">
      <div><div style="font-size:20px;font-weight:700;color:var(--cn-900)">AI 大模型管理</div><div style="font-size:13px;color:var(--cn-500)">配置大模型 API、密钥与调用参数</div></div>
      <button class="btn btn-p" @click="showModal=true"><svg viewBox="0 0 16 16" fill="none"><path d="M8 2V14M2 8H14" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>新增 API</button>
    </div>

    <div v-for="api in apis" :key="api.name" class="api-card">
      <div class="api-card-h">
        <div class="flex aic g3"><span class="sd sd-g"></span><span style="font-size:14px;font-weight:600;color:var(--cn-800)">{{ api.name }}</span></div>
        <div class="flex aic g2"><button class="btn btn-sm btn-g">编辑</button><button class="btn btn-sm" style="color:var(--ce)">删除</button></div>
      </div>
      <div style="display:grid;grid-template-columns:1fr 1fr;gap:12px;font-size:11px;color:var(--cn-500)">
        <span>地址：{{ api.url }}</span><span>限制：{{ api.limit }}</span>
        <span>Agent：{{ api.agents }}</span><span>今日调用：{{ api.todayCall }}</span>
      </div>
      <div style="margin-top:12px">
        <div class="flex aic jbt" style="margin-bottom:4px"><span style="font-size:11px;color:var(--cn-500)">额度使用</span><span class="fnm" style="font-size:11px;color:var(--cn-600);font-weight:600">{{ api.used }}/{{ api.total }}</span></div>
        <div style="height:6px;background:var(--cn-100);border-radius:3px"><div style="height:100%;border-radius:3px;background:var(--gai)" :style="{width: api.pct+'%'}"></div></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const showModal = ref(false)
const apis = ref([
  { name:'GPT-4o', url:'api.openai.com/v1', limit:'60次/分', agents:'3个', todayCall:'2,847', used:'$1,247', total:'$2,000', pct:62 },
  { name:'文心一言 4.0', url:'aip.baidubce.com', limit:'100次/分', agents:'2个', todayCall:'1,523', used:'¥3,420', total:'¥5,000', pct:68 },
  { name:'通义千问 Max', url:'dashscope.aliyuncs.com', limit:'80次/分', agents:'1个', todayCall:'876', used:'¥1,200', total:'¥3,000', pct:40 }
])
</script>

<style scoped>
.api-card{background:#fff;border-radius:12px;padding:20px;box-shadow:var(--shs);border:1px solid var(--cn-100);margin-bottom:16px}
.api-card-h{display:flex;align-items:center;justify-content:space-between;margin-bottom:16px}
</style>
