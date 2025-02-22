package com.lima.consoleservice.logs;

import co.elastic.clients.elasticsearch.core.BulkResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lima.consoleservice.common.connection.ElasticsearchConnection;
import com.lima.consoleservice.logs.model.StockData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StockDataParser {

  private final ElasticsearchConnection elasticsearchConnection;
  @Setter
  private String index;
  @Setter
  private String timeTitle;

  public StockDataParser() {
    this.elasticsearchConnection = new ElasticsearchConnection();
  }

  public void dataParser(String jsonString) {
    try {
      log.info("Data Parsing Start");
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode rootNode = objectMapper.readTree(jsonString);
      String symbol = rootNode.path("Meta Data").path("2. Symbol").asText();
      JsonNode timeSeries = rootNode.path(this.timeTitle);
      List<String> jsonDataList = new ArrayList<>();
      Iterator<Entry<String, JsonNode>> fields = timeSeries.fields();

      while (fields.hasNext()) {
        Entry<String, JsonNode> field = fields.next();
        String timestamp = field.getKey();
        JsonNode values = field.getValue();

        // StockData 객체 생성 후 리스트에 추가
        StockData stockData = StockData.builder()
            .symbol(symbol)
            .timestamp(timestamp)
            .open(values.path("1. open").asDouble())
            .high(values.path("2. high").asDouble())
            .low(values.path("3. low").asDouble())
            .close(values.path("4. close").asDouble())
            .volume(values.path("5. volume").asInt())
            .build();
        jsonDataList.add(objectMapper.writeValueAsString(stockData));
      }

      log.info("Data Parsing End");
      BulkResponse response = elasticsearchConnection.bulkInsert(index, jsonDataList);
      if (response.errors()) {
        throw new RuntimeException("Elasticsearch bulk insert failed");
      } else {
        log.info("Elasticsearch bulk insert success. size: " + jsonDataList.size());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
