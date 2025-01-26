# bull-talk

주식장에서 실시간 채팅을 열어 주식에 대해 이야기 나누는 플랫폼. 

## 서버 구조
<img width="1130" alt="image" src="https://github.com/user-attachments/assets/7fd6f535-0d45-4344-9187-38a63ed24eca" />


## 프로젝트 목표
BullTalk 프로젝트는 주식 데이터를 기반으로 한 실시간 채팅 및 데이터 예측 플랫폼을 개발하는 것을 목표로 합니다.

1. 안정적이고 확장 가능한 백엔드 서비스 구축
    - Spring Boot 기반 REST API와 WebSocket을 활용한 실시간 채팅 기능 구현.
    - Redis, PostgreSQL, 그리고 ElasticSearch를 사용한 효율적인 데이터 저장 및 검색.
  
2. 대용량 트래픽 처리 및 시스템 안정성 확보
    - Nginx를 활용한 로드 밸런싱과 CORS 처리.
    - Redis 캐싱을 통해 DB 부하를 최소화하고 빠른 응답 제공.
    - Fluent Bit와 ElasticSearch로 실시간 로그 수집 및 모니터링 체계 구축.

3. 사용자 경험 향상
    - JWT 인증을 통한 간편하고 안전한 사용자 관리.
    - 채팅 메시지와 이미지 파일 처리 및 알림 기능 지원.

4. 자동화된 배포 환경
    - Docker와 Jenkins를 활용하여 CI/CD 파이프라인 구축.

5. 데이터 기반 가치 제공
    - 주식 데이터 분석 및 TA4J를 활용한 데이터 예측 기능.

## Troubleshooting
