import Enum from '../enum'

/**
 * 枚举类：服务单状态
 * VehicleOrderStatusEnum
 */
export default new Enum([
  { key: 'CREATED', name: '已提交', value: 'A' },
  { key: 'SERVICE', name: '服务中', value: 'B' },
  { key: 'COMPELETE', name: '已完成', value: 'C' },
])
