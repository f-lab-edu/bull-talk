package com.lima.consoleservice.common.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtils {

  public static final String STANDARD_DATE_TIME_FORMAT = "yyyy-MM-dd hh:mm:ss";

  public static String getNowStandardTime() {
    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(STANDARD_DATE_TIME_FORMAT);

    return now.format(formatter);
  }
}
