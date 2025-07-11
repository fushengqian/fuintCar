import request from '@/utils/request'

// api地址
const api = {
  bookList: 'merchantApi/book/list',
  bookDetail: 'merchantApi/book/detail',
  cancel: 'merchantApi/book/cancel',
  confirm: 'merchantApi/book/confirm'
}

// 我的预约列表
export const bookList = (param) => {
  return request.get(api.bookList, param)
}

// 我的预约详情
export const bookDetail = (bookId) => {
  return request.post(api.bookDetail, { bookId })
}

// 取消预约
export function cancel(bookId, data) {
  return request.get(api.cancel, { bookId, ...data })
}

// 确认预约
export function confirm(bookId, data) {
  return request.get(api.confirm, { bookId, ...data })
}