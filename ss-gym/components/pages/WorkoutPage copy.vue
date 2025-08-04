<template>
	<view class="container">
		<view class="gredien">
			<view class="status-bar" :style="{ paddingTop: statusBarHeight + 'px' }">
				<view class="training-controls">
					<view  class="training-timer" @click="startTraining">
						<text class="timer-text">{{ trainingTime }}</text>
						<text class="timer-bofang" v-if="!isTrainingStarted">▶</text>
					</view>
					<view class="finish-btn" @click="showFinishModal">
						<text class="finish-btn-text">{{isTrainingStarted?'完成':'返回'}}</text>
					</view>
				</view>
			</view>
			<view class="progress-info">
				<view class="progress-text">
					<text class="progress-title visible-text">进行中</text>
					<text class="progress-name visible-text">{{ trainingData.name }}</text>
				</view>
				<view class="completed-status">
					<text class="completed-text visible-text">已完成</text>
					<text class="completed-count visible-text">{{ completedActions }}/{{ totalActions }}</text>
				</view>
			</view>
		</view>
		
		<view class="card">
			<scroll-view scroll-y class="action-list" :bounce="false" :show-scrollbar="false" :scroll-with-animation="true">
				<view v-for="(action, index) in trainingData.actions" :key="index" class="action-item-container">
					<view class="action-item" :class="{'action-expanded': expandedActionIndex === index }" @click="toggleActionExpand(index)">
						<view class="action-info">
							<image class="action-image" src="/static/actions/杠铃深蹲.gif" mode="aspectFill"></image>
							<view class="action-details">
								<view class="action-name visible-text">{{ action.name }}</view>
								<view class="action-sets visible-text">
									<view class="dots-container">
										<view v-for="n in action.sets" :key="n" class="set-indicator-dot" :class="{'dot-completed': n <= action.completedSets}"></view>
									</view>
									<text>{{ getCompletedSets(action) }}/{{ action.sets.length }} 组</text>
								</view>
							</view>
						</view>
						<view class="action-settings" @click.stop="openSettings(index)">
							<image src="/static/workout/shezhi.png" mode="aspectFit" class="settings-icon"></image>
						</view>
						<!-- 展开的详情部分 -->
						<view class="action-detail" v-if="expandedActionIndex === index">
							<view class="action-detail-header">
								<text class="action-detail-title visible-text">{{ action.name }}</text>
								<view class="action-detail-info">
									<view class="sets-dot"></view>
									<text class="action-detail-sets visible-text">{{ action.sets.length }} 组</text>
								</view>
							</view>
							
							<!-- 组数样式的调节 -->
							<view class="sets-container">
								<view v-for="(set, setIndex) in action.sets" :key="setIndex" class="set-row">
									<view class="set-right">	
									<view class="set-num set-bg">
										<text class="set-num-text">{{ setIndex + 1 }}</text>
									</view>

										<view class="input-wrapper set-bg" :class="{ 'set-complete-active': set.completed }"
										@click.stop>
											<input type="number" v-model="set.weight" class="set-input" step="0.01" min="0" max="999.99"/>
											<view class="input-suffix">{{action.unit}}</view>
										</view>

										<view class="input-wrapper set-bg" :class="{ 'set-complete-active': set.completed }"
										@click.stop>
											<input type="number" v-model="set.reps" class="set-input" />
											<text class="input-suffix">次</text>
										</view>
									 
										<view class="input-wrapper set-bg"  :class="{ 'set-complete-active': set.completed }"
										@click.stop="toggleSetComplete(index, setIndex)">
											<view class="set-input set-check" :class="{ 'set-complete-active-2': set.completed }">✓</view>
										</view>
									 
									</view>
									<view class="set-more" @click.stop>
										<text class="set-more-dots">···</text>
									</view>
								</view>
							</view>
							
							<view class="action-buttons">
								<view class="action-button add-set-button" @click.stop="addSet(index)">
									<text class="button-text visible-text">新增一组</text>
								</view>
							</view>
						</view>
					</view>
				</view>
			</scroll-view>
		</view>
		
		<view class="bottom-toolbar">
			<view class="toolbar-item" @click="minimizeWorkout">
				<view class="toolbar-icon">
					<image src="/static/workout/sx.png" mode="aspectFit" class="toolbar-icon-img"></image>
				</view>
				<text class="toolbar-text visible-text">最小化</text>
			</view>
			<view class="toolbar-item" @click="showAddAction">
				<view class="add-icon">+</view>
				<text class="toolbar-text visible-text">添加动作</text>
			</view>
			<view class="toolbar-item" @click="showMenu">
				<view class="toolbar-icon">
					<image src="/static/workout/caidanguanli.png" mode="aspectFit" class="toolbar-icon-img"></image>
				</view>
				<text class="toolbar-text visible-text">菜单</text>
			</view>
		</view>
		
		<!-- 完成训练弹窗 -->
		<view class="finish-modal" v-if="showFinishModalFlag">
			<view class="modal-mask" @click="cancelFinishModal"></view>
			<view class="modal-content">
				<view class="modal-icon">
					<view class="smile-face">
						<text class="smile-eyes">⌒⌒</text>
						<text class="smile-mouth">◡</text>
					</view>
				</view>
				<view class="modal-title">完成训练</view>
				<view class="modal-subtitle">是否已经完成训练</view>
				<view class="modal-buttons">
					<view class="modal-button primary-button" @click="finishTraining">
						<text class="primary-button-text">完成并生成分享图</text>
					</view>
					<view class="modal-button cancel-button" @click="cancelFinishModal">
						<text class="cancel-button-text">取消</text>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import { trainingApi } from '@/utils/api';
export default {
	name: 'WorkoutPage',
	props: {
	
		pageOption: {
			type: Object,
			default: () => null
		},
		trainingData: {
			type: Object,
			default: () => null
		},
		trainingTime: {
			type: String,
			default: '00:00'
		},
		isTrainingStarted: {
			type: Boolean,
			default: false
		},
		showFinishModalFlag: {
			type: Boolean,
			default: false
		}
	},
	data() {
		return {
			statusBarHeight: 20,
			expandedActionIndex: 0,
		};
	},
	computed: {
		totalActions() {
			return this.trainingData.actions.length;
		},
		completedActions() {
			return this.trainingData.actions.filter(action => action.completed).length;
		},
		displayWorkoutName() {
			return this.trainingData.name || '默认训练';
		},
		displayWorkoutType() {
			return this.trainingData.type || "力量训练";
		},
		displayWorkoutDate() {
			return this.trainingData.date || '8月15';
		}
	},
	created() {
		this.statusBarHeight = getApp().getStatusBarHeight();
	
	},

	methods: {
	 
		openSettings(index) {
			const action = this.trainingData.actions[index];
			uni.showActionSheet({
				itemList: [ '切换单位','删除动作'],
				success: (res) => {
					if(res.tapIndex === 0){

						if(action.unit === 'kg'){
							action.unit = 'lbs';
							action.sets.forEach(set => {
								if(set.weight){
									set.weight = set.weight / 0.453592;
								}
							});


						}else{
							action.unit = 'kg';
							action.sets.forEach(set => {
								if(set.weight){
									set.weight = set.weight * 0.453592;
								}
							});	
						}
					}else if(res.tapIndex === 1){	
						// 删除动作
						uni.showModal({
							title: '确认删除',
							content: `确定要删除"${action.name}"吗？`,
							success: (res) => {
								if (res.confirm) {
									this.trainingData.actions.splice(index, 1);
									if (this.expandedActionIndex === index) {
										this.expandedActionIndex = -1;
									} else if (this.expandedActionIndex > index) {
										this.expandedActionIndex--;
									}
								}
							}
						});
				 
				}}
			});
		},

 

		toggleActionExpand(index) {
			if (this.expandedActionIndex === index) {
				this.expandedActionIndex = -1; // 关闭当前展开的动作
			} else {
				this.expandedActionIndex = index; // 展开点击的动作
			}
		},
		getCompletedSets(action) {
			return action.sets.filter(set => set.completed).length;
		},
		// 切换动作组完成状态
		toggleSetComplete(actionIndex, setIndex) {
			const action = this.trainingData.actions[actionIndex];
	 
			action.sets[setIndex].completed = !action.sets[setIndex].completed;
			
			// 更新已完成组数
			const completedCount = action.sets.filter(s => s.completed).length;
			action.completedSets = completedCount;
			
			// 检查动作是否完成
			if (action.completedSets === action.sets.length) {
				action.completed = true;
			} else {
				action.completed = false;
			}
			
		},

		// 添加训练组
		addSet(index) {

			 let lastIndex = this.trainingData.actions[index].sets.length-1;
			 this.trainingData.actions[index].sets.push({
				...this.trainingData.actions[index].sets[lastIndex],
				completed:false});
		},
 
 
		finishWorkout() {
			// 调用finishTraining方法完成训练
			this.finishTraining();
		},
		minimizeWorkout() {
			this.$emit('minimize-workout');
		},
		showAddAction() {
			// 发送显示添加动作事件
			this.$emit('show-add-action');
		},
		showMenu() {
			this.$emit('show-menu');
		},
		// 开始训练
		startTraining() {
			this.$emit('start-training');
		},
		
		// 显示完成训练弹窗
		showFinishModal() {
			// 如果没开始训练,应该时返回按钮,返回按钮则不弹窗
			this.$emit('show-finish-modal');			
		},
		
		// 取消完成训练弹窗
		cancelFinishModal() {
			this.$emit('cancel-finish-modal');
		},
		
		// 完成训练
		finishTraining() {
			this.$emit('finish-training');
		},
		// 格式化日期
		formatDate(date) {
			const year = date.getFullYear();
			const month = (date.getMonth() + 1).toString().padStart(2, '0');
			const day = date.getDate().toString().padStart(2, '0');
			return `${year}-${month}-${day}`;
		},
 
	}
}
</script>

<style>
.container {
	overflow: hidden;
	background-color: transparent;
	height: 100vh;
}

.gredien {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	height: 180px;
	background: linear-gradient(135deg, #1890ff, #096dd9);
	z-index: 20;
	box-shadow: 0 2px 15px rgba(24, 144, 255, 0.3);
	color: #f6ffed;
}

.status-bar {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding-left: 10px;
	padding-right: 10px;
}

.finish-btn {
	background-color: rgba(255, 255, 255, 0.3);
	border-radius: 20px;
	padding: 4px 30px;
	height: 36px; /* 固定高度 */
	display: flex;
	align-items: center;
	justify-content: center;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
	transition: all 0.2s ease;
}

.finish-btn:active {
	transform: scale(0.95);
	background-color: rgba(255, 255, 255, 0.35);
}

.finish-btn-text {
	color: white;
	font-size: 16px;
	font-weight: bold;
	text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.progress-info {
	display: flex;
	justify-content: space-between;
	padding: 15px;
	align-items: center;
	margin-top: 5px;
}

.progress-text {
	display: flex;
	flex-direction: column;
	color: #f6ffed;
}

.progress-title {
	color: rgba(255, 255, 255, 0.9);
	font-size: 14px;
	margin-bottom: 5px;
}

.progress-name {
	color: white;
	font-size: 22px;
	font-weight: bold;
	text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.completed-status {
	display: flex;
	flex-direction: column;
	align-items: flex-end;
}

.completed-text {
	color: rgba(255, 255, 255, 0.9);
	font-size: 14px;
	margin-bottom: 5px;
}

.completed-count {
	color: white;
	font-size: 22px;
	font-weight: bold;
	text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.card {
	margin-top: 180px;
	margin-bottom: 60px;
	background-color: #f7f9fc;
	border-radius: 20px 20px 0 0;
	overflow: hidden;
	z-index: 5;
	position: relative;
	box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.08);
}

.action-list {
	height: calc(100vh - 180px - 60px);
	overscroll-behavior: none;
	-webkit-overscroll-behavior: none;
	overflow-y: auto;
	overflow-x: hidden;
	background-color: transparent;
	position: relative;
	padding-top: 5px;
}

.action-list:before {
	content: '';
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	height: 15px;
	background-color: #f7f9fc;
	margin-top: -12px;
	border-radius: 20px 20px 0 0;
	z-index: 6;
}

.action-item-container {
	padding: 8px 15px;
}

.action-item {
	background-color: white;
	border-radius: 16px;
	padding: 16px;
	display: flex;
	flex-direction: column;
	box-shadow: 0 3px 12px rgba(0, 0, 0, 0.06);
	z-index: 6;
	position: relative;
	transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.action-item:active {
	transform: scale(0.98);
	box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.action-info {
	display: flex;
	margin-bottom: 15px;
}

.action-image {
	width: 65px;
	height: 65px;
	border-radius: 12px;
	margin-right: 15px;
	object-fit: cover;
	box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.action-details {
	flex: 1;
	display: flex;
	flex-direction: column;
	justify-content: center;
}

.action-name {
	font-size: 18px;
	font-weight: bold;
	color: #222;
	margin-bottom: 6px;
}

.action-sets {
	color: #444;
	font-size: 15px;
	display: flex;
	align-items: center;
}

.dots-container {
	display: flex;
	align-items: center;
	margin-right: 10px;
}

.set-indicator-dot {
	width: 8px;
	height: 8px;
	border-radius: 50%;
	background-color: #e0e0e0;
	margin-right: 4px;
}

.dot-completed {
	background-color: #1890ff;
}

.action-settings {
	position: absolute;
	top: 16px;
    right: 16px;
	width: 36px;
	height: 36px;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	background-color: rgba(0, 0, 0, 0.04);
	transition: all 0.2s ease;
}

.action-settings:active {
	background-color: rgba(0, 0, 0, 0.1);
	transform: scale(0.95);
}

.settings-icon {
	width: 20px;
	height: 20px;
	opacity: 0.8;
}

.bottom-toolbar {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	height: 55px;
	background-color: white;
	display: flex;
	justify-content: space-around;
	align-items: center;
	box-shadow: 0 -2px 15px rgba(0, 0, 0, 0.08);
	z-index: 900;
	padding-bottom: var(--safe-area-inset-bottom);
}

.toolbar-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	transition: all 0.2s ease;
	padding: 6px 10px;
}

.toolbar-item:active {
	opacity: 0.7;
	transform: scale(0.97);
}

.toolbar-icon {
	width: 22px;
	height: 22px;
	margin-bottom: 3px;
	display: flex;
	justify-content: center;
	align-items: center;
}

.toolbar-icon-img {
	width: 22px;
	height: 22px;
}

.toolbar-text {
	font-size: 12px;
	color: #444;
}

.add-icon {
	width: 34px;
	height: 34px;
	background-color: #1890ff;
	color: white;
	border-radius: 50%;
	display: flex;
	justify-content: center;
	align-items: center;
	margin-bottom: 3px;
	font-size: 20px;
	font-weight: bold;
	box-shadow: 0 3px 8px rgba(24, 144, 255, 0.4);
}

.visible-text {
	visibility: visible !important;
	opacity: 1 !important;
	color: inherit !important;
}

.action-expanded {
	margin: 5px 0;
	box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
	border-left: 0; /* 移除左边蓝色条 */
}

.action-detail {
	margin-top: 15px;
	padding-top: 15px;
	border-top: 1px solid #f0f0f0;
}

.action-detail-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 15px;
}

.action-detail-title {
	font-size: 18px;
	font-weight: bold;
	color: #222;
}

.action-detail-info {
	display: flex;
	align-items: center;
}

.sets-dot {
	width: 7px;
	height: 7px;
	background-color: #1890ff;
	border-radius: 50%;
	margin-right: 6px;
	display: inline-block;
}

.action-detail-sets {
	font-size: 15px;
	color: #444;
}

.sets-container {
	margin-bottom: 15px;
}

.set-row {
	display: flex;
	align-items: center;
	padding: 15px 0;
	border-bottom: 1px solid #f0f0f0;
	width: 100%;
	flex-wrap: nowrap;
	justify-content: space-between;
}
.set-right {
	display: flex;
}

.set-num {
	width: 30px;
	display: flex;
	justify-content: center;
	align-items: center;
	flex-shrink: 0;
}

.set-num-text {
	font-size: 18px;
	color: #333;
}

.input-wrapper {
	position: relative;
	display: flex;
	align-items: center;
	width: 60px;
	height: 40px;
	margin-left: 13px;
	 
}

.set-bg {
	background-color:rgb(235,235,235);
	border-radius: 10px;
	position: relative;
	box-sizing: border-box;
}

.set-input {
	font-size: 18px;
	color: inherit;
    height: 40px;
	width: 60px;
	text-align: center;
	-webkit-appearance: none;
	
}

.set-check {
	color: rgb(180,180,180) ;
	font-size: 25px;
	font-weight: normal;
	display: flex;
	justify-content: center;
	align-items: center;
}

.input-suffix {
	position: absolute;
	left: 4px;
	top: 1px;
	font-size: 9px;
	color: inherit;
	font-weight: normal;
	
}

.set-complete-active {
	background-color: #1890ff;
	color: #ffffff;
}
.set-complete-active-2 { 
	color: #ffffff;
}

.set-more {
	width: 30px;
	display: flex;
	justify-content: center;
	align-items: center;
	flex-shrink: 0;
}

.set-more-dots {
	color: #999;
	font-size: 20px;
	letter-spacing: -2px;
}

.action-buttons {
	padding-top: 5px;
}

.action-button {
	height: 45px;
	border-radius: 8px;
	display: flex;
	align-items: center;
	justify-content: center;
}

.add-set-button {
	background-color: #f5f5f5;
	border: 1px solid #ddd;
}

.add-set-button:active {
	opacity: 0.8;
}

.button-text {
	font-size: 16px;
	color: #333;
	font-weight: bold;
}

.training-controls {
	display: flex;
	flex-direction: row;
	align-items: center;
	justify-content: space-between;
	width: 100%;
	padding: 0 5px;
	margin-bottom: 10px;
	height: 40px; /* 固定高度 */
	margin-top: 10px;
}

.training-timer {
	border-radius: 20px;
	padding: 5px 5px;
	height: 36px; /* 固定高度 */
	min-width: 100px; /* 固定最小宽度 */
	display: flex;
	align-items: center;
	justify-content: center;
	transition: all 0.2s ease;
	box-sizing: border-box;
}

.timer-text {
	color: white;
	font-size: 30px;
	font-weight: bold;
	text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.timer-bofang {
	margin-left: 10px;
	font-size: 16px;
	background-color: rgba(255, 255, 255, 0.3);
	border-radius: 8px; 
	height: 26px;
	width: 26px;  
	display: flex;
	align-items: center;
	justify-content: center;
}

.finish-modal {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	z-index: 9999;
	display: flex;
	justify-content: center;
	align-items: center;
}

.modal-mask {
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background-color: rgba(0, 0, 0, 0.6);
}

.modal-content {
	width: 80%;
	max-width: 320px;
	background-color: #fff;
	border-radius: 16px;
	padding: 30px 20px;
	display: flex;
	flex-direction: column;
	align-items: center;
	box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
	position: relative;
	z-index: 10000;
}

.modal-icon {
	margin-bottom: 20px;
}

.smile-face {
	width: 100px;
	height: 100px;
	background-color: #FFE082;
	border-radius: 50%;
	display: flex;
	flex-direction: column;
	justify-content: center;
	align-items: center;
	border: 2px solid #FFD54F;
	box-shadow: 0 3px 10px rgba(255, 193, 7, 0.3);
}

.smile-eyes {
	font-size: 30px;
	line-height: 30px;
	color: #5D4037;
	margin-bottom: 5px;
	font-weight: bold;
}

.smile-mouth {
	font-size: 40px;
	line-height: 40px;
	color: #5D4037;
	transform: scale(2, 1);
	display: block;
}

.modal-title {
	font-size: 22px;
	font-weight: bold;
	color: #333;
	margin-bottom: 10px;
	text-align: center;
}

.modal-subtitle {
	font-size: 16px;
	color: #666;
	margin-bottom: 30px;
	text-align: center;
}

.modal-buttons {
	width: 100%;
	display: flex;
	flex-direction: column;
	gap: 15px;
}

.modal-button {
	width: 100%;
	height: 50px;
	border-radius: 25px;
	display: flex;
	justify-content: center;
	align-items: center;
	transition: all 0.2s ease;
}

.primary-button {
	background-color: #1890FF;
	box-shadow: 0 3px 8px rgba(24, 144, 255, 0.3);
}

.primary-button:active {
	transform: scale(0.98);
	box-shadow: 0 2px 5px rgba(24, 144, 255, 0.2);
}

.primary-button-text {
	color: white;
	font-size: 16px;
	font-weight: bold;
}

.cancel-button {
	background-color: #F5F5F5;
	border: 1px solid #E0E0E0;
}

.cancel-button:active {
	transform: scale(0.98);
	background-color: #EEEEEE;
}

.cancel-button-text {
	color: #666;
	font-size: 16px;
}

@media screen and (max-height: 700px) {
	.gredien {
		height: 150px;
	}
	
	.card {
		margin-top: 150px;
	}
	
	.action-list {
		height: calc(100vh - 150px - 60px);
	}
	
	.progress-name, .completed-count {
		font-size: 18px;
	}
	
	.progress-title, .completed-text {
		font-size: 12px;
	}
	
	.action-image {
		width: 55px;
		height: 55px;
	}
}
</style> 