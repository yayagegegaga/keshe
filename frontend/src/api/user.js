import request from './request'

export const getVolunteers = () => request.get('/users/volunteers')
