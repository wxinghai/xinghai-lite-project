<template>
	<view v-if="hasActiveTraining"class="training-entry" @click="goToTraining">
		<view class="training-timer">
			<text class="timer-icon">⏱</text>
			<text class="timer-text"> {{trainingTime || '00:00'}}</text>
		</view>
	</view>
</template>

<script>
import TrainingManager from '@/utils/TrainingManager.js';

export default {
	name: 'TrainingEntry',
	data() {
		return {
			hasActiveTraining: false,
			currentTraining: null,
			trainingTime: '00:00'
		};
	},
	created() {
		// 监听最小化信息
		uni.$on('workout-page-minimized', this.handleWorkoutPageMinimized);
		// 添加更新时间的回调函数
		TrainingManager.addUpdateCallback(this.updateTime);
	},
	beforeDestroy() {
		// 移除事件监听
		uni.$off('workout-page-minimized', this.handleWorkoutPageMinimized);
	},
	methods: {
		// 跳转到训练页面
		goToTraining() {
			uni.navigateTo({
				url: '/pages/workout/workout'
			});
		},
		updateTime(elapsedTime, formattedTime) {
			console.log('训练时间更新:', formattedTime);
			this.trainingTime = formattedTime;
		},
		
		handleWorkoutPageMinimized() {
			this.hasActiveTraining = TrainingManager.hasActiveTraining();
			if(!this.hasActiveTraining){
				// 移除更新时间的回调函数
				TrainingManager.removeUpdateCallback(this.updateTime);
			}
		}
 
	}
};
</script>

<style scoped>
.training-entry {
	position: fixed;
	bottom: 40px; /* 调整为底部栏上方 */
	right: 15px; /* 贴在右下角 */
	background: linear-gradient(135deg, #1890ff, #096dd9);
	border-radius: 50%; /* 圆形 */
	width: 60px; /* 固定宽度 */
	height: 60px; /* 固定高度 */
	display: flex;
	align-items: center;
	justify-content: center;
	box-shadow: 0 2px 8px rgba(24, 144, 255, 0.3);
	z-index: 1000;
	transition: all 0.3s ease;
}

.training-entry:active {
	transform: scale(0.92);
	box-shadow: 0 1px 4px rgba(24, 144, 255, 0.4);
}

.training-timer {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
}

.timer-icon {
	font-size: 16px;
	color: white;
	margin-bottom: 2px;
}

.timer-text {
	color: white;
	font-size: 14px;
	font-weight: bold;
	font-family: monospace;
}
</style> 