<template>
  <aside class="sb">
    <div class="sb-brand">
      <div class="sb-brand-icon">
        <svg viewBox="0 0 16 16" fill="none"><path d="M8 2L12 5.5V10.5L8 14L4 10.5V5.5L8 2Z" fill="white" opacity=".9"/><path d="M8 4L10.5 6V10L8 12L5.5 10V6L8 4Z" fill="white"/><circle cx="8" cy="8" r="1.5" fill="rgba(254,44,85,.6)"/></svg>
      </div>
      <span class="sb-brand-text">红书智创</span>
      <span class="sb-brand-badge">AI</span>
    </div>
    <nav class="sb-nav">
      <div class="sb-lbl">工作台</div>
      <router-link to="/dashboard" class="sb-item">
        <svg viewBox="0 0 18 18" fill="none"><rect x="2" y="2" width="6" height="6" rx="1.5" fill="currentColor" opacity=".6"/><rect x="10" y="2" width="6" height="6" rx="1.5" fill="currentColor" opacity=".4"/><rect x="2" y="10" width="6" height="6" rx="1.5" fill="currentColor" opacity=".4"/><rect x="10" y="10" width="6" height="6" rx="1.5" fill="currentColor" opacity=".6"/></svg>
        <span>数据看板</span>
      </router-link>
      <router-link to="/ai-create" class="sb-item">
        <svg viewBox="0 0 18 18" fill="none"><path d="M9 2L11 7H16L12 10.5L13.5 16L9 12.5L4.5 16L6 10.5L2 7H7L9 2Z" fill="currentColor" opacity=".6"/></svg>
        <span>AI 创作</span><span class="ai-b">AI</span>
      </router-link>
      <router-link to="/review" class="sb-item">
        <svg viewBox="0 0 18 18" fill="none"><path d="M3 3H15C15.55 3 16 3.45 16 4V14C16 14.55 15.55 15 15 15H3C2.45 15 2 14.55 2 14V4C2 3.45 2.45 3 3 3Z" stroke="currentColor" stroke-width="1.5" fill="none"/><path d="M6 7H12M6 10H10" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
        <span>图文审核</span><span class="cnt-b">5</span>
      </router-link>
      <router-link to="/queue" class="sb-item">
        <svg viewBox="0 0 18 18" fill="none"><rect x="2" y="3" width="14" height="2.5" rx="1" fill="currentColor" opacity=".5"/><rect x="2" y="7.75" width="14" height="2.5" rx="1" fill="currentColor" opacity=".5"/><rect x="2" y="12.5" width="14" height="2.5" rx="1" fill="currentColor" opacity=".5"/><circle cx="5" cy="4.25" r="1" fill="currentColor"/><circle cx="5" cy="9" r="1" fill="currentColor"/><circle cx="5" cy="13.75" r="1" fill="currentColor"/></svg>
        <span>发布队列</span>
      </router-link>
      <router-link to="/analytics" class="sb-item">
        <svg viewBox="0 0 18 18" fill="none"><path d="M3 15V8M7 15V5M11 15V10M15 15V3" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
        <span>数据分析</span>
      </router-link>
      <div class="sb-lbl">系统</div>
      <router-link to="/accounts" class="sb-item">
        <svg viewBox="0 0 18 18" fill="none"><circle cx="7" cy="7" r="3.5" stroke="currentColor" stroke-width="1.5" fill="none"/><path d="M1 15C1 12 3.5 10 7 10C10.5 10 13 12 13 15" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" fill="none"/><circle cx="14" cy="6" r="2.5" stroke="currentColor" stroke-width="1.2" fill="none" opacity=".5"/><path d="M14 10C16 10 17 11.5 17 13" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" fill="none" opacity=".5"/></svg>
        <span>账号绑定</span>
      </router-link>
      <router-link to="/settings" class="sb-item">
        <svg viewBox="0 0 18 18" fill="none"><circle cx="9" cy="9" r="2.5" stroke="currentColor" stroke-width="1.5" fill="none"/><path d="M9 1.5V3.5M9 14.5V16.5M1.5 9H3.5M14.5 9H16.5M3.4 3.4L4.8 4.8M13.2 13.2L14.6 14.6M14.6 3.4L13.2 4.8M4.8 13.2L3.4 14.6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
        <span>个人设置</span>
      </router-link>
    </nav>
    <div class="sb-user">
      <div class="sb-avatar">小</div>
      <div class="sb-user-info">
        <div class="sb-user-name">小红创作者</div>
        <div class="sb-user-plan">专业版 · 到期 2026/12</div>
      </div>
    </div>
  </aside>
</template>

<style scoped>
.sb { width: var(--sw); min-width: var(--sw); background: #fff; border-right: 1px solid var(--cn-200); display: flex; flex-direction: column; z-index: 10; }
.sb-brand { height: var(--hh); display: flex; align-items: center; padding: 0 20px; gap: 12px; border-bottom: 1px solid var(--cn-100); }
.sb-brand-icon { width: 28px; height: 28px; background: var(--gai); border-radius: 6px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.sb-brand-icon svg { width: 16px; height: 16px; }
.sb-brand-text { font-size: 14px; font-weight: 600; color: var(--cn-900); white-space: nowrap; }
.sb-brand-badge { font-size: 11px; background: linear-gradient(135deg, #FFE0E5, #EDE9FE); color: var(--cp-600); padding: 2px 6px; border-radius: 9999px; font-weight: 500; white-space: nowrap; }
.sb-nav { flex: 1; padding: 12px; overflow-y: auto; }
.sb-lbl { font-size: 11px; font-weight: 600; color: var(--cn-500); text-transform: uppercase; letter-spacing: .05em; padding: 16px 12px 4px; }
.sb-item { display: flex; align-items: center; gap: 12px; padding: 8px 12px; border-radius: 6px; color: var(--cn-600); font-size: 13px; font-weight: 500; cursor: pointer; transition: all .15s; position: relative; margin-bottom: 2px; text-decoration: none; }
.sb-item:hover { background: var(--cn-100); color: var(--cn-800); }
.sb-item.router-link-active { background: linear-gradient(90deg, #FFF1F3, transparent); color: var(--cp-600); font-weight: 600; }
.sb-item.router-link-active::before { content: ''; position: absolute; left: -12px; top: 4px; bottom: 4px; width: 3px; background: var(--cp-500); border-radius: 0 2px 2px 0; }
.sb-item svg { width: 18px; height: 18px; flex-shrink: 0; }
.ai-b { background: var(--gai); color: #fff; font-size: 9px; padding: 1px 5px; border-radius: 9999px; font-weight: 700; }
.cnt-b { background: var(--cp-500); color: #fff; font-size: 10px; min-width: 18px; height: 18px; border-radius: 9999px; display: flex; align-items: center; justify-content: center; font-weight: 600; margin-left: auto; }
.sb-user { padding: 16px; border-top: 1px solid var(--cn-100); display: flex; align-items: center; gap: 12px; }
.sb-avatar { width: 32px; height: 32px; border-radius: 9999px; background: var(--gb); display: flex; align-items: center; justify-content: center; color: #fff; font-size: 13px; font-weight: 700; flex-shrink: 0; }
.sb-user-info { flex: 1; min-width: 0; }
.sb-user-name { font-size: 13px; font-weight: 600; color: var(--cn-800); }
.sb-user-plan { font-size: 11px; color: var(--cn-400); }
</style>
