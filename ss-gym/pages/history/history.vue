<template>
	<view class="container" v-if="unloading">
		<!-- 状态栏占位 -->
		<view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
		
		<!-- 导航栏 -->
		<view class="nav-bar">
			<view class="nav-title">训练记录</view>
			<view class="nav-right">
				<view class="calendar-icon">
					{{'📋'}}
				</view>  
			</view>
		</view>

		<!-- 主内容区 -->
		<view class="main-content">
			<!-- 切换视图 - 日历视图 -->
			<view  class="calendar-container">
				<!-- 使用uni-calendar组件 -->
				<uni-calendar 
					:insert="true"
					:lunar="false"
					:start-date="firstDay"
					:end-date="lastDay"
					:date="currentDate"
					@change="dateClick"
					@monthSwitch="switchMonth"
					:selected="selectedDates"
					:showMonth="false"
					:slide="true">
					<template #train-items="{ date }">
                      <!-- 自定义训练项内容 -->
                    <view class="train-item" v-for="(item, index) in getTrainingDataForDate(date.fullDate)" :key="index">
                        {{ item.name }}
                    </view>
                   </template>
				</uni-calendar>
			</view>
	 
 
		</view>
		
		<!-- 弹窗 -->
		<view class="popup-mask" v-if="showPopup" @click="closePopup"></view>
		<view class="day-popup" :class="{ 'day-popup-height': trainingRecords.length > 0 }"
			 v-if="showPopup">
			<view class="popup-header">
				<view class="popup-date">{{ selectedDate }}</view>
				<view class="close-btn" @click="closePopup">×</view>
			</view>
			
			<view class="popup-content">
				<!-- 无日程内容 -->
				<view v-if="trainingRecords.length === 0" class="no-schedule">
					<view class="no-schedule-text">今日无安排</view>
					<view class="action-buttons">
						<button class="action-btn strength" @click="openTrainingPage()">启动力量训练</button>
						<button class="action-btn rest" @click="addRestDay">添加休息日</button>
					</view>
				</view>
				
				<!-- 有日程内容 -->
				<view v-else class="has-schedule">
					
					<!-- 调整训练容器结构 -->
					<view class="training-container">
						<swiper class="training-swiper" 
							:current="currentIndex" 
							@change="swiperChange"
							:indicator-dots="false"
							:circular="true"
							:duration="300">
							<swiper-item v-for="(record, index) in trainingRecords" :key="index"
							 class="swiper-item">
								<view class="training-card">
									<!-- 训练标题部分 -->
									<view class="training-title">
										 {{record.name}}
									</view>
									
									<!-- 训练信息部分 -->
									<view class="training-info">
										<view class="info-left">
											<text class="info-value"> {{record.volume}}kg</text>
											<text class="info-label">容量</text>
										</view>
										<view class="info-center">
											<text class="time-range">{{record.range}}</text>
											<text class="time-date">{{record.date}}</text>
										</view>
									</view>
									
																		<!-- 训练动作列表 - 添加可滚动区域 -->
									<scroll-view class="exercises-scroll" scroll-y="true">
										<view class="exercises-list" v-for="(action, indexs) in record.actions" :key="indexs+'0'">
											<!-- 第一个动作 -->
											<view class="exercise-item">
												<view class="exercise-icon">
													<image :src="getActionImage(action.name)" mode="aspectFit" class="exercise-img" :lazy-load="true"></image>
												</view>
												<view class="exercise-content">
													<view class="exercise-name">{{action.name}}</view>
													<view class="exercise-sets">
														<template v-if="action.sets && action.sets.length > 0">
															<!-- 如果组数小于5，全部显示 -->
															<template v-if="action.sets.length <= 5">
																<view class="set-item" v-for="(set, indexset) in action.sets" :key="indexset+'set'">
																	<text class="set-num">{{indexset+1}}</text>
																	<text class="set-weight">{{set.weight}}kg × {{set.reps}}</text>
																</view>
															</template>
															<!-- 如果组数大于5，只显示前4组和总结 -->
															<template v-else>
																<view class="set-item" v-for="(set, indexset) in action.sets.slice(0, 4)" :key="indexset+'set'">
																	<text class="set-num">{{indexset+1}}</text>
																	<text class="set-weight">{{set.weight}}kg × {{set.reps}}</text>
																</view>
																<view class="set-item set-summary">
																	<text class="set-summary-text">共{{action.sets.length}}组</text>
																</view>
															</template>
														</template>
														<template v-else>
															<view class="set-item">
																<text class="set-num">1</text>
																<text class="set-weight">未记录</text>
															</view>
														</template>
													</view>
												</view>
											</view>
										</view>
									</scroll-view>
								</view>
							</swiper-item>
						</swiper>
						
						<!-- 页面指示器 -->
						<view class="swiper-dots" v-if="trainingRecords.length>1">
							<view v-for="(_, indexdot) in trainingRecords.length" :key="indexdot+'dot'" 
								:class="['dot', indexdot === currentIndex ? 'active' : '']"
								 >
							</view>
						</view>
					</view>
					
					<!-- 操作按钮 -->
					<view class="action-buttons-list">
						<button class="action-btn edit" @click="editTraining">编辑</button>
						<button class="action-btn add" @click="copyToToday">复制</button>
						<button class="action-btn delete" @click="deleteTraining">删除</button>
						<button class="action-btn more" @click="moreAction">更多</button>
					</view>
				</view>
			</view>
		</view>

 
	</view>
</template>

<script>
// 导入uni-calendar组件
import uniCalendar from '@/uni_modules/uni-calendar/components/uni-calendar/uni-calendar.vue';
import { trainingApi } from '@/utils/api';
import { formatDate ,getThisYearRange} from '@/utils/dateUtils.js';
export default {
	components: {
		uniCalendar
	},
	data() {
		return {
			statusBarHeight: 20,
			currentDate: '',
			firstDay: '',
			lastDay: '',
			selectedDate: '',
			showPopup: false,
			selectedDates: [],
			currentIndex: 0, // 新增：当前显示的训练记录索引
			yearWithMonth: '',
			trainingRecords: [],
			dbTrainingDatas:{},
			unloading:false,
			updateTrainingTimeout: null, // 新增：防抖定时器
		}
	},
 
	onLoad() {
		this.statusBarHeight = getApp().getStatusBarHeight();
		// 使用日期工具函数初始化日历设置
		// 当前日期 
	 
		const {firstDay,lastDay,nowDay} = getThisYearRange();
		this.firstDay = firstDay;
		this.lastDay = lastDay;
		this.yearWithMonth = nowDay
		this.currentDate = nowDay;
		this.getDbTrainingData(); // 直接获取当前月份数据
	},

	methods: {

	getDbTrainingData(){
		this.unloading = false;
		 uni.showLoading({ title: '加载中...', mask: true });
		 trainingApi.getMonthlyTrainings(this.yearWithMonth).then(res => {
			console.log('获取到数据:', res);
			this.dbTrainingDatas = res.data || {}; 
		}).catch(err => {
			this.dbTrainingDatas = {};
		}).finally(() => {
			uni.hideLoading();
			this.unloading = true;
		});
		},
 
		
		// 点击日期
	dateClick(e) {
		this.selectedDate = e.fulldate;
		
		// 根据选中日期加载相应的训练数据
		const trainingData = this.getTrainingDataForDate(this.selectedDate);
		
		// 使用防抖处理，避免频繁更新
		if (this.updateTrainingTimeout) {
			clearTimeout(this.updateTrainingTimeout);
		}
		
		this.updateTrainingTimeout = setTimeout(() => {
			this.trainingRecords = trainingData;
			this.currentIndex = 0; // 重置索引
			this.showPopup = true;
		}, 50);
	},
		
			// 月份切换
	switchMonth(e) {
		console.log('月份切换', e);
		// 使用dateUtils格式化月份
		const date = new Date(e.year, e.month - 1, 1); // 月份从0开始，所以要减1
		const newYearMonth = formatDate(date, 'YYYY-MM');
		// 无论月份是否变化，都重新获取数据以确保实时性
		this.yearWithMonth = newYearMonth;
		this.getDbTrainingData(); // 直接获取数据，加载提示已在方法中处理
	},

		// 获取指定日期的训练数据
	getTrainingDataForDate(date) {
		
		if(date) {
			console.log('date',date);
			console.log('date😎😎😎😎',this.dbTrainingDatas);	
			return this.dbTrainingDatas[date] || [];
		}
		return [];
	},

	// 添加休息日
	addRestDay() {
		// 添加休息日逻辑
		uni.showToast({
				title: '已添加休息日',
				icon: 'success'
			});
			this.closePopup();
	},

 


	// 根据动作名称获取图片
	getActionImage(name) {
	    return `/static/actions/${name}.gif`
	},

	// 操作逻辑 ：弹窗、切换、跳转
			// 关闭弹窗
    closePopup() {
		this.showPopup = false;
	},
	// 弹窗逻辑
	moreAction() {
		uni.showActionSheet({
			itemList: ['添加新的力量训练', '导出训练记录', '复制到今日'],
			success: (res) => {
				switch (res.tapIndex) {
					case 0: // 添加新的力量训练
						this.addStrengthTraining();
						break;
					case 1: // 导出训练记录
						this.exportTrainingData();
						break;
					case 2: // 复制到今日
						this.copyToToday();
						break;
				}
			}
		});
	},
	openTrainingPage(){
		// 跳转到训练编辑页面
		uni.navigateTo({
			url: '/pages/training/training'
		});
	},
	// 编辑训练
	editTraining() {
		 // 跳转到训练编辑页面
	},
	
	// 复制到今天
	copyToToday() {
		// 复制到今天
	},
	
	// 删除训练
	deleteTraining() {
		uni.showActionSheet({
			itemList: ['确认删除'],
			success: (res) => {
				if (res.tapIndex === 0) {
				 this.currentIndex = this.currentIndex-1;
					uni.showToast({
						title: '已删除',
						icon: 'success'
					});
				}
			}
		});
	},
	// 训练记录滑动切换
	swiperChange(e) {
		this.currentIndex = e.detail.current;
	},
	// 导出训练数据
	exportTrainingData() {
		// 获取选中日期的训练数据
		uni.showToast({
				title: '暂未开发',
				icon: 'none'
			});
		},
	}
}
</script>

<style>
/* 基本样式 */
.container {
	display: flex;
	flex-direction: column;
	height: 100vh;
	background-color: #f5f5f5;
}

.status-bar {
	background-color: #fff;
	width: 100%;
}

.nav-bar {
	background-color: #fff;
	padding: 10px 15px;
	display: flex;
	justify-content: space-between;
	align-items: center;
	border-bottom: 1px solid #eee;
}

.nav-title {
	font-size: 18px;
	font-weight: bold;
	color: #333;
}

.nav-right {
	display: flex;
	align-items: center;
}

.calendar-icon {
	font-size: 20px;
}

.main-content {
	flex: 1;
	padding-bottom: calc(50px + var(--safe-area-inset-bottom));
	overflow-y: auto;
}

/* 日历样式 */
.calendar-container {
	margin-bottom: 15px;
}
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

.calendar-day-content {
	height: 100%;
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 2px;
}

.calendar-day-number {
	font-size: 14px;
	margin-bottom: 2px;
}

.calendar-day-training {
	width: 100%;
	display: flex;
	flex-direction: column;
	align-items: center;
}

.calendar-day-volume {
	background-color: #1890ff;
	color: white;
	text-align: center;
	padding: 2px;
	font-size: 10px;
	font-weight: bold;
	border-radius: 4px;
	width: 85%;
	margin-bottom: 2px;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.calendar-day-type {
	background-color: #000;
	color: white;
	text-align: center;
	padding: 2px;
	font-size: 9px;
	border-radius: 4px;
	width: 85%;
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
}

.calendar-day-rest {
	background-color: #52c41a;
	color: white;
	text-align: center;
	padding: 2px;
	font-size: 10px;
	font-weight: bold;
	border-radius: 4px;
	width: 85%;
}

/* 弹窗样式 */
.popup-mask {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background-color: rgba(0, 0, 0, 0.5);
	z-index: 100;
}

.day-popup {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	background-color: #fff;
	border-top-left-radius: 16px;
	border-top-right-radius: 16px;
	z-index: 101;
	height: 25vh;
	padding-bottom: calc(env(safe-area-inset-bottom) + 10px);
}

.day-popup-height {
	height: 80vh;
}

.popup-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 15px;
	border-bottom: 1px solid #f0f0f0;
}

.popup-date {
	font-size: 18px;
	font-weight: bold;
}

.close-btn {
	font-size: 24px;
	color: #999;
	padding: 0 10px;
}

.popup-content {
	padding: 15px;
	height: 100%;
	box-sizing: border-box;
}

.no-schedule {
	text-align: center;
	 
}

.no-schedule-text {
	font-size: 16px;
	color: #999;
}



.action-btn {
	width: 45%;
	margin: 5px;
	height: 40px;
	line-height: 40px;
	text-align: center;
	border-radius: 20px;
	color: #fff;
	font-size: 14px;
	border: none;
}

.action-btn.strength {
	background-color: #1890ff;
}

.action-btn.rest {
	background-color: #faad14;
}

.action-btn.add {
	background-color: #1890ff;
}

.action-btn.edit {
	background-color: #52c41a;
}

.action-btn.delete {
	background-color: #f5222d;
}

.action-btn.more {
	background-color: #722ed1;
}

/* 训练列表样式 */
.training-list {
	margin-bottom: 20px;
	max-height: 50vh;
	overflow-y: auto;
}

.training-item {
	background-color: #f9f9f9;
	border-radius: 8px;
	padding: 12px;
	margin-bottom: 10px;
}

.training-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 8px;
}

.training-name {
	font-size: 16px;
	font-weight: 500;
}

.training-type-badge {
	font-size: 12px;
	padding: 2px 8px;
	border-radius: 10px;
	color: #fff;
}

.training-type-badge.strength {
	background-color: #1890ff;
}

.training-type-badge.cardio {
	background-color: #52c41a;
}

.training-type-badge.rest {
	background-color: #faad14;
}

.training-type-badge.chest,
.training-type-badge.back,
.training-type-badge.leg,
.training-type-badge.shoulder,
.training-type-badge.arm {
	background-color: #1890ff;
}

.training-status {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.status-badge {
	font-size: 12px;
	padding: 2px 8px;
	border-radius: 10px;
}

.status-badge.completed {
	background-color: #52c41a;
	color: #fff;
}

.status-badge.incomplete {
	background-color: #f5f5f5;
	color: #999;
}

.training-volume {
	font-size: 12px;
	color: #666;
}

.has-schedule {
    height:100%;
	display: flex;
	flex-direction: column;
}
/* 训练记录滑动切换样式 */
.training-container {
	position: relative;
	width: 100%;
	display: flex;
	flex-direction: column;
	align-items: center;
	flex:1;
}


.action-buttons {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-top: 15px;
}

 
.action-buttons-list {
	display: flex;
	justify-content: space-around;
	width: 100%;
	height: 100px;
}

.training-swiper {
	width: 100%;
	height: 100%;
}

.exercises-scroll {
	height: 260px;
	width: 100%;
	overflow-y: auto;
	-webkit-overflow-scrolling: touch; /* 为iOS启用惯性滚动 */
	will-change: scroll-position; /* 提示浏览器将进行滚动，优化性能 */
	transform: translateZ(0); /* 启用GPU加速 */
	backface-visibility: hidden; /* 提高渲染性能 */
	-webkit-backface-visibility: hidden;
	perspective: 1000;
	-webkit-perspective: 1000;
}

/* 新增：组数摘要样式 */
.set-summary {
	background-color: #f9f9f9;
	border-radius: 4px;
	padding: 3px 0;
	margin-top: 5px;
}

.set-summary-text {
	font-size: 12px;
	color: #666;
	text-align: center;
	width: 100%;
	display: block;
}

/* 优化训练项目样式 */
.train-item {
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
	padding: 2px 2px;
	box-sizing: border-box;
	display: flex;
	justify-content: center;
	align-items: center;
	transform: translateZ(0); /* 启用GPU加速 */
	will-change: transform; /* 提示浏览器将进行变换，优化性能 */
}

/* 优化弹窗动画性能 */
.day-popup {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	background-color: #fff;
	border-top-left-radius: 16px;
	border-top-right-radius: 16px;
	z-index: 101;
	height: 25vh;
	padding-bottom: calc(env(safe-area-inset-bottom) + 10px);
	transform: translateZ(0); /* 启用GPU加速 */
	will-change: transform;
	transition: transform 0.3s cubic-bezier(0.23, 1, 0.32, 1);
}

.swiper-item {
	display: flex;
	justify-content: center;
	align-items: flex-start;
	height: 100%;
}

.training-card {
	width: 100%;
	background-color: #fff;
	border-radius: 12px;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
	padding: 15px;
	height: 100%;
}

/* 训练标题样式 */
.training-title {
	font-size: 24px;
	font-weight: bold;
	color: #333;
	margin-bottom: 15px;
}

/* 训练信息区域样式 */
.training-info {
	display: flex;
	margin-bottom: 20px;
}

.info-left {
	display: flex;
	flex-direction: column;
	width: 80px;
}

.info-value {
	font-size: 18px;
	font-weight: bold;
	color: #333;
}

.info-label {
	font-size: 14px;
	color: #999;
}

.info-center {
	flex: 1;
	display: flex;
	flex-direction: column;
	align-items: flex-start;
	margin-left: 20px;
}

.time-range {
	font-size: 16px;
	color: #666;
}

.time-date {
	font-size: 14px;
	color: #999;
}

/* 训练动作列表样式 */
.exercises-list {
	border-top: 1px solid #eee;
	padding-top: 15px;
}

.exercise-item {
	display: flex;
	margin-bottom: 20px;
	padding-bottom: 15px;
	border-bottom: 1px solid #f5f5f5;
}

.exercise-item:last-child {
	margin-bottom: 0;
	border-bottom: none;
}

.exercise-icon {
	width: 60px;
	height: 60px;
	border-radius: 8px;
	background-color: #f9f9f9;
	overflow: hidden;
	display: flex;
	justify-content: center;
	align-items: center;
}

.exercise-img {
	width: 50px;
	height: 50px;
}

.exercise-content {
	flex: 1;
	margin-left: 12px;
}

.exercise-name {
	font-size: 18px;
	font-weight: bold;
	color: #333;
	margin-bottom: 10px;
}

.exercise-sets {
	display: flex;
	flex-direction: column;
}

.set-item {
	display: flex;
	align-items: center;
	margin-bottom: 6px;
}

.set-num {
	width: 20px;
	font-size: 16px;
	color: #666;
}

.set-weight {
	font-size: 16px;
	color: #333;
}

.swiper-nav {
	position: absolute;
	top: 50%;
	transform: translateY(-50%);
	width: 100%;
	display: flex;
	justify-content: space-between;
	z-index: 10;
}

.nav-btn {
	width: 40px;
	height: 40px;
	border-radius: 50%;
	background-color: rgba(0, 0, 0, 0.5);
	display: flex;
	justify-content: center;
	align-items: center;
	color: #fff;
	font-size: 24px;
}

.nav-arrow {
	line-height: 1;
}

.swiper-dots {
	position: absolute;
	bottom: 10px;
	left: 50%;
	transform: translateX(-50%);
	display: flex;
	z-index: 10;
}

.dot {
	width: 8px;
	height: 8px;
	border-radius: 50%;
	background-color: #ccc;
	margin: 0 4px;
}

.dot.active {
	background-color: #1890ff;
}

:deep(.uni-calendar-item__weeks-box-item) {
	line-height: 1.2;
	height: 45px;
}

:deep(.uni-calendar-item__weeks-box-item-disable) {
	color: #ccc;
}

.multi-training-container {
	width: 100%;
	display: flex;
	flex-direction: column;
	align-items: center;
	margin-top: 2px; /* 与日期数字保持一些间距 */
	max-height: 75px; /* 限制最大高度 */
	overflow: hidden; /* 防止超出部分显示 */
}

.multi-training-item {
	width: 85%;
	padding: 1px 2px;
	margin-bottom: 1px;
	border-radius: 3px;
	background-color: #1890ff;
	overflow: hidden;
	white-space: nowrap;
	text-overflow: ellipsis;
	max-width: 100%; /* 确保不会超出父容器宽度 */
}

.multi-training-item.rest-day {
	background-color: #52c41a;
}

.multi-training-text {
	font-size: 6px; /* 减小字体大小 */
	color: white;
	text-align: center;
	display: block;
	width: 100%;
}
</style> 