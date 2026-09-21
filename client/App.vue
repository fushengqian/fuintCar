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
</style>
