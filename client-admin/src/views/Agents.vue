<template>
  <div>
    <!-- 页面标题 -->
    <div class="flex aic jbt mb6">
      <div>
        <div style="font-size:20px;font-weight:700;color:var(--cn-900)">Agent 管理</div>
        <div style="font-size:13px;color:var(--cn-500);margin-top:4px">创建和管理 AI 创作 Agent，配置提示词与模型绑定</div>
      </div>
      <button class="btn btn-p" @click="openAdd">
        <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M7 1V13M1 7H13" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
        新增 Agent
      </button>
    </div>

    <!-- 加载中 -->
    <div v-if="loading" style="display:flex;align-items:center;justify-content:center;min-height:200px">
      <span style="color:var(--cn-400);font-size:14px">加载中...</span>
    </div>

    <!-- 空状态 -->
    <div v-else-if="agents.length===0" style="text-align:center;padding:80px 0">
      <svg width="64" height="64" viewBox="0 0 64 64" fill="none" style="margin:0 auto 20px">
        <rect x="12" y="8" width="40" height="48" rx="6" stroke="#D6D3D1" stroke-width="2"/>
        <line x1="22" y1="22" x2="42" y2="22" stroke="#D6D3D1" stroke-width="2" stroke-linecap="round"/>
        <line x1="22" y1="30" x2="36" y2="30" stroke="#D6D3D1" stroke-width="2" stroke-linecap="round"/>
        <line x1="22" y1="38" x2="30" y2="38" stroke="#D6D3D1" stroke-width="2" stroke-linecap="round"/>
      </svg>
      <div style="font-size:15px;font-weight:600;color:var(--cn-600);margin-bottom:8px">还没有 Agent</div>
      <div style="font-size:13px;color:var(--cn-400);margin-bottom:24px">创建第一个创作 Agent，让 AI 帮你生成内容</div>
      <button class="btn btn-p" @click="openAdd">创建 Agent</button>
    </div>

    <!-- Agent 卡片列表 -->
    <div v-else style="display:grid;grid-template-columns:repeat(3,1fr);gap:20px">
      <div class="agent-card" v-for="a in agents" :key="a.id">
        <div class="flex aic jbt" style="margin-bottom:14px">
          <div class="flex aic g3">
            <div class="agent-icon" :style="{background:getIconBg(a)}">
              <svg width="18" height="18" viewBox="0 0 18 18" fill="none">
                <path d="M9 1.5L10.5 5H14.5L11.5 7.5L12.5 11.5L9 9L5.5 11.5L6.5 7.5L3.5 5H7.5L9 1.5Z" :fill="getStarColor(a)" opacity=".8"/>
              </svg>
            </div>
            <div>
              <div style="font-size:14px;font-weight:600;color:var(--cn-800)">{{ a.name }}</div>
              <div style="font-size:11px;color:var(--cn-400)">{{ getModelName(a.apiId) }}</div>
            </div>
          </div>
          <span class="tag" :class="a.status===0?'tag-n':'tag-g'">{{ a.status===0?'禁用':'启用' }}</span>
        </div>

        <div style="font-size:13px;color:var(--cn-600);line-height:1.7;margin-bottom:8px;min-height:36px">
          {{ a.description || '暂无描述' }}
        </div>

        <div class="flex aic g2 mb4" style="flex-wrap:wrap">
          <span :style="{fontSize:'11px',padding:'1px 8px',borderRadius:'10px',background:a.agentType===1?'#EDE9FE':'var(--cp-50)',color:a.agentType===1?'#8B5CF6':'var(--cp-500)'}">{{ a.agentType===1?'图片Agent':'文案Agent' }}</span>
          <span v-if="a.style" style="font-size:11px;color:var(--cp-500);background:var(--cp-50);padding:1px 8px;border-radius:10px">{{ a.style }}</span>
          <span v-if="a.domain" style="font-size:11px;color:var(--ca-purple);background:var(--ca-purple-l);padding:1px 8px;border-radius:10px">{{ a.domain }}</span>
        </div>

        <div class="flex aic jbt" style="border-top:1px solid var(--cn-100);padding-top:12px">
          <span style="font-size:11px;color:var(--cn-400)">排序 {{ a.sortOrder || 0 }}</span>
          <div class="flex aic g2">
            <button class="btn btn-sm btn-g" @click="openEdit(a)">编辑</button>
            <button class="btn btn-sm btn-d" @click="handleDelete(a)">删除</button>
          </div>
        </div>
      </div>

      <!-- 新增卡片 -->
      <div class="agent-card-add" @click="openAdd">
        <svg width="36" height="36" viewBox="0 0 36 36" fill="none">
          <circle cx="18" cy="18" r="14" stroke="#D6D3D1" stroke-width="2" stroke-dasharray="4 3"/>
          <path d="M18 10V26M10 18H26" stroke="#A8A29E" stroke-width="2" stroke-linecap="round"/>
        </svg>
        <span style="font-size:13px;color:var(--cn-400);margin-top:14px">添加新 Agent</span>
      </div>
    </div>

    <!-- 弹窗：新增 / 编辑 Agent -->
    <div v-if="showModal" class="modal-mask" @click.self="closeModal">
      <div class="modal-card">
        <div class="flex aic jbt" style="margin-bottom:24px">
          <div style="font-size:16px;font-weight:700;color:var(--cn-900)">{{ isEdit?'编辑 Agent':'新增 Agent' }}</div>
          <button @click="closeModal" style="width:32px;height:32px;border-radius:8px;display:flex;align-items:center;justify-content:center;color:var(--cn-400)">
            <svg width="16" height="16" viewBox="0 0 16 16" fill="none"><path d="M4 4L12 12M12 4L4 12" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
          </button>
        </div>

        <form @submit.prevent="handleSave">
          <div style="display:grid;grid-template-columns:1fr 1fr;gap:16px">
            <div>
              <label class="fl">Agent 名称 *</label>
              <input class="ipt" v-model="form.name" placeholder="如：美食探店 Agent" required />
            </div>
            <div>
              <label class="fl">Agent 类型 *</label>
              <select class="ipt" v-model.number="form.agentType">
                <option :value="0">文案 Agent</option>
                <option :value="1">图片 Agent</option>
              </select>
            </div>
            <div>
              <label class="fl">绑定 AI 模型</label>
              <select class="ipt" v-model="form.apiId">
                <option :value="null">不绑定（使用默认模型）</option>
                <option v-for="m in apiModels" :key="m.id" :value="m.id">{{ m.name }} ({{ m.modelName }})</option>
              </select>
            </div>
            <div style="grid-column:1/-1">
              <label class="fl">描述</label>
              <input class="ipt" v-model="form.description" placeholder="描述该 Agent 的用途和风格特点" />
            </div>
            <div>
              <label class="fl">创作风格</label>
              <input class="ipt" v-model="form.style" placeholder="如：轻松自然、专业严谨" />
            </div>
            <div>
              <label class="fl">内容领域</label>
              <input class="ipt" v-model="form.domain" placeholder="如：美食、旅行、时尚" />
            </div>
            <div style="grid-column:1/-1">
              <label class="fl">提示词模板</label>
              <textarea class="ipt" v-model="form.promptTemplate" rows="3" placeholder="自定义系统提示词，留空则使用默认模板&#10;如：你是一个小红书爆款文案创作专家..." style="resize:vertical"></textarea>
            </div>
            <div>
              <label class="fl">排序序号</label>
              <input class="ipt" type="number" v-model.number="form.sortOrder" placeholder="数字越小越靠前" />
            </div>
            <div>
              <label class="fl">状态</label>
              <select class="ipt" v-model.number="form.status">
                <option :value="1">启用</option>
                <option :value="0">禁用</option>
              </select>
            </div>
          </div>

          <div class="flex aic jbt" style="margin-top:24px">
            <button v-if="isEdit" type="button" class="btn btn-d" @click="handleDelete(currentAgent)" style="font-size:12px">删除此 Agent</button>
            <span v-else></span>
            <div class="flex aic g3">
              <button type="button" class="btn btn-g" @click="closeModal">取消</button>
              <button type="submit" class="btn btn-p" :disabled="saving">
                {{ saving?'保存中...':'保存' }}
              </button>
            </div>
          </div>
        </form>
      </div>
    </div>

    <!-- 删除确认弹窗 -->
    <div v-if="showDeleteConfirm" class="modal-mask" @click.self="showDeleteConfirm=false">
      <div class="modal-card" style="max-width:380px;text-align:center">
        <svg width="48" height="48" viewBox="0 0 48 48" fill="none" style="margin:0 auto 16px">
          <circle cx="24" cy="24" r="20" fill="#FEE2E2"/>
          <path d="M24 16V26M24 32H24.01" stroke="#EF4444" stroke-width="2.5" stroke-linecap="round"/>
        </svg>
        <div style="font-size:16px;font-weight:700;color:var(--cn-900);margin-bottom:8px">确认删除</div>
        <div style="font-size:13px;color:var(--cn-500);margin-bottom:24px">确定要删除 Agent「{{ deleteTarget?.name }}」吗？此操作不可撤销。</div>
        <div class="flex aic g3" style="justify-content:center">
          <button class="btn btn-g" @click="showDeleteConfirm=false">取消</button>
          <button class="btn btn-d" @click="confirmDelete" :disabled="deleting">{{ deleting?'删除中...':'确认删除' }}</button>
        </div>
      </div>
    </div>

    <!-- Toast -->
    <div v-if="toast.show" class="toast" :class="toast.type">{{ toast.msg }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { agentApi, aiModelApi } from '@/api/index'

const agents = ref([])
const apiModels = ref([])
const loading = ref(true)
const showModal = ref(false)
const isEdit = ref(false)
const currentAgent = ref(null)
const saving = ref(false)
const showDeleteConfirm = ref(false)
const deleteTarget = ref(null)
const deleting = ref(false)
const toast = ref({ show: false, msg: '', type: 'ok' })

const defaultForm = () => ({
  name: '',
  description: '',
  style: '',
  domain: '',
  apiId: null,
  promptTemplate: '',
  agentType: 0,
  sortOrder: 0,
  status: 1
})
const form = ref(defaultForm())

function showToast(msg, type='ok') {
  toast.value = { show: true, msg, type }
  setTimeout(() => { toast.value.show = false }, 2500)
}

const iconColors = ['#FFF1F3','#EDE9FE','#DCFCE7','#FEF3C7','#DBEAFE']
function getIconBg(a) {
  const idx = (a.id || 0) % iconColors.length
  return iconColors[idx]
}
const starColors = ['#FE2C55','#8B5CF6','#22C55E','#F59E0B','#3B82F6']
function getStarColor(a) {
  const idx = (a.id || 0) % starColors.length
  return starColors[idx]
}

function getModelName(apiId) {
  if (!apiId) return '默认模型'
  const m = apiModels.value.find(x => x.id === apiId)
  return m ? m.name : '未知模型'
}

async function loadData() {
  loading.value = true
  try {
    const [agentRes, apiRes] = await Promise.all([agentApi.list(), aiModelApi.list()])
    agents.value = agentRes.data?.data || []
    apiModels.value = apiRes.data?.data || []
  } catch (e) {
    showToast('加载失败: ' + (e.response?.data?.msg || e.message), 'err')
  } finally {
    loading.value = false
  }
}

function openAdd() {
  isEdit.value = false
  currentAgent.value = null
  form.value = defaultForm()
  showModal.value = true
}

function openEdit(a) {
  isEdit.value = true
  currentAgent.value = a
  form.value = {
    name: a.name || '',
    description: a.description || '',
    style: a.style || '',
    domain: a.domain || '',
    apiId: a.apiId || null,
    promptTemplate: a.promptTemplate || '',
    agentType: a.agentType ?? 0,
    sortOrder: a.sortOrder || 0,
    status: a.status ?? 1
  }
  showModal.value = true
}

function closeModal() {
  showModal.value = false
}

async function handleSave() {
  if (!form.value.name.trim()) return showToast('请输入 Agent 名称', 'err')
  saving.value = true
  try {
    if (isEdit.value) {
      await agentApi.update(currentAgent.value.id, form.value)
      showToast('更新成功')
    } else {
      await agentApi.create(form.value)
      showToast('创建成功')
    }
    closeModal()
    await loadData()
  } catch (e) {
    showToast('保存失败: ' + (e.response?.data?.msg || e.message), 'err')
  } finally {
    saving.value = false
  }
}

function handleDelete(a) {
  deleteTarget.value = a
  showDeleteConfirm.value = true
}

async function confirmDelete() {
  if (!deleteTarget.value) return
  deleting.value = true
  try {
    await agentApi.delete(deleteTarget.value.id)
    showToast(`已删除「${deleteTarget.value.name}」`)
    showDeleteConfirm.value = false
    if (showModal.value) closeModal()
    await loadData()
  } catch (e) {
    showToast('删除失败: ' + (e.response?.data?.msg || e.message), 'err')
  } finally {
    deleting.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.agent-card{
  background:#fff; border-radius:12px; padding:20px;
  box-shadow:var(--shs); border:1px solid var(--cn-100);
  transition: transform .2s, box-shadow .2s;
}
.agent-card:hover{ transform:translateY(-2px); box-shadow:var(--shm) }
.agent-icon{
  width:40px; height:40px; border-radius:8px;
  display:flex; align-items:center; justify-content:center; flex-shrink:0
}
.agent-card-add{
  border:2px dashed var(--cn-300); border-radius:12px;
  display:flex; flex-direction:column; align-items:center;
  justify-content:center; min-height:200px; cursor:pointer;
  background:transparent; transition: all .2s
}
.agent-card-add:hover{ border-color:var(--cp-300); background:var(--cp-50) }
.fl{ display:block; font-size:12px; font-weight:500; color:var(--cn-600); margin-bottom:6px }

/* modal */
.modal-mask{
  position:fixed; top:0; left:0; right:0; bottom:0;
  background:rgba(0,0,0,.35); display:flex; align-items:center;
  justify-content:center; z-index:9999;
}
.modal-card{
  background:#fff; border-radius:16px; padding:28px; width:560px; max-width:90vw;
  max-height:90vh; overflow-y:auto; box-shadow:var(--shl)
}
.modal-card .btn-g:hover{ background:var(--cn-100) }

/* toast */
.toast{
  position:fixed; top:24px; left:50%; transform:translateX(-50%);
  padding:10px 24px; border-radius:8px; font-size:13px; font-weight:500;
  z-index:99999; animation: toastIn .25s ease;
}
.toast.ok{ background:#DCFCE7; color:#15803D }
.toast.err{ background:#FEE2E2; color:#B91C1C }
@keyframes toastIn{ from{opacity:0;transform:translateX(-50%) translateY(-8px)} to{opacity:1;transform:translateX(-50%) translateY(0)} }
</style>
