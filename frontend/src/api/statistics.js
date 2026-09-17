import request from './request'

export const getStatisticsOverview = () => request.get('/statistics/overview')
