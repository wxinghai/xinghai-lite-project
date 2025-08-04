<template>
	<view class="uni-calendar-item__weeks-box" :class="{
		'uni-calendar-item--disable':weeks.disable,
		'uni-calendar-item--isDay':calendar.fullDate === weeks.fullDate && weeks.isDay,
		'uni-calendar-item--checked':(calendar.fullDate === weeks.fullDate && !weeks.isDay) ,
		'uni-calendar-item--today-not-selected': weeks.isDay && calendar.fullDate !== weeks.fullDate
		}"
	 @click="choiceDate(weeks)">
		<view class="uni-calendar-item__weeks-box-item">
			<!-- 日期数字 -->
			<text class="uni-calendar-item__weeks-box-text" :class="{
				'uni-calendar-item--isDay-text': weeks.isDay,
				'uni-calendar-item--isDay':calendar.fullDate === weeks.fullDate && weeks.isDay,
				'uni-calendar-item--checked':calendar.fullDate === weeks.fullDate && !weeks.isDay,
				'uni-calendar-item--disable':weeks.disable,
				'uni-calendar-item--today-not-selected': weeks.isDay && calendar.fullDate !== weeks.fullDate
				}">{{weeks.date}}</text>
		</view>
	</view>
</template>

<script>
	import { initVueI18n } from '@dcloudio/uni-i18n'
	import i18nMessages from './i18n/index.js'
	const {	t	} = initVueI18n(i18nMessages)

	export default {
		emits:['change'],
		props: {
			weeks: {
				type: Object,
				default () {
					return {}
				}
			},
			calendar: {
				type: Object,
				default: () => {
					return {}
				}
			},
			selected: {
				type: Array,
				default: () => {
					return []
				}
			},
			lunar: {
				type: Boolean,
				default: false
			}
		},
		computed: {
			todayText() {
				return t("uni-calender.today")
			}
		},
		methods: {
			choiceDate(weeks) {
				this.$emit('change', weeks)
			}
		}
	}
</script>

<style lang="scss" scoped>
	$uni-font-size-base:15px; /* 增加默认字体大小 */
	$uni-text-color:#333;
	$uni-opacity-disabled: 0.3;
	$uni-text-color-disable:#c0c0c0;
	$uni-primary: #2979ff !default;
	.uni-calendar-item__weeks-box {
		/* #ifndef APP-NVUE */
		display: flex;
		/* #endif */
		flex-direction: column;
		justify-content: center; /* 改为顶部对齐 */
		align-items: center;
		height: 24px;
		width: 24px; /* 增加日期单元格高度，确保有足够空间 */
		box-sizing: border-box; /* 确保padding计入高度 */
	}

	.uni-calendar-item__weeks-box-text {
		font-size: $uni-font-size-base;
		color: $uni-text-color;
		// font-weight: 500; /* 增加字重 */
		width: 24px; /* 设置固定宽度 */
		height: 24px; /* 设置固定高度 */
		line-height: 24px; /* 使文字垂直居中 */
		text-align: center; /* 使文字水平居中 */
		border-radius: 50%; /* 让背景为圆形 */
	}

	.uni-calendar-item__weeks-box-item {
		position: relative;
		/* #ifndef APP-NVUE */
		display: flex;
		/* #endif */
		flex-direction: column;
		justify-content: flex-start; /* 改为顶部对齐 */
		align-items: center;
		width: 100%;  /* 使用百分比宽度 */
		height: 100%; /* 增加高度 */
		overflow: visible; /* 允许内容溢出 */
		box-sizing: border-box; /* 确保padding不会增加总体尺寸 */
	}

	.uni-calendar-item--disable {
		background-color: transparent; /* 取消背景色 */
		opacity: $uni-opacity-disabled;
	}
	
	.uni-calendar-item--disable .uni-calendar-item__weeks-box-text {
		color: $uni-text-color-disable;
	}

	.uni-calendar-item--isDay-text {
		color: #fff; /* 今天文字为白色 */
	}

	.uni-calendar-item--isDay .uni-calendar-item__weeks-box-text {
		background-color: $uni-primary; /* 今天背景色为主色 */
		border-radius: 50%; /* 圆角 */
		color: #fff;
	}

	.uni-calendar-item--checked .uni-calendar-item__weeks-box-text {
		background-color: $uni-primary;
		color: #fff;
		opacity: 0.8;
		border-radius: 50%; /* 圆角 */
	}

	.uni-calendar-item--today-not-selected .uni-calendar-item__weeks-box-text {
		background-color: #f0f0f0;
		color: $uni-text-color;
		border-radius: 50%; /* 圆角 */
		border: 1px solid #e0e0e0;
	}
</style>
