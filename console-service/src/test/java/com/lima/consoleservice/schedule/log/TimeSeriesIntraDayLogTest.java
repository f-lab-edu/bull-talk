package com.lima.consoleservice.schedule.log;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lima.consoleservice.config.OkHttpClientConnection;
import com.lima.consoleservice.schedule.log.params.Symbol;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quartz.JobExecutionContext;
import org.springframework.boot.test.context.SpringBootTest;
import okhttp3.HttpUrl.Builder;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
class TimeSeriesIntraDayLogTest {

  @Mock
  private OkHttpClientConnection connection;

  @InjectMocks
  private TimeSeriesIntraDayLog job;

  @BeforeEach
  void setUp() {
    // 테스트 실행 전에 @Mock으로 선언된 객체들을 자동으로 생성하고 초기화하는 작업임
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void executeConnectAPI() {
    // Given
    // mock() 로 가짜 객체를 만들어서 메서드 호출을 검증할 수 있게 할 수 있다.
    JobExecutionContext context = mock(JobExecutionContext.class);

    // TimeSeriesIntraDayLog 에 connection 필드는 private final 이여서 생성자에서 직접 초기화 해야한다.
    // 테스트 코드에서 new TimeSeriesIntraDayLog(); 를 호출하면 내부적으로 진짜 객체를 가져와버린다.
    // 강제로 필드 값을 바꾸기 위해서 ReflectionTestUtils.setField()를 사용한다.
    // 원래는 setter 메서드를 사용해야 한다. private final로 되어있어서 리플랙션 사용.
    ReflectionTestUtils.setField(job, "connection", connection);

    Builder mockBuilder = mock(Builder.class);

    // When
    // 여기서 buildParameters()의 반환값을 설정
    when(connection.buildParameters()).thenReturn(mockBuilder);
    // addQueryParameter도 mock 처리
    when(mockBuilder.addQueryParameter(anyString(), anyString())).thenReturn(mockBuilder);

    job.execute(context);

    // Then
    // buildParameters()가 Symbol 개수만큼 호출되었는지 검증
    verify(connection, times(Symbol.values().length)).buildParameters();
  }
}