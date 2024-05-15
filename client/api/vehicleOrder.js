import request from '@/utils/request'

// api地址
const api = {
  list: 'clientApi/vehicleOrder/list',
  detail: 'clientApi/vehicleOrder/detail',
}

// 我的服务单列表
export function list(param, option) {
  return request.post(api.list, param, option)
}

// 服务单详情
export function detail(orderId, param) {
  return request.get(api.detail, { orderId, ...param })
}
