// 全局训练状态管理器
class TrainingManager {
    constructor() {
        this.isTraining = false;
        this.trainingData = null;
        this.timerInterval = null;
        this.startTime = null;
        this.elapsedTime = 0;
        this.alreadyTrainingTime = 0;
        this.updateTimeCallbacks = null;
  
    }
    
 
    
    // 开始训练
    startTraining(callback, alreadyTrainingTime = 0) {

        if(this.isTraining){
            console.log('已有训练在进行中');
            return { success: false, message: '已有训练在进行中' };
        }

        this.isTraining = true;
        // 开始时间
        this.startTime = Date.now();
        this.elapsedTime = 0;
        this.updateTimeCallbacks = callback;
        this.alreadyTrainingTime = alreadyTrainingTime;
        // 启动计时器
        this.startTimer();
    
        return { success: true, message: '训练开始成功' };
    }

      getCurrentTime(timess) {
        const now = new Date(timess);
        const hours = now.getHours().toString().padStart(2, '0'); // 获取小时并补零
        const minutes = now.getMinutes().toString().padStart(2, '0'); // 获取分钟并补零
        return `${hours}:${minutes}`;
      }

    resetTrainingData(trainingData){
        this.trainingData = trainingData;
    }
    getRangeTime(){
        return this.getCurrentTime(this.startTime) + '~' + this.getCurrentTime(new Date());
    }
    // 完成训练
    finishTraining() {
        if (!this.isTraining) {
            return { success: false, message: '没有进行中的训练' };
        }
        // 停止计时器
        this.stopTimer();
        
        // 计算总训练时间
        const totalTime = this.elapsedTime;

        let rangeTime = this.getCurrentTime(this.startTime) + '~' + this.getCurrentTime(new Date());
        
        // 清除训练状态
        this.isTraining = false;
        this.trainingData = null;
        this.startTime = null;
        this.updateTimeCallbacks = null;
        this.elapsedTime = 0;
    
        return {
            success: true,
            message: '训练完成',
            rangeTime: rangeTime,
            
        }
    }
    
    // 启动计时器
    startTimer() {
        if (this.timerInterval) {
            clearInterval(this.timerInterval);
        }
        this.timerInterval = setInterval(() => {
            this.elapsedTime = Date.now() - this.startTime;
            // 更新时间
            let calcTime = this.elapsedTime+this.alreadyTrainingTime;
            this.triggerUpdateTimeCallbacks(calcTime);
        }, 1000);
    }
 
    
    // 停止计时器
    stopTimer() {
        if (this.timerInterval) {
            clearInterval(this.timerInterval);
            this.timerInterval = null;
        }
    }
    
 
    // 获取当前训练状态
    getCurrentTraining() {
        if (!this.isTraining) {
            return null;
        }
        
        return {
            trainingData: this.trainingData,
            elapsedTime: this.elapsedTime,
            formattedTime: this.formatTime(this.elapsedTime)
        };
    }
    
    // 检查是否有进行中的训练
    hasActiveTraining() {
        return this.isTraining;
    }

    // 触发更新回调
    triggerUpdateTimeCallbacks(calcTime) {      
        if(this.updateTimeCallbacks){   
				console.log(typeof calcTime,"hhhh ")
            this.updateTimeCallbacks(calcTime);
        }
       
    }
    
    // 格式化时间
    formatTime(milliseconds) {
        const seconds = Math.floor(milliseconds / 1000);
        const minutes = Math.floor(seconds / 60);
        const hours = Math.floor(minutes / 60);
        
        const remainingSeconds = seconds % 60;
        const remainingMinutes = minutes % 60;
        
        if (hours > 0) {
            return `${String(hours).padStart(2, '0')}:${String(remainingMinutes).padStart(2, '0')}:${String(remainingSeconds).padStart(2, '0')}`;
        } else {
            return `${String(remainingMinutes).padStart(2, '0')}:${String(remainingSeconds).padStart(2, '0')}`;
        }
    }
}

// 导出单例
export default new TrainingManager(); 