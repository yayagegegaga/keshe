import request from './request'

export const createRescueOrder = data => request.post('/rescue-orders', data)

export const getRescueOrders = params => request.get('/rescue-orders', { params })

export const getRescueOrderDetail = id => request.get(`/rescue-orders/${id}`)

export const assignRescueOrder = (id, data) => request.put(`/rescue-orders/${id}/assign`, data)

export const startRescueOrder = id => request.put(`/rescue-orders/${id}/start`)

export const finishRescueOrder = (id, data) => request.put(`/rescue-orders/${id}/finish`, data)

export const closeRescueOrder = (id, data) => request.put(`/rescue-orders/${id}/close`, data)

export const cancelRescueOrder = (id, data) => request.put(`/rescue-orders/${id}/cancel`, data)

export const getRescueOrderLogs = id => request.get(`/rescue-orders/${id}/logs`)

export const deleteRescueOrder = id => request.delete(`/rescue-orders/${id}`)