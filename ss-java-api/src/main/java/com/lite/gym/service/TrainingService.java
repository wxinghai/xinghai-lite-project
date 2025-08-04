 package com.lite.gym.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lite.gym.model.Training;
import com.lite.gym.model.User;

import java.util.List;
import java.util.Map;

 /**
 * 用户服务接口
 */
public interface TrainingService extends IService<Training> {
     Map<String, List<Training>> getMonthTrainingList(String strDate);
     List<Training> getTodyTrainingList(String strDate);
     Training getTrainingById(Long id);
     Boolean saveTraining(Training training);

} 