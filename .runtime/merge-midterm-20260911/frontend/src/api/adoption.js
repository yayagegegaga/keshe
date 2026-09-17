import request from './request'

export const applyAdoption = data => request.post('/adoptions', data)

export const getAdoptions = params => request.get('/adoptions', { params })

export const getMyAdoptions = params => request.get('/adoptions/my', { params })

export const getAdoptionDetail = id => request.get(`/adoptions/${id}`)

export const firstApproveAdoption = (id, data) => request.put(`/adoptions/${id}/first-approve`, data)

export const interviewAdoption = (id, data) => request.put(`/adoptions/${id}/interview`, data)

export const trialAdoption = (id, data) => request.put(`/adoptions/${id}/trial`, data)

export const successAdoption = (id, data) => request.put(`/adoptions/${id}/success`, data)

export const rejectAdoption = (id, data) => request.put(`/adoptions/${id}/reject`, data)

export const trialFailedAdoption = (id, data) => request.put(`/adoptions/${id}/trial-failed`, data)

export const cancelAdoption = (id, data) => request.put(`/adoptions/${id}/cancel`, data)

export const getAdoptionLogs = id => request.get(`/adoptions/${id}/logs`)
