/**
 * 日期工具类
 * 提供常用的日期处理、格式化功能
 */

/**
 * 获取当前日期的标准格式字符串 YYYY-MM-DD
 * @returns {String} 格式化后的日期
 */
export const getCurrentDateString = () => {
  const date = new Date();
  return formatDate(date);
};

/**
 * 获取当前月份字符串 YYYY-MM
 * @returns {String} 格式化后的月份
 */
export const getCurrentMonthString = () => {
  const date = new Date();
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  return `${year}-${month}`;
};

/**
 * 将日期格式化为标准格式 YYYY-MM-DD
 * @param {Date|String} date - 日期对象或日期字符串
 * @returns {String} 格式化后的日期字符串
 */
export const formatDate = (date) => {
  if (!date) return '';
  
  // 如果是字符串且已经是标准格式，直接返回
  if (typeof date === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(date)) {
    return date;
  }
  
  const d = new Date(date);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  
  return `${year}-${month}-${day}`;
};

/**
 * 将时间格式化为标准格式 HH:MM:SS
 * @param {Date|String} time - 时间对象或时间字符串
 * @returns {String} 格式化后的时间字符串
 */
export const formatTime = (time) => {
  if (!time) return '';
  
  const d = new Date(time);
  const hours = String(d.getHours()).padStart(2, '0');
  const minutes = String(d.getMinutes()).padStart(2, '0');
  const seconds = String(d.getSeconds()).padStart(2, '0');
  
  return `${hours}:${minutes}:${seconds}`;
};

/**
 * 将日期时间格式化为标准格式 YYYY-MM-DD HH:MM:SS
 * @param {Date|String} datetime - 日期时间对象或日期时间字符串
 * @returns {String} 格式化后的日期时间字符串
 */
export const formatDateTime = (datetime) => {
  if (!datetime) return '';
  
  const d = new Date(datetime);
  return `${formatDate(d)} ${formatTime(d)}`;
};

/**
 * 获取指定月份的所有天数
 * @param {String} yearMonth - 年月字符串，格式：YYYY-MM
 * @returns {Number} 该月的总天数
 */
export const getDaysInMonth = (yearMonth) => {
  if (!yearMonth) return 0;
  
  const [year, month] = yearMonth.split('-').map(Number);
  return new Date(year, month, 0).getDate();
};

/**
 * 获取指定月份的所有日期
 * @param {String} yearMonth - 年月字符串，格式：YYYY-MM
 * @returns {Array} 该月的所有日期数组 ['YYYY-MM-01', 'YYYY-MM-02', ...]
 */
export const getAllDatesInMonth = (yearMonth) => {
  if (!yearMonth) return [];
  
  const [year, month] = yearMonth.split('-').map(Number);
  const daysInMonth = getDaysInMonth(yearMonth);
  
  const dates = [];
  for (let i = 1; i <= daysInMonth; i++) {
    const day = String(i).padStart(2, '0');
    dates.push(`${year}-${String(month).padStart(2, '0')}-${day}`);
  }
  
  return dates;
};

/**
 * 检查日期是否是今天
 * @param {String} dateString - 日期字符串，格式：YYYY-MM-DD
 * @returns {Boolean} 是否是今天
 */
export const isToday = (dateString) => {
  return dateString === getCurrentDateString();
};

/**
 * 计算两个日期之间的天数差
 * @param {String|Date} date1 - 第一个日期
 * @param {String|Date} date2 - 第二个日期
 * @returns {Number} 天数差
 */
export const daysBetween = (date1, date2) => {
  const d1 = new Date(formatDate(date1));
  const d2 = new Date(formatDate(date2));
  const diffTime = Math.abs(d2 - d1);
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  return diffDays;
};

/**
 * 根据日期获取星期几
 * @param {String|Date} date - 日期
 * @returns {String} 星期几的中文表示
 */
export const getWeekdayCN = (date) => {
  const weekdays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
  const d = new Date(date);
  return weekdays[d.getDay()];
};

/**
 * 将秒数转换为可读的时间格式
 * @param {Number} seconds - 秒数
 * @returns {String} 格式化的时间字符串 (HH:MM:SS 或 MM:SS)
 */
export const secondsToTimeString = (seconds) => {
  if (!seconds || seconds < 0) return '00:00';
  
  const hours = Math.floor(seconds / 3600);
  const minutes = Math.floor((seconds % 3600) / 60);
  const remainingSeconds = seconds % 60;
  
  if (hours > 0) {
    return `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(remainingSeconds).padStart(2, '0')}`;
  } else {
    return `${String(minutes).padStart(2, '0')}:${String(remainingSeconds).padStart(2, '0')}`;
  }
};

/**
 * 将毫秒数转换为可读的时间格式
 * @param {Number} milliseconds - 毫秒数
 * @returns {String} 格式化的时间字符串
 */
export const millisecondsToTimeString = (milliseconds) => {
  return secondsToTimeString(Math.floor(milliseconds / 1000));
};


/**
 * 获取当前日期的前一年同一天
 * 例如：当前为2025-08-04，则返回2024-08-04
 * @returns {String} 格式化后的日期字符串（YYYY-MM-DD）
 */
export const getLastYearToday = () => {
  const now = new Date();
  // 获取当前年、月、日
  const year = now.getFullYear() - 1;
  const month = now.getMonth();
  const day = now.getDate();
  // 构造前一年同月同日的日期对象
  const lastYearDate = new Date(year, month, day);
  return formatDate(lastYearDate, 'YYYY-MM-DD');
};
 
/**
 * 获取当前日期的下一年同一天
 * 例如：当前为2025-08-04，则返回2026-08-04
 * @returns {String} 格式化后的日期字符串（YYYY-MM-DD）
 */
export const getNextYearToday = () => {
  const now = new Date();
  // 获取当前年、月、日
  const year = now.getFullYear() + 1;
  const month = now.getMonth();
  const day = now.getDate();
  // 构造下一年同月同日的日期对象
  const nextYearDate = new Date(year, month, day);
  return formatDate(nextYearDate, 'YYYY-MM-DD');
};

/**
 * 获取今年的第一天和最后一天
 * @returns {{firstDay: String, lastDay: String}} 今年第一天和最后一天（YYYY-MM-DD格式）
 */
export const getThisYearRange = () => {
  const now = new Date();
  const year = now.getFullYear();
  const firstDay = formatDate(new Date(year, 0, 1), 'YYYY-MM-DD');
  const lastDay = formatDate(new Date(year, 11, 31), 'YYYY-MM-DD');
  const nowDay = formatDate(now, 'YYYY-MM-DD');
  return {
    firstDay,
    lastDay,
    nowDay  
  };
};

 

export default {
  getCurrentDateString,
  getCurrentMonthString,
  formatDate,
  formatTime,
  formatDateTime,
  getDaysInMonth,
  getAllDatesInMonth,
  isToday,
  daysBetween,
  getWeekdayCN,
  secondsToTimeString,
  millisecondsToTimeString,
  getLastYearToday,
  getNextYearToday,
  getThisYearRange
}; 