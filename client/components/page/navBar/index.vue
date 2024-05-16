<template>
  <!-- 导航组 -->
  <view class="diy-navBar">
    <view class="data-list" :class="[`avg-sm-${itemStyle.rowsNum}`]">
      <view class="item-nav" v-for="(dataItem, index) in dataList" :key="index">
        <view class="nav-to" @click="onLink(dataItem.linkUrl)">
          <view class="item-image">
            <image class="image" mode="widthFix" :src="dataItem.imgUrl"></image>
          </view>
          <view class="item-text onelist-hidden">
             <view class="text">{{ dataItem.text }}</view>
             <view class="tip">{{ dataItem.tip }}</view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
  import mixin from '../mixin'

  export default {
    name: "NavBar",
    /**
     * 组件的属性列表
     * 用于组件自定义设置
     */
    props: {
      itemIndex: String,
      itemStyle: Object,
      params: Object,
      dataList: Array
    },

    mixins: [mixin],

    /**
     * 组件的方法列表
     * 更新属性和数据的方法与更新页面数据的方法类似
     */
    methods: {
        onLink(linkObj) {
            const app = this;
            if (linkObj == 'vehicleScan') {
                uni.scanCode({
                    success:function(res){
                       app.$navTo('pages/vehicle/order?code=' + res.result);
                    }
                });
            } else {
                 app.$navTo(linkObj);
            }
        }
    }

  }
</script>

<style lang="scss" scoped>
  .diy-navBar {
      background: #ffffff;
      padding: 10rpx;
      margin: 10rpx;
      border-radius: 20rpx;
  }
  .diy-navBar .data-list::after {
    clear: both;
    content: " ";
    display: table;
  }

  .item-nav {
    float: left;
    margin: 10rpx 0px 5rpx 0px;
    text-align: center;
    background: #ffffff;
    padding: 2rpx;
    color: #666666;
    .nav-to {
        border: 2rpx solid $fuint-theme;
        margin: 0rpx 2px 0px 2px;
        padding: 38rpx 20rpx 20rpx 20rpx;
        border-radius: 8rpx;
        background: #ffffff;
        height: 150rpx;
    }

    .item-text {
      text-align: left;
      padding-left: 20rpx;
      .text {
          font-size: 32rpx;
      }
      .tip {
          font-size: 22rpx;
          margin-top: 8rpx;
          color: #999;
      }
    }

    .item-image {
      margin-bottom: 4px;
      font-size: 0;
      margin-left: 30rpx;
      width: 88rpx;
      height: 88rpx;
      float: left;
    }

    .item-image .image {
      width: 80rpx;
      height: 80rpx;
    }

  }

  /* 分列布局 */
  .diy-navBar .avg-sm-3>.item-nav {
    width: 33.33333333%;
  }

  .diy-navBar .avg-sm-4>.item-nav {
    width: 25%;
  }

  .diy-navBar .avg-sm-2>.item-nav {
    width: 50%;
  }
</style>
