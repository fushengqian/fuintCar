<template>
  <view class="container" :style="themeVars">
    <!--店铺切换 + 搜索框：作为一个整体吸顶固定，不随页面滚动 -->
    <view class="cate-sticky-header">
      <Location v-if="storeInfo" :storeInfo="storeInfo"/>
    
      <Search position="static" tips="请输入搜索关键字..." @event="$navTo('pages/search/index')" />
    </view>

    <view class="cate-content dis-flex" v-if="list.length > 0">
      <!-- 左侧 分类 -->
      <scroll-view class="cate-left f-28" scroll-y :show-scrollbar="false" :enhanced="true" :style="{ height: `${scrollHeight}px` }">
          <view v-for="(item, index) in list" :key="index">
              <text class="cart-badge" v-if="item.total">{{ item.total }}</text>
              <view class="type-nav" :class="{ selected: curIndex == index }" @click="handleSelectNav(index)">
                  <image class="logo" lazy-load :lazy-load-margin="0" :src="item.logo ? item.logo : '/static/empty-02.png'"></image>
                  <view class="name">{{ item.name }}</view>
              </view>
          </view>
      </scroll-view>

      <!-- 右侧 商品 -->
      <scroll-view 
        class="cate-right b-f" 
        :scroll-top="scrollTop" 
        :scroll-y="true" 
        :style="{ height: `${scrollHeight}px` }"
        @scroll="handleScroll"
        scroll-with-animation
        :scroll-into-view="scrollIntoView"
      >
        <view v-if="list[curIndex]">
          <view class="cate-right-cont">
            <view class="cate-two-box">
              <view v-if="list[curIndex].goodsList.length" class="cate-cont-box">
                <!-- 为每个分类添加锚点 -->
                <view v-for="(category, catIndex) in list" :key="catIndex" :id="`category-${catIndex}`">
                  <view class="category-title">{{category.name}}</view>
                  <view class="flex-five item" v-for="(item, idx) in category.goodsList" :key="idx">
                    <view class="cate-img">
                      <view class="img-wrap">
                        <image v-if="item.logo" lazy-load :lazy-load-margin="0" :src="item.logo" @click="onTargetGoods(item.id)"></image>
                        <view class="member-tag" v-if="item.gradeIds">会员专属</view>
                      </view>
                    </view>
                    <view class="cate-info">
                      <view class="base">
                        <text class="name text">{{ item.name }}</text>
                        <text class="salepoint text" v-if="item.salePoint">{{ item.salePoint }}</text>
                        <text class="stock text">库存:{{ item.stock ? item.stock : 0 }} 已售:{{ item.initSale ? item.initSale : 0 }}</text>
                      </view>
                      <view class="action">
                          <text class="price">￥{{ item.price ? item.price : 0 }}</text>
                          <view class="cart">
                              <view v-if="item.isSingleSpec === 'Y'" class="singleSpec">
                                  <view class="ii do-minus" v-if="item.buyNum" @click="onSaveCart(item.id, '-')"></view>
                                  <view class="ii num" v-if="item.buyNum">{{ (item.buyNum != undefined) ? item.buyNum : 0 }}</view>
                                  <view class="ii do-add" v-if="item.stock > 0" @click="onSaveCart(item.id, '+')"></view>
                              </view>
                              <view v-if="item.isSingleSpec === 'N'" class="multiSpec">
                                  <text class="num-badge" v-if="item.buyNum">{{ item.buyNum }}</text>
                                  <view class="select-spec" @click="onShowSkuPopup(2, item.id)">选规格</view>
                              </view>
                          </view>
                      </view>
                    </view>
                  </view>
                </view>
              </view>
              <empty v-if="!list[curIndex].goodsList.length" :isLoading="isLoading" tips="暂无商品~"></empty>
            </view>
          </view>
        </view>
      </scroll-view>
    </view>
    
    <!-- 商品SKU弹窗 -->
    <SkuPopup v-if="!isLoading" v-model="showSkuPopup" :skuMode="skuMode" :goods="goods" @addCart="onAddCart"/>
    
    <view class="flow-fixed-footer b-f">
      <view class="dis-flex chackout-box">
        <view class="chackout-left pl-12">
          <view class="col-amount-do">总金额：<text class="amount">￥{{ totalPrice.toFixed(2) }}</text></view>
          <view class="col-amount-view">共计：{{ totalNum }} 件</view>
        </view>
        <view class="chackout-right" @click="doSubmit()">
          <view class="flow-btn f-32">去结算</view>
        </view>
      </view>
    </view>
    
    <empty v-if="!list.length" :isLoading="isLoading" />
  </view>
</template>

<script>
  import { setCartTabBadge, setCartTotalNum } from '@/utils/app'
  import * as CartApi from '@/api/cart'
  import * as GoodsApi from '@/api/goods'
  import * as settingApi from '@/api/setting'
  import Search from '@/components/search'
  import Empty from '@/components/empty'
  import SkuPopup from './components/SkuPopup'
  import Location from '@/components/page/location'

  const App = getApp()

  export default {
    components: {
      Search,
      SkuPopup,
      Empty,
      Location
    },
    data() {
      return {
        goodsCart: [],
        totalNum: 0,
        totalPrice: 0.00,
        // 列表高度
        scrollHeight: 500,
        // 吸顶头部(门店信息 + 搜索框)实测高度
        headerHeight: 0,
        // 一级分类：指针
        curIndex: 0,
        // 内容区竖向滚动条位置
        scrollTop: 0,
        // 分类列表
        list: [],
        // 正在加载中
        isLoading: true,
        showSkuPopup: false,
        skuMode: 1,
        goods: {},
        storeInfo: null,
        // 用于自动滚动定位
        scrollIntoView: '',
        // 存储每个分类的位置信息
        categoryPositions: [],
        // 防抖计时器
        scrollTimer: null,
        // 是否正在手动切换分类
        isManualSelect: false
      }
    },

    onLoad() {
      const app = this
      app.updateLayout()
    },

    onReady() {
      // 首次渲染完成后按实测高度重新计算列表高度
      this.updateLayout();
    },

    onShow() {
      const app = this;
      app.getPageData();
      app.onGetStoreInfo();
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
      getPageData() {
        const app = this
        app.isLoading = true
        Promise.all([
            GoodsApi.cateList(),
            CartApi.list()
          ])
          .then(result => {
            app.list = result[0].data;
            app.totalNum = result[1].data.totalNum;
            app.goodsCart = result[1].data.list;
            setCartTotalNum(app.totalNum);
            setCartTabBadge();
            
            // 数据加载完成后，计算分类位置
            this.$nextTick(() => {
              this.calculateCategoryPositions();
            });
          })
          .finally(() => {
              app.isLoading = false
              app.totalPrice = 0
              app.list.forEach(function(item, index) {
                  let total = 0
                  item.goodsList.forEach(function(goods, key) {
                      let totalBuyNum = 0
                      app.goodsCart.forEach(function(cart){
                         if (goods.id == cart.goodsId) {
                             total = total + cart.num
                             totalBuyNum = totalBuyNum + cart.num
                             app.totalPrice = app.totalPrice + (cart.goodsInfo.price * cart.num)
                         } 
                      })
                      app.$set(app.list[index].goodsList[key], 'buyNum', totalBuyNum)
                  })
                  app.$set(app.list[index], 'total', total)
              })
          })
      },
      
      // 计算每个分类的位置信息
      calculateCategoryPositions() {
        const query = uni.createSelectorQuery().in(this);
        this.categoryPositions = [];
        
        this.list.forEach((item, index) => {
          query.select(`#category-${index}`).boundingClientRect();
        });
        
        query.exec(res => {
          res.forEach((rect, index) => {
            if (rect) {
              this.categoryPositions.push({
                index,
                top: rect.top,
                height: rect.height
              });
            }
          });
        });
      },
      
      // 滚动事件处理
      handleScroll(e) {
        if (this.isManualSelect) {
          this.isManualSelect = false;
          return;
        }
        
        // 防抖处理
        clearTimeout(this.scrollTimer);
        this.scrollTimer = setTimeout(() => {
          const scrollTop = e.detail.scrollTop;
          this.updateActiveCategory(scrollTop);
        }, 50);
      },
      
      // 根据滚动位置更新当前激活的分类
      updateActiveCategory(scrollTop) {
        if (!this.categoryPositions.length) return;
        
        // 增加一个偏移量，提前切换分类
        const offset = 100;
        const adjustedScrollTop = scrollTop + offset;
        
        // 找到当前应该激活的分类
        let activeIndex = 0;
        for (let i = 0; i < this.categoryPositions.length; i++) {
          const position = this.categoryPositions[i];
          if (adjustedScrollTop >= position.top) {
            activeIndex = position.index;
          } else {
            break;
          }
        }
        
        // 更新当前激活的分类
        if (this.curIndex !== activeIndex) {
          this.curIndex = activeIndex;
        }
      },
      
      onGetStoreInfo() {
         const app = this
         settingApi.systemConfig()
           .then(result => {
               app.storeInfo = result.data.storeInfo;
               // 门店信息渲染后头部高度会变化，重新计算列表高度
               app.updateLayout();
           })
       },
      
      onTargetGoods(goodsId) {
        this.$navTo(`pages/goods/detail`, { goodsId })
      },

      /**
       * 计算吸顶头部(门店信息 + 搜索框)与底部结算栏的实测高度，
       * 据此设置分类列表高度，让整页不再滚动，门店信息始终固定在顶部
       */
      updateLayout() {
        const app = this
        this.$nextTick(() => {
          const query = uni.createSelectorQuery().in(this)
          query.select('.cate-sticky-header').boundingClientRect()
          query.select('.flow-fixed-footer').boundingClientRect()
          query.exec(res => {
            const header = (res && res[0]) || {}
            const footer = (res && res[1]) || {}
            if (header.height) {
              app.headerHeight = header.height
            }
            uni.getSystemInfo({
              success(sys) {
                // 取不到实测值时按 240rpx(头部) / 120rpx(结算栏) 估算
                const headerHeight = app.headerHeight || (240 * sys.windowWidth / 750)
                const footerHeight = footer.height || (120 * sys.windowWidth / 750)
                app.scrollHeight = Math.max(200, sys.windowHeight - headerHeight - footerHeight)
              }
            })
          })
        })
      },

      // 一级分类：选中分类
      handleSelectNav(index) {
        this.isManualSelect = true;
        this.curIndex = index;
        this.scrollIntoView = `category-${index}`;
        setTimeout(() => {
          this.scrollIntoView = '';
        }, 500);
      },
      
      onSaveCart(goodsId, action) {
        const app = this
        return new Promise((resolve, reject) => {
          CartApi.save(goodsId, action)
            .then(result => {
                app.getPageData();
                resolve(result);
            })
            .catch(err => {
               // 错误提示已由全局拦截器处理
               reject(err);
            })
        })
      },
      
      onAddCart(total) {
        this.getPageData();
        this.$toast("添加购物车成功");
      },
      
      doSubmit() {
        if (this.totalPrice > 0) {
            this.$navTo('pages/cart/index')
        } else {
            this.$error("请先选择商品")
        }
      },
      
      onShowSkuPopup(skuMode, goodsId) {
        const app = this
        app.isLoading = true
        return new Promise((resolve, reject) => {
          GoodsApi.detail(goodsId)
            .then(result => {
              const goodsData = result.data
              
              if (goodsData.skuList) {
                  goodsData.skuList.forEach(function(sku, index) {
                    goodsData.skuList[index].specIds = sku.specIds.split('-')
                    goodsData.skuList[index].skuId = sku.id
                  })
              }
              
              app.goods = goodsData
              app.skuMode = skuMode
              app.showSkuPopup = !app.showSkuPopup
              app.isLoading = false
              resolve(result)
            })
            .catch(err => reject(err))
        })
      },
    },

    onShareAppMessage() {
      const app = this
      return {
        title: _this.templet.shareTitle,
        path: '/pages/category/index?' + app.$getShareUrlParams()
      }
    },

    onShareTimeline() {
      const app = this
      return {
        title: _this.templet.shareTitle,
        path: '/pages/category/index?' + app.$getShareUrlParams()
      }
    }
  }
</script>

<style>
  page {
    background: #fff;
  }
</style>
<style lang="scss" scoped>
  // 吸顶头部：门店信息 + 搜索框整体固定，不随页面滚动
  .cate-sticky-header {
    position: sticky;
    top: 0;
    z-index: 100;
    background: #ffffff;
  }

  .cate-content {
    background: #fff;
  }
  .cate-wrapper {
    padding: 0 20rpx 20rpx 20rpx;
    box-sizing: border-box;
    overflow: hidden;
  }
  /* 分类内容 */
  .cate-content {
    width: 100%;
    overflow: hidden;
  }
  .cate-left {
    flex-direction: column;
    display: flex;
    width: 200rpx;
    color: #444;
    height: 100%;
    background: #f8f8f8;
    overflow: hidden;
    &::-webkit-scrollbar {
        display: none !important;
        width: 0 !important;
        height: 0 !important;
    }
    .cart-badge {
      position: absolute;
      right: 1rpx;
      margin-top: 10rpx;
      margin-right: 5rpx;
      font-size: 18rpx;
      background: #fa5151;
      z-index: 999;
      text-align: center;
      line-height: 28rpx;
      color: #ffffff;
      border-radius: 50%;
      min-width: 32rpx;
      padding: 5rpx 13rpx 5rpx 13rpx;
    }
  }
  .cate-right {
    display: flex;
    flex-direction: column;
    width: 100%;
    height: 100%;
    overflow: hidden;
  }

  .cate-right-cont {
    width: 100%;
    display: flex;
    flex-flow: row wrap;
    align-content: flex-start;
    padding-top: 10rpx;
  }

  .type-nav {
    position: relative;
    height: 140rpx;
    text-align: center;
    z-index: 10;
    display: block;
    font-size: 26rpx;
    padding: 20rpx 0rpx 126rpx 0rpx;
    .logo {
        width: 60rpx;
        height: 60rpx;
        border-radius: 60rpx;
        margin: 0rpx;
        padding: 0rpx;
    }
    .name {
        margin-top: 2rpx;
        width: 100%;
        overflow-x: hidden;
        height: 40rpx;
        line-height: 40rpx;
        text-align: center;
    }
  }

  .type-nav.selected {
    color: #666666;
    background: #ffffff;
    border-right: none;
    border-left: solid 10rpx #f03c3c;
    font-weight: bold;
    font-size: 28rpx;
  }

  .cate-cont-box {
    margin-bottom: 10rpx;
    padding-bottom: 10rpx;
    overflow: hidden;
    height: auto;
    display: block;
    .item {
        height: 220rpx;
        display: block;
        padding-top: 5rpx;
        border-radius: 3rpx;
        margin-bottom: 5rpx;
    }
    
    .category-title {
      font-size: 32rpx;
      font-weight: bold;
      padding: 20rpx;
      background: #f8f8f8;
      margin-bottom: 10rpx;
    }
  }

  .cate-cont-box .cate-img {
    padding: 13rpx 10rpx 4rpx 10rpx;
    display: block;
  }
  
  .cate-cont-box .cate-img .img-wrap {
    position: relative;
    .member-tag {
      position: absolute;
      top: 0;
      right: 0;
      padding: 4rpx 12rpx;
      font-size: 20rpx;
      color: #fff;
      background: linear-gradient(135deg, #d4a843, #b8860b);
      border-radius: 0 0 0 12rpx;
      z-index: 2;
    }
  }  

  .cate-cont-box .cate-img image {
    width: 160rpx;
    height: 150rpx;
    float: left;
    border-radius: 5rpx;
    display: block;
    margin-top: 5rpx;
  }

  .cate-cont-box .cate-info {
    text-align: left;
    display: flex;
    flex-direction: column;
    font-size: 26rpx;
    margin-left: 168rpx;
    padding-bottom: 14rpx;
    color: #444;
    padding: 0 15rpx 30rpx 15rpx;
    .base {
        height: 100%;
        display: block;
        .text {
            display: block;
            float: left;
            width: 100%;
        }
        .name {
            font-weight: bold;
            width: 100%;
            font-size: 26rpx;
            overflow: hidden;
            display: -webkit-box;
            -webkit-box-orient: vertical;
            -webkit-line-clamp: 2;
        }
        .salepoint {
            font-size: 22rpx;
            color: #e49a3d;
            margin-top: 6rpx;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }
        .stock {
            margin-top: 10rpx;
            color: #999;
        }
    }
    .action {
        display: block;
        height: 50rpx;
        .price {
            margin-top: 20rpx;
            color: #f03c3c;
            float: left;
            font-size: 32rpx;
            font-weight: bold;
        }
        .cart {
            margin-top: 20rpx;
            float: right;
            font-size: 30rpx;
            height: 60rpx;
            .ii {
                float: left;
                text-align: center;
                width: 60rpx;
                cursor: pointer;
            }
            // 加购按钮：改为用主题色绘制(原来是固定颜色的图片，不跟随主题)
            .do-add {
                position: relative;
                margin: 0 auto;
                background: var(--theme-primary);
                border-radius: 50%;
                width: 45rpx;
                height: 45rpx;
                &::before,
                &::after {
                    content: '';
                    position: absolute;
                    top: 50%;
                    left: 50%;
                    transform: translate(-50%, -50%);
                    background: var(--theme-primary-text);
                }
                &::before {
                    width: 22rpx;
                    height: 3rpx;
                }
                &::after {
                    width: 3rpx;
                    height: 22rpx;
                }
            }
            .do-minus {
                position: relative;
                margin: 0 auto;
                background: var(--theme-primary);
                border-radius: 50%;
                width: 45rpx;
                height: 45rpx;
                &::before {
                    content: '';
                    position: absolute;
                    top: 50%;
                    left: 50%;
                    transform: translate(-50%, -50%);
                    width: 22rpx;
                    height: 3rpx;
                    background: var(--theme-primary-text);
                }
            }
            .multiSpec {
                .num-badge {
                    position: absolute;
                    margin-top: 10rpx;
                    margin-right: 25rpx;
                    font-size: 18rpx;
                    background: #f03c3c;
                    text-align: center;
                    line-height: 36rpx;
                    color: #ffffff;
                    border-radius: 50%;
                    min-width: 36rpx;
                    padding: 2rpx;
                    right: 90rpx;
                }
                .select-spec {
                    border: solid 1rpx var(--theme-primary);
                    padding: 10rpx 20rpx 10rpx 20rpx;
                    font-size: 25rpx;
                    border-radius: 5rpx;
                    color: var(--theme-primary-text);
                    background: var(--theme-primary);
                }
            }
        }
    }
  }
  .cate-two-box {
    width: 100%;
    padding: 0 2px;
  }
  
  // 底部操作栏
  .flow-fixed-footer {
    position: fixed;
    bottom: var(--window-bottom);
    width: 100%;
    background: #fff;
    border-top: 1px solid #eee;
    z-index: 11;
    padding-top: 8rpx;
    .chackout-left {
      font-size: 28rpx;
      height: 98rpx;
      color: #777;
      flex: 4;
      padding-left: 12px;
        text-align: right;
        padding-right: 40rpx;
        .col-amount-do {
          font-size: 35rpx;
          margin-top: 5rpx;
          margin-bottom:5rpx;
          .amount {
              color: #f03c3c;
              font-weight: bold;
          }
        }
    }
    
    .chackout-right {
      font-size: 34rpx;
      flex: 2;
    }
    
    // 提交按钮
    .flow-btn {
      // 结算按钮跟随主题色
      background: linear-gradient(to right, var(--theme-primary), var(--theme-primary));
      color: var(--theme-primary-text);
      text-align: center;
      line-height: 92rpx;
      display: block;
      font-size: 28rpx;
      border-radius: 5rpx;
      margin-right: 20rpx;
       // 禁用按钮
       &.disabled {
         background: #ff9779;
       }
    }
  }
</style>