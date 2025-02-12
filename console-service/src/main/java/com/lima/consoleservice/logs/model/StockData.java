package com.lima.consoleservice.logs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class StockData implements Serializable {

  private static final long serialVersionUID = -8078859458940418257L;

  private String symbol;
  private String timestamp;
  private double open;
  private double high;
  private double low;
  private double close;
  private int volume;

}
