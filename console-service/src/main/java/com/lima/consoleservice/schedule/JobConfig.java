package com.lima.consoleservice.schedule;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Slf4j
@Configuration
public class JobConfig {

  // 리스트로 반환하여 여러개의 Job을 등록할 수 있게 한다.
  @Bean
  public List<JobDetail> jobDetail() {

    return Arrays.stream(RegisterJob.values())
        .map(job -> JobBuilder.newJob(job.getClazz())
            .withIdentity(job.name())
            .usingJobData("index", job.getIndex())
            .usingJobData("schedule_time", job.getScheduleTime())
            .storeDurably() // JobDetail이 트리거 없이도 유지될 수 있도록 저장하는 옵션
            .build()
        ).collect(Collectors.toList());
  }

  @Bean
  List<CronTrigger> cronTriggers(List<JobDetail> jobDetails) {
    try {
      List<CronTrigger> triggers = new ArrayList<>();

      for (JobDetail jobDetail : jobDetails) {
        RegisterJob registerJob = RegisterJob.valueOf(jobDetail.getKey().getName());

        CronTriggerFactoryBean triggerFactory = new CronTriggerFactoryBean();
        triggerFactory.setJobDetail(jobDetail);
        triggerFactory.setCronExpression(registerJob.getScheduleTime());
        triggerFactory.afterPropertiesSet(); // InitializingBean 인터페이스에서 제공하는 메서드이고 CronTriggerFactoryBean의 설정이 완료된 후 필요한 초기화를 수행하는 역할을 한다.
        triggers.add(triggerFactory.getObject());
      }
      return triggers;
    } catch (ParseException e) {
      throw new RuntimeException();
    }
  }

  @Bean
  public SchedulerFactoryBean schedulerFactory(List<CronTrigger> triggers) {
    SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
    // triggers를 CronTrigger[] 배열로 변환하여 Quartz에 등록
    schedulerFactory.setTriggers(triggers.toArray(new CronTrigger[0]));
    return schedulerFactory;
  }
}
