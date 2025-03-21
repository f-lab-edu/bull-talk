package com.lima.consoleservice.schedule.log;

import com.lima.consoleservice.common.connection.OkHttpClientConnection;
import com.lima.consoleservice.common.utils.BeansUtils;
import com.lima.consoleservice.config.ThreadPool;
import com.lima.consoleservice.schedule.log.params.Function;
import com.lima.consoleservice.schedule.log.params.Symbol;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl.Builder;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

@Slf4j
public class TimeSeriesWeeklyLog implements Job {
  private final OkHttpClientConnection connection;
  private final ThreadPool threadPool;
  private final String timeTitle = "Weekly Time Series";
  private final String symbolParam = "symbol";

  public TimeSeriesWeeklyLog() {
    this.connection = BeansUtils.getBean(OkHttpClientConnection.class);
    this.threadPool = new ThreadPool();
  }

  @Override
  public void execute(JobExecutionContext context) {
    try {
      List<Callable<Void>> tasks = new ArrayList<>();

      for (Symbol symbol : Symbol.values()) {
        tasks.add(() -> {
          Builder builder = connection.buildParameters();
          builder.addQueryParameter(Function.FUNCTION.name().toLowerCase(), Function.TIME_SERIES_WEEKLY.name());
          builder.addQueryParameter(symbolParam, symbol.name());
          connection.connectAlphaVantage(context, builder, timeTitle);
          return null;
        });
      }
      threadPool.invokeAll(tasks);
      threadPool.shutdown();
    } catch (Exception e) {
      log.error("", e);
    }
  }
}
