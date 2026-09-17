import request from './request'

export const createFollowUp = data => request.post('/follow-ups', data)

export const getFollowUps = params => request.get('/follow-ups', { params })

export const getMyFollowUps = params => request.get('/follow-ups/my', { params })

export const getFollowUpDetail = id => request.get(`/follow-ups/${id}`)

export const deleteFollowUp = id => request.delete(`/follow-ups/${id}`)
