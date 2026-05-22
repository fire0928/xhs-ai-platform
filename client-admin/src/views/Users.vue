<template>
  <div>
    <div class="flex aic jbt mb6">
      <div><div style="font-size:20px;font-weight:700;color:var(--cn-900)">用户管理</div><div style="font-size:13px;color:var(--cn-500)">管理所有注册用户</div></div>
      <div class="flex aic g3">
        <button class="btn btn-g">新增用户</button>
        <button class="btn btn-p">导出用户</button>
      </div>
    </div>

    <div class="am-row">
      <div class="am"><div class="am-lb">总用户</div><div class="am-vl">1,284</div><div class="am-ch" style="color:var(--cs)">↑ 12.3%</div></div>
      <div class="am"><div class="am-lb">活跃(7天)</div><div class="am-vl">856</div><div class="am-ch" style="color:var(--cs)">↑ 8.5%</div></div>
      <div class="am"><div class="am-lb">今日新增</div><div class="am-vl">19</div><div class="am-ch" style="color:var(--cs)">↑ 5</div></div>
      <div class="am"><div class="am-lb">已禁用</div><div class="am-vl" style="color:var(--ce)">7</div><div class="am-ch" style="color:var(--cn-500)">占比 0.5%</div></div>
    </div>

    <div class="tbl-wrap">
      <div class="tbl-bar">
        <div class="tbl-bar-l">
          <div style="display:flex;align-items:center;gap:8px;background:var(--cn-100);border-radius:8px;padding:4px 12px;width:200px"><svg width="14" height="14" viewBox="0 0 14 14" fill="none"><circle cx="6" cy="6" r="4.5" stroke="currentColor" stroke-width="1.5"/><path d="M9.5 9.5L13 13" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg><input type="text" placeholder="搜索手机号..." style="border:none;background:none;outline:none;font-size:11px;width:100%"></div>
          <select class="ipt" style="width:120px;padding:4px 12px"><option>全部状态</option><option>正常</option><option>已禁用</option></select>
        </div>
        <span style="font-size:11px;color:var(--cn-400)">共 1,284 条</span>
      </div>
      <table class="tbl">
        <thead><tr><th><input type="checkbox"></th><th>用户ID</th><th>手机号</th><th>会员</th><th>绑定账号</th><th>终端</th><th>注册时间</th><th>状态</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="u in users" :key="u.id">
            <td><input type="checkbox"></td><td class="fnm" style="font-size:12px">{{ u.id }}</td>
            <td>{{ u.phone }}</td><td><span class="tag" :class="'tag-'+u.memberCls">{{ u.member }}</span></td>
            <td>{{ u.accounts }}</td><td><span class="tag" :class="'tag-'+u.terminalCls">{{ u.terminal }}</span></td>
            <td style="font-size:12px;color:var(--cn-500)">{{ u.registerTime }}</td>
            <td><span class="tag" :class="'tag-'+u.statusCls"><span class="sd" :class="'sd-'+u.statusDot"></span>{{ u.status }}</span></td>
            <td><div class="flex aic g2"><button class="btn btn-sm btn-g">编辑</button><button class="btn btn-sm" style="color:var(--ce)">禁用</button></div></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const users = ref([
  { id:'1001',phone:'138****6721',member:'专业版',memberCls:'p',accounts:'2个',terminal:'电脑端',terminalCls:'b',registerTime:'2026-03-15',status:'正常',statusCls:'g',statusDot:'g' },
  { id:'1002',phone:'155****8832',member:'标准版',memberCls:'o',accounts:'1个',terminal:'鸿蒙端',terminalCls:'r',registerTime:'2026-04-02',status:'正常',statusCls:'g',statusDot:'g' },
  { id:'1003',phone:'186****3345',member:'免费版',memberCls:'n',accounts:'0个',terminal:'小程序',terminalCls:'g',registerTime:'2026-05-01',status:'正常',statusCls:'g',statusDot:'g' },
  { id:'1005',phone:'139****5567',member:'免费版',memberCls:'n',accounts:'1个',terminal:'鸿蒙端',terminalCls:'r',registerTime:'2026-05-10',status:'已禁用',statusCls:'d',statusDot:'r' },
])
</script>
