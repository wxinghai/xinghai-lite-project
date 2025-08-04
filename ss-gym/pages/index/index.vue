<template>
	<view class="container">
		<!-- 状态栏占位 -->
		<view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
		<view class="status-bar-fix" :style="{ height: statusBarHeight + 'px' }"></view>
		<!-- 主内容区 -->
		<view class="main-content">
			<view class="main-title">今日训练</view>
			<!-- 训练进度卡片列表 - 纵向排列 -->
			<view class="progress-cards-container">
				<!-- 使用v-for遍历训练进度卡数据 -->
				<view class="progress-card" 
					v-for="(workout, index) in todayWorkouts" 
					:key="index"
					@click="checkAndStartTraining(workout)">
					<view class="progress-info">
						<view class="progress-percent"><text>{{ workout.name }}</text></view>
						<view class="progress-details">
							<text class="tag">个人模板</text>
							<text class="count">动作 {{ workout.trainingActions.length }}</text>
							<text class="count">组数 {{ getSetCount(workout.trainingActions) }}</text>
						</view>
					</view>
					<view class="go-button">
						<text>GO!</text>
					</view>
				</view>
			</view>
			
			<!-- 计划选项卡 -->
			<view class="plan-tabs">
				<view :class="['tab', activeTab === 0 ? 'active' : '']" @click="switchTab(0)">官方计划</view>
				<view :class="['tab', activeTab === 1 ? 'active' : '']" @click="switchTab(1)">个人计划</view>
			</view>
			
			<!-- 官方计划列表 -->
			<scroll-view scroll-y class="plan-list" v-if="activeTab === 0" :show-scrollbar="false" :bounce="true" :enhanced="true" :fast-deceleration="false" :scroll-with-animation="true" style="height: calc(100vh - 320px); flex: 1;">
				<!-- 使用v-for遍历训练计划数组 -->
				<view 
					v-for="(plan, index) in trainingPlans" 
					:key="plan.id"
					class="plan-card" 
					@click="checkAndOpenPlanDetail(plan)"
					@longpress="showOperationMenu(plan, index, 'official')"
				>
					<view class="plan-info">
						<view class="plan-icon">{{ plan.icon }}</view>
						<view class="plan-content">
							<view class="plan-name">{{ plan.name }}</view>
							<view class="plan-desc">{{ plan.description }}</view>
							<view class="plan-sessions">总训练次数: {{ plan.totalSessions }}次</view>
						</view>
						<view class="more-indicator-container" @click.stop="showOperationMenu(plan, index, 'official')">
							<view class="more-indicator">⋮</view>
						</view>
					</view>
				</view>
			</scroll-view>
			
			<!-- 个人计划列表 -->
			<scroll-view scroll-y class="plan-list" v-if="activeTab === 1" :show-scrollbar="false" :bounce="true" :enhanced="true" :fast-deceleration="false" :scroll-with-animation="true" style="height: calc(100vh - 320px); flex: 1;">
				<view 
					v-for="(plan, index) in personalPlans" 
					:key="plan.id"
					class="plan-card" 
					@click="checkAndOpenPlanDetail(plan)"
					@longpress="showOperationMenu(plan, index, 'personal')"
				>
					<view class="plan-info">
						<view class="plan-icon">{{ plan.icon || '📝' }}</view>
						<view class="plan-content">
							<view class="plan-name">{{ plan.name }}</view>
							<view class="plan-desc">{{ plan.description }}</view>
							<view class="plan-sessions">总训练次数: {{ plan.totalSessions }}次</view>
						</view>
						<view class="more-indicator-container" @click.stop="showOperationMenu(plan, index, 'personal')">
							<view class="more-indicator">⋮</view>
						</view>
					</view>
				</view>
				
				<!-- 空状态提示 -->
				<view class="empty-state" v-if="personalPlans.length === 0">
					<text class="empty-text">暂无个人计划</text>
				</view>
				
				<!-- 添加按钮 -->
				<view class="add-plan-button" @click="addPersonalPlan">
					<view class="add-icon">+</view>
					<text class="add-text">添加个人计划</text>
				</view>
			</scroll-view>
 
		</view>
	</view>
</template>

<script>
import TrainingManager from '@/utils/TrainingManager.js';
import { trainingApi } from '@/utils/api';
import { formatDate } from '@/utils/dateUtils.js';

export default {
	data() {
		return {
			statusBarHeight: 20,
			activeTab: 0,
			// 今日训练数据
			todayWorkouts: [],
			// 训练计划数组
			trainingPlans: [
				{
					id: 'plan1',
					icon: '💪',
					name: '22天力量训练周期',
					description: '系统化增强肌肉力量，从70%RM到96%RM的全面训练计划',
					totalSessions: 22
				},
				{
					id: 'plan2',
					icon: '🔥',
					name: '30天减脂训练',
					description: '高效燃脂计划，HIIT和力量训练结合，科学减脂塑形',
					totalSessions: 30
				}
				 
			],
			// 个人计划数组
			personalPlans: []
		}
	},

	onShow() {
		const key = formatDate(new Date());
		
		trainingApi.getTodyTrainingList(key).then(res => {
			//buxing
			 this.todayWorkouts = res.data;
			 console.log('res❤️❤️❤️❤️',res.data);
		}).catch(err => {
			console.log('API请求错误',err);
	 
		});
	},
	// 需要实时获取数据
	onLoad() {
		this.statusBarHeight = getApp().getStatusBarHeight();
	
	},
	methods: {
		switchTab(index) {
			this.activeTab = index;
		},
		checkAndStartTraining(workout) {
			// 如果有存在训练的，判断是不是同一个，同一个直接进入就可以了，
			    const isTraining = TrainingManager.hasActiveTraining();
				if(isTraining){
					this.trainingData = TrainingManager.getCurrentTraining().trainingData;

					if(this.trainingData.id === workout.id){
						uni.navigateTo({
							url: '/pages/workout/workout?id=' + workout.trainingId,
							animationType: 'slide-in-bottom'
						});
					}else{
						uni.showToast({
							title: '只能启动一个训练',
							icon: 'none'
						});
					}
				}else{
					uni.navigateTo({
						url: '/pages/workout/workout?id=' + workout.trainingId,
						animationType: 'slide-in-bottom'
					})
				}
		 
		},
		 
		startTraining(workout) {
			// 传递选中的训练类型
			this.$emit('switch-page', 'workout', { 
				workoutType: workout ? workout.workoutType : 'strength',
				workoutName: workout ? workout.name : '训练'
			});
		},
		navigateTo(url) {
			uni.navigateTo({
				url: url
			});
		},
		checkAndOpenPlanDetail(plan) {
			// 移除训练状态检查，直接打开计划详情
			this.openPlanDetail(plan);
		},
		openPlanDetail(plan) {
			// 通过事件告知父组件切换到计划详情页，并传递选中的计划
			this.$emit('switch-page', 'plan-detail', { selectedPlan: plan });
		},
		addPersonalPlan() {
		 
			// 添加个人计划逻辑
			uni.showModal({
				title: '提示',
				content: '您确定要添加个人计划吗？',
				success: (res) => {
					if (res.confirm) {
						// 模拟添加一个个人计划
						const newPlan = {
							id: Date.now(), // 使用时间戳作为ID
							icon: '📝',
							name: '新个人计划',
							description: '这是一个新的个人训练计划',
							totalSessions: 0,
							createdAt: new Date()
						};
						this.personalPlans.push(newPlan);
						uni.showToast({
							title: '个人计划添加成功',
							icon: 'none'
						});
					}
				}
			});
		},
		// 显示操作菜单
		showOperationMenu(plan, index, type) {
			// 震动反馈
			uni.vibrateShort({
				success: () => {
					console.log('震动成功')
				}
			});
			
			// 根据计划类型显示不同的选项
			let itemList = ['删除'];
			if (type === 'personal') {
				itemList = ['编辑', '删除', '置顶'];
			} else if (type === 'official') {
				itemList = ['添加到个人计划', '收藏'];
			}
			
			// 显示菜单
			uni.showActionSheet({
				itemList: itemList,
				success: (res) => {
					if (type === 'personal') {
						switch (res.tapIndex) {
							case 0: // 编辑
								this.editPlan(plan, index);
								break;
							case 1: // 删除
								this.deletePlan(plan, index, type);
								break;
							case 2: // 置顶
								this.movePlanToTop(index);
								break;
						}
					} else if (type === 'official' && res.tapIndex === 0) {
						// 添加到个人计划
						this.addToPersonal(plan);
					}
				},
				fail: () => {
					console.log('操作菜单关闭');
				}
			});
		},
		// 删除计划
		deletePlan(plan, index, type) {
			uni.showModal({
				title: '确认删除',
				content: `确定要删除"${plan.name}"吗？`,
				success: (res) => {
					if (res.confirm) {
						if (type === 'personal') {
							this.personalPlans.splice(index, 1);
							uni.showToast({
								title: '删除成功',
								icon: 'none'
							});
						}
					}
				}
			});
		},
		// 编辑计划
		editPlan(plan, index) {
			// 这里可以跳转到编辑页面或者弹出编辑对话框
			uni.showToast({
				title: '编辑功能开发中',
				icon: 'none'
			});
		},
		// 置顶计划
		movePlanToTop(index) {
			if (index > 0) {
				const plan = this.personalPlans[index];
				this.personalPlans.splice(index, 1);
				this.personalPlans.unshift(plan);
				uni.showToast({
					title: '已置顶',
					icon: 'none'
				});
			}
		},
		// 添加到个人计划
		addToPersonal(plan) {
			// 复制官方计划到个人计划
			const personalPlan = {
				id: Date.now(),
				icon: plan.icon,
				name: `${plan.name} (个人)`,
				description: plan.description,
				totalSessions: plan.totalSessions,
				createdAt: new Date()
			};
			
			this.personalPlans.push(personalPlan);
			uni.showToast({
				title: '已添加到个人计划',
				icon: 'none'
			});
		},
		getSetCount(actions) {
			let count = 0;
			if(!actions) return 0;
			actions.forEach(action => {
				count += action.trainingSets.length;
			});
			return count;
		}
	}
}
</script>

<style>
.container {
	display: flex;
	flex-direction: column;
	height: 100vh;
	background-color: #f5f5f5;
}

.status-bar {
	background-color: #f5f5f5;
	width: 100%;
}
 
.status-bar-fix {
	background-color: #f5f5f5;
	width: 100%;
	position: fixed;
	top: 0;
	left: 0;
	z-index: 10;
}

.main-content {
	flex: 1;
	padding: 15px;
	padding-top: 35px;
	padding-bottom: calc(50px + var(--safe-area-inset-bottom));
	background-color: #f5f5f5;
}

.main-title {
	font-size: 24px;
	font-weight: bold;
	margin-top: 10px;
	margin-bottom: 15px;
}

/* 训练进度卡片列表 - 纵向排列 */
.progress-cards-container {
	display: flex;
	flex-direction: column; /* 纵向排列 */
	padding: 5px 0;
	margin-bottom: 20px; /* 添加底部间距 */
}

.progress-card {
	background-color: #fff;
	border-radius: 12px;
	padding: 15px;
	margin-bottom: 15px; /* 卡片之间有间距 */
	display: flex;
	justify-content: space-between;
	align-items: center;
	box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
	min-width: 280px;
	flex-shrink: 0;
}

.progress-card:last-child {
	margin-bottom: 5px; /* 最后一个卡片底部间距 */
}

.progress-info {
	flex: 1;
}

.progress-percent {
	font-size: 28px;
	font-weight: bold;
	color: #333;
	margin-bottom: 8px;
}

.progress-percent text {
	font-size: 14px;
	color: #666;
	margin-left: 5px;
}

.progress-details {
	display: flex;
	flex-wrap: wrap;
}

.tag {
	background-color: #e6f7ff;
	color: #1890ff;
	padding: 2px 8px;
	font-size: 12px;
	border-radius: 4px;
	margin-right: 8px;
}

.count {
	color: #666;
	font-size: 12px;
	margin-right: 8px;
}

.go-button {
	background: linear-gradient(135deg, #1890ff, #096dd9);
	color: white;
	width: 60px;
	height: 60px;
	border-radius: 30px;
	display: flex;
	justify-content: center;
	align-items: center;
	font-size: 18px;
	font-weight: bold;
	box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
}

.plan-tabs {
	display: flex;
	margin-bottom: 15px;
	background-color: #fff;
	border-radius: 8px;
	padding: 3px;
	box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.tab {
	flex: 1;
	text-align: center;
	padding: 8px 0;
	font-size: 14px;
	color: #666;
	border-radius: 6px;
}

.tab.active {
	background-color: #1890ff;
	color: white;
}

/* 优化卡片列表，每张卡片有视觉间隔，使滚动感更流畅 */
.plan-card {
	background-color: white;
	border-radius: 12px;
	margin-bottom: 15px;
	overflow: hidden;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
	transition: transform 0.2s ease, box-shadow 0.2s ease;
	position: relative; /* 为卡片提供定位上下文 */
	transform: translateZ(0); /* 启用硬件加速，提升滚动性能 */
	will-change: transform; /* 提示浏览器将进行变换，优化性能 */
	
	/* 添加长按提示标识 */
}

/* 滚动容器样式优化 */
.plan-list {
	height: calc(100vh - 320px); /* 固定高度，减去其他UI元素高度 */
	flex: 1;
	overflow-y: auto; /* 允许内容垂直滚动 */
	position: relative;
	-webkit-overflow-scrolling: touch; /* iOS设备上的惯性滚动 */
	scroll-behavior: smooth; /* 平滑滚动效果 */
	padding: 5px 3px; /* 添加内边距避免内容紧贴边缘 */
	transform: translateZ(0); /* 启用GPU加速 */
	backface-visibility: hidden; /* 提高渲染性能 */
	-webkit-backface-visibility: hidden;
	perspective: 1000;
	-webkit-perspective: 1000;
}

/* 添加触摸反馈效果，增强滚动过程中的视觉体验 */
.plan-card:active {
	transform: scale(0.98);
	box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

/* 长按操作按钮容器 */
.plan-card .more-indicator-container {
	position: absolute;
	right: 5px;
	top: 5px;
	width: 40px;
	height: 40px;
	display: flex;
	justify-content: center;
	align-items: center;
	z-index: 10;
}

/* 长按提示标识 */
.plan-card .more-indicator {
	font-size: 22px;
	font-weight: bold;
	color: #1890ff;
	opacity: 0.8;
	width: 30px;
	height: 30px;
	text-align: center;
	line-height: 30px;
	background-color: rgba(255, 255, 255, 0.5);
	border-radius: 15px;
}

.plan-card .more-indicator-container:active .more-indicator {
	background-color: rgba(24, 144, 255, 0.1);
	transform: scale(0.9);
}

.plan-info {
	display: flex;
	padding: 15px;
	padding-right: 45px; /* 为操作按钮留出空间 */
	position: relative;
}

.plan-icon {
	font-size: 30px;
	margin-right: 15px;
	background-color: #f0f5ff;
	width: 50px;
	height: 50px;
	display: flex;
	align-items: center;
	justify-content: center;
	border-radius: 25px;
}

.plan-content {
	flex: 1;
}

.plan-name {
	font-size: 16px;
	font-weight: bold;
	margin-bottom: 5px;
	color: #333;
}

.plan-desc {
	font-size: 12px;
	color: #666;
	margin-bottom: 8px;
	line-height: 1.4;
}

.plan-progress {
	margin-bottom: 8px;
}

.progress-bar {
	height: 4px;
	background-color: #f0f0f0;
	border-radius: 2px;
	margin-bottom: 4px;
}

.progress-fill {
	height: 100%;
	background-color: #1890ff;
	border-radius: 2px;
}

.progress-text {
	font-size: 12px;
	color: #999;
}

.plan-phases {
	display: flex;
	margin-top: 8px;
}

.phase-dot {
	width: 12px;
	height: 12px;
	border-radius: 50%;
	margin-right: 25px;
	position: relative;
}

.phase-dot::after {
	content: "";
	position: absolute;
	height: 2px;
	background-color: #ddd;
	width: 25px;
	top: 5px;
	left: 12px;
}

.phase-dot:last-child::after {
	display: none;
}

.phase-dot.white {
	background-color: #f0f0f0;
	border: 1px solid #ddd;
}

.phase-dot.blue {
	background-color: #1890ff;
}

.phase-dot.green {
	background-color: #52c41a;
}

.phase-dot.red {
	background-color: #f5222d;
}

.phase-dot.active::before {
	content: "";
	position: absolute;
	width: 18px;
	height: 18px;
	border-radius: 50%;
	border: 2px solid rgba(24, 144, 255, 0.3);
	top: -5px;
	left: -5px;
}

.plan-tags {
	display: flex;
	margin-top: 5px;
}

.plan-tag {
	background-color: #fff7e6;
	color: #fa8c16;
	padding: 2px 8px;
	font-size: 12px;
	border-radius: 4px;
	margin-right: 8px;
}

.plan-sessions {
	color: #1890ff;
	font-size: 14px;
	margin-top: 8px;
	font-weight: 500;
}

/* 添加计划列表的固定高度和平滑滚动样式 */
.plan-list {
	height: calc(100vh - 320px); /* 固定高度，减去其他UI元素高度 */
	flex: 1;
	overflow-y: auto; /* 允许内容垂直滚动 */
	position: relative;
	-webkit-overflow-scrolling: touch; /* iOS设备上的惯性滚动 */
	scroll-behavior: smooth; /* 平滑滚动效果 */
	padding: 5px 3px; /* 添加内边距避免内容紧贴边缘 */
}

/* 修复scroll-view样式 */
:deep(.uni-scroll-view) {
	height: 100% !important;
	width: 100% !important;
}

.plan-list :deep(.uni-scroll-view) {
	height: 100% !important;
	width: 100% !important;
}

.plan-list::-webkit-scrollbar {
	display: none; /* 隐藏滚动条 */
}

.empty-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 30px;
	background-color: #f9f9f9;
	border-radius: 8px;
	margin: 15px 0;
}

.empty-text {
	color: #999;
	font-size: 16px;
}

.start-workout-button {
	background: linear-gradient(135deg, #1890ff, #096dd9);
	color: white;
	text-align: center;
	padding: 15px;
	border-radius: 8px;
	font-size: 16px;
	font-weight: bold;
	margin-top: 20px;
	box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
}

/* 添加按钮样式 */
.add-plan-button {
	background-color: white;
	border-radius: 12px;
	margin-top: 20px;
	margin-bottom: 25px;
	padding: 15px;
	display: flex;
	align-items: center;
	justify-content: center;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
	border: 2px dashed #1890ff;
	transition: all 0.2s ease;
}

.add-plan-button:active {
	transform: scale(0.97);
	background-color: #f0f9ff;
}

.add-icon {
	width: 30px;
	height: 30px;
	border-radius: 15px;
	background-color: #1890ff;
	color: white;
	font-size: 24px;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-right: 10px;
	font-weight: bold;
}

.add-text {
	color: #1890ff;
	font-size: 16px;
	font-weight: 500;
}
</style> 