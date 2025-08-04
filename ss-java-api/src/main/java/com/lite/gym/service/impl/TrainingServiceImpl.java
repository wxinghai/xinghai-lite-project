package com.lite.gym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lite.gym.dao.TrainingActionDao;
import com.lite.gym.dao.TrainingDao;
import com.lite.gym.dao.TrainingSetDao;
import com.lite.gym.model.Training;
import com.lite.gym.model.TrainingAction;
import com.lite.gym.model.TrainingSet;
import com.lite.gym.service.TrainingActionService;
import com.lite.gym.service.TrainingService;
import com.lite.gym.service.TrainingSetService;
import com.lite.gym.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 训练服务实现类
 */
@Service
public class TrainingServiceImpl extends ServiceImpl<TrainingDao, Training> implements TrainingService {



    @Autowired
    private TrainingActionService trainingActionService;

    @Autowired
    private TrainingSetService trainingSetService;

    @Override
    public Map<String, List<Training>> getMonthTrainingList(String strDate) {
        // 参照提供的py代码实现功能
        // 1. 查询指定月份的训练记录
        LambdaQueryWrapper<Training> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Training::getTrainingDate, strDate);
        List<Training> trainings = list(queryWrapper);

        // 2. 按日期分组结果
        Map<String, List<Training>> result = new HashMap<>();

        // 3. 处理每条训练记录
        for (Training training : trainings) {
            String key = training.getTrainingDate();
            // 4. 查询训练相关的动作
            LambdaQueryWrapper<TrainingAction> actionQueryWrapper = new LambdaQueryWrapper<>();

            actionQueryWrapper.eq(TrainingAction::getTrainingId, training.getTrainingId());
            List<TrainingAction> actions = trainingActionService.list(actionQueryWrapper);

            training.setTrainingActions(actions);

            // 6. 处理每个动作
            for (TrainingAction action : actions) {
                // 7. 查询动作相关的组
                LambdaQueryWrapper<TrainingSet> setQueryWrapper = new LambdaQueryWrapper<>();
                setQueryWrapper.eq(TrainingSet::getActionId, action.getActionId());
                List<TrainingSet> trainingSets = trainingSetService.list(setQueryWrapper);
                action.setTrainingSets(trainingSets);
            }
            // 9. 将训练记录添加到对应日期的列表中
            if (!result.containsKey(key)) {
                result.put(key, new ArrayList<>());
            }
            result.get(key).add(training);

        }

        return result;
    }

    @Override
    public List<Training> getTodyTrainingList(String strDate) {
        Map<String, List<Training>> monthTrainingList = getMonthTrainingList(strDate);
        return monthTrainingList.getOrDefault(strDate,new ArrayList<>());
    }

    @Override
    public Training getTrainingById(Long id) {

        Training training = this.getById(id);

        // 4. 查询训练相关的动作
        LambdaQueryWrapper<TrainingAction> actionQueryWrapper = new LambdaQueryWrapper<>();

        actionQueryWrapper.eq(TrainingAction::getTrainingId, training.getTrainingId());
        List<TrainingAction> actions = trainingActionService.list(actionQueryWrapper);

        training.setTrainingActions(actions);

        // 6. 处理每个动作
        for (TrainingAction action : actions) {
            // 7. 查询动作相关的组
            LambdaQueryWrapper<TrainingSet> setQueryWrapper = new LambdaQueryWrapper<>();
            setQueryWrapper.eq(TrainingSet::getActionId, action.getActionId());
            List<TrainingSet> trainingSets = trainingSetService.list(setQueryWrapper);
            action.setTrainingSets(trainingSets);
        }
        return training;
    }

    @Override
    @Transactional
    public Boolean saveTraining(Training training) {
        training.setUserId(SessionUtil.getCurrentUserId());


        boolean save = this.saveOrUpdate(training);
        Long trainingId = training.getTrainingId();

        // 添加非空检查
        List<TrainingAction> newActions = training.getTrainingActions();
        if (newActions == null) {
            newActions = new ArrayList<>();
        }

        // 查询并删除旧数据
        LambdaQueryWrapper<TrainingAction> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TrainingAction::getTrainingId, trainingId);
        List<TrainingAction> trainingActions = trainingActionService.list(queryWrapper);

        if (!trainingActions.isEmpty()) {
            List<Long> actionIds = trainingActions.stream().map(TrainingAction::getActionId).collect(Collectors.toList());
            trainingActionService.removeBatchByIds(actionIds);
            trainingSetService.remove(new LambdaQueryWrapper<TrainingSet>().in(TrainingSet::getActionId, actionIds));
        }

        // 保存新数据
        for (TrainingAction trainingAction : newActions) {
            trainingAction.setTrainingId(trainingId);
        }
        trainingActionService.saveBatch(newActions);

        // 保存训练组数据
        List<TrainingSet> trainingSets = new ArrayList<>();
        for (TrainingAction trainingAction : newActions) {
            Long actionId = trainingAction.getActionId();
            if (trainingAction.getTrainingSets() != null) {
                for (TrainingSet trainingSet : trainingAction.getTrainingSets()) {
                    trainingSet.setActionId(actionId);
                    trainingSets.add(trainingSet);
                }
            }
        }
        if (!trainingSets.isEmpty()) {
            trainingSetService.saveBatch(trainingSets);
        }
        return save;
    }
}