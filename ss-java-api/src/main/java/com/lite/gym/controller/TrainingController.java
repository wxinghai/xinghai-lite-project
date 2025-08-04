package com.lite.gym.controller;

import com.lite.gym.common.ApiResult;
import com.lite.gym.model.Training;
import com.lite.gym.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/training")
public class TrainingController {

    @Autowired
    private TrainingService trainingService;

    /**
     * 获取所有训练列表
     */
    @GetMapping("/list")
    public ApiResult<List<Training>> listTrainings() {
        return  ApiResult.success(trainingService.list());
    }


    /**
     * 新增训练
     */
    @PostMapping("/add")
    public ApiResult<Boolean> addTraining(@RequestBody Training training) {
        return  ApiResult.success(trainingService.save(training));
    }

    /**
     * 更新训练信息
     */
    @PutMapping("/update")
    public ApiResult<Boolean> updateTraining(@RequestBody Training training) {
        return  ApiResult.success(trainingService.updateById(training));
    }

    /**
     * 删除训练
     */
    @DeleteMapping("/delete/{id}")
    public ApiResult<Boolean> deleteTraining(@PathVariable Long id) {
        return  ApiResult.success(trainingService.removeById(id));
    }


      /**
     * 删除训练
     */
    @GetMapping("/getTrainingAction/{strDate}")
    public ApiResult<Map<String, List<Training>>> getMonthTrainingList(@PathVariable String strDate) {
         return ApiResult.success(trainingService.getMonthTrainingList(strDate));

    }

    @GetMapping("/getTodyTrainingList/{strDate}")
    public ApiResult<List<Training>> getTodyTrainingList(@PathVariable String strDate) {
        return ApiResult.success(trainingService.getTodyTrainingList(strDate));

    }
    @GetMapping("/getTrainingById/{id}")
    public ApiResult<Training> getTrainingById(@PathVariable Long id) {
        return ApiResult.success(trainingService.getTrainingById(id));

    }

    @PostMapping("/saveTraining")
    public ApiResult<Boolean> saveTraining(@RequestBody Training training) {
        return ApiResult.success(trainingService.saveTraining(training));
    }
}
