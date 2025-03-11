package com.lima.consoleservice.schedule.log;

import com.lima.consoleservice.common.connection.OkHttpClientConnection;
import com.lima.consoleservice.common.utils.BeansUtils;
import com.lima.consoleservice.config.ThreadPool;
import com.lima.consoleservice.schedule.log.params.Function;
import com.lima.consoleservice.schedule.log.params.Interval;
import com.lima.consoleservice.schedule.log.params.Symbol;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl.Builder;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

@Slf4j
public class TimeSeriesIntraDayLog implements Job {
  private final OkHttpClientConnection connection;
  private final ThreadPool threadPool;
  private final String timeTitle = "Time Series (5min)";
  private final String symbolParam = "symbol";

  public TimeSeriesIntraDayLog() {
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
          builder.addQueryParameter(Function.FUNCTION.name().toLowerCase(), Function.TIME_SERIES_INTRADAY.name());
          builder.addQueryParameter(symbolParam, symbol.name());
          builder.addQueryParameter(Interval.INTERVAL.name().toLowerCase(), Interval.FIVE_MIN.getValue());
          connection.connectAlphaVantage(context, builder, timeTitle);
          return null;
        });
      }

      threadPool.invokeAll(tasks);
      threadPool.shutdown();
    } catch (Exception e) {
      // LIMA: catch 부분 예외처리 고민 필요
      log.error("", e);
    }
  }
}
