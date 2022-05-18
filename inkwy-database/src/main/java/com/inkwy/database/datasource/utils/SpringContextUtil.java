package com.inkwy.database.datasource.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @Aouthor: shixianchao
 * @name: SpringContextUtil
 * @projectName metroplex
 * @create 2019-09-03 下午2:21
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringContextUtil.applicationContext == null) {
            SpringContextUtil.applicationContext = applicationContext;
        }
    }


    public static <T> T getBean(Class<T> beanType) {
        return applicationContext.getBean(beanType);
    }

    public static <T> T getBean(String beanName, Class<T> beanType) {
        return applicationContext.getBean(beanName, beanType);
    }

    public static Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Object getProperty(String propertyName) {
        Environment env = applicationContext.getEnvironment();
        return env.getProperty(propertyName);
    }
}
