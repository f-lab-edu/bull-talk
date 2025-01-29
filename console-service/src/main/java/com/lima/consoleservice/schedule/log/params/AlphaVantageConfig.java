package com.lima.consoleservice.schedule.log.params;

public record AlphaVantageConfig(String baseUrl) {
  public static final AlphaVantageConfig DEFAULT = new AlphaVantageConfig("https://www.alphavantage.co/query");
}
