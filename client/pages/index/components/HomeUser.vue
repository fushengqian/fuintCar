<template>
    <view class="com-user">
        <view class="user-main">
            <image class="avatar" :src="userInfo.avatar ? userInfo.avatar : '/static/default-avatar.png'"></image>
            <view class="uc">
                <view class="name">Hi，你好！</view>
                <view class="tip" v-if="!userInfo.id">为了向您提供更好的服务，请登录！</view>
                <view class="tip" v-if="userInfo.id">
                    <view>余额：<text>{{ userInfo.balance }}</text></view>
                    <view>积分：<text>{{ userInfo.point }}</text></view>
                </view>
            </view>
            <view class="ur" v-if="!userInfo.id" @click="goLogin">登录</view>
            <view class="plate" v-if="userInfo.id" @click="toVehicle">
                <text class="text">车牌：{{ vehicle ? vehicle.vehiclePlateNo : '' }}</text>
                <image v-if="!vehicle" class="edit" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAAEPUlEQVRoQ+2Zj3FTMQzGkw3oBLQTQCcgnQA6AekE0AlIJ6BMQDoB3YAwAWUCsgFsEPTrSXd6jv0s+yXH9a6+8738sWV90idZ9pvPnnibP3H9Z88AzIO73e6FfF5Kfyn9tXS+02lb6X+1/5Ln/Xw+57fJbZIHROlTVfqtPPlsCkcU28igH9LXU8B0AVBrf1LlW5QuAbsVENcR1OmYZgCi/EcRgvKp4lDkQa3K02jDmuYdnm+kvysoeyNAVi1AwgDU6p/V6n4NqHAnHV4DItREHiDeZ8BsRM5FSIgMCgFQrn+V8QsnGCtjsfvoYrlxCgTD4B1ryL6MxEYVgCr/PVngVpUPW7wGUtaBOlDTGrIvBARgim0UgNLmm7M8Qq9F6LqmUM//sh4exljWtrLW2RQAKG8Bh/JXUylTAyYg2EN+unGjMVH0gGYbuGkN5Y9i+RRUxhN4HdrutSwApc5vGW2psjtP1yxe+l90wHikbBreP88FdQnASiZYQG11cnPAagKYRbJJDojMh0pQisaOfZWO2wOgizLRrN9FHeUyAYkcssmm1RsZKp2lxsgB8NZv2lS8gkkMNe+wJkvk+ESyJycHAKstVACbSddGleT1KQDIgoB4jAXR58QbagBA6UPwPg6Wjsuauc/kAwKAgj6hDGiUAiDqLXVS21y28ta53lOx2wNqDMqYpcoepNQUQHFgK5BDeUABoDy60QaGTQH4gOnm/yEppLJ8HIwC8AFcTX2a5sjTlnK9o6j7F/rDRp6cvnKNTMf/xZbE5qA+Sj1AsJyqpL2c61fI1CytLPPja2thoD86YZCJUgAMMmuejGUgLTdM6BTlmTvq7RYP+K2b2mO0FtfDiG31KYgoheB0bR1foT7I+HNb7KkEMbFk54RBdZAC8BVgsYSN8OWIaXRQ1KUAlqJcNt9GlE6CfCXfraKdupEV66EUQDHa/xcATRbEpmXHQWzmijmfSrtKacAeikKaKKyYGwQw6+QA+DiYUk4fhEICwJc3eyfDHABPI0B2lRTJRtcrw2ef7H5ROlJ61NWrjVJ8KAiOlKN5fmS+L21iR0rlb+qFSSm1NQGoDr60bzvUZ4Kwm0qdypPOiUUra4ppuHYz512ILtXyokfhZP+gbLDLAP7aaBxmT4Y1AFgAYb7e6QrICDAtz0mZZvmtGq14rI1e7vprFnSZtLPmwOgthn/vgNJUqaMJoApA4wEPkJm8JxD8RRZYR6xbGqNWh++pbDbRavYKAVAQuNXfVJtO9woEroaalgco/EE6x0XfkANNi7Txg8MAbFLm0tfLAwxHRyzH/rFVZU/lOx0j2Csm47nNR+Eb6eT7kPJMbAbAJFUq97op5IFkEMpidfaabauALgDOG9AACvCuCwu3NLyE4ncRrpcETwLghWrZsJDfXikYANGxMJY169qL7mqARqxxMACRxY4x5hnAMazaIvMfmQ4TTxREm8cAAAAASUVORK5CYII="></image>
            </view>
        </view>
    </view>
</template>

<script>
    export default {
        props: {
            userInfo: {
                type: Object,
                default: { id: '', avatar: '', name: '', balance: '', point: '' }
            },
            vehicle: {
                type: Object,
                default: {}
            }
        },
        methods: {
            // 去登录
            goLogin() {
                this.$navTo('pages/login/index')
            },
            // 跳转会员码
            goMemberCode(userId) {
                this.$navTo('pages/user/code', { userId: this.userInfo.id })
            },
            // 跳转绑定车牌
            toVehicle() {
               this.$navTo('pages/vehicle/index');
            }
        }
    }
</script>

<style lang="scss" scoped>
.com-user {
    width: 100%;
    height: auto;
    padding: 0rpx 10rpx 10rpx 10rpx;
    margin-top: 20rpx;
    position: relative;
    z-index: 2;
    .user-main{
        width: 100%;
        padding: 20rpx;
        background: #f5f5f5;
        border-radius: 20rpx;
        border: #cccccc solid 1rpx;
        display: flex;
        align-items: center;
        .avatar{
            width: 130rpx;
            height: 130rpx;
            border-radius: 50%;
        }
        .uc{
            flex: 1;
            padding-left: 10rpx;
            .name{
                font-size: 32rpx;
                font-weight: 500;
                color: #000;
                
            }
            .tip{
                font-size: 24rpx;
                color: #666;
                margin-top: 10rpx;
            }
        }
        .ur{
            width: 140rpx;
            height: 60rpx;
            display: flex;
            align-items: center;
            border-radius: 60rpx;
            justify-content: center;
            color: #fff;
            font-size: 26rpx;
            background-color: $fuint-theme;
        }
        .qr{
            width: 80rpx;
            height: 80rpx;
            display: flex;
            align-items: center;
            border-radius: 6rpx;
            justify-content: center;
            text-align: center;
            color: $fuint-theme;
            font-size: 68rpx;
            background-color: #fff;
            border: solid 1rpx $fuint-theme;
            padding: 2rpx;
        }
        .plate {
           background: $fuint-theme;
           color: #ffffff;
           border: #f5f5f5 solid 1rpx;
           height: 100rpx;
           line-height: 100rpx;
           border-radius: 100rpx;
           text-align: center;
           margin: auto 6rpx;
           font-size: 28rpx;
           font-weight: bold;
           padding: 0rpx 30rpx;
           .edit {
               height: 50rpx;
               width: 50rpx;
               margin-left: 8rpx;
               align-items: center;
               justify-content: center;
               vertical-align: middle;
               margin-bottom: 8rpx;
               font-weight: bold;
           }
        }
    }
}
</style>
