import request from './request'

export const createVolunteerTask = data => request.post('/volunteer-tasks', data)
export const getVolunteerTasks = params => request.get('/volunteer-tasks', { params })
export const getMyVolunteerTasks = params => request.get('/volunteer-tasks/my', { params })
export const getVolunteerTaskDetail = id => request.get(`/volunteer-tasks/${id}`)
export const updateVolunteerTask = (id, data) => request.put(`/volunteer-tasks/${id}`, data)
export const claimVolunteerTask = (id, data = {}) => request.put(`/volunteer-tasks/${id}/claim`, data)
export const finishVolunteerTask = (id, data) => request.put(`/volunteer-tasks/${id}/finish`, data)
export const reviewVolunteerTask = (id, data) => request.put(`/volunteer-tasks/${id}/review`, data)
export const cancelVolunteerTask = (id, data = {}) => request.put(`/volunteer-tasks/${id}/cancel`, data)
export const deleteVolunteerTask = id => request.delete(`/volunteer-tasks/${id}`)
