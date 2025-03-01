package com.sid;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringApplicationContext implements ApplicationContextAware {

	private static ApplicationContext CONTEXT;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// TODO Auto-generated method stub
        CONTEXT = applicationContext;
	}
	
	//recuperer any objet d'une class
	public static Object getBean(String beanName) {
		return CONTEXT.getBean(beanName);
	}

}
