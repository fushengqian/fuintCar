<template>
  <view class="container">
      <empty v-if="!storeInfo" :isLoading="isLoading" tips="数据加载中..."></empty>
      <block v-if="storeInfo">
          <Location :storeInfo="storeInfo"/>
      </block>
      <block>
          <Search v-if="storeInfo" tips="请输入搜索关键字" @event="$navTo('pages/search/index')"/>
      </block>
      <block>
          <Banner v-if="storeInfo" :itemStyle="options.bannerStyle" :params="options.bannerParam" :dataList="banner"/>
      </block>
      <block>
          <Blank v-if="storeInfo" :itemStyle="options.blankStyle"/>
      </block>
      <block>
          <Vehicle v-if="storeInfo" :vehicle="vehicle"/>
      </block>
      <block>
          <Blank v-if="storeInfo" :itemStyle="options.blankStyle"/>
      </block>
      <block>
          <NavBar v-if="storeInfo" :itemStyle="options.navStyle" :params="{}" :dataList="options.navBar"/>
      </block>
      <block>
          <Blank v-if="storeInfo" :itemStyle="options.blankStyle"/>
      </block>
      <block>
          <Goods v-if="storeInfo" :itemStyle="options.goodsStyle" :isReflash="isReflash" ref="mescrollItem" :params="options.goodsParams"/>
      </block>
  </view>
</template>

<script>
  import { setCartTabBadge, showMessage } from '@/utils/app'
  import Location from '@/components/page/location'
  import Search from '@/components/search'
  import Banner from '@/components/page/banner'
  import NavBar from '@/components/page/navBar'
  import Blank from '@/components/page/blank'
  import Empty from '@/components/empty'
  import Goods from '@/components/page/goods'
  import Vehicle from '@/components/page/vehicle'
  import * as settingApi from '@/api/setting'
  import * as Api from '@/api/page'
  import MescrollCompMixin from "@/components/mescroll-uni/mixins/mescroll-comp.js";

  const App = getApp()
  
  export default {
    mixins: [MescrollCompMixin],
    components: {
       Location,
       Search,
       Banner,
       NavBar,
       Blank,
       Empty,
       Goods,
       Vehicle
    },
    data() {
      return {
        options: {
            "blankStyle": {
                "height": "5",
                "background": "#ffffff",
            },
            "navBar": [{
                        "imgUrl": "/static/nav/6.png",
                        "imgName": "icon-1.png",
                        "linkUrl": "pages\/vehicle\/order",
                        "text": "到店洗车",
                        "tip": "到店洗车服务",
                        "color": "#666666"
                    },{
                        "imgUrl": "/static/nav/1.png",
                        "imgName": "icon-1.png",
                        "linkUrl": "pages\/pay\/index",
                        "text": "买单支付",
                        "tip": "支付攒积分",
                        "color": "#666666"
                    }, {
                        "imgUrl": "/static/nav/3.png",
                        "imgName": "icon-1.png",
                        "linkUrl": "pages\/coupon\/list?type=C",
                        "text": "领券中心",
                        "tip": "积分换好礼",
                        "color": "#666666"
                    }, {
                        "imgUrl": "/static/nav/2.png",
                        "imgName": "icon-1.png",
                        "linkUrl": "pages\/coupon\/list?type=P",
                        "text": "预存充值",
                        "tip": "充值有优惠",
                        "color": "#666666",
                    }],
            "goodsStyle": {
                "background": "#F6F6F6",
                "display": "list",
                "column": 1,
                "show": ["goodsName", "goodsPrice", "linePrice", "sellingPoint", "goodsSales"]
            },
            "goodsParams": {
                "source": "auto",
                "auto": {
                    "category": 0,
                    "goodsSort": "all",
                    "showNum": 40
                }
            },
            "bannerStyle": {
                "btnColor": "#ffffff",
                "btnShape": "round",
                "interval": 2.5,
                
            },
            "bannerParam": {
                "interval": 2000
            },
            "navStyle": {
                "background": "#ffffff",
                "rowsNum": "2",
            }
        },
        banner: [],
        storeInfo: null,
        isReflash: false,
        vehicle: { vehiclePlateNo : ''},
        isLoading: false
      }
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad() {
      this.getPageData();
    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow() {
      const app = this;
      showMessage();
      setCartTabBadge();
      app.onGetStoreInfo();
      let isReflash = uni.getStorageSync("reflashHomeData");
      app.isReflash = isReflash;
      if (isReflash === true) {
          app.getPageData();
      }
      uni.getLocation({
          type: 'gcj02',
          success(res){
              uni.setStorageSync('latitude', res.latitude);
              uni.setStorageSync('longitude', res.longitude);
              app.onGetStoreInfo();
          },
          fail(e) {
             // empty
          }
      })
    },

    methods: {
        
        /**
         * 加载页面数据
         * @param {Object} callback
         */
        getPageData(callback) {
          const app = this
          Api.home()
            .then(result => {
                 app.banner = result.data.banner;
                 if (result.data.vehicle) {
                     app.vehicle = result.data.vehicle;
                 }
                 uni.removeStorageSync("reflashHomeData");
                 app.isReflash = false;
            })
            .finally(() => callback && callback())
        },
        
        /**
         * 下拉刷新
         */
        onPullDownRefresh() {
          // 获取数据
          this.getPageData(() => {
             uni.stopPullDownRefresh()
          })
        },
        
        /**
         * 获取默认店铺
         * */
         onGetStoreInfo() {
            const app = this
            settingApi.systemConfig()
             .then(result => {
               app.storeInfo = result.data.storeInfo;
               if (app.storeInfo) {
                   uni.setStorageSync("storeId", app.storeInfo.id)
               }
             })
         }
    },

    /**
     * 分享当前页面
     */
    onShareAppMessage() {
      const app = this
      return {
         title: "fuint会员系统",
         path: "/pages/index/index?" + app.$getShareUrlParams()
      }
    },

    /**
     * 分享到朋友圈
     * 本接口为 Beta 版本，暂只在 Android 平台支持，详见分享到朋友圈 (Beta)
     * https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/share-timeline.html
     */
    onShareTimeline() {
      const app = this
      const { page } = app
      return {
        title: page.params.share_title,
        path: "/pages/index/index?" + app.$getShareUrlParams()
      }
    }

  }
</script>

<style lang="scss" scoped>
  .container {
    background: #f5f5f5;
  }
</style>
