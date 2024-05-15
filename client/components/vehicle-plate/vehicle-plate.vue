<template>
  <view>
    <view class="plate" :class="computedClass">
      <view class="item" :class="computedClass" style="border-radius: 10rpx 0 0 10rpx">{{ plateNumber[0] }}</view>
      <view class="item" :class="computedClass">{{ plateNumber[1] }}</view>
      <view class="point" :class="computedClass">●</view>
      <view class="item" :class="computedClass">{{ plateNumber[2] }}</view>
      <view class="item" :class="computedClass">{{ plateNumber[3] }}</view>
      <view class="item" :class="computedClass">{{ plateNumber[4] }}</view>
      <view class="item" :class="computedClass">{{ plateNumber[5] }}</view>
      <view class="item" :class="computedClass" :style="computedStyle">{{ plateNumber[6] }}</view>
      <view class="item new-energy" v-if="plateNumber[7]" style="border-radius: 0 8rpx 8rpx 0">
          <view v-if="plateNumber[7] || plateNumber[7] === 0">
            <text>{{ plateNumber[7] }}</text>
          </view>
      </view>
    </view>
  </view>
</template>

<script>
import { computed } from "vue"
export default {
  name: 'VehiclePlate',
  props: {
    plateNumber: {
      type: Array,
      default: Array.from({
        length: 8
      }, v => '')
    },
  },
  computed: {
    computedStyle: function(){
      if ( this.plateNumber.length == 7 ) {
           return "border-radius: 0 10rpx 10rpx 0"
      }
    },
    computedClass: function(){
      if ( this.plateNumber.length == 7 ) {
           return "active"
      }
    }
  }
}
</script>

<style  lang="scss" scoped>
.plate {
    display: flex;
    border: 4rpx solid #000000;
    box-shadow: 0 5rpx 5rpx rgba(0,0,0,0.5);
    justify-content: space-between;
    border-radius: 16rpx;
    &.active {
        border: 4rpx solid #FFFFFF;
    }
    .item {
        width: 100%;
        height: 80rpx;
        background-color: #06aa4b;
       // border-radius: 8rpx;
        text-align: center;
        line-height: 80rpx;
        font-size: 32rpx;
        color: #000000;
        font-weight: bold;
        position: relative;
        &.active {
            background-color: #0041c5;
            color: #FFFFFF;
        }
    }
    .new-energy {
        box-sizing: border-box;
        border: 2rpx dashed #03BE9F;
        font-weight: bold;
        uni-icons {
            display: flex;
            align-items: center;
            justify-content: center;
        }
    }
    .point {
        height: 80rpx;
        text-align: center;
        line-height: 80rpx;
        color: #BDC4CC;
        font-size: 18rpx;
        background-color: #06aa4b;
        &.active {
            background-color: #0041c5;
            color: #FFFFFF;
        }
    }
    .triangle {
        width: 0;
        height: 0;
        border: 6rpx solid transparent;
        border-right-color: #00C69D;
        border-bottom-color: #00C69D;
        border-radius: 1rpx 2rpx 1rpx;
        position: absolute;
        right: 6rpx;
        bottom: 6rpx;
    }
}

</style>