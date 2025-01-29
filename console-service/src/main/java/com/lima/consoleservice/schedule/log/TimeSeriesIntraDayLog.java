package com.lima.consoleservice.schedule.log;

import com.lima.consoleservice.common.utils.BeansUtils;
import com.lima.consoleservice.config.OkHttpClientConnection;
import com.lima.consoleservice.schedule.log.params.Function;
import com.lima.consoleservice.schedule.log.params.Interval;
import com.lima.consoleservice.schedule.log.params.Symbol;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.HttpUrl.Builder;
import okhttp3.Request;
import okhttp3.Response;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

@Slf4j
public class TimeSeriesIntraDayLog implements Job {
  private final OkHttpClientConnection connection;

  public TimeSeriesIntraDayLog() {
    this.connection = BeansUtils.getBean(OkHttpClientConnection.class);
  }

  @Override
  public void execute(JobExecutionContext context) {
    try {
      ExecutorService executorService = Executors.newFixedThreadPool(10);
      List<Callable<Void>> tasks = new ArrayList<>();

      for (Symbol symbol : Symbol.values()) {
        tasks.add(() -> {
          Builder builder = connection.buildParameters();
          builder.addQueryParameter(Function.FUNCTION.name().toLowerCase(), Function.TIME_SERIES_INTRADAY.name());
          builder.addQueryParameter(Symbol.SYMBOL.name().toLowerCase(), symbol.name());
          builder.addQueryParameter(Interval.INTERVAL.name().toLowerCase(), Interval.FIVE_MIN.getValue());
          connectIntraDayHttp(context, builder);
          return null;
        });
      }
      executorService.invokeAll(tasks);
      executorService.shutdown();
    } catch (Exception e) {
      // LIMA: catch 부분 예외처리 고민 필요
      log.error("", e);
    }
  }

  private void connectIntraDayHttp(JobExecutionContext context, HttpUrl.Builder url) {
    HttpUrl httpUrl = url.build();
    try(Response response = OkHttpClientConnection.getInstance().newCall(new Request.Builder().url(httpUrl).build()).execute()) {
      if (response.isSuccessful()) {
        String body = response.body().string();
        String index = context.getJobDetail().getJobDataMap().get("index").toString();
        // LIMA: Elasticsearch 붙여야 한다.
        
      }
    } catch (IOException e) {
      log.error("Fail" + url, e);
    }
  }

}
