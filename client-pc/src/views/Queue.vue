<template>
  <div>
    <div class="header">
      <div>
        <h2 class="h-xl">发布队列</h2>
        <p class="sub">管理内容发布计划，拖拽调整顺序</p>
      </div>
      <button class="btn btn-primary"><svg width="16" height="16" viewBox="0 0 16 16" fill="none"><path d="M8 3V13M3 8H13" stroke="white" stroke-width="1.5" stroke-linecap="round"/></svg>添加到队列</button>
    </div>

    <div class="ql">
      <div>
        <div class="qdg">
          <div class="qdh"><span class="dd"></span> 今天 · 6月15日 周日</div>
          <QueueCard v-for="item in todayItems" :key="item.id" :title="item.title" :time="item.time" :status="item.status" :bg="item.bg" />
        </div>
        <div class="qdg">
          <div class="qdh"><span class="dd orange"></span> 明天 · 6月16日 周一</div>
          <QueueCard v-for="item in tomorrowItems" :key="item.id" :title="item.title" :time="item.time" :status="item.status" :bg="item.bg" />
        </div>
      </div>

      <div class="qs">
        <h4>队列统计</h4>
        <div class="qs-it"><span class="qs-lb">今日待发</span><span class="qs-vl">3</span></div>
        <div class="qs-it"><span class="qs-lb">本周计划</span><span class="qs-vl">12</span></div>
        <div class="qs-it"><span class="qs-lb">发布成功率</span><span class="qs-vl" style="color:var(--cs)">96%</span></div>
        <div class="qs-bar">
          <div class="qs-bar-lb">本周完成进度</div>
          <div class="qs-bar-tr"><div class="qs-bar-fl" style="width:65%;background:var(--gb)"></div></div>
        </div>
        <div class="qs-bar" style="margin-top:12px">
          <div class="qs-bar-lb">AI 创作占比</div>
          <div class="qs-bar-tr"><div class="qs-bar-fl" style="width:78%;background:var(--gai)"></div></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import QueueCard from './components/QueueCard.vue'
const todayItems = [
  { id: 1, title: '春日樱花限定甜品', time: '预定 10:00 发布', status: 'sched', bg: '#FFE0E5' },
  { id: 2, title: '周末city walk路线推荐', time: '预定 14:00 发布', status: 'pend', bg: '#EDE9FE' },
  { id: 3, title: '居家好物分享', time: '预定 18:00 发布', status: 'pubg', bg: '#FEF3C7' },
]
const tomorrowItems = [
  { id: 4, title: '健康轻食搭配一周食谱', time: '预定 09:00 发布', status: 'pend', bg: '#DCFCE7' },
]
</script>

<style scoped>
.header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px; }
.h-xl { font-size: 20px; font-weight: 700; color: var(--cn-900); margin-bottom: 4px; }
.sub { font-size: 13px; color: var(--cn-500); }
.btn { display: inline-flex; align-items: center; justify-content: center; gap: 8px; padding: 8px 16px; border-radius: 8px; font-size: 13px; font-weight: 500; cursor: pointer; transition: all .15s; }
.btn svg { width: 16px; height: 16px; }
.btn-primary { background: var(--cp-500); color: #fff; }
.btn-primary:hover { background: var(--cp-600); }

.ql { display: grid; grid-template-columns: 1fr 280px; gap: 20px; }
.qdg { margin-bottom: 24px; }
.qdh { font-size: 13px; font-weight: 600; color: var(--cn-500); margin-bottom: 12px; display: flex; align-items: center; gap: 8px; }
.dd { width: 6px; height: 6px; border-radius: 50%; background: var(--cp-500); }
.dd.orange { background: var(--ca-orange); }

.qs { background: #fff; border-radius: 12px; padding: 20px; box-shadow: 0 1px 3px rgba(0,0,0,.06); border: 1px solid var(--cn-100); position: sticky; top: 0; }
.qs h4 { font-size: 14px; font-weight: 600; color: var(--cn-800); margin-bottom: 20px; }
.qs-it { display: flex; align-items: center; justify-content: space-between; padding: 12px 0; border-bottom: 1px solid var(--cn-100); }
.qs-it:last-child { border-bottom: none; }
.qs-lb { font-size: 13px; color: var(--cn-500); }
.qs-vl { font-family: 'JetBrains Mono', monospace; font-size: 16px; font-weight: 700; color: var(--cn-800); }
.qs-bar { margin-top: 16px; }
.qs-bar-lb { font-size: 11px; color: var(--cn-500); margin-bottom: 8px; }
.qs-bar-tr { height: 8px; background: var(--cn-100); border-radius: 9999px; overflow: hidden; }
.qs-bar-fl { height: 100%; border-radius: 9999px; transition: width .5s ease; }
</style>
