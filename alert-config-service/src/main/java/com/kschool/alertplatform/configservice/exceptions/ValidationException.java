package com.kschool.alertplatform.configservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ValidationException extends RuntimeException {
  public static final String ID_NOT_EXIST_ERROR = "AlertConfig Id received does not exist: ";

  public ValidationException(String exception) {
    super(exception);
  }
}
