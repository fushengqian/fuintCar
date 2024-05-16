<template>
  <!-- 辅助线 -->
  <view class="vehicle">
    <view class="plate" @click="toVehicle">
        <text class="text">我的车辆：{{ vehicle.vehiclePlateNo ? vehicle.vehiclePlateNo : '' }}</text>
        <image v-if="vehicle.vehiclePlateNo" class="edit" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAACqklEQVRoQ+2ZjVECMRCFvQ6kA+hAOoAKlA6gAqUCsQLpQDqQDsQKpAPpQKwA35vZY46QXDZHLoSRzNx4o7ns+3Y3mx+LmwtvxYXrv7kCxIrgbrfrYqzboijWIWNmEQER/0UAPC+AmGkhcgG4g2AClG0OiKkGIgsACkUU3vBjHAqRDYADwptOWQE0gcgOwAExxZyY2+aEFwC5ycrAScaH76nao2HPCuEEgPABBngV4alE++yMEIlltZMVAOIpnBUhpcd94vn3DQB6tQAQT+EsaWXb4oWrI59fjZWIfe6NDDhaHw4iIPn+XfH8Cu8TUG8iilINBS1PksJl/wVeOA/o0H0zAaof0eND8wOV9RM7acXTjAnwjt89iP2jCXOiLtXnIeJtAEyfrljqpU6dUPE2gF3pJoj3rhEqlyo7NRGfDQDEm7tR64S1+cKcA2eJgFS/HxGoFp9NBChEVn4unKuQyueKwBaDdJTp21o3icwYBpaugmICzNCZmyh+MGlNmXJgAHyg6wCP06FJK41S974bALxz8n8ByO0BV2pnToZ6ua5/1AhIreZWo1uXk1kCGOKpcYOnH1LumoBFiYBFPLez3GIfnIyaCPSkT3V1XsNe39a/dhI7PM8tNiPQaoNtlvRnMbJwlfW6MzE9UOY8x+H5gFvsFOJpm2tAeaRlxBfqCFg83/rhRlZdFghWueqNBLcWQ1e4jyJgEd9qqngG53xjyjpvrG0AvGRlCM/dVhDgPY/bDvXltjY1AL3N+cXnEw8n7sEBXjUH5E6Ih/uyqa+6UxPTnutiq9FVdzYAFNL0vj41hG8hyz4S3u107pHwAjjSyfufk1SppAJwQHQ0Za5tEDWAQPDanSWWB5pR2+I04wcBCAT/Ge1dYDTGY/QJBohhNOYYV4CY3mwy1h9QgjRANOU/7AAAAABJRU5ErkJggg=="></image>
        <image v-if="!vehicle.vehiclePlateNo" class="edit" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAAEPUlEQVRoQ+2Zj3FTMQzGkw3oBLQTQCcgnQA6AekE0AlIJ6BMQDoB3YAwAWUCsgFsEPTrSXd6jv0s+yXH9a6+8738sWV90idZ9pvPnnibP3H9Z88AzIO73e6FfF5Kfyn9tXS+02lb6X+1/5Ln/Xw+57fJbZIHROlTVfqtPPlsCkcU28igH9LXU8B0AVBrf1LlW5QuAbsVENcR1OmYZgCi/EcRgvKp4lDkQa3K02jDmuYdnm+kvysoeyNAVi1AwgDU6p/V6n4NqHAnHV4DItREHiDeZ8BsRM5FSIgMCgFQrn+V8QsnGCtjsfvoYrlxCgTD4B1ryL6MxEYVgCr/PVngVpUPW7wGUtaBOlDTGrIvBARgim0UgNLmm7M8Qq9F6LqmUM//sh4exljWtrLW2RQAKG8Bh/JXUylTAyYg2EN+unGjMVH0gGYbuGkN5Y9i+RRUxhN4HdrutSwApc5vGW2psjtP1yxe+l90wHikbBreP88FdQnASiZYQG11cnPAagKYRbJJDojMh0pQisaOfZWO2wOgizLRrN9FHeUyAYkcssmm1RsZKp2lxsgB8NZv2lS8gkkMNe+wJkvk+ESyJycHAKstVACbSddGleT1KQDIgoB4jAXR58QbagBA6UPwPg6Wjsuauc/kAwKAgj6hDGiUAiDqLXVS21y28ta53lOx2wNqDMqYpcoepNQUQHFgK5BDeUABoDy60QaGTQH4gOnm/yEppLJ8HIwC8AFcTX2a5sjTlnK9o6j7F/rDRp6cvnKNTMf/xZbE5qA+Sj1AsJyqpL2c61fI1CytLPPja2thoD86YZCJUgAMMmuejGUgLTdM6BTlmTvq7RYP+K2b2mO0FtfDiG31KYgoheB0bR1foT7I+HNb7KkEMbFk54RBdZAC8BVgsYSN8OWIaXRQ1KUAlqJcNt9GlE6CfCXfraKdupEV66EUQDHa/xcATRbEpmXHQWzmijmfSrtKacAeikKaKKyYGwQw6+QA+DiYUk4fhEICwJc3eyfDHABPI0B2lRTJRtcrw2ef7H5ROlJ61NWrjVJ8KAiOlKN5fmS+L21iR0rlb+qFSSm1NQGoDr60bzvUZ4Kwm0qdypPOiUUra4ppuHYz512ILtXyokfhZP+gbLDLAP7aaBxmT4Y1AFgAYb7e6QrICDAtz0mZZvmtGq14rI1e7vprFnSZtLPmwOgthn/vgNJUqaMJoApA4wEPkJm8JxD8RRZYR6xbGqNWh++pbDbRavYKAVAQuNXfVJtO9woEroaalgco/EE6x0XfkANNi7Txg8MAbFLm0tfLAwxHRyzH/rFVZU/lOx0j2Csm47nNR+Eb6eT7kPJMbAbAJFUq97op5IFkEMpidfaabauALgDOG9AACvCuCwu3NLyE4ncRrpcETwLghWrZsJDfXikYANGxMJY169qL7mqARqxxMACRxY4x5hnAMazaIvMfmQ4TTxREm8cAAAAASUVORK5CYII="></image>
    </view>
  </view>
</template>

<script>

  export default {

    /**
     * 组件的属性列表
     * 用于组件自定义设置
     */
    props: {
      vehicle: Object
    },

    /**
     * 组件的方法列表
     * 更新属性和数据的方法与更新页面数据的方法类似
     */
    methods: {
      toVehicle() {
         this.$navTo('pages/vehicle/index');
      }
    }

  }
</script>

<style lang="scss" scoped>
  .vehicle {
    margin-top: 20rpx;
    width: 100%;
    height: 100rpx;
    padding: 0 10rpx;
    .plate {
       background: $fuint-theme;
       color: #ffffff;
       border: #f5f5f5 solid 1rpx;
       height: 100rpx;
       line-height: 100rpx;
       border-radius: 6rpx;
       text-align: center;
       margin: auto 6rpx;
       font-size: 32rpx;
       font-weight: bold;
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
</style>
