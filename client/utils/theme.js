import * as themeApi from '@/api/theme'
import { getMerchantScope, isMerchantReady } from './merchant'

// 无主题缓存/后台主题不可用时的兜底色：
// 必须用可读的中性深色，绝对不能用白色——主题色普遍用在按钮、选中态、卡片背景上，
// 白色兜底会让这些元素与白色底融为一体（如首页「登录」按钮会直接看不见）。
// 这里取与后台默认主题、uni.scss 品牌色一致的主题蓝，主题接口返回后即被覆盖。
const DEFAULT_PRIMARY = '#ffffff'
const DEFAULT_THEME = {
  themeId: '',
  themeName: '默认主题',
  colors: {
    primary: DEFAULT_PRIMARY,
    // 与后台默认主题(主题蓝)保持一致的辅助色/价格色，避免兜底时颜色不统一
    secondary: '#e8e9f1',
    text: '#333333',
    bg: '#f5f5f5',
    price: '#ff4d4f'
  }
}

// 主题缓存有效期:1 小时
// (App 启动时已通过 loadTheme(true) 强制拉取最新主题并写入缓存,
// 因此页面 onShow 期间只需在缓存超时后兜底刷新, 避免每个页面反复请求导致换色闪烁)
const CACHE_DURATION = 60 * 60 * 1000

// 主题缓存版本:兜底色调整后递增, 让旧版本客户端里缓存的错误兜底色自动失效并重新拉取
const CACHE_VERSION = 2

let loadingPromise = null

/**
 * 读取缓存的主题配置
 */
export function getTheme() {
  const theme = uni.getStorageSync('theme')
  return theme && theme.colors ? theme : DEFAULT_THEME
}

/**
 * 缓存主题配置
 */
export function setTheme(theme, scope) {
  uni.setStorageSync('theme', { ...theme, _scope: scope || getMerchantScope(), _v: CACHE_VERSION })
  uni.setStorageSync('theme_time', Date.now())
}

/**
 * 合并主题颜色并剔除无效值
 *
 * 后台未配置主题时返回的是空对象/空字符串，若直接写入 CSS 变量会得到非法值，
 * 元素背景/文字色在计算时整体失效(表现为按钮、选中态"消失")，
 * 因此这里统一用兜底色补全缺失或空的颜色。
 */
export function resolveColors(colors) {
  const out = Object.assign({}, DEFAULT_THEME.colors)
  const src = colors || {}
  Object.keys(out).forEach(key => {
    const value = src[key]
    if (typeof value === 'string' && value.trim()) {
      out[key] = value.trim()
    }
  })
  return out
}

/**
 * 生成页面 CSS 变量样式字符串,用于页面根节点 :style 绑定
 *
 * 注意必须返回字符串而非对象：uni-app 编译到微信小程序时,
 * :style="obj" 会被序列化为 style="{{(obj)}}",对象会变成 [object Object],
 * CSS 变量在 page 内彻底失效。字符串形式在 H5 与小程序端都会被作为 inline style 正确解析。
 */
export function buildThemeVars(theme) {
  const t = theme || getTheme()
  const c = resolveColors(t.colors)
  const parts = []
  parts.push(`--theme-primary: ${c.primary}`)
  parts.push(`--theme-secondary: ${c.secondary}`)
  parts.push(`--theme-text: ${c.text}`)
  parts.push(`--theme-bg: ${c.bg}`)
  parts.push(`--theme-price: ${c.price}`)
  // 主色上的文字色：主题色为浅色时使用深色文字，避免白字白底看不清
  parts.push(`--theme-primary-text: ${isLightColor(c.primary) ? '#333333' : '#ffffff'}`)
  // 同时同步 SCSS 编译后对应的 CSS 变量，让 $fuint-theme 的 100+ 处引用也跟随主题
  parts.push(`--fuint-theme: ${c.primary}`)
  return parts.join('; ')
}

/**
 * 读取当前主题的 primary 色（用于组件如 tabbar 选中色等无 CSS 变量场景的兜底）
 */
export function getThemePrimary() {
  const t = getTheme()
  return resolveColors(t && t.colors).primary
}

/**
 * 判断颜色是否为浅色(用于导航栏前景文字黑/白选择)
 * @param {string} color 如 '#ffffff'
 */
export function isLightColor(color) {
  const hex = String(color || '').trim().replace('#', '')
  if (!/^[0-9a-fA-F]{6}$/.test(hex)) return false
  const r = parseInt(hex.substr(0, 2), 16)
  const g = parseInt(hex.substr(2, 2), 16)
  const b = parseInt(hex.substr(4, 2), 16)
  // 感知亮度(0~255), 大于 160 视为浅色, 前景用深色文字
  return 0.299 * r + 0.587 * g + 0.114 * b > 160
}

/**
 * H5 环境下注入全局 CSS 变量(作用于 document.documentElement)
 */
function applyH5Theme(theme) {
  // #ifdef H5
  const t = theme || getTheme()
  const c = resolveColors(t && t.colors)
  const style = document.documentElement.style
  style.setProperty('--theme-primary', c.primary)
  style.setProperty('--theme-secondary', c.secondary)
  style.setProperty('--theme-text', c.text)
  style.setProperty('--theme-bg', c.bg)
  style.setProperty('--theme-price', c.price)
  style.setProperty('--theme-primary-text', isLightColor(c.primary) ? '#333333' : '#ffffff')
  style.setProperty('--fuint-theme', c.primary)
  // #endif
}

/**
 * 加载主题配置(带缓存,force 为 true 时强制刷新)
 */
export function loadTheme(force) {
  const scope = getMerchantScope()
  const cached = uni.getStorageSync('theme')
  const cachedTheme = cached && cached.colors ? cached : DEFAULT_THEME
  // 缓存需同时匹配商户/店铺作用域与缓存版本(版本变化说明兜底色调整过，需重新拉取)
  const scopeMatched = !!(cached && cached.colors && cached._scope === scope && cached._v === CACHE_VERSION)
  const time = uni.getStorageSync('theme_time')
  const inCacheTime = !!(time && Date.now() - time < CACHE_DURATION)

  // 切换店铺后商户号尚未就绪(systemConfig 未返回)时不请求,
  // 否则请求头带的仍是上一个商户的商户号, 会拉到错误商户的主题
  if (!isMerchantReady()) {
    applyH5Theme(cachedTheme)
    return Promise.resolve(cachedTheme)
  }

  // 缓存命中条件:未强制刷新 + 商户/店铺作用域一致 + 未超过缓存有效期
  if (!force && scopeMatched && inCacheTime) {
    applyH5Theme(cachedTheme)
    return Promise.resolve(cachedTheme)
  }

  // 防止并发重复请求:作用域一致时复用同一个请求
  if (loadingPromise) {
    if (loadingPromise.scope === scope) {
      return loadingPromise
    }
    // 作用域已变化(切换了商户/店铺), 等当前请求结束后按新作用域重新拉取
    return loadingPromise.then(() => loadTheme(force))
  }

  const pending = themeApi.theme()
    .then(res => {
      const theme = res.data || {}
      // 后台未配置主题时返回空对象：用兜底色补全缺失/空颜色后再缓存，
      // 避免非法色值让主色元素(按钮/选中态)整体消失
      theme.colors = resolveColors(theme.colors)
      setTheme(theme, scope)
      applyH5Theme(theme)
      return theme
    })
    .catch(() => {
      const theme = getTheme()
      applyH5Theme(theme)
      return theme
    })
    .finally(() => {
      // 作用域已变化(切换商户/店铺)时可能已有新请求, 避免把新请求误清空
      if (loadingPromise === pending) {
        loadingPromise = null
      }
    })
  // 记录本次请求所属作用域, 供并发调用判断是否可复用
  pending.scope = scope
  loadingPromise = pending
  return pending
}
