<template>
  <div>
    <div class="flex aic jbt mb6">
      <div><div style="font-size:20px;font-weight:700;color:var(--cn-900)">调用监控</div><div style="font-size:13px;color:var(--cn-500)">实时查看大模型 API 调用记录</div></div>
      <div class="flex aic g3">
        <select class="ipt" style="width:140px"><option>全部 API</option></select>
        <select class="ipt" style="width:120px"><option>今日</option></select>
        <button class="btn btn-p">导出</button>
      </div>
    </div>

    <div class="am-row">
      <div class="am"><div class="am-lb">今日总调用</div><div class="am-vl">5,246</div><div class="am-ch" style="color:var(--cs)">↑ 18.2%</div></div>
      <div class="am"><div class="am-lb">成功率</div><div class="am-vl" style="color:var(--cs)">99.2%</div><div class="am-ch">42次失败</div></div>
      <div class="am"><div class="am-lb">平均响应</div><div class="am-vl">2.3s</div><div class="am-ch" style="color:var(--cs)">↓ 0.4s</div></div>
      <div class="am"><div class="am-lb">预估费用</div><div class="am-vl">¥286</div><div class="am-ch" style="color:var(--ca-orange)">↑ 12%</div></div>
    </div>

    <div class="tbl-wrap">
      <table class="tbl">
        <thead><tr><th>时间</th><th>用户</th><th>Agent</th><th>API</th><th>终端</th><th>结果</th><th>耗时</th><th>消耗</th></tr></thead>
        <tbody>
          <tr v-for="r in records" :key="r.time">
            <td style="font-size:11px;color:var(--cn-400)">{{ r.time }}</td><td>{{ r.user }}</td>
            <td>{{ r.agent }}</td><td>{{ r.api }}</td>
            <td><span class="tag" :class="'tag-'+r.terminalCls">{{ r.terminal }}</span></td>
            <td><span class="tag" :class="'tag-'+r.resultCls">{{ r.result }}</span></td>
            <td class="fnm" style="font-size:11px">{{ r.elapsed }}</td>
            <td class="fnm" style="font-size:11px">{{ r.cost }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const records = ref([
  { time:'10:32:15', user:'138****6721', agent:'生活分享', api:'GPT-4o', terminal:'电脑', terminalCls:'b', result:'成功', resultCls:'g', elapsed:'1.8s', cost:'$0.02' },
  { time:'10:31:48', user:'155****8832', agent:'美食探店', api:'文心一言', terminal:'鸿蒙', terminalCls:'r', result:'成功', resultCls:'g', elapsed:'2.1s', cost:'¥0.03' },
  { time:'10:30:22', user:'186****3345', agent:'旅行攻略', api:'GPT-4o', terminal:'小程序', terminalCls:'g', result:'失败', resultCls:'d', elapsed:'10.0s', cost:'-' },
])
</script>
