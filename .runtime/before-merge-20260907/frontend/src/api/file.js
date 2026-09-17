import request from './request'

export const uploadFile = (file, bizType, bizId) => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('biz_type', bizType)
  if (bizId) {
    formData.append('biz_id', bizId)
  }
  return request.post('/files/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export const getFiles = params => request.get('/files', { params })

export const getMyFiles = params => request.get('/files/my', { params })

export const deleteFile = id => request.delete(`/files/${id}`)
