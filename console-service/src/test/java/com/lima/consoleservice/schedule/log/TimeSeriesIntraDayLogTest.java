package com.lima.consoleservice.schedule.log;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lima.consoleservice.common.connection.OkHttpClientConnection;
import com.lima.consoleservice.schedule.log.params.Symbol;
import java.io.IOException;
import okhttp3.HttpUrl.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quartz.JobExecutionContext;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
class TimeSeriesIntraDayLogTest {

  @Mock
  private OkHttpClientConnection connection;

  @InjectMocks
  private TimeSeriesIntraDayLog job;

  @Mock
  private JobExecutionContext context;

  @BeforeEach
  void setUp() {
    // 테스트 실행 전에 @Mock으로 선언된 객체들을 자동으로 생성하고 초기화하는 작업임
    MockitoAnnotations.openMocks(this);
  }

  // TimeSeriesIntraDayLog 에 connection 필드는 private final 이여서 생성자에서 직접 초기화 해야한다.
  // 테스트 코드에서 new TimeSeriesIntraDayLog(); 를 호출하면 내부적으로 진짜 객체를 가져와버린다.
  // 강제로 필드 값을 바꾸기 위해서 ReflectionTestUtils.setField()를 사용한다.
  // 원래는 setter 메서드를 사용해야 한다. private final로 되어있어서 리플랙션 사용.

  @Test
  void executeConnectAPI() {
    // Given
    // mock() 로 가짜 객체를 만들어서 메서드 호출을 검증할 수 있게 할 수 있다.
    JobExecutionContext context = mock(JobExecutionContext.class);

    ReflectionTestUtils.setField(job, "connection", connection);
    Builder mockBuilder = mock(Builder.class);

    // When
    when(connection.buildParameters()).thenReturn(mockBuilder);
    when(mockBuilder.addQueryParameter(anyString(), anyString())).thenReturn(mockBuilder);

    job.execute(context);

    // Then
    // buildParameters()가 Symbol 개수만큼 호출되었는지 검증
    verify(connection, times(Symbol.values().length)).buildParameters();
  }

  // doAnswer()는 메소드의 실행을 커스터마이징 하기위해 사용하는 메소드 이다.
  // void 반환 타입을 가진 메소드에서 사용된다.
  // spy()는 부분적으로 모의 객체를 만들 때 사용하는 메소드이다.
  // mock()는 전체 객체를 가짜로 만들지만, spy()는 실제 객체를 감싸서 일부 메소드만 가짜로 만들 수 있다.
  @Test
  void executeWithIOException() {
    // Given
    ReflectionTestUtils.setField(job, "connection", connection);
    Builder mockBuilder = mock(Builder.class);

    when(connection.buildParameters()).thenReturn(mockBuilder);
    when(mockBuilder.addQueryParameter(anyString(), anyString())).thenReturn(mockBuilder);

    // spyJob은 TimeSeriesIntraDayLog의 스파이 객체로, connectAlphaVantage() 메소드가 실제로 호출되었는지, 몇 번 호출되었는지를 검증한다.
    TimeSeriesIntraDayLog spyJob = spy(job);

    // OkHttpClientConnection의 connectAlphaVantage() 메소드가 IOException을 던지도록 설정
    doAnswer(invocation -> {
      throw new IOException("Test Exception");
    }).when(connection).connectAlphaVantage(any(), any(), anyString());

    // When
    spyJob.execute(context);

    // Then
    verify(connection, times(Symbol.values().length)).buildParameters();
    // any(), any()는 connectAlphaVantage() 메소드에 전달된 인자들에 대해 특정 값이 아니라 인자 값이 무엇이든 상관없다는 의미로 사용된다.
    verify(connection, times(Symbol.values().length)).connectAlphaVantage(any(), any(), anyString());
  }
}