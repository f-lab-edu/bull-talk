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
    HttpHost[] httpHosts = new HttpHost[ELASTICSEARCH_HOSTS.length];
    for (int i = 0; i < ELASTICSEARCH_HOSTS.length; i++) {
      String[] hostParts = ELASTICSEARCH_HOSTS[i].split(":");
      String hostname = hostParts[0];
      int port = Integer.parseInt(hostParts[1]);
      httpHosts[i] = new HttpHost(hostname, port, SCHEME);
    }

    RestClientBuilder builder = RestClient.builder(httpHosts)
        .setRequestConfigCallback(requestConfigBuilder ->
            requestConfigBuilder
                .setConnectTimeout(CONNECTION_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT)
        )
        .setFailureListener(new RestClient.FailureListener() {
          @Override
          public void onFailure(Node node) {
            log.warn("Node failed: {}", node);
          }
        });

    RestClient client = builder.build();
    checkNodes(client);
    return client;
  }

  private void checkNodes(RestClient client) {
    Optional<Node> aliveNode = client.getNodes().stream()
        .filter(node -> isNodeAlive(node, client))
        .findFirst();

    if (aliveNode.isEmpty()) {
      log.warn("No available Elasticsearch nodes found. Proceeding with client creation anyway.");
    } else {
      log.info("Connected to Elasticsearch node: {}", aliveNode.get().getHost());
    }
  }

  private boolean isNodeAlive(Node node, RestClient client) {
    try {
      HttpHost host = node.getHost();
      log.info("Checking node: {}", host);
      Request request = new Request("GET", "/");
      Response response = client.performRequest(request); // 로컬 client 사용
      return response.getStatusLine().getStatusCode() == 200;
    } catch (Exception e) {
      log.warn("Node {} is not responding: {}", node.getHost(), e.getMessage());
      return false;
    }
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
