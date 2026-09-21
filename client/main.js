import Vue from 'vue'
import App from './App'
import store from './store'
import uView from 'uview-ui'
import bootstrap from './core/bootstrap'
import {
  getPlatform,
  navTo,
  showToast,
  showSuccess,
  showError,
  getShareUrlParams
} from './utils/app'
import './core/ican-H5Api'
import {
  getTheme,
  loadTheme,
  buildThemeVars,
  getThemePrimary,
  isLightColor
} from './utils/theme'
import { applyStoreIdFromOptions, ensureMerchantConfig } from './utils/merchantConfig'

Vue.config.productionTip = false

App.mpType = 'app'

// 当前运行的终端
Vue.prototype.$platform = getPlatform()

// 全局主题 mixin:注入 themeVars(CSS 变量),页面显示时刷新主题配置
Vue.mixin({
  data() {
    return {
      themeVars: buildThemeVars(getTheme()),
      // 供模板内原生控件(radio/u-icon/第三方组件等)绑定主题色
      themeColor: getThemePrimary()
    }
  },
  onLoad(options) {
    // 会员端通过链接参数 storeId=xxx 切换商户：任意页面入口都要生效，
    // 切换后清空主题/导航缓存，后续 onShow 会按新商户重新拉取
    applyStoreIdFromOptions(options)
  },
  onShow() {
    // 通过 storeId 切换后商户号可能尚未就绪，先同步店铺信息并刷新主题/导航，
    // 保证切换商户时主题色与底部导航实时切换（非切换场景不会额外发请求）
    ensureMerchantConfig(this).then(() => loadTheme()).then(theme => {
      this.themeVars = buildThemeVars(theme)
      // 微信小程序运行时设置顶部导航栏颜色，覆盖 pages.json 中的静态值
      // #ifdef MP-WEIXIN
      const c = (theme && theme.colors) || {}
      const primary = c.primary || getThemePrimary()
      this.themeColor = primary
      try {
        uni.setNavigationBarColor({
          // 背景为浅色(含白色兜底)时使用黑色文字, 否则白色文字
          frontColor: isLightColor(primary) ? '#000000' : '#ffffff',
          backgroundColor: primary,
          animation: { duration: 0, timingFunc: 'linear' }
        })
      } catch (e) {}
      // #endif
    })
  }
})

// 载入uView库
Vue.use(uView)

// 挂载全局函数
Vue.prototype.$toast = showToast
Vue.prototype.$success = showSuccess
Vue.prototype.$error = showError
Vue.prototype.$navTo = navTo
Vue.prototype.$getShareUrlParams = getShareUrlParams

// 实例化应用
const app = new Vue({
  ...App,
  store,
  created: bootstrap
})
app.$mount()
