<template>
	<view class="container">
		<!-- 状态栏占位 -->
		<view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
		
		<!-- 导航栏 -->
		<view class="nav-bar">
			<view v-if="!isSelectionMode" class="search-bar">
				<view class="search-icon">🔍</view>
				<input class="search-input" type="text" placeholder="搜索动作" />
				<view class="add-button">+</view>
			</view>
			<view v-else class="selection-header">
				<view class="back-button" @click="cancelSelection">←</view>
				<view class="selection-title">已选择 {{ selectedActions.length }} 个动作</view>
				<view class="selection-actions">
					<view class="confirm-btn" @click="confirmSelection">完成</view>
				</view>
			</view>
		</view>

		<!-- 两栏布局主容器 -->
		<view class="two-column-layout">
			<!-- 左侧分类栏 - 只保留训练部位 -->
			<view class="left-column">
				<!-- 身体部位分类 -->
				<view class="category-section">
					<view class="category-title">部位</view>
					<view class="category-list">
						<view 
							v-for="(part, index) in bodyParts" 
							:key="index"
							:class="['category-item', activeBodyPart === part ? 'active' : '']"
							@click="switchBodyPart(part)"
						>{{ part }}</view>
					</view>
				</view>
			</view>
			
			<!-- 右侧内容区 -->
			<view class="right-column">
				<!-- 顶部筛选区 - 移动器械类型到这里 -->
				<view class="filter-bar">
					<scroll-view scroll-x class="equipment-scroll" :show-scrollbar="false">
						<view 
							v-for="(type, index) in equipmentTypes"
							:key="index"
							:class="['equipment-item', activeEquipment === type ? 'active' : '']"
							@click="switchEquipment(type)"
						>{{ type }}</view>
					</scroll-view>
				</view>
				
				<!-- 当前分类标题 -->
				<view class="section-header">
					<text class="primary-title">{{ currentSectionTitle }}</text>
				</view>
				
				<!-- 动作列表 -->
				<scroll-view class="action-scroll" scroll-y :style="{ paddingBottom: isSelectionMode ? '60px' : '10px' }">
					<!-- 按当前条件筛选的动作 -->
					<view class="actions-grid">
						<view 
							v-for="(action, index) in currentActions" 
							:key="action.id"
							:class="['action-card', isActionSelected(action) ? 'action-selected' : '']"
							@click="isSelectionMode ? toggleSelectAction(action) : viewActionDetail(action)"
						>
							<image class="action-image action-gif" :src="action.image" mode="aspectFill"></image>
							<view class="tutorial animation-tag">动画演示</view>
							<view class="action-title">{{ action.name }}</view>
							<view class="action-times">{{ formatTimes(action.times) }}</view>
							<!-- 选中标记 -->
							<view v-if="isSelectionMode" class="selection-checkbox" :class="{ 'checkbox-selected': isActionSelected(action) }">
								<text v-if="isActionSelected(action)" class="checkbox-mark">✓</text>
							</view>
						</view>
					</view>
					
					<!-- 空状态提示 -->
					<view class="empty-state" v-if="currentActions.length === 0">
						<text>暂无符合条件的训练动作</text>
					</view>
				</scroll-view>
			</view>
		</view>
		
		<!-- 底部操作栏 - 仅在选择模式显示 -->
		<view class="bottom-action-bar" v-if="isSelectionMode">
			<view class="selected-count">已选择: {{ selectedActions.length }}</view>
			<view class="confirm-selection" @click="confirmSelection">确认添加</view>
		</view>
	</view>
</template>

<script>
export default {
	props: {
		// 是否处于选择模式
		selectionMode: {
			type: Boolean,
			default: false
		}
	},
	data() {
		return {
			statusBarHeight: 20,
			activeBodyPart: '全部', // 当前选中的身体部位
			activeEquipment: '全部', // 当前选中的器械类型
			isSelectionMode: false, // 是否处于选择模式
			selectedActions: [], // 已选择的动作
			// 身体部位数据
			bodyParts: ['全部', '胸部', '背部', '肩部', '手臂', '腿部', '核心'],
			// 器械类型（增加"全部"选项）
			equipmentTypes: ['全部', '杠铃', '哑铃', '绳索', '器械', '自重'],
			// 动作数据，按照训练部位和类型组织
			actions: [
				{
					id: 'bench_press',
					name: '杠铃卧推',
					image: '/static/actions/杠铃卧推.gif',
					type: '杠铃',
					bodyPart: '胸部',
					times: 3568
				},
				{
					id: 'incline_dumbbell_press',
					name: '上斜哑铃卧推',
					image: '/static/actions/上斜哑铃卧推.gif',
					type: '哑铃',
					bodyPart: '胸部',
					times: 1298
				},
				{
					id: 'squat',
					name: '杠铃深蹲',
					image: '/static/actions/杠铃深蹲.gif',
					type: '杠铃',
					bodyPart: '腿部',
					times: 3021
				},
				{
					id: 'deadlift',
					name: '杠铃硬拉',
					image: '/static/actions/杠铃硬拉.gif',
					type: '杠铃',
					bodyPart: '背部',
					secondaryBodyPart: '腿部', // 次要训练部位
					times: 2876
				},
				{
					id: 'barbell_row',
					name: '杠铃划船',
					image: '/static/actions/杠铃划船.gif',
					type: '杠铃',
					bodyPart: '背部',
					times: 2187
				},
				{
					id: 'dumbbell_row',
					name: '哑铃划船',
					image: '/static/actions/哑铃划船.gif',
					type: '哑铃',
					bodyPart: '背部',
					times: 1863
				},
				{
					id: 'narrow_grip_pulldown',
					name: '窄握前拉',
					image: '/static/actions/窄握前拉.gif',
					type: '绳索',
					bodyPart: '背部',
					times: 1725
				},
				{
					id: 't_bar_row',
					name: 'T杆划船',
					image: '/static/actions/T杆划船.gif',
					type: '器械',
					bodyPart: '背部',
					times: 1613
				},
				{
					id: 'leg_curl',
					name: '腿弯举',
					image: '/static/actions/腿弯举.gif',
					type: '器械',
					bodyPart: '腿部',
					times: 1845
				},
				{
					id: 'rope_donkey_kick',
					name: '绳索驴踢',
					image: '/static/actions/绳索驴踢.gif',
					type: '绳索',
					bodyPart: '腿部',
					times: 1532
				}
			]
		}
	},
	created() {
		this.statusBarHeight = getApp().getStatusBarHeight();
		// 初始化选择模式
		this.isSelectionMode = this.selectionMode;
		
 
	},
 
	computed: {
		// 当前部分标题
		currentSectionTitle() {
			let title = '';
			
			if (this.activeBodyPart !== '全部') {
				title += this.activeBodyPart;
			}
			
			if (this.activeEquipment !== '全部') {
				title += (title ? ' - ' : '') + this.activeEquipment;
			}
			
			return title || '所有训练动作';
		},
		
		// 当前显示的动作列表
		currentActions() {
			let result = this.actions;
			
			// 按身体部位筛选
			if (this.activeBodyPart !== '全部') {
				result = result.filter(action => 
					action.bodyPart === this.activeBodyPart || 
					action.secondaryBodyPart === this.activeBodyPart
				);
			}
			
			// 按器械类型筛选
			if (this.activeEquipment !== '全部') {
				result = result.filter(action => action.type === this.activeEquipment);
			}
			
			return result;
		}
	},
	methods: {
		// 切换身体部位
		switchBodyPart(bodyPart) {
			this.activeBodyPart = bodyPart;
		},
		// 切换器械类型
		switchEquipment(type) {
			this.activeEquipment = type;
		},
		// 格式化访问次数
		formatTimes(times) {
			return times.toString() + '次';
		},
		
		// 检查动作是否被选中
		isActionSelected(action) {
			return this.selectedActions.some(item => item.id === action.id);
		},
		
		// 切换动作选中状态
		toggleSelectAction(action) {
			const index = this.selectedActions.findIndex(item => item.id === action.id);
			if (index >= 0) {
				// 如果已选中，则取消选中
				this.selectedActions.splice(index, 1);
			} else {
				// 如果未选中，则添加到选中列表
				this.selectedActions.push(action);
			}
		},
		
	 
		
		// 确认选择并返回
		confirmSelection() {
			// 确保selectedActions是数组
			if (!Array.isArray(this.selectedActions)) {
				console.error('选中的动作不是数组:', this.selectedActions);
				uni.showToast({
					title: '数据格式错误',
					icon: 'none'
				});
				return;
			}
			
			// 向父组件发送选中的动作
			this.$emit('actions-selected', this.selectedActions);
			
			// 发送全局事件，以便index.vue处理
			// 直接发送数组，不要包装在对象中
			uni.$emit('actions-selected', this.selectedActions);
			
			console.log('已选择动作:', this.selectedActions.length);
		},
		
		// 取消选择
		cancelSelection() {
			// 发送取消事件
			this.$emit('selection-canceled');
			
			// 发送全局事件
			uni.$emit('actions-selection-canceled');
		},
		
		// 处理返回按钮点击
		handleBackClick() {
			// 如果处于选择模式，则取消选择
			if (this.isSelectionMode) {
				this.cancelSelection();
			} else {
				// 正常返回上一页
				uni.$emit('navigate-back');
			}
		},
		
		// 查看动作详情
		viewActionDetail(action) {
			// 如果在选择模式，则切换选中状态
			if (this.isSelectionMode) {
				this.toggleSelectAction(action);
				return;
			}
			
			// 正常查看动作详情逻辑
			console.log('查看动作详情:', action.name);
			uni.showToast({
				title: '查看动作: ' + action.name,
				icon: 'none'
			});
		},
	 
	}
}
</script>

<style>
.container {
	display: flex;
	flex-direction: column;
	height: 100vh;
	background-color: #f5f5f5;
	position: relative;
}

.status-bar {
	background-color: #fff;
	width: 100%;
}

.nav-bar {
	background-color: #fff;
	padding: 10px 15px;
	box-shadow: 0 1px 2px rgba(0,0,0,0.05);
	z-index: 100;
}

.search-bar {
	display: flex;
	align-items: center;
	background-color: #f0f0f0;
	border-radius: 20px;
	padding: 6px 12px;
}

.back-button {
	width: 24px;
	height: 24px;
	display: flex;
	align-items: center;
	justify-content: center;
	background-color: #f0f0f0;
	color: #666;
	border-radius: 50%;
	font-size: 16px;
	margin-right: 8px;
	cursor: pointer;
}

.back-button:active {
	background-color: #e0e0e0;
	transform: scale(0.95);
}

.search-icon {
	margin-right: 8px;
	color: #999;
}

.search-input {
	flex: 1;
	border: none;
	background: transparent;
	font-size: 14px;
	color: #333;
}

.add-button {
	width: 24px;
	height: 24px;
	display: flex;
	align-items: center;
	justify-content: center;
	background-color: #1890ff;
	color: white;
	border-radius: 50%;
	font-size: 16px;
	margin-left: 10px;
}

/* 两栏布局样式 */
.two-column-layout {
	display: flex;
	flex: 1;
	overflow: hidden;
	height: calc(100% - 50px); /* 减去导航栏高度 */
	position: relative;
}

/* 左侧栏样式 - 减少宽度 */
.left-column {
	width: 70px;
	background-color: #fff;
	border-right: 1px solid #eee;
	overflow-y: auto;
	padding: 5px 0;
	height: 100%;
	-webkit-overflow-scrolling: touch;
}

.category-section {
	margin-bottom: 15px;
}

.category-title {
	padding: 4px 8px;
	font-size: 13px;
	font-weight: bold;
	color: #333;
	text-align: center;
}

.category-list {
	margin-top: 5px;
}

.category-item {
	padding: 8px 4px;
	font-size: 13px;
	color: #666;
	position: relative;
	text-align: center;
	line-height: 1.2;
	word-break: break-all;
	display: flex;
	align-items: center;
	justify-content: center;
	min-height: 36px;
}

.category-item.active {
	background-color: #e6f7ff;
	color: #1890ff;
	font-weight: 500;
	border-right: 2px solid #1890ff;
}

/* 右侧内容区样式 */
.right-column {
	flex: 1;
	display: flex;
	flex-direction: column;
	overflow: hidden;
	height: 100%;
}

/* 器械筛选栏 */
.filter-bar {
	background-color: #fff;
	padding: 10px 0;
	border-bottom: 1px solid #eee;
}

.equipment-scroll {
	white-space: nowrap;
	padding: 0 10px;
}

.equipment-item {
	display: inline-block;
	padding: 5px 12px;
	margin-right: 8px;
	font-size: 12px;
	color: #666;
	background-color: #f5f5f5;
	border-radius: 15px;
}

.equipment-item.active {
	background-color: #1890ff;
	color: white;
}

.section-header {
	padding: 10px 15px;
	background-color: #fff;
	border-bottom: 1px solid #eee;
}

.primary-title {
	font-size: 16px;
	font-weight: bold;
	color: #333;
}

.action-scroll {
	flex: 1;
	height: auto;
	overflow-y: auto;
	-webkit-overflow-scrolling: touch;
	padding: 10px;
	box-sizing: border-box;
	width: 100%;
}

.actions-grid {
	display: flex;
	flex-wrap: wrap;
	margin: 0 -5px;
	width: 100%;
	box-sizing: border-box;
}

.action-card {
	width: calc(50% - 10px);
	margin: 0 5px 15px;
	background-color: white;
	border-radius: 8px;
	overflow: hidden;
	box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
	position: relative;
	display: flex;
	flex-direction: column;
	box-sizing: border-box;
}

.action-image {
	width: 100%;
	height: 140px;
	background-color: #f0f0f0;
	object-fit: cover;
}

.action-gif {
	border-radius: 8px 8px 0 0;
	box-shadow: inset 0 -4px 6px rgba(0, 0, 0, 0.05);
	transform: translateZ(0);
	will-change: transform;
}

.tutorial {
	position: absolute;
	top: 8px;
	right: 8px;
	background-color: rgba(0, 0, 0, 0.6);
	color: white;
	font-size: 10px;
	padding: 2px 6px;
	border-radius: 10px;
	z-index: 10;
}

.animation-tag {
	background-color: rgba(24, 144, 255, 0.8);
	color: white;
	font-weight: 500;
	padding: 3px 8px;
	font-size: 11px;
	box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
}

.action-title {
	padding: 8px 10px 4px;
	font-size: 14px;
	font-weight: 500;
	color: #333;
}

.action-times {
	padding: 0 10px 8px;
	font-size: 12px;
	color: #999;
}

.empty-state {
	display: flex;
	justify-content: center;
	align-items: center;
	height: 150px;
	color: #999;
	font-size: 14px;
}

/* 选择模式相关样式 */
.selection-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 10px 15px;
	width: 100%;
	box-sizing: border-box;
}

.selection-title {
	font-size: 16px;
	font-weight: 500;
	color: #333;
}

.selection-actions {
	display: flex;
}

.cancel-btn, .confirm-btn {
	padding: 5px 12px;
	font-size: 14px;
	border-radius: 4px;
	margin-left: 10px;
}

.cancel-btn {
	color: #666;
}

.confirm-btn {
	color: #1890ff;
	font-weight: 500;
}

.action-selected {
	border: 2px solid #1890ff;
	box-shadow: 0 2px 10px rgba(24, 144, 255, 0.25);
}

.selection-checkbox {
	position: absolute;
	right: 10px;
	bottom: 10px;
	width: 22px;
	height: 22px;
	border-radius: 50%;
	border: 1px solid #ddd;
	background-color: white;
	display: flex;
	align-items: center;
	justify-content: center;
	z-index: 5;
}

.checkbox-selected {
	background-color: #1890ff;
	border-color: #1890ff;
}

.checkbox-mark {
	color: white;
	font-size: 12px;
	font-weight: bold;
}

.bottom-action-bar {
	position: absolute; /* 改为绝对定位 */
	bottom: 0;
	left: 0;
	right: 0;
	height: 50px;
	background-color: white;
	box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 0 15px;
	z-index: 100;
}

.selected-count {
	font-size: 14px;
	color: #666;
}

.confirm-selection {
	background-color: #1890ff;
	color: white;
	padding: 6px 20px;
	border-radius: 20px;
	font-size: 14px;
	font-weight: 500;
}
</style> 