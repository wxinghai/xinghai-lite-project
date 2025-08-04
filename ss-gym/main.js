import App from './App'

// #ifndef VUE3
import Vue from 'vue'
import './uni.promisify.adaptor'
import TrainingEntry from './components/common/TrainingEntry.vue'

Vue.config.productionTip = false
App.mpType = 'app'

// 注册全局组件
Vue.component('training-entry', TrainingEntry)

// 禁用橡皮筋效果的全局处理
document.addEventListener('touchmove', function (e) {
  if (e.touches.length > 1) {
    e.preventDefault();
  }
}, { passive: false });

const app = new Vue({
  ...App
})
app.$mount()
// #endif

// #ifdef VUE3
import { createSSRApp } from 'vue'
import TrainingEntry from './components/common/TrainingEntry.vue'

export function createApp() {
  const app = createSSRApp(App)
  
  // 注册全局组件
  app.component('training-entry', TrainingEntry)
  
  // 禁用橡皮筋效果的全局处理
  if (typeof document !== 'undefined') {
    document.addEventListener('touchmove', function (e) {
      if (e.touches.length > 1) {
        e.preventDefault();
      }
    }, { passive: false });
  }
  
  return {
    app
  }
}
// #endif