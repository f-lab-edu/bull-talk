package com.lima.consoleservice.common.connection;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.IndexOperation;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.Node;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ElasticsearchConnection {

  private final ElasticsearchClient client;
  private final ElasticsearchTransport transport;
  private final RestClient restClient;

  private static final String[] ELASTICSEARCH_HOSTS = {"localhost:9200", "localhost:9201", "localhost:9202"};
  private static final String SCHEME = "http";
  private static final int CONNECTION_TIMEOUT = 5000;
  private static final int SOCKET_TIMEOUT = 30000;

  public ElasticsearchConnection() {
    RestClient initializedRestClient;
    try {
      initializedRestClient = initializeRestClient();
    } catch (Exception e) {
      log.error("Failed to initialize Elasticsearch RestClient", e);
      throw new IllegalStateException("Cannot initialize Elasticsearch connection", e);
    }
    this.restClient = initializedRestClient;
    this.transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
    this.client = new ElasticsearchClient(transport);
  }

  private RestClient initializeRestClient() {
    HttpHost[] hosts = Arrays.stream(ELASTICSEARCH_HOSTS)
        .map(host -> {
          String[] parts = host.split(":");
          return new HttpHost(parts[0], Integer.parseInt(parts[1]), SCHEME);
        })
        .toArray(HttpHost[]::new);

    return RestClient.builder(hosts)
        .setRequestConfigCallback(builder ->
            builder.setConnectTimeout(CONNECTION_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT))
        .build();
  }

  public synchronized BulkResponse bulkInsert(String index, List<String> jsonDataList) {
    List<BulkOperation> operations = new ArrayList<>();
    log.info("index: {}, insert start", index);
    try {
      if (jsonDataList.isEmpty()) {
        throw new IllegalArgumentException("jsonDataList cannot be empty");
      }

      for (String jsonData : jsonDataList) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> jsonMap = objectMapper.readValue(
            jsonData, new TypeReference<Map<String, Object>>() {}
        );

        IndexOperation<Object> indexOp = IndexOperation.of(op -> op
            .index(index)
            .document(jsonMap)
        );

        // BulkOperation 객체에 IndexOperation 추가
        operations.add(BulkOperation.of(op -> op.index(indexOp)));  // BulkOperation 생성 시 인덱스 작업 추가
      }

      BulkRequest bulkRequest = BulkRequest.of(req -> req.operations(operations)  // 생성된 BulkOperation 목록 전달
      );

      return client.bulk(bulkRequest);

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Failed to perform bulk insert", e);
    }
  }


  @PreDestroy
  public void closeClient() {
    try {
      if (transport != null) {
        transport.close();
      }
      if (restClient != null) {
        restClient.close();
      }
    } catch (IOException e) {
      throw new RuntimeException("Error closing Elasticsearch client", e);
    }
  }
}
