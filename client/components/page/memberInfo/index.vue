<template>
  <!-- 会员信息：会员端复用 pages/index/components/HomeUser.vue 渲染，
       会员数据（登录状态、余额积分、默认车牌）由组件自身获取 -->
  <HomeUser
    :userInfo="userInfo"
    :vehicle="vehicle"
    :boxStyle="boxStyle"
    :cardStyle="cardStyle"
  />
</template>

<script>
  import HomeUser from '@/pages/index/components/HomeUser.vue'
  import * as UserApi from '@/api/user'
  import * as VehicleApi from '@/api/vehicle'

  // 后台样式数值按 px 配置，小程序按 750rpx 设计稿换算
  const rpxRatio = 2

  export default {
    name: 'MemberInfo',

    components: {
      HomeUser
    },

    props: {
      itemStyle: Object,
      params: Object
    },

    data() {
      return {
        // 会员信息（未登录时的默认占位，HomeUser 依赖 id 判断登录状态）
        userInfo: { id: 0, avatar: '', name: '', balance: '', point: '' },
        // 默认车牌（未登录或未绑定车牌时为空）
        vehicle: null,
        // 请求进行中标识，避免首页显示与登录状态变化时重复请求
        loading: false
      }
    },

    computed: {
      // 登录状态（登录/退出后自动刷新会员信息）
      userId() {
        return this.$store.getters.userId
      },
      // 外层容器样式：背景色、左右边距、上边距
      boxStyle() {
        const style = this.itemStyle || {}
        const parts = []
        if (style.background) {
          parts.push(`background: ${style.background}`)
        }
        const paddingX = parseInt(style.paddingX, 10)
        if (paddingX > 0) {
          parts.push(`padding-left: ${paddingX * rpxRatio}rpx`)
          parts.push(`padding-right: ${paddingX * rpxRatio}rpx`)
        }
        const marginTop = parseInt(style.marginTop, 10)
        if (marginTop > 0) {
          parts.push(`margin-top: ${marginTop * rpxRatio}rpx`)
        }
        return parts.join('; ')
      },
      // 会员卡片样式：背景色、边框色、圆角、内边距
      cardStyle() {
        const style = this.itemStyle || {}
        const parts = []
        if (style.cardBg) {
          parts.push(`background: ${style.cardBg}`)
        }
        if (style.cardBorder) {
          parts.push(`border: 1rpx solid ${style.cardBorder}`)
        }
        const radius = parseInt(style.cardRadius, 10)
        if (radius > 0) {
          parts.push(`border-radius: ${radius * rpxRatio}rpx`)
        }
        const padding = parseInt(style.cardPadding, 10)
        if (padding > 0) {
          parts.push(`padding: ${padding * rpxRatio}rpx`)
        }
        return parts.join('; ')
      }
    },

    watch: {
      // 登录/退出后重新获取会员信息
      userId() {
        this.getMemberInfo()
      }
    },

    created() {
      this.getMemberInfo()
    },

    mounted() {
      // 页面每次显示时刷新（登录状态、余额积分、默认车牌可能已变化）
      uni.$on('memberInfoRefresh', this.getMemberInfo)
    },

    beforeDestroy() {
      uni.$off('memberInfoRefresh', this.getMemberInfo)
    },

    methods: {

      // 获取会员信息与默认车牌
      getMemberInfo() {
        const app = this
        if (app.loading) {
          return
        }
        // 未登录：展示登录引导
        if (!app.userId) {
          app.resetMemberInfo()
          return
        }
        app.loading = true
        Promise.all([app.getUserInfo(), app.getVehicle()])
          .finally(() => {
            app.loading = false
          })
      },

      // 获取会员信息
      getUserInfo() {
        const app = this
        return new Promise((resolve) => {
          UserApi.info({}, { isPrompt: false, load: false })
            .then(result => {
              const userInfo = (result.data && result.data.userInfo) || null
              if (userInfo && userInfo.id) {
                app.userInfo = userInfo
              } else {
                app.resetMemberInfo()
              }
              resolve()
            })
            .catch(() => {
              app.resetMemberInfo()
              resolve()
            })
        })
      },

      // 获取默认车牌（接口无默认车牌时取第一条）
      getVehicle() {
        const app = this
        return new Promise((resolve) => {
          VehicleApi.list()
            .then(result => {
              const list = result.data || []
              app.vehicle = list.find(item => item.isDefault === 'Y') || list[0] || null
              resolve()
            })
            .catch(() => {
              app.vehicle = null
              resolve()
            })
        })
      },

      // 重置为未登录状态
      resetMemberInfo() {
        this.userInfo = { id: 0, avatar: '', name: '', balance: '', point: '' }
        this.vehicle = null
      }
    }
  }
</script>
