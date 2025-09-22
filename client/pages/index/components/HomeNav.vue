<template>
    <view class="navigation">
        <view class="nav">
            <view class="nav-rows">
                <view class="item">
                    <image class="icon" src="/static/nav/pay.png" @click.stop="goUrl('pages/pay/index')"></image>
                    <view class="title">买单支付</view>
                </view>
                <view class="item">
                    <image class="icon" src="/static/nav/xi.png" @click.stop="goUrl('pages/vehicle/order')"></image>
                    <view class="title">美容洗车</view>
                </view>
                <view class="item">
                    <image class="icon" src="/static/nav/add.png" @click.stop="goUrl('pages/wallet/recharge/index')"></image>
                    <view class="title">充值有礼</view>
                </view>
            </view>
            <view class="nav-rows">
                <view class="item">
                    <image class="icon" src="/static/nav/new.png" @click.stop="goUrl('pages/share/index')"></image>
                    <view class="title">邀请有礼</view>
                </view>
                <view class="item">
                    <image class="icon" src="/static/nav/service.png" @click.stop="goUrl('subPages/book/index')"></image>
                    <view class="title">预约服务</view>
                </view>
                <view class="item">
                    <image class="icon" src="/static/nav/coupon.png" @click.stop="goUrl('subPages/coupon/list')"></image>
                    <view class="title">领券中心</view>
                </view>
            </view>
        </view>
    </view>
</template>

<script>
export default {
    methods: {
        goUrl(url) {
            this.$navTo(url);
        }
    }
}
</script>

<style lang="scss" scoped>
.nav {
    margin-top: 10rpx;
    border-radius: 10rpx;
    background-color: #ffffff;
    box-shadow: #666;
    padding: 20rpx 0;
    border: solid 1rpx #ccc;
    margin: 0 10rpx 10rpx 10rpx;
    align-items: center;
    justify-content: center;
    height: 380rpx;
    .nav-rows {
        height: 120rpx;
        .item {
            text-align: center;
            justify-content: center;
            align-items: center;
            width: 33.33%;
            display: block;
            float: left;
            background: #fff;
            .icon {
                width: 60rpx;
                height: 60rpx;
                margin: 18rpx;
                padding: 15rpx;
                border: #666 solid 1rpx;
                border-radius: 60rpx;
                background: #f5f5f5;
            }
            .title {
                font-size: 24rpx;
                color: #666;
                font-weight: 600;
            }
        }
    }
}

</style>
