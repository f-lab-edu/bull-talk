package com.lima.consoleservice.domain.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lima.consoleservice.common.exception.ErrorCode;
import com.lima.consoleservice.config.security.Hasher;
import com.lima.consoleservice.config.security.JwtTokenProvider;
import com.lima.consoleservice.domain.repository.UserRepository;
import com.lima.consoleservice.domain.repository.entity.User;
import com.lima.consoleservice.domain.repository.entity.UserCredentials;
import com.lima.consoleservice.domain.user.model.request.CreateUserRequest;
import com.lima.consoleservice.domain.user.model.request.LoginUserRequest;
import com.lima.consoleservice.domain.user.model.request.UpdateUserRequest;
import com.lima.consoleservice.domain.user.model.response.AuthResponse;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/*
  @Mock
  목적: 특정 클래스의 Mock 객체(가짜 객체)를 생성한다.
  테스트 환경에서 테스트 대상 객체의 의존성을 대체하는 데 사용 된다.
  @Mock은 의존성 주입과 무관하며, 단순히 Mock 객체를 생성하기만 한다.

  @InjectMocks
  목적: 테스트 대상 객체를 생성하고, @Mock으로 생성된 Mock 객체들을 자동으로 주입한다.
  Mock 객체와 테스트 대상 객체를 연결해주는 역할을 한다.
  의존성 주입 방식은 생성자, 필드, 세터를 통해 이루어질 수 있다.

  @Spy
  실제 객체를 감싸고, 기본적으로 실제 객체의 동작을 사용하지만, 특정 메서드에 대해서만 stub(대체)할 수 있다.
  when(userService.getCurrentUserEmail()).thenReturn(email); 때문에 추가 하였다.
 */

@SpringBootTest
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Spy
  @InjectMocks
  private UserService userService;

  @Mock
  private JwtTokenProvider jwtTokenProvider;

  @Mock
  private RedisTemplate<String, String> redisTemplate;

  // Spring Data Redis 에서 제공하는 인터페이스 이고, Redis 의 String 타입 값에 대해 조작할 수 있는 기능을 제공한다.
  @Mock
  private ValueOperations<String, String> valueOperations;

  @BeforeEach
  void setUp() {
    // RedisTemplate의 opsForValue() 설정
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
  }

  @Test
  void createUserSuccess() {
    // Given
    String email = "lima@gmail.com";
    String name = "HelloTest";
    String password = "1016";

    CreateUserRequest request = new CreateUserRequest(email, name, password);

    // UserRepository에서 이메일이 존재하지 않는다고 가정.
    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    // UserRepository의 save 메서드가 호출될 때 가짜로 저장된 User 객체를 반환하도록 설정하자.
    when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

    // When
    AuthResponse response = userService.createUser(request);

    // Then
    assertEquals(ErrorCode.SUCCESS.getMessage(), response.code());
    assertEquals(email, response.result());

    // Verify: userRepository.findByEmail과 save 메서드가 호출되었는지 확인
    verify(userRepository).findByEmail(email);
    verify(userRepository).save(any(User.class));
  }

//  @Test
//  void createUserAlreadyExists() {
//    // Given
//    String email = "lima@gmail.com";
//    String name = "HelloTest";
//    String password = "1016";
//
//    CreateUserRequest request = new CreateUserRequest(email, name, password);
//
//    // 유저가 이미 있을때
//    when(userRepository.findByEmail(email)).thenReturn(Optional.of(User.builder().email(email).build()));
//
//    // UserRepository의 save 메서드가 호출될 때 가짜로 저장된 User 객체를 반환하도록 설정하자.
//    when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);
//
//    // When
//    AuthResponse response = userService.createUser(request);
//
//    // Then
//    // throw로 던져서 exception이 나긴 함.
//    assertEquals(ErrorCode.USER_ALREADY_EXISTS.getMessage(), response.code());
//
//    // Verify: userRepository.findByEmail과 save 메서드가 호출되었는지 확인
//    verify(userRepository).findByEmail(email);
//    verify(userRepository).save(any(User.class));
//  }

  @Test
  void loginSuccess() {
    // Given
    String email = "lima@gmail.com";
    String password = "1016";
    Hasher hasher = new Hasher("SHA-256");
    LoginUserRequest request = new LoginUserRequest(email, password);

    // UserRepository에서 이메일이 존재 한다고 가정.
    when(userRepository.findByEmail(email))
        .thenReturn(
            Optional.of(User.builder()
                .email(email)
                .userCredentials(
                    UserCredentials.builder().hashedPassword(hasher.getHashingValue(password))
                        .build())
                .build())
        ).thenAnswer(i -> i.getArguments()[0]);

    // Mock JwtTokenProvider
    when(jwtTokenProvider.createToken(email)).thenReturn("mock-token");

    // When
    AuthResponse response = userService.login(request);

    // Then
    assertEquals(ErrorCode.SUCCESS.getMessage(), response.code());
    assertEquals("mock-token", response.result());
  }

//  @Test
//  void loginNotExistUser() {
//    // Given
//    String email = "lima@gmail.com";
//    String password = "1016";
//    LoginUserRequest request = new LoginUserRequest(email, password);
//
//    // UserRepository에서 이메일이 존재 하지 않는다고 가정.
//    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
//
//    // When
//    AuthResponse response = userService.login(request);
//
//    // Then
//    assertEquals(ErrorCode.NOT_EXIST_USER.getMessage(), response.code());
//    assertEquals("mock-token", response.result());
//  }

  @Test
  void logoutSuccess() {
    // Given
    String name = "HelloTest";
    String email = "lima@gmail.com";
    long userId = 1L;

    // 찾는 유저가 있다고 가정
    when(userRepository.findById(userId))
        .thenReturn(Optional.of(User.builder().name(name).email(email).build()));
    when(userService.getCurrentUserEmail()).thenReturn(email);

    // When
    AuthResponse logout = userService.logout(userId);

    // Then
    assertEquals(ErrorCode.SUCCESS.getMessage(), logout.code());
  }

//  @Test
//  void logoutNotExistUser() {
//    // Given
//    String email = "lima@gmail.com";
//    long userId = 1L;
//
//    // 찾는 유저가 있다고 없다고 가정
//    when(userRepository.findById(userId)).thenReturn(Optional.empty());
//    when(userService.getCurrentUserEmail()).thenReturn(email);
//
//    // When
//    AuthResponse logout = userService.logout(userId);
//
//    // Then
//    assertEquals(ErrorCode.NOT_EXIST_USER.getMessage(), logout.code());
//  }

  @Test
  void updateUserSuccess() {
    // Given
    String name = "HelloTest";
    String email = "lima@gmail.com";
    long userId = 1L;

    UpdateUserRequest request = new UpdateUserRequest(name);

    // 찾는 유저가 있다고 가정
    when(userRepository.findById(userId))
        .thenReturn(Optional.of(User.builder().name(name).email(email).build()));
    when(userService.getCurrentUserEmail()).thenReturn(email);

    // When
    AuthResponse response = userService.updateUser(userId, request);

    // Then
    assertEquals(ErrorCode.SUCCESS.getMessage(), response.code());
    assertEquals(name, response.result());
  }

//  @Test
//  void updateUserNotExistUser() {
//    // Given
//    String name = "HelloTest";
//    String email = "lima@gmail.com";
//    long userId = 1L;
//
//    UpdateUserRequest request = new UpdateUserRequest(name);
//
//    when(userRepository.findById(userId)).thenReturn(Optional.empty());
//    when(userService.getCurrentUserEmail()).thenReturn(email);
//
//    // When
//    AuthResponse response = userService.updateUser(userId, request);
//
//    // Then
//    assertEquals(ErrorCode.NOT_EXIST_USER.getMessage(), response.code());
//    assertEquals(name, response.result());
//  }

  @Test
  void deleteUserSuccess() {
    // Given
    String name = "HelloTest";
    String email = "lima@gmail.com";
    long userId = 1L;

    // 찾는 유저가 있다고 가정
    when(userRepository.findById(userId))
        .thenReturn(Optional.of(User.builder().name(name).email(email).build()));
    when(userService.getCurrentUserEmail()).thenReturn(email);

    // When
    AuthResponse response = userService.deleteUser(userId);

    // Then
    assertEquals(ErrorCode.SUCCESS.getMessage(), response.code());
  }

//  @Test
//  void deleteUserNotExistUser() {
//    // Given
//    String email = "lima@gmail.com";
//    long userId = 1L;
//
//    when(userRepository.findById(userId)).thenReturn(Optional.empty());
//    when(userService.getCurrentUserEmail()).thenReturn(email);
//
//    // When
//    AuthResponse response = userService.deleteUser(userId);
//
//    // Then
//    assertEquals(ErrorCode.NOT_EXIST_USER.getMessage(), response.code());
//  }
}