package com.lima.consoleservice.schedule;

import com.lima.consoleservice.schedule.log.TimeSeriesIntraDayLog;
import com.lima.consoleservice.schedule.log.TimeSeriesMonthlyLog;
import com.lima.consoleservice.schedule.log.TimeSeriesWeeklyLog;
import lombok.Getter;
import org.quartz.Job;

@Getter
public enum RegisterJob {
  // 0 0 6 * * ? 매일 아침 6시
  // 0 0 0/1 * * ? 시간마다 실행
  // 0 0 7 ? * MON 매주 월요일 아침 7시
  TIME_SERIES_INTRADAY(TimeSeriesIntraDayLog.class, "time_series_intraday", "0 0/2 * * * ?"),
//  TIME_SERIES_WEEKLY(TimeSeriesWeeklyLog.class, "time_series_weekly", "0 0 7 ? * MON"), // 매주 월요일 아침 7시
//  TIME_SERIES_MONTHLY(TimeSeriesMonthlyLog.class, "time_series_monthly", "0 0 8 1 * ?"), // 매월 1일 아침 8시
  ;



  private final Class<? extends Job> clazz;
  private final String index;
  private final String scheduleTime;

  RegisterJob(Class<? extends Job> clazz, String index, String scheduleTime) {
    this.clazz = clazz;
    this.index = index;
    this.scheduleTime = scheduleTime;
  }
}
