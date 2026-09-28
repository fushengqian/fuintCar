<script>
  import { applyStoreIdFromOptions } from '@/utils/merchantConfig'

  export default {

    /**
     * 全局变量
     */
    globalData: {

    },

    /**
     * 初始化完成时触发
     */
    onLaunch(options) {
      // 小程序主动更新
      this.updateManager()
      // 链接中携带 storeId 时先切换店铺(早于页面 onLoad)，
      // 保证主题/导航等接口带的是当前链接对应店铺的参数，而不是上次访问残留的
      this.applyUrlStoreId(options)
      if (options.query.spm) {
          uni.setStorageSync('shareId', options.query.spm);
      }
    },

    methods: {

      /**
       * 解析启动参数/链接中的 storeId 并切换店铺（H5 场景）
       *
       * 早于页面 onLoad 执行，保证主题/导航接口带的是当前链接对应店铺的参数，
       * 而不是上次访问残留的；与当前店铺相同则不切换。
       */
      applyUrlStoreId(options) {
        applyStoreIdFromOptions(options && options.query ? options.query : {})
      },

      /**
       * 小程序主动更新
       */
      updateManager() {
        const updateManager = uni.getUpdateManager();
        updateManager.onCheckForUpdate(res => {
          // 请求完新版本信息的回调
          // console.log(res.hasUpdate)
        })
        updateManager.onUpdateReady(() => {
          uni.showModal({
            title: '更新提示',
            content: '新版本已经准备好，即将重启应用',
            showCancel: false,
            success(res) {
              if (res.confirm) {
                // 新的版本已经下载好，调用 applyUpdate 应用新版本并重启
                updateManager.applyUpdate()
              }
            }
          })
        })
        updateManager.onUpdateFailed(() => {
          // 新的版本下载失败
          uni.showModal({
            title: '更新提示',
            content: '新版本下载失败',
            showCancel: false
          })
        })
      }
    }

  }
</script>

<style lang="scss">
  /* 引入uView库样式 */
  @import "uview-ui/index.scss";
</style>

<style>
  /* 项目基础样式 */
  @import "./app.scss";

  /* #ifdef H5 */
  /*
   * 隐藏 H5 内置 tabBar（uni-app 自动渲染的 uni-tabbar）：
   * 本项目的底部导航由 components/tabbar 自定义渲染，内置 tabBar 的图标/字号/高度与其不一致，
   * 一旦自定义导航被卸载（如"我的"页加载中根节点 v-if 为 false）或页面切换的瞬间，
   * 内置 tabBar 就会露出来，造成底部导航"闪动"。
   * 这里用 visibility 而非 display，避免影响 tabBar 占位带来的底部留白与 --window-bottom 相关布局。
   */
  uni-tabbar.uni-tabbar-bottom {
    visibility: hidden !important;
    pointer-events: none !important;
  }
  /* #endif */
</style>
