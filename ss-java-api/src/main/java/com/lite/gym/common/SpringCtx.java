package com.lite.gym.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring Context 上下文
 * @author dbg
 *
 */
@Component
public class SpringCtx implements ApplicationContextAware {
	public static ApplicationContext applicationContext; 	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		System.out.println("init spring ctx...");
		SpringCtx.applicationContext = applicationContext;
	}

	public static Object getBean(String name) {
		return applicationContext.getBean(name);
	}

	public static <T> T getBean(String name, Class<T> requiredType) {
		return applicationContext.getBean(name, requiredType);
	}

	/**
	 * 获取默认的Bean
	 * @param clz
	 * @param <T>
	 * @return
	 */
	public static <T> T getBean(Class<T> clz){
		String beanName = firstLowcase(clz.getSimpleName());
		return SpringCtx.getBean(beanName, clz);
	}
	   /** 首字母小写 */
    private static String firstLowcase(String str) {
        char first = str.charAt(0);
        return Character.toLowerCase(first)+str.substring(1,str.length());
    }
	public static boolean containsBean(String name) {
		return applicationContext.containsBean(name);
	}

	public static boolean isSingleton(String name) {
		return applicationContext.isSingleton(name);
	}

	public static Class<? extends Object> getType(String name) {
		return applicationContext.getType(name);
	}
	/**
	 * 获取当前环境 
	 * @return dev|test|uat|prod
	 */
	public static String getActiveProfile() {
		return applicationContext.getEnvironment().getActiveProfiles()[0];
	}
	public static String getEnvPropVal(String key) {
		return applicationContext.getEnvironment().getProperty(key);
	}

}