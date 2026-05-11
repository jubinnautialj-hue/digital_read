import request from './request'

export const authApi = {
  login: (data) => request.post('/auth/login', data),
  register: (data) => request.post('/auth/register', data),
  ping: () => request.get('/auth/ping')
}

export const userApi = {
  getAll: () => request.get('/users'),
  getById: (id) => request.get(`/users/${id}`),
  create: (data) => request.post('/users', data),
  update: (id, data) => request.put(`/users/${id}`, data),
  delete: (id) => request.delete(`/users/${id}`),
  changePassword: (id, data) => request.post(`/users/${id}/change-password`, data),
  uploadAvatar: (id, formData) => request.post(`/users/${id}/avatar`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export const documentApi = {
  getAll: () => request.get('/documents'),
  getAccessible: () => request.get('/documents/accessible'),
  getById: (id) => request.get(`/documents/${id}`),
  getStructured: (id) => request.get(`/documents/${id}/structured`),
  search: (keyword) => request.get('/documents/search', { params: { keyword } }),
  create: (data) => request.post('/documents', data),
  update: (id, data) => request.put(`/documents/${id}`, data),
  delete: (id) => request.delete(`/documents/${id}`),
  upload: (formData) => request.post('/documents/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }),
  parse: (data) => request.post('/documents/parse', data),
  getSupportedFormats: () => request.get('/documents/supported-formats'),
  stats: () => request.get('/documents/stats')
}

export const settingsApi = {
  getByUserId: (userId) => request.get(`/user-settings/user/${userId}`),
  update: (userId, data) => request.put(`/user-settings/user/${userId}`, data),
  reset: (userId) => request.post(`/user-settings/user/${userId}/reset`)
}

export const activityApi = {
  getAll: () => request.get('/activities'),
  getByUser: (userId) => request.get(`/activities/user/${userId}`),
  getRecent: (userId, days = 7) => request.get(`/activities/user/${userId}/recent`, { params: { days } }),
  start: (data) => request.post('/activities/start', data),
  end: (id, data) => request.post(`/activities/${id}/end`, data),
  updateProgress: (id, data) => request.put(`/activities/${id}/progress`, data),
  analytics: () => request.get('/activities/analytics')
}

export const accessibilityApi = {
  getAllSettings: () => request.get('/accessibility/settings'),
  getActiveSettings: () => request.get('/accessibility/settings/active'),
  createSetting: (data) => request.post('/accessibility/settings', data),
  updateSetting: (id, data) => request.put(`/accessibility/settings/${id}`, data),
  deleteSetting: (id) => request.delete(`/accessibility/settings/${id}`),
  audit: () => request.get('/accessibility/audit'),
  globalSettings: () => request.get('/accessibility/global-settings')
}

export const ttsApi = {
  getVoices: () => request.get('/tts/voices'),
  synthesize: (data) => request.post('/tts/synthesize', data),
  getAudio: (data) => request.post('/tts/audio', data, { responseType: 'blob' }),
  getMarkers: (data) => request.post('/tts/markers', data),
  chunkText: (data) => request.post('/tts/chunk', data),
  getConfig: () => request.get('/tts/config')
}
