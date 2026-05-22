<template>
  <div>
    <div class="flex aic jbt mb6">
      <div><div style="font-size:20px;font-weight:700;color:var(--cn-900)">会员管理</div><div style="font-size:13px;color:var(--cn-500)">设定会员等级、定价与注册开通管理</div></div>
      <div class="flex aic g3"><button class="btn btn-g">新增等级</button><button class="btn btn-p">保存变更</button></div>
    </div>

    <div style="display:grid;grid-template-columns:1fr 1fr 1fr;gap:20px;margin-bottom:24px">
      <div class="mem-card" v-for="m in members" :key="m.name" :style="{ borderTop: `3px solid ${m.color}` }">
        <div class="flex aic jbt" style="margin-bottom:16px"><span class="fw6" style="font-size:14px" :style="{color:m.color}">{{ m.name }}</span><span class="tag" :class="'tag-'+m.tagCls">当前{{ m.count }}人</span></div>
        <div style="font-size:20px;font-weight:700;color:var(--cn-900);margin-bottom:4px">{{ m.name }}</div>
        <div style="margin-bottom:16px"><span class="fnm" style="font-size:28px;font-weight:700" :style="{color: m.price === '¥0' ? 'var(--cn-500)' : 'var(--cn-900)'}">{{ m.price }}</span><span style="font-size:11px;color:var(--cn-400);margin-left:4px">/{{ m.period }}</span></div>
        <ul style="list-style:none;margin-bottom:20px">
          <li v-for="(f,fi) in m.features" :key="fi" style="font-size:11px;color:var(--cn-600);padding:4px 0;display:flex;align-items:center;gap:8px" :style="{color: (fi >= m.activeCount) ? 'var(--cn-300)' : ''}">
            <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M3 7L6 10L11 4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>{{ f }}
          </li>
        </ul>
        <button class="btn" style="width:100%" :class="m.btnClass">编辑等级</button>
      </div>
    </div>

    <div style="display:grid;grid-template-columns:1fr 1fr;gap:20px">
      <div class="card"><div class="card-t">注册设置</div></div>
      <div class="card"><div class="card-t">升降级规则</div></div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const members = ref([
  { name:'免费版', price:'¥0', period:'永久', count:'682', color:'var(--cn-400)', tagCls:'n', btnClass:'btn-s',
    features:['每日3次AI创作','绑定1个账号','基础数据分析','2个Agent','批量操作','数据导出'], activeCount:4 },
  { name:'标准版', price:'¥49', period:'月', count:'412', color:'var(--ca-orange)', tagCls:'o', btnClass:'btn-p',
    features:['每日20次AI创作','绑定2个账号','高级分析','全部Agent','批量审核','数据导出'], activeCount:5 },
  { name:'专业版', price:'¥99', period:'月', count:'190', color:'var(--ca-purple)', tagCls:'p', btnClass:'btn-p',
    features:['无限AI创作','绑定3个账号','全功能分析','自定义Agent','定时发布','数据导出'], activeCount:6 }
])
</script>

<style scoped>
.mem-card{background:#fff;border-radius:12px;padding:20px;box-shadow:var(--shs);border:1px solid var(--cn-100)}
</style>
