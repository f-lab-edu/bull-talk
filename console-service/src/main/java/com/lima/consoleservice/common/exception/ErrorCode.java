package com.lima.consoleservice.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode implements CodeInterface {
  SUCCESS("BT-0000", "SUCCESS"),

  USER_ALREADY_EXISTS("BT-0001", "USER_ALREADY_EXISTS"),
  USER_SAVED_FAILED("BT-0002", "USER_SAVED_FAILED"),
  NOT_EXIST_USER("BT-0003", "NOT_EXIST_USER"),
  MIS_MATCH_PASSWORD("BT-0004", "MIS_MATCH_PASSWORD"),
  NOT_MATCH_USER("BT-0005", "NOT_MATCH_USER")
  ;

  private String code;
  private String message;
}
