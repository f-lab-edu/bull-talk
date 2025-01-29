package com.lima.consoleservice.schedule;

import com.lima.consoleservice.schedule.log.TimeSeriesIntraDayLog;
import lombok.Getter;
import org.quartz.Job;

@Getter
public enum RegisterJob {
  // 0 0 6 * * ? 매일 아침 6시
  // 0 0 0/1 * * ? 시간마다 실행
  TIME_SERIES_INTRADAY(TimeSeriesIntraDayLog.class, "TIME_SERIES_INTRADAY", "0 0/2 * * * ?"),
//  TIME_SERIES_WEEKLY(TimeSeriesIntraDayLog.class, "TIME_SERIES_WEEKLY", "0 0 0/5 * * ?"),
//  TIME_SERIES_MONTHLY(TimeSeriesIntraDayLog.class, "TIME_SERIES_MONTHLY", "0 0 0/5 * * ?"),
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
