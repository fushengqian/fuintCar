import config from '@/config'

/**
 * 商户/店铺作用域工具
 *
 * 后台的主题、底部导航等装修配置均按「商户号 + 店铺ID」维度下发，
 * 而客户端此前把这些配置按固定 key 缓存（theme / tabbar），
 * 导致从 A 商户/店铺切到 B 商户/店铺时仍命中上一家的本地缓存，
 * 出现「tabbar、页面主题没有实时切换」的问题。
 * 这里统一提供作用域标识，供主题与导航缓存做隔离校验。
 */

/**
 * 当前商户号（优先本地缓存，回退默认配置）
 */
export function getMerchantNo() {
  return uni.getStorageSync('merchantNo') || config.merchantNo || ''
}

/**
 * 当前店铺ID
 */
export function getStoreId() {
  const storeId = uni.getStorageSync('storeId')
  return storeId ? String(storeId) : ''
}

/**
 * 商户 + 店铺维度的作用域标识
 */
export function getMerchantScope() {
  return `${getMerchantNo()}#${getStoreId()}`
}

/**
 * 商户号是否已就绪
 *
 * 通过链接带 storeId 切换店铺时，新店铺所属的商户号要等 systemConfig 接口返回后才知道。
 * 在此期间若直接请求主题/导航，请求头带的还是上一个商户的商户号（或默认商户号），
 * 会拉到错误商户的配置，因此此时不做请求，等商户号就绪后再拉取。
 */
export function isMerchantReady() {
  const storeId = getStoreId()
  // 未指定店铺时按当前/默认商户号请求即可
  if (!storeId || storeId === '0') return true
  return !!uni.getStorageSync('merchantNo')
}

/**
 * 主题缓存是否属于当前商户/店铺
 *
 * 商户号就绪(systemConfig 返回)后用它判断是否需要重新拉取主题与底部导航，
 * 避免切换商户/店铺后主题缓存被清空却没有触发刷新。
 */
export function isThemeScopeMatched() {
  const theme = uni.getStorageSync('theme')
  return !!(theme && theme._scope === getMerchantScope())
}

/**
 * 切换店铺
 *
 * 写入新的店铺ID并清空商户号（由 systemConfig 返回后重新写入），
 * 同时清空主题与底部导航缓存，避免切换过程中沿用上一个商户的配置。
 * @param {number|string} storeId
 * @returns {boolean} 是否发生了切换
 */
export function switchStore(storeId) {
  const next = storeId ? String(storeId) : ''
  if (!next || next === getStoreId()) return false
  uni.setStorageSync('storeId', next)
  // 该店铺的商户号若已缓存过则直接复用，切换后可立即拉取主题/导航，
  // 不必等 systemConfig 返回（等待期间请求头会带上一个商户的商户号，拉到错误配置）
  const cachedNo = getMerchantNoMap()[next]
  if (cachedNo) {
    uni.setStorageSync('merchantNo', cachedNo)
  } else {
    uni.removeStorageSync('merchantNo')
  }
  uni.removeStorageSync('theme')
  uni.removeStorageSync('theme_time')
  uni.removeStorageSync('tabbar')
  return true
}

/**
 * 店铺 → 商户号 映射缓存
 *
 * 会员端通过链接参数 storeId=xxx 切换商户/店铺，而新店铺所属的商户号要等
 * systemConfig 返回才知道。缓存映射后再次切回该店铺可立即就绪，
 * 保证主题与底部导航能实时切换到新商户的配色与导航项。
 */
function getMerchantNoMap() {
  return uni.getStorageSync('merchantNoMap') || {}
}

/**
 * 写入当前商户号（systemConfig 返回店铺信息后调用）
 * @param {string} merchantNo
 * @returns {boolean} 商户号是否发生变化
 */
export function setMerchantNo(merchantNo) {
  if (!merchantNo) return false
  if (merchantNo === uni.getStorageSync('merchantNo')) return false
  uni.setStorageSync('merchantNo', merchantNo)
  // 记录店铺 → 商户号映射，供下次通过 storeId 切回该店铺时直接复用
  const storeId = getStoreId()
  if (storeId) {
    const map = getMerchantNoMap()
    map[storeId] = merchantNo
    uni.setStorageSync('merchantNoMap', map)
  }
  return true
}

/**
 * 解析页面参数/链接中的 storeId（形如 ?storeId=xxx）
 *
 * 会员端通过链接参数切换商户：小程序取页面 onLoad 的 options，
 * H5 场景（页面参数可能未透传、或 SPA 内 URL 变化）再从地址栏兜底解析。
 * @param {Object} options 页面 onLoad 参数
 * @returns {string} storeId，未指定时返回空串
 */
export function parseStoreId(options) {
  let storeId = options && options.storeId ? options.storeId : ''
  // #ifdef H5
  if (!storeId) {
    try {
      const match = window.location.href.match(/[?&]storeId=(\d+)/)
      if (match) {
        storeId = match[1]
      }
    } catch (e) {
      // empty
    }
  }
  // #endif
  return storeId ? String(storeId) : ''
}

/**
 * 底部导航缓存是否属于当前商户/店铺
 *
 * 与 isThemeScopeMatched 同理：切换商户/店铺后缓存若属于上一个商户，
 * 需要强制重新拉取导航配置，避免沿用上一家的导航项。
 */
export function isTabbarScopeMatched() {
  const tabbar = uni.getStorageSync('tabbar')
  return !!(tabbar && tabbar._scope === getMerchantScope())
}
