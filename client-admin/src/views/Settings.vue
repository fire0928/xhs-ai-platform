<template>
  <div>
    <div style="font-size:20px;font-weight:700;color:var(--cn-900);margin-bottom:4px">系统设置</div>
    <p style="font-size:13px;color:var(--cn-500);margin-bottom:24px">全局配置与安全设置</p>

    <div style="display:grid;grid-template-columns:1fr 1fr;gap:20px;margin-bottom:20px">
      <div class="card">
        <div class="card-t" style="margin-bottom:16px">安全设置</div>
        <div style="margin-bottom:16px"><label style="display:block;font-size:13px;font-weight:500;color:var(--cn-700);margin-bottom:6px">管理员密码策略</label><select class="ipt"><option>强密码</option></select></div>
        <div style="margin-bottom:16px"><label style="display:block;font-size:13px;font-weight:500;color:var(--cn-700);margin-bottom:6px">登录失败锁定</label><div class="flex aic g3"><input class="ipt" type="number" value="5" style="width:80px"><span style="font-size:13px;color:var(--cn-600)">次</span></div></div>
        <div style="margin-bottom:16px"><label style="display:block;font-size:13px;font-weight:500;color:var(--cn-700);margin-bottom:6px">两步验证</label><div class="flex aic g3"><div class="tgl on" @click="$event.target.classList.toggle('on')"></div><span style="font-size:13px;color:var(--cn-600)">已启用</span></div></div>
      </div>
      <div class="card">
        <div class="card-t" style="margin-bottom:16px">系统配置</div>
        <div style="margin-bottom:16px"><label style="display:block;font-size:13px;font-weight:500;color:var(--cn-700);margin-bottom:6px">加密方式</label><select class="ipt"><option>AES-256-GCM</option></select></div>
        <div style="margin-bottom:16px"><label style="display:block;font-size:13px;font-weight:500;color:var(--cn-700);margin-bottom:6px">日志保留</label><input class="ipt" type="number" value="90" style="width:120px"><span style="font-size:11px;color:var(--cn-400);margin-left:8px">天</span></div>
        <div style="margin-bottom:16px"><label style="display:block;font-size:13px;font-weight:500;color:var(--cn-700);margin-bottom:6px">发布重试</label><div class="flex aic g3"><input class="ipt" type="number" value="2" style="width:80px"><span style="font-size:13px;color:var(--cn-600)">次</span></div></div>
      </div>
    </div>

    <div class="card"><div class="card-t" style="margin-bottom:16px">通知设置</div>
      <div style="display:grid;grid-template-columns:1fr 1fr 1fr;gap:20px">
        <label v-for="n in notifs" :key="n" class="flex aic g2" style="font-size:13px;color:var(--cn-600);cursor:pointer"><input type="checkbox" checked>{{ n }}</label>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const notifs = ['违规内容告警','API额度告警','发布失败告警','会员到期提醒','系统异常告警','每日数据汇总']
</script>

<style scoped>
.tgl{width:36px;height:20px;background:var(--cn-300);border-radius:10px;position:relative;cursor:pointer;transition:background .2s}
.tgl.on{background:var(--cp-500)}
.tgl::after{content:'';width:16px;height:16px;background:#fff;border-radius:50%;position:absolute;top:2px;left:2px;transition:transform .2s;box-shadow:0 1px 2px rgba(0,0,0,.04)}
.tgl.on::after{transform:translateX(16px)}
</style>
