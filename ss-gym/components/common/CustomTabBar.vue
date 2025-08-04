<template>
	<view class="tab-bar" :style="{ paddingBottom: safeAreaBottom + 'px' }">
		<view 
			class="tab-item" 
			v-for="(tab, index) in tabs" 
			:key="index"
			@click="handleTabClick(tab.page)"
			:class="{ active: currentTab === tab.page }"
		>
			<image 
				:src="currentTab === tab.page ? tab.selectedIcon : tab.icon" 
				mode="aspectFit" 
				class="tab-icon"
			></image>
			<text class="tab-text">{{ tab.text }}</text>
		</view>
	</view>
</template>

<script>
export default {
	props: {
		// 当前激活的tab页面
		currentTab: {
			type: String,
			default: 'home'
		},
		// 是否隐藏tab bar
		hidden: {
			type: Boolean,
			default: false
		}
	},
	data() {
		return {
			safeAreaBottom: 0,
			tabs: [
				{
					text: '训练',
					page: 'home',
					icon: '/static/tabbar/xunlian-unselect.png',
					selectedIcon: '/static/tabbar/xunlian-select.png'
				},
				{
					text: '动作',
					page: 'actions',
					icon: '/static/tabbar/dongzuo-unselect.png',
					selectedIcon: '/static/tabbar/dongzuo-select.png'
				},
				{
					text: '历史',
					page: 'history',
					icon: '/static/tabbar/lishi-unselect.png',
					selectedIcon: '/static/tabbar/lishi-select.png'
				},
				{
					text: '我的',
					page: 'profile',
					icon: '/static/tabbar/wode-unselect.png',
					selectedIcon: '/static/tabbar/wode-select.png'
				}
			]
		};
	},
	created() {
		try {
			const systemInfo = uni.getSystemInfoSync();
			if (systemInfo.safeAreaInsets) {
				this.safeAreaBottom = systemInfo.safeAreaInsets.bottom;
			}
		} catch (e) {
			console.error('获取系统安全区域高度失败:', e);
		}
	},
	methods: {
		// 处理标签点击
		handleTabClick(page) {
			// 如果点击当前页面，不做操作
			if (page === this.currentTab) return;
			
			// 发送点击事件给父组件
			this.$emit('tab-click', page);
		}
	}
};
</script>

<style>
.tab-bar {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	height: 50px;
	background-color: #ffffff;
	display: flex;
	border-top: 1px solid #f0f0f0;
	z-index: 999;
}

.tab-item {
	flex: 1;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
}

.tab-icon {
	width: 24px;
	height: 24px;
	margin-bottom: 2px;
}

.tab-text {
	font-size: 12px;
	color: #999;
}

.tab-item.active .tab-text {
	color: #1890ff;
}
</style> 