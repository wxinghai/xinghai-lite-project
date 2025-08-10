<script>
	export default {
		globalData:{
		  statusBarHeight:''
		},
		onLaunch() {
			const { statusBarHeight = 0} = uni.getSystemInfoSync()
			this.globalData.statusBarHeight =  statusBarHeight+"px"
		},
		onShow: function() {
			console.log('App Show')
		},
		onHide: function() {
			console.log('App Hide')
		}
	}
</script>

<style>
	/*每个页面公共css */
</style>