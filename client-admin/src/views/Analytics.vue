<template>
  <div>
    <div class="flex aic jbt mb6">
      <div><div style="font-size:20px;font-weight:700;color:var(--cn-900)">数据分析</div><div style="font-size:13px;color:var(--cn-500)">全局统计报表</div></div>
      <div class="flex aic g3">
        <select class="ipt" style="width:140px"><option>全部用户</option></select>
        <select class="ipt" style="width:120px"><option>近7天</option></select>
        <button class="btn btn-p">导出</button>
      </div>
    </div>

    <div class="am-row">
      <div class="am"><div class="am-lb">平均创作量/用户</div><div class="am-vl">41</div><div class="am-ch" style="color:var(--cs)">↑ 15%</div></div>
      <div class="am"><div class="am-lb">平均审核通过率</div><div class="am-vl" style="color:var(--cs)">87.3%</div><div class="am-ch">目标 90%</div></div>
      <div class="am"><div class="am-lb">平均发布成功率</div><div class="am-vl" style="color:var(--cs)">96.8%</div><div class="am-ch" style="color:var(--cs)">↑ 1.2%</div></div>
      <div class="am"><div class="am-lb">平均内容效果</div><div class="am-vl">356</div><div class="am-ch">互动量/篇</div></div>
    </div>

    <div style="display:grid;grid-template-columns:1.5fr 1fr;gap:20px;margin-bottom:24px">
      <div class="card">
        <div class="card-t" style="margin-bottom:16px">各终端创作效果对比</div>
        <div class="hb"><div class="hb-l">电脑-AI</div><div class="hb-t"><div class="hb-f" style="width:78%;background:var(--gai)">412</div></div></div>
        <div class="hb"><div class="hb-l">电脑-手动</div><div class="hb-t"><div class="hb-f" style="width:45%;background:var(--cp-200)">238</div></div></div>
        <div class="hb"><div class="hb-l">鸿蒙-AI</div><div class="hb-t"><div class="hb-f" style="width:65%;background:var(--gai)">343</div></div></div>
        <div class="hb"><div class="hb-l">小程序-AI</div><div class="hb-t"><div class="hb-f" style="width:52%;background:var(--gai)">274</div></div></div>
      </div>
      <div class="card">
        <div class="card-t" style="margin-bottom:16px">会员分布</div>
        <svg viewBox="0 0 200 200" style="width:180px;margin:0 auto;display:block">
          <circle cx="100" cy="100" r="70" fill="none" stroke="#E7E5E4" stroke-width="24"/>
          <circle cx="100" cy="100" r="70" fill="none" stroke="#78716C" stroke-width="24" stroke-dasharray="232 208" stroke-dashoffset="0" transform="rotate(-90 100 100)"/>
          <circle cx="100" cy="100" r="70" fill="none" stroke="#F59E0B" stroke-width="24" stroke-dasharray="140 300" stroke-dashoffset="-232" transform="rotate(-90 100 100)"/>
          <circle cx="100" cy="100" r="70" fill="none" stroke="#8B5CF6" stroke-width="24" stroke-dasharray="68 372" stroke-dashoffset="-372" transform="rotate(-90 100 100)"/>
          <text x="100" y="95" text-anchor="middle" fill="#1C1917" font-size="20" font-weight="700">1,284</text>
          <text x="100" y="115" text-anchor="middle" fill="#78716C" font-size="11">总用户</text>
        </svg>
        <div class="flex" style="justify-content:center;gap:24px;margin-top:16px">
          <span class="flex aic g1"><span style="width:8px;height:8px;border-radius:2px;background:var(--cn-500)"></span><span style="font-size:11px;color:var(--cn-600)">免费 53%</span></span>
          <span class="flex aic g1"><span style="width:8px;height:8px;border-radius:2px;background:var(--ca-orange)"></span><span style="font-size:11px;color:var(--cn-600)">标准 32%</span></span>
          <span class="flex aic g1"><span style="width:8px;height:8px;border-radius:2px;background:var(--ca-purple)"></span><span style="font-size:11px;color:var(--cn-600)">专业 15%</span></span>
        </div>
      </div>
    </div>

    <div class="tbl-wrap">
      <div class="card-t" style="padding:16px 20px">活跃用户 Top 10</div>
      <table class="tbl">
        <thead><tr><th>排名</th><th>用户</th><th>会员</th><th>创作数</th><th>通过率</th><th>成功率</th><th>互动</th></tr></thead>
        <tbody>
          <tr v-for="(u,i) in topUsers" :key="u.user">
            <td><span :style="{width:'22px',height:'22px',borderRadius:'6px',display:'inline-flex',alignItems:'center',justifyContent:'center',fontSize:'11px',fontWeight:700,background:i===0?'var(--cp-500)':i===1?'var(--cp-300)':'var(--ca-orange)',color:'#fff'}">{{ i+1 }}</span></td>
            <td>{{ u.user }}</td><td><span class="tag" :class="'tag-'+u.cls">{{ u.member }}</span></td>
            <td class="fnm">{{ u.count }}</td>
            <td style="color:var(--cs)">{{ u.reviewRate }}</td>
            <td style="color:var(--cs)">{{ u.pubRate }}</td>
            <td class="fnm">{{ u.engagement }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const topUsers = ref([
  { user:'138****6721', member:'专业版', cls:'p', count:342, reviewRate:'95%', pubRate:'99%', engagement:892 },
  { user:'177****9901', member:'专业版', cls:'p', count:287, reviewRate:'92%', pubRate:'98%', engagement:756 },
  { user:'182****2234', member:'标准版', cls:'o', count:234, reviewRate:'88%', pubRate:'97%', engagement:623 }
])
</script>
