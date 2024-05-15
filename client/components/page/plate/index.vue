<template>
    <view>
        <view class="plate" :class="{'show': show}">
            <view class="item" :class="{'active': index === 0}" @click="handleChange(0)">
                {{ plateNumber[0] }}
                <text class="triangle"></text>
            </view>
            <view class="item" :class="{'active': index === 1}" @click="handleChange(1)">{{ plateNumber[1] }}</view>
            <view class="point">●</view>
            <view class="item" :class="{'active': index === 2}" @click="handleChange(2)">{{ plateNumber[2] }}</view>
            <view class="item" :class="{'active': index === 3}" @click="handleChange(3)">{{ plateNumber[3] }}</view>
            <view class="item" :class="{'active': index === 4}" @click="handleChange(4)">{{ plateNumber[4] }}</view>
            <view class="item" :class="{'active': index === 5}" @click="handleChange(5)">{{ plateNumber[5] }}</view>
            <view class="item" :class="{'active': index === 6}" @click="handleChange(6)">{{ plateNumber[6] }}</view>
            <view class="item new-energy" :class="{'active': index === 7}" @click="handleChange(7)">
                <view v-if="plateNumber[7] || plateNumber[7] === 0">
                    <text>{{ plateNumber[7] }}</text>
                </view>
                <uni-icons type="plusempty" size="13" color="#03BE9F" v-else></uni-icons>
            </view>
        </view>
        <section class="panel" :class="{'show': show}">
            <view class="header">
                <view @click="handleReset">重置</view>
                <view @click="show = false">完成</view>
            </view>
            <view class="panelList">
                <view class="item" v-for="(item, idx) of currentDatas" :key="idx">
                    <view :class="{'disabled': (index == 1 && idx < 10) || (index > 1 && index < 6 && idx > 33) }"
                        v-if="item !==''" @click="handleClickKeyBoard(item, idx)"
                    >{{ item }}</view>
                </view>
                <view class="item backspace" :class="{'special': index === 0 }" @click="handleDelete">×</view>
            </view>
        </section>
    </view>
</template>
<script>
    export default {
        name: "plate",
        data() {
            return {
                show: false,
                index: -1,
                areaDatas: [
                    '京', '津', '渝', '沪', '冀', '晋', '辽', '吉', '黑', '苏',
                    '浙', '皖', '闽', '赣', '鲁', '豫', '鄂', '湘', '粤', '琼',
                    '川', '贵', '云', '陕', '甘', '青', '蒙', '桂', '宁', '新', 
                    '藏', '使', '领', '', '', '', '', '', ''
                ],
                characterDatas: [
                    0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 
                    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 
                    'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 
                    'W', 'X', 'Y', 'Z', '挂', '警', '学','港', '澳',  
                ]
            }
        },
        props: {
            plateNumber: {
                type: Array,
                default: Array.from({
                    length: 8
                }, v => '')
            }
        },
        computed: {
            currentDatas() {
                return this.index === 0 ? this.areaDatas : this.characterDatas;
            }
        },
        methods: {
            handleChange(index) {
                this.index = index;
                this.show = true;
            },
            handleClickKeyBoard(item, idx) {
                if((this.index === 1 && idx < 10) || (this.index > 1 && this.index < 6 && idx > 33)) {
                    return;
                }
                if (this.index < 8) {
                    this.$set(this.plateNumber, this.index, item);
                    this.$emit("myPlateChange", this.plateNumber);
                }
                if (this.index < 7) {
                    this.index++;
                }
            },
            // 重置
            handleReset() {
                this.index = 0;
                for(let i = 0; i < 8; i++) {
                    this.$set(this.plateNumber, i, '');
                }
                this.$emit("myPlateChange", this.plateNumber);
            },
            // 删除
            handleDelete() {
                this.$set(this.plateNumber, this.index, '');
                this.$emit("myPlateChange", this.plateNumber);
                if(this.index > 0) {
                    this.index--;
                }
            }
        }
    }
</script>
<style scoped lang='less'>
    .plate {
        display: flex;
        justify-content: space-between;
        .item {
            width: 64rpx;
            height: 80rpx;
            background-color: #F3F4F7;
            border-radius: 8rpx;
            text-align: center;
            line-height: 80rpx;
            font-size: 32rpx;
            color: rgba(0,0,0,0.90);
            font-weight: bold;
            position: relative;
            &.active {
                background-color: #bbbbbb;
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
    .panel {
        position: fixed;
        left: 0;
        width: 100%;
        bottom: 0;
        z-index: 999;
        box-sizing: border-box;
        background-color: #F5F5F5;
        transition: all 0.3s ease;
        transform: translateY(100%);
        &.show {
            transform: translateX(0);
        }
        .header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 24rpx;
            height: 96rpx;
            color: #333333;
            font-size: 34rpx;
            font-weight: bold;
        }
        .panelList {
            padding: 0 19rpx 20rpx;
            .item {
                display: inline-block;
                width: calc(~"(100% - 72rpx) / 10");
                height: 84rpx;
                margin-right: 8rpx;
                margin-bottom: 8rpx;
                vertical-align: top;
                view {
                    width: 100%;
                    height: 84rpx;
                    line-height: 84rpx;
                    border-radius: 6rpx;
                    background: #FEFFFE;
                    font-size: 32rpx;
                    color: rgba(0,0,0,0.90);
                    font-weight: bold;
                    text-align: center;
                    &.disabled {
                        background-color: rgba(254, 255, 254, 0.6);
                        color: rgba(0, 0, 0, 0.23);
                    }
                }
                &:nth-of-type(10n) {
                    margin-right: 0;
                }
            }
            .backspace {
                vertical-align: top;
                font-size: 48rpx;
                font-weight: bold;
                text-align: center;
                height: 84rpx;
                line-height: 84rpx;
                border-radius: 6rpx;
                background: #FEFFFE;
                color: rgba(0,0,0,0.90);
            }
        }
    }
</style>
