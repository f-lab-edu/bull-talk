package com.lima.consoleservice.common.connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.bulk.OperationType;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ElasticsearchConnectionTest {

  @Mock
  private ElasticsearchClient client;

  @InjectMocks
  private ElasticsearchConnection elasticsearchConnection;

  private final String index = "time_series_intraday";
  private List<String> validJsonList;
  private List<String> invalidJsonList;

  @BeforeEach
  void setUp() {
    validJsonList = List.of(
        "{ \"open\": \"222.222\", \"high\": \"123.123\" }",
        "{ \"open\": \"111.111\", \"high\": \"321.321\" }"
    );

    invalidJsonList = List.of(
        "{ \"open\": \"111.111\", \"high\": }"
    );
  }

  @Test
  void testBulkInsert_Success() throws IOException {
    List<BulkResponseItem> items = List.of(
        new BulkResponseItem.Builder()
            .index("time_series_intraday")
            .result("created")
            .operationType(OperationType.Index)
            .status(201)
            .build()
    );

    BulkResponse mockResponse = new BulkResponse.Builder()
        .errors(false)
        .items(items)
        .took(1000)
        .build();

    doReturn(mockResponse).when(client).bulk(any(BulkRequest.class));
    BulkResponse response = elasticsearchConnection.bulkInsert("time_series_intraday", validJsonList);

    assertNotNull(response);
    assertFalse(response.errors());
    assertEquals(2, response.items().size());
  }

  @Test
  void testBulkInsertInvalidJson() {
    RuntimeException exception = assertThrows(
        RuntimeException.class,
        () -> elasticsearchConnection.bulkInsert(index, invalidJsonList)
    );

    assertTrue(exception.getMessage().contains("Failed to perform bulk insert"));
  }
}