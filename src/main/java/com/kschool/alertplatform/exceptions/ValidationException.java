package com.kschool.alertplatform.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ValidationException extends RuntimeException {
  public static final String UUID_NOT_EXIST_ERROR = "Uuid received does not exist: ";

  public ValidationException(String exception) {
    super(exception);
  }
}
