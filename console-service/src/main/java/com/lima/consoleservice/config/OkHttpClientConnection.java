package com.lima.consoleservice.config;

import com.lima.consoleservice.schedule.log.params.AlphaVantageConfig;
import java.util.Objects;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OkHttpClientConnection {

  private static final String API_KEY = "O4DHI459B5GRYIIX";
  private final String URL = AlphaVantageConfig.DEFAULT.baseUrl();
  @Getter
  private final OkHttpClient okHttpClient;

  public OkHttpClientConnection() {
    this.okHttpClient = new OkHttpClient();
  }

  public HttpUrl.Builder buildParameters() {
//    log.info("[time: " + DateTimeUtils.getNowStandardTime() + "] - url: " + URL);
    HttpUrl.Builder builder = Objects.requireNonNull(Objects.requireNonNull(HttpUrl.parse(URL))).newBuilder();
    builder.addQueryParameter("apikey", API_KEY);
    return builder;
  }
}
