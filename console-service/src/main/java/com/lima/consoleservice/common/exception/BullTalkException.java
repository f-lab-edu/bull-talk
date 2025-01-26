package com.lima.consoleservice.common.exception;

import lombok.Getter;

@Getter
public class BullTalkException extends RuntimeException {

  private final CodeInterface codeInterface;

  public BullTalkException(CodeInterface codeInterface) {
    super(codeInterface.getMessage());
    this.codeInterface = codeInterface;
  }

  public BullTalkException(CodeInterface codeInterface, String message) {
    super(message);
    this.codeInterface = codeInterface;
  }
}
