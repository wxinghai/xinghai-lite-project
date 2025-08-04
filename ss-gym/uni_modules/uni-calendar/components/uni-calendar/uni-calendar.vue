<template>
	<view class="uni-calendar">
		<view v-if="!insert&&show" class="uni-calendar__mask" :class="{'uni-calendar--mask-show':aniMaskShow}" @click="clean"></view>
		<view v-if="insert || show" class="uni-calendar__content" :class="{'uni-calendar--fixed':!insert,'uni-calendar--ani-show':aniMaskShow}">
			<view v-if="!insert" class="uni-calendar__header uni-calendar--fixed-top">
				<view class="uni-calendar__header-btn-box" @click="close">
					<text class="uni-calendar__header-text uni-calendar--fixed-width">{{cancelText}}</text>
				</view>
				<view class="uni-calendar__header-btn-box" @click="confirm">
					<text class="uni-calendar__header-text uni-calendar--fixed-width">{{okText}}</text>
				</view>
			</view>
			<view class="uni-calendar__header">
				<view class="uni-calendar__header-btn-box" @click.stop="pre">
					<view class="uni-calendar__header-btn uni-calendar--left"></view>
				</view>
				<picker mode="date" :value="date" fields="month" @change="bindDateChange">
					<text class="uni-calendar__header-text">{{ (nowDate.year||'') +' / '+( nowDate.month||'')}}</text>
				</picker>
				<view class="uni-calendar__header-btn-box" @click.stop="next">
					<view class="uni-calendar__header-btn uni-calendar--right"></view>
				</view>
				<text class="uni-calendar__backtoday" @click="backToday">{{todayText}}</text>
			</view>
			<!-- 添加触摸事件处理 -->
			<view class="uni-calendar__box full-width"
				@touchstart="handleTouchStart"
				@touchmove="handleTouchMove"
				@touchend="handleTouchEnd">
				<view class="uni-calendar-content full-width" :style="{transform: `translateX(${slideOffset}px)`, transition: isAnimating ? 'transform 0.3s ease' : 'none'}">
					<view v-if="showMonth" class="uni-calendar__box-bg">
						<text class="uni-calendar__box-bg-text">{{nowDate.month}}</text>
					</view>
					<view class="uni-calendar__weeks full-width">
						<view class="uni-calendar__weeks-day">
							<text class="uni-calendar__weeks-day-text">{{SUNText}}</text>
						</view>
						<view class="uni-calendar__weeks-day">
							<text class="uni-calendar__weeks-day-text">{{monText}}</text>
						</view>
						<view class="uni-calendar__weeks-day">
							<text class="uni-calendar__weeks-day-text">{{TUEText}}</text>
						</view>
						<view class="uni-calendar__weeks-day">
							<text class="uni-calendar__weeks-day-text">{{WEDText}}</text>
						</view>
						<view class="uni-calendar__weeks-day">
							<text class="uni-calendar__weeks-day-text">{{THUText}}</text>
						</view>
						<view class="uni-calendar__weeks-day">
							<text class="uni-calendar__weeks-day-text">{{FRIText}}</text>
						</view>
						<view class="uni-calendar__weeks-day">
							<text class="uni-calendar__weeks-day-text">{{SATText}}</text>
						</view>
					</view>
					<view class="uni-calendar__weeks full-width" v-for="(item,weekIndex) in weeks" :key="weekIndex">
						<view class="uni-calendar__weeks-item" v-for="(weeks,weeksIndex) in item" :key="weeksIndex">
							<calendar-item class="uni-calendar-item--hook" :weeks="weeks" :calendar="calendar" :selected="selected" :lunar="lunar" @change="choiceDate">
							</calendar-item>
							<!-- 这里需要展示5条数据，如果数据不足5条，则展示空数据 -->
							 <!-- 第一条数据为容量，背景颜色为主题色，是所有完成训练的计划的容量总和，如果都没有完成，则不显示容量，其他显示训练的名字 -->
							 <view class="train-item-box">
							  <slot name="train-items" :date="weeks">
							    <!-- <view class="train-item" v-for="(item,index) in 5" :key="index">
							      90%腿训
							    </view> -->
							  </slot>
						   </view>
						</view>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import Calendar from './util.js';
	import CalendarItem from './uni-calendar-item.vue'

	import { initVueI18n } from '@dcloudio/uni-i18n'
	import i18nMessages from './i18n/index.js'
	const {	t	} = initVueI18n(i18nMessages)

	/**
	 * Calendar 日历
	 * @description 日历组件可以查看日期，选择任意范围内的日期，打点操作。常用场景如：酒店日期预订、火车机票选择购买日期、上下班打卡等
	 * @tutorial https://ext.dcloud.net.cn/plugin?id=56
	 * @property {String} date 自定义当前时间，默认为今天
	 * @property {Boolean} lunar 显示农历
	 * @property {String} startDate 日期选择范围-开始日期
	 * @property {String} endDate 日期选择范围-结束日期
	 * @property {Boolean} range 范围选择
	 * @property {Boolean} insert = [true|false] 插入模式,默认为false
	 * 	@value true 弹窗模式
	 * 	@value false 插入模式
	 * @property {Boolean} clearDate = [true|false] 弹窗模式是否清空上次选择内容
	 * @property {Array} selected 打点，期待格式[{date: '2019-06-27', info: '签到', data: { custom: '自定义信息', name: '自定义消息头',xxx:xxx... }}]
	 * @property {Boolean} showMonth 是否选择月份为背景
	 * @event {Function} change 日期改变，`insert :ture` 时生效
	 * @event {Function} confirm 确认选择`insert :false` 时生效
	 * @event {Function} monthSwitch 切换月份时触发
	 * @example <uni-calendar :insert="true":lunar="true" :start-date="'2019-3-2'":end-date="'2019-5-20'"@change="change" />
	 */
	export default {
		components: {
			CalendarItem
		},
		emits:['close','confirm','change','monthSwitch'],
		props: {
			date: {
				type: String,
				default: ''
			},
			selected: {
				type: Array,
				default () {
					return []
				}
			},
			lunar: {
				type: Boolean,
				default: false
			},
			startDate: {
				type: String,
				default: ''
			},
			endDate: {
				type: String,
				default: ''
			},
			range: {
				type: Boolean,
				default: false
			},
			insert: {
				type: Boolean,
				default: true
			},
			showMonth: {
				type: Boolean,
				default: true
			},
			clearDate: {
				type: Boolean,
				default: true
			}
		},
		data() {
			return {
				show: false,
				weeks: [],
				calendar: {},
				nowDate: '',
				aniMaskShow: false,
				// 滑动相关变量
				slideOffset: 0,          // 滑动偏移量
				touchStartX: 0,          // 触摸起始X位置
				touchStartY: 0,          // 触摸起始Y位置
				touchMoveX: 0,           // 触摸移动X位置
				touchMoveY: 0,           // 触摸移动Y位置
				isSliding: false,        // 是否正在滑动
				isAnimating: false,      // 是否正在动画
				screenWidth: 0,          // 屏幕宽度
				slideThreshold: 50,      // 滑动触发阈值
				sliding: false,          // 滑动中状态
				slideAnimationActive: false // 滑动动画激活
			}
		},
		computed: {
			/**
			 * 日历内容
			 */
			SUNText() {
				return t("uni-calender.SUN")
			},
			monText() {
				return t("uni-calender.MON")
			},
			TUEText() {
				return t("uni-calender.TUE")
			},
			WEDText() {
				return t("uni-calender.WED")
			},
			THUText() {
				return t("uni-calender.THU")
			},
			FRIText() {
				return t("uni-calender.FRI")
			},
			SATText() {
				return t("uni-calender.SAT")
			},
			/**
			 * 按钮文字
			 */
			todayText() {
				return t("uni-calender.today")
			},
			okText() {
				return t("uni-calender.ok")
			},
			cancelText() {
				return t("uni-calender.cancel")
			},
			/**
			 * 当前日期是否可选
			 */
			resetButtonType(){
				const nowDate = this.calendar.fullDate === this.date ? this.date : this.calendar.fullDate
				if(this.range){
					return  false
				}
				const date = new Date(nowDate)
				const year = date.getFullYear()
				const month = date.getMonth() + 1
				if(this.startDate){
					const startDate = new Date(this.startDate.replace('-','/'))
					const startYear = startDate.getFullYear()
					const startMonth = startDate.getMonth() + 1
					if(year < startYear || (year === startYear && month < startMonth)){
						return true
					}
				}
				if(this.endDate){
					const endDate = new Date(this.endDate.replace('-','/'))
					const endYear = endDate.getFullYear()
					const endMonth = endDate.getMonth() + 1
					if(year > endYear || (year === endYear && month > endMonth)){
						return true
					}
				}
				return false
			}
		},
		watch: {
			date(newVal) {
				this.init(newVal)
			},
			startDate(val){
				// 字节跳动小程序 watch 早于 created
				if(!this.cale){
					return
				}
				this.cale.resetSatrtDate(val)
				this.cale.setDate(this.date)
				this.weeks = this.cale.weeks
			},
			endDate(val){
				// 字节跳动小程序 watch 早于 created
				if(!this.cale){
					return
				}
				this.cale.resetEndDate(val)
				this.cale.setDate(this.date)
				this.weeks = this.cale.weeks
			},
			selected(newVal) {
				// 字节跳动小程序 watch 早于 created
				if(!this.cale){
					return
				}
				this.cale.setSelectInfo(this.nowDate.fullDate, newVal)
				this.weeks = this.cale.weeks
			}
		},
		created() {
			// 获取日历方法实例
			this.cale = new Calendar({
				selected: this.selected,
				startDate: this.startDate,
				endDate: this.endDate,
				range: this.range,
			})
			// 选中某一天
			this.init(this.date)
			
			// 获取屏幕宽度
			try {
				const info = uni.getSystemInfoSync()
				this.screenWidth = info.screenWidth
			} catch (e) {
				console.error('获取屏幕宽度失败：', e)
				this.screenWidth = 375 // 默认值
			}
		},
		methods: {
			/**
			 * 初始化日期显示
			 * @param {Object} date
			 */
			init(date) {
				this.cale.setDate(date)
				this.weeks = this.cale.weeks
				this.nowDate = this.calendar = this.cale.getInfo(date)
			},
			
			/**
			 * 处理触摸开始事件
			 */
			handleTouchStart(e) {
				// 如果正在动画中，不处理触摸事件
				if (this.isAnimating) return
				
				this.touchStartX = e.touches[0].clientX
				this.touchStartY = e.touches[0].clientY
				this.isSliding = false
				this.slideOffset = 0
				this.sliding = false
			},
			
			/**
			 * 处理触摸移动事件
			 */
			handleTouchMove(e) {
				// 如果正在动画中，不处理触摸事件
				if (this.isAnimating) return
				
				this.touchMoveX = e.touches[0].clientX
				this.touchMoveY = e.touches[0].clientY
				
				const deltaX = this.touchMoveX - this.touchStartX
				const deltaY = this.touchMoveY - this.touchStartY
				
				// 判断是否为水平滑动（水平位移大于垂直位移）
				if (!this.isSliding) {
					if (Math.abs(deltaX) > Math.abs(deltaY) * 1.2) {
						this.isSliding = true
						this.sliding = true
					} else {
						return // 不是水平滑动，不处理
					}
				}
				
				// 阻止页面滚动
				e.preventDefault()
				e.stopPropagation()
				
				// 计算滑动偏移量（添加阻尼效果）
				// 限制滑动范围，最多滑动屏幕宽度的40%
				const maxOffset = this.screenWidth * 0.4
				if (Math.abs(deltaX) > maxOffset) {
					this.slideOffset = deltaX > 0 ? maxOffset : -maxOffset
				} else {
					this.slideOffset = deltaX * 0.8 // 添加阻尼效果
				}
			},
			
			/**
			 * 处理触摸结束事件
			 */
			handleTouchEnd() {
				// 如果不是在滑动状态，直接返回
				if (!this.isSliding || this.isAnimating) return
				
				const deltaX = this.touchMoveX - this.touchStartX
				
				// 设置动画状态
				this.isAnimating = true
				this.slideAnimationActive = true
				
				// 判断滑动方向和距离是否满足切换月份的条件
				if (Math.abs(deltaX) > this.slideThreshold) {
					// 计算目标偏移量（屏幕宽度）
					const targetOffset = deltaX > 0 ? this.screenWidth : -this.screenWidth
					this.slideOffset = targetOffset
					
					// 延迟执行月份切换，等待动画完成
					setTimeout(() => {
						if (deltaX > 0) {
							this.pre() // 向右滑动，显示上个月
						} else {
							this.next() // 向左滑动，显示下个月
						}
						
						// 重置状态
						setTimeout(() => {
							this.slideOffset = 0
							this.isAnimating = false
							this.isSliding = false
							this.slideAnimationActive = false
							this.sliding = false
						}, 50)
					}, 250)
				} else {
					// 滑动距离不够，回弹到原位置
					this.slideOffset = 0
					setTimeout(() => {
						this.isAnimating = false
						this.isSliding = false
						this.slideAnimationActive = false
						this.sliding = false
					}, 200)
				}
			},
			
			// 取消穿透
			clean() {},
			
			/**
			 * 打开日历弹窗
			 */
			open() {
				// 弹窗模式下，清空日历选中状态
				if(this.clearDate && !this.insert){
					this.cale.cleanMultipleStatus()
					// this.cale.setDate(this.date)
					this.init(this.date)
				}
				this.show = true
				this.$nextTick(() => {
					setTimeout(() => {
						this.aniMaskShow = true
					}, 50)
				})
			},
			/**
			 * 关闭日历弹窗
			 */
			close() {
				this.aniMaskShow = false
				this.$nextTick(() => {
					setTimeout(() => {
						this.show = false
						this.$emit('close')
					}, 300)
				})
			},
			/**
			 * 确认按钮
			 */
			confirm() {
				this.setEmit('confirm')
				this.close()
			},
			/**
			 * 变化触发
			 */
			change() {
				if (!this.insert) return
				this.setEmit('change')
			},
			/**
			 * 选择月份触发
			 */
			monthSwitch() {
				let {
					year,
					month
				} = this.nowDate
				this.$emit('monthSwitch', {
					year,
					month: Number(month)
				})
			},
			/**
			 * 派发事件
			 * @param {Object} name
			 */
			setEmit(name) {
				let {
					year,
					month,
					date,
					fullDate,
					lunar,
					extraInfo
				} = this.calendar
				this.$emit(name, {
					range: this.cale.multipleStatus,
					year,
					month,
					date,
					fulldate: fullDate,
					lunar,
					extraInfo: extraInfo || {}
				})
			},
			/**
			 * 选择天触发
			 * @param {Object} weeks
			 */
			choiceDate(weeks) {
				if (weeks.disable) return
				this.calendar = weeks
				// 设置多选
				this.cale.setMultiple(this.calendar.fullDate)
				this.weeks = this.cale.weeks
				this.change()
			},
			/**
			 * 回到今天
			 */
			backToday() {
				this.cale.setDate(this.date)
				this.weeks = this.cale.weeks
				this.nowDate = this.calendar = this.cale.getInfo(this.date)
				this.change()
			},
			/**
			 * 上个月
			 */
			pre() {
				const preDate = this.cale.getDate(this.nowDate.fullDate, -1, 'month')
				this.setDate(preDate.fullDate)
				this.monthSwitch()
			},
			/**
			 * 下个月
			 */
			next() {
				const nextDate = this.cale.getDate(this.nowDate.fullDate, +1, 'month')
				this.setDate(nextDate.fullDate)
				this.monthSwitch()
			},
			/**
			 * 设置日期
			 * @param {Object} date
			 */
			setDate(date) {
				this.cale.setDate(date)
				this.weeks = this.cale.weeks
				this.nowDate = this.cale.getInfo(date)
			},
			
			/**
			 * 日期选择
			 * @param {Object} e
			 */
			bindDateChange(e) {
				const value = e.detail.value + '-1'
				this.init(value)
			}
		}
	}
</script>

<style lang="scss">
	$uni-bg-color-mask: rgba($color: #000000, $alpha: 0.4);
	$uni-border-color: #EDEDED;
	$uni-text-color: #333;
	$uni-bg-color-hover:#f1f1f1;
	$uni-font-size-base:14px;
	$uni-text-color-placeholder: #808080;
	$uni-color-subtitle: #555555;
	$uni-text-color-grey:#999;
	.train-item{
		width: 90%;
		height: 12px;
		font-size: 8px;
		font-weight: 400;
		background-color: #000000;
		color: #fff;
		margin-top: 1px;
		border-radius: 2px;
		overflow: hidden;
		white-space: nowrap;
		text-overflow: ellipsis;
		padding:2px 2px;
		box-sizing: border-box;
		display: flex;
		justify-content: center;
		align-items: center;
	}
	.train-item-volume{
		background-color: #2979ff;
	}
	.uni-calendar {
		/* #ifndef APP-NVUE */
		display: flex;
		/* #endif */
		flex-direction: column;
	}

	.uni-calendar__mask {
		position: fixed;
		bottom: 0;
		top: 0;
		left: 0;
		right: 0;
		background-color: $uni-bg-color-mask;
		transition-property: opacity;
		transition-duration: 0.3s;
		opacity: 0;
		/* #ifndef APP-NVUE */
		z-index: 99;
		/* #endif */
	}
	
	.uni-calendar--mask-show {
		opacity: 1
	}

	.uni-calendar--fixed {
		position: fixed;
		/* #ifdef APP-NVUE */
		bottom: 0;
		/* #endif */
		left: 0;
		right: 0;
		transition-property: transform;
		transition-duration: 0.3s;
		transform: translateY(460px);
		/* #ifndef APP-NVUE */
		bottom: calc(var(--window-bottom));
		z-index: 99;
		/* #endif */
	}

	.uni-calendar--ani-show {
		transform: translateY(0);
	}

	.full-width {
		width: 100% !important;
		box-sizing: border-box;
	}
	
	.uni-calendar__content {
		background-color: #fff;
		width: 100%; /* 确保内容宽度占满 */
	}

	.uni-calendar__header {
		position: relative;
		/* #ifndef APP-NVUE */
		display: flex;
		/* #endif */
		flex-direction: row;
		justify-content: center;
		align-items: center;
		height: 60px; /* 增加头部高度 */
		border-bottom-color: $uni-border-color;
		border-bottom-style: solid;
		border-bottom-width: 1px;
	}

	.uni-calendar--fixed-top {
		/* #ifndef APP-NVUE */
		display: flex;
		/* #endif */
		flex-direction: row;
		justify-content: space-between;
		border-top-color: $uni-border-color;
		border-top-style: solid;
		border-top-width: 1px;
	}

	.uni-calendar--fixed-width {
		width: 50px;
	}

	.uni-calendar__backtoday {
		position: absolute;
		right: 0;
		top: 25rpx;
		padding: 0 5px;
		padding-left: 10px;
		height: 25px;
		line-height: 25px;
		font-size: 12px;
		border-top-left-radius: 25px;
		border-bottom-left-radius: 25px;
		color: $uni-text-color;
		background-color: $uni-bg-color-hover;
	}
	
	.uni-calendar__header-text {
		text-align: center;
		width: 100px;
		font-size: 18px; /* 增加字体大小 */
		font-weight: bold;
		color: $uni-text-color;
	}

	.uni-calendar__header-btn-box {
		/* #ifndef APP-NVUE */
		display: flex;
		/* #endif */
		flex-direction: row;
		align-items: center;
		justify-content: center;
		width: 60px; /* 增加按钮区域宽度 */
		height: 60px; /* 增加按钮区域高度 */
	}

	.uni-calendar__header-btn {
		width: 12px; /* 增加箭头大小 */
		height: 12px; /* 增加箭头大小 */
		border-left-color: $uni-text-color-placeholder;
		border-left-style: solid;
		border-left-width: 2px;
		border-top-color: $uni-text-color-placeholder;
		border-top-style: solid;
		border-top-width: 2px;
	}

	.uni-calendar--left {
		transform: rotate(-45deg);
	}

	.uni-calendar--right {
		transform: rotate(135deg);
	}

	.uni-calendar__weeks {
		position: relative;
		/* #ifndef APP-NVUE */
		display: flex;
		/* #endif */
		flex-direction: row;
		overflow: hidden;
	}

	.uni-calendar__weeks-item {
		flex: 1;
		height: 80px; /* 增加高度以匹配calendar-item的高度 */
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: start;
		overflow: hidden !important;
	}
	.train-item-box {
		height: 56px;
		width: 100%;
	}
	.uni-calendar-item--hook {
		height: 24px;
	}

	.uni-calendar__weeks-day {
		flex: 1;
		/* #ifndef APP-NVUE */
		display: flex;
		/* #endif */
		flex-direction: column;
		justify-content: center;
		align-items: center;
		height: 55px; /* 增加周标题高度 */
		border-bottom-color: #F5F5F5;
		border-bottom-style: solid;
		border-bottom-width: 1px;
		margin-top: 2px;
	}

	.uni-calendar__weeks-day-text {
		font-size: 16px; /* 增加字体大小 */
		font-weight: 500; /* 增加字体粗细 */
	}

	.uni-calendar__box {
		position: relative;
		overflow: hidden; /* 防止内容溢出 */
		width: 100%; /* 确保宽度占满 */
	}
	
	/* 添加动画效果样式 */
	.uni-calendar-content {
		width: 100%;
		transition: transform 0.3s ease;
		will-change: transform; /* 优化动画性能 */
	}

	.uni-calendar__box-bg {
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
	}

	.uni-calendar__box-bg-text {
		width: 100%;
		text-align: center;
		font-size: 200px;
		font-weight: bold;
		color: $uni-text-color-placeholder;
		opacity: 0.1;
		/* #ifndef APP-NVUE */
		transform: translateY(-50%);
		/* #endif */
		/* #ifdef APP-NVUE */
		lines: 1;
		/* #endif */
	}
</style>
