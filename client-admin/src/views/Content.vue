<template>
  <div>
    <div class="flex aic jbt mb6">
      <div><div style="font-size:20px;font-weight:700;color:var(--cn-900)">内容管理</div><div style="font-size:13px;color:var(--cn-500)">全量内容查看与违规监控</div></div>
      <div class="flex aic g3">
        <button class="btn btn-d">违规词管理</button>
        <button class="btn btn-p">导出内容</button>
      </div>
    </div>

    <div style="background:#FEE2E2;border:1px solid #FECACA;border-radius:12px;padding:16px 20px;margin-bottom:24px;display:flex;align-items:center;gap:16px">
      <div style="width:36px;height:36px;border-radius:6px;background:#FEE2E2;display:flex;align-items:center;justify-content:center"><span style="color:#EF4444;font-weight:700">!</span></div>
      <div style="flex:1"><div style="font-size:13px;font-weight:600;color:#B91C1C">3条违规内容待处理</div><div style="font-size:11px;color:#DC2626">系统已自动标记，请及时审核</div></div>
      <button class="btn btn-d btn-sm">立即处理</button>
    </div>

    <div class="tbl-wrap">
      <div class="tbl-bar">
        <div class="tbl-bar-l">
          <select class="ipt" style="width:120px;padding:4px 12px"><option>全部状态</option></select>
        </div>
        <span style="font-size:11px;color:var(--cn-400)">共 52,647 条</span>
      </div>
      <table class="tbl">
        <thead><tr><th><input type="checkbox"></th><th>内容ID</th><th>用户</th><th>标题</th><th>Agent</th><th>终端</th><th>审核</th><th>发布</th><th>时间</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="c in contents" :key="c.id" :style="{ background: c.violation ? '#FEF2F2' : '' }">
            <td><input type="checkbox"></td>
            <td class="fnm" style="font-size:11px" :style="{ color: c.violation ? 'var(--ce)' : '' }">{{ c.id }}</td>
            <td>{{ c.user }}</td>
            <td style="max-width:200px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap">{{ c.title }}</td>
            <td><span class="tag tag-r">{{ c.agent }}</span></td>
            <td><span class="tag tag-b">{{ c.terminal }}</span></td>
            <td><span class="tag" :class="'tag-'+c.reviewCls"><span v-if="c.violation" class="sd sd-r"></span>{{ c.review }}</span></td>
            <td><span class="tag tag-n">{{ c.publish }}</span></td>
            <td style="font-size:11px">{{ c.time }}</td>
            <td><div class="flex aic g2"><button class="btn btn-sm btn-g">查看</button><button v-if="c.violation" class="btn btn-sm btn-d">删除</button></div></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const contents = ref([
  { id:'#8234', user:'138****6721', title:'春日野餐全攻略：从装备到美食', agent:'生活', terminal:'电脑', review:'已通过', reviewCls:'g', publish:'已发布', time:'05-22', violation:false },
  { id:'#8233', user:'155****8832', title:'减肥神药曝光！这款产品千万别碰', agent:'美食', terminal:'鸿蒙', review:'违规', reviewCls:'d', publish:'未发布', time:'05-22', violation:true },
  { id:'#8232', user:'186****3345', title:'周末 city walk 路线推荐', agent:'旅行', terminal:'小程序', review:'审核中', reviewCls:'b', publish:'未发布', time:'05-22', violation:false }
])
</script>
