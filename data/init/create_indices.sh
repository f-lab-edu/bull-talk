#!/bin/bash

echo "Waiting for Elasticsearch to be ready..."
# Elasticsearch가 준비될 때까지 주기적으로 확인
until curl -s "http://elasticsearch2:9200" > /dev/null; do
  echo "localhost Waiting for Elasticsearch to be available..."
  sleep 5
done

echo "Elasticsearch is ready!"

# JSON 파일이 있는 디렉토리 지정
INIT_DIR="/usr/share/elasticsearch/init"

# 모든 JSON 파일을 찾아서 인덱스를 생성
for file in "$INIT_DIR"/*.json; do
  # 파일명에서 경로와 확장자를 제거하여 인덱스명 추출
  index_name=$(basename "$file" .json)

  echo "Creating index: $index_name"
  curl -X PUT "http://elasticsearch2:9200/$index_name" -H "Content-Type: application/json" -d @"$file"

  echo "Index $index_name creation completed!"
done

