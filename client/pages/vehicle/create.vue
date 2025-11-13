<template>
  <view class="container">
    <view class="page-title">请输入您的车牌号</view>
    <!-- 表单组件 -->
    <view class="form-wrapper">
        <Plate :plateNumber="plateNumber" @myPlateChange="plateChange" />
    </view>
    
    <view class="form-wrapper">
      <view class="item">
        <text class="name">车辆品牌：</text>
        <input class="value" type="text" v-model="vehicleBrand" placeholder="请输入您的车辆品牌,如特斯拉/大众.."/>
      </view>
    </view>
    <view class="form-wrapper">
      <view class="item">
        <text class="name">车辆型号：</text>
        <input class="value" type="text" v-model="vehicleModel" placeholder="请输入您的车辆型号,如Model Y/途观.."/>
      </view>
    </view>
    <view class="form-wrapper">
      <view class="item">
        <text class="name">车辆类型：</text>
        <radio-group @change="radioChange">
           <label class="radio-item"><radio class="radio" value="C" color="#373F64" :checked="true"/>轿车</label>
           <label class="radio-item"><radio class="radio" value="S" color="#373F64" :checked="false"/>SUV</label>
           <label class="radio-item"><radio class="radio" value="M" color="#373F64" :checked="false"/>MPV</label>
        </radio-group>
      </view>
    </view>
    
    <!-- 操作按钮 -->
    <view class="footer">
      <view class="btn-wrapper">
        <view class="btn-item btn-item-main" :class="{ disabled }" @click="handleSubmit()">保存</view>
      </view>
    </view>
  </view>
</template>

<script>
  import Plate from '@/components/page/plate'
  import * as VehicleApi from '@/api/vehicle'
  export default {
    components: { Plate },
    data() {
      return {
        plateNumber: ['', '', '', '', '', '', '', ''],
        // 按钮禁用
        disabled: false,
        vehicleBrand: '',
        vehicleModel: '',
        vehicleType: 'C'
      }
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {},

    // 必须要在onReady生命周期，因为onLoad生命周期组件可能尚未创建完毕
    onReady() {
      this.plateNumber = ['', '', '', '', '', '', '', ''];
    },

    methods: {
      // 车牌
      plateChange(val) {
         this.plateNumber = val;
      },
      radioChange(e) {
          this.vehicleType = e.detail.value;
      },
      // 表单提交
      handleSubmit() {
        const app = this
        if (app.disabled) {
            return false
        }
        let plate = "";
        app.plateNumber.forEach((item, index) => {
            if (index === 1) {
                plate = plate + item;
            } else {
                plate += item;
            }
        })
        if (plate.length < 7) {
            app.$toast("请输入完整的车牌号码");
            return false;
        }
        VehicleApi.save(plate, app.vehicleBrand, app.vehicleModel, app.vehicleType, 'A', 0)
          .then(result => {
              app.$toast(result.message);
              uni.navigateBack();
              uni.setStorageSync("reflashHomeData", true);
          })
          .finally(() => app.disabled = false)
      }
    }
  }
</script>

<style>
  page {
    background: #f7f8fa;
  }
</style>
<style lang="scss" scoped>
  .page-title {
    width: 94%;
    margin: 0 auto;
    height: 30rpx;
    line-height: 30rpx;
    font-size: 32rpx;
    margin: 20rpx 0rpx 0rpx 30rpx;
    color: rgba(69, 90, 100, 0.6);
  }

  .form-wrapper {
    margin: 20rpx auto 10rpx auto;
    padding: 40rpx;
    width: 94%;
    border-radius: 16rpx;
    background: #fff;
    .value {
        border: solid 1rpx #00acab;
        padding: 15rpx;
        border-radius: 10rpx;
        margin-top: 5rpx;
    }
    .radio-item {
        margin-right: 50rpx;
        .radio {
            margin-right: -5rpx;
            vertical-align: middle;
            transform: scale(0.76);
        }
    }
  }

  /* 底部操作栏 */
  .footer {
    margin-top: 60rpx;
    .btn-wrapper {
      height: 100%;
      display: flex;
      align-items: center;
      padding: 0 20rpx;
    }
    .btn-item {
      flex: 1;
      font-size: 28rpx;
      height: 80rpx;
      line-height: 80rpx;
      text-align: center;
      color: #fff;
      border-radius: 40rpx;
    }

    .btn-item-main {
      background: $fuint-theme;
      // 禁用按钮
      &.disabled {
        background: #cccccc;
      }
    }
  }
</style>
