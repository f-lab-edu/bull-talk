package com.lima.consoleservice.common.utils;

import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
@Getter
public class BeansUtils implements ApplicationContextAware {

  private static ApplicationContext applicationContext;

  @Override
  public void setApplicationContext(ApplicationContext context) throws BeansException {
    applicationContext = context;
  }

  // Getter 메서드로 빈을 얻을 수 있도록 구현
  public static <T> T getBean(Class<T> clazz) {
    return applicationContext.getBean(clazz);
  }
}
