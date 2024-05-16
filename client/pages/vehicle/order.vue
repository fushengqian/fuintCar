<template>
  <view v-if="!isLoading" class="container">
    <!-- 我的车辆 -->
    <view class="row-item b-f m-top20 dis-flex" @click="switchVehicle()">
      <view class="row-title">服务车辆：</view>
      <view class="row-value">{{ vehicle.vehiclePlateNo ? vehicle.vehiclePlateNo : "--" }}</view>
      <image v-if="!vehicle.vehiclePlateNo" class="select" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAAEPUlEQVRoQ+2Zj3FTMQzGkw3oBLQTQCcgnQA6AekE0AlIJ6BMQDoB3YAwAWUCsgFsEPTrSXd6jv0s+yXH9a6+8738sWV90idZ9pvPnnibP3H9Z88AzIO73e6FfF5Kfyn9tXS+02lb6X+1/5Ln/Xw+57fJbZIHROlTVfqtPPlsCkcU28igH9LXU8B0AVBrf1LlW5QuAbsVENcR1OmYZgCi/EcRgvKp4lDkQa3K02jDmuYdnm+kvysoeyNAVi1AwgDU6p/V6n4NqHAnHV4DItREHiDeZ8BsRM5FSIgMCgFQrn+V8QsnGCtjsfvoYrlxCgTD4B1ryL6MxEYVgCr/PVngVpUPW7wGUtaBOlDTGrIvBARgim0UgNLmm7M8Qq9F6LqmUM//sh4exljWtrLW2RQAKG8Bh/JXUylTAyYg2EN+unGjMVH0gGYbuGkN5Y9i+RRUxhN4HdrutSwApc5vGW2psjtP1yxe+l90wHikbBreP88FdQnASiZYQG11cnPAagKYRbJJDojMh0pQisaOfZWO2wOgizLRrN9FHeUyAYkcssmm1RsZKp2lxsgB8NZv2lS8gkkMNe+wJkvk+ESyJycHAKstVACbSddGleT1KQDIgoB4jAXR58QbagBA6UPwPg6Wjsuauc/kAwKAgj6hDGiUAiDqLXVS21y28ta53lOx2wNqDMqYpcoepNQUQHFgK5BDeUABoDy60QaGTQH4gOnm/yEppLJ8HIwC8AFcTX2a5sjTlnK9o6j7F/rDRp6cvnKNTMf/xZbE5qA+Sj1AsJyqpL2c61fI1CytLPPja2thoD86YZCJUgAMMmuejGUgLTdM6BTlmTvq7RYP+K2b2mO0FtfDiG31KYgoheB0bR1foT7I+HNb7KkEMbFk54RBdZAC8BVgsYSN8OWIaXRQ1KUAlqJcNt9GlE6CfCXfraKdupEV66EUQDHa/xcATRbEpmXHQWzmijmfSrtKacAeikKaKKyYGwQw6+QA+DiYUk4fhEICwJc3eyfDHABPI0B2lRTJRtcrw2ef7H5ROlJ61NWrjVJ8KAiOlKN5fmS+L21iR0rlb+qFSSm1NQGoDr60bzvUZ4Kwm0qdypPOiUUra4ppuHYz512ILtXyokfhZP+gbLDLAP7aaBxmT4Y1AFgAYb7e6QrICDAtz0mZZvmtGq14rI1e7vprFnSZtLPmwOgthn/vgNJUqaMJoApA4wEPkJm8JxD8RRZYR6xbGqNWh++pbDbRavYKAVAQuNXfVJtO9woEroaalgco/EE6x0XfkANNi7Txg8MAbFLm0tfLAwxHRyzH/rFVZU/lOx0j2Csm47nNR+Eb6eT7kPJMbAbAJFUq97op5IFkEMpidfaabauALgDOG9AACvCuCwu3NLyE4ncRrpcETwLghWrZsJDfXikYANGxMJY169qL7mqARqxxMACRxY4x5hnAMazaIvMfmQ4TTxREm8cAAAAASUVORK5CYII="></image>
      <image v-if="vehicle.vehiclePlateNo" class="select" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAABmUlEQVRoQ+2YjW3CMBCF8QZ0A0boCLBB2QAmYBRGgA1KJygbwAYwAhuk7xCpICTxzz0bBZ0lCymK7e+7Z3CIGw28uYHzj0zg1QlaApaAsgK2hZQFVA+3BNQlVE7wfglUVTVFUdboE/Q9+o9zbqssVNJwsCwwcNXH8pAABnzh5u+W1faQmCVRJA4CywZDRaDZ5mDZ1RebAgIvEm2tmEQPvHBdIPDRJXC6xdVVt+wSHvgrFwT+Cx+TQC2VTSIEHhAP6zcFJrjhgD72bF26RCD8BVwzBHBs3UJyERN94uO3pEQE/PL+C3zdTm2VLiWBdSRp+cleeBKXyj/BdwqUSIIB3yuQU4IF7xXIIcGEDxJgSrDhgwUYEjngowQ0ErngowVSJHLCJwnESuD+s+Z33nM+pL+ZizjsfAydh5RvYHIC9cQECRW8WiByOzULqoanCCRKUOBpApESNHiqQKAEFZ4u4JGgw2cRuEncP+cLuPyDkud5OROo7f1ebFHLU2AyS6BAkXuXsAQsAWUFbAspC6gebgmoS6icYPAJ/AH539oxLCet/gAAAABJRU5ErkJggg=="></image>
    </view>
    <!-- 核销的卡券 -->
    <view class="row-item b-f m-top20 dis-flex" @click="selectCoupon()">
      <view class="row-title">使用次卡：</view>
      <view class="row-value">{{ couponInfo.name ? couponInfo.name : '--' }}</view>
      <image class="select" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAABmUlEQVRoQ+2YjW3CMBCF8QZ0A0boCLBB2QAmYBRGgA1KJygbwAYwAhuk7xCpICTxzz0bBZ0lCymK7e+7Z3CIGw28uYHzj0zg1QlaApaAsgK2hZQFVA+3BNQlVE7wfglUVTVFUdboE/Q9+o9zbqssVNJwsCwwcNXH8pAABnzh5u+W1faQmCVRJA4CywZDRaDZ5mDZ1RebAgIvEm2tmEQPvHBdIPDRJXC6xdVVt+wSHvgrFwT+Cx+TQC2VTSIEHhAP6zcFJrjhgD72bF26RCD8BVwzBHBs3UJyERN94uO3pEQE/PL+C3zdTm2VLiWBdSRp+cleeBKXyj/BdwqUSIIB3yuQU4IF7xXIIcGEDxJgSrDhgwUYEjngowQ0ErngowVSJHLCJwnESuD+s+Z33nM+pL+ZizjsfAydh5RvYHIC9cQECRW8WiByOzULqoanCCRKUOBpApESNHiqQKAEFZ4u4JGgw2cRuEncP+cLuPyDkud5OROo7f1ebFHLU2AyS6BAkXuXsAQsAWUFbAspC6gebgmoS6icYPAJ/AH539oxLCet/gAAAABJRU5ErkJggg=="></image>
    </view>

    <!-- 备注信息 -->
    <view class="row-textarea">
      <view class="row-title remark">备注信息：</view>
      <view class="content">
        <textarea class="textarea" v-model="remark" maxlength="2000" placeholder="请填写备注信息"
          placeholderStyle="color:#ccc"></textarea>
      </view>
    </view>

    <!-- 底部操作按钮 -->
    <view class="footer-fixed">
      <view class="btn-wrapper">
        <view class="btn-item btn-item-main" :class="{ disabled }" @click="handleSubmit()">确认提交</view>
      </view>
    </view>

     <!-- 车辆选择-->
    <u-popup v-model="showPopupforCar" mode="bottom">
      <view class="popup__coupon">
        <view class="coupon__title f-30">选择车辆</view>
        <!-- 卡券列表 -->
        <view class="coupon-list">
          <scroll-view :scroll-y="true" style="height: 565rpx;">
            <view class="coupon-item" v-for="(item, index) in vehicleList" :key="index">
              <view class="contacts" @click="useVehicle(item)">
                <VehiclePlate :plateNumber="getPlateArray(item.vehiclePlateNo)"/>
              </view>
            </view>
            <view class="no-coupon" v-if="vehicleList.length < 1">暂无可用次卡</view>
          </scroll-view>
        </view>
        <!-- 不使用卡券 -->
        <view class="coupon__do_not dis-flex flex-y-center flex-x-center">
          <view class="control dis-flex flex-y-center flex-x-center" @click="handleNotUseVehicle()">
            <text class="f-26">取 消</text>
          </view>
        </view>
      </view>
    </u-popup>

    <!-- 卡券弹出框 -->
    <u-popup v-model="showPopup" mode="bottom">
      <view class="popup__coupon">
        <view class="coupon__title f-30">选择次卡</view>
        <!-- 卡券列表 -->
        <view class="coupon-list">
          <scroll-view :scroll-y="true" style="height: 565rpx;">
            <view class="coupon-item" v-for="(item, index) in couponList" :key="index">
              <view class="item-wrapper"
                :class="[item.status == 'A' ? 'color-default': 'color-gray']"
                @click="useCoupon(item)">
                <view class="coupon-type">次卡</view>
                <view class="tip dis-flex flex-dir-column flex-x-center">
                  <text class="money">剩余{{ item.num }}次</text>
                  <text class="pay-line">{{ item.tips }}</text>
                </view>
                <view class="split-line"></view>
                <view class="content dis-flex flex-dir-column flex-x-between">
                  <view class="title">{{ item.name }}</view>
                  <view class="bottom dis-flex flex-y-center">
                    <view class="time flex-box">
                      <block>{{ item.effectiveDate }}</block>
                    </view>
                  </view>
                </view>
              </view>
            </view>
            <view class="no-coupon" v-if="couponList.length < 1">暂无可用次卡</view>
          </scroll-view>
        </view>
        <!-- 不使用卡券 -->
        <view class="coupon__do_not dis-flex flex-y-center flex-x-center">
          <view class="control dis-flex flex-y-center flex-x-center" @click="handleNotUseCoupon()">
            <text class="f-26">取 消</text>
          </view>
        </view>
      </view>
    </u-popup>
  </view>
</template>

<script>
  import * as VehicleApi from '@/api/vehicle'
  import * as MyCouponApi from '@/api/myCoupon'
  import VehiclePlate from '@/components/vehicle-plate/vehicle-plate.vue'

  export default {
    components: {VehiclePlate},
    data() {
      return {
        // 正在加载
        isLoading: true,
        vehicle: {},
        // 按钮禁用
        disabled: false,
        showPopup: false,
        showPopupforCar: false,
        remark: '',
        couponInfo: {},
        couponList: [],
        vehicleInfo: {},
        vehicleList: [],
        scanCode: ''
      }
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad({ code }) {
      // 扫码编码
      this.scanCode = code;
    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow() {
      // 获取默认车辆
      this.getMyVehicles();
      // 获取我的次卡
      this.getMyCoupon();
    },

    methods: {
      /**
       * 获取车牌的数组格式
       * @param {string} plate
       */
      getPlateArray(plate) {
         if (plate) {
             return plate.split("");
         }
         return [];
      },
      // 获取默认车辆
      getMyVehicles() {
        const app = this;
        app.isLoading = true;
        VehicleApi.list()
          .then(result => {
              app.vehicleList = result.data;
              if (app.vehicleList) {
                  app.vehicle = app.vehicleList[0];
              }
              app.isLoading = false
          })
      },

      // 获取次卡列表
      getMyCoupon() {
          const app = this;
          MyCouponApi.list({ type: 'T', status: 'A', page: 1, pageSize: 1000 }, { load: false })
            .then(result => {
                  app.couponList = result.data.content;
                  if (app.couponList && app.couponList.length > 0) {
                      app.couponInfo = app.couponList[0];
                  }
            })
      },

      // 切换车辆
      switchVehicle() {
        if (this.vehicleList < 1 ) {
          this.$navTo('pages/vehicle/index?action=switch');
        } else {
          this.showPopupforCar = true;
         }
      },

      handleNotUseVehicle() {
          this.showPopupforCar = false;
      },

      // 取消卡券
      handleNotUseCoupon() {
          this.showPopup = false;
      },

      // 选择卡券
      selectCoupon() {
          this.showPopup = true;
      },

      // 使用卡券
      useCoupon(couponInfo) {
          this.showPopup = false;
          if (couponInfo) {
              this.couponInfo = couponInfo;
          }
      },

      useVehicle(vehicleInfo) {
          this.showPopupforCar = false;
          if (vehicleInfo) {
              this.vehicle = vehicleInfo;
          }
      },

      // 表单提交
      handleSubmit() {
        const app = this
        // 判断是否重复提交
        if (app.disabled === true) {
            return false;
        }
        if (!app.vehicle || !app.vehicle.id) {
            app.$error('请选择您的车辆！');
            return false;
        }
        if (!app.couponInfo || !app.couponInfo.id) {
            app.$error('请先选择次卡！');
            return false;
        }
        // 按钮禁用
        app.disabled = true;
        app.onSubmit();
      },

      // 提交到后端
      onSubmit() {
        const app = this;
        const params = { vehicleId : app.vehicle.id, couponId: app.couponInfo.id, remark: app.remark, scanCode: app.scanCode };
        VehicleApi.submitOrder(params)
          .then(result => {
              if (result.code == '200') {
                app.$error('提交成功！');
                setTimeout(() => {
                    app.disabled = false;
                    uni.navigateBack();
                }, 4000)
              } else {
                  app.$error('提交失败，' + (result.message ? result.message : "系统繁忙"));
                  app.disabled = false;
              }
          })
          .catch(err => app.disabled = false)
      }
    }
  }
</script>

<style lang="scss" scoped>
  .container {
     padding: 50rpx 10rpx 100rpx 10rpx;
  }

  .row-title {
    color: #ffffff;
    margin-bottom: 20rpx;
    font-weight: bold;
    font-size: 32rpx;
    display: block;
    min-width: 140rpx;
    height: 50rpx;
    white-space: nowrap;
    float: left;
    margin-right: 0rpx;
  }
  .row-value {
      color: #ffffff;
  }

  /* 备注信息 */
  .row-textarea {
    padding: 30rpx 20rpx;
    .remark {
        color: #666666;
        margin: 10rpx;
    }
    .textarea {
      width: 100%;
      min-height: 220rpx;
      padding: 20rpx;
      border: 1rpx solid $fuint-theme;
      padding: 10rpx;
      border-radius: 5rpx;
      box-sizing: border-box;
      font-size: 30rpx;
    }
  }

  .row-item {
    padding: 40rpx 20rpx;
    font-size: 30rpx;
    background: $fuint-theme;
    margin-left: 20rpx;
    margin-right: 20rpx;
    border-radius: 10rpx;
    color: #ffffff;
    position: relative;
    .row-title {
      margin-bottom: 0;
      margin-right: 20rpx;
    }
    .switch {
        width: 48rpx;
        height: 48rpx;
        float: right;
        margin-left: 10rpx;
    }
    .select {
        width: 40rpx;
        height: 40rpx;
        margin-left: 10rpx;
        position: absolute;
        right: 30rpx;
    }
  }

  /* 次卡选择 */
  .popup__coupon {
    width: 750rpx;
    background: #fff;
    box-sizing: border-box;
    padding: 30rpx;

    .coupon__do_not {
      .control {
        width: 90%;
        height: 82rpx;
        margin-top: 30rpx;
        margin-bottom: 1rpx;
        color: #fff;
        background: $fuint-theme;
        border: 1rpx solid $fuint-theme;
        border-radius: 8rpx;
      }
    }
    .no-coupon {
        padding-top: 100rpx;
        color: #cccccc;
        text-align: center;
    }

    .coupon__title {
      text-align: center;
      margin-bottom: 30rpx;
    }

    .coupon-item {
      position: relative;
      overflow: hidden;
      margin-bottom: 22rpx;
    }

    .item-wrapper {
      width: 100%;
      display: flex;
      background: #fff;
      border-radius: 8rpx;
      color: #fff;
      height: 180rpx;

      .coupon-type {
        position: absolute;
        top: 0;
        right: 0;
        z-index: 10;
        width: 128rpx;
        padding: 6rpx 0;
        background: #fa2209;
        font-size: 20rpx;
        text-align: center;
        color: #ffffff;
        transform: rotate(45deg);
        transform-origin: 64rpx 64rpx;
      }

      &.color-default {
        background: linear-gradient(-128deg, $fuint-theme, $fuint-theme);
      }

      &.color-gray {
        background: linear-gradient(-113deg, #bdbdbd, #a2a1a2);
        .coupon-type {
          background: #9e9e9e;
        }
      }

      .content {
        flex: 1;
        padding: 30rpx 20rpx;
        border-radius: 16rpx 0 0 16rpx;

        .title {
          font-size: 32rpx;
        }

        .bottom {
          .time {
            font-size: 24rpx;
          }

          .receive {
            height: 46rpx;
            width: 122rpx;
            line-height: 46rpx;
            text-align: center;
            border: 1rpx solid #fff;
            border-radius: 6rpx;
            color: #fff;
            font-size: 24rpx;

            &.state {
              border: none;
            }
          }
        }
      }

      .tip {
        position: relative;
        flex: 0 0 32%;
        text-align: center;
        border-radius: 0 16rpx 16rpx 0;

        .money {
          font-weight: bold;
          font-size: 42rpx;
        }

        .pay-line {
          font-size: 22rpx;
          margin-top: 5rpx;
        }
      }

      .split-line {
        position: relative;
        flex: 0 0 0;
        border-left: 4rpx solid #fff;
        margin: 0 10rpx 0 6rpx;
        background: #fff;

        &:before,
          {
          border-radius: 0 0 16rpx 16rpx;
          top: 0;
        }

        &:after {
          border-radius: 16rpx 16rpx 0 0;
          bottom: 0;
        }

        &:before,
        &:after {
          content: '';
          position: absolute;
          width: 24rpx;
          height: 12rpx;
          background: #f7f7f7;
          left: -14rpx;
          z-index: 1;
        }
      }
    }
  }

  // 底部操作栏
  .footer-fixed {
    position: fixed;
    bottom: var(--window-bottom);
    left: 0;
    right: 0;
    height: 180rpx;
    padding-bottom: 30rpx;
    z-index: 11;
    box-shadow: 0 -4rpx 40rpx 0 rgba(144, 52, 52, 0.1);
    background: #fff;

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
      background: linear-gradient(to right, $fuint-theme, $fuint-theme);
      // 禁用按钮
      &.disabled {
        background: #cccccc;
      }
    }

  }
</style>
