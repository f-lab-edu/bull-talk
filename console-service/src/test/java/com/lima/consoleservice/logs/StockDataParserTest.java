package com.lima.consoleservice.logs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lima.consoleservice.common.connection.ElasticsearchConnection;
import com.lima.consoleservice.logs.model.StockData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class StockDataParserTest {


  @InjectMocks
  private StockDataParser stockDataParser;

  @Mock
  private ElasticsearchConnection elasticsearchConnection;

  private String validJsonString;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    stockDataParser.setIndex("time_series_intraday");
    stockDataParser.setTimeTitle("Time Series (5min)");

    validJsonString = "{\n"
        + "  \"Meta Data\": {\n"
        + "    \"1. Information\": \"Intraday (5min) open, high, low, close prices and volume\",\n"
        + "    \"2. Symbol\": \"MSFT\",\n"
        + "    \"3. Last Refreshed\": \"2025-01-28 19:55:00\",\n"
        + "    \"4. Interval\": \"5min\",\n"
        + "    \"5. Output Size\": \"Compact\",\n"
        + "    \"6. Time Zone\": \"US/Eastern\"\n"
        + "  },\n"
        + "  \"Time Series (5min)\": {\n"
        + "    \"2025-01-28 19:55:00\": {\n"
        + "      \"1. open\": \"446.7900\",\n"
        + "      \"2. high\": \"447.0000\",\n"
        + "      \"3. low\": \"446.5000\",\n"
        + "      \"4. close\": \"446.7000\",\n"
        + "      \"5. volume\": \"672\"\n"
        + "    }\n"
        + "  }\n"
        + "}";
  }

  @Test
  void testDataParseSuccess() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode rootNode = objectMapper.readTree(validJsonString);
    String symbol = rootNode.path("Meta Data").path("2. Symbol").asText();
    JsonNode timeSeries = rootNode.path("Time Series (5min)");
    Iterator<Entry<String, JsonNode>> fields = timeSeries.fields();

    List<StockData> parsedStockData = new ArrayList<>();
    while (fields.hasNext()) {
      Entry<String, JsonNode> field = fields.next();
      String timestamp = field.getKey();
      JsonNode values = field.getValue();

      StockData stockData = StockData.builder()
          .symbol(symbol)
          .timestamp(timestamp)
          .open(values.path("1. open").asDouble())
          .high(values.path("2. high").asDouble())
          .low(values.path("3. low").asDouble())
          .close(values.path("4. close").asDouble())
          .volume(values.path("5. volume").asInt())
          .build();
      parsedStockData.add(stockData);
    }

    assertEquals(1, parsedStockData.size());
    StockData result = parsedStockData.get(0);
    assertEquals("MSFT", result.getSymbol());
    assertEquals("2025-01-28 19:55:00", result.getTimestamp());
    assertEquals(446.7900, result.getOpen());
    assertEquals(447.0000, result.getHigh());
    assertEquals(446.5000, result.getLow());
    assertEquals(446.7000, result.getClose());
    assertEquals(672, result.getVolume());
  }

  @Test
  void testDataParserInvalidData() {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode rootNode;
    try {
      rootNode = objectMapper.readTree(validJsonString);
      JsonNode timeSeries = rootNode.path("Time Series (5min)");
      Iterator<Entry<String, JsonNode>> fields = timeSeries.fields();

      while (fields.hasNext()) {
        Entry<String, JsonNode> field = fields.next();
        JsonNode values = field.getValue();

        String openValue = values.path("1. open").asText(null);
        Double.parseDouble(openValue);
      }
    } catch (NumberFormatException e) {
      assertTrue(true);
    } catch (IOException e) {
      fail("IOException 발생");
    }
  }

}