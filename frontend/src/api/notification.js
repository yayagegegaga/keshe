import request from './request'

export const getMyNotifications = params => request.get('/notifications/my', { params })

export const markNotificationRead = id => request.put(`/notifications/${id}/read`)

export const markAllNotificationsRead = () => request.put('/notifications/read-all')

export const getUnreadNotificationCount = () => request.get('/notifications/unread-count')
