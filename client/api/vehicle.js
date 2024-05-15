import request from '@/utils/request'

// api地址
const api = {
  list: 'clientApi/vehicle/list',
  detail: 'clientApi/vehicle/detail',
  save: 'clientApi/vehicle/save',
  submitOrder: 'clientApi/vehicle/submitOrder'
}

// 个人车辆列表
export const list = (param) => {
  return request.get(api.list, param)
}

// 车辆详情
export const detail = (vehicleId) => {
  return request.post(api.detail, { vehicleId })
}

// 保存车辆
export const save = (vehiclePlateNo,vehicleBrand, vehicleModel, vehicleType, status, vehicleId) => {
  return request.post(api.save, { vehiclePlateNo, vehicleBrand, vehicleModel, vehicleType, status, vehicleId })
}

// 设置默认车辆
export const setDefault = (vehicleId, isDefault) => {
  return request.post(api.save, { vehicleId, isDefault })
}

// 删除车辆
export const remove = (vehicleId, status) => {
  return request.post(api.save, { vehicleId, status })
}

// 提交服务单
export const submitOrder = (data) => {
  return request.post(api.submitOrder, data)
}
