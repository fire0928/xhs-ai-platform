<template>
  <div class="settings-page">
    <div class="page-header">
      <h2>个人设置</h2>
      <p class="sub">管理个人信息、通知偏好与安全设置</p>
    </div>

    <div class="settings-sections">
      <!-- 个人信息 -->
      <div class="section-card">
        <div class="section-title">
          <svg width="18" height="18" viewBox="0 0 18 18" fill="none"><circle cx="9" cy="7" r="3.5" stroke="var(--cp-500)" stroke-width="1.5" fill="none"/><path d="M3 16c0-3.3 3.1-6 7-6s7 2.7 7 6" stroke="var(--cp-500)" stroke-width="1.5" stroke-linecap="round" fill="none"/></svg>
          个人信息
        </div>
        <div class="form-item">
          <span class="item-label">头像</span>
          <div class="item-value">
            <div class="avatar-preview" :style="{ background: 'var(--gb)' }">
              {{ userInfo?.nickname?.charAt(0) || '我' }}
            </div>
            <button class="btn btn-ghost btn-sm">更换头像</button>
          </div>
        </div>
        <div class="form-item">
          <span class="item-label">昵称</span>
          <div class="item-value">
            <input class="ipt-sm" v-model="userInfo.nickname" placeholder="输入昵称" />
          </div>
        </div>
        <div class="form-item">
          <span class="item-label">手机号</span>
          <div class="item-value text-muted">{{ userInfo?.phone || '138****6721' }}</div>
        </div>
        <div class="form-item">
          <span class="item-label">会员等级</span>
          <div class="item-value">
            <span class="tag" :class="memberTagClass">{{ memberLabel }}</span>
          </div>
        </div>
      </div>

      <!-- AI 偏好 -->
      <div class="section-card">
        <div class="section-title">
          <svg width="18" height="18" viewBox="0 0 18 18" fill="none"><path d="M9 2L10.5 5.5H14.5L11.25 8L12.75 12L9 9.5L5.25 12L6.75 8L3.5 5.5H7.5L9 2Z" fill="var(--ca-purple)" opacity=".7"/></svg>
          AI 偏好设置
        </div>
        <div class="form-item">
          <span class="item-label">默认 Agent</span>
          <div class="item-value">
            <select class="ipt-sm" v-model="aiPrefs.defaultAgent">
              <option value="">无偏好</option>
              <option value="1">生活分享</option>
              <option value="2">美食探店</option>
              <option value="3">旅行攻略</option>
              <option value="4">穿搭时尚</option>
            </select>
          </div>
        </div>
        <div class="form-item">
          <span class="item-label">内容风格</span>
          <div class="item-value">
            <select class="ipt-sm" v-model="aiPrefs.style">
              <option value="auto">自动匹配</option>
              <option value="casual">轻松随意</option>
              <option value="professional">专业严谨</option>
              <option value="trendy">潮流时尚</option>
              <option value="warm">温暖治愈</option>
            </select>
          </div>
        </div>
        <div class="form-item">
          <span class="item-label">每次生成数</span>
          <div class="item-value">
            <select class="ipt-sm" v-model="aiPrefs.generateCount">
              <option value="3">3 条标题</option>
              <option value="5">5 条标题</option>
              <option value="7">7 条标题</option>
            </select>
          </div>
        </div>
        <div class="form-item">
          <span class="item-label">配图数量</span>
          <div class="item-value">
            <select class="ipt-sm" v-model="aiPrefs.imageCount">
              <option value="3">3 张</option>
              <option value="5">5 张</option>
              <option value="9">9 张</option>
            </select>
          </div>
        </div>
      </div>

      <!-- 发布偏好 -->
      <div class="section-card">
        <div class="section-title">
          <svg width="18" height="18" viewBox="0 0 18 18" fill="none"><path d="M22 2L11 13" stroke="var(--cs)" stroke-width="2" stroke-linecap="round"/><path d="M22 2l-7 20-4-9-9-4 20-7z" stroke="var(--cs)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
          发布偏好
        </div>
        <div class="form-item">
          <span class="item-label">默认发布时间</span>
          <div class="item-value">
            <input class="ipt-sm" type="time" v-model="publishPrefs.defaultTime" style="width:140px" />
          </div>
        </div>
        <div class="form-item">
          <span class="item-label">发布时间区间</span>
          <div class="item-value">
            <input class="ipt-sm" type="time" v-model="publishPrefs.startTime" style="width:110px" />
            <span style="margin:0 8px;color:var(--cn-400)">至</span>
            <input class="ipt-sm" type="time" v-model="publishPrefs.endTime" style="width:110px" />
          </div>
        </div>
        <div class="form-item">
          <span class="item-label">最小间隔</span>
          <div class="item-value">
            <select class="ipt-sm" v-model="publishPrefs.interval">
              <option value="30">30 分钟</option>
              <option value="60">1 小时</option>
              <option value="120">2 小时</option>
            </select>
          </div>
        </div>
        <div class="form-item">
          <span class="item-label">每日上限</span>
          <div class="item-value">
            <select class="ipt-sm" v-model="publishPrefs.dailyLimit">
              <option value="3">3 篇</option>
              <option value="5">5 篇</option>
              <option value="8">8 篇</option>
            </select>
          </div>
        </div>
        <div class="form-item">
          <span class="item-label">发布后操作</span>
          <div class="item-value">
            <label class="checkbox-row">
              <input type="checkbox" v-model="publishPrefs.autoRecord" /> 自动记录发布数据
            </label>
          </div>
        </div>
      </div>

      <!-- 通知设置 -->
      <div class="section-card">
        <div class="section-title">
          <svg width="18" height="18" viewBox="0 0 18 18" fill="none"><path d="M4 7C4 4.79 6.24 3 9 3C11.76 3 14 4.79 14 7V11L15 14H3L4 11V7Z" stroke="var(--ca-orange)" stroke-width="1.5" fill="none"/><path d="M7 14C7 15.1 7.9 16 9 16C10.1 16 11 15.1 11 14" stroke="var(--ca-orange)" stroke-width="1.5"/></svg>
          通知设置
        </div>
        <div class="form-item">
          <span class="item-label">发布结果通知</span>
          <div class="item-value">
            <label class="toggle" :class="{ on: notifPrefs.publishResult }" @click="notifPrefs.publishResult = !notifPrefs.publishResult"></label>
            <span class="toggle-label">{{ notifPrefs.publishResult ? '已开启' : '已关闭' }}</span>
          </div>
        </div>
        <div class="form-item">
          <span class="item-label">审核结果通知</span>
          <div class="item-value">
            <label class="toggle" :class="{ on: notifPrefs.reviewResult }" @click="notifPrefs.reviewResult = !notifPrefs.reviewResult"></label>
            <span class="toggle-label">{{ notifPrefs.reviewResult ? '已开启' : '已关闭' }}</span>
          </div>
        </div>
        <div class="form-item">
          <span class="item-label">AI 创作完成通知</span>
          <div class="item-value">
            <label class="toggle" :class="{ on: notifPrefs.aiComplete }" @click="notifPrefs.aiComplete = !notifPrefs.aiComplete"></label>
            <span class="toggle-label">{{ notifPrefs.aiComplete ? '已开启' : '已关闭' }}</span>
          </div>
        </div>
        <div class="form-item">
          <span class="item-label">Cookie 过期提醒</span>
          <div class="item-value">
            <label class="toggle" :class="{ on: notifPrefs.cookieExpiry }" @click="notifPrefs.cookieExpiry = !notifPrefs.cookieExpiry"></label>
            <span class="toggle-label">{{ notifPrefs.cookieExpiry ? '已开启' : '已关闭' }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 保存按钮 -->
    <div class="save-bar">
      <button class="btn btn-outline" @click="resetAll">恢复默认</button>
      <button class="btn btn-primary" @click="saveSettings" :disabled="saving">
        <svg v-if="saving" width="16" height="16" viewBox="0 0 16 16" fill="none" style="animation:spin 1s linear infinite"><circle cx="8" cy="8" r="6" stroke="currentColor" stroke-width="2" fill="none" stroke-dasharray="28" stroke-linecap="round"/></svg>
        {{ saving ? '保存中...' : '保存设置' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/store'
import api from '@/api'

const userStore = useUserStore()
const userInfo = ref({ nickname: '创作者' })
const saving = ref(false)

const memberLabel = ref('免费版')
const memberTagClass = ref('tag-free')

const aiPrefs = reactive({ defaultAgent: '', style: 'auto', generateCount: '3', imageCount: '3' })
const publishPrefs = reactive({ defaultTime: '12:00', startTime: '08:00', endTime: '22:00', interval: '60', dailyLimit: '5', autoRecord: true })
const notifPrefs = reactive({ publishResult: true, reviewResult: true, aiComplete: true, cookieExpiry: true })

function resetAll() {
  aiPrefs.defaultAgent = ''; aiPrefs.style = 'auto'; aiPrefs.generateCount = '3'; aiPrefs.imageCount = '3'
  publishPrefs.defaultTime = '12:00'; publishPrefs.startTime = '08:00'; publishPrefs.endTime = '22:00'
  publishPrefs.interval = '60'; publishPrefs.dailyLimit = '5'; publishPrefs.autoRecord = true
  notifPrefs.publishResult = true; notifPrefs.reviewResult = true; notifPrefs.aiComplete = true; notifPrefs.cookieExpiry = true
}

async function saveSettings() {
  saving.value = true
  try {
    await api.put('/user/settings', {
      aiPrefs, publishPrefs, notifPrefs, nickname: userInfo.value.nickname
    })
  } catch { /* ignore */ }
  finally { saving.value = false }
}

onMounted(async () => {
  try {
    const res = await api.get('/user/profile')
    if (res.data.code === 200) {
      const d = res.data.data
      userInfo.value = d
      if (d.memberLevel === 'PRO') { memberLabel.value = '专业版'; memberTagClass.value = 'tag-pro' }
      else if (d.memberLevel === 'STD') { memberLabel.value = '标准版'; memberTagClass.value = 'tag-std' }
      if (d.settings) {
        Object.assign(aiPrefs, d.settings.aiPrefs || {})
        Object.assign(publishPrefs, d.settings.publishPrefs || {})
        Object.assign(notifPrefs, d.settings.notifPrefs || {})
      }
    }
  } catch { /* ignore */ }
})
</script>

<style scoped>
.settings-page { max-width: 800px; }
.page-header { margin-bottom: 24px; }
.page-header h2 { font-size: 20px; font-weight: 700; color: var(--cn-900); }
.page-header .sub { font-size: 13px; color: var(--cn-500); margin-top: 4px; }
.settings-sections { display: flex; flex-direction: column; gap: 16px; }
.section-card { background: #fff; border-radius: 12px; padding: 20px 24px; box-shadow: 0 1px 3px rgba(0,0,0,.06); border: 1px solid var(--cn-100); }
.section-title { display: flex; align-items: center; gap: 8px; font-size: 15px; font-weight: 600; color: var(--cn-800); padding-bottom: 16px; margin-bottom: 8px; border-bottom: 1px solid var(--cn-100); }
.form-item { display: flex; align-items: center; padding: 12px 0; border-bottom: 1px solid var(--cn-50); }
.form-item:last-child { border-bottom: none; }
.item-label { width: 140px; flex-shrink: 0; font-size: 13px; font-weight: 500; color: var(--cn-600); }
.item-value { display: flex; align-items: center; gap: 12px; flex: 1; font-size: 13px; }
.text-muted { color: var(--cn-400); }
.avatar-preview { width: 40px; height: 40px; border-radius: 50%; display: flex; align-items: center; justify-content: center; color: #fff; font-size: 16px; font-weight: 700; }
.ipt-sm { padding: 6px 10px; border: 1px solid var(--cn-300); border-radius: 6px; font-size: 13px; color: var(--cn-800); outline: none; background: #fff; }
.ipt-sm:focus { border-color: var(--cp-400); box-shadow: 0 0 0 3px rgba(254,44,85,.08); }
select.ipt-sm { appearance: none; background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath d='M3 4.5L6 7.5L9 4.5' stroke='%2378716C' stroke-width='1.5' stroke-linecap='round' fill='none'/%3E%3C/svg%3E"); background-repeat: no-repeat; background-position: right 8px center; padding-right: 28px; }
.tag { display: inline-flex; padding: 3px 10px; border-radius: 16px; font-size: 12px; font-weight: 600; }
.tag-free { background: var(--cn-100); color: var(--cn-600); }
.tag-std { background: var(--ca-orange-l); color: #B45309; }
.tag-pro { background: var(--ca-purple-l); color: var(--ca-purple); }
.toggle { width: 36px; height: 20px; background: var(--cn-300); border-radius: 10px; position: relative; cursor: pointer; transition: background .2s; }
.toggle.on { background: var(--cp-500); }
.toggle::after { content: ''; width: 16px; height: 16px; background: #fff; border-radius: 50%; position: absolute; top: 2px; left: 2px; transition: transform .2s; box-shadow: 0 1px 2px rgba(0,0,0,.04); }
.toggle.on::after { transform: translateX(16px); }
.toggle-label { font-size: 12px; color: var(--cn-400); }
.checkbox-row { display: flex; align-items: center; gap: 6px; cursor: pointer; color: var(--cn-600); }
.checkbox-row input { accent-color: var(--cp-500); }
.save-bar { display: flex; justify-content: flex-end; gap: 12px; margin-top: 24px; padding: 16px 0; }

.btn { display: inline-flex; align-items: center; gap: 6px; padding: 8px 20px; border-radius: 8px; font-size: 13px; font-weight: 600; cursor: pointer; transition: all .15s; }
.btn-primary { background: var(--gb); color: #fff; box-shadow: 0 2px 8px rgba(254,44,85,.25); }
.btn-primary:hover { transform: translateY(-1px); }
.btn-primary:disabled { opacity: .5; cursor: not-allowed; }
.btn-outline { background: #fff; color: var(--cp-500); border: 1.5px solid var(--cp-400); }
.btn-outline:hover { background: var(--cp-50); }
.btn-ghost { background: transparent; color: var(--cn-600); }
.btn-ghost:hover { background: var(--cn-100); }
.btn-sm { padding: 4px 10px; font-size: 12px; }

@keyframes spin { to { transform: rotate(360deg); } }
</style>
