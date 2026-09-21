import * as SettingApi from '@/api/setting'
import { loadTheme, buildThemeVars, getThemePrimary } from '@/utils/theme'
import { loadAndApplyTabbar } from '@/utils/tabbar'
import {
  parseStoreId,
  switchStore,
  setMerchantNo,
  getStoreId,
  isThemeScopeMatched,
  isTabbarScopeMatched
} from '@/utils/merchant'

/**
 * 商户主题/导航配置的统一刷新入口
 *
 * 会员端通过链接参数 storeId=xxx 切换商户/店铺，此前只有首页会同步商户号并刷新配置，
 * 直接打开「我的/订单/微店」等页面时商户号可能一直未知（接口被跳过），
 * 主题色与底部导航就会停留在上一个商户的配色。
 * 这里把「同步店铺信息 + 刷新主题 + 刷新导航」收敛为公共能力，
 * 由全局 mixin 在任意页面的 onLoad/onShow 调用，实现切换商户后实时生效。
 */

// systemConfig 同步进行中标识（多个页面同时 onShow 时只请求一次）
let syncing = null
// 同步失败后的冷却时间，避免接口异常时每个页面 onShow 都重复请求
let syncFailedAt = 0
const SYNC_RETRY_INTERVAL = 30 * 1000

/**
 * 当前页栈中的页面 Vue 实例（小程序端页面实例需经 $vm 访问）
 */
function getPageVms() {
  const pages = (typeof getCurrentPages === 'function' ? getCurrentPages() : []) || []
  return pages.map(page => (page && page.$vm) ? page.$vm : page).filter(vm => !!vm)
}

/**
 * 是否需要拉取店铺信息同步商户号
 *
 * 仅在「指定了店铺但商户号未知」或「主题/导航缓存不属于当前商户」时才请求，
 * 避免每个页面 onShow 都重复请求 systemConfig。
 */
function needSyncStoreInfo() {
  // 上一次同步失败后短时间内不再重试
  if (syncFailedAt && Date.now() - syncFailedAt < SYNC_RETRY_INTERVAL) {
    return false
  }
  const storeId = getStoreId()
  if (storeId && storeId !== '0' && !uni.getStorageSync('merchantNo')) {
    return true
  }
  // 主题缓存缺失或不属于当前商户/店铺（切换后缓存已被清空）
  if (!isThemeScopeMatched()) {
    return true
  }
  // 导航缓存存在但属于其他商户/店铺
  const tabbar = uni.getStorageSync('tabbar')
  if (tabbar && !isTabbarScopeMatched()) {
    return true
  }
  return false
}

/**
 * 同步当前店铺信息（storeId / 商户号），商户号决定主题与导航接口的作用域
 * @returns {Promise<boolean>} 商户或店铺是否发生变化（需要刷新主题与导航）
 */
export function syncStoreInfo() {
  if (!needSyncStoreInfo()) {
    return Promise.resolve(false)
  }
  // 并发调用复用同一个请求（同一时刻可能有多个页面 onShow）
  if (syncing) {
    return syncing
  }
  syncing = new Promise(resolve => {
    SettingApi.systemConfig()
      .then(result => {
        const storeInfo = result && result.data ? result.data.storeInfo : null
        if (!storeInfo) {
          resolve(false)
          return
        }
        const storeChanged = String(uni.getStorageSync('storeId') || '') !== String(storeInfo.id)
        uni.setStorageSync('storeId', storeInfo.id)
        const merchantChanged = setMerchantNo(storeInfo.merchantNo)
        resolve(merchantChanged || storeChanged || !isThemeScopeMatched() || !isTabbarScopeMatched())
      })
      .catch(() => {
        syncFailedAt = Date.now()
        resolve(false)
      })
      .finally(() => {
        syncing = null
      })
  })
  return syncing
}

/**
 * 把主题 CSS 变量同步到当前页栈的所有页面
 *
 * 主题变量按页面注入（小程序端无 document.documentElement），
 * 切换商户后需要让已打开的页面一起变色，否则要等页面再次 onShow 才更新。
 */
function applyThemeToPages(theme) {
  const vars = buildThemeVars(theme)
  const primary = (theme && theme.colors && theme.colors.primary) || getThemePrimary()
  getPageVms().forEach(vm => {
    if (vm.themeVars !== undefined) {
      vm.themeVars = vars
    }
    if (vm.themeColor !== undefined) {
      vm.themeColor = primary
    }
  })
}

/**
 * 刷新当前商户的主题配色与底部导航
 * @param {Object} page 页面实例（为空时只刷新配置与已打开页面的主题变量）
 * @param {boolean} force 是否强制拉取接口（切换商户/店铺后用 true）
 */
export function refreshMerchantConfig(page, force = true) {
  loadTheme(force).then(theme => {
    applyThemeToPages(theme)
  })
  // 当前页面重新拉取并应用导航配置（其它页面在 onShow 时自动跟随同一份缓存）
  page && loadAndApplyTabbar(page, force)
  // #ifdef H5
  // H5 的底部导航由页面内组件渲染，遍历页栈逐个刷新（loadTabbar 有并发保护，只请求一次）
  getPageVms().forEach(vm => {
    const tabbar = vm.$refs && vm.$refs.h5Tabbar
    tabbar && tabbar.refresh && tabbar.refresh(force)
  })
  // #endif
  // #ifdef MP-WEIXIN
  // 微信自定义 tabBar 实例挂在原生页面上，切换配置后校正选中态
  getPageVms().forEach(vm => {
    const host = vm.$scope || vm
    const tabbar = host && typeof host.getTabBar === 'function' && host.getTabBar()
    tabbar && tabbar.syncSelected && tabbar.syncSelected()
  })
  // #endif
}

/**
 * 页面 onLoad 时调用：解析链接参数中的 storeId 并切换店铺
 * @param {Object} options 页面 onLoad 参数
 * @returns {boolean} 是否发生了切换
 */
export function applyStoreIdFromOptions(options) {
  const storeId = parseStoreId(options)
  if (!storeId) {
    return false
  }
  return switchStore(storeId)
}

/**
 * 页面 onShow 时调用：保证当前店铺/商户配置已就绪，必要时强制刷新主题与底部导航
 * @param {Object} page 页面实例
 * @returns {Promise<boolean>} 是否执行了刷新
 */
export function ensureMerchantConfig(page) {
  return syncStoreInfo().then(changed => {
    if (changed) {
      refreshMerchantConfig(page, true)
    }
    return changed
  })
}
