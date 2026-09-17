import request from './request'

export const getAnimals = params => request.get('/animals', { params })

export const getAnimalDetail = id => request.get(`/animals/${id}`)

export const createAnimal = data => request.post('/animals', data)

export const updateAnimal = (id, data) => request.put(`/animals/${id}`, data)

export const updateAnimalStatus = (id, data) => request.put(`/animals/${id}/status`, data)

export const deleteAnimal = id => request.delete(`/animals/${id}`)

export const addAnimalImage = (id, data) => request.post(`/animals/${id}/images`, data)

export const addHealthRecord = (id, data) => request.post(`/animals/${id}/health-records`, data)
