import request from './request'

export const createRescueClue = data => request.post('/rescue-clues', data)

export const getRescueClues = params => request.get('/rescue-clues', { params })

export const getMyRescueClues = params => request.get('/rescue-clues/my', { params })

export const getRescueClueDetail = id => request.get(`/rescue-clues/${id}`)

export const reviewRescueClue = (id, data) => request.put(`/rescue-clues/${id}/review`, data)

export const cancelRescueClue = id => request.put(`/rescue-clues/${id}/cancel`)
