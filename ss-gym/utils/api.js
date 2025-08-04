/**
 * API接口调用模块
 */

import request from './request.js';

// 认证相关API
const authAPI = {
  // 用户登录
  login(username, password) {
    return request.post('/login', {
      username,
      password
    });
  },
  
  // 用户注册
  register(userData) {
    return request.post('/register', userData);
  },
  
  // 获取当前用户信息
  getCurrentUser() {
    return request.get('/auth/me');
  }
};

// 训练相关API
const trainingAPI = {
  // 获取按月训练记录
  getMonthlyTrainings(month) {
    return request.get(`/training/getTrainingAction/${month}`);
  },
  getTodyTrainingList(month) {
    return request.get(`/training/getTodyTrainingList/${month}`);
  },
  
  // 获取训练详情
  getTrainingById(trainingId) {
    return request.get(`/training/getTrainingById/${trainingId}`);
  },


  saveTraining(training) {
    return request.post('/training/saveTraining', training);  
  },
  
  // 获取所有训练记录
  getAllTrainings() {
    return request.get('/training/list');
  },
  
  // 创建训练记录
  createTraining(trainingData) {
    return request.post('/training/add', trainingData);
  },
  
  // 更新训练记录
  updateTraining(trainingData) {
    return request.put('/training/update', trainingData);
  },
  
  // 删除训练记录
  deleteTraining(trainingId) {
    return request.delete(`/training/delete/${trainingId}`);
  },
  
  // 完成训练组
  completeTrainingSet(setId, data) {
    return request.put(`/trainings/sets/${setId}/complete`, data);
  }
};

// 系统健康检查
const systemAPI = {
  healthCheck() {
    return request.get('/health');
  }
};

// 向后兼容性支持 - 保留旧的导出名称
const userApi = authAPI;
const trainingApi = trainingAPI;

// 导出新版API名称
export {
  authAPI,
  trainingAPI,
  systemAPI
};

// 同时导出旧版API名称，保持兼容性
export {
  userApi,
  trainingApi
}; 