<template>
  <div>
    <!-- 页面标题 -->
    <div class="flex aic jbt mb6">
      <div>
        <div style="font-size:20px;font-weight:700;color:var(--cn-900)">AI 大模型管理</div>
        <div style="font-size:13px;color:var(--cn-500)">配置大模型 API、密钥与调用参数</div>
      </div>
      <button class="btn btn-p" @click="openAddModal">
        <svg viewBox="0 0 16 16" fill="none" style="width:14px;height:14px;margin-right:4px"><path d="M8 2V14M2 8H14" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
        新增 API
      </button>
    </div>

    <!-- API 列表 -->
    <div v-for="api in apis" :key="api.id" class="api-card">
      <div class="api-card-h">
        <div class="flex aic g3">
          <span class="sd" :class="api.status === 1 ? 'sd-g' : 'sd-r'"></span>
          <span style="font-size:14px;font-weight:600;color:var(--cn-800)">{{ api.name }}</span>
          <span class="tag" :class="providerTagCls(api.provider)">{{ providerLabel(api.provider) }}</span>
          <span v-if="api.modelName" style="font-size:11px;color:var(--cn-400)">{{ api.modelName }}</span>
        </div>
        <div class="flex aic g2">
          <button class="btn btn-sm btn-g" @click="editApi(api)">编辑</button>
          <button class="btn btn-sm" style="color:var(--ce)" @click="deleteApi(api)">删除</button>
        </div>
      </div>
      <div style="display:grid;grid-template-columns:1fr 1fr;gap:12px;font-size:11px;color:var(--cn-500)">
        <span>地址：{{ api.apiUrl }}</span>
        <span>限制：{{ api.rateLimit || 60 }}次/分</span>
        <span>密钥：{{ maskKey(api.apiKey) }}</span>
        <span>今日调用：{{ api.todayCall || 0 }}</span>
      </div>
      <div style="margin-top:12px">
        <div class="flex aic jbt" style="margin-bottom:4px">
          <span style="font-size:11px;color:var(--cn-500)">额度使用</span>
          <span class="fnm" style="font-size:11px;color:var(--cn-600);font-weight:600">{{ formatTokens(api.usedTokens) }} / {{ formatTokens(api.totalTokens) }}</span>
        </div>
        <div style="height:6px;background:var(--cn-100);border-radius:3px">
          <div style="height:100%;border-radius:3px;background:var(--gai)" :style="{width: tokenPct(api)+'%'}"></div>
        </div>
      </div>
    </div>

    <div v-if="apis.length === 0" style="text-align:center;padding:60px;color:var(--cn-400)">
      <div style="font-size:14px;margin-bottom:8px">暂无 API 配置</div>
      <div style="font-size:12px">点击右上角「新增 API」添加第一个大模型接口</div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-card" style="width:560px">
        <div class="flex aic jbt mb4">
          <div class="card-t">{{ editingId ? '编辑 API' : '新增 API' }}</div>
          <button class="btn btn-sm btn-g" @click="closeModal" style="font-size:16px;width:28px;height:28px">&times;</button>
        </div>

        <!-- 预设模板选择 -->
        <div v-if="!editingId" style="margin-bottom:16px">
          <label style="display:block;font-size:13px;font-weight:500;color:var(--cn-700);margin-bottom:8px">快速选择预设模板</label>
          <div style="display:grid;grid-template-columns:1fr 1fr 1fr;gap:8px">
            <div v-for="tpl in presets" :key="tpl.provider"
                 class="preset-card"
                 :class="{active: selectedPreset === tpl.provider}"
                 @click="applyPreset(tpl)">
              <div style="font-size:13px;font-weight:600;color:var(--cn-800)">{{ tpl.name }}</div>
              <div style="font-size:11px;color:var(--cn-400);margin-top:2px">{{ tpl.modelName }}</div>
            </div>
          </div>
        </div>

        <div style="max-height:460px;overflow-y:auto">
          <div class="flex aic g3 mb3">
            <span class="hb-l">API名称</span>
            <input v-model="form.name" class="ipt" placeholder="如：GPT-4o、豆包Pro等">
          </div>
          <div class="flex aic g3 mb3">
            <span class="hb-l">厂商</span>
            <select v-model="form.provider" class="ipt" style="width:200px">
              <option value="openai">OpenAI</option>
              <option value="doubao">字节豆包</option>
              <option value="baidu">百度文心</option>
              <option value="aliyun">阿里通义</option>
              <option value="deepseek">DeepSeek</option>
              <option value="other">其他</option>
            </select>
          </div>
          <div class="flex aic g3 mb3">
            <span class="hb-l">API地址</span>
            <input v-model="form.apiUrl" class="ipt" placeholder="https://api.example.com/v1">
          </div>
          <div class="flex aic g3 mb3">
            <span class="hb-l">API密钥</span>
            <input v-model="form.apiKey" class="ipt" type="password" placeholder="sk-... 或 AK...">
          </div>
          <div class="flex aic g3 mb3">
            <span class="hb-l">模型名称</span>
            <input v-model="form.modelName" class="ipt" placeholder="如：gpt-4o、ep-xxx等">
          </div>
          <div class="flex aic g3 mb3">
            <span class="hb-l">频率限制</span>
            <div class="flex aic g2">
              <input v-model.number="form.rateLimit" class="ipt" type="number" style="width:100px">
              <span style="font-size:13px;color:var(--cn-600)">次/分钟</span>
            </div>
          </div>
          <div class="flex aic g3 mb3">
            <span class="hb-l">状态</span>
            <div class="tgl" :class="form.status === 1 ? 'on' : ''" @click="form.status = form.status === 1 ? 0 : 1"></div>
            <span style="font-size:13px;color:var(--cn-600)">{{ form.status === 1 ? '启用' : '禁用' }}</span>
          </div>
          <div class="flex aic g3 mb3">
            <span class="hb-l">请求参数</span>
            <textarea v-model="form.requestParams" class="ipt" rows="3" placeholder='{"temperature":0.7,"max_tokens":2000}' style="resize:vertical;font-family:monospace;font-size:12px"></textarea>
          </div>
        </div>
        <div class="flex aic jbt" style="margin-top:20px">
          <div></div>
          <div class="flex aic g2">
            <button class="btn btn-g" @click="closeModal">取消</button>
            <button class="btn btn-p" @click="saveApi" :disabled="saving">{{ saving ? '保存中...' : '保存' }}</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/api'

const apis = ref([])
const showModal = ref(false)
const saving = ref(false)
const editingId = ref(null)
const selectedPreset = ref('')

const form = ref({
  name: '',
  provider: 'openai',
  apiUrl: '',
  apiKey: '',
  modelName: '',
  rateLimit: 60,
  status: 1,
  requestParams: ''
})

// 预设模板
const presets = [
  {
    provider: 'doubao',
    name: '字节豆包',
    modelName: 'doubao-pro-32k',
    apiUrl: 'https://ark.cn-beijing.volces.com/api/v3',
    rateLimit: 60
  },
  {
    provider: 'openai',
    name: 'OpenAI GPT-4o',
    modelName: 'gpt-4o',
    apiUrl: 'https://api.openai.com/v1',
    rateLimit: 60
  },
  {
    provider: 'aliyun',
    name: '阿里通义千问',
    modelName: 'qwen-max',
    apiUrl: 'https://dashscope.aliyuncs.com/compatible-mode/v1',
    rateLimit: 80
  },
  {
    provider: 'baidu',
    name: '百度文心一言',
    modelName: 'ernie-4.0-turbo-8k',
    apiUrl: 'https://qianfan.baidubce.com/v2',
    rateLimit: 100
  },
  {
    provider: 'deepseek',
    name: 'DeepSeek',
    modelName: 'deepseek-chat',
    apiUrl: 'https://api.deepseek.com/v1',
    rateLimit: 60
  }
]

function providerLabel(p) {
  return { openai: 'OpenAI', doubao: '字节豆包', baidu: '百度', aliyun: '阿里', deepseek: 'DeepSeek', other: '其他' }[p] || p
}
function providerTagCls(p) {
  return { openai: 'p', doubao: 'o', baidu: 'o', aliyun: 'o', deepseek: 'p', other: 'n' }[p] || 'n'
}
function maskKey(k) {
  if (!k) return '未设置'
  if (k.length <= 8) return '****'
  return k.substring(0, 4) + '****' + k.substring(k.length - 4)
}
function formatTokens(n) {
  if (!n) return '0'
  if (n >= 1000000) return (n / 1000000).toFixed(1) + 'M'
  if (n >= 1000) return (n / 1000).toFixed(1) + 'K'
  return n.toString()
}
function tokenPct(api) {
  if (!api.totalTokens || api.totalTokens === 0) return 0
  return Math.min(100, Math.round((api.usedTokens || 0) / api.totalTokens * 100))
}

function applyPreset(tpl) {
  selectedPreset.value = tpl.provider
  form.value.provider = tpl.provider
  form.value.apiUrl = tpl.apiUrl
  form.value.modelName = tpl.modelName
  form.value.rateLimit = tpl.rateLimit
  if (!form.value.name) {
    form.value.name = tpl.name
  }
}

function resetForm() {
  form.value = {
    name: '',
    provider: 'openai',
    apiUrl: '',
    apiKey: '',
    modelName: '',
    rateLimit: 60,
    status: 1,
    requestParams: ''
  }
  selectedPreset.value = ''
  editingId.value = null
}

function openAddModal() {
  resetForm()
  showModal.value = true
}

function editApi(api) {
  editingId.value = api.id
  form.value = {
    name: api.name || '',
    provider: api.provider || 'openai',
    apiUrl: api.apiUrl || '',
    apiKey: '', // 密钥不回显
    modelName: api.modelName || '',
    rateLimit: api.rateLimit || 60,
    status: api.status !== undefined ? api.status : 1,
    requestParams: api.requestParams || ''
  }
  showModal.value = true
}

function closeModal() {
  showModal.value = false
  resetForm()
}

async function saveApi() {
  if (!form.value.name || !form.value.apiUrl) {
    alert('请填写 API 名称和地址')
    return
  }
  saving.value = true
  try {
    if (editingId.value) {
      await api.put(`/ai/apis/${editingId.value}`, form.value)
    } else {
      await api.post('/ai/apis', form.value)
    }
    closeModal()
    fetchApis()
  } catch (e) {
    alert(e.response?.data?.message || '保存失败')
  } finally {
    saving.value = false
  }
}

async function deleteApi(api) {
  if (!confirm(`确认删除「${api.name}」?`)) return
  try {
    await api.delete(`/ai/apis/${api.id}`)
    fetchApis()
  } catch (e) {
    alert(e.response?.data?.message || '删除失败')
  }
}

async function fetchApis() {
  try {
    const res = await api.get('/ai/apis')
    if (res.data.code === 200) {
      apis.value = res.data.data
    }
  } catch (e) {
    console.error('fetchApis error', e)
  }
}

onMounted(() => {
  fetchApis()
})
</script>

<style scoped>
.api-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: var(--shs);
  border: 1px solid var(--cn-100);
  margin-bottom: 16px;
}
.api-card-h {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,.4);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
}
.modal-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px 28px;
  width: 460px;
  max-width: 90vw;
  box-shadow: var(--shl);
}
.hb-l {
  font-size: 13px;
  color: var(--cn-600);
  width: 80px;
  flex-shrink: 0;
  text-align: right;
}
.preset-card {
  border: 1px solid var(--cn-200);
  border-radius: 8px;
  padding: 10px 12px;
  cursor: pointer;
  transition: all .2s;
}
.preset-card:hover {
  border-color: var(--cp-500);
  background: var(--cp-50);
}
.preset-card.active {
  border-color: var(--cp-500);
  background: var(--cp-50);
  box-shadow: 0 0 0 2px var(--cp-200);
}
.tgl {
  width: 36px;
  height: 20px;
  background: var(--cn-300);
  border-radius: 10px;
  position: relative;
  cursor: pointer;
  transition: background .2s;
  flex-shrink: 0;
}
.tgl.on { background: var(--cp-500) }
.tgl::after {
  content: '';
  width: 16px;
  height: 16px;
  background: #fff;
  border-radius: 50%;
  position: absolute;
  top: 2px;
  left: 2px;
  transition: transform .2s;
  box-shadow: 0 1px 2px rgba(0,0,0,.04);
}
.tgl.on::after { transform: translateX(16px) }
</style>
