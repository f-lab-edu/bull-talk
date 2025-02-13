package com.lima.consoleservice.common.connection;

import com.lima.consoleservice.logs.StockDataParser;
import com.lima.consoleservice.schedule.log.params.AlphaVantageConfig;
import java.io.IOException;
import java.util.Objects;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.HttpUrl.Builder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OkHttpClientConnection {

  @Value("${api-key}")
  private String apiKey;
  private final String URL = AlphaVantageConfig.DEFAULT.baseUrl();
  @Getter
  private final OkHttpClient okHttpClient;

  public OkHttpClientConnection() {
    this.okHttpClient = new OkHttpClient();
  }

  public HttpUrl.Builder buildParameters() {
    HttpUrl.Builder builder = Objects.requireNonNull(Objects.requireNonNull(HttpUrl.parse(URL))).newBuilder();
    builder.addQueryParameter("apikey", apiKey);
    return builder;
  }

  public void connectAlphaVantage(JobExecutionContext context, Builder url, String timeTitle) {
    HttpUrl httpUrl = url.build();
    try (Response response = getOkHttpClient().newCall(new Request.Builder().url(httpUrl).build()).execute()) {
      if (response.isSuccessful()) {
        String body = response.body().string();
        String index = context.getJobDetail().getJobDataMap().get("index").toString();
        StockDataParser parser = new StockDataParser();
        parser.setIndex(index);
        parser.setTimeTitle(timeTitle);
        parser.dataParser(body);
      } else {
        log.info("emtpy log: {}", httpUrl);
      }
    } catch (IOException e) {
      log.error("Fail to connect: {}", url, e);
    }
  }
}
