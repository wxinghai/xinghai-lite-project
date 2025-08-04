<template>
  <view class="profile-container">
   
    <view  class="user-profile">
      <!-- 用户信息头部 -->
      <view class="user-header">
        <view class="avatar-container">
          <image class="user-avatar" src="/static/logo.png" mode="aspectFill"></image>
          <view class="gender-icon">
            <text class="icon">♂</text>
          </view>
        </view>
        <view class="user-basic-info">
          <view class="username-pro" @click="showLoginModal">
            <text class="username">{{userInfo.username || '未登录'}}</text>
            <view class="pro-tag" v-if="isLoggedIn">
              <text>永久PRO</text>
            </view>
          </view>
        </view>
        
      </view>

      <!-- 功能模块区一 -->
      <view class="feature-section">
        <view class="feature-item" @click="navigateTo('/pages/feature/period-record')">
          <view class="feature-icon pink">
            <text class="icon">💧</text>
          </view>
          <view class="feature-name">经期记录</view>
          
        </view>

        <view class="feature-item" @click="navigateTo('/pages/feature/photo-wall')">
          <view class="feature-icon blue">
            <text class="icon">📷</text>
          </view>
          <view class="feature-name">照片墙</view>
          
        </view>

        <view class="feature-item none-border" @click="navigateTo('/pages/feature/training-notes')">
          <view class="feature-icon blue-dark">
            <text class="icon">📝</text>
          </view>
          <view class="feature-name">训记备忘录</view>
          
        </view>
      </view>

      <!-- 功能模块区二 -->
      <view class="feature-section">
        <view class="feature-item" @click="navigateTo('/pages/feature/share-training')">
          <view class="feature-icon blue-light">
            <text class="icon">↗️</text>
          </view>
          <view class="feature-name">分享训记</view>
          
        </view>

        <view class="feature-item" @click="navigateTo('/pages/feature/settings')">
          <view class="feature-icon blue">
            <text class="icon">⚙️</text>
          </view>
          <view class="feature-name">偏好设置</view>
          
        </view>

        <view class="feature-item" @click="navigateTo('/pages/feature/feedback')">
          <view class="feature-icon blue-smile">
            <text class="icon">😊</text>
          </view>
          <view class="feature-name">反馈中心</view>
          
        </view>

        <view class="feature-item none-border" @click="navigateTo('/pages/feature/body-data')">
          <view class="feature-icon blue">
            <text class="icon">👤</text>
          </view>
          <view class="feature-name">身体数据</view>
          
        </view>
      </view>

      <!-- 功能模块区三 -->
      <view class="feature-section">
        <view class="feature-item none-border" @click="navigateTo('/pages/feature/coach-notes')">
          <view class="feature-icon green">
            <text class="icon">🏋️</text>
          </view>
          <view class="feature-name">教练记</view>
          
        </view>
      </view>

      <!-- 功能模块区四 -->
      <view class="feature-section">
        <view class="feature-item" @click="navigateTo('/pages/feature/tutorial')">
          <view class="feature-icon green-dark">
            <text class="icon">📖</text>
          </view>
          <view class="feature-name">操作指南</view>
          
        </view>

        <view class="feature-item" @click="navigateTo('/pages/feature/language')">
          <view class="feature-icon purple">
            <text class="icon">🌐</text>
          </view>
          <view class="feature-name">语言设置 / Language</view>
          <text class="beta-tag">Beta</text>
          
        </view>

        <view class="feature-item none-border" @click="navigateTo('/pages/feature/rm-calculator')">
          <view class="feature-icon brown">
            <text class="icon">🧮</text>
          </view>
          <view class="feature-name">RM计算器</view>
          
        </view>
      </view>

      <!-- 退出登录按钮 - 隐藏在更多菜单中 -->
      <button class="logout-btn-hidden" v-if="isLoggedIn" @click="handleLogout">退出登录</button>
    </view>
    
    <!-- 登录/注册弹窗 -->
    <uni-popup ref="loginPopup" type="center">
      <view class="login-popup">
        <!-- 登录/注册切换选项卡 -->
        <view class="tab-container">
          <view class="tab" :class="{ active: activeTab === 'login' }" @click="activeTab = 'login'">登录</view>
          <view class="tab" :class="{ active: activeTab === 'register' }" @click="activeTab = 'register'">注册</view>
        </view>
        
        <!-- 登录表单 -->
        <view v-if="activeTab === 'login'" class="form-container">
          <view class="form-item">
            <text class="label">用户名</text>
            <input type="text" v-model="loginForm.username" placeholder="请输入用户名" />
          </view>
          <view class="form-item">
            <text class="label">密码</text>
            <input type="password" v-model="loginForm.password" placeholder="请输入密码" />
          </view>
          <button class="submit-btn" @click="handleLogin">登录</button>
        </view>
        
        <!-- 注册表单 -->
        <view v-else class="form-container">
          <view class="form-item">
            <text class="label">用户名</text>
            <input type="text" v-model="registerForm.username" placeholder="请输入用户名" />
          </view>
          <view class="form-item">
            <text class="label">邮箱</text>
            <input type="text" v-model="registerForm.email" placeholder="请输入邮箱" />
          </view>
          <view class="form-item">
            <text class="label">姓名</text>
            <input type="text" v-model="registerForm.full_name" placeholder="请输入姓名" />
          </view>
          <view class="form-item">
            <text class="label">密码</text>
            <input type="password" v-model="registerForm.password" placeholder="请输入密码" />
          </view>
          <view class="form-item">
            <text class="label">确认密码</text>
            <input type="password" v-model="confirmPassword" placeholder="请再次输入密码" />
          </view>
          <button class="submit-btn" @click="handleRegister">注册</button>
        </view>
        
        <view class="close-btn">
          <text @click="closeLoginModal">关闭</text>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script>
import { userApi, trainingApi } from '@/utils/api.js';
 
import uniPopup from '@/uni_modules/uni-popup/components/uni-popup/uni-popup.vue';

export default {
  components: {
    uniPopup
  },
  data() {
    return {
      isLoggedIn: false,
      activeTab: 'login', // 'login' 或 'register'
      loginForm: {
        username: '',
        password: ''
      },
      registerForm: {
        username: '',
        email: '',
        full_name: '',
        password: ''
      },
      confirmPassword: '',
      userInfo: {},
      queryMonth: this.getDefaultMonth(),
      trainings: [],
      hasQueried: false,
	  statusBarHeight: 0
    }
  },
  computed: {
    // 按日期分组的训练数据
    trainingsGrouped() {
      const grouped = {};
      this.trainings.forEach(training => {
        const date = training.trainingDate;
        if (!grouped[date]) {
          grouped[date] = [];
        }
        grouped[date].push(training);
      });
      return grouped;
    }
  },
  onShow() {
    this.checkLoginStatus();
  },

  methods: {
    // 默认显示当前月份
    getDefaultMonth() {
      const now = new Date();
      const year = now.getFullYear();
      const month = now.getMonth() + 1;
      return `${year}-${month < 10 ? '0' + month : month}`;
    },
    
    // 检查登录状态
    checkLoginStatus() {
      const token = uni.getStorageSync('ss-gym-token');
      const userInfoStr = uni.getStorageSync('ss-gym-userInfo');
      
      if (token && userInfoStr) {
        try {
          const userInfo = JSON.parse(userInfoStr);
          this.isLoggedIn = true;
          this.userInfo = userInfo;
        } catch (error) {
          console.error('解析用户信息失败:', error);
          uni.removeStorageSync('ss-gym-token');
          uni.removeStorageSync('ss-gym-userInfo');
          this.isLoggedIn = false;
          this.userInfo = {};
        }
      } else {
        this.isLoggedIn = false;
        this.userInfo = {};
      }
    },
    
    // 显示登录弹窗
    showLoginModal() {
      this.$refs.loginPopup.open();
    },
    
    // 关闭登录弹窗
    closeLoginModal() {
      this.$refs.loginPopup.close();
    },
    
    // 处理登录
    async handleLogin() {
      if (!this.loginForm.username || !this.loginForm.password) {
        uni.showToast({
          title: '用户名和密码不能为空',
          icon: 'none'
        });
        return;
      }
      
      try {
        uni.showLoading({ title: '登录中...' });
        
        // 调用登录接口
        const response = await userApi.login(this.loginForm.username, this.loginForm.password);
        console.log('登录响应:', response);
        
        // 登录成功，保存token和用户信息
        if (response && response.data && response.data.access_token) {
          console.log("token", response);
          uni.setStorageSync('ss-gym-token', response.data.access_token);
          
          // 获取用户信息
          if (response.data.user) {
            const userInfo = response.data.user;
            uni.setStorageSync('ss-gym-userInfo', JSON.stringify(userInfo));
            this.isLoggedIn = true;
            this.userInfo = userInfo;
          }
          
          // 关闭弹窗
          this.closeLoginModal();
          
          uni.showToast({
            title: '登录成功',
            icon: 'success'
          });
        } else {
          throw new Error('登录失败: 返回数据格式不正确');
        }
      } catch (error) {
        console.error('登录失败:', error);
        uni.showToast({
          title: '登录失败: ' + (error.message || '请检查用户名和密码'),
          icon: 'none'
        });
      } finally {
        uni.hideLoading();
      }
    },
    
    // 处理注册
    async handleRegister() {
      // 表单验证
      if (!this.registerForm.username || !this.registerForm.email || 
          !this.registerForm.password || !this.confirmPassword) {
        uni.showToast({
          title: '请填写所有必填项',
          icon: 'none'
        });
        return;
      }
      
      if (this.registerForm.password !== this.confirmPassword) {
        uni.showToast({
          title: '两次密码输入不一致',
          icon: 'none'
        });
        return;
      }
      
      try {
        uni.showLoading({ title: '注册中...' });
        
        // 转换注册请求，适配后端接口
        const registerData = {
          username: this.registerForm.username,
          email: this.registerForm.email,
          hashedPassword: this.registerForm.password, // 后端会加密处理
          fullName: this.registerForm.full_name || ''
        };
        
        // 调用注册接口
        const response = await userApi.register(registerData);
        console.log('注册响应:', response);
        
        // 注册成功，自动登录
        if (response && response.data && response.data.access_token) {
          uni.showToast({
            title: '注册成功，正在登录...',
            icon: 'success'
          });
          
          // 保存token和用户信息
          uni.setStorageSync('ss-gym-token', response.data.access_token);
          
          if (response.data.user) {
            const userInfo = response.data.user;
            uni.setStorageSync('ss-gym-userInfo', JSON.stringify(userInfo));
            this.isLoggedIn = true;
            this.userInfo = userInfo;
          }
          
          // 关闭弹窗
          this.closeLoginModal();
        } else {
          throw new Error('注册失败: 返回数据格式不正确');
        }
      } catch (error) {
        console.error('注册失败:', error);
        uni.showToast({
          title: '注册失败: ' + (error.message || '请检查注册信息'),
          icon: 'none'
        });
      } finally {
        uni.hideLoading();
      }
    },
    
    // 处理退出登录
    handleLogout() {
      uni.removeStorageSync('ss-gym-token');
      uni.removeStorageSync('ss-gym-userInfo');
      this.isLoggedIn = false;
      this.userInfo = {};
      this.trainings = [];
      this.hasQueried = false;
      
      uni.showToast({
        title: '已退出登录',
        icon: 'success'
      });
    },
    
    // 月份选择变化
    onMonthChange(e) {
      this.queryMonth = e.detail.value;
    },
    
    // 查询训练记录
    async queryTrainings() {
      if (!this.isLoggedIn) {
        uni.showToast({
          title: '请先登录',
          icon: 'none'
        });
        return;
      }
      
      try {
        uni.showLoading({ title: '查询中...' });
        
        // 调用训练记录查询接口
        // 确保月份格式正确
        const monthStr = String(this.queryMonth);
        console.log('查询参数:', { month: monthStr });
        
        const response = await trainingApi.getMonthlyTrainings(monthStr);
        console.log('查询结果:', response);
        
        // 处理返回的数据
        let allTrainings = [];
        for (const date in response) {
          if (response[date] && response[date].length > 0) {
            allTrainings = [...allTrainings, ...response[date]];
          }
        }
        
        this.trainings = allTrainings;
        this.hasQueried = true;
        
        if (allTrainings.length === 0) {
          uni.showToast({
            title: '该月份没有训练记录',
            icon: 'none'
          });
        }
      } catch (error) {
        console.error('查询训练记录失败:', error);
        uni.showToast({
          title: '查询失败: ' + (error.message || '请稍后再试'),
          icon: 'none'
        });
      } finally {
        uni.hideLoading();
      }
    },
    
    // 获取动作的总组数
    getTotalSets(action) {
      return action.sets ? action.sets.length : 0;
    },

   

    // 导航到指定页面
    navigateTo(url) {
      try {
        if (url.includes('feature')) {
          // 功能尚未实现的提示
          uni.showToast({
            title: '功能开发中',
            icon: 'none',
            duration: 2000
          });
          console.log('尝试导航到:', url);
        } else {
          uni.navigateTo({
            url: url,
            fail: (err) => {
              console.error('导航失败:', err);
              uni.showToast({
                title: '页面跳转失败',
                icon: 'none'
              });
            }
          });
        }
      } catch (error) {
        console.error('导航异常:', error);
      }
    }
  }
}
</script>

<style>
.profile-container {
  padding: 15px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

/* 未登录状态样式 */
.no-login-tips {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
}

.no-login-tips text {
  font-size: 16px;
  color: #666;
  margin-bottom: 20px;
}

.login-btn {
  width: 200px;
  background-color: #007aff;
  color: #fff;
}

/* 登录弹窗样式 */
.login-popup {
  width: 300px;
  background-color: #fff;
  border-radius: 15px;
  padding: 25px;
  position: relative;
}

.close-btn {
  position: absolute;
  top: 15px;
  right: 15px;
  color: #999;
  font-size: 14px;
}

/* 选项卡样式 */
.tab-container {
  display: flex;
  border-bottom: 1px solid #eee;
  margin-bottom: 20px;
}

.tab {
  flex: 1;
  text-align: center;
  padding: 15px 0;
  font-size: 16px;
  color: #666;
  position: relative;
}

.tab.active {
  color: #007aff;
  font-weight: bold;
}

.tab.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 25%;
  width: 50%;
  height: 2px;
  background-color: #007aff;
}

/* 表单样式 */
.form-container {
  padding: 10px 0;
}

.form-item {
  margin-bottom: 15px;
}

.label {
  display: block;
  margin-bottom: 5px;
  font-size: 14px;
  color: #666;
}

input {
  width: 100%;
  height: 40px;
  border: 1px solid #ddd;
  border-radius: 5px;
  padding: 0 10px;
}

.submit-btn {
  margin-top: 20px;
  background-color: #007aff;
  color: #fff;
}

/* 用户头部样式 */
.user-header {
  display: flex;
  padding: 22px 18px;
  background-color: #fff;
  margin-bottom: 15px;
  border-radius: 12px;
  position: relative;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.avatar-container {
  position: relative;
  margin-right: 18px;
}

.user-avatar {
  width: 70px;
  height: 70px;
  border-radius: 35px;
  border: 2px solid #fff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
}

.gender-icon {
  position: absolute;
  bottom: 0;
  right: 0;
  background-color: #007aff;
  border-radius: 12px;
  width: 24px;
  height: 24px;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
}

.gender-icon .icon {
  color: #fff;
  font-size: 16px;
}

.user-basic-info {
  flex: 1;
  padding-top: 12px;
}

.username-pro {
  display: flex;
  align-items: center;
}

.username {
  font-size: 22px;
  font-weight: bold;
  color: #333;
}

.pro-tag {
  background: linear-gradient(to right, #ffd700, #ff8c00);
  color: #fff;
  border-radius: 15px;
  padding: 3px 10px;
  margin-left: 10px;
  font-size: 12px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.15);
}

.user-id {
  font-size: 14px;
  color: #666;
  margin-top: 8px;
}

.edit-info-btn {
  position: absolute;
  top: 22px;
  right: 18px;
  color: #999;
  font-size: 14px;
}

.edit-info-btn .arrow {
  margin-left: 5px;
}

/* 功能模块区样式 */
.feature-section {
  background-color: #fff;
  border-radius: 12px;
  padding: 8px 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  margin-bottom: 15px;
  overflow: hidden;
}

.feature-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  position: relative;
  transition: background-color 0.2s;
}
.none-border{
  border-bottom: none;
}

.feature-item:active {
  background-color: rgba(0, 0, 0, 0.03);
  transform: scale(0.98);
}

/* 功能图标样式 */
.feature-icon {
  width: 40px;
  height: 40px;
  border-radius: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 15px;
  background-color: #f0f5ff;  /* 统一使用淡蓝色背景，类似HomePage.vue */
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
}

.icon {
  font-size: 22px;
}

/* 移除所有不同颜色的背景渐变，使用统一背景 */
.feature-icon.pink,
.feature-icon.blue,
.feature-icon.blue-dark,
.feature-icon.blue-light,
.feature-icon.blue-smile,
.feature-icon.green,
.feature-icon.green-dark,
.feature-icon.purple,
.feature-icon.brown {
  background-color: #f0f5ff;
  color: #333;
}

.feature-name {
  flex: 1;
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

/* 箭头指示器 */
.arrow {
  font-size: 16px;
  color: #bbb;
  font-weight: normal;
}

.beta-tag {
  background: linear-gradient(135deg, #ff4500, #ff6347);
  color: #fff;
  border-radius: 10px;
  padding: 2px 8px;
  font-size: 12px;
  font-weight: normal;
  margin-left: 10px;
  box-shadow: 0 2px 3px rgba(0, 0, 0, 0.1);
}

/* 训练记录查询样式 */
.training-section {
  background-color: #fff;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 15px;
}

.query-form {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.picker-view {
  flex: 1;
  height: 40px;
  line-height: 40px;
  padding: 0 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  margin-right: 10px;
  display: flex;
  align-items: center;
}

.query-btn {
  width: 80px;
  height: 40px;
  line-height: 40px;
  font-size: 14px;
  margin: 0;
}

/* 训练记录列表样式 */
.training-list {
  margin-top: 15px;
}

.training-day {
  margin-bottom: 15px;
}

.date-header {
  font-size: 16px;
  font-weight: bold;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.training-item {
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
}

.training-name {
  font-size: 16px;
  font-weight: bold;
}

.training-volume,
.training-range {
  font-size: 14px;
  color: #666;
  margin-top: 5px;
}

.training-actions {
  margin-top: 10px;
}

.actions-title {
  font-size: 14px;
  margin-bottom: 5px;
}

.action-item {
  font-size: 14px;
  color: #666;
  margin-left: 10px;
  margin-bottom: 3px;
}

.no-data {
  text-align: center;
  padding: 20px;
  color: #999;
}

/* 隐藏的退出登录按钮 */
.logout-btn-hidden {
  width: 100%;
  height: 44px;
  line-height: 44px;
  background-color: #fff;
  color: #ff3b30;
  font-size: 16px;
  border-radius: 12px;
  margin-top: 20px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}
</style> 