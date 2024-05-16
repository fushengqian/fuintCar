<template>
  <view class="container">
    <view class="addres-list">
      <radio-group>
      <view class="vehicle-item" v-for="(item, index) in list" :key="index">
        <view class="contacts">
          <VehiclePlate :plateNumber="getPlateArray(item.vehiclePlateNo)"/>
        </view>
        <view class="line"></view>
        <view class="item-option">
          <view class="_left">
            <label class="item-radio" @click="handleSetDefault(item.id)">
              <radio class="radio" color="#fa2209" :value="item.id" :checked="item.isDefault=='Y'?true:false"></radio>
              <text class="text">设为默认</text>
            </label>
          </view>
          <view class="_right">
            <view class="events">
              <view class="event-item" @click="handleRemove(item.id)">
                <text class="iconfont icon-delete"></text>
                <text class="title">删除</text>
              </view>
            </view>
          </view>
        </view>
      </view>
      </radio-group>
    </view>
    <empty v-if="!list.length" :isLoading="isLoading" tips="暂无车辆.."/>
    <!-- 底部操作按钮 -->
    <view class="footer-fixed">
      <view class="btn-wrapper">
        <view class="btn-item btn-item-main" @click="handleCreate()">添加新车辆</view>
      </view>
    </view>
  </view>
</template>

<script>
  import * as VehicleApi from '@/api/vehicle'
  import Empty from '@/components/empty'
  import VehiclePlate from '@/components/vehicle-plate/vehicle-plate.vue'

  export default {
    components: {
      Empty,
      VehiclePlate
    },
    data() {
      return {
        //当前页面参数
        options: {},
        // 正在加载
        isLoading: true,
        // 车辆列表
        list: []
      }
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
      // 当前页面参数
      this.options = options
    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow() {
      // 获取页面数据
      this.getPageData()
    },

    methods: {

      // 获取页面数据
      getPageData() {
        const app = this
        app.isLoading = true
        Promise.all([app.getVehicleList()])
          .then(() => {
            // 列表排序把默认收货车辆放到最前
            app.onReorder()
          })
          .finally(() => app.isLoading = false)
      },

      // 获取车辆列表
      getVehicleList() {
        const app = this
        return new Promise((resolve, reject) => {
          VehicleApi.list()
            .then(result => {
                app.list = result.data;
                resolve(result)
            })
            .catch(err => reject(err))
        })
      },

      // 列表排序把默认车辆放到最前
      onReorder() {
        // empty
      },

      /**
       * 添加新新车辆
       */
      handleCreate() {
        this.$navTo('pages/vehicle/create')
      },

      /**
       * 编辑车辆
       * @param {int} vehicleId 车辆ID
       */
      handleUpdate(vehicleId) {
        this.$navTo('pages/vehicle/update', { vehicleId })
      },

      /**
       * 删除车辆
       * @param {int} vehicleId 车辆ID
       */
      handleRemove(vehicleId) {
        const app = this
        uni.showModal({
          title: "提示",
          content: "您确定要删除当前车辆吗?",
          success({ confirm }) {
             confirm && app.onRemove(vehicleId)
          }
        });
      },

      /**
       * 确认删除车辆
       * @param {int} vehicleId 车辆ID
       */
      onRemove(vehicleId) {
        const app = this
        VehicleApi.remove(vehicleId, 'D')
          .then(result => {
              app.getPageData();
              uni.setStorageSync("reflashHomeData", true);
          })
      },
      
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

      /**
       * 设置为默认车辆
       * @param {Object} vehicleId
       */
      handleSetDefault(vehicleId) {
        const app = this;
        uni.showModal({
          title: "提示",
          content: "确定将该车辆设为默认吗?",
          success({ confirm }) {
             if (confirm) {
                 VehicleApi.setDefault(vehicleId, 'Y')
                   .then(result => {
                       app.list = [];
                       app.getVehicleList();
                       uni.setStorageSync("reflashHomeData", true);
                   })
             } else {
                 app.list = [];
                 app.getVehicleList();
             }
          }
        });
      }
    }
  }
</script>

<style lang="scss" scoped>
  .addres-list {
     padding-bottom: 160rpx;
  }

  // 项目内容
  .vehicle-item {
    margin: 20rpx auto 20rpx auto;
    padding: 30rpx 40rpx;
    width: 94%;
    box-shadow: 0 1rpx 5rpx 0px rgba(0, 0, 0, 0.05);
    border-radius: 16rpx;
    background: #fff;
  }

  .contacts {
    font-size: 30rpx;
    margin-bottom: 16rpx;

    .name {
      margin-right: 16rpx;
    }
  }

  .vehicle {
    font-size: 28rpx;

    .region {
      margin-right: 10rpx;
    }
  }

  .line {
    margin: 20rpx 0;
    border-bottom: 1rpx solid #f3f3f3;
  }

  .item-option {
    display: flex;
    justify-content: space-between;
    height: 48rpx;

    // 单选框
    .item-radio {
      font-size: 28rpx;

      .radio {
        vertical-align: middle;
        transform: scale(0.76)
      }

      .text {
        vertical-align: middle;
      }
    }

    // 操作
    .events {
      display: flex;
      align-items: center;
      line-height: 48rpx;

      .event-item {
        font-size: 28rpx;
        margin-right: 22rpx;
        color: #4c4c4c;

        &:last-child {
          margin-right: 0;
        }

        .title {
          margin-left: 8rpx;
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
    z-index: 11;
    box-shadow: 0 -4rpx 40rpx 0 rgba(144, 52, 52, 0.1);
    background: #fff;
    padding-bottom: 40rpx;

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
    }

  }
</style>
