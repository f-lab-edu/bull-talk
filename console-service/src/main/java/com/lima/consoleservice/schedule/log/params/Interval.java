package com.lima.consoleservice.schedule.log.params;

import lombok.Getter;

@Getter
public enum Interval {
  INTERVAL("Interval")
  , ONE_MIN("1min")
  , FIVE_MIN("5min")
  , FIFTEEN_MIN("15min")
  , THIRTY_MIN("30min")
  , SIXTY_MIN("60min")
  , DAILY("DAILY")
  , WEEKLY("WEEKLY")
  , MONTHLY("MONTHLY")
  ;

  private final String value;

  Interval(String value) {
    this.value = value;
  }
}
