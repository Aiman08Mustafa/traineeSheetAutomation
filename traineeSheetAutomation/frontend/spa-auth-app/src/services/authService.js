const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
const AUTH_STORAGE_KEY = 'trainee_auth'

function getBackendMessage(data) {
  if (!data) return ''
  if (typeof data === 'string') return data
  return (
    data.message ||
    data.error ||
    data.details ||
    (Array.isArray(data.errors) ? data.errors.join(', ') : '') ||
    ''
  )
}

function errorHandler({ status, method, path, backendMessage }) {
  const msg = (backendMessage || '').toString().trim()

  // Auth
  if (status === 401) return 'Invalid email or password.'
  if (status === 403) return "You don’t have permission to perform this action."

  // Common backend runtime messages -> friendlier text
  const rules = [
    [/email already registered/i, 'This email is already registered. Please sign in instead.'],
    [/user not found/i, 'Invalid email or password.'],
    [/bad credentials/i, 'Invalid email or password.'],
    [/role not found/i, 'Unable to create account. Please contact support.'],
    [/template not found/i, 'The selected template was not found. Please refresh and try again.'],
    [/module not found/i, 'The selected module was not found. Please refresh and try again.'],
    [/topic not found/i, 'The selected topic was not found. Please refresh and try again.'],
    [/service line not found/i, 'Please select a valid service line.'],
    [/already exists/i, 'This item already exists. Please choose a different name/sequence and try again.'],
    [/sequence order/i, 'That sequence number is already in use. Please choose a different number.'],
    [/content-type.*not supported/i, 'Something went wrong while sending your request. Please refresh and try again.'],
    [/failed to fetch/i, 'Unable to reach the server. Please check your connection and try again.'],
  ]
  for (const [pattern, replacement] of rules) {
    if (pattern.test(msg)) return replacement
  }

  // Validation-ish messages
  if (/must not be null|must not be blank|validation failed/i.test(msg)) {
    return 'Please fill out all required fields and try again.'
  }

  // Fallbacks
  if (status >= 500) return 'Server error. Please try again in a moment.'
  if (msg) return msg
  return `Something went wrong (${method} ${path}). Please try again.`
}

async function request(path, options = {}) {
  const { headers: customHeaders = {}, ...restOptions } = options
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...restOptions,
    headers: {
      'Content-Type': 'application/json',
      ...customHeaders,
    },
  })

  let data = null
  try {
    data = await response.json()
  } catch {
    data = null
  }

  if (!response.ok) {
    const method = options.method || 'GET'
    const backendMessage = getBackendMessage(data)
    const message = humanizeError({ status: response.status, method, path, backendMessage })
    throw new Error(message)
  }

  return data
}

function getAuthHeaders(token) {
  if (!token) return {}
  return { Authorization: `Bearer ${token}` }
}

export async function registerUser(payload) {
  return request('/api/v1/auth/register', { method: 'POST', body: JSON.stringify(payload) })
}

export async function loginUser(payload) {
  return request('/api/v1/auth/authenticate', { method: 'POST', body: JSON.stringify(payload) })
}

export async function validateToken(token) {
  await request('/api/v1/templates', { method: 'GET', headers: getAuthHeaders(token) })
  return true
}

export function saveAuth(data) {
  localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(data))
}

export function loadAuth() {
  const raw = localStorage.getItem(AUTH_STORAGE_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw)
  } catch {
    localStorage.removeItem(AUTH_STORAGE_KEY)
    return null
  }
}

export function clearAuth() {
  localStorage.removeItem(AUTH_STORAGE_KEY)
}

export async function fetchTemplates(token) {
  return request('/api/v1/templates', { method: 'GET', headers: getAuthHeaders(token) })
}

export async function fetchServiceLines(token) {
  return request('/api/v1/service-lines', { method: 'GET', headers: getAuthHeaders(token) })
}

export async function createTemplate(payload, token) {
  return request('/api/v1/templates', {
    method: 'POST',
    headers: getAuthHeaders(token),
    body: JSON.stringify(payload),
  })
}

export async function fetchModulesByTemplate(templateId, token) {
  return request(`/api/v1/modules/template/${templateId}`, {
    method: 'GET',
    headers: getAuthHeaders(token),
  })
}

export async function createModule(payload, token) {
  return request('/api/v1/modules', {
    method: 'POST',
    headers: getAuthHeaders(token),
    body: JSON.stringify(payload),
  })
}

export async function updateModule(moduleId, payload, token) {
  return request(`/api/v1/modules/${moduleId}`, {
    method: 'PUT',
    headers: getAuthHeaders(token),
    body: JSON.stringify(payload),
  })
}

export async function deleteModule(moduleId, token) {
  return request(`/api/v1/modules/${moduleId}`, {
    method: 'DELETE',
    headers: getAuthHeaders(token),
  })
}

export async function fetchTopicsByModule(moduleId, token) {
  return request(`/api/v1/topics/module/${moduleId}`, {
    method: 'GET',
    headers: getAuthHeaders(token),
  })
}

export async function createTopic(payload, token) {
  return request('/api/v1/topics', {
    method: 'POST',
    headers: getAuthHeaders(token),
    body: JSON.stringify(payload),
  })
}

export async function updateTopic(topicId, payload, token) {
  return request(`/api/v1/topics/${topicId}`, {
    method: 'PUT',
    headers: getAuthHeaders(token),
    body: JSON.stringify(payload),
  })
}

export async function deleteTopic(topicId, token) {
  return request(`/api/v1/topics/${topicId}`, {
    method: 'DELETE',
    headers: getAuthHeaders(token),
  })
}

export async function fetchTraineeTemplates(traineeId, token) {
  return request(`/api/v1/trainee-templates/trainee/${traineeId}`, {
    method: 'GET',
    headers: getAuthHeaders(token),
  })
}

export async function assignTemplateToTrainee({ templateId, traineeId, token }) {
  return request('/api/v1/trainee-templates', {
    method: 'POST',
    headers: getAuthHeaders(token),
    body: JSON.stringify({ templateId, traineeId }),
  })
}

export async function fetchTraineeDashboardData({ traineeId, token, traineeTemplateId }) {
  if (!traineeId || !token) throw new Error('Missing trainee identity or token.')

  const traineeTemplates = await fetchTraineeTemplates(traineeId, token)
  if (!Array.isArray(traineeTemplates) || traineeTemplates.length === 0) {
    return { assignedTemplate: null, modules: [] }
  }

  const assignedTemplate =
    traineeTemplates.find((t) => t.traineeTemplateId === traineeTemplateId) ||
    traineeTemplates.find((t) => t.status === 'IN_PROGRESS') ||
    traineeTemplates[0]

  const traineeModules = await request(
    `/api/v1/trainee-modules/trainee-template/${assignedTemplate.traineeTemplateId}`,
    { method: 'GET', headers: getAuthHeaders(token) }
  )

  const topicsByModule = await Promise.all(
    traineeModules.map(async (module) => {
      const topics = await request(`/api/v1/trainee-topics/trainee-module/${module.traineeModuleId}`, {
        method: 'GET',
        headers: getAuthHeaders(token),
      })
      return { traineeModuleId: module.traineeModuleId, topics }
    })
  )

  const modules = traineeModules.map((module) => ({
    ...module,
    topics: topicsByModule.find((e) => e.traineeModuleId === module.traineeModuleId)?.topics || [],
  }))

  return { assignedTemplate, modules }
}

export async function updateTraineeTopicStatus({ traineeTopicId, status, token }) {
  return request(`/api/v1/trainee-topics/${traineeTopicId}/status?status=${status}`, {
    method: 'PATCH',
    headers: getAuthHeaders(token),
  })
}

