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
                    <image class="icon" src="/static/nav/new.png" @click.stop="goUrl('pages/coupon/list?type=T')"></image>
                    <view class="title">新手福利</view>
                </view>
                <view class="item">
                    <image class="icon" src="/static/nav/service.png" @click.stop="goUrl('pages/book/index')"></image>
                    <view class="title">预约服务</view>
                </view>
                <view class="item">
                    <image class="icon" src="/static/nav/coupon.png" @click.stop="goUrl('pages/coupon/list?type=C')"></image>
                    <view class="title">领取卡券</view>
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
    height: 365rpx;
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
                margin: 28rpx;
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
