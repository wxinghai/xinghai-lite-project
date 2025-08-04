 
const BASE_URL = 'http://localhost:8080/api/v1';

// 请求方法封装
const request = (url, options = {}) => {
  return new Promise((resolve, reject) => {
    // 获取存储的token
    const token = uni.getStorageSync('ss-gym-token') || '';
    console.log("发送请求时的token:", token);
    
    // 处理特殊的内容类型，如表单提交
    let header = {
      'Content-Type': 'application/json',
    };
    
    // 如果是表单提交
    if (options.isForm) {
      header['Content-Type'] = 'application/x-www-form-urlencoded';
    }
    
    // 添加token - 确保token添加了Bearer前缀
    if (token) {
      header['Authorization'] = `Bearer ${token}`;
      console.log("设置的Authorization请求头:", header['Authorization']);
    }
    
    // 默认配置
    const defaultOptions = {
      url: BASE_URL + url,
      header: header,
      success: (res) => {
        // 请求成功
        if (res.statusCode >= 200 && res.statusCode < 300) {
          // 检查是否有业务逻辑错误（success: false）
          if (res.data && res.data.success === false) {
            // 业务逻辑错误，但HTTP请求成功
            const errorMsg = res.data.message || res.data.error || res.data.errors || '操作失败';
            uni.showToast({
              title: errorMsg,
              icon: 'none'
            });
            reject(new Error(errorMsg));
          } else {
            // 正常返回数据
            resolve(res.data);
          }
        } else if (res.statusCode === 401) {
          // 未授权，清除token并跳转到登录页
          uni.removeStorageSync('ss-gym-token');
          uni.removeStorageSync('ss-gym-userInfo');
          uni.showToast({
            title: '登录已过期，请重新登录',
            icon: 'none'
          });
          
          // 跳转到登录页
          setTimeout(() => {
            uni.navigateTo({
              url: '/pages/profile/profile'
            });
          }, 1500);
          
          reject(new Error('未授权，请重新登录'));
        } else {
          // 其他HTTP错误
          const errorMsg = 
            (res.data && (res.data.message || res.data.error || res.data.detail)) || 
            '请求失败';
            
          uni.showToast({
            title: errorMsg,
            icon: 'none'
          });
          reject(new Error(errorMsg));
        }
      },
      fail: (err) => {
        uni.showToast({
          title: '网络请求错误',
          icon: 'none'
        });
        reject(err);
      }
    };
    
    // 合并选项
    const mergedOptions = { ...defaultOptions, ...options };
    
    // 发起请求
    uni.request(mergedOptions);
  });
};

// 将对象转换为表单格式字符串
const objectToFormData = (obj) => {
  let result = '';
  for (let key in obj) {
    result += encodeURIComponent(key) + '=' + encodeURIComponent(obj[key]) + '&';
  }
  return result.slice(0, -1);  // 去掉末尾的&
};

// 处理登录成功的响应
const handleLoginSuccess = (response) => {
  if (response && response.data && response.data.access_token) {
    // 保存token和用户信息
    uni.setStorageSync('ss-gym-token', response.data.access_token);
    if (response.data.user) {
      uni.setStorageSync('ss-gym-userInfo', JSON.stringify(response.data.user));
    }
  }
  return response;
};

// 导出请求方法
export default {
  // GET请求
  get(url, params = {}) {
    return request(url, {
      method: 'GET',
      data: params
    });
  },
  
  // POST请求
  post(url, data = {}, isForm = false) {
    return request(url, {
      method: 'POST',
      data: isForm ? objectToFormData(data) : data,
      isForm: isForm
    }).then(res => {
      // 处理登录请求
      if (url.includes('/login')) {
        return handleLoginSuccess(res);
      }
      return res;
    });
  },
  
  // PUT请求
  put(url, data = {}) {
    return request(url, {
      method: 'PUT',
      data
    });
  },
  
  // DELETE请求
  delete(url) {
    return request(url, {
      method: 'DELETE'
    });
  }
}; 