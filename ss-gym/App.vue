<script>
	export default {
		globalData: {
			statusBarHeight: 0 // 状态栏高度，初始值为0
		},
		onLaunch: function() {
			console.log('App Launch');
			// 获取系统信息，包含状态栏高度等
			const systemInfo = uni.getSystemInfoSync();
            // 将状态栏高度存储到全局变量中
			this.globalData.statusBarHeight = systemInfo.statusBarHeight;
		},
		onShow: function() {
			console.log('App Show');
		},
		onHide: function() {
			console.log('App Hide');
		},
		methods: {
			// 应用级别方法
			getStatusBarHeight() {
				return this.globalData.statusBarHeight;
			}
		}
	}
</script>

<style>
	/* 全局样式 */
	page {
		--status-bar-height: var(--window-top);
		--safe-area-inset-bottom: env(safe-area-inset-bottom);
		font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
		color: #333;
		font-size: 14px;
		line-height: 1.4;
		background-color: #f5f5f5;
		height: 100%;
	}

	/* 禁用橡皮筋效果 */
	page, view, scroll-view {
		overscroll-behavior: none;
		-webkit-overscroll-behavior: none;
		touch-action: pan-y;
	}

	/* 修复滚动行为 */
	scroll-view {
		overflow-x: hidden;
		-webkit-overflow-scrolling: touch;
	}

	/* 全局可见文本修复 */
	text, view {
		visibility: visible !important;
		opacity: 1 !important;
	}
	
	/* 训练组件共享样式 */
	.visible-text {
		visibility: visible !important;
		opacity: 1 !important;
		color: inherit !important;
	}
	
	/* 解决文字不显示问题 */
	text {
		color: inherit;
	}
	
	/* 确保输入框文字可见 */
	input {
		color: #333;
	}
	
	/* uni-app中常见文本元素 */
	.uni-input-placeholder, 
	.uni-textarea-placeholder {
		color: #999;
	}
	
	/* 强制可见性 */
	view text {
		opacity: 1 !important;
	}
	
	.page-container {
		padding-top: var(--status-bar-height);
	}
</style>
