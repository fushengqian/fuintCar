<template>
    <view class="navigation">
        <view class="nav">
            <view class="nav-rows">
                <view class="item" v-for="item in navigation">
                    <image class="icon" :src="item.iconUrl" @click.stop="goUrl(item.url)"></image>
                    <view class="title">{{ item.name }}</view>
                </view>
            </view>
        </view>
    </view>
</template>

<script>
export default {
    props: {
        navigation: {
            type: Array,
            default: []
        }
    },
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
            margin-bottom: 10rpx;
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
